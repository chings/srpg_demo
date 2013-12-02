package cn.ching.srpg_demo.game.ui;

import cn.ching.srpg_demo.game.battle.Ability;
import cn.ching.srpg_demo.game.battle.Battler;
import cn.ching.srpg_demo.game.battle.CommandSource;

public class AbilityAction extends Action implements CommandSource {

	Ability ability;
	Battler actor;
	int nonce;
	int[] position;

	public AbilityAction(Battler actor, Ability ability) {
		this.actor = actor;
		this.nonce = 0;
		this.ability = ability;
	}

	public Ability getAbility() {
		return ability;
	}

	@Override
	public int getResource() {
		return ability.getResource();
	}

	@Override
	public int prePerform(int[] position) {
		int result = ability.check(actor, position);
		if(result >= 0) {
			this.position = position.clone();
		}
		return result;
	}

	@Override
	public void perform() {
		actor.setCommandSource(this);
	}

	@Override
	public void byPerform() {
		ability.assist(actor, position);
	}

	@Override
	public String toString() {
		return ability.getName();
	}

	@Override
	public boolean suggest(Battler battler) {
		if(actor != battler) {
			throw new IllegalStateException();
		}
		return ability.apply(battler, position, nonce++);
	}

}
