#pragma once

#include "types.hpp"

#include <memory>
#include <iostream>

// TODO: Clean up and remove duplicate code here.

#define DISPATCH_TYPED_FUNC(ctx, func, a, b) \
    std::cout << a->to_string(ctx) << '\n' << b->to_string(ctx) << "\n\n"; \
    std::shared_ptr<GenericCNum> ac = std::dynamic_pointer_cast<GenericCNum>(a); \
    std::shared_ptr<GenericCNum> bc = std::dynamic_pointer_cast<GenericCNum>(b); \
    if (!ac || !bc) throw std::runtime_error{"tried to do numeric operation on non numeric type"}; \
    if (ac->is_float() || bc->is_float()) \
        return func<double>(ctx, ac->fval(), bc->fval()); \
    else if (!(ac->is_signed() && bc->is_signed())) \
        return func<uint64_t>(ctx, ac->uval(), bc->uval()); \
    else \
        return func<int64_t>(ctx, ac->ival(), bc->ival());

#define DISPATCH_TYPED_IFUNC(ctx, func, a, b) \
    std::cout << a->to_string(ctx) << '\n' << b->to_string(ctx) << "\n\n"; \
    std::shared_ptr<GenericCNum> ac = std::dynamic_pointer_cast<GenericCNum>(a); \
    std::shared_ptr<GenericCNum> bc = std::dynamic_pointer_cast<GenericCNum>(b); \
    if (!ac || !bc) throw std::runtime_error{"tried to do integer operation on non integer type"}; \
    if (!(ac->is_signed() && bc->is_signed())) \
        return func<uint64_t>(ctx, ac->uval(), bc->uval()); \
    else \
        return func<int64_t>(ctx, ac->ival(), bc->ival());

#define BIN_OP(name, func, expr) \
    class name : public ExpressionNode { \
    public: \
        std::unique_ptr<ExpressionNode> a, b; \
        name(std::unique_ptr<ExpressionNode> &&a, std::unique_ptr<ExpressionNode> &&b) : a(std::move(a)), b(std::move(b)) {}; \
        std::shared_ptr<Object> evaluate(VM *ctx) const override  { \
            expr \
        } \
    };

#define TYPED_FUNC(cls, name, op) template <typename T> \
    std::shared_ptr<Object> name(VM *ctx, T a, T b) { \
        return std::make_shared<CNumber<T>>(op); \
    } \
    BIN_OP(cls, name, DISPATCH_TYPED_FUNC(ctx, name, a->evaluate(ctx), b->evaluate(ctx)))

#define TYPED_IFUNC(cls, name, op) template <typename T> \
    std::shared_ptr<Object> name(VM *ctx, T a, T b) { \
        return std::make_shared<CNumber<T>>(op); \
    } \
    BIN_OP(cls, name, DISPATCH_TYPED_IFUNC(ctx, name, a->evaluate(ctx), b->evaluate(ctx)))

#define TYPED_BFUNC(cls, name, op) template <typename T> \
    std::shared_ptr<Object> name(VM *ctx, T a, T b) { \
        return std::make_shared<Bool>(op); \
    } \
    BIN_OP(cls, name, DISPATCH_TYPED_FUNC(ctx, name, a->evaluate(ctx), b->evaluate(ctx)))

TYPED_FUNC(AddNode, add, a + b)
TYPED_FUNC(SubNode, sub, a - b)
TYPED_FUNC(MulNode, mul, a * b)
TYPED_FUNC(DivNode, divide, a / b)
TYPED_IFUNC(BXorNode, bxor, a ^ b)
TYPED_IFUNC(BAndNode, band, a & b)
TYPED_IFUNC(BOrNode, bor, a | b)
TYPED_BFUNC(LENode, le, a <= b)
TYPED_BFUNC(GENode, ge, a >= b)
TYPED_BFUNC(GTNode, gt, a > b)
TYPED_BFUNC(LTNode, lt, a < b)

class ValueNode : public ExpressionNode {
public:
    std::shared_ptr<Object> ref;
    ValueNode(std::shared_ptr<Object> ref) : ref(ref) {};

    std::shared_ptr<Object> evaluate(VM *ctx) const override {
        return ref;
    }
};


