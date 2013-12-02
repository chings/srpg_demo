package cn.ching.srpg_demo.game.core;

import cn.ching.srpg_demo.util.Rectangles;

import android.graphics.Canvas;
import android.graphics.Rect;

abstract public class Sprite {

	public static final int[] DEFAULT_OFFSET = new int[] {0, 0};

	protected SpriteListener listener = null;
	protected FrameSequence frames;
	protected Frame frame;
	protected int nonce = 0;
	protected int x, y;

	protected int[] destAnchor;
	protected Rect destRect;
	protected Rect srcRect;

	public Sprite() {
		destAnchor = new int[2];
		destRect = new Rect();
	}

	public Sprite(SpriteListener listener) {
		this();
		this.listener = listener;
	}

	public void setListener(SpriteListener listener) {
		this.listener = listener;
	}

	public FrameSequence getFrames() {
		return frames;
	}

	public Frame getFrame() {
		return frame;
	}

	public int[] getOffset() {
		return DEFAULT_OFFSET;
	}

	public int[] getDestAnchor() {
		return destAnchor;
	}

	int[] anchor = new int[2];
	public int[] getDestAnchor(String name) {
		int[] anchor = frame.getAnchor();
		int destLeft = destAnchor[0] - anchor[0];
		int destTop = destAnchor[1] - anchor[1];
		anchor = frame.getAnchor(name);
		if(anchor == null) {
			return null;
		}
		this.anchor[0] = destLeft + anchor[0];
		this.anchor[1] = destTop + anchor[1];
		return this.anchor;
	}

	public Rect getDestRect() {
		srcRect = frame.getRect();
		int[] anchor = frame.getAnchor();
		destRect.left = destAnchor[0] - anchor[0];
		destRect.top = destAnchor[1] - anchor[1];
		destRect.right = destRect.left + srcRect.width();
		destRect.bottom = destRect.top + srcRect.height();
		return destRect;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public void moveTo(int x, int y) {
		this.x = x;
		this.y = y;
	}

	abstract public void play(String motion);

	public void tick() {
		frame = frames.getFrame(nonce);
		int[] offset = getOffset();
		destAnchor[0] = x + offset[0];
		destAnchor[1] = y + offset[1];
		if(nonce < frames.length() - 1) {
			nonce++;
		} else {
			if(listener != null) {
				listener.onMotionEnd(this);
			}
		}
	}

	public void render(Canvas canvas, Camera camera) {
		getDestRect();
		int[] intersectionOffset = Rectangles.intersect(destRect, camera.getViewRect());
		if(intersectionOffset != null) {
			destRect.left += intersectionOffset[0];
			srcRect.left += intersectionOffset[0];
			destRect.top += intersectionOffset[1];
			srcRect.top += intersectionOffset[1];
			destRect.right += intersectionOffset[2];
			srcRect.right += intersectionOffset[2];
			destRect.bottom += intersectionOffset[3];
			srcRect.bottom += intersectionOffset[3];
			camera.view(destRect);
			//canvas.drawRect(destRect, Resource.paints.get("background.green_transparent"));
			canvas.drawBitmap(frame.getBitmap(), srcRect, destRect, null);
		}
	}

}