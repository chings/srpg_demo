package cn.ching.srpg_demo.game.battle.abilities;

import java.util.ArrayList;
import java.util.List;

import cn.ching.srpg_demo.game.battle.Battler;
import cn.ching.srpg_demo.game.battle.Command;
import cn.ching.srpg_demo.game.battle.InstrumentAbility;
import cn.ching.srpg_demo.game.battle.Item;
import cn.ching.srpg_demo.game.battle.Weapon;

public class SmiteEvil extends InstrumentAbility {

	@Override
	public List<Item> findInstruments(Battler battler) {
		List<Item> instruments = new ArrayList<Item>();
		for(Weapon weapon : battler.getWeapons()) {
			if(weapon.isMelee()) {
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
	public boolean apply(Battler caster, final int[] position) {
		int challenge = caster.getInt("attackPower", 0) + caster.getInt("spellPower", 0) + value(getInstrument().getInts("damage"));
		final int subEffect = (int)(challenge * 0.5);
		final int orientation = caster.getBattle().getBattleground().orientate(caster.getPosition(), position);
		caster.take(new Command() {
			@Override
			public void execute(Battler taker) {
				if(orientation != 0) {
					taker.setOrientation(orientation);
				}
				interpolator = new JumpInterpolator(20);
				motion = "attack";
			}
			@Override
			public void postExecute(Battler taker) {
				if(taker.getBoolean("holyRadiation", false)) {
					for(Battler anyAlly: taker.getBattle().findEnclosedBattlers(position, 1, taker.getParty(), true)) {
						int many = anyAlly.modifyLife(subEffect);
						anyAlly.attachLifeTip(many, false);
					}
				}
				spentEnergyAndActivity(taker);
			}
		});
		Battler foe = caster.getBattle().findBattler(position);
		int defence = Math.min(foe.getInt("armorClass", 0), foe.getInt("magicResistance", 0));
		int damage = challenge - defence;
		android.util.Log.i(this.getClass().getName(), String.format("SmiteEvil: %d - %d = %d", challenge, defence, damage));
		foe.take(new InjureCommand(damage, caster.getBattle().getBattleground().orientate(caster.getPosition(), foe.getPosition())));
		for(Battler anyFoe : caster.getBattle().findEnclosedBattlers(position, 1, caster.getParty(), false)) {
			if(anyFoe != foe) {
				defence = anyFoe.getInt("magicResistance", 0);
				damage = Math.max(subEffect - defence, 1);
				android.util.Log.i(this.getClass().getName(), String.format("HolyRadiation(SmiteEvil): %d - %d = %d", challenge, defence, damage));
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
