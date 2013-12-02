package cn.ching.srpg_demo.game.battle;

import java.util.HashMap;
import java.util.Map;


import cn.ching.srpg_demo.game.Resource;
import cn.ching.srpg_demo.game.core.IntegerPairInterpolator;
import cn.ching.srpg_demo.game.core.Sprite;
import cn.ching.srpg_demo.game.core.SpriteListener;
import cn.ching.srpg_demo.game.map.HexagonMap;

import android.graphics.Bitmap;
import android.graphics.Rect;

public class BattlerSprite extends Sprite {

	public static final int DEFAULT_ORIENTATION = HexagonMap.RIGHT;
	public static final int DEFAULT_SPEED = 4;
	public static final int FAST_SPEED = 1;
	public static final int SLOW_SPEED = 7;

	static final Map<String, int[]> X;
	static final Map<Integer, Integer> Y;
	static {
		X = new HashMap<String, int[]>();
		X.put("ready", new int[] {0});
		X.put("move", new int[] {64, 128, 192, 256});
		X.put("attack", new int[] {320, 384, 448, 512});
		X.put("cast", new int[] {576, 640, 704, 768});
		X.put("yield", new int[] {0, 0, 0, 0});
		X.put("fall", new int[] {0, 832, 0, 832});
		X.put("die", new int[] {832});
		Y = new HashMap<Integer, Integer>();
		Y.put(HexagonMap.LEFT, 256);
		Y.put(HexagonMap.DOWNLEFT, 0);
		Y.put(HexagonMap.UPLEFT, 128);
		Y.put(HexagonMap.DOWNRIGHT, 192);
		Y.put(HexagonMap.UPRIGHT, 64);
		Y.put(HexagonMap.RIGHT, 320);
	}

	public class Frame implements cn.ching.srpg_demo.game.core.Frame {
		int x, y;
		@Override
		public Bitmap getBitmap() {
			return Resource.images.get(resource);
		}
		Rect rect = new Rect();
		@Override
		public Rect getRect() {
			rect.left = x;
			rect.top = y;
			rect.right = x + 64;
			rect.bottom = y + 64;
			return rect;
		}
		int[] body = new int[] { 32, 32 };
		int[] hands = new int[] { 32, 32 };
		int[] feet = new int[] { 32, 53 };
		@Override
		public int[] getAnchor(String name) {
			if("body".equals(name)) {
				return body;
			} else if("hands".equals(name)) {
				return hands;
			} else {
				return feet;
			}
		}
		@Override
		public int[] getAnchor() {
			return getAnchor(null);
		}
	}

	public class FrameSequence implements cn.ching.srpg_demo.game.core.FrameSequence {
		int xs[];
		int n;
		int y;
		int speed;
		int length;
		public FrameSequence() {
			this.xs = new int[32];
		}
		public void setMotion(String motion) {
			int[] a = X.get(motion);
			n = a.length;
			length = n * speed;
			for(int i = 0; i < n; i++) {
				this.xs[i] = a[i];
			}
		}
		public void setOrientation(int orientation) {
			this.y = Y.get(orientation);
		}
		public void setSpeed(int speed) {
			this.speed = speed;
			length = n * speed;
		}
		@Override
		public int length() {
			return length;
		}
		Frame frame = new Frame();
		@Override
		public Frame getFrame(int nonce) {
			nonce = nonce / speed;
			frame.x = xs[nonce];
			frame.y = y;
			return frame;
		}
	}

	protected int resource;
	protected String defaultMotion;
	protected int orientation;
	protected int speed;
	protected IntegerPairInterpolator offsetInterpolator;

	public BattlerSprite(int resource, SpriteListener listener) {
		this.resource = resource;
		this.listener = listener;
		frames = new FrameSequence();
		defaultMotion = "ready";
		orientation = HexagonMap.RIGHT;
		speed = DEFAULT_SPEED;
		play(defaultMotion);
	}

	public void setDefaultMotion(String defaultMotion) {
		this.defaultMotion = defaultMotion;
		play(defaultMotion);
	}

	public void setOrientation(int orientation) {
		this.orientation = orientation;
		((FrameSequence)this.frames).setOrientation(orientation);
	}

	public void setSpeed(int speed) {
		this.speed = speed;
		((FrameSequence)this.frames).setSpeed(speed);
	}

	@Override
	public int[] getOffset() {
		if(offsetInterpolator != null) {
			return offsetInterpolator.getPair((float)nonce / (float)frames.length());
		}
		return DEFAULT_OFFSET;
	}

	public void play() {
		play(defaultMotion, null);
	}

	@Override
	public void play(String motion) {
		play(motion, null);
	}

	public void play(String motion, IntegerPairInterpolator interpolator) {
		FrameSequence frames = (FrameSequence)this.frames;
		frames.setMotion(motion);
		frames.setOrientation(orientation);
		frames.setSpeed(speed);
		nonce = 0;
		offsetInterpolator = interpolator;
	}

	@Override
	public String toString() {
		return "DefaultSprite [x=" + x + ", y=" + y + ", nonce=" + nonce + "]";
	}

}
