import java.util.*;

import ia.battle.core.BattleField;
import ia.battle.core.ConfigurationManager;
import ia.battle.core.FieldCell;
import ia.battle.core.FieldCellType;
import ia.battle.core.actions.Move;

public class AStar extends Move {

	private FieldCell from;
	private FieldCell to;

	public AStar(FieldCell from, FieldCell to) {
		this.from = from;
		this.to = to;
	}
	
	@Override
	public ArrayList<FieldCell> move() {
		if (from.equals(to))
			return new ArrayList<>();

		PriorityQueue<Node> open = new PriorityQueue<>();
		Map<String, Node> closed = new HashMap<>();

		float manhattan = BattleField.getInstance().calculateDistance(from, to);
		Node first = new Node(manhattan, from);
		first.costFromRoot = 0;
		open.add(first);

		Node current = null;
		while(open.size() > 0) {
			current = open.poll();

			if (current.cell.getX() == to.getX() && current.cell.getY() == to.getY())
				break;

			closed.put(buildKey(current), current);

			setAdjacency(current);
			for(Node n : current.adjacents) {
				if (!closed.containsKey(buildKey(n))) {
					if (current.costFromRoot + n.cell.getCost() < n.costFromRoot) {
						n.parent = current;
						n.costFromRoot = current.costFromRoot + n.cell.getCost();
						open.add(n);
					}
				}
			}
		}

		ArrayList<FieldCell> path = new ArrayList<>();
		buildPath(current, path);
		Collections.reverse(path);

		return path;
	}

	private ArrayList<FieldCell> buildPath(Node current, ArrayList<FieldCell> path) {
		// Check if finished or no path found
		if (current == null)
			return path;

		path.add(current.cell);
		return buildPath(current.parent, path);
	}

	private void setAdjacency(Node node) {
		if (node.adjacenciesSet)
			return;

		List<FieldCell> adjacents = BattleField.getInstance().getAdjacentCells(node.cell);
		List<Node> adjacentNodes = new ArrayList<>();

		for(FieldCell cell : adjacents) {
			if (cell.getFieldCellType() == FieldCellType.NORMAL)
				adjacentNodes.add(new Node(BattleField.getInstance().calculateDistance(cell, to), cell));
		}

		node.setAdjacency(adjacentNodes);
		node.adjacenciesSet = true;
	}

	private String buildKey(Node node) {
		return String.format("%d:%d", node.cell.getX(), node.cell.getY());
	}
}
