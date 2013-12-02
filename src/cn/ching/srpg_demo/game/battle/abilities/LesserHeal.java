package cn.ching.srpg_demo.game.battle.abilities;

import cn.ching.srpg_demo.game.battle.Battler;
import cn.ching.srpg_demo.game.battle.Command;
import cn.ching.srpg_demo.game.battle.InstrumentAbility;

public class LesserHeal extends InstrumentAbility {

	@Override
	public boolean checkTarget(Battler caster, int[] position) {
		return checkAlly(caster, position);
	}

	@Override
	public boolean apply(Battler caster, int[] position) {
		final Battler ally = caster.getBattle().findBattler(position);
		final int healing = caster.getInt("spellPower", 0) + value(getInstrument().getInts("healing"));
		final int orientation = caster.getBattle().getBattleground().orientate(caster.getPosition(), position);
		caster.take(new Command() {
			@Override
			public void execute(Battler taker) {
				if(orientation != 0) {
					taker.setOrientation(orientation);
				}
				motion = "cast";
			}
			@Override
			public void postExecute(Battler taker) {
				int many = ally.modifyLife(healing);
				ally.attachLifeTip(many, false);
				spentEnergyAndActivity(taker);
			}
		});
		return false;
	}

}
