#include <algorithm>
#include <cmath>
#include <cstring>
#include <random>

using num_t = float;
using tok_t = int;

constexpr int NUM_LAYERS = 24;
constexpr int NUM_ATTN_SINKS = 4;

// 200 MB
struct KVCache {
  num_t keys[4096][24][256];
  num_t values[4096][24][256];
};

// 6 MB
struct InterpStats {
  num_t attn_scores[24][8][4096];
  num_t value_norms[24][8][4096];
  num_t layer_activ[24][256];
};

struct InferenceState {
  KVCache *kv_cache;
  int kv_cursor = 0;

  int top_k = 40;
  num_t temp = 0.8;

  std::mt19937 eng;

  // misc statistics tracked
  InterpStats *stats;

  InferenceState(int top_k, num_t temp) : top_k(top_k), temp(temp) {
    std::random_device r{};

    std::seed_seq seq{r(), r(), r(), r(), r(), r(), r(), r()};
    eng = std::mt19937{seq};

    kv_cache = new KVCache{};
    stats = new InterpStats{};
  }

  ~InferenceState() {
    delete kv_cache;
    delete stats;
  }
};

// linear transformation : R^M -> R^N
template <int N, int M>
struct Linear {
  num_t weights[N][M];

  void forward(num_t *in, num_t *out) const;
};

struct AttentionBlock {
  num_t attn_norm[256];
  Linear<256, 256> attn_wq;
  Linear<256, 256> attn_wk;
  Linear<256, 256> attn_wv;
  Linear<256, 256> attn_wo;
  
  num_t mlp_norm[256];

  Linear<768, 256> mlp_w1;
  Linear<256, 768> mlp_w2;
  Linear<768, 256> mlp_w3;

  void forward(num_t *base, InferenceState &state, int idx) const;
};


struct CharModel {
  Linear<256, 256> embed;
  AttentionBlock layers[24];
  num_t norm[256];

  void forward(InferenceState &config, tok_t nextin, num_t *outvec);
};


inline void apply_rope(num_t *vec, int step) {
  constexpr int d_model = 256;
  constexpr int n_heads = 8;
  constexpr int head_dim = d_model / n_heads;

  for (int h = 0; h < n_heads; h++) {
    for (int i = 0; i < head_dim; i += 2) {
      num_t theta = step * std::pow(10000.0f, -(float)i / head_dim);

      int idx = h * head_dim + i;
      num_t x = vec[idx] * std::cos(theta) - vec[idx + 1] * std::sin(theta);
      num_t y = vec[idx] * std::sin(theta) + vec[idx + 1] * std::cos(theta);

      vec[idx] = x;
      vec[idx + 1] = y;
    }
  }
}

template <int N>
inline void apply_rmsnorm(num_t *vec, const num_t *gain) {
  num_t norm = 0;
  for (int i = 0; i < N; i++)
    norm += vec[i] * vec[i];
  norm = 1.0f / std::sqrt(norm / N + 1e-6f);

  for (int i = 0; i < N; i++)
    vec[i] *= gain[i] * norm;
}

inline void softmax_ip(num_t *vec, int N) {
  num_t max_val = vec[0];
  for (int i = 1; i < N; i++) {
    if (vec[i] > max_val)
      max_val = vec[i];
  }
  
  num_t sum = 0;
  for (int i = 0; i < N; i++) {
    vec[i] = std::exp(vec[i] - max_val);
    sum += vec[i];
  }

  for (int i = 0; i < N; i++)
    vec[i] /= sum;
}

template <int N, int M>
inline void Linear<N, M>::forward(num_t *in, num_t *out) const {
  for (int i = 0; i < N; i++) {
    out[i] = 0;
    for (int j = 0; j < M; j++)
      out[i] += weights[i][j] * in[j];
  }
}

template <int N>
inline void silu(num_t *vec) {
  for (int i = 0; i < N; i++)
    vec[i] /= (1 + std::exp(-vec[i]));
}

