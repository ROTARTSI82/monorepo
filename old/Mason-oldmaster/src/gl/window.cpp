//
// Created by Grant on 2019-08-26.
//

#include "mason/gl/gl_includes.h"
#include "mason/imgui_includes.h"

namespace mason::gl {
    std::string OPENGL_VERSION;
    std::string GLSL_VERSION;

    void initGLEW() {  // Only call this after you have a valid window
        glewExperimental = GL_TRUE;
        GLenum glew = glewInit();
        if (glew != GLEW_OK) {
            MASON_CRITICAL("Failed to init GLEW: glewInit() = {}", glew)
            std::terminate();
        }
        MASON_INFO("Successfully init GLEW {}", glewGetString(GLEW_VERSION))
    }

    static void
    errCallback(GLenum source, GLenum type, GLuint id, GLenum severity, GLsizei length, const GLchar *message,
                const void *userParam) {
        MASON_CRITICAL("[** OPENGL ERROR **]: [SRC={}|TYPE={}|SEVERITY={}]: {}", source, type, severity, message)
    }

    void initGL(bool gl4) {
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glBlendEquation(GL_ADD);

        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LESS);

        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        glFrontFace(GL_CW);

        if (gl4) {
            glEnable(GL_DEBUG_OUTPUT);
            glDebugMessageCallback(errCallback, nullptr);
        }

//        glEnable(GL_STENCIL_TEST);
//        glStencilMask(0x00);  // Everything gets written as-is
//        glStencilFunc(GL_ALWAYS, 0, 0x00);  // Everything passes
//        glStencilOp(GL_KEEP, GL_KEEP, GL_KEEP);  // Keep everything

        std::string glVer(reinterpret_cast<const char *>(glGetString(GL_VERSION)));

        MASON_INFO("Initialized OpenGL {}", glVer)
        MASON_INFO("OpenGL Vendor: {}", glGetString(GL_VENDOR))
        MASON_INFO("GL Shading Language Version: ", glGetString(GL_SHADING_LANGUAGE_VERSION))
        MASON_INFO("OpenGL Extensions: {}", glGetString(GL_EXTENSIONS))

        glVer = glVer.substr(0, 3); // get opengl version;
        OPENGL_VERSION = glVer;

        if (glVer == "2.0") {
            GLSL_VERSION = "#version 110";
        } else if (glVer == "2.1") {
            GLSL_VERSION = "#version 120";
        } else if (glVer == "3.0") {
            GLSL_VERSION = "#version 130";
        } else if (glVer == "3.1") {
            GLSL_VERSION = "#version 140";
        } else if (glVer == "3.2") {
            GLSL_VERSION = "#version 150";
        } else {
            std::stringstream ss;
            ss << "#version " << glVer[0] << glVer[2] << "0";
            GLSL_VERSION = ss.str();
        }

