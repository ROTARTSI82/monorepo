//
// Created by Grant on 2019-08-24.
//

#include "mason/layers.h"

namespace mason {

    void AsyncEventHandler::tick() const {
        // Make it thread safe. what if a new event gets added between event_que.front() and event_que.pop()?
        // One event would be handled twice and another wouldn't be handled at all ;(
        std::lock_guard<std::mutex> lck(mason::event_mtx);

        if (!mason::event_que.empty()) {
            Event *e = mason::event_que.front();
            for (auto iter = lsparent->rbegin(); iter != lsparent->rend(); iter++) {
                if ((*iter)->active) {
                    (*iter)->updateEvent(e);
                    if (e->handled) {
                        break;
                    }
                }
            }

            // Event is handled and deleted
            delete e;

            mason::event_que.pop();
        }
    }

    void Layer::enable() {
        this->onEnable();
        active = true;
    }

    void Layer::disable() {
        this->onDisable();
        active = false;
    }

    LayerStack::LayerStack() {
        eventHandler->start();
    }

    void LayerStack::update() {
        for (const std::shared_ptr<Layer> &layer : (*this)) {
            layer->update();
        }
    }

    void LayerStack::setLayerActive(int index, bool active) {
        if (active) {
            (*this)[index]->enable();
        } else {
            (*this)[index]->disable();
        }
    }

}
