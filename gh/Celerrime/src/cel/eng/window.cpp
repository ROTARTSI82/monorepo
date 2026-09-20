#include "cel/eng/window.hpp"

#include "cel/app.hpp"
#include "cel/eng/log.hpp"

namespace cel {

    #define CEL_MAKE_GLERRMSGCB_CASE(pref, val, store) case (pref##val):\
(store) = (#val); break;

    void GLAPIENTRY gl_error_callback( GLenum source,
                    GLenum type,
                    GLuint id,
                    GLenum severity,
                    GLsizei length,
                    const GLchar* message,
                    const void* userParam ) {
        CEL_IGNORE(length);
        CEL_IGNORE(userParam);

        if (source == GL_DEBUG_SOURCE_API && (severity == GL_DEBUG_SEVERITY_NOTIFICATION || severity == GL_DEBUG_SEVERITY_LOW) && type == GL_DEBUG_TYPE_OTHER) return;
        const char *type_str, *src_str, *sev_str;
        switch (source) {
            CEL_MAKE_GLERRMSGCB_CASE(GL_DEBUG_SOURCE_, API, src_str)
            CEL_MAKE_GLERRMSGCB_CASE(GL_DEBUG_SOURCE_, WINDOW_SYSTEM, src_str)
            CEL_MAKE_GLERRMSGCB_CASE(GL_DEBUG_SOURCE_, SHADER_COMPILER, src_str)
            CEL_MAKE_GLERRMSGCB_CASE(GL_DEBUG_SOURCE_, THIRD_PARTY, src_str)
            CEL_MAKE_GLERRMSGCB_CASE(GL_DEBUG_SOURCE_, APPLICATION, src_str)
            CEL_MAKE_GLERRMSGCB_CASE(GL_DEBUG_SOURCE_, OTHER, src_str)
            default:
                src_str = "UNKNOWN";
        }

        switch (type) {
            CEL_MAKE_GLERRMSGCB_CASE(GL_DEBUG_TYPE_, ERROR, type_str)
            CEL_MAKE_GLERRMSGCB_CASE(GL_DEBUG_TYPE_, DEPRECATED_BEHAVIOR, type_str)
            CEL_MAKE_GLERRMSGCB_CASE(GL_DEBUG_TYPE_, UNDEFINED_BEHAVIOR, type_str)
            CEL_MAKE_GLERRMSGCB_CASE(GL_DEBUG_TYPE_, PORTABILITY, type_str)
            CEL_MAKE_GLERRMSGCB_CASE(GL_DEBUG_TYPE_, PERFORMANCE, type_str)
            CEL_MAKE_GLERRMSGCB_CASE(GL_DEBUG_TYPE_, MARKER, type_str)
            CEL_MAKE_GLERRMSGCB_CASE(GL_DEBUG_TYPE_, PUSH_GROUP, type_str)
            CEL_MAKE_GLERRMSGCB_CASE(GL_DEBUG_TYPE_, POP_GROUP, type_str)
            CEL_MAKE_GLERRMSGCB_CASE(GL_DEBUG_TYPE_, OTHER, type_str)
            default:
                type_str = "UNKNOWN"; 
        }

        switch (severity) {
            CEL_MAKE_GLERRMSGCB_CASE(GL_DEBUG_SEVERITY_, LOW, sev_str)
            CEL_MAKE_GLERRMSGCB_CASE(GL_DEBUG_SEVERITY_, MEDIUM, sev_str)
            CEL_MAKE_GLERRMSGCB_CASE(GL_DEBUG_SEVERITY_, HIGH, sev_str)
            CEL_MAKE_GLERRMSGCB_CASE(GL_DEBUG_SEVERITY_, NOTIFICATION, sev_str)
            default:
                sev_str = "UNKNOWN";
        }

        CEL_WARN("OpenGL Error {} [{}] [{}] [{}]: {}", id, src_str, sev_str, type_str, message);
    }


    draw_instance::draw_instance(gl_mat2 t, gl_vec3 c, gl_float a, gl_vec2 o, gl_vec2 e, gl_vec2 n) : transform(t), center(c), alpha_mult(a), sample_origin(o), sample_extent(e), sample_ntiles(n) {}

    void draw_line(draw_instance &inst, vec2 a, vec2 b, float depth, float semi_width) {
        inst.center = vec3{(a + b) / 2, depth};
        const auto diff = b - a;
        inst.transform = gl_mat2::transform({diff.magnitude() / 2, semi_width}, std::atan2(diff.y, diff.x));
    }

    draw_call::draw_call(size_t max_instances, const quad_vbo &qvbo) : instances(new draw_instance[max_instances]) {
        vao.bind_record();

        glBindBuffer(GL_ARRAY_BUFFER, qvbo.value);
        vao.vertexAttribPointer(0, 2, consts_for<gl_float>::gl_type_enum, GL_FALSE, sizeof(gl_vec2), 0); // vertex vec2
        vao.enableVertexAttribArray(0);


        glGenBuffers(1, &vbo);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, max_instances * sizeof(draw_instance), NULL, GL_DYNAMIC_DRAW);

        // 2 vec2s in the mat2 (2d model matrix)
        vao.vertexAttribPointer(1, 2, consts_for<gl_float>::gl_type_enum, GL_FALSE, sizeof(draw_instance), 0);
        vao.vertexAttribPointer(2, 2, consts_for<gl_float>::gl_type_enum, GL_FALSE, sizeof(draw_instance), reinterpret_cast<const GLvoid *>(sizeof(gl_float) * 2));

        // 3d translation
        vao.vertexAttribPointer(3, 3, consts_for<gl_float>::gl_type_enum, GL_FALSE, sizeof(draw_instance), reinterpret_cast<const GLvoid *>(offsetof(draw_instance, center)));
        // alpha multiplier
        vao.vertexAttribPointer(4, 1, consts_for<gl_float>::gl_type_enum, GL_FALSE, sizeof(draw_instance), reinterpret_cast<const GLvoid *>(offsetof(draw_instance, alpha_mult)));

        // vec 2 for bottom left corner of sampling rect
        vao.vertexAttribPointer(5, 2, consts_for<gl_float>::gl_type_enum, GL_FALSE, sizeof(draw_instance), reinterpret_cast<const GLvoid *>(offsetof(draw_instance, sample_origin)));

        // vec 2 for extent of sampling rect (width, height)
        vao.vertexAttribPointer(6, 2, consts_for<gl_float>::gl_type_enum, GL_FALSE, sizeof(draw_instance), reinterpret_cast<const GLvoid *>(offsetof(draw_instance, sample_extent)));

        // vec 2 for the number of times we should tile in x and y
        vao.vertexAttribPointer(7, 2, consts_for<gl_float>::gl_type_enum, GL_FALSE, sizeof(draw_instance), reinterpret_cast<const GLvoid *>(offsetof(draw_instance, sample_ntiles)));

        vao.enableVertexAttribArray(1);
        vao.enableVertexAttribArray(2);
        vao.enableVertexAttribArray(3);
        vao.enableVertexAttribArray(4);
        vao.enableVertexAttribArray(5);
        vao.enableVertexAttribArray(6);
        vao.enableVertexAttribArray(7);

        vao.set_instanced(1, 1);
        vao.set_instanced(2, 1);
        vao.set_instanced(3, 1);
        vao.set_instanced(4, 1);
        vao.set_instanced(5, 1);
        vao.set_instanced(6, 1);
        vao.set_instanced(7, 1);
        
        vao.finalize();
    }

