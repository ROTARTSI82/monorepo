#define _GNU_SOURCE
#include <ctype.h>
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
    if (bytes <= 0 || buf[0] == 0)
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

int try_start_bpf(int bpfout[2], int bpftrig[2]) {
  
  if (pipe2(bpfout, O_DIRECT) == -1)
    return -1;
  if (pipe2(bpftrig, O_DIRECT) == -1)
    return -1;
    
  int pid = fork();
  if (pid == 0) {
    close(bpftrig[1]);
    close(bpfout[0]);
    bpf_runner(bpftrig[0], bpfout[1]);
    return -2;
  } else if (pid < 0){
    return -1;
  }

  close(bpftrig[0]);
  close(bpfout[1]);

  const char *sudo_uid_str = getenv("SUDO_UID");
  const char *sudo_gid_str = getenv("SUDO_GID");
  if (!sudo_uid_str || !sudo_gid_str) {
    perror("not invoked with sudo");
    return pid;
  }

  if (setgid(atoi(sudo_gid_str)) == -1 || setuid(atoi(sudo_uid_str)) == -1 ||
      getuid() == 0 || setuid(0) != -1) {
    perror("failed to drop root privileges\n");
    return -1;
  }
  return pid;
}

int main() {
  int bpfout[2] = {-1, -1};
  int bpftrig[2] = {-1, -1};
  int pid = getuid() == 0 ? try_start_bpf(bpfout, bpftrig) : -1;
  if (pid == -2)
    return 0;
  
  printf("uid %d gid %d\n", getuid(), getgid());

  {
    struct sigaction config = {{exit_handler}, {0}, 0, NULL};
    if (sigaction(SIGINT, &config, NULL) != 0)
      return 1;
  }

  struct iostat_t cumulative = {0, 0, 0, 0};

  // hardcoded 16 cores + initial cpu line for my system
  long cpu_counters[17][2] = {0};
  long proc_counter = 0;

  // initial getline() allocation.
  size_t capacity = 1024;
  char *line = malloc(capacity); 

  while (running) {
    sleep(3);
   
    struct timespec ts = {0, 0};
    clock_gettime(CLOCK_REALTIME, &ts);
    double unix_time = ts.tv_sec + ts.tv_nsec / 1e9;
    printf("\tt %f", unix_time);

    int fd = popen2("/usr/bin/sensors", "sensors");
    FILE *pwr = fd > 0 ? fdopen(fd, "r") : NULL;
    FILE *mem = fopen("/proc/meminfo", "r");
    FILE *stats = fopen("/proc/stat", "r");

    struct iostat_t new;

    if (!pwr || !mem || !stats)
      goto cont;

    ssize_t bytes;

    // parse /proc/stat
    double tot_util = 0;
    for (int cpu = 0; cpu < 17; cpu++) {
      bytes = getline(&line, &capacity, stats);
      if (bytes <= 5)
        continue;
      char *lineptr = line;
      while (++lineptr < line + bytes && !isspace(*lineptr));
      long tot = 0, idle = 0;
      for (int i = 0; i < 8; i++) {
        long timer = strtol(lineptr, &lineptr, 10);
        tot += timer;
        if (i == 3 || i == 4)
          idle += timer;
      }

      long dtot = tot - cpu_counters[cpu][0];
      long didle = idle - cpu_counters[cpu][1];
      double utilization = 1 - didle / (double) dtot;
      if (cpu > 0)
        tot_util += utilization;
      cpu_counters[cpu][0] = tot;
      cpu_counters[cpu][1] = idle;
    }
    printf("\tcpu %f", tot_util);

    do {
      bytes = getline(&line, &capacity, stats);
      if (bytes <= 10)
        continue;
      if (strncmp(line, "processes", 9) == 0) {
        long newproc = atol(line + 9);
        printf("\tdproc %ld", newproc - proc_counter);
        proc_counter = newproc;
        break;
      }
    } while (bytes > 0);

    // parse /proc/meminfo
    bytes = getline(&line, &capacity, mem);
    if (bytes <= 9)
      goto cont; 
    long mem_total = atol(line + 9);
    bytes = getline(&line, &capacity, mem);
    bytes = getline(&line, &capacity, mem);
    if (bytes <= 13)
      goto cont;
    long mem_avail = atol(line + 13);
    double mem_frac = 1 - mem_avail / (double) mem_total;
    printf("\tmem %f\n", mem_frac);

    // parse sensors output
    bytes = getline(&line, &capacity, pwr);
    bytes = getline(&line, &capacity, pwr);
    bytes = getline(&line, &capacity, pwr);
    if (bytes <= 5)
      goto cont;
    int fanrpm = atoi(line + 5);
    double wattage = 0;
    double maxtmp = -100;
    double mintmp = 200;
    do {
      bytes = getline(&line, &capacity, pwr);
      if (bytes > 4 && strncmp("PPT:", line, 4) == 0)
        wattage = atof(line + 4);
      else {
        char *lineptr = line;
        while (++lineptr < line + bytes && !isspace(*lineptr));
        if (lineptr < line + bytes) {
          double measured = atof(lineptr);
          if (measured != 0 && measured > maxtmp)
            maxtmp = measured;
          if (measured != 0 && measured < mintmp)
            mintmp = measured;
        }
      }
    } while (bytes > 0);
    printf("\trpm %d\twatts %f\ttemps %f %f\n", fanrpm, wattage, mintmp, maxtmp);
    
    // ipc with the root bpf process
    if (bpftrig[1] != -1) {
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
      printf("\ttx %llu\trx %llu\tr %llu\tw %llu\n", diff.nettx, diff.netrx, diff.diskr, diff.diskw);

      cumulative = new;
    }

  cont:
    printf("\n");
    if (mem)
      fclose(mem);
    if (stats)
      fclose(stats);
    if (pwr)
      fclose(pwr);
    else if (fd > 0)
      close(fd);
  }

  if (pid > 0) {
    int status = 0;
    write(bpftrig[1], &status, 1);
    close(bpftrig[1]);
    close(bpfout[0]);
    waitpid(pid, &status, 0);
  }
}
