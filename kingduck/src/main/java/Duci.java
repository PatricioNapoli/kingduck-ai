import java.util.ArrayList;

import ia.battle.core.BattleField;
import ia.battle.core.FieldCell;
import ia.battle.core.Warrior;
import ia.battle.core.WarriorData;
import ia.battle.core.actions.Action;
import ia.battle.core.actions.Attack;
import ia.exceptions.RuleException;

public class Duci extends Warrior {

	public Duci(String name, int health, int defense, int strength, int speed, int range) throws RuleException {
		super(name, health, defense, strength, speed, range);
	}
	
	public void checkSI() {
		
	}
	
	@Override
	public Action playTurn(long tick, int actionNumber) {
		WarriorData wd = BattleField.getInstance().getEnemyData();
		checkSI();
		
		
		if (wd.getInRange()) {
			return new Attack(wd.getFieldCell());
		}
		
		BattleField bf = BattleField.getInstance();
		
		WarriorData hd = bf.getHunterData();
		ArrayList<FieldCell> si = bf.getSpecialItems();
		
		
		return new AStar(this.getPosition());
	}

	@Override
	public void wasAttacked(int damage, FieldCell source) {
		
	}

	@Override
	public void enemyKilled() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void worldChanged(FieldCell oldCell, FieldCell newCell) {
		// TODO Auto-generated method stub
		
	}

}
