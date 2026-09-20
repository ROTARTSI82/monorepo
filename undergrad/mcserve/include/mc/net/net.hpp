#pragma once

#include "mc/async.hpp"

namespace mc {

class tcp_server {
public:
  thread_pool *pool;
  int sock = -1;
  bool nodelay;

  tcp_server() = delete;
  tcp_server(const tcp_server &) = delete;
  tcp_server &operator=(const tcp_server &) = delete;

  tcp_server(const char *addr, const char *port, thread_pool *pool,
             bool nodelay = false);
  ~tcp_server();

  static pool_future<void> accept_loop(tcp_server *);

  virtual pool_future<void> on_connect(int client_fd) = 0;
};

class simple_http_echo : public tcp_server {
public:
  using tcp_server::tcp_server;
  pool_future<void> on_connect(int) override;
};

} // namespace mc
