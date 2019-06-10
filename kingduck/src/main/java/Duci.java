import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import ia.battle.core.*;
import ia.battle.core.abilities.Ability;
import ia.battle.core.actions.Action;
import ia.battle.core.actions.Attack;
import ia.battle.core.actions.Suicide;
import ia.exceptions.RuleException;

public class Duci extends Warrior {
    private BattleField battleField;

    private int spCount;
    private int spLimit = 3;

    private boolean suicide;

    private boolean itemsDepleted;

	public Duci(String name, int health, int defense, int strength, int speed, int range) throws RuleException {
		super(name, health, defense, strength, speed, range);

		this.battleField = BattleField.getInstance();
	}

    /**
     * Strategy is to farm until strong enough, then approach and kill enemy.
     * When enemy is killed, go into a frenzy, and reduce farm limit, in order
     * to prevent enemy warriors to farm.
     *
     * Ignores building walls, because you can attack over them anyways and implementing
     * a wall block strategy is... complicated.
     *
     * @param tick ignored, irrelevant for strategy
     * @param actionNumber ignored, irrelevant for strategy
     * @return victory
     */
	@Override
	public Action playTurn(long tick, int actionNumber) {
		if (suicide)
			return new Suicide();

		WarriorData wd = battleField.getEnemyData();

		if (wd.getInRange() && this.getHealth() <= 10) {
			suicide = true;
			return new AStar(this.getPosition(), approachEnemy());
		}

		// Attack close enemy
		if (wd.getInRange())
			return new Attack(wd.getFieldCell());

		// After farming and not in range or items depleted, find enemy
		if ((spCount >= spLimit && !wd.getInRange()) || itemsDepleted && !wd.getInRange())
		    return new AStar(this.getPosition(), approachEnemy());

		ArrayList<FieldCell> si = battleField.getSpecialItems();
        ArrayList<FieldCell> items = new ArrayList<>();

        // Check if field cell contains stealth ability, ignore those
        for(FieldCell i : si) {
            if (!isStealth(i))
            	items.add(i);
        }

        // Prioritize closest loot
		if (items.size() > 0) {
			FieldCell[] arr = items.toArray(new FieldCell[0]);
			Arrays.sort(arr, new CellDistanceComparator());

			spCount++;

            return new AStar(this.getPosition(), arr[0]);
		}

        return new AStar(this.getPosition(), approachFarItem());
	}

	private boolean isStealth(FieldCell i) {
		try {
			Method m = i.getClass().getDeclaredMethod("getItem");
			m.setAccessible(true);
			Object ability = m.invoke(i);

			if ((ability instanceof Ability))
				return true;
		} catch (Exception e) {
			return true;
		}

		return false;
	}

    private FieldCell approachFarItem() {
		List<FieldCell> cells = new ArrayList<>();

		for(FieldCell[] row : battleField.getMap()) {
			for(FieldCell c : row) {
				if (c.hasItem() && !isStealth(c))
					cells.add(c);
			}
		}

		if (cells.size() == 0) {
			itemsDepleted = true;
			return approachEnemy();
		}

		FieldCell[] arr = cells.toArray(new FieldCell[0]);
		Arrays.sort(arr, new CellDistanceComparator());

        return arr[0];
    }

    private FieldCell approachEnemy() {
        return battleField.getEnemyData().getFieldCell();
    }

	private float calcDist(FieldCell to) {
		return battleField.calculateDistance(this.getPosition(), to);
	}

	@Override
	public void enemyKilled() {
		// Only go frenzy if farmed
		if (spCount >= spLimit) {
			spCount = 0;
			spLimit = 1;
		}
	}

	@Override
	public void wasAttacked(int damage, FieldCell source) {
	    // Meh
	}

	@Override
	public void worldChanged(FieldCell oldCell, FieldCell newCell) {
		// Meh
	}

	public class CellDistanceComparator implements Comparator<FieldCell> {

		@Override
		public int compare(FieldCell o1, FieldCell o2) {
			return Float.compare(calcDist(o1), calcDist(o2));
		}

		@Override
		public boolean equals(Object obj) {
			return false;
		}
	}
}
