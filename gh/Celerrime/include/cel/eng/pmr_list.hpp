/**
 * @file pmr_list.hpp
 * @author your name (you@domain.com)
 * @brief
 * @version 0.1
 * @date 2021-07-12
 *
 * @copyright Copyright (c) 2021
 *
 */

#pragma once

#ifndef CEL_ENG_UTIL_HPP
#define CEL_ENG_UTIL_HPP

#include <stdexcept>

namespace cel {

    /**
     * @brief Polymorphic linked list where
     *        each node can be a different size.
     * Modeled after std::forward_list
     */
    template <typename T>
    class pmr_list {
    public:
        typedef unsigned size_type;

    private:
        template <typename F>
        friend class pmr_list;

        struct node {
            node *next = nullptr;
            uint8_t data{}; // minimum size is 1 byte.
        };

        // this node is used for implemented before_begin()
        node head{};

        inline node *bare_push_after(node *after, size_type size) {
            // intentional sizeof(node *): We need the size of the pointer type.
            node *to_push = reinterpret_cast<node *>(new uint8_t[sizeof(node *) + size]);

            to_push->next = after->next;
            after->next = to_push;
            return to_push;
        }

    public:

        class iterator {
        private:
            template <typename F>
            friend class pmr_list;

            node *n;

        public:
            // can't figure out how to do it without const_cast :[
            explicit iterator(const node *in) : n(const_cast<node *>(in)) {}

            iterator(const iterator &rhs) noexcept : n(rhs.n) {} // allow implicit conversion

            iterator &operator=(const iterator &rhs) noexcept {
                if (this == &rhs) return *this;
                n = rhs.n;
                return *this;
            }

            inline T *operator*() const { return reinterpret_cast<T *>(&n->data); }
            inline iterator &operator++() { n = n->next; return *this; } // prefix ++

            inline bool operator!=(const iterator &rhs) const noexcept { return n != rhs.n; }
        };

        pmr_list() = default;

        pmr_list(const pmr_list &other) { *this = other; };
        pmr_list(pmr_list &&other) noexcept { *this = std::move(other); }; // should this be std::forward?

        ~pmr_list() {
            clear();
        }

        template <typename P>
        pmr_list &operator=(const pmr_list<P> &other) {
            if (this == &other) return *this;
            clear();

            iterator pos = before_begin();

            auto e = other.end();
            for (auto it = other.begin(); it != e; ++it)
                pos = insert_after(pos, *(*it)); // dereference twice!
        }

        template <typename P>
        pmr_list &operator=(pmr_list<P> &&other) noexcept {
            if (this == &other) return *this;
            clear();
            head = other.head;
            other.head = node{};
            return *this;
        }

        [[nodiscard]] inline bool empty() const noexcept { return head.next == nullptr; /* begin() == end() */ };

        inline void pop_front() {
            erase_after(before_begin());
        }

        void clear() noexcept {
            while (!empty()) {
                pop_front();
            }
        }

        template <typename P>
        iterator push_front(const P &in) {
            node *ret = bare_push_after(&head, sizeof(P));
            new (&ret->data) P(in);
            return iterator{ret};
        }

        template <typename P>
        iterator push_front(P &&in) {
            node *ret = bare_push_after(&head, sizeof(P));
            new (&ret->data) P(std::forward<P>(in));
            return iterator{ret};
        }

        template <typename P, typename... Args>
        iterator emplace_front(Args&&... args) {
            node *ret = bare_push_after(&head, sizeof(P));
            new (&ret->data) P(args...);
            return iterator{ret};
        }

        inline iterator before_begin() noexcept { return iterator{&head}; }
        inline iterator begin() noexcept { return iterator{head.next}; }
        inline iterator end() noexcept { return iterator{nullptr}; }

        template <typename P>
        iterator insert_after(iterator pos, const P &value) {
            node *ret = bare_push_after(pos.n, sizeof(P));
            new (&ret->data) P(value);
            return iterator{ret};
        }

        template <typename P>
        iterator insert_after(iterator pos, P &&value) {
            node *ret = bare_push_after(pos.n, sizeof(P));
            new (&ret->data) P(std::forward<P>(value));
            return iterator{ret};
        }

        /**
         * Inserts a range after a position. The input iterator should point
         * to the raw VALUES of the things we want to insert, not pointers.
         *
         * @tparam InputIt Type of the input iterator.
         * @param pos Position to start inserting after.
         * @param first First element to insert.
         * @param last Iterator to the element AFTER the last element to insert.
         * @return Iterator to the last element that was inserted.
         */
        template<class InputIt>
        iterator insert_after(iterator pos, InputIt first, InputIt last) {
            for (auto begin = first; begin != last; ++begin) // is this copy necessary?
                pos = insert_after(pos, *begin);
            return pos;
        }

        /**
         * Inserts some number of elements after `pos`. The initializer list must contain
         * the desired VALUES to insert, not pointers.
         *
         * @tparam P Type of the values we are inserting.
         * @param pos Element to start inserting after.
         * @param list List of elements to insert.
         * @return Iterator to the last element that was inserted.
         */
        template <typename P>
        iterator insert_after(iterator pos, std::initializer_list<P> list) {
            for (const auto &i : list)
                pos = insert_after<P>(pos, i);
            return pos;
        }

        iterator erase_after(iterator pos) {
            node *after = pos.n;
            node *tmp = after->next->next;

            // explicit destruction. yay.
            reinterpret_cast<T *>(&after->next->data)->~T();
            delete[] reinterpret_cast<uint8_t *>(after->next);
            after->next = tmp;

            return iterator{tmp};
        }

        iterator erase_after(iterator first, iterator last) {
            for (auto begin = first; begin != last;) // is this copy necessary?
                begin = erase_after(begin);
            return last;
        }


        iterator element_before(iterator b) {
            iterator ret = before_begin();
            while (ret.n->next != b.n) ++ret;
            return ret;
        }

    };
}

#endif
