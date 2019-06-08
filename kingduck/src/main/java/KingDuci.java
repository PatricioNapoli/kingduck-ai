import ia.battle.core.ConfigurationManager;
import ia.battle.core.Warrior;
import ia.battle.core.WarriorManager;
import ia.exceptions.RuleException;

public class KingDuci extends WarriorManager {

	@Override
	public String getName() {
		return "King Duci";
	}

	@Override
	public Warrior getNextWarrior() throws RuleException {
		ConfigurationManager mgr = ConfigurationManager.getInstance();
		int maxPoints = mgr.getMaxPointsPerWarrior();

		int range = mgr.getMaxRangeForWarrior();
		maxPoints = maxPoints - range;

		return new Duci("Duci",
				(int)Math.round(maxPoints * 0.35),
				(int)Math.round(maxPoints * 0.15),
				(int)Math.round(maxPoints * 0.25),
				(int)Math.round(maxPoints * 0.25),
				range);
	}

}