inline void AttentionBlock::forward(num_t *base, InferenceState &conf, int idx) const {
  // attention
  num_t attn[256];
  memcpy(attn, base, 256 * sizeof(num_t));
  apply_rmsnorm<256>(attn, attn_norm);
  
  num_t q[256];
  attn_wq.forward(attn, q);
  num_t k[256];
  attn_wk.forward(attn, k);
  num_t v[256];
  attn_wv.forward(attn, v);

  apply_rope(q, conf.kv_cursor);
  apply_rope(k, conf.kv_cursor);

  memcpy(conf.kv_cache->keys[conf.kv_cursor][idx], k, 256 * sizeof(num_t));
  memcpy(conf.kv_cache->values[conf.kv_cursor][idx], v, 256 * sizeof(num_t));

  constexpr int head_dim = 256 / 8;
  const num_t scale = 1.0f / std::sqrt(head_dim);

  // 8 attention heads * 32 head_dim = 256.
  num_t attn_valout[256];
  memset(attn_valout, 0, 256 * sizeof(num_t));
  for (int i = 0; i < 8; i++) {
    // can stack allocate exactly conf.kv_cursor? but idk
    num_t dots[4096];

    for (int j = 0; j <= conf.kv_cursor; j++) {
      dots[j] = 0;
      for (int l = 0; l < head_dim; l++) {
        dots[j] +=
          conf.kv_cache->keys[j][idx][i*head_dim + l] * q[i*head_dim + l];
      }

      dots[j] *= scale;
    }

    memcpy(conf.stats->attn_scores[idx][i], dots, (conf.kv_cursor + 1) * sizeof(num_t));
    softmax_ip(dots, conf.kv_cursor + 1);

    for (int j = 0; j <= conf.kv_cursor; j++) {
      conf.stats->value_norms[idx][i][j] = 0;
      for (int l = 0; l < head_dim; l++) {
        num_t val_x = conf.kv_cache->values[j][idx][i * head_dim+l]; 
        conf.stats->value_norms[idx][i][j] += val_x * val_x;
        attn_valout[i*head_dim + l] += dots[j] * val_x;
      }
      conf.stats->value_norms[idx][i][j] = std::sqrt(conf.stats->value_norms[idx][i][j]);
    }
  }

  num_t attn_out[256];
  attn_wo.forward(attn_valout, attn_out);
  for (int i = 0; i < 256; i++)
    base[i] += attn_out[i];

  // mlp layer
  num_t mlp[256];
  memcpy(mlp, base, 256 * sizeof(num_t));
  apply_rmsnorm<256>(mlp, mlp_norm);

  num_t gate[768];
  num_t activ[768];
  mlp_w3.forward(mlp, gate);
  mlp_w1.forward(mlp, activ);

  silu<768>(activ);
  for (int i = 0; i < 768; i++)
    activ[i] *= gate[i];

  num_t mlp_out[256];
  mlp_w2.forward(activ, mlp_out);
  for (int i = 0; i < 256; i++)
    base[i] += mlp_out[i];

  memcpy(conf.stats->layer_activ[idx], base, 256 * sizeof(num_t));
}

inline void run_top_k(num_t *vec, int top_k, num_t setval) {
  num_t sorted[256];
  memcpy(sorted, vec, 256 * sizeof(num_t));
  std::sort(sorted, sorted + 256);

  num_t cutoff = sorted[256 - top_k];
  for (int i = 0; i < 256; i++)
    if (vec[i] < cutoff)
      vec[i] = setval;
}

inline tok_t multinomial(num_t *vec, InferenceState &rng) {
  double to_double[256];
  for (int i = 0; i < 256; i ++)
    to_double[i] = static_cast<double>(vec[i]);

  std::discrete_distribution<> discrete{to_double, to_double + 256};
  return static_cast<tok_t>(discrete(rng.eng));
}

void CharModel::forward(InferenceState &config, tok_t nextin, num_t *outvec) {
  num_t base[256];
  memcpy(base, embed.weights[nextin], 256 * sizeof(num_t));

  int idx = 0;
  for (const auto &layer : layers) {
    layer.forward(base, config, idx++);
  }

  apply_rmsnorm<256>(base, norm);
  embed.forward(base, outvec);
}

inline tok_t sample_tok(num_t *inp, InferenceState &config) {
  num_t vec[256];
  memcpy(vec, inp, 256 * sizeof(num_t));

  for (int i = 0; i < 256; i++)
    vec[i] /= config.temp;

  // std::numeric_limits<num_t>::infinity() is undefined behavior with fast math.
  constexpr num_t neg_infty = -1e9; 
  if (config.top_k > 0)
    run_top_k(vec, config.top_k, neg_infty);

  softmax_ip(vec, 256);

  return multinomial(vec, config);
}


