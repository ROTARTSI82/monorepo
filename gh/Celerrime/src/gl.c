//
// Created by grant on 5/29/21.
//

#include <stdlib.h>
#include <stdio.h>
#include "gl.h"
#include "file_reader.h"
#include "common.h"

#include <arpa/inet.h>
#include "config.h"

#include <zlib.h>


#define MAKE_GLERRMSGCB_CASE(pref, val, store) case (pref##val):\
(store) = (#val); break;

void GLAPIENTRY gl_error_MessageCallback( GLenum source,
                 GLenum type,
                 GLuint id,
                 GLenum severity,
                 GLsizei length,
                 const GLchar* message,
                 const void* userParam )
{
    const char *type_str, *src_str, *sev_str;
    switch (source) {
        MAKE_GLERRMSGCB_CASE(GL_DEBUG_SOURCE_, API, src_str)
        MAKE_GLERRMSGCB_CASE(GL_DEBUG_SOURCE_, WINDOW_SYSTEM, src_str)
        MAKE_GLERRMSGCB_CASE(GL_DEBUG_SOURCE_, SHADER_COMPILER, src_str)
        MAKE_GLERRMSGCB_CASE(GL_DEBUG_SOURCE_, THIRD_PARTY, src_str)
        MAKE_GLERRMSGCB_CASE(GL_DEBUG_SOURCE_, APPLICATION, src_str)
        MAKE_GLERRMSGCB_CASE(GL_DEBUG_SOURCE_, OTHER, src_str)
        default:
            src_str = "UNKNOWN";
    }

    switch (type) {
        MAKE_GLERRMSGCB_CASE(GL_DEBUG_TYPE_, ERROR, type_str)
        MAKE_GLERRMSGCB_CASE(GL_DEBUG_TYPE_, DEPRECATED_BEHAVIOR, type_str)
        MAKE_GLERRMSGCB_CASE(GL_DEBUG_TYPE_, UNDEFINED_BEHAVIOR, type_str)
        MAKE_GLERRMSGCB_CASE(GL_DEBUG_TYPE_, PORTABILITY, type_str)
        MAKE_GLERRMSGCB_CASE(GL_DEBUG_TYPE_, PERFORMANCE, type_str)
        MAKE_GLERRMSGCB_CASE(GL_DEBUG_TYPE_, MARKER, type_str)
        MAKE_GLERRMSGCB_CASE(GL_DEBUG_TYPE_, PUSH_GROUP, type_str)
        MAKE_GLERRMSGCB_CASE(GL_DEBUG_TYPE_, POP_GROUP, type_str)
        MAKE_GLERRMSGCB_CASE(GL_DEBUG_TYPE_, OTHER, type_str)
        default:
            type_str = "UNKNOWN"; 
    }

    switch (severity) {
        MAKE_GLERRMSGCB_CASE(GL_DEBUG_SEVERITY_, LOW, sev_str)
        MAKE_GLERRMSGCB_CASE(GL_DEBUG_SEVERITY_, MEDIUM, sev_str)
        MAKE_GLERRMSGCB_CASE(GL_DEBUG_SEVERITY_, HIGH, sev_str)
        MAKE_GLERRMSGCB_CASE(GL_DEBUG_SEVERITY_, NOTIFICATION, sev_str)
        default:
            sev_str = "UNKNOWN";
    }

    PRINT("%u [%s] [%s] [%s]: %s\n", id, src_str, sev_str, type_str, message);
}

#undef MAKE_GLERRMSGCB_CASE

#define PROC_GL_ERROR_GEN_CASE(exp) case (exp):\
PRINT("GLERROR: "#exp "\n"); break;

GLenum proc_gl_error() {
    GLenum ret = glGetError();
    switch (ret) {
        PROC_GL_ERROR_GEN_CASE(GL_INVALID_ENUM);
        PROC_GL_ERROR_GEN_CASE(GL_INVALID_VALUE);
        PROC_GL_ERROR_GEN_CASE(GL_INVALID_OPERATION);
        PROC_GL_ERROR_GEN_CASE(GL_STACK_OVERFLOW);
        PROC_GL_ERROR_GEN_CASE(GL_STACK_UNDERFLOW);
        PROC_GL_ERROR_GEN_CASE(GL_OUT_OF_MEMORY);
        PROC_GL_ERROR_GEN_CASE(GL_INVALID_FRAMEBUFFER_OPERATION);
        PROC_GL_ERROR_GEN_CASE(GL_CONTEXT_LOST);
        PROC_GL_ERROR_GEN_CASE(GL_TABLE_TOO_LARGE);
        case GL_NO_ERROR:
            break;
        default:
            PRINT("Unknown OpenGL Error was received!\n");
    }

    GLchar dbg_msg[1024]; // should be enough in most cases
    GLenum src, type, severity;
    GLuint id;
    GLsizei len;
    GLuint dbg_ret = glGetDebugMessageLog(1, 1024, &src, &type, &id, &severity, &len, dbg_msg);
    if (dbg_ret) {
        gl_error_MessageCallback(src, type, id, severity, len, dbg_msg, NULL);
    }

    return ret != GL_NO_ERROR || dbg_ret;
}

void flush_gl_errors() {
    while (proc_gl_error());
}

void init_ctx(GLuint global_vbo, render_ctx_t *ctx, unsigned max_blits) {
    ctx->local_array = malloc(sizeof(draw_instance_t) * max_blits);
    ctx->do_flush = 0;
    ctx->max_blits = max_blits;
    ctx->num_blits = 0;
    ctx->fbo_tex = 0;
    ctx->framebuffer = 0;

    glBindBuffer(GL_ARRAY_BUFFER, 0);
    glGenBuffers(1, &ctx->instance_vbo);

    glGenVertexArrays(1, &ctx->vao);
    glBindVertexArray(ctx->vao);

    glBindBuffer(GL_ARRAY_BUFFER, global_vbo);
    glVertexAttribPointer(0, 2, GL_FLOAT_T, GL_FALSE, 2 * sizeof(float), 0); // vertex vec2
    glEnableVertexAttribArray(0);

    GLsizeiptr vbo_size = (GLsizeiptr) sizeof(draw_instance_t) * max_blits;
    glBindBuffer(GL_ARRAY_BUFFER, ctx->instance_vbo);
    glBufferData(GL_ARRAY_BUFFER, vbo_size, NULL, GL_DYNAMIC_DRAW);

    // 2 vec2s in the mat2 (2d model matrix)
    glVertexAttribPointer(1, 2, GL_FLOAT_T, GL_FALSE, sizeof(draw_instance_t), 0);
    glVertexAttribPointer(2, 2, GL_FLOAT_T, GL_FALSE, sizeof(draw_instance_t), (const void *) (sizeof(FLOAT_T) * 2));

    // 3d translation
    glVertexAttribPointer(3, 3, GL_FLOAT_T, GL_FALSE, sizeof(draw_instance_t), (const void *) (sizeof(FLOAT_T) * 4));
    // alpha multiplier
    glVertexAttribPointer(4, 1, GL_FLOAT_T, GL_FALSE, sizeof(draw_instance_t), (const void *) (sizeof(FLOAT_T) * 7));

    // vec 2 for bottom left corner of sampling rect
    glVertexAttribPointer(5, 2, GL_FLOAT_T, GL_FALSE, sizeof(draw_instance_t), (const void *) (sizeof(FLOAT_T) * 8));

    // vec 2 for extent of sampling rect (width, height)
    glVertexAttribPointer(6, 2, GL_FLOAT_T, GL_FALSE, sizeof(draw_instance_t), (const void *) (sizeof(FLOAT_T) * 10));

    // vec 2 for the number of times we should tile in x and y
    glVertexAttribPointer(7, 2, GL_FLOAT_T, GL_FALSE, sizeof(draw_instance_t), (const void *) (sizeof(FLOAT_T) * 12));

    glEnableVertexAttribArray(1);
    glEnableVertexAttribArray(2);
    glEnableVertexAttribArray(3);
    glEnableVertexAttribArray(4);
    glEnableVertexAttribArray(5);
    glEnableVertexAttribArray(6);
    glEnableVertexAttribArray(7);

    glVertexAttribDivisor(1, 1);
    glVertexAttribDivisor(2, 1);
    glVertexAttribDivisor(3, 1);
    glVertexAttribDivisor(4, 1);
    glVertexAttribDivisor(5, 1);
    glVertexAttribDivisor(6, 1);
    glVertexAttribDivisor(7, 1);

    glBindBuffer(GL_ARRAY_BUFFER, 0);
    // Don't unbind the vao because that causes a shader recompile.
}

void destroy_ctx(render_ctx_t *ctx) {
    glDeleteVertexArrays(1, &ctx->vao);
    glDeleteBuffers(1, &ctx->instance_vbo);
    glDeleteFramebuffers(1, &ctx->framebuffer);
    glDeleteTextures(1, &ctx->fbo_tex);
    free(ctx->local_array);
}

void render(render_ctx_t *ctx) {
    if (ctx->do_flush) {
        ctx->do_flush = 0;
        glBindBuffer(GL_ARRAY_BUFFER, ctx->instance_vbo);
        
        // this might be expensive? maybe double buffer this asynchronously?
        glBufferSubData(GL_ARRAY_BUFFER, 0, ctx->num_blits * sizeof(draw_instance_t), ctx->local_array);
    }

    glBindFramebuffer(GL_FRAMEBUFFER, ctx->framebuffer);
    glClear(GL_COLOR_BUFFER_BIT);
    glBindVertexArray(ctx->vao);
    glDrawArraysInstanced(GL_TRIANGLES, 0, 6, (GLsizei) ctx->num_blits);
}

void compile_and_check_shader(GLuint shader) {
    glCompileShader(shader);
    int  success;
    char infoLog[4096];
    glGetShaderiv(shader, GL_COMPILE_STATUS, &success);
    glGetShaderInfoLog(shader, 4096, NULL, infoLog);
    if (!success) {
        PRINT("Error in shader %i: %s\n", shader, infoLog);
        exit(1);
    }
}

void init_fbo(render_ctx_t *ctx, GLsizei width, GLsizei height) {
    glGenFramebuffers(1, &ctx->framebuffer);
    glBindFramebuffer(GL_FRAMEBUFFER, ctx->framebuffer);

    glGenTextures(1, &ctx->fbo_tex);
    glBindTexture(GL_TEXTURE_2D, ctx->fbo_tex);
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, NULL);

    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

    glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, ctx->fbo_tex, 0);

    EXIF(glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE, "Incomplete framebuffer!\n");
}

