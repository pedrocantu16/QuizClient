import java.util.concurrent.CountDownLatch;

public class Clients {

  private static final int NUM_THREADS = 500;

  public static void main(String[] args) throws InterruptedException {

    // expect arguments 'serverIP' and 'port'
    String serverIP = args[1];
    String port = args[3];

    // Counter array
    Counter[] counters = new Counter[NUM_THREADS];
    for (int j = 0; j < NUM_THREADS; j++) {
      counters[j] = new Counter();
    }

    // URL
    String url = "http://" +  serverIP + ":" + port + "/prime/";

    // Create Threads
    CountDownLatch completed = new CountDownLatch(NUM_THREADS);
    Thread[] threads = new Thread[NUM_THREADS];
    for (int k = 0; k < NUM_THREADS; k++) {
      // instantiate a new Client (which implements Runnable)
      Client newClient = new Client(url, completed, counters[k]);
      Thread newThread = new Thread(newClient);
      threads[k] = newThread;
    }

    long start = System.currentTimeMillis();
    System.out.println("START TIME: " + start);
    for (int i = 0; i < NUM_THREADS; i++) {
      // start the thread
      threads[i].start();
    }
    completed.await();
    long end = System.currentTimeMillis();

    // Process results
    int total = NUM_THREADS*200;
    int success = 0;
    for (int j = 0; j < NUM_THREADS; j++) {
      success += counters[j].getPrimes();
    }
    int failed = total - success;

    System.out.println("Number of Primes: " + success);
    System.out.println("Number of Non-Primes: " + failed);
    System.out.println("WALL TIME (milliseconds): " + (end - start));
  }

}
