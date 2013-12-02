package cn.ching.srpg_demo.app;

import cn.ching.srpg_demo.R;

import cn.ching.srpg_demo.game.Game;
import cn.ching.srpg_demo.game.GestureListener;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity implements SurfaceHolder.Callback, OnGestureListener, OnDoubleTapListener {

	public static final int longPressJitter = 4;

	GestureDetector gestureDetector;
	int[] lastInput;

	Game game;
	Thread gameInstance;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.main);
		SurfaceView surfaceView = (SurfaceView)this.findViewById(R.id.screen);
		SurfaceHolder screen = surfaceView.getHolder();
		screen.addCallback(this);
		screen.setKeepScreenOn(true);

		gestureDetector = new GestureDetector(this, this);
		gestureDetector.setIsLongpressEnabled(true);
		gestureDetector.setOnDoubleTapListener(this);
		lastInput = new int[3];

		game = new Game(this.getApplicationContext(), screen, 30);
		gameInstance = new Thread(game, "gameInstance");
		gameInstance.setPriority(8);
	}

	@Override
	protected void onResume() {
		super.onResume();
		android.util.Log.v(this.getClass().getName(), "onResume");
		game.proceed();
	}

	@Override
	protected void onPause() {
		super.onPause();
		android.util.Log.v(this.getClass().getName(), "onPause");
		game.suspend();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		android.util.Log.v(this.getClass().getName(), "onDestroy");
		if(gameInstance.isAlive()) {
			gameInstance.interrupt();
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		android.util.Log.v(this.getClass().getName(), "surfaceCreated");
		if(!gameInstance.isAlive()) {
			gameInstance.start();
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		android.util.Log.v(this.getClass().getName(), "surfaceChanged");
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		android.util.Log.v(this.getClass().getName(), "surfaceDestroyed");
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(gestureDetector.onTouchEvent(event)) {
			return true;
		}
		if(event.getAction() == MotionEvent.ACTION_MOVE) {
			int e = lastInput[0];
			if(e == GestureListener.DRAG) {
				int x = (int)event.getX();
				int y = (int)event.getY();
				game.onInput(GestureListener.DRAG, lastInput[1], lastInput[2], x, y);
				lastInput[0] = GestureListener.DRAG;
				lastInput[1] = x;
				lastInput[2] = y;
				return true;
			} else if(e == GestureListener.LONG_PRESS) {
				int x = (int)event.getX();
				int y = (int)event.getY();
				int dx = x - lastInput[1];
				int dy = y - lastInput[2];
				if(dx > longPressJitter || dx < -longPressJitter || dy > longPressJitter || dy < -longPressJitter) {
					game.onInput(GestureListener.DRAG, lastInput[1], lastInput[2], x, y);
					lastInput[0] = GestureListener.DRAG;
					lastInput[1] = x;
					lastInput[2] = y;
				}
				return true;
			}
		} else if(event.getAction() == MotionEvent.ACTION_UP) {
			int e = lastInput[0];
			if(e == GestureListener.DRAG) {
				game.onInput(GestureListener.DRAG_RELEASE, lastInput[1], lastInput[2], (int)event.getX(), (int)event.getY());
				lastInput[0] = 0;
				return true;
			} else if(e == GestureListener.LONG_PRESS) {
				game.onInput(GestureListener.LONG_PRESS_RELEASE, lastInput[1], lastInput[2]);
				lastInput[0] = 0;
				return true;
			}
		}
		android.util.Log.v(this.getClass().getName(), "onTouchEvent: " + event.toString());
		return false;
	}

	@Override
	public boolean onDown(MotionEvent arg0) {
		return false;
	}

	@Override
	public boolean onFling(MotionEvent event1, MotionEvent event2, float arg2, float arg3) {
		game.onInput(GestureListener.DRAG_RELEASE, (int)event1.getX(), (int)event1.getY(), (int)event2.getX(), (int)event2.getY());
		return true;
	}

	@Override
	public void onLongPress(MotionEvent event) {
		android.util.Log.i(this.getClass().getName(), "onLongPress: " + event.toString());
		int x = (int)event.getX();
		int y = (int)event.getY();
		game.onInput(GestureListener.LONG_PRESS, x, y);
		lastInput[0] = GestureListener.LONG_PRESS;
		lastInput[1] = x;
		lastInput[2] = y;
	}

	@Override
	public boolean onScroll(MotionEvent event1, MotionEvent event2, float distanceX, float distanceY) {
		android.util.Log.i(this.getClass().getName(), "onScroll: " + event1.toString() + "," + event2.toString());
		int x = (int)event2.getX();
		int y = (int)event2.getY();
		game.onInput(GestureListener.DRAG, (int)event1.getX(), (int)event2.getY(), x, y);
		lastInput[0] = GestureListener.DRAG;
		lastInput[1] = x;
		lastInput[2] = y;
		return true;
	}

	@Override
	public void onShowPress(MotionEvent arg0) {

	}

	@Override
	public boolean onSingleTapUp(MotionEvent event) {
		return false;
	}

	@Override
	public boolean onDoubleTap(MotionEvent event) {
		android.util.Log.i(this.getClass().getName(), "onDoubleTap: " + event.toString());
		game.onInput(GestureListener.DOUBLE_TAP, (int)event.getX(), (int)event.getY());
		return true;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent arg0) {
		return false;
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent event) {
		android.util.Log.i(this.getClass().getName(), "onSingleTapConfirmed: " + event.toString());
		game.onInput(GestureListener.TAP, (int)event.getX(), (int)event.getY());
		return true;
	}

}