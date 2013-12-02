package cn.ching.srpg_demo.game.battle.abilities;

import cn.ching.srpg_demo.game.battle.AbilityInstruction;
import cn.ching.srpg_demo.game.battle.Battler;
import cn.ching.srpg_demo.game.battle.Command;
import cn.ching.srpg_demo.game.battle.InstrumentAbility;

public class Attack extends InstrumentAbility implements AbilityInstruction {

	@Override
	public boolean checkTarget(Battler caster, int[] position) {
		return checkFoe(caster, position);
	}

	@Override
	public boolean apply(Battler caster, int[] position) {
		final int orientation = caster.getBattle().getBattleground().orientate(caster.getPosition(), position);
		Battler foe = caster.getBattle().findBattler(position);
		boolean critical = caster.getBoolean("backstabbing", false) && usingMeleeWeapon() && orientation == foe.getOrientation();
		int challenge = caster.getInt("attackPower", 0) + (critical ? max(getInstrument().getInts("damage")) : value(getInstrument().getInts("damage")));
		int defence = foe.getInt("armorClass", 0);
		int damage = Math.max(challenge - defence, 1);
		android.util.Log.i(this.getClass().getName(), String.format("Attack: %d - %d = %d", challenge, defence, damage));
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

	@Override
	public int[] selectCastingPosition(Battler caster) {
		Battler battler = caster.getBattle().findClosestBattler(caster.getPosition(), caster.getParty(), false);
		return battler != null ? battler.getPosition() : null;
	}

	@Override
	public int[] getCastingRange(Battler caster) {
		return getInstrument().getInts("range");
	}

}
