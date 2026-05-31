#define _GNU_SOURCE
#include <signal.h>
#include <time.h>

#include "statread.c"


static volatile bool running = true;

void exit_handler(int) {
  running = false;
}

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

  struct differential_t integral = {0};
  struct stats_t stats = {0};
  
  while (running) {
    sleep(3);
   
    struct timespec ts = {0, 0};
    clock_gettime(CLOCK_REALTIME, &ts);
    double unix_time = ts.tv_sec + ts.tv_nsec / 1e9;
    printf("\tt %f", unix_time);


    int result = read_stats(&stats, &integral);
    if (result != 0) {
      printf("err: %s", strerror(errno));
      goto cont;
    }
    

    // parse /proc/stat
    printf("\tcpu %f", stats.cpu_tot_util);

    printf("\tdproc %ld", stats.newproc_diff);

    double mem_frac = 1 - stats.mem_avail / (double) stats.mem_total;
    printf("\tmem %f\n", mem_frac);

    // parse sensors output
    printf("\trpm %d\twatts %f\ttemps %f %f\n",
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
    
      // TODO: parse the files above and log the stats
      printf("\ttx %llu\trx %llu\tr %llu\tw %llu\n", stats.io_diff.nettx,
             stats.io_diff.netrx, stats.io_diff.diskr, stats.io_diff.diskw);

      integral.io_last = new;
    }

  cont:
    printf("\n");
  }

  printf("goodbye!\n");
  if (pid > 0) {
    int status = 0;
    write(bpftrig[1], &status, 1);
    close(bpftrig[1]);
    close(bpfout[0]);
    waitpid(pid, &status, 0);
  }
}
