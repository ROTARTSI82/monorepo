#pragma once

#include <string>
#include <memory>

class VM;

class Object {
public:
    virtual bool is_true(VM *ctx) const { return false; };

    virtual std::string to_string(VM *ctx) const = 0;
};

class ExpressionNode {
public:
    virtual std::shared_ptr<Object> evaluate(VM *ctx) const = 0;
};


class JumpAddr : public Object {
public:
    virtual uint8_t *mutate(VM *ctx, uint8_t *pc) const = 0;
};

class String : public Object {
public:
    std::string data;

    String(const std::string data) : data(data) {};

    bool is_true(VM *ctx) const override { return !data.empty(); }
    std::string to_string(VM *ctx) const override {
        return data;
    }
};
