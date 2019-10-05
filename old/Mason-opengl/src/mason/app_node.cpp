//
// Created by Grant on 9/23/19.
//

#include "mason/app_node.h"

namespace mason {

    app_node::~app_node() {
        // Should the children be deleted first? I don't think it matters... (this is probably foreshadowing)
        for (layer *layer : layer_stack) {
            layer->on_detach();
            delete layer;
        }

        for (app_node *child : active_children) {
            child->on_deactivate();
            delete child;
        }
    }

    void app_node::push_underlay(mason::layer *obj) {
        obj->parent_app = this;
        obj->on_attach();
        layer_stack.insert(layer_stack.begin() + insert_index, obj);
        insert_index++;
    }

    void app_node::push_overlay(mason::layer *obj) {
        obj->parent_app = this;
        obj->on_attach();
        layer_stack.emplace_back(obj);
    }

    void app_node::push_child(mason::app_node *obj) {
        obj->parent = this;
        obj->on_activate();
        active_children.emplace_back(obj);
    }

    void app_node::remove_child(const int &index) {
        app_node *child = active_children.at(index);
        child->on_deactivate();
        delete child;

        active_children.erase(active_children.begin() + index);
    }

    void app_node::remove_underlay(const int &index) {
        layer *obj = layer_stack.at(index);
        obj->on_detach();
        delete obj;

        layer_stack.erase(layer_stack.begin() + index);
    }

    void app_node::remove_overlay(const int &index) {
        layer *obj = layer_stack.at(insert_index + index);
        obj->on_detach();
        delete obj;

        layer_stack.erase(layer_stack.begin() + insert_index + index);
    }

    void app_node::update(int thread) {
        for (int i = 0; i < insert_index; i++) {
            layer_stack[i]->on_update(thread);
        }

        for (app_node *child : active_children) {
            child->update(thread);
        }

        for (unsigned long i = insert_index; i < layer_stack.size(); i++) {
            layer_stack[i]->on_update(thread);
        }
    }

    bool app_node::handle_event(mason::gl::gl_event *ev) {
        for (unsigned long i = layer_stack.size(); i >= insert_index; i--) {
            if (layer_stack[i]->on_event(ev)) {
                return true;
            }
        }

        for (unsigned i = active_children.size(); i-- > 0;) {
            if (active_children.at(i)->handle_event(ev)) {
                return true;
            }
        }

        for (int i = insert_index - 1; i >= 0; i--) {
            if (layer_stack[i]->on_event(ev)) {
                return true;
            }
        }

        return false;

    }
}
