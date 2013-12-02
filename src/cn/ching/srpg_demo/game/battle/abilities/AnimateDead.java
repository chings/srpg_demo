package cn.ching.srpg_demo.game.battle.abilities;

import cn.ching.srpg_demo.R;

import cn.ching.srpg_demo.game.Resource;
import cn.ching.srpg_demo.game.battle.Ability;
import cn.ching.srpg_demo.game.battle.AbilityInstruction;
import cn.ching.srpg_demo.game.battle.Battler;
import cn.ching.srpg_demo.game.battle.Character;
import cn.ching.srpg_demo.game.battle.Command;
import cn.ching.srpg_demo.game.battle.ai.SimpleIntelligence;

public class AnimateDead extends Ability implements AbilityInstruction {

	@Override
	public boolean checkTarget(Battler caster, int[] position) {
		return checkSpace(caster, position);
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
		Battler minion = new Battler();
		minion.loadCharacter((Character)Resource.data.load(caster.getBoolean("necrophant", false) ? R.raw.character_skeleton_captain : R.raw.character_skeleton, new Character()));
		if(!caster.isPlayer()) {
			SimpleIntelligence intelligence = new SimpleIntelligence("default");
			minion.setIntelligence(intelligence);
		}
		minion.loadPosition(position);
		minion.loadOrientation(caster.getOrientation());
		minion.setEnergy(minion.getInt("maxEnergy"));
		minion.tick();
		caster.getBattle().addBattler(minion, caster.getParty(), position);
		return false;
	}

	@Override
	public int[] selectCastingPosition(Battler caster) {
		int[] range = getInts("range");
		return caster.getBattle().findEnclosedSpace(caster.getPosition(), range[range.length - 1]);
	}

	@Override
	public int[] getCastingRange(Battler caster) {
		return getInts("range");
	}

}
