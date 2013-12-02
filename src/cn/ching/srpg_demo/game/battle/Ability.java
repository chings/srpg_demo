package cn.ching.srpg_demo.game.battle;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;


import cn.ching.srpg_demo.game.core.Context;
import cn.ching.srpg_demo.game.data.Initializer;
import cn.ching.srpg_demo.game.data.Loader;

abstract public class Ability implements Context {

	public static final int ALL = -1;

	public static final int OUT_OF_RANGE = 4;
	public static final int OUT_OF_ACTIVITY = 3;
	public static final int OUT_OF_ENERGY = 2;
	public static final int LESS_OF_TARGET = 1;
	public static final int READY = 0;
	public static final int OK = 0;
	public static final int INAPPLICABLE = -1;
	public static final int UNQUALIFIED = -2;
	public static final int INVALID_TARGET = -3;

	protected static final Random random = new Random();

	protected String name;
	protected int resource;
	protected String[] tags;
	protected Map<String, Object> properties;

	public Ability() {

	}

	@Loader
	public void loadName(String name) {
		this.name = name;
	}

	@Loader
	public void loadResource(int resource) {
		this.resource = resource;
	}

	@Loader
	public void loadTags(String[] tags) {
		this.tags = tags;
	}

	@Initializer
	public Map<String, Object> initProperties() {
		if(properties == null) {
			properties = new HashMap<String, Object>();
		}
		return properties;
	}

	@Loader
	public void loadProperties(Map<String, Object> properties) {
		this.properties = properties;
	}

	public String getName() {
		return name;
	}

	public int getResource() {
		return resource;
	}

	public String[] getTags() {
		return tags;
	}

	@Override
	public boolean with(String key) {
		return properties.containsKey(key);
	}

	@Override
	public String getString(String key) {
		return (String)properties.get(key);
	}

	@Override
	public String getString(String key, String defaultValue) {
		String s = (String)properties.get(key);
		return s != null ? s : defaultValue;
	}

	@Override
	public Boolean getBoolean(String key) {
		return (Boolean)properties.get(key);
	}

	@Override
	public boolean getBoolean(String key, boolean defaultValue) {
		Boolean b = (Boolean)properties.get(key);
		return b != null ? b.booleanValue() : defaultValue;
	}

	@Override
	public Integer getInt(String key) {
		return (Integer)properties.get(key);
	}

	@Override
	public int getInt(String key, int defaultValue) {
		Integer n = (Integer)properties.get(key);
		return n != null ? n.intValue() : defaultValue;
	}

	@Override
	public int[] getInts(String key) {
		return (int[])properties.get(key);
	}

	@Override
	public int[] getInts(String key, int[] defaultValue) {
		int[] ns = (int[])properties.get(key);
		return ns != null ? ns : defaultValue;
	}

	@Override
	public Float getFloat(String key) {
		return (Float)properties.get(key);
	}

	@Override
	public float getFloat(String key, float defaultValue) {
		Float f = (Float)properties.get(key);
		return f != null ? f.floatValue() : defaultValue;
	}

	public boolean checkTargetNumber(Battler caster, int[] position) {
		boolean end = position.length % 2 == 1;
		if(!end && position.length >> 1 < getInt("targetNumber", 1)) {
			return false;
		}
		return true;
	}

	public boolean checkTarget(Battler caster, int[] position) {
		return false;
	}

	public boolean checkSelf(Battler caster, int[] position) {
		Battler target = caster.getBattle().findBattler(position);
		if(target == null || caster != target) {
			return false;
		}
		return true;
	}

	public boolean checkAlly(Battler caster, int[] position) {
		Battler target = caster.getBattle().findBattler(position);
		if(target == null || caster.getParty() != target.getParty()) {
			return false;
		}
		return true;
	}

	public boolean checkFoe(Battler caster, int[] position) {
		Battler target = caster.getBattle().findBattler(position);
		if(target == null || caster.getParty() == target.getParty()) {
			return false;
		}
		return true;
	}

	public boolean checkSpace(Battler caster, int[] position) {
		Tile tile = caster.getBattle().getBattleground().getTile(position);
		if(tile == null || tile.isOccupied()) {
			return false;
		}
		return true;
	}

	public boolean checkMisc(Battler caster, int[] position) {
		return true;
	}

	public boolean checkEnergyCost(Battler caster) {
		if(caster.getEnergy() < getInt("energyCost", 0)) {
			return false;
		}
		return true;
	}

	public boolean checkActivityCost(Battler caster) {
		if(caster.getActivity() < getInt("activityCost", 10)) {
			return false;
		}
		return true;
	}

	public boolean checkRange(Battler caster, int[] position) {
		int[] range = getInts("range");
		if(range != null) {
			if(!between(range, caster.getBattle().getBattleground().distance(caster.getPosition(), position))) {
				return false;
			}
		}
		return true;
	}

	public int check(Battler caster, int[] position) {
		if(!checkTarget(caster, position)) {
			return INVALID_TARGET;
		}
		if(!checkMisc(caster, position)) {
			return UNQUALIFIED;
		}
		if(!checkEnergyCost(caster)) {
			return OUT_OF_ENERGY;
		}
		if(!checkActivityCost(caster)) {
			return OUT_OF_ACTIVITY;
		}
		if(!checkRange(caster, position)) {
			return OUT_OF_RANGE;
		}
		return READY;
	}

	public void spentEnergyAndActivity(Battler caster) {
		caster.modifyEnergy(-getInt("energyCost", 0));
		int cost = getInt("activityCost", ALL);
		if(cost == ALL) {
			caster.endTurn();
		} else {
			caster.modifyActivity(-cost);
		}
	}

	public boolean apply(Battler caster, int[] position, int nonce) {
		if(nonce == 0) {
			return apply(caster, position);
		}
		return false;
	}

	abstract public boolean apply(Battler caster, int[] position);

	public void assist(Battler caster, int[] position) {

	}

	public void assistCircle(Battler caster, int[] position, int radius) {
		caster.getBattle().getBattleground().highlight(caster.getBattle().getBattleground().circleRegion(position, radius));
	}

	public static int value(int[] pair) {
		if(pair.length > 1) {
			int min = pair[0];
			int max = pair[1];
			return random.nextInt(max - min + 1) + min;
		}
		return pair[0];
	}

	public static int max(int[] pair) {
		return pair[pair.length - 1];
	}

	public static boolean between(int[] pair, int value) {
		if(pair.length > 1) {
			return value >= pair[0] && value <= pair[1];
		}
		return value == pair[0];
	}

}
