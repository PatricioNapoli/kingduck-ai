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
    private int spLimit = 5;

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
		try {
			WarriorData wd = battleField.getEnemyData();

			// Attack close enemy
			if (wd.getInRange())
				return new Attack(wd.getFieldCell());

			// After farming and not in range or items depleted, find enemy
			if ((spCount >= spLimit && !wd.getInRange()) || itemsDepleted)
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
		} catch (Exception e) { }

		WarriorData wd = battleField.getEnemyData();

		// Attack close enemy
		if (wd.getInRange())
			return new Attack(wd.getFieldCell());

		return new AStar(this.getPosition(), approachEnemy());
	}

	private boolean isStealth(FieldCell i) {
	    // COMENTARIO AGREGADO LUEGO DE TORNEO
        //
        // La intencion de este metodo es evitar que el warrior se quede trabado
        // La idea era compensar el bug de battlefield-ia, porque no sabia que iba
        // a ser arreglado a la hora del torneo.
        // Luego de ser arreglado antes de agregar los jars, lo deje porque el resultado
        // no hubiese cambiado. Si bien el bot usa una logica de spLimit, el limite se lo hubiese
        // subido para compensar por aquellas habilidades que no eran stat increase.
        // La intencion no es cheatear, sino que no se tenia el conocimiento de si el bug
        // seria arreglado, por ende programe el bot asumiendo que el bug estaria ahi.
        // Se puede remover el chequeo de isStealth() y subir el spLimit a 6, el resultado seria
        // el mismo. No considere hacerlo porque fue muy de ultimo momento.
        // Notese que este metodo no se usa para priorizar un stat o el otro, solo se usa para
        // evitar que el warrior se trabe. En un futuro, si se implementasen las habilidades,
        // la estrategia del bot cambiaria radicalmente.
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
