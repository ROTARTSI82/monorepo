#include <iostream>
#include <chrono>
#include <queue>
#include <cstdint>

template <typename T>
inline void swap(T *arr, int a, int b) {
    T tmp = arr[a];
    arr[a] = arr[b];
    arr[b] = tmp;
}

template <typename T>
void print_arr(T *arr, int size) {
    for (int i = 0; i < size; i++)
        std::cout << arr[i] << ", ";
    std::cout << "\n";
}

template <typename T>
void insertion_sort(T *arr, int size) {
    for (int i = 1; i < size; i++) {
        int j = i - 1;
        T value = arr[i];

        for (; j >= 0 && arr[j] > value; j--)
            arr[j + 1] = arr[j];

        arr[j + 1] = value;
    }
}

template <typename T>
void selection_sort(T *arr, int size) {
    for (int i = 0; i < size - 1; i++) {
        int min = i + 1;
        for (int j = i + 1; j < size; j++)
            if (arr[j] < arr[min]) min = j;
        swap(arr, min, i);
    }
}

template <typename T>
void merge_sort(T *arr, int size) {
    if (size <= 1)
        return;
    T *mergeA = arr;
    T *mergeB = arr + size / 2;

    merge_sort(mergeA, size / 2);
    merge_sort(mergeB, size - size / 2);

    T aux[size];
    T *head = aux;

    T *aEnd = arr + size / 2;
    T *bEnd = arr + size;
    while (mergeA < aEnd && mergeB < bEnd)
        *head++ = *mergeA > *mergeB ? *mergeB++ : *mergeA++;

    while (mergeA < aEnd) *head++ = *mergeA++;
    while (mergeB < bEnd) *head++ = *mergeB++;

    std::copy(aux, aux + size, arr);
}

template <typename T>
void bubble_sort(T *arr, int size) {
    int numSwaps;
    do {
        numSwaps = 0;
        for (int i = 0; i < size - 1; i++) {
            if (arr[i] > arr[i + 1]) {
                numSwaps++;
                swap(arr, i, i + 1);
            }
        }

        size--;
    } while (numSwaps > 0);
}

template <typename T>
inline void sift_down(T *arr, int index, int size) {
    for (;;) {
        int rc = index * 2 + 2;
        int lc = rc - 1;

        int swapInd;

        if (rc < size && arr[rc] > arr[index]) {
            swapInd = arr[lc] > arr[rc] ? lc : rc;
        } else if (lc < size && arr[lc] > arr[index]) {
            swapInd = lc;
        } else {
            break;
        }

        swap(arr, index, swapInd);
        index = swapInd;
    }
}

template <typename T>
void heap_sort(T *arr, int size) {
    // heapify
    for (int i = (size / 2) - 1; i >= 0; i--)
        sift_down(arr, i, size);

    for (int i = size - 1; i >= 0; i--) {
        swap(arr, i, 0);
        sift_down(arr, 0, --size);
    }
}

inline uint64_t xorshift64(uint64_t &x) {
    x ^= x << 13;
    x ^= x >> 7;
    x ^= x << 17;
    return x;
}


template <typename T>
void quick_sort(T *arr, int size) {
    // it's probably a good idea to switch into
    // another sorting algo for small sizes
    if (size <= 1)
        return;

    uint64_t seed = 0xb544e85dd4f70573 ^ size;
    int pivot = xorshift64(seed) % size;

    T aux[size];
    T *head = aux;
    T *tail = aux + size - 1;

    for (int i = 0; i < pivot; i++)
        (arr[i] > arr[pivot] ? *tail-- : *head++) = arr[i];
    for (int i = pivot + 1; i < size; i++)
        (arr[i] > arr[pivot] ? *tail-- : *head++) = arr[i];

    *head = arr[pivot];
    quick_sort(aux, head - aux);
    quick_sort(head + 1, aux + size - head - 1);

    std::copy(aux, aux + size, arr);
}

template <typename T>
class BSTNode {
public:
    T val;
    BSTNode *lc, *rc;

    BSTNode(T val, BSTNode *l, BSTNode *r) : val(val), lc(l), rc(r) {}

    inline static BSTNode *insert(BSTNode *root, T val) {
        if (!root) return new BSTNode(val, nullptr, nullptr);
        if (val > root->val) root->rc = insert(root->rc, val);
        else root->lc = insert(root->lc, val);
        return root;
    }

    inline static void inorder_and_delete(BSTNode *node, T *&store) {
        if (!node) return;
        inorder_and_delete(node->lc, store);
        *store++ = node->val;
        inorder_and_delete(node->rc, store);
        delete node;
    }
};

