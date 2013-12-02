package cn.ching.srpg_demo.game.battle.abilities;

import java.util.Arrays;

import cn.ching.srpg_demo.game.battle.Ability;
import cn.ching.srpg_demo.game.battle.Battleground;
import cn.ching.srpg_demo.game.battle.Battler;
import cn.ching.srpg_demo.game.battle.Command;

public class Fireball extends Ability {

	@Override
	public boolean checkTarget(Battler caster, int[] position) {
		return checkFoe(caster, position);
	}

	@Override
	public boolean apply(Battler caster, final int[] position) {
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
		int[] start = caster.getSprite().getDestAnchor("hands").clone();
		int[] end = caster.getBattle().findBattler(position).getSprite().getDestAnchor("body").clone();
		caster.attach(new FireballSprite(start[0], start[1], end[0], end[1]) {
			@Override
			public void onFade(Battler owner) {
				int fullChallenge = owner.getInt("spellPower", 0) + value(getInts("damage"));
				int subChallenge = (int)(fullChallenge * 0.75);
				for(Battler foe : owner.getBattle().findEnclosedBattlers(position, getInt("radius"), owner.getParty(), false)) {
					int challenge = Arrays.equals(position, foe.getPosition()) ? fullChallenge : subChallenge;
					int defence = foe.getInt("magicResistance", 0);
					int damage = Math.max(challenge - defence, 1);
					android.util.Log.i(this.getClass().getName(), String.format("Fireball: %d - %d = %d", challenge, defence, damage));
					foe.take(new InjureCommand(damage, owner.getBattle().getBattleground().orientate(owner.getPosition(), foe.getPosition())));
				}
			}
		});
		return false;
	}

	@Override
	public void assist(Battler caster, int[] position) {
		assistCircle(caster, position, getInt("radius"));
	}

}
