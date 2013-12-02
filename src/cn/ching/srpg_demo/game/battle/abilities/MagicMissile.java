package cn.ching.srpg_demo.game.battle.abilities;

import cn.ching.srpg_demo.game.battle.Ability;
import cn.ching.srpg_demo.game.battle.Battleground;
import cn.ching.srpg_demo.game.battle.Battler;
import cn.ching.srpg_demo.game.battle.Command;

public class MagicMissile extends Ability {

	@Override
	public boolean checkTarget(Battler caster, int[] position) {
		return checkFoe(caster, position);
	}

	@Override
	public int check(Battler caster, int[] position) {
		int result = 0;
		int[] a = new int[2];
		for(int i = 0; i <= position.length - 2; i += 2) {
			a[0] = position[i];
			a[1] = position[i + 1];
			result = super.check(caster, a);
			if(result != READY) {
				return result;
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
				motion = "cast";
			}
			@Override
			public void postExecute(Battler taker) {
				spentEnergyAndActivity(taker);
			}
		});
		final int[] a = new int[2];
		int[] start = caster.getSprite().getDestAnchor("hands").clone();
		int angle = 120;
		for(int i = 0; i <= position.length - 2; i += 2) {
			a[0] = position[i];
			a[1] = position[i + 1];
			final Battler foe = caster.getBattle().findBattler(a);
			int[] end = foe.getSprite().getDestAnchor("body").clone();
			caster.attach(new MagicMissileSprite(start[0], start[1], end[0], end[1], angle) {
				@Override
				public void onFade(Battler owner) {
					if(!foe.isFallen()) {
						int challenge = value(getInts("damage"));
						int defence = 0;
						final int damage = challenge - defence;
						android.util.Log.i(this.getClass().getName(), String.format("MagicMissile: %d - %d = %d", challenge, defence, damage));
						foe.take(new InjureCommand(damage, owner.getBattle().getBattleground().orientate(owner.getPosition(), foe.getPosition())));
					}
				}
			});
			angle = angle < 60 ? angle - 60 + 360 : angle - 60;
		}
		return false;
	}

}
