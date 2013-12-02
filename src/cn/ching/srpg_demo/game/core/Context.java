package cn.ching.srpg_demo.game.core;

public interface Context {

	public boolean with(String key);

	public String getString(String key);

	public String getString(String key, String defaultValue);

	public Boolean getBoolean(String key);

	public boolean getBoolean(String key, boolean defaultValue);

	public Integer getInt(String key);

	public int getInt(String key, int defaultValue);

	public int[] getInts(String key);

	public int[] getInts(String key, int[] defaultValue);

	public Float getFloat(String key);

	public float getFloat(String key, float defaultValue);

}
