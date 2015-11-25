import org.junit.Test;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class SummaryTest {

    @Test
    public void ascending_stream_of_integers() {
        Stream<Integer> stream = Stream.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);

        Summary<Integer> summary = Summary.ofStream(stream);

        assertEquals(Integer.valueOf(0), summary.min);
        assertEquals(Integer.valueOf(9), summary.max);
        assertEquals(10, summary.count);
    }

    @Test
    public void descending_stream_of_integers() {
        Stream<Integer> stream = Stream.of(9, 8, 7, 6, 5, 4, 3, 2, 1, 0);

        Summary<Integer> summary = Summary.ofStream(stream);

        assertEquals(Integer.valueOf(0), summary.min);
        assertEquals(Integer.valueOf(9), summary.max);
        assertEquals(10, summary.count);
    }

    @Test
    public void stream_of_integers_in_random_order() {
        Stream<Integer> stream = Stream.of(9, 3, 0, 1, 8, 4, 7, 5, 6, 2);

        Summary<Integer> summary = Summary.ofStream(stream);

        assertEquals(Integer.valueOf(0), summary.min);
        assertEquals(Integer.valueOf(9), summary.max);
        assertEquals(10, summary.count);
    }

    @Test
    public void stream_of_strings() {
        Stream<String> stream = IntStream.range(0, 100).mapToObj(Integer::toString);

        Summary<String> summary = Summary.ofStream(stream);

        assertEquals("0", summary.min);
        assertEquals("99", summary.max);
        assertEquals(100, summary.count);
    }

    @Test
    public void stream_of_single_element() throws Exception {
        Stream<Integer> stream = Stream.of(-1000);

        Summary<Integer> summary = Summary.ofStream(stream);

        assertEquals(Integer.valueOf(-1000), summary.min);
        assertEquals(Integer.valueOf(-1000), summary.max);
        assertEquals(1, summary.count);
    }

    @Test
    public void empty_stream() throws Exception {
        Stream<Integer> stream = Stream.empty();

        Summary<Integer> summary = Summary.ofStream(stream);

        assertNull(summary.min);
        assertNull(summary.max);
        assertEquals(0, summary.count);
    }

    @Test
    public void large_parallel_stream_of_doubles() throws Exception {
        int limit = 100_000_000;
        Stream<Double> stream = IntStream.rangeClosed(-limit, limit).parallel().mapToObj(Double::valueOf);

        Summary<Double> summary = Summary.ofStream(stream);

        assertEquals(Double.valueOf(-limit), summary.min);
        assertEquals(Double.valueOf(limit), summary.max);
        assertEquals(2 * limit + 1, summary.count);
    }
}
