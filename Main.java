import java.io.*;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

// ============================================================
// Operating Systems Project 2 — Spring 2025
// Thread-Based Process Simulation & Dining Philosophers
//
// How to compile:  javac Main.java
// How to run:      java Main
// Requires:        processes.txt in the same directory
// ============================================================


// ------------------------------------------------------------
// ProcessThread
// Think of this like a worker at a store.
// Each worker has a job number (pid) and a
// certain number of hours to work (burstTime).
// They all start at the same time and work independently.
// ------------------------------------------------------------
class ProcessThread extends Thread {
    int pid;        // unique process ID
    int burstTime;  // how long (in seconds) this process runs

    public ProcessThread(int pid, int burstTime) {
        this.pid = pid;
        this.burstTime = burstTime;
    }

    @Override
    public void run() {
        System.out.println("[Process " + pid + "] Started.  Burst time = " + burstTime + "s");
        try {
            // Simulate the CPU burst by sleeping
            Thread.sleep(burstTime * 1000L);
        } catch (InterruptedException e) {
            System.out.println("[Process " + pid + "] Was interrupted unexpectedly!");
        }
        System.out.println("[Process " + pid + "] Finished.");
    }
}


// ------------------------------------------------------------
// Philosopher — Dining Philosophers Problem
//
// Picture 5 people sitting around a round table.
// Between each pair of neighbors there is exactly ONE fork.
// To eat, a philosopher needs BOTH forks on either side.
// If everyone grabs the left fork at the same time, no one
// can grab a right fork → deadlock (everyone waits forever).
//
// Deadlock fix: odd-numbered philosophers grab right fork
// first; even-numbered grab left fork first.
// This breaks the circular-wait condition.
// ------------------------------------------------------------
class Philosopher extends Thread {
    int id;
    ReentrantLock leftFork;   // the fork to the philosopher's left
    ReentrantLock rightFork;  // the fork to the philosopher's right

    public Philosopher(int id, ReentrantLock leftFork, ReentrantLock rightFork) {
        this.id        = id;
        this.leftFork  = leftFork;
        this.rightFork = rightFork;
    }

    @Override
    public void run() {
        try {
            // Each philosopher goes through 3 full think→eat cycles
            for (int round = 1; round <= 3; round++) {

                // ── THINKING ──────────────────────────────────────
                System.out.println("[Philosopher " + id + "] Thinking... (round " + round + ")");
                Thread.sleep((long)(Math.random() * 800 + 200)); // 200–1000 ms

                // ── WAITING FOR FORKS ─────────────────────────────
                System.out.println("[Philosopher " + id + "] Hungry — waiting for forks...");

                // Decide which fork to pick up FIRST to prevent deadlock:
                // even IDs → grab left first; odd IDs → grab right first
                ReentrantLock firstFork  = (id % 2 == 0) ? leftFork  : rightFork;
                ReentrantLock secondFork = (id % 2 == 0) ? rightFork : leftFork;

                // Acquire first fork (blocks until available)
                firstFork.lock();
                System.out.println("[Philosopher " + id + "] Picked up fork #1.");

                // Acquire second fork (blocks until available)
                secondFork.lock();
                System.out.println("[Philosopher " + id + "] Picked up fork #2. Now eating...");

                // ── EATING ────────────────────────────────────────
                System.out.println("[Philosopher " + id + "] Eating... (round " + round + ")");
                Thread.sleep((long)(Math.random() * 800 + 200)); // 200–1000 ms

                // ── RELEASE FORKS ────────────────────────────────
                firstFork.unlock();
                secondFork.unlock();
                System.out.println("[Philosopher " + id + "] Released both forks.");
            }

            System.out.println("[Philosopher " + id + "] Finished eating for the night. Going home!");

        } catch (InterruptedException e) {
            System.out.println("[Philosopher " + id + "] Interrupted!");
        }
    }
}


// ------------------------------------------------------------
// Main
// Entry point. Runs two phases:
//   Phase 1 — Read processes.txt and simulate each process
//              as a thread (they all run in parallel).
//   Phase 2 — Run the Dining Philosophers with deadlock
//              prevention via ordered lock acquisition.
// ------------------------------------------------------------
public class Main {

    public static void main(String[] args) throws Exception {

        // ════════════════════════════════════════════════════
        // PHASE 1: Process Thread Simulation
        // ════════════════════════════════════════════════════
        System.out.println("\n╔══════════════════════════════════════════╗");
        System.out.println(  "║  PHASE 1: Process Thread Simulation      ║");
        System.out.println(  "╚══════════════════════════════════════════╝");

        List<ProcessThread> processThreads = new ArrayList<>();

        // Load processes from file
        // Expected format per line:  <pid>  <burstTime>
        // Lines starting with '#' are treated as comments.
        try (BufferedReader reader = new BufferedReader(new FileReader("processes.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] parts = line.split("\\s+");
                int pid       = Integer.parseInt(parts[0]);
                int burstTime = Integer.parseInt(parts[1]);
                processThreads.add(new ProcessThread(pid, burstTime));
            }
        } catch (FileNotFoundException e) {
            System.err.println("ERROR: processes.txt not found. Please create it next to Main.java.");
            System.exit(1);
        }

        System.out.println("Loaded " + processThreads.size() + " process(es). Starting threads...\n");

        // Start ALL process threads (they run concurrently)
        for (ProcessThread pt : processThreads) pt.start();

        // Wait for ALL process threads to complete before continuing
        for (ProcessThread pt : processThreads) pt.join();

        System.out.println("\n✔ All processes completed.\n");


        // ════════════════════════════════════════════════════
        // PHASE 2: Dining Philosophers
        // ════════════════════════════════════════════════════
        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println(  "║  PHASE 2: Dining Philosophers            ║");
        System.out.println(  "╚══════════════════════════════════════════╝");

        final int NUM = 5; // number of philosophers (and forks)

        // Create one lock per fork — forks[i] sits between
        // philosopher i (left) and philosopher (i+1) % NUM (right)
        ReentrantLock[] forks = new ReentrantLock[NUM];
        for (int i = 0; i < NUM; i++) {
            forks[i] = new ReentrantLock();
        }

        // Create philosopher threads, giving each its two adjacent forks
        Philosopher[] philosophers = new Philosopher[NUM];
        for (int i = 0; i < NUM; i++) {
            ReentrantLock left  = forks[i];
            ReentrantLock right = forks[(i + 1) % NUM];
            philosophers[i] = new Philosopher(i, left, right);
        }

        System.out.println("Starting " + NUM + " philosophers...\n");

        // Start all philosopher threads
        for (Philosopher p : philosophers) p.start();

        // Wait for all philosophers to finish
        for (Philosopher p : philosophers) p.join();

        System.out.println("\n✔ All philosophers are done. The table is empty.");
    }
}
