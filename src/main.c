#define _GNU_SOURCE
#include <stdio.h>
#include <signal.h>
#include <unistd.h>
#include <fcntl.h>
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

static volatile bool running = true;

void exit_handler(int) {
  running = false;
}

struct iostat_t {
  __u64 nettx, netrx, diskr, diskw;
};

void bpf_runner(int infd, int outfd) {
  struct statlog_bpf *bpf = statlog_bpf__open_and_load();
  if (!bpf) {
    perror("bpf open and load error");
    return;
  }

  if (statlog_bpf__attach(bpf)) {
    perror("bpf attach error");
    goto cleanup;
  }

  
  for (;;) {
    char buf[2] = {0, 0};
    ssize_t bytes = read(infd, buf, 1);
    if (bytes == 0 || buf[0] == 0)
      break;

    struct iostat_t new = {
      __atomic_load_n(&bpf->bss->net_tx, __ATOMIC_RELAXED),
      __atomic_load_n(&bpf->bss->net_rx, __ATOMIC_RELAXED),
      __atomic_load_n(&bpf->bss->disk_reads, __ATOMIC_RELAXED),
      __atomic_load_n(&bpf->bss->disk_writes, __ATOMIC_RELAXED)
    };

    write(outfd, &new, sizeof(new));
  }

cleanup:
  close(infd);
  close(outfd);
  statlog_bpf__destroy(bpf);
}

int main() {
  int bpfout[2];
  int bpftrig[2];
  if (pipe2(bpfout, O_DIRECT) == -1)
    return 1;
  if (pipe2(bpftrig, O_DIRECT) == -1)
    return 1;
  
  
  int pid = fork();
  if (pid == 0) {
    close(bpftrig[1]);
    close(bpfout[0]);
    bpf_runner(bpftrig[0], bpfout[1]);
    return 0;
  } else if (pid < 0){
    return 1;
  }

  close(bpftrig[0]);
  close(bpfout[1]);

  {
    struct sigaction config = {{exit_handler}, {0}, 0, NULL};
    if (sigaction(SIGINT, &config, NULL) != 0)
      return 1;
  }

  struct iostat_t cumulative = {0, 0, 0, 0};
  while (running) {
    sleep(3);
   
    struct timespec ts = {0, 0};
    clock_gettime(CLOCK_REALTIME, &ts);

    int fd = popen2("/usr/bin/sensors", "sensors");
    FILE *pwr = fd > 0 ? fdopen(fd, "r") : NULL;
    FILE *mem = fopen("/proc/meminfo", "r");
    FILE *cpu = fopen("/proc/stat", "r");

    struct iostat_t new;

    if (!pwr || !mem || !cpu)
      goto cont;

    char w = 1;
    if (write(bpftrig[1], &w, 1) != 1 ||
        read(bpfout[0], &new, sizeof(new)) != sizeof(new))
      goto cont;

    struct iostat_t diff = {
      new.nettx - cumulative.nettx,
      new.netrx - cumulative.netrx,
      new.diskr - cumulative.diskr,
      new.diskw - cumulative.diskw
    };
    
    // TODO: parse the files above and log the stats
    printf("\t%llu\t%llu\t%llu\t%llu\n", new.nettx, new.netrx, new.diskr, new.diskw);

    
    cumulative = new;

  cont:
    if (mem)
      fclose(mem);
    if (cpu)
      fclose(cpu);
    if (pwr)
      fclose(pwr);
    else if (fd > 0)
      close(fd);
  }

  int status = 0;
  write(bpftrig[1], &status, 1);
  close(bpftrig[1]);
  close(bpfout[0]);
  waitpid(pid, &status, 0);
}
