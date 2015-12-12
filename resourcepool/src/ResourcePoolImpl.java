import java.util.Collection;
import java.util.concurrent.TimeUnit;

public class ResourcePoolImpl<R> implements ResourcePool<R> {

    public ResourcePoolImpl(Collection<? extends R> resources) {
    }

    @Override
    public R acquire() throws InterruptedException {
        return null;
    }

    @Override
    public R acquire(long timeout, TimeUnit timeUnit) throws InterruptedException {
        return null;
    }

    @Override
    public R tryAcquire() {
        return null;
    }

    @Override
    public void release(R resource) {
    }
}
