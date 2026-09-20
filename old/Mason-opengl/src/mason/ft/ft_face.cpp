//
// Created by 25granty on 10/28/19.
//

#include "mason/ft/ft_face.h"

namespace mason::ft {
    ft_face::ft_face(const std::string &font, FT_UInt size) {
        internal_face = FT_Face();
        proj = glm::ortho(0.0f, 1.0f, 0.0f, 1.0f);

        FT_New_Face(*mason::ft::ft_lib, font.c_str(), 0, &internal_face);
        FT_Set_Pixel_Sizes(internal_face, 0, size);

        glGenBuffers(1, &vbo);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, sizeof(GLfloat) * 6 * 4, NULL, GL_DYNAMIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    ft_face::~ft_face() {
        for (auto glyph : glyphs) {
            glDeleteTextures(1, &glyph.second.tex_id);
        }

        glDeleteBuffers(1, &vbo);

        FT_Done_Face(internal_face);
    }

    ft_glyph ft_face::get_glyph(char x) {
        if (glyphs.find(x) != glyphs.end()) {
            // Skip invocation if x is already cached.
            // If you want to regenerate a glyph, remove it from the cache and call this function.
            return glyphs[x];
        }

        if (FT_Load_Char(internal_face, x, FT_LOAD_RENDER)) {
            MASON_WARN("Failed to generate glyph for the character {}", x);
            return ft_glyph();
        }

        // Generate texture
        GLuint tex;
        glGenTextures(1, &tex);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, tex);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RED, internal_face->glyph->bitmap.width,
                     internal_face->glyph->bitmap.rows, 0, GL_RED, GL_UNSIGNED_BYTE,
                     internal_face->glyph->bitmap.buffer);

        // Set texture options
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        // Now store character for later use
        ft_glyph glph = {tex, glm::ivec2(internal_face->glyph->bitmap.width, internal_face->glyph->bitmap.rows),
                         glm::ivec2(internal_face->glyph->bitmap_left, internal_face->glyph->bitmap_top),
                         internal_face->glyph->advance.x};
        glyphs[x] = glph;
        return glph;

//        glBindTexture(GL_TEXTURE_2D, 0); // TODO: Figure out if it's worth unbinding. Bad Texture is obvious
    }

    void ft_face::set_screen_size(glm::vec2 size) {
        proj = glm::ortho(0.0f, size.x, 0.0f, size.y);
    }

    void ft_face::render(const std::string &msg, glm::vec2 pos, float scale, glm::vec4 col) {
        float xcpy = pos.x;
        mason::ft::prog->bind();
        mason::ft::prog->set_uniform_mat4fv("projection", proj);
        mason::ft::prog->set_uniform_1i("tex", 0);
        mason::ft::prog->set_uniform_4f("color", col.r, col.g, col.b, col.a);

        glActiveTexture(GL_TEXTURE0);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 4, GL_FLOAT, GL_FALSE, 4 * sizeof(GLfloat), nullptr);

        for (char c : msg) {
            ft_glyph glph = get_glyph(c);

            GLfloat xpos = xcpy + glph.bearing.x * scale;
            GLfloat ypos = pos.y - (glph.size.y - glph.bearing.y) * scale;

            GLfloat w = glph.size.x * scale;
            GLfloat h = glph.size.y * scale;
            // Update VBO for each character
            GLfloat vertices[6][4] = {
                    {xpos,     ypos + h, 0.0, 0.0},
                    {xpos,     ypos,     0.0, 1.0},
                    {xpos + w, ypos,     1.0, 1.0},

                    {xpos,     ypos + h, 0.0, 0.0},
                    {xpos + w, ypos,     1.0, 1.0},
                    {xpos + w, ypos + h, 1.0, 0.0}
            };
            // Render glyph texture over quad
            glBindTexture(GL_TEXTURE_2D, glph.tex_id);
            // Update content of VBO memory
            glBufferSubData(GL_ARRAY_BUFFER, 0, sizeof(vertices),
                            vertices); // Be sure to use glBufferSubData and not glBufferData

            // Render quad
            glDrawArrays(GL_TRIANGLES, 0, 6);
            // Now advance cursors for next glyph (note that advance is number of 1/64 pixels)
            xcpy += (glph.advance >> 6) *
                    scale; // Bitshift by 6 to get value in pixels (2^6 = 64 (divide amount of 1/64th pixels by 64 to get amount of pixels))

        }

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindTexture(GL_TEXTURE_2D, 0);

    }
}