template <typename T>
void tree_sort(T *arr, int size) {
    BSTNode<T> *root = nullptr;

    for (int i = 0; i < size; i++)
        root = BSTNode<T>::insert(root, arr[i]);
    BSTNode<T>::inorder_and_delete(root, arr);
}

template <typename T, uint64_t size>
struct RadixQueue {
    T data[size];
    uint64_t ind = 0;

    inline void emplace(T value) {
        data[ind++] = value;
    }
};


constexpr auto QUEUE_SIZE = 4096;

// doesn't handle negatives
template <typename T, uint64_t base>
void radix_sort(T *arr, int size) {
    uint64_t divisor = 1;

    RadixQueue<T, QUEUE_SIZE> buckets[base];

    do {

        T *head = arr;
        for (int i = 0; i < base; i++) {
            std::copy(buckets[i].data, buckets[i].data + buckets[i].ind, head);
            head += buckets[i].ind;
            buckets[i].ind = 0;
        }

        for (int i = 0; i < size; i++) {
            buckets[(arr[i] / divisor) % base].emplace(arr[i]);
        }

        divisor *= base; // divisor *= base
    } while (buckets[0].ind < size);
}

template <typename T>
void std_sort(T *arr, int size) {
    std::sort(arr, arr + size);
}


enum class GenerationMethod {
    Random, Sorted, Reversed
};


struct Timing {
    std::string funcName;
    GenerationMethod mode;
    uint64_t time;
};

template <uint64_t Trials, uint64_t ListSize, GenerationMethod Mode>
inline Timing bench(std::pair<std::function<void(uint64_t *, int)>, std::string> function) {
    uint64_t master[ListSize];

    uint64_t seed = 0x9266d7daac53201d ^ Trials << 37 ^ ListSize << 17 ^ static_cast<uint64_t>(Mode) << 43;

    uint64_t min = xorshift64(seed) % 4096;
    uint64_t max = std::numeric_limits<uint64_t>::max() - (xorshift64(seed) % 4096);

    for (int i = 0; i < ListSize; i++)
        switch (Mode) {
        case GenerationMethod::Random:
            master[i] = xorshift64(seed);
            break;
        case GenerationMethod::Sorted:
            master[i] = (min += xorshift64(seed) % 64);
            break;
        case GenerationMethod::Reversed:
            master[i] = (max -= xorshift64(seed) % 64);
            break;
        }

    auto start = std::chrono::high_resolution_clock::now();
    for (int trial = 0; trial < Trials; trial++) {
        uint64_t toSort[ListSize];
        std::copy(master, master + ListSize, toSort);
        function.first(toSort, ListSize);
    }

    auto duration = std::chrono::high_resolution_clock::now() - start;
//    std::cout << function.second << " took " << std::chrono::duration_cast<std::chrono::milliseconds>(duration).count() / 1000.0;
//    std::cout << "s\n";

    return {function.second, Mode, static_cast<uint64_t>(std::chrono::duration_cast<std::chrono::nanoseconds>(duration).count())};
}

std::string to_string(GenerationMethod mode) {
    switch (mode) {
        case GenerationMethod::Reversed: return "Reversed";
        case GenerationMethod::Random: return "Random";
        default: return "Sorted";
    }
}


#define S(func) std::make_pair<std::function<void(uint64_t *, int)>, std::string>(func, #func)

int main() {
    int arr[] = {45, 23, 35, 2, 99, 44, 45, 46};
    radix_sort<int, 16>(arr, 8);

    auto radix_sort64_uint64_t = radix_sort<uint64_t, 64>;

    std::vector<Timing> timings;
    for (auto sort : {S(std_sort<uint64_t>), S(radix_sort64_uint64_t), S(bubble_sort<uint64_t>),
                      S(insertion_sort<uint64_t>), S(merge_sort<uint64_t>),
                      S(quick_sort<uint64_t>), S(heap_sort<uint64_t>),
                      S(selection_sort<uint64_t>), S(tree_sort<uint64_t>),}) {

        constexpr auto trials = 2048;
        constexpr auto list = 1024;

        std::cout << "Random: " << sort.second << "\n";
        timings.emplace_back(bench<trials, list, GenerationMethod::Random>(sort));

        std::cout << "Sorted: " << sort.second << "\n";
        timings.emplace_back(bench<trials, list, GenerationMethod::Sorted>(sort));

        std::cout << "Reversed: " << sort.second << "\n";
        timings.emplace_back(bench<trials, list, GenerationMethod::Reversed>(sort));

        std::cout << "\n";
    }

    std::sort(timings.begin(), timings.end(), [](const Timing &a, const Timing &b) -> bool {
        return a.time > b.time;
    });

    for (auto &t : timings) {
        std::cout << to_string(t.mode) << '\t' << t.funcName << '\t' << t.time / 1e9 << "s\n";
    }


    print_arr(arr, 8);
}
