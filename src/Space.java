

import java.awt.Color;

class Space {
	int nr, nc;
	Color[][] space;
	Space(int nr, int nc) {
		this.nr = nr;
		this.nc = nc;
		space = new Color[nr][nc];
	}
	Color get(int r, int c) {
		return space[r][c];
	}
	void set(int r, int c, Color color) {
		space[r][c] = color;
	}
	boolean isEmpty(int r, int c) {
		return get(r, c) == null;
	}
	void clear() {
		space = new Color[nr][nc];
	}
	int countAndRemoveFullRows() {
		int count = 0;
		for (int r = nr-1; r >= 0; r--) {
			boolean full = true;
			for (int c = 0; c < nc; c++) {
				if (isEmpty(r, c)) full = false;
			}
			if (full) {
				count++;
			} else {
				for (int c = 0; c < nc; c++) {
					Color temp = space[r][c];
					space[r][c] = null;
					if (r+count < nr) {
						space[r+count][c] = temp;
					}
				}
			}
		}
		return count;
	}
	
	void fill(int r, int c, Shape shape) {
//		System.out.println("fill shape at (" + r + ", " + c+ ")");
		Color color = shape.color;
		for (int[] offset: shape.offsets) {
			set(r-offset[1], c+offset[0], color);
		}
	}
	boolean fit(int r, int c, Shape shape) {
//		System.out.println("fit shape at (" + r + ", " + c+ ")");
		for (int[] offset: shape.offsets) {
			int R = r-offset[1], C = c+offset[0];
			if (R < 0 || R >= nr || C < 0 || C >= nc) return false;
			if (!isEmpty(R, C)) return false;
		}
		return true;
	}
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int r = 0; r < nr; r++) {
			for (int c = 0; c < nc; c++) {
				if (isEmpty(r, c)) { sb.append(" null"); }
				else sb.append("color");
				if (c != nc-1) {
					sb.append(", ");
				}
			}
			sb.append("\n");
		}
		return sb.toString();
	}
}
