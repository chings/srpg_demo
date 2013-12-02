

package cn.ching.srpg_demo.game.battle.abilities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.ching.srpg_demo.game.battle.Battleground;
import cn.ching.srpg_demo.game.battle.Battler;
import cn.ching.srpg_demo.game.battle.BattlerSprite;
import cn.ching.srpg_demo.game.battle.Command;
import cn.ching.srpg_demo.game.battle.InstrumentAbility;
import cn.ching.srpg_demo.game.battle.Item;
import cn.ching.srpg_demo.game.battle.MoveCommand;
import cn.ching.srpg_demo.game.battle.MoveInterpolator;
import cn.ching.srpg_demo.game.battle.Tile;
import cn.ching.srpg_demo.game.battle.Weapon;

public class Charge extends InstrumentAbility {

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
	public boolean checkRange(Battler caster, int[] position) {
		Battleground battleground = caster.getBattle().getBattleground();
		List<int[]> positions = battleground.lineRegion(caster.getPosition(), position);
		if(positions == null) {
			return false;
		}
		int[] range = getInts("range");
		if(range != null) {
			if(!between(range, positions.size())) {
				return false;
			}
		}
		for(int i = positions.size() - 2; i > 0; i--) {
			Tile tile = battleground.getTile(positions.get(i));
			if(tile.isOccupied()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean apply(Battler caster, final int[] position) {
		final Battleground battleground = caster.getBattle().getBattleground();
		final int orientation = battleground.orientate(caster.getPosition(), position);
		final List<int[]> positions = battleground.lineRegion(caster.getPosition(), position);
		final int sprint = positions.size();
		positions.remove(positions.size() - 1);
		Iterator<int[]> it = positions.iterator();
		int[] currentPosition = it.next();
		int[] nextPosition = null;
		for(; it.hasNext(); ) {
			nextPosition = it.next();
			caster.take(new MoveCommand(currentPosition, nextPosition) {
				@Override
				public void execute(Battler taker) {
					taker.setOrientation(orientation);
					taker.getBattle().moveBattler(taker, end);
					taker.getSprite().setSpeed(BattlerSprite.FAST_SPEED);
					motion = "move";
					interpolator = new MoveInterpolator(battleground.coordinate(start), battleground.coordinate(end));
				}
				@Override
				public void postExecute(Battler taker) {
					taker.getSprite().setSpeed(BattlerSprite.DEFAULT_SPEED);
				}
			});
			currentPosition = nextPosition;
		}
		caster.take(new Command() {
			@Override
			public void execute(Battler taker) {
				taker.setOrientation(orientation);
				taker.getSprite().setSpeed(BattlerSprite.FAST_SPEED);
				motion = "ready";
			}
			@Override
			public void postExecute(Battler taker) {
				spentEnergyAndActivity(taker);
				taker.getSprite().setSpeed(BattlerSprite.DEFAULT_SPEED);
				Battler foe = taker.getBattle().findBattler(position);
				int challenge = taker.getInt("attackPower", 0) + value(getInstrument().getInts("damage"));
				challenge += (int)(challenge  * sprint * getFloat("sprintAddition", 0.1f));
				int defence = foe.getInt("armorClass", 0);
				int damage = Math.max(challenge - defence, 1);
				android.util.Log.i(this.getClass().getName(), String.format("Charge: %d - %d = %d", challenge, defence, damage));
				foe.take(new InjureCommand(damage, orientation) {
					@Override
					public void postExecute(Battler taker) {
						super.postExecute(taker);
						taker.setActivity(0);
					}
				});
			}
		});
		return false;
	}

	@Override
	public void assist(Battler caster, int[] position) {
		Battleground battleground = caster.getBattle().getBattleground();
		List<int[]> positions = battleground.lineRegion(caster.getPosition(), position);
		if(positions != null) {
			battleground.highlight(positions);
		}
	}

}

