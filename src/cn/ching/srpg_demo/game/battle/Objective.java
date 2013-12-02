package cn.ching.srpg_demo.game.battle;

import java.util.Collection;
import java.util.HashSet;

public class Objective {

	Boolean victory;

	Collection<Battler> goodBattlers;
	Party evilBattlers;

	public Objective() {
		goodBattlers = new HashSet<Battler>();
		victory = null;
	}

	public Boolean getVictory() {
		return victory;
	}

	public void addGoodBattler(Battler battler) {
		goodBattlers.add(battler);
	}

	public void setEvilBattlers(Party evilBattlers) {
		this.evilBattlers = evilBattlers;
	}

	public void checkFallenBattler(Battler fallenBattler) {
		if(goodBattlers.contains(fallenBattler)) {
			victory = false;
		}
	}

	public void checkFallenParty(Party fallenParty) {
		if(evilBattlers == fallenParty) {
			victory = true;
		}
	}

}
