package cn.ching.srpg_demo.game;

public interface GestureListener {

	public static final int TAP = 1;
	public static final int DOUBLE_TAP = 2;
	public static final int LONG_PRESS = 3;
	public static final int LONG_PRESS_RELEASE = 4;
	public static final int DRAG = 5;
	public static final int DRAG_RELEASE = 6;

	public boolean onTap(int x, int y);

	public boolean onDoubleTap(int x, int y);

	public boolean onLongPress(int x, int y);

	public boolean onLongPressRelease(int x, int y);

	public boolean onDrag(int xStart, int yStart, int xEnd, int yEnd);

	public boolean onDragRelease(int xStart, int yStart, int xEnd, int yEnd);

}
