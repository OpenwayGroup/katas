package dfs;

import dfs.UtilDFS.AbstractCycleException;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.newLinkedList;
import static dfs.UtilDFS.DFS;
import static dfs.UtilDFSTest.CycleMatcher.isCycle;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;

public class UtilDFSTest {

    class Node {
        final String name;
        final List<Node> children = newArrayList();
        Stream<Node> children() { return children.stream(); }
        Node(String name) {
            this.name = name;
        }
        public Node addChild(String name) {
            return addChild(name, n -> { });
        }
        public Node addChild(String name, Consumer<Node> init) {
            Node child = new Node(name);
            children.add(child);
            init.accept(child);
            return this;
        }
        @Override
        public String toString() {
            return name;
        }
    }

    Consumer<String> action;

    @Before
    @SuppressWarnings("unchecked")
    public void init() {
        action = mock(Consumer.class);
    }

    private void dfs(Node... root) {
        DFS(asList(root), Node::children, n -> action.accept(n.name));
    }

    @Test
    public void emptyGraph() {
        dfs();
        verifyZeroInteractions(action);
    }

    @Test
    public void singleNode() {
        dfs(new Node("root"));
        verify(action).accept("root");
        verifyNoMoreInteractions(action);
    }

    @Test
    public void nodeChain() {
        dfs(
            new Node("root")
                .addChild("child", n -> {
                    n.addChild("grandChild");
                })
        );
        verify(action).accept("root");
        verify(action).accept("child");
        verify(action).accept("grandChild");
        verifyNoMoreInteractions(action);
    }

    @Test
    public void simpleTree() {
        dfs(
            new Node("root")
                .addChild("child1")
                .addChild("child2")
        );
        verify(action).accept("root");
        verify(action).accept("child1");
        verify(action).accept("child2");
        verifyNoMoreInteractions(action);
    }

    @Test
    public void complexTree() {
        dfs(
            new Node("root")
                .addChild("child1", n -> {
                    n.addChild("child11");
                    n.addChild("child12");
                })
                .addChild("child2", n -> {
                    n.addChild("child21");
                    n.addChild("child22");
                })
        );
        verify(action).accept("root");
        verify(action).accept("child1");
        verify(action).accept("child11");
        verify(action).accept("child12");
        verify(action).accept("child2");
        verify(action).accept("child21");
        verify(action).accept("child22");
        verifyNoMoreInteractions(action);
    }

    @Test
    public void diamond() {
        Node root = new Node("root")
            .addChild("child1")
            .addChild("child2");
        Node gandChild = new Node("grandChild");
        root.children().forEach(c -> c.children.add(gandChild));
        dfs(root);
        verify(action).accept("root");
        verify(action).accept("child1");
        verify(action).accept("grandChild");
        verify(action).accept("child2");
        verifyNoMoreInteractions(action);
    }

    @Test(expected = AbstractCycleException.class)
    public void selfChild() {
        Node root = new Node("root");
        root.children.add(root);
        try {
            dfs(root);
        } catch (AbstractCycleException e) {
            assertThat(e.getCycle(), isCycle(asList(root)));
            throw e;
        }
    }

    @Test(expected = AbstractCycleException.class)
    public void loop() {
        Node root = new Node("root");
        Node child = new Node("child");
        Node grandChild = new Node("grandChild");
        root.children.add(child);
        child.children.add(grandChild);
        grandChild.children.add(root);
        try {
            dfs(root);
        } catch (AbstractCycleException e) {
            assertThat(e.getCycle(), isCycle(asList(root, child, grandChild)));
            throw e;
        }
    }

    @Test(expected = AbstractCycleException.class)
    public void loopNotFromRoot() {
        Node root = new Node("root");
        Node a = new Node("a");
        Node b = new Node("b");
        Node c = new Node("c");
        root.children.add(a);
        a.children.add(b);
        b.children.add(c);
        c.children.add(a);
        try {
            dfs(root);
        } catch (AbstractCycleException e) {
            assertThat(e.getCycle(), isCycle(asList(a, b, c)));
            throw e;
        }
    }

    @Test
    public void severalRoots() {
        Node root1 = new Node("root1");
        Node root2 = new Node("root2");
        Node a = new Node("a");
        Node b = new Node("b");

        root1.children.add(a);
        root1.children.add(b);
        root2.children.add(a);
        root2.children.add(b);

        dfs(root1, root2);
        verify(action).accept("root1");
        verify(action).accept("a");
        verify(action).accept("b");
        verify(action).accept("root2");
        verifyNoMoreInteractions(action);
    }


    static class CycleMatcher<T> extends BaseMatcher<List<T>> {

        private final LinkedList<T> expected = newLinkedList();

        private CycleMatcher(List<T> elements) {
            expected.addAll(elements);
        }

        public static <T> Matcher<List<T>> isCycle(List<T> elements) {
            return new CycleMatcher<>(elements);
        }

        @Override
        public boolean matches(Object item) {
            for (int i = 0; i < expected.size(); i++) {
                if (expected.equals(item)) return true;
                expected.add(expected.removeFirst());
            }
            return false;
        }

        @Override
        public void describeTo(Description description) {
            description.appendValue(expected);
        }

    }

}