    draw_call::~draw_call() {
        glDeleteBuffers(1, &vbo);
        delete[] instances;
    }


    window::window(app_t *app) : app(app), inp(std::make_unique<hacky_input_provider>()) {

        glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, CEL_USE_OGL_DBG_CTX);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE); // macOS support
        win = glfwCreateWindow(app->opt.win_width, app->opt.win_height, 
                               app->opt.txt("win_title").c_str(), NULL, NULL);
        
        if (!win) {
            constexpr const char *msg = "Window creation failed!";
            CEL_CRITICAL(msg);
            throw std::runtime_error{msg};
        }

        glfwMakeContextCurrent(win);
        glfwSwapInterval(1);

        glewExperimental = GL_TRUE;
        int glew_status = glewInit();
        if (glew_status != GLEW_OK) { 
            const std::string msg = fmt::format("GLEW init failed: {}", glewGetErrorString(glew_status));
            CEL_CRITICAL(msg.c_str());
            throw std::runtime_error{msg};
        }


        if (glDebugMessageCallback) { // janky method to check for support. might become an error if it is replaced with a macro/actual function instead of a function pointer
            glEnable(GL_DEBUG_OUTPUT);
            glDebugMessageCallback(gl_error_callback, NULL);
            glDebugMessageControl(GL_DONT_CARE, GL_DONT_CARE, GL_DONT_CARE, 0, nullptr, GL_TRUE);
        }

        glEnable(GL_DEPTH_TEST);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        //glDisable(GL_STENCIL_TEST);

        glDisable(GL_CULL_FACE);
        glCullFace(GL_BACK);

        // glActiveTexture(GL_TEXTURE0);

    }

    window::~window() {
        flush_gl_errors();
        CEL_TRACE("glfwDestroyWindow()");
        glfwDestroyWindow(win);
    }
}