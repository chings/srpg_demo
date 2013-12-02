package cn.ching.srpg_demo.game.battle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import cn.ching.srpg_demo.R;

import cn.ching.srpg_demo.game.Game;
import cn.ching.srpg_demo.game.Profile;
import cn.ching.srpg_demo.game.Resource;
import cn.ching.srpg_demo.game.Scene;
import cn.ching.srpg_demo.game.battle.ai.SimpleIntelligence;
import cn.ching.srpg_demo.game.core.Camera;
import cn.ching.srpg_demo.game.core.EventListener;
import cn.ching.srpg_demo.game.data.Loader;
import cn.ching.srpg_demo.game.debug.FPS;
import cn.ching.srpg_demo.game.debug.Trace;
import cn.ching.srpg_demo.game.map.AStar;
import cn.ching.srpg_demo.game.ui.Action;
import cn.ching.srpg_demo.game.ui.ActionPanel;
import cn.ching.srpg_demo.util.Rectangles;

import android.graphics.Canvas;
import android.graphics.Rect;

public class BattleScene extends Scene implements EventListener {

	int id;
	String name;

	Camera camera;
	Rect field;

	Battleground battleground;
	Map<Battler, AStar.Map> battlegroundSituations;
	List<Party> parties;

	Objective objective;
	Battler actor;
	Sequence<Battler> actList;
	List<Battler> renderList;
	Comparator<Battler> renderComparator;

	ActionPanel actionPanel;

	boolean debug;
	FPS fps;
	Trace trace;

	public BattleScene(Game game, int id) {
		super(game);
		this.id = id;

		camera = new Camera(game.getScreen().getSurfaceFrame());
		battlegroundSituations = new HashMap<Battler, AStar.Map>();
		actList = new Sequence<Battler>();
		renderList = new LinkedList<Battler>();
		renderComparator = new Comparator<Battler>() {
			@Override
			public int compare(Battler a, Battler b) {
				int r = a.position[1] - b.position[1];
				if(r == 0) {
					r = a.position[0] - b.position[0];
					if(r == 0) {
						r = a.status - b.status;
					}
				}
				return r;
			}
		};

		objective = new Objective();
		actionPanel = new ActionPanel();

		debug = true;
		fps = new FPS(10);
		trace = new Trace(new Rect(5, 5, 795, 25));

		int[] a = new int[] { R.raw.battle_01, R.raw.battle_02, R.raw.battle_03 };
		Resource.data.load(a[(id - 1) % a.length], this);

		actList.init();
		actor = actList.get(actList.getTurn());
		actor.activity = 0;
	}

	@Loader
	public void loadName(String name) {
		this.name = name;
	}

	@Loader
	public void loadBattleground(Battleground battleground) {
		this.battleground = battleground;
		battleground.setBattle(this);
		field = battleground.getRect();
	}

	@Loader
	public void loadParties(List<Party> parties) {
		int i = 0;
		for(Party party : parties) {
			party.setBattle(this);
			if(i != 0) {
				objective.setEvilBattlers(party);
			}
			int j = 0;
			for(Battler battler : party) {
				battler.setBattle(this);
				if(battler.getCharacter() == null) {
					Character character = Profile.current.getPlayerCharacter(j++);
					if(character == null) {
						continue;
					}
					battler.loadCharacter(character);
					objective.addGoodBattler(battler);
				} else {
					SimpleIntelligence intelligence = new SimpleIntelligence("default");
					battler.setIntelligence(intelligence);
				}
				moveBattler(battler, battler.position);
				actList.add(battler);
				renderList.add(battler);
			}
			i++;
		}
		this.parties = parties;
	}

	public boolean isBusy() {
		return actor.isBusy();
	}

	public Battleground getBattleground() {
		return battleground;
	}

	public AStar.Map getBattlegroundSituation(final Battler viewer) {
		AStar.Map battlegroundSituation = battlegroundSituations.get(viewer);
		if(battlegroundSituation == null) {
			battlegroundSituation = new AStar.Map() {
				@Override
				public int[][] adjacent(int[] position) {
					return battleground.adjacent(position);
				}
				@Override
				public boolean reachable(int[] position) {
					if(!battleground.contains(position)) {
						return false;
					}
					Tile tile = battleground.getTile(position);
					return tile.occupier == null;
				}
				@Override
				public int baseRoughness() {
					return battleground.getBaseRoughnesses();
				}
				@Override
				public int roughness(int[] position) {
					Tile tile = battleground.getTile(position);
					int roughness = battleground.getRoughnesses(tile);
					if(tile.piles != null) {
						roughness += tile.piles.size() * battleground.getBaseRoughnesses();
					}
					roughness += viewer.getInt("terrainModification" + tile.terrain, 0);
					return roughness;
				}
				@Override
				public int distance(int[] start, int[] end) {
					return battleground.distance(start, end);
				}
			};
			battlegroundSituations.put(viewer, battlegroundSituation);
		}
		return battlegroundSituation;
	}

