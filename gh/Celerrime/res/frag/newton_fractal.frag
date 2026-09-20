#version 400

layout(location = 0) out vec4 diffuse_color;

in vec2 tex_co; // this variable name is now pretty misleading lol.
in float alpha_mult_io;

flat in vec2 sample_extent_io;
flat in vec2 sample_bl_io;

#define MAX_MAGN 4
#define MAX_ITER 1000

const vec2 roots[3] = vec2[](
        vec2(1, 0),
        vec2(-0.5, sqrt(3)/2),
        vec2(-0.5, -sqrt(3)/2)
);

const vec4 colors[3] = vec4[](
    vec4(1, 0, 0, 1),
    vec4(0, 1, 0, 1),
    vec4(0, 0, 1, 1)
);

// const vec2 roots[3] = vec2[](
//         vec2(10, 0),
//         vec2(4, 2),
//         vec2(-4, -2)
// );

vec2 cmult(vec2 a, vec2 b) {
    // (a.x + a.y*i)(b.x + b.y*i)
    // a.x * b.x - a.y * b.y + (a.x * b.y + a.y * b.x)*i
    return vec2(a.x*b.x - a.y*b.y, a.x*b.y+a.y*b.x);
}

vec2 cdiv(vec2 a, vec2 b) {
    return cmult(a, vec2(b.x, -b.y)) / (b.x*b.x + b.y*b.y);
}

// vec2 f(vec2 n) {
//     return cmult(n - vec2(4, 2), cmult(n + vec2(4, 2), n - vec2(10, 0)));
// }

// vec2 fprime(vec2 x) {
//     return cmult((vec2(-10, 0) + x), (vec2(-4, - 2) + x)) + cmult((vec2(-10, 0) + x), (vec2(4, 2) + x)) + cmult((vec2(-4, - 2)+ x), (vec2(4, 2) + x));
// }

//z^3-1 
vec2 f (vec2 z)
{
	return cmult(z, cmult(z, z)) - vec2(1, 0); //cpow is an exponential function for complex numbers
}

//3*z^2
vec2 fprime (vec2 z)
{
	return 3 * cmult(z, z); //cmult is a function that handles multiplication of complex numbers
}


vec2 iterate(vec2 n) {
    return n - cdiv(f(n), fprime(n));
}

void main() {
    diffuse_color = vec4(0, 0, 0, 1);

    vec2 orig = sample_bl_io + mod(tex_co, sample_extent_io);
    vec2 iteration = orig;

    for (int i = 0; i < 1000; i++)
        iteration = iterate(iteration);

    // float r1 = distance(iteration, vec2(1, 0));
    // float r2 = distance(iteration, vec2(-0.5, sqrt(3)/2));
    // float r3 = distance(iteration, vec2(-0.5, -sqrt(3)/2));


    // float r1 = distance(iteration, vec2(10, 0));
    // float r2 = distance(iteration, vec2(4, 2));
    // float r3 = distance(iteration, -vec2(4, 2));


    float m = 999;
    for (int j = 0; j < 3; j++)
    {
        
        //If the current iteration is close enough to a root, color the pixel.
        float d = abs(distance(roots[j], iteration));
        if (d < m)
        {
            diffuse_color = colors[j]; //Return the color corresponding to the root
            m = d;
        }
    }


    diffuse_color.w *= alpha_mult_io;
}
