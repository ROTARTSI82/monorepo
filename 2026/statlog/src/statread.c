#include <ctype.h>
#include <dirent.h>
#include <errno.h>
#include <fcntl.h>
#include <limits.h>
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/wait.h>
#include <unistd.h>

// expect defines:
// #define NCPU 16
// #define NDISKS 1
// #define DISKS {"nvme0n1"}
// #define NDEVS 1
// #define DEVS {"enp191s0"}

// disks to include in the count
const char *DISK_ARR[NDISKS] = DISKS;
const char *DEVS_ARR[NDEVS] = DEVS;

#define CHK_ERRNO(msg, expr, chk, ret)                                         \
  if ((expr) == (chk)) {                                                       \
    printf("error on " msg ": %s\n", strerror(errno));                         \
    return (ret);                                                              \
  }

struct iostat_t {
  uint64_t nettx, netrx, diskr, diskw;
};

struct differential_t {
  struct iostat_t io_last;
  long proc_counter;
  long cpu_counters[NCPU + 1][2];
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

enum { ITEM_INT, ITEM_STR };

struct parseline_t {
  int type;
  int idx;
  union {
    long int_val;
    struct {
      char *str;
      size_t len;
    };
  };
};

void parseline(struct parseline_t *spec, int nspec, char *line, ssize_t size) {
  char *lineptr = line;
  struct parseline_t *specptr = spec;
  int idx = 0;
  while (lineptr < line + size && specptr < spec + nspec) {
    while (isspace(*lineptr) && ++lineptr < line + size)
      ;
    if (lineptr >= line + size)
      break;

    if (idx == specptr->idx) {
      if (specptr->type == ITEM_INT) {
        specptr->int_val = strtol(lineptr, &lineptr, 10);
      } else if (specptr->type == ITEM_STR) {
        specptr->str = lineptr;
        while (++lineptr < line + size && !isspace(*lineptr))
          ;
        specptr->len = lineptr - specptr->str;
      }
      specptr++;
    } else {
      while (++lineptr < line + size && !isspace(*lineptr))
        ;
    }
    idx++;
  }
}

int opendirat(DIR *dir, char *pathname) {
  int dfd = dirfd(dir);
  if (dfd == -1)
    return -1;
  return openat(dfd, pathname, O_RDONLY);
}

size_t buf_capacity = 1024;
char *shared_buf = NULL;

long atol_at(DIR *dir, char *pathname) {
  int fd = opendirat(dir, pathname);
  if (fd == -1)
    return -1;

  ssize_t bytes = read(fd, shared_buf, buf_capacity - 1);
  close(fd);
  if (bytes <= 0 || (size_t)bytes >= buf_capacity - 1)
    return -1;
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
    if (subdirfd == -1)
      continue;
    if ((subdir = fdopendir(subdirfd)) == NULL) {
      close(subdirfd);
      continue;
    }

    struct dirent *entry;
    while ((entry = readdir(subdir)) != NULL) {
      if (strncmp(entry->d_name, "temp", 4) == 0 &&
          strstr(entry->d_name, "_input")) {
        // Temperature: temp[N]_input (millidegrees C)
        long val = atol_at(subdir, entry->d_name);
        if (val == -1)
          continue;

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
          *stpncpy(buf, entry->d_name, sizeof(buf) - 1) = '\0';
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
  if (bytes <= 9)
    goto fail;

  stats->mem_total = atol(shared_buf + 9);
  bytes = getline(&shared_buf, &buf_capacity, mem);
  bytes = getline(&shared_buf, &buf_capacity, mem);
  if (bytes <= 13)
    goto fail;

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
  for (int cpu = 0; cpu < NCPU + 1; cpu++) {
    bytes = getline(&shared_buf, &buf_capacity, procstat);
    if (bytes <= 5)
      continue;
    char *lineptr = shared_buf;
    while (++lineptr < shared_buf + bytes && !isspace(*lineptr))
      ;
    long tot = 0, idle = 0;
    for (int i = 0; i < 8; i++) {
      long timer = strtol(lineptr, &lineptr, 10);
      tot += timer;
      if (i == 3 || i == 4)
        idle += timer;
    }

    long dtot = tot - diff->cpu_counters[cpu][0];
    long didle = idle - diff->cpu_counters[cpu][1];
    double utilization = 1 - didle / (double)dtot;
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

int read_diskstat(struct stats_t *stats, struct differential_t *diff) {
  FILE *diskstat = fopen("/proc/diskstats", "r");
  CHK_ERRNO("open /proc/diskstats", diskstat, NULL, -1);

  long read_bytes = 0, write_bytes = 0;
  struct parseline_t linespec[] = {
    { .type = ITEM_STR, .idx = 2 }, // drive name
    { .type = ITEM_INT, .idx = 5 }, // bytes read
    { .type = ITEM_INT, .idx = 9 }  // bytes written
  };

  ssize_t bytes = getline(&shared_buf, &buf_capacity, diskstat);
  while (bytes > 0) {
    shared_buf[buf_capacity - 1] = '\0';
    parseline(linespec, 3, shared_buf, bytes);

    bool found = 0;
    for (int i = 0; i < NDISKS; i++) {
      const char *needle = DISK_ARR[i];
      if (strncmp(linespec[0].str, needle, linespec[0].len) == 0) {
        found |= 1;
        break;
      }
    }

    if (found) {
      read_bytes += linespec[1].int_val * 512;
      write_bytes += linespec[2].int_val * 512;
    }

    bytes = getline(&shared_buf, &buf_capacity, diskstat);
  };

  stats->io_diff.diskr = read_bytes - diff->io_last.diskr;
  stats->io_diff.diskw = write_bytes - diff->io_last.diskw;

  diff->io_last.diskr = read_bytes;
  diff->io_last.diskw = write_bytes;

  fclose(diskstat);
  return 0;
}

int read_netdev(struct stats_t *stats, struct differential_t *diff) {
  FILE *netdev = fopen("/proc/net/dev", "r");
  CHK_ERRNO("open /proc/net/dev", netdev, NULL, -1);

  // skip first line
  ssize_t bytes = getline(&shared_buf, &buf_capacity, netdev);

  struct parseline_t linespec[] = {
    { .type = ITEM_STR, .idx = 0 }, // interface
    { .type = ITEM_INT, .idx = 1 }, // bytes rx
    { .type = ITEM_INT, .idx = 9 }, // bytes tx
  };

  bytes = getline(&shared_buf, &buf_capacity, netdev);
  long tx_bytes = 0, rx_bytes = 0;
  while (bytes > 0) {
    shared_buf[buf_capacity - 1] = '\0';
    parseline(linespec, 3, shared_buf, bytes);

    bool found = 0;
    for (int i = 0; i < NDEVS; i++) {
      const char *needle = DEVS_ARR[i];
      if (strncmp(linespec[0].str, needle, linespec[0].len - 1) == 0) {
        found |= 1;
        break;
      }
    }

    if (found) {
      rx_bytes += linespec[1].int_val;
      tx_bytes += linespec[2].int_val;
    }

    bytes = getline(&shared_buf, &buf_capacity, netdev);
  }

  stats->io_diff.nettx = tx_bytes - diff->io_last.nettx;
  stats->io_diff.netrx = rx_bytes - diff->io_last.netrx;

  diff->io_last.nettx = tx_bytes;
  diff->io_last.netrx = rx_bytes;

  fclose(netdev);
  return 0;
}

int read_stats(struct stats_t *stats, struct differential_t *diff) {
  if (shared_buf == NULL) {
    shared_buf = malloc(buf_capacity);
    if (!shared_buf)
      return -1;
  }

  int hwmon = read_hwmon(stats);
  int meminfo = read_meminfo(stats);
  int procstat = read_procstat(stats, diff);
  int diskstat = read_diskstat(stats, diff);
  int dev = read_netdev(stats, diff);

  return hwmon + meminfo + procstat + diskstat + dev;
}
