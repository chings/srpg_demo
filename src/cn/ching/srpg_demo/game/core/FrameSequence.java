package cn.ching.srpg_demo.game.core;

public interface FrameSequence {

	public int length();

	public Frame getFrame(int nonce);

}
