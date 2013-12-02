package cn.ching.srpg_demo.game.battle.abilities;

import java.util.List;

import cn.ching.srpg_demo.game.battle.Ability;
import cn.ching.srpg_demo.game.battle.AbilityInstruction;
import cn.ching.srpg_demo.game.battle.Battler;
import cn.ching.srpg_demo.game.battle.Command;
import cn.ching.srpg_demo.game.battle.Party;

public class Blasphemy extends Ability implements AbilityInstruction {

	@Override
	public boolean checkTarget(Battler caster, int[] position) {
		return true;
	}

	@Override
	public boolean apply(Battler caster, final int[] position) {
		final int healing = caster.getInt("spellPower", 0) + value(getInts("healing"));
		caster.take(new Command() {
			@Override
			public void execute(Battler taker) {
				taker.setOrientation(taker.getBattle().getBattleground().orientate(taker.getPosition(), position));
				motion = "cast";
			}
			@Override
			public void postExecute(Battler taker) {
				spentEnergyAndActivity(taker);
				for(Battler anyAlly: taker.getBattle().findEnclosedBattlers(position, getInt("radius"), taker.getParty(), true)) {
					anyAlly.modifyLife(healing);
				}
			}
		});
		int challenge = caster.getInt("spellPower", 0) + value(getInts("damage"));
		for(Battler anyFoe: caster.getBattle().findEnclosedBattlers(position, getInt("radius"), caster.getParty(), false)) {
			int defence = anyFoe.getInt("magicResistance", 0);
			int damage = Math.max(challenge - defence, 1);
			android.util.Log.i(this.getClass().getName(), String.format("Blasphemy: %d - %d = %d", challenge, defence, damage));
			anyFoe.take(new InjureCommand(damage, caster.getBattle().getBattleground().orientate(caster.getPosition(), anyFoe.getPosition())));
		}
		return false;
	}

	@Override
	public void assist(Battler caster, int[] position) {
		assistCircle(caster, position, getInt("radius"));
	}

	@Override
	public int[] selectCastingPosition(Battler caster) {
		int radius = getInt("radius");
		int crowd = 0;
		int[] position = null;
		for(Party party : caster.getBattle().getParties()) {
			for(Battler battler : party) {
				int[] currentPosition = battler.getPosition();
				List<Battler> battlers = caster.getBattle().findEnclosedBattlers(currentPosition, radius, null, false);
				if(crowd < battlers.size()) {
					crowd = battlers.size();
					position = currentPosition;
				}
			}
		}
		return position.clone();
	}

	@Override
	public int[] getCastingRange(Battler caster) {
		return getInts("range");
	}

}