	public Battler getActor() {
		return actor;
	}

	public List<Party> getParties() {
		return parties;
	}

	public Battler findBattler(int[] position) {
		Tile tile = battleground.getTile(position);
		if(tile.isOccupied() && tile.occupier instanceof Battler) {
			return (Battler)tile.occupier;
		}
		return null;
	}

	public Battler findClosestBattler(int[] position, Party party, boolean allyOrFoe) {
		Party inParty = party == parties.get(0) ^ allyOrFoe ? parties.get(1) : parties.get(0);
		Battler target = null;
		int targetDistance = Integer.MAX_VALUE;
		for(Battler battler : inParty) {
			int distance = battleground.distance(position, battler.getPosition());
			if(distance < targetDistance) {
				target = battler;
				targetDistance = distance;
			}
		}
		return target;
	}

	public List<Battler> findEnclosedBattlers(int[] center, int radius, Party party, boolean allyOrFoe) {
		List<Battler> battlers = new ArrayList<Battler>();
		for(int[] position : battleground.circleRegion(center, radius)) {
			if(!battleground.contains(position)) {
				continue;
			}
			Tile tile = battleground.getTile(position);
			if(!tile.isOccupied() || !(tile.occupier instanceof Battler)) {
				continue;
			}
			Battler battler = (Battler)tile.occupier;
			if(party != null && (battler.party == party ^ allyOrFoe)) {
				continue;
			}
			battlers.add(battler);
		}
		return battlers;
	}

	public int[] findEnclosedSpace(int[] center, int radius) {
		for(int[] position : battleground.circleRegion(center, radius)) {
			if(!battleground.contains(position)) {
				continue;
			}
			Tile tile = battleground.getTile(position);
			if(!tile.isOccupied()) {
				return position;
			}
		}
		return null;
	}

	public void moveBattler(Battler battler, int[] position) {
		Tile tile = battleground.getTile(battler.position);
		if(tile.occupier == battler) {
			tile.occupier = null;
		}
		battler.setPosition(position);
		int[] center = battleground.coordinate(position).getCenter();
		battler.sprite.moveTo(center[0], center[1]);
		tile = battleground.getTile(position);
		tile.occupier = battler;
	}

	public void addBattler(Battler battler, Party party, int[] position) {
		battler.setBattle(this);
		party.add(battler);
		actList.add(battler);
		renderList.add(battler);
		Tile tile = battleground.getTile(position);
		tile.occupier = battler;
		battler.setPosition(position);
		int[] center = battleground.coordinate(position).getCenter();
		battler.sprite.moveTo(center[0], center[1]);
	}

	@Override
	public Scene onUpdate() {
		battleground.tick();
		boolean noBattlerBusy = true;
		for(Battler battler : actList) {
			battler.tick();
			noBattlerBusy &= !battler.isBusy();
		}
		if(objective.getVictory() != null) {
			return objective.getVictory() ? new LoadingScene(game, id + 1) : new LoadingScene(game, id);
		}
		if(noBattlerBusy && actor.getActivity() <= 0) {
			do {
				actor = actList.next();
				if(actor.isFallen()) {
					continue;
				}
			}
			while(false);
			int[] point = actor.sprite.getDestAnchor();
			camera.center(point[0], point[1], field);
			if(actor.isPlayer()) {
				actionPanel.load(actor);
			}
			actionPanel.dismiss();
		}
		Canvas canvas = game.getScreen().lockCanvas();
		if(canvas != null) {
			try {
				battleground.render(canvas, camera);
				Collections.sort(renderList, renderComparator);
				for(Battler battler : renderList) {
					battler.renderSprite(canvas, camera);
				}
				for(Battler battler : renderList) {
					battler.renderAdditions(canvas, camera);
				}
				if(actionPanel != null && !actionPanel.isHidden()) {
					actionPanel.render(canvas, camera);
				}
				if(debug && trace != null) {
					trace.clear();
					trace.println(String.format("Battle %d {Round:%d, Turn:%d} %s {Life:%d/%d, Energy:%d/%d, Activity:%d/%d, Position:(%d,%d)}",
						id, actList.getRound(), actList.getTurn(), actor, actor.life, actor.getInt("maxLife"), actor.energy, actor.getInt("maxEnergy"), actor.activity, actor.getInt("maxActivity"), actor.position[0], actor.position[1]));
					trace.render(canvas);
				}
				if(fps != null) {
					fps.render(canvas, 5, camera.getRenderRect().bottom - 5, Resource.paints.get("text.debug"));
				}
			} finally {
				game.getScreen().unlockCanvasAndPost(canvas);
			}
		}
		return this;
	}

