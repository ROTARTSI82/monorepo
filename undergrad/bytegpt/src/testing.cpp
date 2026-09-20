#include "model.cpp"
#include <fcntl.h>
#include <unistd.h>
#include <iostream>
#include <sys/mman.h>

int main() {
  std::cout << "character model is " << sizeof(CharModel) << '\n';

  int modelfd = open("rawmodel.bin", O_RDONLY);
  if (modelfd == -1) {
    std::cout << "failed to open rawmodel.bin: " << strerror(errno) << '\n';
    return 1;
  }

  CharModel *model = reinterpret_cast<CharModel *>(
    mmap(nullptr, sizeof(CharModel), PROT_READ, MAP_PRIVATE, modelfd, 0));

  if (model == nullptr) {
    std::cout << "failed to mmap model: " << strerror(errno) << '\n';
    close(modelfd);
    return 1;
  }
 
  close(modelfd);

  InferenceState state{0, 0.8};

  std::string prompt = R"(
rotartsi 2026-04-03 13:44:55
and so you get this proof of cantor's theorem
rohan what do you think?

chopinfan239 2026-04-03 13:45:06
you diagonalize? like
```haskell
/-- Constructive Cantor's theorem: no function from a type to its powerset is surjective. -/
theorem cantor {α : Type u} (f : α → Set α) : ¬ Function.Surjective f := by
    sorry
```
did i get the statement right?

rotartsi 2026-04-03 13:45:12
oh maybe you should do
```)";
  std::cout << prompt;

  num_t out[256];
  for (char c : prompt) {
    model->forward(state, static_cast<unsigned char>(c), out);
    state.kv_cursor++;
  }

  tok_t nextin = sample_tok(out, state);
  for (int i = prompt.size(); i < 4096; i++) {
    std::cout << (char) nextin << std::flush;
    model->forward(state, nextin, out);
    nextin = sample_tok(out, state);
    state.kv_cursor++;
  }

  munmap(model, sizeof(CharModel));
}
