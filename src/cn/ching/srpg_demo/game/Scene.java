package cn.ching.srpg_demo.game;

abstract public class Scene implements GestureListener {

	protected Game game;

	public Scene(Game game) {
		this.game = game;
	}

	abstract public Scene onUpdate();

	@Override
	public boolean onTap(int x, int y) {
		return false;
	}

	@Override
	public boolean onDoubleTap(int x, int y) {
		return false;
	}

	@Override
	public boolean onLongPress(int x, int y) {
		return false;
	}

	@Override
	public boolean onLongPressRelease(int x, int y) {
		return false;
	}

	@Override
	public boolean onDrag(int xStart, int yStart, int xEnd, int yEnd) {
		return false;
	}

	@Override
	public boolean onDragRelease(int xStart, int yStart, int xEnd, int yEnd) {
		return false;
	}

}
