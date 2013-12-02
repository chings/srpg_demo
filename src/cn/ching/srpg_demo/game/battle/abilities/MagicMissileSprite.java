package cn.ching.srpg_demo.game.battle.abilities;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import cn.ching.srpg_demo.R;

import cn.ching.srpg_demo.game.Resource;
import cn.ching.srpg_demo.game.battle.ParticleSprite;
import cn.ching.srpg_demo.game.core.FrameSequence;

import android.graphics.Bitmap;
import android.graphics.Rect;

abstract public class MagicMissileSprite extends ParticleSprite implements FrameSequence {

	static Random DITHERING = new Random();
	static int DEFLECTION = 10;
	static int VElOCITY = 15;
	static Map<Integer, Integer> X_VElOCITY;
	static Map<Integer, Integer> Y_VElOCITY;
	static {
		X_VElOCITY = new HashMap<Integer, Integer>();
		for(int angle = 0; angle < 360; angle += DEFLECTION) {
			X_VElOCITY.put(angle, (int)(Math.round(Math.cos(Math.toRadians(angle)) * VElOCITY)));
		}
		Y_VElOCITY = new HashMap<Integer, Integer>();
		for(int angle = 0; angle < 360; angle += DEFLECTION) {
			Y_VElOCITY.put(angle, (int)(Math.round(Math.sin(Math.toRadians(angle)) * VElOCITY)));
		}
	}

	public class Frame implements cn.ching.srpg_demo.game.core.Frame {
		int x, y;
		@Override
		public Bitmap getBitmap() {
			return Resource.images.get(R.drawable.sprite_magic_missile);
		}
		Rect rect = new Rect();
		@Override
		public Rect getRect() {
			rect.left = x;
			rect.top = y;
			rect.right = x + 16;
			rect.bottom = y + 16;
			return rect;
		}
		int[] center = new int[] { 8, 8 };
		@Override
		public int[] getAnchor(String name) {
			if(name != null) {
				return null;
			} else {
				return center;
			}
		}
		@Override
		public int[] getAnchor() {
			return getAnchor(null);
		}
	}

	int xEnd, yEnd;
	int angle;
	Rect targetRect;

	public MagicMissileSprite(int xStart, int yStart, int xEnd, int yEnd, int angle) {
		frames = this;
		frame = new Frame();
		((Frame)frame).y = 32;
		this.x = xStart;
		this.y = yStart;
		this.xEnd = xEnd;
		this.yEnd = yEnd;
		this.angle = angle;
		targetRect = new Rect(xEnd - 16, yEnd - 16, xEnd + 16, yEnd + 16);
	}

	@Override
	public int length() {
		return 0;
	}

	@Override
	public Frame getFrame(int nonce) {
		Frame frame = (Frame)this.frame;
		frame.x = (nonce % 3) << 4;
		return frame;
	}

	protected int measure(int xStart, int yStart, int xEnd, int yEnd) {
		int dx = xEnd - xStart;
		int dy = yEnd - yStart;
		return dx * dx + dy * dy;
	}

	int[] offset = new int[2];
	int dd = Integer.MAX_VALUE;
	int[][] triple = new int[3][4];
	Comparator<int[]> tripleComparator = new Comparator<int[]>() {
		@Override
		public int compare(int[] lhs, int[] rhs) {
			return lhs[3] - rhs[3];
		}
	};
	@Override
	public int[] getOffset() {
		triple[0][0] = this.angle;
		triple[1][0] = this.angle < DEFLECTION ? this.angle - DEFLECTION + 360 : this.angle - DEFLECTION;
		triple[2][0] = this.angle >= 360 - DEFLECTION ? this.angle + DEFLECTION - 360 : this.angle + DEFLECTION;
		for(int i = 0; i < 3; i++) {
			triple[i][1] = X_VElOCITY.get(triple[i][0]);
			triple[i][2] = Y_VElOCITY.get(triple[i][0]);
			triple[i][3] = measure(this.x + triple[i][1], this.y + triple[i][2], xEnd, yEnd);
		}
		Arrays.sort(triple, tripleComparator);
		for(int i = 0; i < 3; i++) {
			if(triple[i][0] == angle && (triple[i][3] > dd || DITHERING.nextInt(4) == 0)) {
				continue;
			}
			if(i < 2 && triple[i][3] == triple[i + 1][3] && DITHERING.nextInt(2) == 0) {
				continue;
			}
			angle = triple[i][0];
			offset[0] = triple[i][1];
			offset[1] = triple[i][2];
			dd = triple[i][3];
			break;
		}
		return offset;
	}

	@Override
	public void play(String motion) {
		nonce = 0;
	}

	Rect collisionRect = new Rect();
	@Override
	public void tick() {
		frame = frames.getFrame(nonce);
		srcRect = frame.getRect();
		int[] offset = getOffset();
		x += offset[0];
		y += offset[1];
		destAnchor[0] = x;
		destAnchor[1] = y;
		int[] center = frame.getAnchor();
		destRect.left = destAnchor[0] - center[0] + srcRect.left;
		destRect.top = destAnchor[1] - center[1] + srcRect.top;
		destRect.right = destRect.left + srcRect.width();
		destRect.bottom = destRect.top + srcRect.height();
		if(offset[0] < 0) {
			collisionRect.left = x;
			collisionRect.right = x - offset[0];
		} else {
			collisionRect.left = x - offset[0];
			collisionRect.right = x;
		}
		if(offset[1] < 0) {
			collisionRect.top = y;
			collisionRect.bottom = y - offset[1];
		} else {
			collisionRect.top = y - offset[1];
			collisionRect.bottom = y;
		}
		if(collisionRect.intersect(targetRect) || nonce > 300) {
			if(listener != null) {
				listener.onMotionEnd(this);
			}
		}
		nonce++;
	}

}
