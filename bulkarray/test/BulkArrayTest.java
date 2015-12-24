import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BulkArrayTest {

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void outOfBounds1() {
        new BulkArrayImpl<Void>(5).get(-1);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void outOfBounds2() {
        new BulkArrayImpl<Void>(5).get(7);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void outOfBounds3() {
        new BulkArrayImpl<Void>(5).set(-1, null);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void outOfBounds4() {
        new BulkArrayImpl<Void>(5).set(7, null);
    }

    @Test
    public void simpleSetters() {
        BulkArray<String> a = new BulkArrayImpl<>(3);
        a.set(1, "Hello");

        a.set(2, "Worls");
        a.set(2, "World");

        assertEquals(null, a.get(0));
        assertEquals("Hello", a.get(1));
        assertEquals("World", a.get(2));
    }

    @Test
    public void advancedSetters() {
        BulkArray<String> a = new BulkArrayImpl<>(3);
        a.set(1, "Hello");
        a.setAll("World");

        assertEquals("World", a.get(0));
        assertEquals("World", a.get(1));

        a.set(0, "Hello");
        assertEquals("Hello", a.get(0));
        assertEquals("World", a.get(1));
        assertEquals("World", a.get(2));

        a.setAll(null);
        assertEquals(null, a.get(0));
        assertEquals(null, a.get(1));
        assertEquals(null, a.get(2));
    }

    @Test
    public void advancedSetters2() {
        BulkArray<String> a = new BulkArrayImpl<>(3);
        a.setAll("Hello");
        a.set(1, null);
        assertEquals(null, a.get(1));
    }

    @Test(timeout = 2000)
    public void testStess() {
        BulkArray<Integer> a = new BulkArrayImpl<>(1024 * 1024 + 1);
        for (int i = 0; i < 1024 * 1024; i++) {
            a.set(i, i);
            a.setAll(i + 1);
        }
    }

}
