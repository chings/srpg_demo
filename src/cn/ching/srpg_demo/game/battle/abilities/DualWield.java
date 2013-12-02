package cn.ching.srpg_demo.game.battle.abilities;

import java.util.ArrayList;
import java.util.List;

import cn.ching.srpg_demo.game.battle.Battler;
import cn.ching.srpg_demo.game.battle.Command;
import cn.ching.srpg_demo.game.battle.InstrumentAbility;
import cn.ching.srpg_demo.game.battle.Item;
import cn.ching.srpg_demo.game.battle.Weapon;

public class DualWield extends InstrumentAbility {

	@Override
	public List<Item> findInstruments(Battler battler) {
		List<Item> instruments = new ArrayList<Item>();
		for(Weapon weapon : battler.getWeapons()) {
			if(weapon.isMelee()) {
				instruments.add(weapon);
			}
		}
		return instruments;
	}

	@Override
	public boolean checkInstrument(Battler caster) {
		return instruments.size() >= 2;
	}

	@Override
	public boolean checkTarget(Battler caster, int[] position) {
		return checkFoe(caster, position);
	}

	@Override
	public boolean apply(Battler caster, int[] position, int nonce) {
		Battler foe = caster.getBattle().findBattler(position);
		final int orientation = caster.getBattle().getBattleground().orientate(caster.getPosition(), position);
		boolean critical = caster.getBoolean("backstabbing", false) && orientation == foe.getOrientation();
		if(nonce == 0) {
			Weapon weapon = (Weapon)instruments.get(nonce);
			int challenge = caster.getInt("attackPower", 0) + (critical ? max(weapon.getInts("damage")) : value(weapon.getInts("damage")));
			int defence = foe.getInt("armorClass", 0);
			int damage = Math.max(challenge - defence, 1);
			android.util.Log.i(this.getClass().getName(), String.format("DualWield: %d - %d = %d", challenge, defence, damage));
			caster.take(new Command() {
				@Override
				public void execute(Battler taker) {
					taker.setOrientation(orientation);
					motion = "attack";
				}
			});
			foe.take(new InjureCommand(damage, orientation, critical));
			return true;
		} else if(nonce == 1) {
			if(foe == null || foe.isFallen()) {
				spentEnergyAndActivity(caster);
				return false;
			}
			Weapon weapon = (Weapon)instruments.get(nonce);
			int challenge = caster.getInt("attackPower", 0) + (critical ? max(weapon.getInts("damage")) : value(weapon.getInts("damage")));
			int defence = foe.getInt("armorClass", 0);
			int damage = Math.max(challenge - defence, 1);
			android.util.Log.i(this.getClass().getName(), String.format("DualWield: %d - %d = %d", challenge, defence, damage));
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
		}
		return false;
	}

	@Override
	public boolean apply(Battler caster, int[] position) {
		return false;
	}

}
