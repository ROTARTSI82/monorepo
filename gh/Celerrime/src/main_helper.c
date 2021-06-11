#include "main_helper.h"


void glfw_error_callback(int code, const char* description)
{
    PRINT("GLFW ERROR %i: %s\n", code, description);
}

static void on_win_resize(GLFWwindow* window, int width, int height) {
    app_t *app = glfwGetWindowUserPointer(window);
    app->win_width = width;
    app->win_height = height;

    if ((float) width / height > GFB_WIDTH / GFB_HEIGHT) {
        // case: width is too long
        float wover = width / (height * GFB_WIDTH / GFB_HEIGHT);
        app->orthographic = ortho(-wover, wover, -1, 1, ZNEAR, ZFAR);
    } else {
        float hover = height / (width / (GFB_WIDTH / GFB_HEIGHT));
        app->orthographic = ortho(-1, 1, -hover, hover, ZNEAR, ZFAR);
    }
}

static void *logic_thread(void *args) {
    app_t *app = (app_t *) args;
    logic_thread_data_t *dat = &app->lt_dat;
    init_fps_limiter(&dat->limiter, 1000000000UL / GPHYS_FPS);

    while (app->is_logic_thread_running) {
        tick_fps_limiter(&dat->limiter);
    }

    return NULL;
}

