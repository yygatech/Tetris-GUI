

import java.awt.Color;
import java.awt.Point;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

class Shape {
	static String[] namePool = {"S1","Z1","J3","L9","O4","T0","I2"};
	static Set<String> shapePool = new HashSet<>(Arrays.asList(namePool));
	static final String[][] NAMES = {
			{"I1","I2"},
			{"J0","J3","J6","J9"},
			{"L0","L3","L6","L9"},
			{"O4"},
			{"S1","S2"},
			{"T0","T3","T6","T9"},
			{"Z1","Z2"},
			{"EXTA0","EXTA3","EXTA6","EXTA9"},
			{"EXTB0","EXTB3","EXTB6","EXTB9"},
			{"EXTC1","EXTC2"},
			{"EXTD1","EXTD2"},
			{"EXTE1","EXTE2"},
			{"EXTF0","EXTF3","EXTF6","EXTF9"},
			{"EXTG4"},
			{"EXTH1","EXTH2"}};
	static final String[] EXT_NAMES = {
			"EXTA0", "EXTB0", "EXTC1", "EXTD1", 
			"EXTE1", "EXTF0", "EXTG4", "EXTH1"};
	private static final Map<String, int[][]> offsetMap = new HashMap<>();		// capitalize
	static {
		offsetMap.put(NAMES[0][0], new int[][] {{0,0},{0,-1},{0,-2},{0,-3}});
		offsetMap.put(NAMES[0][1], new int[][] {{0,0},{1,0},{2,0},{3,0}});
		
		offsetMap.put(NAMES[1][0], new int[][] {{0,0},{-1,0},{0,1},{0,2}});
		offsetMap.put(NAMES[1][1], new int[][] {{0,0},{0,1},{1,0},{2,0}});
		offsetMap.put(NAMES[1][2], new int[][] {{0,0},{1,0},{0,-1},{0,-2}});
		offsetMap.put(NAMES[1][3], new int[][] {{0,0},{0,-1},{-1,0},{-2,0}});
		
		offsetMap.put(NAMES[2][0], new int[][] {{0,0},{1,0},{0,1},{0,2}});
		offsetMap.put(NAMES[2][1], new int[][] {{0,0},{0,-1},{1,0},{2,0}});
		offsetMap.put(NAMES[2][2], new int[][] {{0,0},{-1,0},{0,-1},{0,-2}});
		offsetMap.put(NAMES[2][3], new int[][] {{0,0},{0,1},{-1,0},{-2,0}});
		
		offsetMap.put(NAMES[3][0], new int[][] {{0,0},{-1,0},{-1,1},{0,1}});
		
		offsetMap.put(NAMES[4][0], new int[][] {{0,0},{1,0},{0,-1},{-1,-1}});
		offsetMap.put(NAMES[4][1], new int[][] {{0,0},{0,-1},{-1,0},{-1,1}});
		
		offsetMap.put(NAMES[5][0], new int[][] {{0,0},{-1,0},{0,1},{1,0}});
		offsetMap.put(NAMES[5][1], new int[][] {{0,0},{0,1},{1,0},{0,-1}});
		offsetMap.put(NAMES[5][2], new int[][] {{0,0},{1,0},{0,-1},{-1,0}});
		offsetMap.put(NAMES[5][3], new int[][] {{0,0},{0,-1},{-1,0},{0,1}});
		
		offsetMap.put(NAMES[6][0], new int[][] {{0,0},{-1,0},{0,-1},{1,-1}});
		offsetMap.put(NAMES[6][1], new int[][] {{0,0},{0,1},{-1,0},{-1,-1}});
		// EXTA
		offsetMap.put(NAMES[7][0], new int[][] {{0,0},{-1,0},{0,1}});
		offsetMap.put(NAMES[7][1], new int[][] {{0,0},{0,1},{1,0}});
		offsetMap.put(NAMES[7][2], new int[][] {{0,0},{1,0},{0,-1}});
		offsetMap.put(NAMES[7][3], new int[][] {{0,0},{0,-1},{-1,0}});
		// EXTB
		offsetMap.put(NAMES[8][0], new int[][] {{0,0},{-1,1},{1,0}});
		offsetMap.put(NAMES[8][1], new int[][] {{0,0},{1,1},{0,-1}});
		offsetMap.put(NAMES[8][2], new int[][] {{0,0},{1,-1},{-1,0}});
		offsetMap.put(NAMES[8][3], new int[][] {{0,0},{-1,-1},{0,1}});
		// EXTC
		offsetMap.put(NAMES[9][0], new int[][] {{0,0},{1,0},{2,0}});
		offsetMap.put(NAMES[9][1], new int[][] {{0,0},{0,-1},{0,-2}});
		// EXTD
		offsetMap.put(NAMES[10][0], new int[][] {{-1,0},{0,1}});
		offsetMap.put(NAMES[10][1], new int[][] {{-1,1},{0,0}});
		// EXTE
		offsetMap.put(NAMES[11][0], new int[][] {{0,0},{0,-1}});
		offsetMap.put(NAMES[11][1], new int[][] {{0,0},{1,0}});
		// EXTF
		offsetMap.put(NAMES[12][0], new int[][] {{0,0},{-1,-1},{1,-1}});
		offsetMap.put(NAMES[12][1], new int[][] {{0,0},{-1,1},{-1,-1}});
		offsetMap.put(NAMES[12][2], new int[][] {{0,0},{-1,1},{1,1}});
		offsetMap.put(NAMES[12][3], new int[][] {{0,0},{1,1},{1,-1}});
		// EXTG
		offsetMap.put(NAMES[13][0], new int[][] {{0,0}});
		// EXTH
		offsetMap.put(NAMES[14][0], new int[][] {{0,0},{-1,-1},{1,1}});
		offsetMap.put(NAMES[14][1], new int[][] {{0,0},{-1,1},{1,-1}});
		
	}
	private static Random rand = new Random();
	String name;
	String type;
	int orientation;
	Color color;
	
