package cn.ching.srpg_demo.game.battle.abilities;

import cn.ching.srpg_demo.game.battle.Battler;
import cn.ching.srpg_demo.game.battle.Command;
import cn.ching.srpg_demo.game.battle.InstrumentAbility;

public class FireBolt extends InstrumentAbility {

	@Override
	public boolean checkTarget(Battler caster, int[] position) {
		return checkFoe(caster, position);
	}

	@Override
	public boolean apply(Battler caster, int[] position) {
		final int orientation = caster.getBattle().getBattleground().orientate(caster.getPosition(), position);
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
		Battler foe = caster.getBattle().findBattler(position);
		int challenge = caster.getInt("spellPower", 0) + value(getInstrument().getInts("damage"));
		int defence = foe.getInt("magicResistance", 0);
		int damage = Math.max(challenge - defence, 1);
		android.util.Log.i(this.getClass().getName(), String.format("FireBolt: %d - %d = %d", challenge, defence, damage));
		foe.take(new InjureCommand(damage, orientation));
		return false;
	}

}
