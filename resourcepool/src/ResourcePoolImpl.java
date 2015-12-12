import java.util.Collection;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class ResourcePoolImpl<R> implements ResourcePool<R> {

    private final BlockingQueue<R> resources;

    public ResourcePoolImpl(Collection<? extends R> resources) {
        this.resources = new ArrayBlockingQueue<>(resources.size(), false, resources);
    }

    public R acquire() throws InterruptedException {
        return resources.take();
    }

    public R acquire(long timeout, TimeUnit timeUnit) throws InterruptedException {
        return resources.poll(timeout, timeUnit);
    }

    public R tryAcquire() {
        return resources.poll();
    }

    public void release(R resource) {
        resources.add(resource);
    }
}
