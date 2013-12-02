package cn.ching.srpg_demo.game;

import android.content.Context;
import android.view.SurfaceHolder;

public class Game implements Runnable {

	private final Object mutex = new Object();
	private Boolean running = true;

	private SurfaceHolder screen;
	private long frameDelay;

	private int[] input;

	private Scene scene;

	public Game(Context context, SurfaceHolder screen, float frameRate) {
		Resource.context = context;
		this.screen = screen;
		this.frameDelay = (long)((1 / frameRate) * 1000);
		input = new int[5];
	}

	public SurfaceHolder getScreen() {
		return screen;
	}

	@Override
	public void run() {
		android.util.Log.d(this.getClass().getName(), "game started");
		//scene = new cn.ching.srpg_demo.game.demo.RippleScene(this);
		//scene = new cn.ching.srpg_demo.game.demo.HexagonScene(this);
		scene = new cn.ching.srpg_demo.game.battle.LoadingScene(this, 1);
		do {
			if(!running) {
				synchronized(mutex) {
					try {
						mutex.wait();
					} catch(InterruptedException x) {
						android.util.Log.d(this.getClass().getName(), "game interrupted");
						break;
					}
				}
			}
			long preUpdate = System.currentTimeMillis();
			switch(input[0]) {
			case GestureListener.TAP:
				scene.onTap(input[1], input[2]);
				break;
			case GestureListener.DOUBLE_TAP:
				scene.onDoubleTap(input[1], input[2]);
				break;
			case GestureListener.LONG_PRESS:
				scene.onLongPress(input[1], input[2]);
				break;
			case GestureListener.LONG_PRESS_RELEASE:
				scene.onLongPressRelease(input[1], input[2]);
				break;
			case GestureListener.DRAG:
				scene.onDrag(input[1], input[2], input[3], input[4]);
				break;
			case GestureListener.DRAG_RELEASE:
				scene.onDragRelease(input[1], input[2], input[3], input[4]);
				break;
			}
			input[0] = 0;
			scene = scene.onUpdate();
			long postUpdate = System.currentTimeMillis();
			long delay = preUpdate + frameDelay - postUpdate;
			if(delay > 0) {
				try {
					Thread.sleep(delay);
				} catch(InterruptedException x) {
					android.util.Log.d(this.getClass().getName(), "game interrupted");
					break;
				}
			}
		} while(scene != null);
		android.util.Log.d(this.getClass().getName(), "game terminated");
	}

	public void proceed() {
		if(!running) {
			running = true;
			synchronized(mutex) {
				mutex.notifyAll();
			}
		}
	}

	public void suspend() {
		running = false;
	}

	public void onInput(int... input) {
		System.arraycopy(input, 0, this.input, 0, input.length);
	}

}
