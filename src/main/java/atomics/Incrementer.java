package atomics;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAccumulator;

public class Incrementer {
  static class Worker implements Runnable {
    @Override
    public void run() {
      for (int i = 0; i < 64_000_000; i++) {
//        synchronized (this) {
//          counter++;
//        }
//        counter.incrementAndGet();
        counter.accumulate(1);
      }
    }
  }

//  private static volatile long counter = 0;
//  private static AtomicLong counter = new AtomicLong(0);
  private static LongAccumulator counter =
    new LongAccumulator((a, b) -> a + b, 0);

  public static void main(String[] args) throws Throwable {
    Worker worker = new Worker();
//    System.out.println("count before is " + counter);
    System.out.println("count before is " + counter.get());
    List<Thread> lt = new ArrayList<>();

    long start = System.nanoTime();
    for (int i = 0; i < 4; i++) {
      Thread t = new Thread(worker);
      lt.add(t);
      t.start();
    }
    for (Thread t : lt) {
      t.join();
    }
    long totalTime = System.nanoTime() - start;
    System.out.println("count after is " + counter
    + "\ntime was " + (totalTime / 1_000_000_000.0));
  }
}
