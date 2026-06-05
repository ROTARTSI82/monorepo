#define _GNU_SOURCE
#include <bpf/libbpf.h>
#include <signal.h>
#include <sys/timerfd.h>
#include <time.h>

#include "statread.c"

static volatile sig_atomic_t running = 1;

void exit_handler(int) { running = 0; }

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
      printf(
          "statlog 2026.05.31 - a simple utility to log hardware usage over "
          "time\n"
          "Author: Grant Yang - rotartsi0482@gmail.com\n"
          "\t-c, --csv\t\t\toutput as a csv instead of a human-readable table\n"
          "\t-i, --interval=INT\t\tset the repeat interval in milliseconds\n"
          "\t-n, --no-header\t\t\tdo not print the csv header\n"
          "\t-h, --help\t\t\tprint this help message\n");
      return !running;
    } else if (strcmp(*arg, "--interval") == 0 || strcmp(*arg, "-i") == 0) {
      if (i++ >= argc - 1)
        goto unrecog;
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

  if (csvmode && !noheader)
    printf("time,cpu,proc,mem,rpm,pwr,mintemp,maxtemp,tx,rx,diskr,diskw\n");

  {
    struct sigaction config = {{exit_handler}, {0}, 0, NULL};
    if (sigaction(SIGINT, &config, NULL) != 0)
      return 1;
  }

  struct differential_t integral = {0};
  struct stats_t stats = {0};

  // throw away first read to initialize counters
  // to show diffs
  read_stats(&stats, &integral);

  int timerfd = timerfd_create(CLOCK_MONOTONIC, 0);
  {
    struct itimerspec utmr = {
        .it_interval = {.tv_sec = poll_interval_ms / 1000,
                        .tv_nsec = (poll_interval_ms % 1000) * 1000000},
        .it_value = {.tv_sec = 0, .tv_nsec = 10 * 1000000}}; // wait 10ms
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

    double mem_frac = 1 - stats.mem_avail / (double)stats.mem_total;

    printf(csvmode ? "%.3f," : "\tt %.3f", unix_time);
    printf(csvmode ? "%.2f," : "\tcpu %.2f", stats.cpu_tot_util);
    printf(csvmode ? "%ld," : "\tdproc %ld", stats.newproc_diff);
    printf(csvmode ? "%.3f," : "\tmem %.3f\n", mem_frac);
    printf(csvmode ? "%d,%.2f,%.2f,%.2f,"
                   : "\trpm %d\twatts %.2f\ttemps %.2f %.2f\n",
           stats.fan_rpm, stats.wattage, stats.min_temp, stats.max_temp);

    printf(csvmode ? "%lu,%lu" : "\tr %lu w %lu\n", stats.io_diff.diskr,
           stats.io_diff.diskw);

  cont:
    // exit if printing fails i guess.
    if (printf("\n") < 0 || fflush(stdout) < 0)
      break;
  }

  close(timerfd);
  printf("goodbye!\n");
}
