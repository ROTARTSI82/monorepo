//
// Created by Grant Yang on 2/10/22.
//

#pragma once

#ifndef APCSDS_RBTREE_H
#define APCSDS_RBTREE_H

#include <cstdint>

enum RBColor : uint8_t {
    RED, BLACK
};

template <typename T>
class RBNode {
public:

    RBNode *parent = nullptr;

    union {
        struct {
            RBNode *lc = nullptr;
            RBNode *rc = nullptr;
        };

        RBNode *child[2] = {nullptr, nullptr};
    };

    RBColor color = BLACK;
    T value;

    RBNode(T v, RBColor col, RBNode *parent, RBNode *left, RBNode *right)
        : value(v), color(col), parent(parent), lc(left), rc(right) {}

    RBNode(T v, RBColor col)
            : value(v), color(col), lc(nullptr), rc(nullptr), parent(nullptr) {}

    inline static RBNode *bst_insert(RBNode *root, T value) {
        if (!root) return new RBNode(value, BLACK);
        for (;;) {
            RBNode *&next = value > root->value ? root->rc : root->lc;
            if (!next) {
                next = new RBNode(value, RED);
                break;
            } else
                root = next;
        }
    }
};

#endif //APCSDS_RBTREE_H
