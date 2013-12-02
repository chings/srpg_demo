package cn.ching.srpg_demo.game.battle.abilities;

import cn.ching.srpg_demo.R;

import cn.ching.srpg_demo.game.Resource;
import cn.ching.srpg_demo.game.battle.ParticleSprite;
import cn.ching.srpg_demo.game.core.FrameSequence;

import android.graphics.Bitmap;
import android.graphics.Rect;

abstract public class FireballSprite extends ParticleSprite implements FrameSequence {

	static int VElOCITY = 30;

	public class Frame implements cn.ching.srpg_demo.game.core.Frame {
		int x, y;
		@Override
		public Bitmap getBitmap() {
			return Resource.images.get(R.drawable.sprite_fireball);
		}
		Rect rect = new Rect();
		@Override
		public Rect getRect() {
			rect.left = x;
			rect.top = y;
			rect.right = x + 32;
			rect.bottom = y + 32;
			return rect;
		}
		int[] center = new int[] { 16, 16 };
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

	int[] offset;
	int density;
	int dx, dy;

	public FireballSprite(int xStart, int yStart, int xEnd, int yEnd) {
		frames = this;
		frame = new Frame();
		((Frame)frame).y = 64;
		x = xEnd;
		y = yEnd;
		offset = new int[] { xStart - xEnd, yStart - yEnd };
		density = (int)(Math.sqrt(offset[0] * offset[0] + offset[1] * offset[1]) / VElOCITY);
		dx = (int)(offset[0] / (float)density);
		dy = (int)(offset[1] / (float)density);
	}

	@Override
	public int length() {
		return density;
	}

	@Override
	public Frame getFrame(int nonce) {
		Frame frame = (Frame)this.frame;
		frame.x = (nonce % 3) << 5;
		return frame;
	}

	@Override
	public int[] getOffset() {
		offset[0] -= dx;
		offset[1] -= dy;
		return offset;
	}

	@Override
	public void play(String motion) {
		nonce = 0;
	}

}
