#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <string.h>

#ifdef __EMSCRIPTEN__
#include <emscripten.h>
#else
#define EMSCRIPTEN_KEEPALIVE
#endif

#include "model.cpp"

CharModel *g_model = nullptr;
InferenceState *g_state = nullptr;
int g_kv_populated = 0;
num_t g_logits[256];

extern "C" {

EMSCRIPTEN_KEEPALIVE
void init_engine(float *weights_ptr) {
    printf("[WASM] Initializing engine with weights at %p\n", weights_ptr);
    g_model = reinterpret_cast<CharModel*>(weights_ptr);
    
    if (g_state) {
        delete g_state;
    }
    g_state = new InferenceState(40, 0.8f);
    printf("KVCache ptr: %p\n", g_state->kv_cache);
    
    printf("[WASM] Engine initialized successfully.\n");
}

EMSCRIPTEN_KEEPALIVE
void set_inference_params(int top_k, num_t temp) {
    g_state->top_k = top_k;
    g_state->temp = temp;
}

EMSCRIPTEN_KEEPALIVE
int seek(int pos) {
   if (pos > g_kv_populated)
       return -1;
   g_state->kv_cursor = pos;
   return 0; 
}

EMSCRIPTEN_KEEPALIVE
int predict_next(int input_token) {
    if (!g_model || !g_state) {
        return -1;
    }
    
    g_model->forward(*g_state, static_cast<unsigned char>(input_token), g_logits);
    tok_t nextin = sample_tok(g_logits, *g_state);
    g_state->kv_cursor++;
    g_kv_populated = std::max(g_kv_populated, g_state->kv_cursor);
    
    return nextin;
}

EMSCRIPTEN_KEEPALIVE
num_t* get_logits() {
    return g_logits;
}

EMSCRIPTEN_KEEPALIVE
InterpStats* get_stats() {
    return g_state ? g_state->stats : nullptr;
}

EMSCRIPTEN_KEEPALIVE
void reset_engine() {
    if (g_state) {
        g_state->kv_cursor = 0;
        g_kv_populated = 0;
    }
}

} // extern "C"
