package cn.ching.srpg_demo.game.core;

import android.graphics.Bitmap;
import android.graphics.Rect;

public interface Frame {

	public static final int[] DEFAULT_CENTER = new int[] { 0, 0 };

	abstract public Bitmap getBitmap();

	public Rect getRect();

	public int[] getAnchor(String name);

	public int[] getAnchor();

}
