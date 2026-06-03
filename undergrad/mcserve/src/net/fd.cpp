#include "mc/net/fd.hpp"
#include <cerrno>
#include <iostream>
#include <string.h>
#include <sys/socket.h>

namespace mc {

io_awaiter send(int fd, const uint8_t *buf, ssize_t bytes) {
  ssize_t sent = ::send(fd, buf, bytes, 0);
  if (sent == -1) {
    if (errno != EAGAIN && errno != EWOULDBLOCK)
      std::cout << "err on send: " << strerror(errno) << '\n';
    return io_awaiter{fd, POLLOUT};
  }

  return io_awaiter{sent};
}

fd_reader::fd_reader(int fd) : fd(fd) {}
fd_reader::~fd_reader() {}

io_awaiter fd_reader::recv() {
  bool is_buf2 = head >= bufs + BUF_SIZE;
  uint8_t *dst = is_buf2 ? bufs + BUF_SIZE : bufs;
  ssize_t bytes = ::recv(fd, dst, BUF_SIZE, 0);
  if (bytes == -1) {
    if (errno != EAGAIN && errno != EWOULDBLOCK)
      std::cout << "err on recv: " << strerror(errno) << '\n';
    return io_awaiter{fd, POLLIN};
  }

  head = dst;
  end = dst + bytes;
  return io_awaiter{bytes};
}
} // namespace mc
