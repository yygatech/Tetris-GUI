

import java.awt.Canvas;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Iterator;
import java.util.Random;

abstract class Configuration extends Canvas {
	
	Tetris t;
	Random rand = new Random();
	Shape shape, nextShape;
	
	// parameter
	float u = 1f;	// u for unit size of a block
	float rWidth, rHeight;
	
	// rectangle areas
	Rectangle.Float mRect, nRect;
	Point.Float pos;
	Rectangle.Float[] mCellRects;
	float buttonWidth = u*4, buttonHeight = u*1.5f;
	Rectangle.Float qRect, oRect, rRect, pRect;
	Rectangle.Float iRect = new Rectangle.Float(u*2, u*3, u*6, u*6f);
	Rectangle.Float tRect;
	Rectangle.Float[] tCellRects;
	Point.Float[][] markGroups;
	
	/*********************/
	/*********************/
	
	Configuration(Tetris t) {
		this.t = t;
		
		// canvas size
		rWidth = u * (t.getW() + 12);
		rHeight = u * (t.getH() + 2);
		
		// rectangle areas
		mRect = new Rectangle.Float(u*-t.getW(), u*t.getH()/2, u*t.getW(), u*t.getH());
		mCellRects = getCellRects(mRect, 4, 2);
		nRect = new Rectangle.Float(u*2, mRect.y, u*6, u*5);
		qRect = new Rectangle.Float(u*2, mRect.y-mRect.height+buttonHeight, buttonWidth, buttonHeight);
		oRect = new Rectangle.Float(u*2, qRect.y+buttonHeight+u*0.5f, buttonWidth, buttonHeight);
		rRect = new Rectangle.Float(u*2, oRect.y+buttonHeight+u*0.5f, buttonWidth, buttonHeight);
		pRect = new Rectangle.Float(
				mRect.x+mRect.width/2-buttonWidth/2, mRect.y-mRect.height/2+buttonHeight/2, 
				buttonWidth, buttonHeight);
		tRect = new Rectangle.Float(u*2, mRect.y, u*8, mRect.height-u*6);
		tCellRects = getCellRects(tRect, 6, 1);
		markGroups = new Point.Float[6][];
		Iterator<Parameter<? extends Number>> itr = t.params.values().iterator();
		for (int r = 0; r < 6; r++) {
			markGroups[r] = getMarks(tCellRects[r], itr.next());
		}
		
		shape = Shape.newRandInstance();
		nextShape = Shape.newRandInstance();
		displayShape(shape);
	}
	
	// separate a rectangle into cells
	Rectangle.Float[] getCellRects(Rectangle.Float rect, int nr, int nc) {
		Rectangle.Float[] cellRects = new Rectangle.Float[nr*nc];
		for (int r = 0; r < nr; r++) {
			for (int c = 0; c < nc; c++) {
				Rectangle.Float cellRect = new Rectangle.Float(
						rect.x+rect.width/nc*c, rect.y-rect.height/nr*r,
						rect.width/nc, rect.height/nr);
				cellRects[c+r*nc] = cellRect;
			}
		}
		return cellRects;
	}
	
	<T extends Number> Point.Float[] getMarks(Rectangle.Float rect, Parameter<T> parameter) {
		T[] range = parameter.getRange();
		int nc = range.length;
		Point.Float[] marks = new Point.Float[nc];
		float xStart = rect.x+0.1f*rect.width, xEnd = rect.x+0.9f*rect.width;
		float y = rect.y-0.5f*rect.height;
		float start = range[0].floatValue(), end = range[nc-1].floatValue(), len = end-start;
		for (int c = 0; c < nc; c++) {
			float value = range[c].floatValue();
			float leftPortion = (value-start)/len, rightPortion = (end-value)/len;
			float x = xStart * rightPortion + xEnd * leftPortion;
			marks[c] = new Point.Float(x, y);
		}
		return marks;
	}
	
	/*********************/
	/*********************/
	// space converter
	int float2Row(float y) { return (int)((mRect.y-y)/u); }
	int float2Col(float x) { return (int)((x-mRect.x)/u); }
	float row2Float(int r) { return mRect.y-u*r; }
	float col2Float(int c) { return mRect.x+u*c; }
	
	/*********************/
	// shape related functions
	void changeShape() {
		shape = nextShape;
		nextShape = Shape.newRandInstance();
	}
	
	void displayShape(Shape shape) {
		pos = new Point.Float(mRect.x+mRect.width/2, mRect.y-shape.top);	// TODO: may be modified
	}
}
