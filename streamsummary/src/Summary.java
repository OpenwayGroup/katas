import java.util.stream.Stream;

public class Summary<T> {

    public final T min;
    public final T max;
    public final int count;

    private Summary(T min, T max, int count) {
        this.min = min;
        this.max = max;
        this.count = count;
    }

    public static <T extends Comparable<T>> Summary<T> ofStream(Stream<? extends T> stream) {
        Accumulator<T> result = stream.collect(Accumulator::new, Accumulator::accept, Accumulator::combine);
        return new Summary<>(result.min, result.max, result.count);
    }

    private static class Accumulator<T extends Comparable<T>> {

        T min;
        T max;
        int count;

        public void accept(T t) {
            checkMin(t);
            checkMax(t);
            count++;
        }

        public void combine(Accumulator<T> other) {
            if (other.min != null) {
                checkMin(other.min);
            }
            if (other.max != null) {
                checkMax(other.max);
            }
            count += other.count;
        }

        private void checkMin(T t) {
            if (min == null || min.compareTo(t) > 0) {
                min = t;
            }
        }

        private void checkMax(T t) {
            if (max == null || max.compareTo(t) < 0) {
                max = t;
            }
        }
    }
}
