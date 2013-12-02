package cn.ching.srpg_demo.game.battle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import cn.ching.srpg_demo.game.core.Context;
import cn.ching.srpg_demo.game.data.Initializer;
import cn.ching.srpg_demo.game.data.Loader;

public class Item implements Context {

	protected String name;
	protected int resource;
	protected String[] tags;
	protected Map<String, Object> properties;
	protected List<Ability> abilities;

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

	@Initializer
	public List<Ability> initAbilities() {
		if(abilities == null) {
			abilities = new ArrayList<Ability>();
		}
		return abilities;
	}

	@Loader
	public void loadAbilities(List<Ability> abilities) {
		this.abilities = abilities;
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

	public List<Ability> getAbilities() {
		return abilities;
	}

}
