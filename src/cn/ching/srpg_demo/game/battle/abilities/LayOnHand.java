package cn.ching.srpg_demo.game.battle.abilities;

import cn.ching.srpg_demo.game.battle.Ability;
import cn.ching.srpg_demo.game.battle.Battler;
import cn.ching.srpg_demo.game.battle.Command;

public class LayOnHand extends Ability {

	@Override
	public boolean checkTarget(Battler caster, int[] position) {
		return checkAlly(caster, position);
	}

	@Override
	public boolean apply(Battler caster, final int[] position) {
		final Battler ally = caster.getBattle().findBattler(position);
		final int healing = caster.getInt("spellPower", 0) + value(getInts("healing"));
		final int subEffect = (int)(healing * 0.5);
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
				if(taker.getBoolean("holyRadiation", false)) {
					for(Battler anyAlly: taker.getBattle().findEnclosedBattlers(position, 1, taker.getParty(), true)) {
						if(anyAlly != ally) {
							many = anyAlly.modifyLife(subEffect);
							anyAlly.attachLifeTip(many, false);
						}
					}
				}
				spentEnergyAndActivity(taker);
			}
		});
		if(caster.getBoolean("holyRadiation", false)) {
			for(Battler anyFoe: caster.getBattle().findEnclosedBattlers(position, 1, caster.getParty(), false)) {
				int defence = anyFoe.getInt("magicResistance", 0);
				int damage = Math.max(subEffect - defence, 1);
				android.util.Log.i(this.getClass().getName(), String.format("HolyRadiation(LayOnHand): %d - %d = %d", subEffect, defence, damage));
				anyFoe.take(new InjureCommand(damage, caster.getBattle().getBattleground().orientate(caster.getPosition(), anyFoe.getPosition())));
			}
		}
		return false;
	}

	@Override
	public void assist(Battler caster, int[] position) {
		if(caster.getBoolean("holyRadiation", false)) {
			assistCircle(caster, position, 1);
		}
	}

}
