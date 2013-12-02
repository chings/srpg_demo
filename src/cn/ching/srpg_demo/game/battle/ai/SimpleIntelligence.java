package cn.ching.srpg_demo.game.battle.ai;

import java.util.Iterator;
import java.util.List;
import java.util.Random;


import cn.ching.srpg_demo.game.battle.Ability;
import cn.ching.srpg_demo.game.battle.AbilityInstruction;
import cn.ching.srpg_demo.game.battle.Battler;
import cn.ching.srpg_demo.game.battle.EndTurnCommand;
import cn.ching.srpg_demo.game.battle.MoveCommand;
import cn.ching.srpg_demo.game.map.AStar;
import cn.ching.srpg_demo.game.map.AStar.Path;
import cn.ching.srpg_demo.game.ui.AbilityAction;

public class SimpleIntelligence implements Intelligence {

	static final Random random = new Random();

	String scheme;

	public SimpleIntelligence(String scheme) {
		this.scheme = scheme;
	}

	@Override
	public boolean suggest(Battler battler) {
		List<Ability> abilities = battler.getAllAbilities();
		do {
			if(abilities.isEmpty()) {
				break;
			}
			int choice = random.nextInt(abilities.size());
			Ability ability = abilities.get(choice);
			if(!(ability instanceof AbilityInstruction)) {
				abilities.remove(choice);
				continue;
			}
			AbilityInstruction instruction = (AbilityInstruction)ability;
			int[] position = instruction.selectCastingPosition(battler);
			if(position == null) {
				abilities.remove(choice);
				continue;
			}
			AbilityAction abilityAction = new AbilityAction(battler, ability);
			int result = abilityAction.prePerform(position);
			if(result == Ability.READY) {
				battler.setCommandSource(abilityAction);
				return true;
			}
			if(result == Ability.OUT_OF_RANGE) {
				int[] range = instruction.getCastingRange(battler);
				int max = range[range.length - 1];
				int distance = battler.getBattle().getBattleground().distance(battler.getPosition(), position);
				if(distance > max) {
					Path path = AStar.findPath(battler.getBattle().getBattlegroundSituation(battler), battler.getPosition(), position, max);
					if(path != null) {
						Iterator<int[]> it = path.positions.iterator();
						int[] position0 = it.next();
						int[] position1 = null;
						for(; it.hasNext(); ) {
							position1 = it.next();
							battler.take(new MoveCommand(position0, position1));
							position0 = position1;
						}
						battler.setCommandSource(abilityAction);
						return true;
					}
				}
			}
			abilities.remove(choice);
		} while(true);
		battler.take(new EndTurnCommand());
		return true;
	}

}
