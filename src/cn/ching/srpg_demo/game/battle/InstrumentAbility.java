package cn.ching.srpg_demo.game.battle;

import java.util.ArrayList;
import java.util.List;

public abstract class InstrumentAbility extends Ability {

	protected List<Item> instruments;

	public InstrumentAbility() {
		super();
		instruments = new ArrayList<Item>(1);
	}

	public Item getInstrument() {
		return !instruments.isEmpty() ? instruments.get(0) : null;
	}

	public void setInstrument(Item instrument) {
		if(instruments.isEmpty()) {
			instruments.add(instrument);
		} else {
			instruments.set(0, instrument);
		}
	}

	public List<Item> getInstruments() {
		return instruments;
	}

	public void setInstruments(List<Item> instruments) {
		this.instruments = instruments;
	}

	public List<Item> findInstruments(Battler battler) {
		return instruments;
	}

	public boolean checkInstrument(Battler caster) {
		return !instruments.isEmpty();
	}

	@Override
	public boolean checkRange(Battler caster, int[] position) {
		int[] range = getInstrument().getInts("range");
		if(range != null) {
			if(!between(range, caster.getBattle().getBattleground().distance(caster.getPosition(), position))) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int check(Battler caster, int[] position) {
		if(!checkInstrument(caster)) {
			return UNQUALIFIED;
		}
		return super.check(caster, position);
	}

	public boolean usingMeleeWeapon() {
		Item item = getInstrument();
		return item != null && (item instanceof Weapon) && ((Weapon)item).isMelee();
	}

}
