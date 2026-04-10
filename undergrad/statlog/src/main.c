#include <stdio.h>
#include <signal.h>
#include <unistd.h>
#include <sys/wait.h>
#include <time.h>

#include "bpf/statlog.skel.h"


int popen2(char *name, char *argname) {
  int pipefd[2];
  if (pipe(pipefd) == -1)
    return -1;

  int pid = fork();
  if (pid == 0) {
    close(pipefd[0]); // read pipe
    if (dup2(pipefd[1], STDOUT_FILENO) == -1)
      _exit(-1);

    close(pipefd[1]);

    char *argv[] = {argname, 0};
    execve(name, argv, NULL);
    _exit(-1);
  } else if (pid > 0) {
    close(pipefd[1]); // write pipe
    
    int status;
    if (waitpid(pid, &status, 0) == -1 || status != 0)
      return -1;

    return pipefd[0];
  }

  close(pipefd[0]);
  close(pipefd[1]);
  return -1;
}

inline void parse_all(__u64 rx, __u64 tx, __u64 rd, __u64 wr) {
}

static volatile bool running = true;

void exit_handler(int) {
  running = false;
}

int main() {
  struct statlog_bpf *bpf;
  bpf = statlog_bpf__open_and_load();
  if (!bpf) {
    printf("bpf did not open: %s\n", strerror(errno));
    return 1;
  }

  int attach = statlog_bpf__attach(bpf);
  if (!attach) {
    printf("attach error: %s\n", strerror(errno));
    goto cleanup;
  }

  __u64 netrx = 0, nettx = 0, diskr = 0, diskw = 0;
  
  {
    struct sigaction config = {{exit_handler}, {0}, 0, NULL};
    if (sigaction(SIGINT, &config, NULL) != 0)
      goto cleanup;
  }

  while (running) {
    sleep(3);

    __u64 n_diskr = __atomic_load_n(&bpf->bss->disk_reads, __ATOMIC_RELAXED);
    __u64 n_diskw = __atomic_load_n(&bpf->bss->disk_writes, __ATOMIC_RELAXED);
    __u64 n_netrx = __atomic_load_n(&bpf->bss->net_rx, __ATOMIC_RELAXED);
    __u64 n_nettx = __atomic_load_n(&bpf->bss->net_tx, __ATOMIC_RELAXED);
   
    struct timespec ts = {0, 0};
    clock_gettime(CLOCK_REALTIME, &ts);

    int fd = popen2("/usr/bin/sensors", "sensors");
    FILE *pwr = fd > 0 ? fdopen(fd, "r") : NULL;
    FILE *mem = fopen("/proc/meminfo", "r");
    FILE *cpu = fopen("/proc/stat", "r");

    if (!pwr || !mem || !cpu)
      goto cont;

    // TODO: parse the files above and log the stats
     printf("\t%llu\t%llu\t%llu\t%llu\n", n_diskr, n_diskw, n_netrx, n_nettx);


  cont:
    diskr = n_diskr; diskw = n_diskw;
    netrx = n_netrx; nettx = n_nettx;

    fclose(mem);
    fclose(cpu);
    fclose(pwr);
  }

cleanup:
  statlog_bpf__destroy(bpf);
  printf("Goodbye!\n");
}
