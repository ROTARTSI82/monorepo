//
// Created by Grant on 9/27/19.
//

#include "mason/gl/vb_layout.h"

namespace mason::gl {

    template<typename T>
    void vb_layout::push_attribute(GLenum type, int quantity, GLboolean normalize) {
        vb_attribute new_attrib = {quantity, type, normalize, stride};
        attributes.emplace_back(new_attrib);

        stride += sizeof(T) * quantity;
    }

    void vb_layout::set_attributes() {
        for (unsigned long i = 0; i < attributes.size(); i++) {
            vb_attribute attrib = attributes.at(i);
            glEnableVertexAttribArray(i);
            glVertexAttribPointer(i, attrib.count, attrib.type, attrib.normalized, stride,
                                  reinterpret_cast<GLvoid *>(attrib.pointer));
        }
    }

    void vb_layout::disable_attributes() {
        for (unsigned long i = 0; i < attributes.size(); i++) {
            glDisableVertexAttribArray(i);
        }
    }


    vb_layout *get_default_layout() {
        if (default_layout != nullptr) {
            return default_layout;
        } else {
            default_layout = new vb_layout();
            default_layout->push_attribute<float>(GL_FLOAT, 3, false); // Position data
            default_layout->push_attribute<float>(GL_FLOAT, 3, false); // Normal data
            default_layout->push_attribute<float>(GL_FLOAT, 2, false); // UV data
            default_layout->push_attribute<float>(GL_FLOAT, 4, false); // Color data

            return default_layout;
        }
    }
}
