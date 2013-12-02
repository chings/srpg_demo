package cn.ching.srpg_demo.game.battle.abilities;

import cn.ching.srpg_demo.game.battle.Ability;
import cn.ching.srpg_demo.game.battle.Battler;
import cn.ching.srpg_demo.game.battle.Command;

public class IceBlast extends Ability {

	@Override
	public boolean checkTarget(Battler caster, int[] position) {
		return true;
	}

	@Override
	public boolean apply(Battler caster, int[] position) {
		final int orientation = caster.getBattle().getBattleground().orientate(caster.getPosition(), position);
		caster.take(new Command() {
			@Override
			public void execute(Battler taker) {
				taker.setOrientation(orientation);
				motion = "cast";
			}
			@Override
			public void postExecute(Battler taker) {
				spentEnergyAndActivity(taker);
			}
		});
		int challenge = caster.getInt("spellPower", 0) + value(getInts("damage"));
		final int slowness = getInt("slowness", 0);
		for(Battler foe : caster.getBattle().findEnclosedBattlers(position, getInt("radius"), caster.getParty(), false)) {
			int defence = foe.getInt("magicResistance", 0);
			int damage = Math.max(challenge - defence, 1);
			android.util.Log.i(this.getClass().getName(), String.format("IceBlast: %d - %d = %d", challenge, defence, damage));
			foe.take(new InjureCommand(damage, caster.getBattle().getBattleground().orientate(caster.getPosition(), foe.getPosition())) {
				@Override
				public void postExecute(Battler taker) {
					super.postExecute(taker);
					taker.modifyActivity(-slowness);
				}
			});
		}
		return false;
	}

	@Override
	public void assist(Battler caster, int[] position) {
		assistCircle(caster, position, getInt("radius"));
	}

}
