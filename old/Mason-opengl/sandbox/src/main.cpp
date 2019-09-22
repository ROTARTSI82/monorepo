#include <iostream>
#include "mason/camera.h"

#define camvel cam.transforms.velocity

int main() {
  mason::camera cam(640, 480);
  std::cout << "Hello World!" << std::endl;
  cam.transforms.position += glm::vec3(1.0);
  cam.transforms.update_velocity();
  std::cout << "Vel={" << camvel.x << ", " << camvel.y << "," << camvel.z << "}" << std::endl;

}
