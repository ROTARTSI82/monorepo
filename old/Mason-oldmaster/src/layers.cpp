//
// Created by Grant on 2019-08-24.
//

#include "mason/layers.h"

namespace mason {

    void Layer::enable() {
        this->onEnable();
        active = true;
    }

    void Layer::disable() {
        this->onDisable();
        active = false;
    }

    void UpdaterGroup::threadFunc(UpdaterGroup *group, unsigned int threadNum) {
        while (group->isRunning) {
            group->tickFunc(group, threadNum);
            std::this_thread::sleep_for(
                    std::chrono::milliseconds(static_cast<std::chrono::milliseconds::rep>(group->delay)));
        }
    }

    void UpdaterGroup::stop() {
        isRunning = false;
        for (int i = 0; i < numThreads; i++) {
            // TODO: Decide whether to join or detach the threads.
            if ((*(threads + i)).joinable()) {
                (*(threads + i)).join();
            }

            // This probably isn't necessary due to delete[] threads;
            delete (threads + i);
        }
        delete[] threads;
    }

    void UpdaterGroup::start() {
        isRunning = true;
        for (int i = 0; i < numThreads; i++) {
            (*(threads + i)) = std::thread(threadFunc, this, i); // Maybe use new?
        }
    }

    UpdaterGroup::UpdaterGroup(void (*func)(mason::UpdaterGroup *, unsigned int), unsigned int nthreads,
                               unsigned int del,
                               LayerStack *par) : tickFunc(func), numThreads(nthreads), delay(del), stack(par) {
        threads = new std::thread[numThreads];
    }

    UpdaterGroup::~UpdaterGroup() {
        stop();
    }

    void LayerStack::eventTick(UpdaterGroup *gp, unsigned int tn) {
        Event *e;
        mason::event_mtx.lock();
        if (!mason::event_que.empty()) {
            e = mason::event_que.front();
            mason::event_que.pop();
            mason::event_mtx.unlock();

            for (auto iter = gp->stack->rbegin(); iter != gp->stack->rend(); iter++) {
                if ((*iter)->active) {
                    (*iter)->updateEvent(e);
                    if (e->handled) {
                        break;
                    }
                }
            }

            // Event is handled and deleted
            delete e;
        } else {
            mason::event_mtx.unlock();
        }
    }

    void
    LayerStack::addUpdaterGroup(void(*func)(UpdaterGroup *, unsigned int), unsigned int threads, unsigned int del) {
        updaterGroups.push_back(new UpdaterGroup(func, threads, del, this));
    }

    void LayerStack::startUpdaters() {
        for (UpdaterGroup *g : updaterGroups) {
            g->start();
        }
    }

    void LayerStack::stopUpdaters() {
        for (UpdaterGroup *g : updaterGroups) {
            g->stop();
        }
    }

    LayerStack::~LayerStack() {
        for (UpdaterGroup *g : updaterGroups) {
            g->stop();
            delete g;
        }

        for (Layer *l : *this) {
            delete l;
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
