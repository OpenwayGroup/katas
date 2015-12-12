import java.util.concurrent.TimeUnit;

/**
 * A pool of resources that can be safely acquired and released by multiple threads.
 *
 * @param <R> type of resource managed by the pool
 */
public interface ResourcePool<R> {

    /**
     * Returns a spare resource from the pool.
     * If all resources are already acquired, the method blocks and waits
     * indefinitely for a resource to be released.
     *
     * @return resource from the pool
     * @throws InterruptedException if wait is interrupted
     */
    public R acquire() throws InterruptedException;

    /**
     * Returns a spare resource from the pool.
     * If all resources are already acquired, the method blocks and waits
     * up to a specified timeout for a resource to be released.
     *
     * @param timeout     timeout
     * @param timeUnit    time unit of {@code timeout}
     * @return resource from the pool, or {@code null} if no resource became available within timeout
     * @throws InterruptedException if wait is interrupted
     */
    public R acquire(long timeout, TimeUnit timeUnit) throws InterruptedException;

    /**
     * Immediately returns a spare resource from the pool, or {@code null} if no resource is available.
     *
     * @return resource from the pool
     */
    public R tryAcquire();

    /**
     * Releases a previously acquired resource.
     *
     * @param resource    resource
     */
    public void release(R resource);
}