	// dimensions
	int[][] offsets;
	Point.Float center;
	int bottom, top, left, right;
	int height, width;
	
	/*********************/
	// constructor
	Shape(String name) {
		this.name = name;
		type = name.substring(0, name.length()-1);
		orientation = name.charAt(name.length()-1)-'0';
		offsets = offsetMap.get(name);	// original offsets
		
		for (int[] offset: offsets) {
			int x = offset[0], y = offset[1];
			bottom = Math.min(bottom, y-1);
			top = Math.max(top, y);
			left = Math.min(left, x);
			right = Math.max(right, x+1);
		}
		height = top-bottom;
		width = right-left;
		center = new Point.Float(0.5f*width+left, 0.5f*height+bottom);
		switch (type) {
			case "I": color = Color.CYAN; break; 
			case "J": color = Color.BLUE; break;
			case "L": color = Color.RED; break;
			case "O": color = Color.GREEN; break;
			case "S": color = Color.YELLOW; break;
			case "T": color = Color.ORANGE; break;
			case "Z": color = Color.MAGENTA; break;
			
			case "EXTA": color = new Color(165,165,165); break;
			case "EXTB": color = new Color(150,200,90); break;
			case "EXTC": color = new Color(200,150,150); break;
			case "EXTD": color = new Color(220,115,50); break;
			case "EXTE": color = new Color(200, 210, 160); break;
			case "EXTF": color = new Color(150, 135, 85); break;
			case "EXTG": color = new Color(25, 55, 95); break;
			case "EXTH": color = new Color(70, 130, 153); break;
			default:  color = Color.BLACK;
		}
	}
	Shape(String name, Color color) {
		this(name);
		this.color = color;
	}
	Shape(Shape shape) {
		this(shape.name);
	}
	
	/*********************/
	static Shape newRandInstance() {
		return new Shape(namePool[rand.nextInt(namePool.length)]);
	}
	private Shape rotate() {
		if (orientation == 1 || orientation == 2) {
			return new Shape(""+type+(3-orientation));
		}
		else return this;
	}
	Shape rotateLeft() {
		if (orientation % 3 == 0) {
			return new Shape(""+type+(orientation+9)%12);
		} else return rotate();
	}
	
	Shape rotateRight() {
		if (orientation % 3 == 0) {
			return new Shape(""+type+(orientation+3)%12);
		} else return rotate();
	}
 }