void resize_fbo(render_ctx_t *ctx, GLsizei width, GLsizei height) {
    glBindFramebuffer(GL_FRAMEBUFFER, ctx->framebuffer);
    glBindTexture(GL_TEXTURE_2D, ctx->fbo_tex);
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, NULL);
    EXIF(glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE, "Incomplete framebuffer!\n");
}

void new_tex_from_file(const char *filename, GLuint *tex) {
    long s = 0;
    uint8_t *raw = full_read_file(filename, &s);
    if (raw == NULL) {
        // message was already printed by full_read_file
        goto missing_texture;
    }

    uint32_t width = 0, height = 0;
    unsigned long dest_len = 0;
    uint8_t *tex_dat = 0;

    if (s > 10) {
        width = ntohl(*(uint32_t *) raw);
        height = ntohl(*(uint32_t *) (raw + 4));

        dest_len = width * height * 4;
        if (dest_len > MAX_IMG_SIZE) {
            PRINT("ERROR: Cannot load %s; image was over %i bytes!\n", filename, MAX_IMG_SIZE);
            goto missing_texture;
        }

        tex_dat = malloc(dest_len);
        if (uncompress(tex_dat, &dest_len, raw + 8, (uLong) s - 8) != Z_OK) {
            PRINT("Uncompression of %s failed!\n", filename);

            free(tex_dat);
            goto missing_texture;
        } else {
            goto success;
        }
    } else {
        PRINT("Data from %s is too short!\n", filename);
        // fall through to missing_texture.
    }

missing_texture:
    width = 2;
    height = 2;
    dest_len = 2 * 2 * 4;
    tex_dat = malloc(dest_len);

    *((uint32_t *) tex_dat) = htonl(0xff0000ff); // red
    *(((uint32_t *) tex_dat) + 1) = htonl(0x00ff00ff); // green
    *(((uint32_t *) tex_dat) + 2) = htonl(0x0000ffff); // blue
    *(((uint32_t *) tex_dat) + 3) = htonl(0xffffffff); // white

success:

    glGenTextures(1, tex);
    glBindTexture(GL_TEXTURE_2D, *tex);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);

    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, (GLsizei) width, (GLsizei) height, 0, GL_RGBA, GL_UNSIGNED_BYTE, tex_dat);
    glGenerateMipmap(GL_TEXTURE_2D);

    free(tex_dat);
    free(raw);

}
