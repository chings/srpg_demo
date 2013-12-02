package cn.ching.srpg_demo.game.battle.abilities;

import cn.ching.srpg_demo.game.battle.Ability;
import cn.ching.srpg_demo.game.battle.Battler;
import cn.ching.srpg_demo.game.battle.Command;

public class Teleport extends Ability {

	@Override
	public boolean checkTarget(Battler caster, int[] position) {
		return checkSpace(caster, position);
	}

	@Override
	public boolean apply(Battler caster, final int[] position) {
		caster.take(new Command() {
			@Override
			public void execute(Battler taker) {
				motion = "cast";
			}
			@Override
			public void postExecute(Battler taker) {
				taker.getBattle().moveBattler(taker, position);
				spentEnergyAndActivity(taker);
			}
		});
		return false;
	}

}
