package cn.ching.srpg_demo.game.core;

public class Animation {

	protected FrameSequence frames;
	protected int nonce;

	public Frame currentFrame() {
		return frames.getFrame(nonce);
	}

	public Frame nextFrame() {
		return frames.getFrame(nonce++);
	}

	public boolean ends() {
		return nonce >= frames.length();
	}

}
