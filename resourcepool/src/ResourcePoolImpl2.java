import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

public class ResourcePoolImpl2<R> implements ResourcePool<R> {

    private final Queue<R> resources;

    public ResourcePoolImpl2(Collection<? extends R> resources) {
        this.resources = new ArrayDeque<>(resources);
    }

    public R acquire() throws InterruptedException {
        synchronized (resources) {
            while (resources.isEmpty()) {
                resources.wait();
            }
            return resources.poll();
        }
    }

    public R acquire(long timeout, TimeUnit timeUnit) throws InterruptedException {
        long remainingWaitTime = timeUnit.toMillis(timeout);
        synchronized (resources) {
            while (resources.isEmpty() && remainingWaitTime > 0) {
                long waitStarted = System.currentTimeMillis();
                resources.wait(remainingWaitTime);
                remainingWaitTime -= System.currentTimeMillis() - waitStarted;
            }
            return resources.poll();
        }
    }

    public R tryAcquire() {
        synchronized (resources) {
            return resources.poll();
        }
    }

    public void release(R resource) {
        synchronized (resources) {
            resources.add(resource);
            resources.notifyAll();
        }
    }
}
