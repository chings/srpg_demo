

package cn.ching.srpg_demo.game.battle.abilities;

import java.util.ArrayList;
import java.util.List;

import cn.ching.srpg_demo.game.battle.Battleground;
import cn.ching.srpg_demo.game.battle.Battler;
import cn.ching.srpg_demo.game.battle.Command;
import cn.ching.srpg_demo.game.battle.InstrumentAbility;
import cn.ching.srpg_demo.game.battle.Item;
import cn.ching.srpg_demo.game.battle.Weapon;

public class Multishot extends InstrumentAbility {

	@Override
	public List<Item> findInstruments(Battler battler) {
		List<Item> instruments = new ArrayList<Item>();
		for(Weapon weapon : battler.getWeapons()) {
			if(weapon.isRanged()) {
				instruments.add(weapon);
				break;
			}
		}
		return instruments;
	}

	@Override
	public int check(Battler caster, int[] position) {
		boolean outOfEnergy = !checkEnergyCost(caster) || !checkActivityCost(caster);
		int[] a = new int[2];
		Battler firstTarget = null;
		for(int i = 0; i <= position.length - 2; i += 2) {
			a[0] = position[i];
			a[1] = position[i + 1];
			Battler target = caster.getBattle().findBattler(a);
			if(target == null || caster.getParty() == target.getParty()) {
				return INVALID_TARGET;
			}
			if(outOfEnergy) {
				return OUT_OF_ENERGY;
			}
			if(firstTarget == null) {
				if(!checkRange(caster, position)) {
					return OUT_OF_RANGE;
				}
				firstTarget = target;
			} else {
				int[] range = getInts("subRange");
				if(range != null) {
					if(!between(range, caster.getBattle().getBattleground().distance(target.getPosition(), firstTarget.getPosition()))) {
						return OUT_OF_RANGE;
					}
				}
			}
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
				motion = "attack";
			}
			@Override
			public void postExecute(Battler taker) {
				spentEnergyAndActivity(taker);
			}
		});
		int challenge = (int)((caster.getInt("attackPower", 0) * 0.5) + value(getInstrument().getInts("damage")));
		int[] a = new int[2];
		for(int i = 0; i <= position.length - 2; i += 2) {
			a[0] = position[i];
			a[1] = position[i + 1];
			final Battler foe = caster.getBattle().findBattler(a);
			int defence = foe.getInt("armorClass", 0);
			final int damage = Math.max(challenge - defence, 1);
			android.util.Log.i(this.getClass().getName(), String.format("Multishot: %d - %d = %d", challenge, defence, damage));
			foe.take(new InjureCommand(damage, caster.getBattle().getBattleground().orientate(caster.getPosition(), foe.getPosition())));
		}
		return false;
	}

}
