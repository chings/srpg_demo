package cn.ching.srpg_demo.game.ui;

public class HexagonalSpiralSpread extends Spread {

	int baseWidth;
	int halfHeight;
	int[][] stepOffsetsBySide;

	int step;
	int side;
	int sideLength;
	int ring;
	int[] offset;
	int[] nextOffset;

	public HexagonalSpiralSpread(int baseWidth, int halfHeight) {
		this.baseWidth = baseWidth;
		this.halfHeight = halfHeight;
		stepOffsetsBySide = new int[][] {
			{ 0, -(halfHeight << 1) },
			{ baseWidth, -halfHeight },
			{ baseWidth, halfHeight },
			{ 0, halfHeight << 1 },
			{ -baseWidth, halfHeight },
			{ -baseWidth, -halfHeight }
		};
		offset = new int[] { 0, 0 };
		nextOffset = new int[] { 0, 0 };
		reset();
	}

	@Override
	public void reset() {
		step = 0;
		side = 0;
		ring = 0;
		offset[0] = 0;
		offset[0] = 0;
		nextOffset[0] = 0;
		nextOffset[1] = 0;
	}

	@Override
	public int[] next() {
		offset[0] = nextOffset[0];
		offset[1] = nextOffset[1];
		if(ring == 0) {
			nextOffset[0] += stepOffsetsBySide[5][0];
			nextOffset[1] += stepOffsetsBySide[5][1];
			ring = 1;
			side = 1;
			sideLength = ring;
			step = 0;
			return offset;
		}
		nextOffset[0] += stepOffsetsBySide[side][0];
		nextOffset[1] += stepOffsetsBySide[side][1];
		++step;
		if(step > sideLength - 1) {
			++side;
			if(side > 5) {
				++ring;
				side = 0;
				sideLength = ring - 1;
			} else if(side > 4) {
				sideLength = ring + 1;
			} else {
				sideLength = ring;
			}
			step = 0;
		}
		return offset;
	}

	@Override
	public int[] forward(int side) {
		while(this.side != side) {
			next();
		}
		return offset;
	}

}
