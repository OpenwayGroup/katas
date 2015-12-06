
/**
 * Array-like structure with fixed size and bulk set operation.
 *
 * @param <T> type of containing elements
 */
public interface BulkArray<T> {

    /**
     * Returns the value at specified index.
     * Computational complexity is O(1).
     */
    T get(int index) throws ArrayIndexOutOfBoundsException;

    /**
     * Sets the specified value to the specified index.
     * Computational complexity is O(1).
     */
    void set(int index, T value) throws ArrayIndexOutOfBoundsException;

    /**
     * Sets the specified value to all indexes.
     * Computational complexity is O(1).
     */
    void setAll(T value);

}
