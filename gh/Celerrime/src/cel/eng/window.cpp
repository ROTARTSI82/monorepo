#include "cel/eng/window.hpp"

#include "cel/settings.hpp"
#include "cel/eng/log.hpp"

namespace cel {


    draw_instance::draw_instance(gl_mat2 t, gl_vec3 c, gl_float a, gl_vec2 o, gl_vec2 e, gl_vec2 n) : transform(t), center(c), alpha_mult(a), sample_origin(o), sample_extent(e), sample_ntiles(n) {}

    draw_call::draw_call(size_t max_instances) : instances(new draw_instance[max_instances]) {
        vao.bind_record();

        glBindBuffer(GL_ARRAY_BUFFER, rectangle_vbo::get());
        vao.vertexAttribPointer(0, 2, consts_for<gl_float>::gl_type_enum, GL_FALSE, 2 * sizeof(gl_float), 0); // vertex vec2
        vao.enableVertexAttribArray(0);


        glGenBuffers(1, &vbo);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, max_instances * sizeof(draw_instance), NULL, GL_DYNAMIC_DRAW);

        // 2 vec2s in the mat2 (2d model matrix)
        vao.vertexAttribPointer(1, 2, consts_for<gl_float>::gl_type_enum, GL_FALSE, sizeof(draw_instance), 0);
        vao.vertexAttribPointer(2, 2, consts_for<gl_float>::gl_type_enum, GL_FALSE, sizeof(draw_instance), reinterpret_cast<const GLvoid *>(sizeof(gl_float) * 2));

        // 3d translation
        vao.vertexAttribPointer(3, 3, consts_for<gl_float>::gl_type_enum, GL_FALSE, sizeof(draw_instance), reinterpret_cast<const GLvoid *>(sizeof(gl_float) * 4));
        // alpha multiplier
        vao.vertexAttribPointer(4, 1, consts_for<gl_float>::gl_type_enum, GL_FALSE, sizeof(draw_instance), reinterpret_cast<const GLvoid *>(sizeof(gl_float) * 7));

        // vec 2 for bottom left corner of sampling rect
        vao.vertexAttribPointer(5, 2, consts_for<gl_float>::gl_type_enum, GL_FALSE, sizeof(draw_instance), reinterpret_cast<const GLvoid *>(sizeof(gl_float) * 8));

        // vec 2 for extent of sampling rect (width, height)
        vao.vertexAttribPointer(6, 2, consts_for<gl_float>::gl_type_enum, GL_FALSE, sizeof(draw_instance), reinterpret_cast<const GLvoid *>(sizeof(gl_float) * 10));

        // vec 2 for the number of times we should tile in x and y
        vao.vertexAttribPointer(7, 2, consts_for<gl_float>::gl_type_enum, GL_FALSE, sizeof(draw_instance), reinterpret_cast<const GLvoid *>(sizeof(gl_float) * 12));

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

    window::window(settings_handler *settings, window_types type) : settings(settings), type(type) {
        glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, CEL_USE_OGL_DBG_CTX);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE); // macOS support
        win = glfwCreateWindow(settings->wins[static_cast<uint8_t>(type)].width, settings->wins[static_cast<uint8_t>(type)].height, 
                               settings->strs.window_titles[static_cast<uint8_t>(type)].c_str(), NULL, NULL);
        
        CEL_EXITIF(!win, 1, "Window creation failed for type {}", type);
        glfwMakeContextCurrent(win);


        glewExperimental = GL_TRUE;
        int glew_status = glewInit();
        CEL_EXITIF(glew_status != GLEW_OK, 1, "GLEW init failed for window type {}: {}", static_cast<int>(type), glewGetErrorString(glew_status));
    }

    window::~window() {
        rectangle_vbo::destroy();
        flush_gl_errors();
        CEL_TRACE("glfwDestroyWindow()");
        glfwDestroyWindow(win);
    }

    void window::next() {
        glfwSwapBuffers(win);
    }
}