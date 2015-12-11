import org.junit.Test;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

public class ResourcePoolTest {

    @SafeVarargs
    private static <R> ResourcePool<R> poolOf(R... resources) {
        return poolOf(Arrays.asList(resources));
    }

    private static <R> ResourcePool<R> poolOf(Collection<? extends R> resources) {
        return new ResourcePool<>(Collections.unmodifiableCollection(resources));
    }

    @Test
    public void successfully_acquire_single_resource() throws Exception {
        Resource originalResource = new Resource();
        ResourcePool<Resource> pool = poolOf(originalResource);

        Resource acquiredResource = pool.acquire();

        assertSame(originalResource, acquiredResource);
    }

    @Test
    public void successfully_tryAcquire_single_resource() throws Exception {
        Resource originalResource = new Resource();
        ResourcePool<Resource> pool = poolOf(originalResource);

        Resource acquiredResource = pool.tryAcquire();

        assertSame(originalResource, acquiredResource);
    }

    @Test
    public void successfully_acquire_multiple_resources() throws Exception {
        int numberOfResources = 10;
        List<Resource> originalResources = IntStream.range(0, numberOfResources)
                .mapToObj(i -> new Resource()).collect(Collectors.toList());
        ResourcePool<Resource> pool = poolOf(originalResources);

        Set<Resource> acquiredResources = new LinkedHashSet<>(numberOfResources);
        for (int i = 0; i < numberOfResources; i++) {
            acquiredResources.add(pool.acquire());
        }

        assertEquals(new LinkedHashSet<>(originalResources), acquiredResources);
    }

    @Test
    public void tryAcquire_returns_null_immediately() throws Exception {
        ResourcePool<Resource> pool = poolOf(new Resource());

        pool.acquire();

        Resource acquiredResource = pool.tryAcquire();

        assertNull(acquiredResource);
    }

    @Test
    public void acquire_blocks_and_throws_exception_when_interrupted() throws Exception {
        ResourcePool<Resource> pool = poolOf(new Resource());

        pool.acquire();

        AtomicReference<Throwable> throwableRef = new AtomicReference<>();
        Thread testThread = new Thread(() -> {
            try {
                pool.acquire();
            } catch (Throwable t) {
                throwableRef.set(t);
            }
        });
        testThread.start();
        testThread.join(1000);
        assertTrue(testThread.isAlive());
        testThread.interrupt();
        testThread.join();
        assertTrue(throwableRef.get() instanceof InterruptedException);
    }

    @Test
    public void acquire_with_timeout_blocks_and_throws_exception_when_interrupted() throws Exception {
        ResourcePool<Resource> pool = poolOf(new Resource());

        pool.acquire();

        AtomicReference<Throwable> throwableRef = new AtomicReference<>();
        Thread testThread = new Thread(() -> {
            try {
                pool.acquire(5, TimeUnit.SECONDS);
            } catch (Throwable t) {
                throwableRef.set(t);
            }
        });
        testThread.start();
        testThread.join(1000);
        assertTrue(testThread.isAlive());
        testThread.interrupt();
        testThread.join();
        assertTrue(throwableRef.get() instanceof InterruptedException);
    }

    @Test
    public void acquire_with_timeout_returns_null_after_timeout() throws Exception {
        ResourcePool<Resource> pool = poolOf(new Resource());

        pool.acquire();
        Resource acquiredResource = pool.acquire(1000, TimeUnit.MILLISECONDS);

        assertNull(acquiredResource);
    }

    @Test
    public void resource_becomes_available_again_after_release() throws Exception {
        Resource originalResource = new Resource();
        ResourcePool<Resource> pool = poolOf(originalResource);

        pool.release(pool.acquire());

        Resource acquiredResource = pool.acquire();
        assertSame(originalResource, acquiredResource);
    }

    @Test
    public void acquire_unblocks_after_release_in_another_thread() throws Exception {
        Resource originalResource = new Resource();
        ResourcePool<Resource> pool = poolOf(originalResource);

        pool.acquire();

        AtomicReference<Resource> resourceRef = new AtomicReference<>();
        AtomicReference<Throwable> throwableRef = new AtomicReference<>();
        Thread acquireThread = new Thread(() -> {
            try {
                resourceRef.set(pool.acquire());
            } catch (Throwable t) {
                throwableRef.set(t);
            }
        });
        acquireThread.start();
        acquireThread.join(1000);
        assertTrue(acquireThread.isAlive());
        pool.release(originalResource);
        acquireThread.join();

        assertSame(originalResource, resourceRef.get());
        assertNull(throwableRef.get());
    }

    @Test
    public void acquire_with_timeout_unblocks_after_release_in_another_thread() throws Exception {
        Resource originalResource = new Resource();
        ResourcePool<Resource> pool = poolOf(originalResource);

        pool.acquire();

        AtomicReference<Resource> resourceRef = new AtomicReference<>();
        AtomicReference<Throwable> throwableRef = new AtomicReference<>();
        Thread acquireThread = new Thread(() -> {
            try {
                resourceRef.set(pool.acquire(10, TimeUnit.SECONDS));
            } catch (Throwable t) {
                throwableRef.set(t);
            }
        });
        acquireThread.start();
        acquireThread.join(1000);
        assertTrue(acquireThread.isAlive());
        pool.release(originalResource);
        acquireThread.join();

        assertSame(originalResource, resourceRef.get());
        assertNull(throwableRef.get());
    }

    @Test
    public void stress_test() throws Exception {
        int numberOfResources = 8;
        List<Resource> originalResources = IntStream.range(0, numberOfResources)
                .mapToObj(i -> new Resource()).collect(Collectors.toList());
        ResourcePool<Resource> pool = poolOf(originalResources);

        AtomicInteger acquiredResourceCounter = new AtomicInteger();
        int numberOfThreads = 16;
        List<Thread> testThreads = new ArrayList<>(numberOfThreads);
        List<Throwable> problems = Collections.synchronizedList(new ArrayList<>());
        for (int i = 0; i < numberOfThreads; i++) {
            Thread thread = new Thread(() -> {
                try {
                    for (;;) {
                        Resource resource = pool.acquire();
                        int currentCount = acquiredResourceCounter.incrementAndGet();
                        Thread.yield();
                        acquiredResourceCounter.decrementAndGet();
                        pool.release(resource);

                        assertNotNull(resource);
                        if (currentCount > numberOfResources) {
                            fail(currentCount + " resources were acquired at the same time");
                        }

                        if (Thread.currentThread().isInterrupted()) {
                            throw new InterruptedException();
                        }
                    }
                } catch (InterruptedException e) {
                    // fine
                } catch (Throwable t) {
                    problems.add(t);
                }
            });
            thread.start();
            testThreads.add(thread);
        }

        testThreads.iterator().next().join(1000);

        for (Thread thread : testThreads) {
            assertTrue(thread.isAlive());
            thread.interrupt();
            thread.join();
        }

        assertTrue(problems.isEmpty());

        Set<Resource> finalResources = new LinkedHashSet<>();
        for (;;) {
            Resource resource = pool.tryAcquire();
            if (resource == null) {
                break;
            } else {
                finalResources.add(resource);
            }
        }
        assertEquals(new LinkedHashSet<>(originalResources), finalResources);
    }
}
