//
// Created by 25granty on 10/28/19.
//

#include "mason/cl/cl_device.h"
#include <string>
#include <iostream>
#include <algorithm>
#include <chrono>

std::chrono::high_resolution_clock::time_point start_time;
std::chrono::high_resolution_clock::time_point stop_time;

#define STOP_CLOCK stop_time = std::chrono::high_resolution_clock::now();
#define START_CLOCK start_time = std::chrono::high_resolution_clock::now();
#define DELTA std::chrono::duration_cast<std::chrono::nanoseconds>(stop_time - start_time).count()

#define BATCH_SIZE 99999999


int main() {
    {
        START_CLOCK
        auto gpu = mason::cl::cl_device(CL_DEVICE_TYPE_GPU);

        std::string code =
                "   void kernel simple_add(global const int* A, global const int* B, global int* C){       "
                "       C[get_global_id(0)]=A[get_global_id(0)]+B[get_global_id(0)];                 "
                "   }                                                                               ";

        cl::Program::Sources srcs;
        srcs.emplace_back(code);

        cl::Program prog = cl::Program(gpu.context, srcs);
        if (prog.build(gpu.devices) != CL_SUCCESS) {
            std::cout << " Error building: " << prog.getBuildInfo<CL_PROGRAM_BUILD_LOG>(gpu.devices[0]) << "\n";
            exit(1);
        }

        std::vector<int> vec_in1(BATCH_SIZE);
        std::fill(vec_in1.begin(), vec_in1.end(), 5);
        std::vector<int> vec_in2(BATCH_SIZE);
        std::fill(vec_in2.begin(), vec_in2.end(), 3);

        cl::Buffer in1(gpu.queue, vec_in1.begin(), vec_in1.end(), true);
        cl::Buffer in2(gpu.queue, vec_in2.begin(), vec_in2.end(), true);

        cl::Buffer out(gpu.context, CL_MEM_READ_WRITE, sizeof(int) * BATCH_SIZE);

        cl::Kernel add_func = cl::Kernel(prog, "simple_add");
        add_func.setArg(0, in1);
        add_func.setArg(1, in2);
        add_func.setArg(2, out);

        gpu.queue.enqueueNDRangeKernel(add_func, cl::NullRange, cl::NDRange(BATCH_SIZE), cl::NullRange);
        gpu.queue.finish();

        std::vector<int> vec_out(BATCH_SIZE);
        gpu.queue.enqueueReadBuffer(out, CL_TRUE, 0, sizeof(int) * BATCH_SIZE, &vec_out[0]);
        gpu.queue.finish();
        STOP_CLOCK;

        std::cout << "Entire calculation took " << DELTA << " nanoseconds in opencl gpu" << std::endl;
    }
    {
        START_CLOCK
        auto cpu = mason::cl::cl_device(CL_DEVICE_TYPE_CPU);

        std::string code =
                "   void kernel simple_add(global const int* A, global const int* B, global int* C){       "
                "       C[get_global_id(0)]=A[get_global_id(0)]+B[get_global_id(0)];                 "
                "   }                                                                               ";

        cl::Program::Sources srcs;
        srcs.push_back({code.c_str(), code.length()});

        cl::Program prog = cl::Program(cpu.context, srcs);
        if (prog.build(cpu.devices) != CL_SUCCESS) {
            std::cout << " Error building: " << prog.getBuildInfo<CL_PROGRAM_BUILD_LOG>(cpu.devices[0]) << "\n";
            exit(1);
        }

        std::vector<int> vec_in1(BATCH_SIZE);
        std::fill(vec_in1.begin(), vec_in1.end(), 5);
        std::vector<int> vec_in2(BATCH_SIZE);
        std::fill(vec_in2.begin(), vec_in2.end(), 3);

        cl::Buffer in1(cpu.queue, vec_in1.begin(), vec_in1.end(), true);
        cl::Buffer in2(cpu.queue, vec_in2.begin(), vec_in2.end(), true);

        cl::Buffer out(cpu.context, CL_MEM_READ_WRITE, sizeof(int) * BATCH_SIZE);

        cl::Kernel add_func = cl::Kernel(prog, "simple_add");
        add_func.setArg(0, in1);
        add_func.setArg(1, in2);
        add_func.setArg(2, out);

        cpu.queue.enqueueNDRangeKernel(add_func, cl::NullRange, cl::NDRange(BATCH_SIZE), cl::NullRange);
        cpu.queue.finish();
        cpu.queue.flush();

        std::vector<int> vec_out(BATCH_SIZE);
        cpu.queue.enqueueReadBuffer(out, CL_TRUE, 0, sizeof(int) * BATCH_SIZE, &vec_out[0]);
        cpu.queue.finish();
        cpu.queue.flush();
        STOP_CLOCK;

        std::cout << "Entire calculation took " << DELTA << " nanoseconds in opencl cpu" << std::endl;
    }


//    std::cout << "[";
//    for (int i : vec_out) {
//        std::cout << i << ", ";
//    }
//    std::cout << "]" << std::endl;
    {
        START_CLOCK
        std::vector<int> van_out(BATCH_SIZE);
        std::vector<int> van_in1(BATCH_SIZE);
        std::vector<int> van_in2(BATCH_SIZE);
        std::fill(van_in2.begin(), van_in2.end(), 3);
        std::fill(van_in1.begin(), van_in1.end(), 5);

        for (int i = 0; i < BATCH_SIZE; i++) {
            van_out[i] = van_in1[i] + van_in2[i];
        }
        STOP_CLOCK
        std::cout << "Entire calculation took " << DELTA << " nanoseconds in plain c++" << std::endl;
    }

    return 0;

}
