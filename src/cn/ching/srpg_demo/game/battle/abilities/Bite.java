package cn.ching.srpg_demo.game.battle.abilities;

import cn.ching.srpg_demo.game.battle.Ability;
import cn.ching.srpg_demo.game.battle.Battler;
import cn.ching.srpg_demo.game.battle.Command;

public class Bite extends Ability {

	@Override
	public boolean checkTarget(Battler caster, int[] position) {
		return checkFoe(caster, position);
	}

	@Override
	public boolean apply(Battler caster, int[] position) {
		final int orientation = caster.getBattle().getBattleground().orientate(caster.getPosition(), position);
		Battler foe = caster.getBattle().findBattler(position);
		boolean critical = caster.getBoolean("backstabbing", false) && orientation == foe.getOrientation();
		int challenge = caster.getInt("attackPower", 0) + (critical ? max(getInts("damage")) : value(getInts("damage")));
		int defence = foe.getInt("armorClass", 0);
		int damage = Math.max(challenge - defence, 1);
		android.util.Log.i(this.getClass().getName(), String.format("Bite: %d - %d = %d", challenge, defence, damage));
		caster.take(new Command() {
			@Override
			public void execute(Battler taker) {
				taker.setOrientation(orientation);
				motion = "attack";
			}
			@Override
			public void postExecute(Battler taker) {
				spentEnergyAndActivity(taker);
			}
		});
		foe.take(new InjureCommand(damage, orientation, critical));
		return false;
	}

}
