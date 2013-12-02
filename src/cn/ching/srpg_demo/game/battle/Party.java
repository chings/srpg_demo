package cn.ching.srpg_demo.game.battle;

import java.util.ArrayList;
import java.util.Iterator;

import cn.ching.srpg_demo.game.core.EventListener;

@SuppressWarnings("serial")
public class Party extends ArrayList<Battler> implements EventListener {

	BattleScene battle;

	public Party() {

	}

	public void setBattle(BattleScene battle) {
		this.battle = battle;
	}

	@Override
	public boolean add(Battler battler) {
		boolean r = super.add(battler);
		battler.party = this;
		return r;
	}

	@Override
	public boolean onEvent(Object... event) {
		return false;
	}

	public void onMemberFall(Battler fallenBattler) {
		for(Iterator<Battler> it = this.iterator(); it.hasNext(); ) {
			Battler battler = it.next();
			if(battler.isFallen()) {
				it.remove();
			}
		}
		if(this.isEmpty()) {
			battle.onPartyFall(this);
		}
	}

}
