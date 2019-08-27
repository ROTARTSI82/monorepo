//
// Created by Grant on 2019-08-23.
//

#include <mason/log.h>
#include "mason/application.h"

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
        for (const std::unique_ptr<std::thread> &t : threads) {
            // TODO: Decide whether to join or detach the threads.
//            try {
//                if (t->joinable()) {
//                    t->join();
//                }
//            } catch (std::exception e) {
//                log_critical("Exception (join): {}", e.what());
//                try {
//                    t->detach();
//                } catch (std::exception f) {
//                    log_critical("Exception (detach): {}", f.what());
//                }
//            }

            if (t->joinable()) {
                t->join();
            }

//            delete t;
        }
    }

    void UpdaterGroup::start() {
        isRunning = true;
        for (int i = 0; i < numThreads; i++) {
            threads.push_back(std::make_unique<std::thread>(threadFunc, this, i)); // Maybe use new?
        }
    }

    UpdaterGroup::UpdaterGroup(void (*func)(mason::UpdaterGroup *, unsigned int), unsigned int nthreads,
                               unsigned int del,
                               LayerStack *par) : tickFunc(func), numThreads(nthreads), delay(del), stack(par) {
    }

    UpdaterGroup::~UpdaterGroup() {
        stop();
    }

    Scene::Scene() {
        stack->scene = this;
    }

    Scene::~Scene() {
        delete stack;
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
            l->disable();  // Call onDisable() just to be sure.
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

    Application::~Application() {
        currentScene->exit(-1);
        delete currentScene;
    }

    void Application::start() {
        updateScene(0);
        pre();
        while (running) {
            if (requestedScene != sceneID) {
//                std::cout << "Scene " << requestedScene << " was requested" << std::endl;
                updateScene(requestedScene);
            }

            tick();
            currentScene->tick();
        }
        post();
    }

    void Application::stop() {
        running = false;
    }

    void Application::updateScene(int newScene) {
        // TODO: Add proper logging here for when currentScene is null
        if (currentScene != nullptr) {
            currentScene->exit(newScene);
            delete currentScene;
        }

        currentScene = scenes[newScene]();
        currentScene->app = this;

        if (currentScene == nullptr) {
            std::cerr << "loadScene() returned null on scene " << newScene << std::endl;
        }
        currentScene->enter(sceneID);
        sceneID = newScene;
        requestedScene = newScene;
    }
}
