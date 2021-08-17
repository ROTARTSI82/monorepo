#include "cel/eng/phys.hpp"

namespace cel {
    aligned_rect_collider::aligned_rect_collider(vec2 axes, vec2 cent) : axes(axes), center(cent) {}

    bool aligned_rect_collider::collides_with(aligned_rect_collider &other) {
        vec2 omax = other.center + other.axes;
        vec2 omin = other.center - other.axes;
        vec2 max = center + axes;
        vec2 min = center - axes;

        return !((max.x <= omin.x || min.x >= omax.x) || (max.y <= omin.y || min.y >= omax.y));
    }

    std::tuple<vec2, uint8_t> aligned_rect_collider::collide_direction(aligned_rect_collider &other) {
        vec2 o_max = other.center + other.axes;
        vec2 o_min = other.center - other.axes;
        vec2 max = center + axes;
        vec2 min = center - axes;

        if ((max.x <= o_min.x - grounded_threshold || min.x >= o_max.x + grounded_threshold) ||
            (max.y <= o_min.y - grounded_threshold || min.y >= o_max.y + grounded_threshold)) {
            return std::make_tuple<vec2, uint8_t>({0, 0}, 0);
        }

        float y_cost = std::min(max.y - o_min.y, o_max.y - min.y);
        float x_cost = std::min(o_max.x - min.x, max.x - o_min.x);

        if (max.x <= o_min.x || min.x >= o_max.x || max.y <= o_min.y || min.y >= o_max.y) { 
            if (x_cost < y_cost) {
                if (center.x - other.center.x < 0) {
                    return std::make_tuple<vec2, uint8_t>({-1, 0}, 1); // -x
                } else {
                    return std::make_tuple<vec2, uint8_t>({1, 0}, 1); // +x
                }
            } else {
                if (center.y - other.center.y < 0) {
                    return std::make_tuple<vec2, uint8_t>({0, -1}, 1); // -y
                } else {
                    return std::make_tuple<vec2, uint8_t>({0, 1}, 1); // +y
                }
            }
        }

        if (x_cost < y_cost) {
            if (center.x - other.center.x < 0) {
                return std::make_tuple<vec2, uint8_t>({o_min.x - max.x, 0}, 3);
            } else {
                return std::make_tuple<vec2, uint8_t>({o_max.x - min.x, 0}, 3);
            }
        } else {
            if (center.y - other.center.y < 0) {
                return std::make_tuple<vec2, uint8_t>({0, o_min.y - max.y}, 3);
            } else {
                return std::make_tuple<vec2, uint8_t>({0, o_max.y - min.y}, 3);
            }
        }





        /* // range of atan2 is [-pi, pi]
        double o_theta = std::atan2(other.axes.y, other.axes.x); // otheta MUST lie in the range (0, pi/2)

        vec2 diff = center - other.center;
        double theta = std::atan2(diff.y, diff.x);

        ImGui::Text("o_max = %s, diff = %s", to_string(o_max).c_str(), to_string(diff).c_str());
        ImGui::Text("theta = %f", theta);
        ImGui::Text("++ = %f", o_theta);
        ImGui::Text("+- = %f", -o_theta);
        ImGui::Text("-+ = %f", consts_for<double>::pi() - o_theta);
        ImGui::Text("-- = %f", (-consts_for<double>::pi() + o_theta));


        std::tuple<vec2, uint8_t> ret = std::make_tuple<vec2, uint8_t>({0, 0}, 3);

        if (max.x <= o_min.x || min.x >= o_max.x || max.y <= o_min.y || min.y >= o_max.y) {
            // this isn't a "true" collision. We count as grounded, but don't push.
            std::get<1>(ret) = 1;

            if (theta <= -o_theta && theta >= (-consts_for<double>::pi() + o_theta)) { 
                ImGui::Text("grounded -y");
                std::get<0>(ret) = vec2{0, -1}; // push in -y
            } else if (theta >= o_theta && theta <= consts_for<double>::pi() - o_theta) {
                ImGui::Text("grounded +y");
                std::get<0>(ret) = vec2{0, 1}; // push in +y
            } else if (theta <= o_theta && theta >= -o_theta) {
                ImGui::Text("grounded +x");
                std::get<0>(ret) = vec2{1, 0};  // push in +x
            } else { 
                ImGui::Text("grounded -x");
                std::get<0>(ret) = vec2{-1, 0}; // push in -x
            }
            

            // std::get<0>(ret).normalize_in_place();
            return ret;
        }

        if (theta <= -o_theta && theta >= (-consts_for<double>::pi() + o_theta)) { 
            ImGui::Text("-y");
            std::get<0>(ret) = vec2{0, o_min.y - max.y}; // push in -y
        } else if (theta >= o_theta && theta <= consts_for<double>::pi() - o_theta) {
            ImGui::Text("+y");
            std::get<0>(ret) = vec2{0, o_max.y - min.y}; // push in +y
        } else if (theta <= o_theta && theta >= -o_theta) {
            ImGui::Text("+x");
            std::get<0>(ret) = vec2{o_max.x - min.x, 0};  // push in +x
        } else { 
            ImGui::Text("-x");
            std::get<0>(ret) = vec2{o_min.x - max.x, 0}; // push in -x
        }

        return ret; */
    }
}
