import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ia.battle.core.*;
import ia.battle.core.actions.Action;
import ia.battle.core.actions.Attack;
import ia.exceptions.RuleException;

public class Duci extends Warrior {
	private Random rand = new Random();

	public Duci(String name, int health, int defense, int strength, int speed, int range) throws RuleException {
		super(name, health, defense, strength, speed, range);
	}
	
	@Override
	public Action playTurn(long tick, int actionNumber) {
		WarriorData wd = BattleField.getInstance().getEnemyData();

		if (wd.getInRange())
			return new Attack(wd.getFieldCell());
		
		ArrayList<FieldCell> si = BattleField.getInstance().getSpecialItems();
		// TODO: ignore stealth abilities
		
		FieldCell destination = this.getPosition();

		if (si.size() > 0) {
			FieldCell[] arr = si.toArray(new FieldCell[0]);
			bubbleSortByDistance(arr);

			destination = arr[0];
		}

		if (destination.getX() == this.getPosition().getX() && destination.getY() == this.getPosition().getY()) {
			List<FieldCell> adjacents = BattleField.getInstance().getAdjacentCells(this.getPosition());

			// I'm trapped
			if (adjacents.size() == 0)
				return new AStar(this.getPosition(), destination);

			destination = adjacents.get(rand.nextInt(adjacents.size() - 1));
		}

		return new AStar(this.getPosition(), destination);
	}

	private void bubbleSortByDistance(FieldCell[] arr)
	{
		int n = arr.length;
		for (int i = 0; i < n-1; i++)
			for (int j = 0; j < n-i-1; j++)
				if (calcDist(arr[j]) > calcDist(arr[j+1]))
				{
					// swap arr[j+1] and arr[i]
					FieldCell temp = arr[j];
					arr[j] = arr[j+1];
					arr[j+1] = temp;
				}
	}

	private float calcDist(FieldCell to) {
		return BattleField.getInstance().calculateDistance(this.getPosition(), to);
	}

	private int nextRand(int min, int max) {
		return rand.nextInt(max + 1 -min) + min;
	}

	@Override
	public void wasAttacked(int damage, FieldCell source) {
		
	}

	@Override
	public void enemyKilled() {
	}

	@Override
	public void worldChanged(FieldCell oldCell, FieldCell newCell) {
	}
}
