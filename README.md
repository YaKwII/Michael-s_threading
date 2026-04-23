# Michael's Threading — OS Project 2
**Course:** Operating Systems 4320–6320 | Spring 2025

## Overview
This project simulates real-time process execution using Java threads and demonstrates synchronization using the **Dining Philosophers** problem with `ReentrantLock`.

---

## Files
| File | Description |
|---|---|
| `Main.java` | Main source code — process threads + Dining Philosophers |
| `processes.txt` | Input file with process IDs and burst times |
| `README.md` | Project report |

---

## How to Run

### Requirements
- Java JDK 8 or higher (free)
- Any terminal or IDE (VS Code, IntelliJ, Eclipse)

### Steps
```bash
# 1. Compile
javac Main.java

# 2. Run
java Main
```

---

## Part 1 — Process Thread Simulation
Each line in `processes.txt` represents a process with a **PID** and **burst time (seconds)**.

Example `processes.txt`:
```
1 2
2 3
3 1
4 4
5 2
```

Each process is loaded and assigned its own Java `Thread`. The CPU burst is simulated using `Thread.sleep(burstTime * 1000)`. All threads start simultaneously and the program waits for all to finish using `.join()`.

**Sample Output:**
```
[Process 1] Started. Burst time: 2s
[Process 2] Started. Burst time: 3s
[Process 3] Started. Burst time: 1s
[Process 3] Finished.
[Process 1] Finished.
[Process 2] Finished.
```

---

## Part 2 — Dining Philosophers (Synchronization)

### Problem Description
5 philosophers sit at a table. Each needs **2 forks** to eat. There is one fork between each pair of philosophers. If all pick up the left fork simultaneously, no one can pick up the right fork — this is a **deadlock**.

### Solution — Lower Fork First
To avoid deadlock, each philosopher picks up the **lower-numbered fork first**:
- Even-numbered philosophers: left fork first, then right
- Odd-numbered philosophers: right fork first, then left

This breaks the circular wait condition, preventing deadlock.

### Synchronization Primitive Used
`java.util.concurrent.locks.ReentrantLock` — one lock per fork.

**Sample Output:**
```
[Philosopher 0] Thinking...
[Philosopher 1] Thinking...
[Philosopher 0] Waiting for forks...
[Philosopher 0] Picked up first fork.
[Philosopher 0] Picked up second fork.
[Philosopher 0] Eating...
[Philosopher 0] Released forks.
[Philosopher 1] Waiting for forks...
[Philosopher 1] Picked up first fork.
[Philosopher 1] Picked up second fork.
[Philosopher 1] Eating...
[Philosopher 1] Released forks.
```

---

## Grading Criteria Met
| Task | Points |
|---|---|
| Thread creation from process input | 25 |
| Dining Philosophers with deadlock prevention | 35 |
| Output clarity (logs, execution order) | 20 |
| This report with explanation | 20 |

---

## Tools Used
- Java (built-in `Thread`, `ReentrantLock`) — free
- VS Code — free
- GitHub — free