void start(app_t *app) {
    PRINT("%s\n", APP_VERSION);

    // TODO: Proper settings loader
    app->settings.win_width = 640;
    app->settings.win_height = 480;
    app->settings.fps_cap = 24.5; // tmp

    app->master_ctx.framebuffer = 0; // default framebuffer to actually draw on the screen
    app->master_ctx.fbo_tex = 0;

    // view matrix is not set
    
    app->perspective = perspective(GFOV, GFB_WIDTH / GFB_HEIGHT, ZNEAR, ZFAR);


    glfwSetErrorCallback(glfw_error_callback);
    EXIF(!glfwInit(), "GLFW initialization failed!\n");
    PRINT("GLFW compiled with %i.%i.%i, linked with %s\n", GLFW_VERSION_MAJOR, GLFW_VERSION_MINOR, GLFW_VERSION_REVISION, glfwGetVersionString());

    // 3.3 is the minimum version we need (3.3 is required for glVertexAttribDivisor)
    glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GOPENGL_DEBUG_CONTEXT);
    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
    glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE); // macOS support
    app->win = glfwCreateWindow(app->settings.win_width, app->settings.win_height, "Game Test", NULL, NULL);

    EXIF(app->win == NULL,  "GLFW window creation failed\n")
    glfwMakeContextCurrent(app->win);
    glfwSetWindowUserPointer(app->win, app);
    if (app->settings.fps_cap < 1 && app->settings.fps_cap > 0) {
        glfwSwapInterval(1);
    } else {
        glfwSwapInterval(0);
    }

    if (app->settings.fps_cap < 1) { // fps_cap is just going to be ignored in both vsync & unlimited
        init_fps_limiter(&app->limiter, 0);
    } else {
        init_fps_limiter(&app->limiter, 1000000000 / app->settings.fps_cap);
    }

    glfwSetFramebufferSizeCallback(app->win, on_win_resize);

    // IMPORTANT: Set orthographic projection and set app->win_(width|height)
    on_win_resize(app->win, app->settings.win_width, app->settings.win_height);

    // we don't ever use non-standard stuff but this might increase compatability.
    glewExperimental = GL_TRUE;
    int glew_status = glewInit();
    if (glew_status != GLEW_OK) {
        PRINT("GLEW initialization failed: %s\n", glewGetErrorString(glew_status));
        exit(1);
    }

    PRINT("GLEW %s initialized\n", glewGetString(GLEW_VERSION));

    PRINT("OpenGL %s (GLSL %s) on %s (%s)\n", glGetString(GL_VERSION),
          glGetString(GL_SHADING_LANGUAGE_VERSION), glGetString(GL_RENDERER), glGetString(GL_VENDOR));


    glGetIntegerv(GL_MAJOR_VERSION, &app->gl_major);
    glGetIntegerv(GL_MINOR_VERSION, &app->gl_minor);
    glGetIntegerv(GL_CONTEXT_FLAGS, &app->gl_ctx_flags);

    PRINT("OpenGL contex flags = %i\n", app->gl_ctx_flags);

    // if (app->gl_ctx_flags & GL_CONTEXT_FLAG_DEBUG_BIT && app->gl_major >= 4 && app->gl_minor >= 3) {
    glEnable(GL_DEBUG_OUTPUT);
    glDebugMessageCallback(gl_error_MessageCallback, NULL);
    glDebugMessageControl(GL_DONT_CARE, GL_DONT_CARE, GL_DONT_CARE, 0, NULL, GL_TRUE);

    glDisable(GL_DEPTH_TEST); // More efficient if we just render it in order lol
    glDisable(GL_CULL_FACE);
    glEnable(GL_BLEND);
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    glDisable(GL_STENCIL_TEST);

    // counter-clockwise definition. culling is useless in 2d tho
    // Using triangles because the spec doesn't support GL_QUAD with glDrawArraysInstanced?
    float quad[] = {
        -1, -1,
         1, -1,
         1,  1,
         1,  1,
        -1,  1,
        -1, -1
    };

    glGenBuffers(1, &app->quad_vbo);

    glBindBuffer(GL_ARRAY_BUFFER, app->quad_vbo);
    glBufferData(GL_ARRAY_BUFFER, sizeof(quad), quad, GL_STATIC_DRAW);

    init_ctx(app->quad_vbo, &app->master_ctx, MAX_BLITS);
    init_ctx(app->quad_vbo, &app->renderer, MAX_BLITS);
    init_fbo(&app->renderer, GFB_WIDTH, GFB_HEIGHT);

    pthread_mutex_init(&app->rend_mtx, NULL);
    app->is_logic_thread_running = 1;
    pthread_create(&app->logic_thread, NULL, logic_thread, app);

    {
        app->master_ctx.num_blits = 1;
        app->master_ctx.do_flush = 1;

        app->master_ctx.local_array[0].alpha_mult = 1;
        app->master_ctx.local_array[0].num_tiles.x = 1;
        app->master_ctx.local_array[0].num_tiles.y = 1;
        app->master_ctx.local_array[0].sampling_bottom_left.x = 0;
        app->master_ctx.local_array[0].sampling_bottom_left.y = 0;
        app->master_ctx.local_array[0].sampling_extent.x = 1;
        app->master_ctx.local_array[0].sampling_extent.y = 1;

        app->master_ctx.local_array[0].transform = *(const smat2 *) sm2_identity;

        app->master_ctx.local_array[0].translate.x = 0;
        app->master_ctx.local_array[0].translate.y = 0;
        app->master_ctx.local_array[0].translate.z = -1;
    }

    long frag_size, vert_size;
    GLchar *frag = full_read_file("./res/shader/default.frag", &frag_size);
    EXIF(frag == NULL, "Default fragment shader missing!");

    GLchar *vert = full_read_file("./res/shader/default.vert", &vert_size);
    EXIF(vert == NULL, "Default vertex shader missing!");

    GLuint vert_sp = glCreateShader(GL_VERTEX_SHADER);
    GLuint frag_sp = glCreateShader(GL_FRAGMENT_SHADER);

    GLint frag_sizei = (GLint) frag_size, vert_sizei = (GLint) vert_size;
    glShaderSource(vert_sp, 1, (const GLchar *const *) &vert, &vert_sizei);
    glShaderSource(frag_sp, 1, (const GLchar *const *) &frag, &frag_sizei);

    compile_and_check_shader(vert_sp);
    compile_and_check_shader(frag_sp);

    app->default_shader = glCreateProgram();
    glAttachShader(app->default_shader, vert_sp);
    glAttachShader(app->default_shader, frag_sp);
    glLinkProgram(app->default_shader);
    glUseProgram(app->default_shader);

    glDeleteShader(vert_sp);
    glDeleteShader(frag_sp);

    free((void *) frag);
    free((void *) vert);

    app->su_proj_mat = glGetUniformLocation(app->default_shader, "projection_mat");
    app->su_view_mat = glGetUniformLocation(app->default_shader, "view_mat");
    app->su_tex_samp = glGetUniformLocation(app->default_shader, "tex");

    {
//         glGenTextures(1, &app->TMP_TEST_TEX);
//         glActiveTexture(GL_TEXTURE0);
//         glBindTexture(GL_TEXTURE_2D, app->TMP_TEST_TEX);
//         glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
//         glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
//         glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
//         glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

//         unsigned char *texdat = malloc(40*40*4);
//         memset(texdat, 0x00, 20*40*4);
//         unsigned char v = 0;
//         for (unsigned char *i = texdat + 20*40*4; i < texdat + 40*40*4; i++) {
//             *i = rand_table[v++];
//         }
// //        memset(texdat, 0x88, 40*40*4);
//         glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, 40, 40, 0, GL_RGBA, GL_UNSIGNED_BYTE, texdat);
//         free(texdat);

//         glGenerateMipmap(GL_TEXTURE_2D);
        new_tex_from_file("./res/out.rim", &app->TMP_TEST_TEX);
    }
}

void stop(app_t *app) {

    destroy_ctx(&app->renderer);
    destroy_ctx(&app->master_ctx);
    glDeleteTextures(1, &app->TMP_TEST_TEX);
    glDeleteBuffers(1, &app->quad_vbo);
    glDeleteProgram(app->default_shader);

    app->is_logic_thread_running = 0;
    pthread_join(app->logic_thread, NULL);
    pthread_mutex_destroy(&app->rend_mtx);

    flush_gl_errors();
    glfwDestroyWindow(app->win);
    glfwTerminate();
}

