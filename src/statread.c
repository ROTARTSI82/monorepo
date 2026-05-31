#include <ctype.h>
#include <stdint.h>
#include <dirent.h>
#include <errno.h>
#include <limits.h>
#include <stdio.h>
#include <fcntl.h>
#include <stdlib.h>
#include <string.h>
#include <sys/wait.h>
#include <unistd.h>

#include "bpf/statlog.skel.h"

#define CHK_ERRNO(msg, expr, chk, ret) if ((expr) == (chk)) { \
  printf("error on " msg ": %s\n", strerror(errno)); \
  return (ret); \
}


struct iostat_t {
  __u64 nettx, netrx, diskr, diskw;
};

struct differential_t {
  struct iostat_t io_last;
  long proc_counter;
  long cpu_counters[17][2];
};

struct stats_t {
  int fan_rpm;
  double wattage;
  double min_temp;
  double max_temp;

  long mem_avail;
  long mem_total;

  // units: 1 = 100% of 1 core. full multithreaded pin on 16 cores = 15.99
  double cpu_tot_util;

  // differential stats (compared to prev)
  long newproc_diff;
  struct iostat_t io_diff;
};

int opendirat(DIR *dir, char *pathname) {
  int dfd = dirfd(dir);
  if (dfd == -1) return -1;
  return openat(dfd, pathname, O_RDONLY);
}

size_t buf_capacity = 1024;
char *shared_buf = NULL;

long atol_at(DIR *dir, char *pathname) {
  int fd = opendirat(dir, pathname);
  if (fd == -1) return -1;

  ssize_t bytes = read(fd, shared_buf, buf_capacity);
  if (bytes <= 0 || (size_t) bytes >= buf_capacity) return -1;
  shared_buf[bytes] = '\0';
  return atol(shared_buf);
}


int read_hwmon(struct stats_t *stats) {
  stats->fan_rpm = 0;
  stats->wattage = 0.0;
  stats->min_temp = 200.0;
  stats->max_temp = -100.0;

  // int dfd = open("/sys/class/hwmon", O_RDONLY | O_DIRECTORY);
  DIR *hwmon = opendir("/sys/class/hwmon");
  CHK_ERRNO("open /sys/class/hwmon", hwmon, NULL, -1);

  struct dirent *dir;
  while ((dir = readdir(hwmon)) != NULL) {
    if (dir->d_name[0] == '.')
      continue;

    DIR *subdir;
    int subdirfd = opendirat(hwmon, dir->d_name);
    if (subdirfd == -1 || (subdir = fdopendir(subdirfd)) == NULL)
      continue;

    struct dirent *entry;
    while ((entry = readdir(subdir)) != NULL) {
      if (strncmp(entry->d_name, "temp", 4) == 0 &&
          strstr(entry->d_name, "_input")) {
        // Temperature: temp[N]_input (millidegrees C)
        long val = atol_at(subdir, entry->d_name);
        if (val == -1) continue;

        double temp = val / 1000.0;
        if (temp < stats->min_temp)
          stats->min_temp = temp;
        if (temp > stats->max_temp)
          stats->max_temp = temp;

      } else if (strncmp(entry->d_name, "fan", 3) == 0 &&
                 strstr(entry->d_name, "_input")) {
        // Fans: fan[N]_input (RPM)
        long val = atol_at(subdir, entry->d_name);
        if (val > 0)
          stats->fan_rpm += (int)val;
      } else if (strncmp(entry->d_name, "power", 5) == 0 &&
                 (strstr(entry->d_name, "_average") ||
                  strstr(entry->d_name, "_input"))) {
        // Prefer _average over _input for the same sensor index
        // to avoid double counting
        if (strstr(entry->d_name, "_input")) {
          char buf[PATH_MAX];
          strncpy(buf, entry->d_name, sizeof(buf));
          char *suffix = strstr(buf, "_input");
          if (suffix == NULL || suffix - buf >= PATH_MAX - 16) {
            printf("err: substr failed or buf too small\n");
            continue;
          }
          strcpy(suffix, "_average"); // overwrite _input with _average
          if (faccessat(subdirfd, buf, F_OK, 0) == 0)
            continue;
        }

        long val = atol_at(subdir, entry->d_name);
        if (val > 0)
          stats->wattage += val / 1000000.0;
      }
    }
    closedir(subdir);
  }
  closedir(hwmon);

  if (stats->min_temp == 200.0)
    stats->min_temp = 0.0;
  if (stats->max_temp == -100.0)
    stats->max_temp = 0.0;
  return 0;
}

int read_meminfo(struct stats_t *stats) {
  FILE *mem = fopen("/proc/meminfo", "r");
  CHK_ERRNO("open /proc/meminfo", mem, NULL, -1);
  
  ssize_t bytes = getline(&shared_buf, &buf_capacity, mem);
  if (bytes <= 9) goto fail;

  stats->mem_total = atol(shared_buf + 9);
  bytes = getline(&shared_buf, &buf_capacity, mem);
  bytes = getline(&shared_buf, &buf_capacity, mem);
  if (bytes <= 13) goto fail;
  
  stats->mem_avail = atol(shared_buf + 13);
  
  fclose(mem);
  return 0;

fail:
  if (mem)
    fclose(mem);
  return -1;
}

int read_procstat(struct stats_t *stats, struct differential_t *diff) {
  FILE *procstat = fopen("/proc/stat", "r");
  CHK_ERRNO("open /proc/stat", procstat, NULL, -1);
  
  stats->cpu_tot_util = 0;
  ssize_t bytes = 0;
  for (int cpu = 0; cpu < 17; cpu++) {
    bytes = getline(&shared_buf, &buf_capacity, procstat);
    if (bytes <= 5)
      continue;
    char *lineptr = shared_buf;
    while (++lineptr < shared_buf + bytes && !isspace(*lineptr));
    long tot = 0, idle = 0;
    for (int i = 0; i < 8; i++) {
      long timer = strtol(lineptr, &lineptr, 10);
      tot += timer;
      if (i == 3 || i == 4)
        idle += timer;
    }

    long dtot = tot - diff->cpu_counters[cpu][0];
    long didle = idle - diff->cpu_counters[cpu][1];
    double utilization = 1 - didle / (double) dtot;
    if (cpu > 0)
      stats->cpu_tot_util += utilization;
    diff->cpu_counters[cpu][0] = tot;
    diff->cpu_counters[cpu][1] = idle;
  }
  
  do {
    bytes = getline(&shared_buf, &buf_capacity, procstat);
    if (bytes <= 10)
      continue;
    if (strncmp(shared_buf, "processes", 9) == 0) {
      long newproc = atol(shared_buf + 9);
      stats->newproc_diff = newproc - diff->proc_counter;
      diff->proc_counter = newproc;
      break;
    }
  } while (bytes > 0);

  fclose(procstat);
  return 0;
}

int read_stats(struct stats_t *stats, struct differential_t *diff) {
  if (shared_buf == NULL)
    shared_buf = malloc(buf_capacity);

  int hwmon = read_hwmon(stats);
  int meminfo = read_meminfo(stats);
  int procstat = read_procstat(stats, diff);

  return hwmon * meminfo * procstat;
}

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

