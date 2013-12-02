 package cn.ching.srpg_demo.game.battle.abilities;

import java.util.Random;


import cn.ching.srpg_demo.game.Resource;
import cn.ching.srpg_demo.game.battle.ParticleSprite;
import cn.ching.srpg_demo.game.core.Camera;

import android.graphics.Canvas;

abstract public class ChainLightningSprite extends ParticleSprite {

	static Random DITHERING = new Random();
	static int SEGMENT = 10;
	static int SWING = 10;

	int x0, y0;
	int dx, dy;
	int[][] swings;

	int jointNumber;
	int[][] joints;

	public ChainLightningSprite(int xStart, int yStart, int xEnd, int yEnd, int lineNumber) {
		x0 = xStart;
		y0 = yStart;
		x = xEnd;
		y = yEnd;
		dx = xEnd - xStart;
		dy = yEnd - yStart;
		float k = -(float)dx/(float)dy;
		boolean O_45 = (k < 0 ? -k : k) <= 1.0;
		int len = SWING * 2 + 1;
		swings = new int[len][2];
		int n = -SWING;
		for(int i = 0; i < len; i++) {
			if(O_45) {
				swings[i][0] = n;
				swings[i][1] = (int)(n * k);
			} else {
				swings[i][0] = (int)(n / k);
				swings[i][1] = n;
			}
			n++;
		}
		jointNumber = (int)(Math.sqrt(dx * dx + dy * dy) / SEGMENT);
		dx = (int)(dx / (float)jointNumber);
		dy = (int)(dy / (float)jointNumber);
		joints = new int[lineNumber][jointNumber * 2 + 2];
	}

	@Override
	public void play(String motion) {
		nonce = 0;
	}

	int[] NO_SWING = new int[] { 0, 0 };
	private void reseed() {
		for(int j = 0; j < joints.length; j++) {
			int x = x0;
			int y = y0;
			int len = joints[j].length - 2;
			for(int i = 0; i < len; i += 2) {
				int[] swing = swings[DITHERING.nextInt(swings.length)];
				joints[j][i] = x + swing[0];
				joints[j][i + 1] = y + swing[1];
				x += dx;
				y += dy;
			}
			joints[j][0] = x0;
			joints[j][1] = y0;
			joints[j][len - 2] = this.x;
			joints[j][len - 1] = this.y;
		}
	}

	@Override
	public void tick() {
		if(nonce++ % 4 == 0) {
			reseed();
		}
		if(nonce > 19 && listener != null) {
			listener.onMotionEnd(this);
		}
	}

	int[] p0 = new int[2];
	int[] p1 = new int[2];
	@Override
	public void render(Canvas canvas, Camera camera) {
		for(int j = 0; j < joints.length; j++) {
			p0[0] = joints[j][0];
			p0[1] = joints[j][1];
			camera.view(p0);
			int len = joints[j].length - 2;
			for(int i = 2; i < len; i += 2) {
				p1[0] = joints[j][i];
				p1[1] = joints[j][i + 1];
				camera.view(p1);
				canvas.drawLine(p0[0], p0[1], p1[0], p1[1], j == 0 ? Resource.paints.get("particle.lightning.shadow") : Resource.paints.get("particle.lightning.branch"));
				p0[0] = p1[0];
				p0[1] = p1[1];
			}
		}
		int j = 0;
		p0[0] = joints[j][0];
		p0[1] = joints[j][1];
		camera.view(p0);
		int len = joints[j].length - 2;
		for(int i = 2; i < len; i += 2) {
			p1[0] = joints[j][i];
			p1[1] = joints[j][i + 1];
			camera.view(p1);
			canvas.drawLine(p0[0], p0[1], p1[0], p1[1], Resource.paints.get("particle.lightning.trunck"));
			p0[0] = p1[0];
			p0[1] = p1[1];
		}
	}

}
