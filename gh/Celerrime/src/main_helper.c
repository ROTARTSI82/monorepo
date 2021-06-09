#include "main_helper.h"

static void GLAPIENTRY gl_error_MessageCallback( GLenum source,
                 GLenum type,
                 GLuint id,
                 GLenum severity,
                 GLsizei length,
                 const GLchar* message,
                 const void* userParam )
{
  fprintf( stderr, "GL CALLBACK: %s type = 0x%x, severity = 0x%x, message = %s\n",
           ( type == GL_DEBUG_TYPE_ERROR ? "** GL ERROR **" : "" ),
            type, severity, message );
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

void start(app_t *app) {
    PRINT("%s\n", APP_VERSION);

    // TODO: Proper settings loader
    app->settings.win_width = 640;
    app->settings.win_height = 480;

    app->master_ctx.framebuffer = 0; // default framebuffer to actually draw on the screen
    app->master_ctx.fbo_tex = 0;

    // view matrix is not set
    
    app->perspective = perspective(GFOV, (float) GFB_WIDTH / (float) GFB_HEIGHT, ZNEAR, ZFAR);

    glfwInit();

    app->win = glfwCreateWindow(app->settings.win_width, app->settings.win_height, "Game Test", NULL, NULL);

    EXIF(app->win == NULL,  "GLFW window creation failed")
    glfwMakeContextCurrent(app->win);
    glfwSwapInterval(1);
    glfwSetWindowUserPointer(app->win, app);

    glfwSetFramebufferSizeCallback(app->win, on_win_resize);

    // IMPORTANT: Set orthographic projection and set app->win_(width|height)
    on_win_resize(app->win, app->settings.win_width, app->settings.win_height);

    EXIF(glewInit() != GLEW_OK, "GLEW initialization failed")

    glEnable(GL_DEBUG_OUTPUT);
    glDebugMessageCallback(gl_error_MessageCallback, 0);

    PRINT("OpenGL initialized: Renderer %s version %s\n", glGetString(GL_RENDERER), glGetString(GL_VERSION));

    glDisable(GL_DEPTH_TEST); // More efficient if we just render it in order lol
    glDisable(GL_CULL_FACE);
    glEnable(GL_BLEND);
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    glDisable(GL_STENCIL_TEST);

    // counter-clockwise definition. culling is useless in 2d tho
    float quad[] = {
        -1, -1,
        1, -1,
        1, 1,
        -1, 1
    };

    glGenBuffers(1, &app->quad_vbo);

    glBindBuffer(GL_ARRAY_BUFFER, app->quad_vbo);
    glBufferData(GL_ARRAY_BUFFER, sizeof(quad), quad, GL_STATIC_DRAW);

    init_ctx(app->quad_vbo, &app->master_ctx, MAX_BLITS);
    init_ctx(app->quad_vbo, &app->renderer, MAX_BLITS);
    init_fbo(&app->renderer, GFB_WIDTH, GFB_HEIGHT);

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

    glfwDestroyWindow(app->win);
    glfwTerminate();
}

