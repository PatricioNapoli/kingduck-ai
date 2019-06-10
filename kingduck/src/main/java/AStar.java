import java.util.*;

import ia.battle.core.*;
import ia.battle.core.actions.Move;

public class AStar extends Move {

	private FieldCell from;
	private FieldCell to;

	private BattleField battleField;

	public AStar(FieldCell from, FieldCell to) {
		this.from = from;
		this.to = to;

		this.battleField = BattleField.getInstance();
	}
	
	@Override
	public ArrayList<FieldCell> move() {
		if (from.equals(to))
			return new ArrayList<>(Collections.singletonList(battleField.getAdjacentCells(from).get(0)));

		PriorityQueue<Node> open = new PriorityQueue<>();
		Map<String, Node> closed = new HashMap<>();

		float manhattan = battleField.calculateDistance(from, to);
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
				if (closed.containsKey(buildKey(n)))
					continue;

				if (!contains(open, n)) {
					n.parent = current;
					n.costFromRoot = current.costFromRoot + n.cell.getCost() + n.negativeBias;
					open.add(n);
				} else {
					if (current.costFromRoot + n.cell.getCost() < n.costFromRoot) {
						n.parent = current;
						n.costFromRoot = current.costFromRoot + n.cell.getCost();
					}
				}
			}
		}

		ArrayList<FieldCell> path = new ArrayList<>();
		buildPath(current, path);
		Collections.reverse(path);

		return path;
	}

	private boolean contains(PriorityQueue<Node> queue, Node node) {
		for (Node n : queue) {
			if (n.cell.getX() == node.cell.getX() && n.cell.getY() == node.cell.getY())
				return true;
		}

		return false;
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

		List<FieldCell> adjacents = battleField.getAdjacentCells(node.cell);
		List<Node> adjacentNodes = new ArrayList<>();

		for(FieldCell cell : adjacents) {
			// Ignore if inside hunter, to escape
			boolean ignoreHunter = inHunterRange(from);

			if (cell.getFieldCellType() == FieldCellType.NORMAL) {
				Node n = new Node(battleField.calculateDistance(cell, to), cell);
				adjacentNodes.add(n);

				if (!ignoreHunter && inHunterRange(cell))
					n.negativeBias = 2.0f;
			}
		}

		node.setAdjacency(adjacentNodes);
		node.adjacenciesSet = true;
	}

	private boolean inHunterRange(FieldCell cell) {
		FieldCell hunterPos = battleField.getHunterData().getFieldCell();

		int centerX = hunterPos.getX();
		int centerY = hunterPos.getY();

		int range = 10;

		int x = cell.getX();
		int y = cell.getY();

		return (Math.pow(centerX - x, 2)) + (Math.pow(centerY - y, 2)) <= Math.pow(range, 2);
	}

	private String buildKey(Node node) {
		return String.format("%d:%d", node.cell.getX(), node.cell.getY());
	}
}
