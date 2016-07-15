package dfs;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

public class UtilDFS {

    // TODO implement
    public static <N> void DFS(Collection<N> roots, Function<N, Stream<N>> children, Consumer<N> action) throws AbstractCycleException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    static abstract class AbstractCycleException extends RuntimeException {
        public abstract List<Object> getCycle();
    }

}
