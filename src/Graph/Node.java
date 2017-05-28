package Graph;

public class Node {
    private boolean visited;

    public Node() {
        visited = false;
    }

    public boolean isVisited() {
        return visited;
    }

    public void visit() {
        visited = true;
    }
}
