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
		int maxPoints = ConfigurationManager.getInstance().getMaxPointsPerWarrior();

		return new Duci("Duci", 50, 25, 25, 50, 50);
	}

}
