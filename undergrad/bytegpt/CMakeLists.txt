cmake_minimum_required(VERSION 3.20)
project(infer LANGUAGES CXX)
set(CMAKE_CXX_STANDARD 23)


# ==============================================================================
# Emscripten specific flags for WebGPU, Pthreads, and SIMD
# ==============================================================================
if (EMSCRIPTEN)
    add_executable(infer src/wasm.cpp)
    target_include_directories(infer PUBLIC src include)

    # Enable WebGPU (using the newer Dawn port)
    target_compile_options(infer PRIVATE "--use-port=emdawnwebgpu")
    target_link_options(infer PRIVATE "--use-port=emdawnwebgpu")   
       
    # Enable Wasm SIMD (128-bit)
    target_compile_options(infer PRIVATE "-msimd128" "-O3" -flto -ffast-math)
    target_link_options(infer PRIVATE "-O3" -flto -ffast-math)
    
    set_target_properties(infer PROPERTIES SUFFIX ".js")
    
    # Allow WebAssembly memory to grow as needed
    target_link_options(infer PRIVATE "-sALLOW_MEMORY_GROWTH=1")
    
    # Keep runtime alive to allow WebGPU async callbacks to resolve
    target_link_options(infer PRIVATE "-sNO_EXIT_RUNTIME=1")
    
    # Export functions to JavaScript
    target_link_options(infer PRIVATE "-sEXPORTED_FUNCTIONS=['_malloc','_free','_init_engine','_predict_next','_reset_engine','_seek','_get_logits','_get_stats', '_set_inference_params']")
    target_link_options(infer PRIVATE "-sEXPORTED_RUNTIME_METHODS=['ccall','cwrap','HEAPU8','HEAPF32']")
else()
    add_executable(infer src/testing.cpp)
    target_include_directories(infer PUBLIC src include)

    target_compile_options(infer PUBLIC -O3 -ffast-math -march=native -mtune=native -flto)
    target_link_options(infer PUBLIC -O3 -ffast-math -march=native -mtune=native -flto -s)
endif()
