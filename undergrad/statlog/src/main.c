#define _GNU_SOURCE
#include <signal.h>
#include <time.h>
#include <bpf/libbpf.h>
#include <sys/timerfd.h>

#include "statread.c"


static volatile sig_atomic_t running = 1;

void exit_handler(int) {
  running = 0;
}

static __u64 sum_percpu_map(struct bpf_map *map) {
  int ncpus = libbpf_num_possible_cpus();
  __u64 values[ncpus];
  __u32 key = 0;
  __u64 sum = 0;

  if (bpf_map__lookup_elem(map, &key, sizeof(key), values, sizeof(__u64) * ncpus, 0) < 0) {
    return 0;
  }

  for (int i = 0; i < ncpus; i++) {
    sum += values[i];
  }
  return sum;
}

void bpf_runner(int infd, int outfd) {
  struct statlog_bpf *bpf = statlog_bpf__open_and_load();
  if (!bpf) {
    perror("bpf open and load error");
    close(infd);
    close(outfd);
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
      sum_percpu_map(bpf->maps.net_tx),
      sum_percpu_map(bpf->maps.net_rx),
      sum_percpu_map(bpf->maps.disk_reads),
      sum_percpu_map(bpf->maps.disk_writes)
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
  if (pipe2(bpftrig, O_DIRECT) == -1) {
    close(bpfout[0]); close(bpfout[1]);
    return -1;
  }
    
  int pid = fork();
  if (pid == 0) {
    close(bpftrig[1]);
    close(bpfout[0]);
    bpf_runner(bpftrig[0], bpfout[1]);
    return -2;
  } else if (pid < 0){
    close(bpfout[0]); close(bpfout[1]);
    close(bpftrig[0]); close(bpftrig[1]);
    return -1;
  }

  close(bpftrig[0]);
  close(bpfout[1]);

  const char *sudo_uid_str = getenv("SUDO_UID");
  const char *sudo_gid_str = getenv("SUDO_GID");
  if (!sudo_uid_str || !sudo_gid_str) {
    fprintf(stderr, "not invoked with sudo\n");
    return pid;
  }

  if (setgid(atoi(sudo_gid_str)) == -1 || setuid(atoi(sudo_uid_str)) == -1 ||
      getuid() == 0 || setuid(0) != -1) {
    perror("failed to drop root privileges\n");
    close(bpftrig[1]);
    close(bpfout[0]);
    bpftrig[1] = bpfout[0] = -1;
    return -1;
  }
  return pid;
}

int main(int argc, char **argv) {
  signal(SIGPIPE, SIG_IGN);

  bool csvmode = false, noheader = false;
  long poll_interval_ms = 1000;
  char **arg = argv + 1;
  for (int i = 1; i < argc; i++) {
    if (strcmp(*arg, "--csv") == 0 || strcmp(*arg, "-c") == 0)
      csvmode |= true;
    else if (strcmp(*arg, "--no-header") == 0 || strcmp(*arg, "-n") == 0)
      noheader = true;
    else if (strstr(*arg, "help") || strcmp(*arg, "-h") == 0) {
    help:
      printf("statlog 2026.05.31 - a simple utility to log hardware usage over time\n"
             "Author: Grant Yang - rotartsi0482@gmail.com\n"
             "\t-c, --csv\t\t\toutput as a csv instead of a human-readable table\n"
             "\t-i, --interval=INT\t\tset the repeat interval in milliseconds\n"
             "\t-n, --no-header\t\t\tdo not print the csv header\n"
             "\t-h, --help\t\t\tprint this help message\n");
      return !running;
    } else if (strcmp(*arg, "--interval") == 0 || strcmp(*arg, "-i") == 0) {
      if (i++ >= argc - 1) goto unrecog;
      poll_interval_ms = atol(*++arg);
    } else {
    unrecog:
      printf("unrecognized or malformed option %s\n\n", *arg);
      running = 0;
      goto help;
    }
    arg++;
    continue;
  }

  int bpfout[2] = {-1, -1};
  int bpftrig[2] = {-1, -1};
  int pid = getuid() == 0 ? try_start_bpf(bpfout, bpftrig) : -1;
  if (pid == -2)
    return 0;
  
  if (csvmode && !noheader)
    printf("time,cpu,proc,mem,rpm,pwr,mintemp,maxtemp,tx,rx,diskr,diskw\n");

  {
    struct sigaction config = {{exit_handler}, {0}, 0, NULL};
    if (sigaction(SIGINT, &config, NULL) != 0)
      return 1;
  }

  struct differential_t integral = {0};
  struct stats_t stats = {0};
  
  int timerfd = timerfd_create(CLOCK_MONOTONIC, 0);
  {
    struct itimerspec utmr = {
      .it_interval = {
        .tv_sec = poll_interval_ms / 1000,
        .tv_nsec = (poll_interval_ms % 1000) * 1000000},
      .it_value = {.tv_sec = 0, .tv_nsec = 1}
    };
    if (timerfd == -1 || timerfd_settime(timerfd, 0, &utmr, NULL) == -1) {
      printf("cannot set timer: %s\n", strerror(errno));
      running = false;
    }
  }
  
  while (running) {
    struct timespec ts;
    // delay, reuse ts as a buffer (nothing should be written)
    read(timerfd, &ts, sizeof(ts));
    clock_gettime(CLOCK_REALTIME, &ts);
    double unix_time = ts.tv_sec + ts.tv_nsec / 1e9;

    int result = read_stats(&stats, &integral);
    if (result != 0) {
      printf("err: %s", strerror(errno));
      goto cont;
    }
    
    double mem_frac = 1 - stats.mem_avail / (double) stats.mem_total;

    printf(csvmode ? "%.3f," : "\tt %.3f", unix_time);
    printf(csvmode ? "%.2f," : "\tcpu %.2f", stats.cpu_tot_util);
    printf(csvmode ? "%ld," : "\tdproc %ld", stats.newproc_diff);
    printf(csvmode ? "%.3f," : "\tmem %.3f\n", mem_frac);
    printf(csvmode ? "%d,%.2f,%.2f,%.2f," : "\trpm %d\twatts %.2f\ttemps %.2f %.2f\n",
           stats.fan_rpm, stats.wattage, stats.min_temp, stats.max_temp);
    
    // ipc with the root bpf process
    if (bpftrig[1] != -1) {
      char w = 1;
      struct iostat_t new = {0};
      if (write(bpftrig[1], &w, 1) != 1 ||
          read(bpfout[0], &new, sizeof(new)) != sizeof(new))
        goto cont;

      stats.io_diff.nettx = new.nettx - integral.io_last.nettx;
      stats.io_diff.netrx = new.netrx - integral.io_last.netrx;
      stats.io_diff.diskr = new.diskr - integral.io_last.diskr;
      stats.io_diff.diskw = new.diskw - integral.io_last.diskw;
    
      printf(csvmode ? "%llu,%llu,%llu,%llu" : "\ttx %llu\trx %llu\tr %llu\tw %llu\n",
             stats.io_diff.nettx, stats.io_diff.netrx, stats.io_diff.diskr, stats.io_diff.diskw);

      integral.io_last = new;
    }

  cont:
    // exit if printing fails i guess.
    if (printf("\n") < 0 || fflush(stdout) < 0) break;
  }

  close(timerfd);
  printf("goodbye!\n");
  if (pid > 0) {
    int status = 0;
    write(bpftrig[1], &status, 1);
    close(bpftrig[1]);
    close(bpfout[0]);
    waitpid(pid, &status, 0);
  }
}
