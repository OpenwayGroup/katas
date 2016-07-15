package fibonacci;

import java.math.BigInteger;
import java.util.stream.Stream;

import static java.math.BigInteger.ZERO;

public class FibonacciUtils {

    // TODO implement
    public static Stream<BigInteger> newFibonacciStream() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Returns the n-th number of the Fibonacci series.
     *
     * Fibonacci series starts from two ones and each element
     * is a sum of two previous elements.
     * So the beginning of the series looks like 1, 1, 2, 3, 5, 8, 13,...
     *
     * Indexes start from 1.
     *
     * @param n positive index of number to be found
     * @return the n-th number of the Fibonacci series
     */
    public static BigInteger fibonacciNum(int n) {
        return ZERO;
    }

    /**
     * Returns sum of first n Fibonacci numbers.
     */
    public static BigInteger fibonacciSum(int n) {
        return ZERO;
    }

    /**
     * Returns sum of squares for first n Fibonacci numbers.
     */
    public static BigInteger fibonacciSqrSum(int n) {
        return ZERO;
    }

}
