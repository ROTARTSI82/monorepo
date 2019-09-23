#include <iostream>
#include "mason/app_node.h"


class custom_layer : public mason::layer {
public:
    int x;
    bool parState;

    custom_layer(int id, bool ps) : x(id), parState(ps) {};

    void on_attach() {
        std::cout << "Layer " << x << " was attached!" << std::endl;
    }

    void on_detach() {
        std::cout << "Layer " << x << " was detached!" << std::endl;
    }

    void on_update(int thread);
};

class custom_app : public mason::app_node {
public:
    bool s;

    custom_app(bool x) : s(x) {}

    void on_activate() {
        std::cout << "app was activated!" << std::endl;
        this->push_overlay(new custom_layer(1, s));
        this->push_underlay(new custom_layer(0, s));
    };

    void on_deactivate() {
        std::cout << "app was deactivated!" << std::endl;
    };
};

void custom_layer::on_update(int thread) {
    {
        std::cout << "update called on layer " << x << " from thread " << thread << std::endl;
        parent_app->parent->remove_child(0);
        parent_app->parent->push_child(new custom_app(!parState));

    }
}

int main() {
    custom_app *app = new custom_app(false);
    app->on_activate();
    for (int i = 0; i < 5; i++) {
        app->update(i);
    }
    app->on_deactivate();
    delete app;

}
