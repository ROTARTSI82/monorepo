#version 400

layout(location = 0) out vec4 diffuse_color;

in vec2 tex_co; // this variable name is now pretty misleading lol.
in float alpha_mult_io;

flat in vec2 sample_extent_io;
flat in vec2 sample_bl_io;

#define MAX_MAGN 4
#define MAX_ITER 1000

void main() {
    vec2 orig = sample_bl_io + mod(tex_co, sample_extent_io);
    vec2 current = vec2(0, 0);
    vec2 tmp = vec2(0, 0);
    uint iter = 0;

    while (current.x + current.y < MAX_MAGN && iter < MAX_ITER) {
        // float tmp = current.x * current.x - current.y * current.y + orig.x;
        // current.y = 2 * current.x * current.y + orig.y;
        // current.x = tmp;
        
        current = vec2(current.x * current.x - current.y * current.y, 2 * current.x * current.y) + orig;
        iter++;
    }

    // Yoinked from https://github.com/Erkaman/vulkan_minimal_compute/blob/e82aa8e7ccf603c42c81e8bd422808e6522a50a8/shaders/shader.comp#L45
    // we use a simple cosine palette to determine color:
    // http://iquilezles.org/www/articles/palettes/palettes.htm    
    float t = float(iter) / float(MAX_ITER);
    vec3 d = vec3(0.3, 0.3 ,0.5);
    vec3 e = vec3(-0.2, -0.3 ,-0.5);
    vec3 f = vec3(2.1, 2.0, 3.0);
    vec3 g = vec3(0.0, 0.1, 0.0);

    diffuse_color = vec4(d + e * cos(6.28318 * (f * t + g)), 1.0);

    diffuse_color.w *= alpha_mult_io;
}
