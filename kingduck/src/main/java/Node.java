import ia.battle.core.FieldCell;

public class Node implements Comparable<Node> {
    public boolean adjacenciesSet;

    public float costFromRoot;

    public FieldCell cell;

    public Node parent;

    public Node left;
    public Node right;
    public Node up;
    public Node down;

    public Node upLeft;
    public Node upRight;
    public Node downLeft;
    public Node downRight;

    public Node(float costFromRoot, FieldCell cell, Node parent) {
        this.costFromRoot = costFromRoot;
        this.cell = cell;
        this.parent = parent;
    }

    public void setAdjacencies(Node left, Node right, Node up, Node down, Node upLeft, Node upRight, Node downLeft, Node downRight) {
        this.left = left;
        this.right = right;
        this.up = up;
        this.down = down;
        this.upLeft = upLeft;
        this.upRight = upRight;
        this.downLeft = downLeft;
        this.downRight = downRight;

        adjacenciesSet = true;
    }

    @Override
    public int compareTo(Node o) {
        return Float.compare(o.costFromRoot, costFromRoot);
    }
}
