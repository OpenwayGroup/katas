import java.util.concurrent.atomic.AtomicInteger;

public class Resource {

    private static final AtomicInteger ID_GENERATOR = new AtomicInteger();

    private final int id;

    public Resource() {
        this.id = ID_GENERATOR.getAndIncrement();
    }

    @Override
    public String toString() {
        return "R#" + id;
    }
}
