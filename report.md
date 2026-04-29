# Operating Systems Project 2 — Report
**Thread-Based Process Simulation and Synchronization**  
Course: Operating Systems 4320–6320 | Spring 2025

---

## 1. Overview

This project simulates real-time process execution using Java threads and demonstrates how an operating system handles **synchronization** using locks (mutexes). The program is split into two phases:

- **Phase 1** – Each process from `processes.txt` becomes a Java thread. They all run concurrently, simulating parallel CPU bursts.
- **Phase 2** – The classic **Dining Philosophers** problem is implemented with deadlock prevention using `ReentrantLock`.

---

## 2. Phase 1 — Process Thread Simulation

### How it works (like a kindergarten explanation)
Imagine 5 kids at recess. Each kid gets a timer — when the timer goes off, they come back inside. All 5 kids go out **at the same time**. The teacher waits for **all** kids to return before moving to the next activity.

### Technical explanation
- `processes.txt` is read line by line. Each line contains a **PID** and a **burst time**.
- A `ProcessThread` object (which extends `Thread`) is created for each process.
- All threads are started with `.start()` and the main program waits for each with `.join()`.
- `Thread.sleep(burstTime * 1000)` simulates CPU activity.

### Sample output
```
[Process 1] Started.  Burst time = 2s
[Process 2] Started.  Burst time = 3s
[Process 3] Started.  Burst time = 1s
[Process 4] Started.  Burst time = 4s
[Process 5] Started.  Burst time = 2s
[Process 3] Finished.
[Process 1] Finished.
[Process 5] Finished.
[Process 2] Finished.
[Process 4] Finished.
```
> Note: finish order depends on burst time — shorter burst = finishes first.

---

## 3. Phase 2 — Dining Philosophers (Synchronization)

### The problem (like a kindergarten explanation)
Picture 5 friends sitting in a circle at lunch. Between every two friends there is **one fork**. To eat, each friend needs **both** forks next to them. If every friend grabs the left fork at the exact same moment, no one can pick up a right fork → everyone waits forever. This is called a **deadlock**.

### Our solution: ordered lock acquisition
To prevent deadlock, we break the circular waiting pattern:
- **Even-numbered philosophers** pick up the **left fork first**.
- **Odd-numbered philosophers** pick up the **right fork first**.

This ensures that at least one philosopher can always complete the pick-up sequence, preventing the circular dependency that causes deadlock.

### Synchronization primitives used
| Tool | Role |
|---|---|
| `ReentrantLock` | Represents a physical fork — only one philosopher can hold it at a time |
| `.lock()` | Philosopher picks up a fork (blocks if already taken) |
| `.unlock()` | Philosopher puts the fork back down |

### Thread activity log format
```
[Philosopher 0] Thinking... (round 1)
[Philosopher 0] Hungry — waiting for forks...
[Philosopher 0] Picked up fork #1.
[Philosopher 0] Picked up fork #2. Now eating...
[Philosopher 0] Eating... (round 1)
[Philosopher 0] Released both forks.
```

---

## 4. Files Submitted

| File | Description |
|---|---|
| `Main.java` | Complete source code with comments |
| `processes.txt` | Input file with 5 processes (PID + burst time) |
| `report.md` | This report |

---

## 5. How to Run

```bash
# Compile
javac Main.java

# Run
java Main
```

No external libraries needed — only Java's built-in `java.util.concurrent.locks.ReentrantLock`.

---

## 6. Key Concepts Learned

- **Thread** — a lightweight process that runs concurrently with others.
- **Mutex/Lock** — a tool that lets only ONE thread access a resource at a time.
- **Deadlock** — when every thread is waiting for a resource held by another thread, causing the whole program to freeze.
- **Deadlock Prevention** — we avoided deadlock by enforcing a consistent order when acquiring two locks.
