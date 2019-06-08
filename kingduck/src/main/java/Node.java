import java.util.List;
import ia.battle.core.*;

public class Node implements Comparable<Node> {
    public boolean adjacenciesSet;

    public float costFromRoot = Float.MAX_VALUE;
    public float manhattan;

    public FieldCell cell;

    public Node parent;

    public List<Node> adjacents;

    public Node(float manhattan, FieldCell cell) {
        this.manhattan = manhattan;
        this.cell = cell;
    }

    public void setAdjacency(List<Node> adjacents) {
        this.adjacents = adjacents;
        adjacenciesSet = true;
    }

    @Override
    public int compareTo(Node o) {
        return Float.compare(costFromRoot + manhattan, o.costFromRoot + o.manhattan);
    }
}
