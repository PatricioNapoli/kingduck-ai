import java.util.*;

import ia.battle.core.BattleField;
import ia.battle.core.ConfigurationManager;
import ia.battle.core.FieldCell;
import ia.battle.core.FieldCellType;
import ia.battle.core.actions.Move;

public class AStar extends Move {

	private FieldCell from;
	private int toX;
	private int toY;

	public AStar(FieldCell from, int toX, int toY) {
		this.from = from;
	}
	
	@Override
	public ArrayList<FieldCell> move() {
		int x = from.getX();
		int y = from.getY();

		ArrayList<FieldCell> path = new ArrayList<>();

		PriorityQueue<Node> open = new PriorityQueue<>();
		Map<String, Node> closed = new HashMap<>();

		Node first = new Node(0.0f, from, null);
		setAdjacents(first);
		open.add(first);

		Node current;
		while(open.size() > 0) {
			current = open.poll();
		}

//		int to = x + stepX;
//		for (; x < to; x++) {
//			if (x < ConfigurationManager.getInstance().getMapWidth() - 1) {
//				path.add(BattleField.getInstance().getFieldCell(x, y));
//			} else {
//				x--;
//				break;
//			}
//		}
//		to = y + stepY;
//		for (; y < to; y++)
//			if (y < ConfigurationManager.getInstance().getMapHeight() - 1) {
//				System.out.println(x);
//				System.out.println(y);
//				path.add(BattleField.getInstance().getFieldCell(x, y));
//			}

		return path;
	}

	private void setAdjacents(Node node) {
		int x = from.getX();
		int y = from.getY();

		node.setAdjacencies(
				fetchNode(x - 1, y),
				fetchNode(x + 1, y),
				fetchNode(x, y + 1),
				fetchNode(x, y -1),
				fetchNode(x - 1, y + 1),
				fetchNode(x + 1, y + 1),
				fetchNode(x - 1, y -1),
				fetchNode(x + 1, y -1)
		);
	}

	private Node fetchNode(int x, int y) {
		int width = ConfigurationManager.getInstance().getMapWidth();
		int height = ConfigurationManager.getInstance().getMapHeight();

		if (x >= 0 && x <= width && y >= 0 && y <= height) {
			FieldCell cell = BattleField.getInstance().getFieldCell(x, y);

			if (cell.getFieldCellType() == FieldCellType.NORMAL)
				return new Node(0.0f, cell, null);
		}

		return null;
	}
}
