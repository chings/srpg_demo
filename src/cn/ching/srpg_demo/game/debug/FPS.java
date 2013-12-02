package cn.ching.srpg_demo.game.debug;

import android.graphics.Canvas;
import android.graphics.Paint;

public class FPS {

	protected int samplingRate = 1;

	protected float fps;
	protected int frames;
	protected int lastFrames;
	protected long lastUpdatedTime;

	public FPS(int samplingRate) {
		this.samplingRate = samplingRate;
		lastUpdatedTime = System.currentTimeMillis();
	}

	public float get() {
		if(frames % samplingRate == 0) {
			long updatedTime = System.currentTimeMillis();
			fps = (float)(frames - lastFrames) / (float)(updatedTime - lastUpdatedTime) * 1000;
			lastFrames = frames;
			lastUpdatedTime = updatedTime;
		}
		frames++;
		return fps;
	}

	public void render(Canvas canvas, int x, int y, Paint paint) {
		canvas.drawText(String.format("FPS: %.2f", get()), x, y, paint);
	}

}