        std::cout << "OpenGL " << glVer << " and GLSL " << GLSL_VERSION << std::endl;

    }

    void initGLFW() {

        int stat = glfwInit();
        if (!stat) {
            MASON_CRITICAL("Failed to init GLFW: glfwInit() = {}", stat)
            std::terminate();
        }

        glfwSetErrorCallback([](int errcode, const char *msg) {
            MASON_FATAL("[** GLFW ERROR {} **]: {}", errcode, msg)
        });

        MASON_INFO("Successfully init GLFW {}", glfwGetVersionString())
    }


    GLWindow::GLWindow(int width, int height, const char *title, GLFWmonitor *mon, GLFWwindow *share) {

        initGLFW();
        const GLFWvidmode *mode;

        if (mon != nullptr) {
            mode = glfwGetVideoMode(mon);
            monitor = mon;
        } else {
            mode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        }

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_RED_BITS, mode->redBits);
        glfwWindowHint(GLFW_GREEN_BITS, mode->greenBits);
        glfwWindowHint(GLFW_BLUE_BITS, mode->blueBits);
        glfwWindowHint(GLFW_REFRESH_RATE, mode->refreshRate);

        if (!width) {
            width = mode->width;
        }

        if (!height) {
            height = mode->height;
        }

        win = glfwCreateWindow(width, height, title, mon, share);
        if (!win) {
            MASON_CRITICAL("Failed to create window! Terminating...");
            glfwTerminate();
            std::terminate();
        }

        bind();
        initGLEW();
        initGL();

        setCallbacks();
    }

    GLWindow::GLWindow(mason::gl::GLWindow *parent) {
        initGLFW();
        const GLFWvidmode *mode;

        monitor = parent->monitor;
        if (monitor != nullptr) {
            mode = glfwGetVideoMode(monitor);
        } else {
            mode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        }

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_RED_BITS, mode->redBits);
        glfwWindowHint(GLFW_GREEN_BITS, mode->greenBits);
        glfwWindowHint(GLFW_BLUE_BITS, mode->blueBits);
        glfwWindowHint(GLFW_REFRESH_RATE, mode->refreshRate);

        int width, height;
        glfwGetWindowSize(parent->win, &width, &height);

        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        win = glfwCreateWindow(width, height, "ThreadSlave", parent->monitor, parent->win);
        if (!win) {
            MASON_FATAL("Failed to copy window! The copy has a nullptr and WILL SEGFAULT!")
        }

        bind();
        initGLEW();
        initGL();

        setCallbacks();
    }

    GLWindow::~GLWindow() {
        if (useIMGUI) {
            ImGui_ImplOpenGL3_Shutdown();
            ImGui_ImplGlfw_Shutdown();
            ImGui::DestroyContext();
        }

        if (win != nullptr) {
            glfwDestroyWindow(win);
        }
    }

    void GLWindow::initIMGUI() {
        useIMGUI = true;
        // Setup Dear ImGui context
        // Setup Dear ImGui context
        IMGUI_CHECKVERSION();
        ImGui::CreateContext();
        ImGuiIO &io = ImGui::GetIO();
        (void) io;
        //io.ConfigFlags |= ImGuiConfigFlags_NavEnableKeyboard;     // Enable Keyboard Controls
        //io.ConfigFlags |= ImGuiConfigFlags_NavEnableGamepad;      // Enable Gamepad Controls

        // Setup Dear ImGui style
        ImGui::StyleColorsDark();
        //ImGui::StyleColorsClassic();

        // Setup Platform/Renderer bindings
        ImGui_ImplGlfw_InitForOpenGL(win, true);
        ImGui_ImplOpenGL3_Init(GLSL_VERSION.c_str());

        // Load Fonts
        // - If no fonts are loaded, dear imgui will use the default font. You can also load multiple fonts and use ImGui::PushFont()/PopFont() to select them.
        // - AddFontFromFileTTF() will return the ImFont* so you can store it if you need to select the font among multiple.
        // - If the file cannot be loaded, the function will return NULL. Please handle those errors in your application (e.g. use an assertion, or display an error and quit).
        // - The fonts will be rasterized at a given size (w/ oversampling) and stored into a texture when calling ImFontAtlas::Build()/GetTexDataAsXXXX(), which ImGui_ImplXXXX_NewFrame below will call.
        // - Read 'misc/fonts/README.txt' for more instructions and details.
        // - Remember that in C/C++ if you want to include a backslash \ in a string literal you need to write a double backslash \\ !
        io.Fonts->AddFontDefault();
//        io.Fonts->AddFontFromFileTTF("/Users/Grant/Desktop/CLion/Mason/vendor/imgui/misc/fonts/Roboto-Medium.ttf", 16.0f);
//        io.Fonts->AddFontFromFileTTF("/Users/Grant/Desktop/CLion/Mason/vendor/imgui/misc/fonts/Cousine-Regular.ttf", 15.0f);
//        io.Fonts->AddFontFromFileTTF("/Users/Grant/Desktop/CLion/Mason/vendor/imgui/misc/fonts/DroidSans.ttf", 16.0f);
//        io.Fonts->AddFontFromFileTTF("/Users/Grant/Desktop/CLion/Mason/vendor/imgui/misc/fonts/ProggyTiny.ttf", 10.0f);
//        ImFont* font = io.Fonts->AddFontFromFileTTF("/Users/Grant/Desktop/CLion/Mason/vendor/imgui/misc/fonts/Karla-Regular", 18.0f);
//        IM_ASSERT(font != nullptr);
    }

    void GLWindow::setCallbacks() {
        glfwSetKeyCallback(win, EventHandler::key);
        glfwSetCursorPosCallback(win, EventHandler::mousemotion);
        glfwSetCursorEnterCallback(win, EventHandler::mousefocus);
        glfwSetMouseButtonCallback(win, EventHandler::mousebutton);
        glfwSetScrollCallback(win, EventHandler::mousescroll);
        glfwSetJoystickCallback(EventHandler::joystick);
        glfwSetDropCallback(win, EventHandler::filedrop);
        glfwSetWindowSizeCallback(win, EventHandler::windowresize);
        glfwSetWindowCloseCallback(win, EventHandler::windowclose);
        glfwSetWindowIconifyCallback(win, EventHandler::windowicon);
        glfwSetWindowFocusCallback(win, EventHandler::windowfocus);
    }

    void GLWindow::bind() {
        glfwMakeContextCurrent(win);
    }

    bool GLWindow::shouldClose() {
        return glfwWindowShouldClose(win);
    }

    void GLWindow::clear() {
        if (useIMGUI) {
            glBindVertexArray(0);
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glUseProgram(0);
            glBindTexture(GL_TEXTURE_2D, 0);

            ImGui_ImplOpenGL3_NewFrame();
            ImGui_ImplGlfw_NewFrame();
            ImGui::NewFrame();
        }

        bind();
        glClearColor(clearColor.r, clearColor.g, clearColor.b, clearColor.a);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        double now = glfwGetTime();
        deltaTime = now - lastFrame;
        lastFrame = now;
    }

    void GLWindow::drawIMGUI() {
        glBindVertexArray(0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glUseProgram(0);
        glBindTexture(GL_TEXTURE_2D, 0);

        ImGui::Render();
        ImGui_ImplOpenGL3_RenderDrawData(ImGui::GetDrawData());
    }

    void GLWindow::flip() {
        bind();
        glfwSwapBuffers(win);
        glfwPollEvents();

        GLenum status = glGetError();

        while (status != GL_NO_ERROR) {
            MASON_CRITICAL("[** OpenGL Error **]: {}", status)
            status = glGetError();
        }
    }

    void GLWindow::setClearColor(GLclampf r, GLclampf g, GLclampf b, GLclampf a) {
        clearColor = {r, g, b, a};
    }

    void GLWindow::draw(GameObject *obj) {
        if (useVAOs) {
            obj->model->vbo->bind();
            obj->model->vao->bind();
        } else {
            obj->model->vbo->reloadLayout();
        }

        obj->model->ibo->bind();
        obj->shader->bind();
        obj->texture->bind();
        obj->shader->setUniform1i("u_Texture", 0);
        obj->shader->setUniformMat4f("u_ModelMatrix", obj->transform);
        obj->shader->setUniformMat4f("u_ViewMatrix", view);
        obj->shader->setUniformMat4f("u_ProjectionMatrix", projection);

        obj->preRender();

        glDrawElements(obj->model->drawMode, obj->model->ibo->count, obj->model->ibo->dataType, nullptr);
    }

    void GLWindow::drawAll() {
        for (GameObject *go : renderQueue) {
            draw(go);
        }
    }
}