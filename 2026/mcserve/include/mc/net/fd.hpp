#pragma once

#include "mc/async.hpp"
#include <cstdint>

namespace mc {

io_awaiter send(int fd, const uint8_t *buf, ssize_t bytes);

class fd_reader {
public:
  constexpr static auto BUF_SIZE = 2048;
  uint8_t bufs[2 * BUF_SIZE];
  uint8_t *head = bufs;
  uint8_t *end = bufs;
  int fd;

  fd_reader(int fd);
  ~fd_reader();

  io_awaiter recv();
};
} // namespace mc
