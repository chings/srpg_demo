package cn.ching.srpg_demo.game.battle.abilities;

import java.util.ArrayList;
import java.util.List;

import cn.ching.srpg_demo.game.battle.Battler;
import cn.ching.srpg_demo.game.battle.Command;
import cn.ching.srpg_demo.game.battle.InstrumentAbility;
import cn.ching.srpg_demo.game.battle.Item;
import cn.ching.srpg_demo.game.battle.Weapon;

public class PreciseShot extends InstrumentAbility {

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
	public boolean checkTarget(Battler caster, int[] position) {
		return checkFoe(caster, position);
	}

	@Override
	public boolean checkRange(Battler caster, int[] position) {
		int[] range = getInstrument().getInts("range");
		range[range.length - 1] += getInt("rangeBonus");
		if(range != null) {
			if(!between(range, caster.getBattle().getBattleground().distance(caster.getPosition(), position))) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean apply(Battler caster, final int[] position) {
		final int orientation = caster.getBattle().getBattleground().orientate(caster.getPosition(), position);
		Battler foe = caster.getBattle().findBattler(position);
		int challenge = caster.getInt("attackPower", 0) + value(getInstrument().getInts("damage"));
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
		int defence = 0;
		int damage = Math.max(challenge - defence, 1);
		android.util.Log.i(this.getClass().getName(), String.format("PreciseShot: %d - %d = %d", challenge, defence, damage));
		foe.take(new InjureCommand(damage, orientation));
		return false;
	}

}
