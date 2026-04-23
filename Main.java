import java.io.*;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

// ─────────────────────────────────────────────
// ProcessThread: simulates a CPU process burst
// ─────────────────────────────────────────────
class ProcessThread extends Thread {
    int pid;
    int burstTime;

    public ProcessThread(int pid, int burstTime) {
        this.pid = pid;
        this.burstTime = burstTime;
    }

    public void run() {
        System.out.println("[Process " + pid + "] Started. Burst time: " + burstTime + "s");
        try {
            Thread.sleep(burstTime * 1000); // simulate CPU burst
        } catch (InterruptedException e) {
            System.out.println("[Process " + pid + "] Interrupted!");
        }
        System.out.println("[Process " + pid + "] Finished.");
    }
}

// ─────────────────────────────────────────────
// Philosopher: implements Dining Philosophers
// Deadlock prevention: always pick up the
// lower-numbered fork first
// ─────────────────────────────────────────────
class Philosopher extends Thread {
    int id;
    ReentrantLock leftFork;
    ReentrantLock rightFork;

    public Philosopher(int id, ReentrantLock leftFork, ReentrantLock rightFork) {
        this.id = id;
        this.leftFork = leftFork;
        this.rightFork = rightFork;
    }

    public void run() {
        try {
            for (int i = 0; i < 3; i++) { // each philosopher eats 3 times

                // THINKING
                System.out.println("[Philosopher " + id + "] Thinking...");
                Thread.sleep((long)(Math.random() * 1000));

                // WAITING FOR FORKS
                System.out.println("[Philosopher " + id + "] Waiting for forks...");

                // Pick up lower-numbered fork first to avoid deadlock
                ReentrantLock firstFork  = (id % 2 == 0) ? leftFork  : rightFork;
                ReentrantLock secondFork = (id % 2 == 0) ? rightFork : leftFork;

                firstFork.lock();
                System.out.println("[Philosopher " + id + "] Picked up first fork.");

                secondFork.lock();
                System.out.println("[Philosopher " + id + "] Picked up second fork.");

                // EATING
                System.out.println("[Philosopher " + id + "] Eating...");
                Thread.sleep((long)(Math.random() * 1000));

                // RELEASE FORKS
                firstFork.unlock();
                secondFork.unlock();
                System.out.println("[Philosopher " + id + "] Released forks.");
            }
            System.out.println("[Philosopher " + id + "] Done eating for the night.");
        } catch (InterruptedException e) {
            System.out.println("[Philosopher " + id + "] Interrupted!");
        }
    }
}

// ─────────────────────────────────────────────
// Main: loads processes.txt, runs process
// threads, then runs Dining Philosophers
// ─────────────────────────────────────────────
public class Main {

    public static void main(String[] args) throws Exception {

        // ── PART 1: Load processes from file and run as threads ──
        System.out.println("======================================");
        System.out.println(" PART 1: Process Thread Simulation");
        System.out.println("======================================");

        List<ProcessThread> processThreads = new ArrayList<>();

        BufferedReader reader = new BufferedReader(new FileReader("processes.txt"));
        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) continue; // skip comments
            String[] parts = line.split("\\s+");
            int pid       = Integer.parseInt(parts[0]);
            int burstTime = Integer.parseInt(parts[1]);
            processThreads.add(new ProcessThread(pid, burstTime));
        }
        reader.close();

        // Start all process threads
        for (ProcessThread pt : processThreads) pt.start();

        // Wait for all process threads to finish
        for (ProcessThread pt : processThreads) pt.join();

        System.out.println("\nAll processes completed.\n");

        // ── PART 2: Dining Philosophers ──
        System.out.println("======================================");
        System.out.println(" PART 2: Dining Philosophers");
        System.out.println("======================================");

        int numPhilosophers = 5;

        // Create one ReentrantLock per fork
        ReentrantLock[] forks = new ReentrantLock[numPhilosophers];
        for (int i = 0; i < numPhilosophers; i++) {
            forks[i] = new ReentrantLock();
        }

        // Create philosopher threads
        Philosopher[] philosophers = new Philosopher[numPhilosophers];
        for (int i = 0; i < numPhilosophers; i++) {
            ReentrantLock leftFork  = forks[i];
            ReentrantLock rightFork = forks[(i + 1) % numPhilosophers];
            philosophers[i] = new Philosopher(i, leftFork, rightFork);
        }

        // Start all philosopher threads
        for (Philosopher p : philosophers) p.start();

        // Wait for all philosophers to finish
        for (Philosopher p : philosophers) p.join();

        System.out.println("\nAll philosophers are done.");
    }
}
