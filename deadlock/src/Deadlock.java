import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

public class Deadlock {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java Deadlock <number of threads>");
            System.exit(1);
        }

        int n = Integer.parseInt(args[0]);

        Object[] monitors = new Object[n];
        Arrays.setAll(monitors, i -> new Object());

        CountDownLatch latch = new CountDownLatch(n);

        for (int i = 0; i < n; i++) {
            int current = i;
            new Thread(() -> {
                synchronized (monitors[current]) {
                    latch.countDown();
                    try {
                        latch.await();
                    } catch (InterruptedException e) {
                        System.err.println("Thread " + Thread.currentThread().getName() + " interrupted");
                        return;
                    }
                    synchronized (monitors[(current + 1) % n]) {
                        // will never get here
                    }
                }
            }).start();
        }
    }
}
