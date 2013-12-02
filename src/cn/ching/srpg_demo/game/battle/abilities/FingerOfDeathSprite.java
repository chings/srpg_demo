 package cn.ching.srpg_demo.game.battle.abilities;


import cn.ching.srpg_demo.game.Resource;
import cn.ching.srpg_demo.game.battle.ParticleSprite;
import cn.ching.srpg_demo.game.core.Camera;

import android.graphics.Canvas;

abstract public class FingerOfDeathSprite extends ParticleSprite {

	int x0, y0;

	public FingerOfDeathSprite(int xStart, int yStart, int xEnd, int yEnd) {
		x0 = xStart;
		y0 = yStart;
		x = xEnd;
		y = yEnd;
	}

	@Override
	public void play(String motion) {
		nonce = 0;
	}

	@Override
	public void tick() {
		if(nonce++ > 9 && listener != null) {
			listener.onMotionEnd(this);
		}
	}

	int[] p0 = new int[2];
	int[] p1 = new int[2];
	@Override
	public void render(Canvas canvas, Camera camera) {
		p0[0] = x0;
		p0[1] = y0;
		camera.view(p0);
		p1[0] = x;
		p1[1] = y;
		camera.view(p1);
		canvas.drawLine(p0[0], p0[1], p1[0], p1[1], Resource.paints.get("particle.deathRay.shadow"));
		canvas.drawLine(p0[0], p0[1], p1[0], p1[1], Resource.paints.get("particle.deathRay.trunck"));
	}

}
