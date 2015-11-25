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
        return null; // write your code here
    }
}
