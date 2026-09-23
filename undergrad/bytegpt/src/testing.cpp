#include "model.cpp"

#include <fcntl.h>
#include <unistd.h>

#include <iostream>

#include <sys/mman.h>
#include <sys/stat.h>

// size:
//    nullptr - no effect
//    &0 - writes out the size of the mmap
//    &x - asserts that file size matches.
void *memmap_file(const char *fname, size_t *size) {
  int fd = open(fname, O_RDONLY);
  if (fd == -1) {
    std::cout << "failed to open " << fname << ": " << strerror(errno) << '\n';
    return nullptr;
  }

  struct stat fileinfo;
  if (fstat(fd, &fileinfo) == -1 || size && *size != 0 && *size != fileinfo.st_size)
    std::cout << strerror(errno) << "\n\twarning: expected size of " << fname << " is wrong (got "
      << fileinfo.st_size << " but expected " << *size << ")\n";

  if (size)
    *size = fileinfo.st_size;
  size_t size_used = size ? *size : fileinfo.st_size;

  void *ret = mmap(nullptr, size_used, PROT_READ, MAP_PRIVATE, fd, 0);
  close(fd);
  return ret;
}

void calculate_traindata_stats();

int main() {
  std::cout << "character model is " << sizeof(CharModel) << '\n';

  calculate_traindata_stats();

  size_t map_size = sizeof(CharModel);
  CharModel *model = reinterpret_cast<CharModel *>(memmap_file("model_linux.bin", &map_size));
  if (model == nullptr) {
    std::cout << "failed to mmap model: " << strerror(errno) << '\n';
    return 1;
  }
 
  InferenceState state{0, 0.8};

  const std::string prompt = R"(
float Q_rsqrt(float number)
{
	long i;
	float x2, y;
	const float threehalfs = 1.5F;)";
  std::cout << prompt;

  num_t out[256];
  model->forward(state, 0, out);
  state.kv_cursor++;
  for (char c : prompt) {
    model->forward(state, static_cast<unsigned char>(c), out);
    state.kv_cursor++;
  }

  tok_t nextin = sample_tok(out, state);
  for (int i = prompt.size(); i < 1024; i++) {
    std::cout << (char) nextin << std::flush;
    model->forward(state, nextin, out);
    nextin = sample_tok(out, state);
    state.kv_cursor++;
  }

  munmap(model, map_size);
}

// idc that this just allocates lol.
inline std::string escape(char c) {
  switch (c) {
  case '\t': return "\\t";
  case '\n': return "\\n";
  case '\r': return "\\r";
  case '\0': return "\\0";
  default: return std::string{c};
  }
}

void calculate_traindata_stats() {
  long counts[256];
  memset(counts, 0, 256 * sizeof(long));

  size_t map_size = 0;
  uint8_t *train = reinterpret_cast<uint8_t *>(memmap_file("train.txt", &map_size));
  if (!train)
    return;

  for (size_t i = 0; i < map_size; i++) {
    counts[train[i]]++;
  }

  for (int i = 0; i < 256; i++)
    std::cout << "byte " << i << ": " << counts[i] << '\t';
  std::cout << '\n';
  
  munmap(train, map_size);
}
