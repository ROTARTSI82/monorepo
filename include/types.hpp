#pragma once

#include "object.hpp"
#include <limits>

class GenericCNum : public Object {
public:
    virtual bool is_signed() const = 0;
    virtual bool is_float() const = 0;

    virtual double fval() const = 0;
    virtual uint64_t uval() const = 0;
    virtual int64_t ival() const = 0;
};


template <typename T>
class CNumber : public GenericCNum {
public:
    T value;
    CNumber(T value) : value(std::move(value)) {};

    bool is_true(VM *ctx) const override { return value != 0; }

    std::string to_string(VM *ctx) const override {
        return std::to_string(value);
    }

    bool is_float() const override {
        return !std::numeric_limits<T>::is_integer;
    }

    bool is_signed() const override {
        return std::numeric_limits<T>::is_signed;
    }

    double fval() const override {
        return value;
    }

    uint64_t uval() const override {
        return value;
    }

    int64_t ival() const override {
        return value;
    }


};

class Bool : public Object {
public:
    bool value;
    Bool(bool value) : value(value) {};

    bool is_true(VM *ctx) const override { return value; }

    std::string to_string(VM *ctx) const override {
        return value ? "True" : "False";
    }
};

