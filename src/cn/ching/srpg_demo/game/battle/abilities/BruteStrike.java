package cn.ching.srpg_demo.game.battle.abilities;

import java.util.ArrayList;
import java.util.List;

import cn.ching.srpg_demo.game.battle.AbilityInstruction;
import cn.ching.srpg_demo.game.battle.Battler;
import cn.ching.srpg_demo.game.battle.Command;
import cn.ching.srpg_demo.game.battle.InstrumentAbility;
import cn.ching.srpg_demo.game.battle.Item;
import cn.ching.srpg_demo.game.battle.Weapon;

public class BruteStrike extends InstrumentAbility implements AbilityInstruction {

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
		int challenge = (int)(caster.getInt("attackPower", 0) * getFloat("enhancement", 1.0f)) + value(getInstrument().getInts("damage"));
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
				spentEnergyAndActivity(taker);
			}
		});
		Battler foe = caster.getBattle().findBattler(position);
		int defence = foe.getInt("armorClass", 0);
		int damage = Math.max(challenge - defence, 1);
		android.util.Log.i(this.getClass().getName(), String.format("BruteStrike: %d - %d = %d", challenge, defence, damage));
		foe.take(new InjureCommand(damage, caster.getBattle().getBattleground().orientate(caster.getPosition(), foe.getPosition())));
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
