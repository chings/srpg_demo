

package cn.ching.srpg_demo.game.battle.abilities;

import cn.ching.srpg_demo.game.battle.Ability;
import cn.ching.srpg_demo.game.battle.Battleground;
import cn.ching.srpg_demo.game.battle.Battler;
import cn.ching.srpg_demo.game.battle.Command;

public class ChainLightning extends Ability {

	@Override
	public int check(Battler caster, int[] position) {
		boolean outOfEnergy = !checkEnergyCost(caster) || !checkActivityCost(caster);
		int[] a = new int[2];
		Battler lastTarget = null;
		for(int i = 0; i <= position.length - 2; i += 2) {
			a[0] = position[i];
			a[1] = position[i + 1];
			Battler target = caster.getBattle().findBattler(a);
			if(target == null || caster.getParty() == target.getParty() || target == lastTarget) {
				return INVALID_TARGET;
			}
			if(outOfEnergy) {
				return OUT_OF_ENERGY;
			}
			if(lastTarget == null) {
				if(!checkRange(caster, position)) {
					return OUT_OF_RANGE;
				}
			} else {
				int[] range = getInts("subRange");
				if(range != null) {
					if(!between(range, caster.getBattle().getBattleground().distance(target.getPosition(), lastTarget.getPosition()))) {
						return OUT_OF_RANGE;
					}
				}
			}
			lastTarget = target;
		}
		if(!checkTargetNumber(caster, position)) {
			return LESS_OF_TARGET;
		}
		return READY;
	}

	@Override
	public boolean apply(Battler caster, int[] position) {
		Battleground battleground = caster.getBattle().getBattleground();
		final int orientation = battleground.orientate(caster.getPosition(), position);
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
		int fullChallenge = caster.getInt("spellPower", 0) + value(getInts("damage"));
		float reduction = getFloat("reduction", 0);
		float factor = 1f;
		int[] a = new int[2];
		int[] start = caster.getSprite().getDestAnchor("hands").clone();
		int[] end;
		for(int i = 0; i <= position.length - 2; i += 2) {
			a[0] = position[i];
			a[1] = position[i + 1];
			final Battler foe = caster.getBattle().findBattler(a);
			end = foe.getSprite().getDestAnchor("body").clone();
			boolean overloaded = random.nextDouble() < caster.getFloat("lightningOverload", 0);
			int challenge = overloaded ? (int)(fullChallenge * factor * 1.5) : (int)(fullChallenge * factor);
			int defence = foe.getInt("magicResistance", 0);
			final int damage = Math.max(challenge - defence, 1);
			android.util.Log.i(this.getClass().getName(), String.format("ChainLightning: %d - %d = %d", challenge, defence, damage));
			factor -= reduction;
			caster.attach(new ChainLightningSprite(start[0], start[1], end[0], end[1], overloaded ? 2 : 1) {
				@Override
				public void onFade(Battler owner) {
					foe.take(new InjureCommand(damage, owner.getBattle().getBattleground().orientate(owner.getPosition(), foe.getPosition())));
				}
			});
			start[0] = end[0];
			start[1] = end[1];
		}
		return false;
	}

}