	@Override
	public boolean onTap(int x, int y) {
		x += camera.getViewLeft();
		y += camera.getViewTop();
		if(isBusy()) {
			return false;
		}
		if(actionPanel != null) {
			int[] position = battleground.decoordinate(x, y);
			if(position == null) {
				return false;
			}
			if(actionPanel.isTargeting()) {
				Action action = actionPanel.choose(x, y, camera);
				if(action != null) {
					action.perform();
					actionPanel.dismiss();
					return true;
				}
				action = actionPanel.getCurrentAction();
				int result = actionPanel.recheck(action, position);
				if(result == Ability.READY) {
					action.perform();
					actionPanel.dismiss();
				} else if(result == Ability.LESS_OF_TARGET) {
					int[] point = battleground.coordinate(position).getCenter();
					actionPanel.moveTo(point[0], point[1]);
					actionPanel.target(position, point[0], point[1]);
				}
				return true;
			}
			if(actionPanel.isReady()) {
				Action action = actionPanel.choose(x, y, camera);
				if(action != null) {
					int result = actionPanel.getResult(action);
					if(result == Ability.READY) {
						action.perform();
						actionPanel.dismiss();
					} else if(result == Ability.LESS_OF_TARGET) {
						actionPanel.target(battleground.decoordinate(actionPanel.getX(), actionPanel.getY()), actionPanel.getX(), actionPanel.getY());
						actionPanel.beginTargeting(action);
					} else {
						actionPanel.dismiss();
					}
					return true;
				}
			}
			actionPanel.dismiss();
			actionPanel.filter(position);
			int[] point = battleground.coordinate(position).getCenter();
			actionPanel.moveTo(point[0], point[1]);
			actionPanel.show();
			return true;
		}
		return false;
	}

	@Override
	public boolean onDoubleTap(int x, int y) {
		x += camera.getViewLeft();
		y += camera.getViewTop();
		if(actor.isBusy()) {
			camera.center(x, y, field);
		}
		return true;
	}

	@Override
	public boolean onLongPress(int x, int y) {
		x += camera.getViewLeft();
		y += camera.getViewTop();
		if(actionPanel != null) {
			if(actionPanel.isReady()) {
				Action action = actionPanel.choose(x, y, camera);
				if(action != null) {
					actionPanel.beginAjusting(action);
					int result = actionPanel.getResult(action);
					if(result >= 0) {
						action.byPerform();
					}
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean onLongPressRelease(int x, int y) {
		if(actionPanel != null) {
			if(actionPanel.isAjusting()) {
				Action action = actionPanel.getCurrentAction();
				int result = actionPanel.getResult(action);
				if(result == Ability.READY) {
					action.perform();
					actionPanel.dismiss();
				} else if(result == Ability.LESS_OF_TARGET) {
					actionPanel.target(battleground.decoordinate(actionPanel.getX(), actionPanel.getY()), actionPanel.getX(), actionPanel.getY());
					actionPanel.beginTargeting(action);
				} else {
					actionPanel.dismiss();
				}
				battleground.clear();
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean onDrag(int xStart, int yStart, int xEnd, int yEnd) {
		xEnd += camera.getViewLeft();
		yEnd += camera.getViewTop();
		if(actionPanel != null) {
			int[] position = battleground.decoordinate(xEnd, yEnd);
			if(position == null) {
				return false;
			}
			if(actionPanel.isAjusting()) {
				Action action = actionPanel.getCurrentAction();
				int result = actionPanel.check(action, position);
				if(result >= 0) {
					action.byPerform();
					int[] point = battleground.coordinate(position).getCenter();
					actionPanel.moveTo(point[0], point[1]);
				} else {
					battleground.clear();
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean onDragRelease(int xStart, int yStart, int xEnd, int yEnd) {
		if(actionPanel != null) {
			if(actionPanel.isAjusting()) {
				Action action = actionPanel.getCurrentAction();
				int result = actionPanel.getResult(action);
				if(result == Ability.READY) {
					action.perform();
					actionPanel.dismiss();
				} else if(result == Ability.LESS_OF_TARGET) {
					actionPanel.target(battleground.decoordinate(actionPanel.getX(), actionPanel.getY()), actionPanel.getX(), actionPanel.getY());
					actionPanel.beginTargeting(action);
				} else {
					actionPanel.dismiss();
				}
				battleground.clear();
				return true;
			}
		}
		camera.move(xStart - xEnd, yStart - yEnd);
		Rectangles.confine(camera.getViewRect(), field);
		return true;
	}

	public void onBattlerFall(Battler fallenBattler) {
		actList.exclude(fallenBattler);
		Tile tile = battleground.getTile(fallenBattler.getPosition());
		if(tile.occupier == fallenBattler) {
			tile.occupier = null;
		}
		tile.pile(fallenBattler);
		objective.checkFallenBattler(fallenBattler);
	}

	public void onPartyFall(Party fallenParty) {
		objective.checkFallenParty(fallenParty);
	}

	@Override
	public boolean onEvent(Object... event) {
		return false;
	}

}
