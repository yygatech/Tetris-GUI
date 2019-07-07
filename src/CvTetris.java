

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Iterator;

class CvTetris extends Configuration {
	
	Graphics g;
	
	// paint related parameters
	float pixelSize;
	int centerX, centerY;
	
	/*********************/
	/*********************/
	// constructor
	CvTetris(Tetris t) { 
		super(t);
	}
	
	/*********************/
	/*********************/
	// paint
	@Override
	public void paint(Graphics g) {
		this.g = g;
		initgr();
		if (t.status != Status.OPTIONS) {
			// 1 draw buttons
			drawButton(oRect, "OPTIONS");
			drawButton(qRect, "QUIT");
			
			// 2 draw main rectangle
			drawMain(mRect);
			if (t.status == Status.PAUSE) {
				drawButton(pRect, "PAUSE", Color.BLUE);
			}
			
			// 3 write text info
			displayInfo(iRect);
			
			// 4 draw next rectangle
			drawNext(nRect);
		} else {
			// 1 draw buttons
			drawButton(rRect, "RESTART");
			drawButton(oRect, "RESUME");
			drawButton(qRect, "QUIT");
			
			// 2 draw shape options
			drawShapeOptions(mRect);
			
			// 3 draw tunables
			drawTunableOptions();
		}
	}
	
	void initgr() {
		Dimension d = getSize();
		int maxX = d.width - 1, maxY = d.height - 1;
		pixelSize = Math.max(rWidth / maxX, rHeight / maxY);
		
		centerX = maxX * (t.getW() + 1) / (t.getW() + 12); centerY = maxY / 2;
	}
	
	/*********************/
	
	// coordinates transformation
	int iX(float x) { return Math.round(centerX + x / pixelSize); }
	int iY(float y) { return Math.round(centerY - y / pixelSize); }
	int iD(float d) { return Math.round(d / pixelSize); }
	float fx(int X) { return pixelSize * (X - centerX); }
	float fy(int Y) { return pixelSize * (centerY - Y); }
//	float fd(int D) { return pixelSize * D; }
	float fd(float D) { return pixelSize * D; }
	
	/*********************/
	
	// draw areas
	void drawMain(Rectangle.Float rect) {
		g.drawRect(iX(rect.x), iY(rect.y), iD(rect.width), iD(rect.height));
		// draw shapes in main area
		drawSpace(t.space);
		drawShape(shape, pos);
	}
	
	void drawNext(Rectangle.Float rect) {
		g.drawRect(iX(rect.x), iY(rect.y), iD(rect.width), iD(rect.height));
		drawShape(nextShape, 
				new Point.Float(rect.x+rect.width/2-nextShape.center.x, rect.y-rect.height/2-nextShape.center.y));
	}
	
	void drawShapeOptions(Rectangle.Float rect) {
		int nr = 4, nc = 2;
		float wCell = rect.width/nc, hCell = rect.height/nr;
		int idx = 0;
		for (int r = 0; r < nr; r++) {
			for (int c = 0; c < nc; c++) {
				float x = rect.x + wCell*(0.5f+c), y = rect.y - hCell*(0.5f+r);	// center of each cell
				if (Shape.shapePool.contains(Shape.EXT_NAMES[idx])) {
					g.drawRect(iX(x-wCell*0.4f), iY(y+hCell*0.4f), iD(wCell*0.8f), iD(hCell*0.8f));
				}
				Shape shape = new Shape(Shape.EXT_NAMES[idx]);
				drawShape(shape, new Point.Float(x-shape.center.x, y-shape.center.y));
				idx++;
			}
		}
	}
	
	/*********************/
	
	// draw blocks
	void drawSpace(Space space) {
		if (space == null) return;
		for (int r = 0; r < space.nr; r++) {
			for (int c = 0; c < space.nc; c++) {
				if (!space.isEmpty(r, c)) {
					Color color = space.get(r, c);
					drawBlock(new Point.Float(col2Float(c), row2Float(r)), color);
				}
			}
		}
	}
	
	void drawShape(Shape shape, Point.Float p1) {
		for (int[] offset: shape.offsets) {
			drawBlock(new Point.Float(p1.x + u*offset[0], p1.y + u*offset[1]), shape.color);
		}
	}
	
	void drawBlock(Point.Float p1, Color color) {
		Color prevColor = g.getColor();
		g.setColor(color);
		g.fillRect(iX(p1.x), iY(p1.y), iD(u), iD(u));
		g.setColor(prevColor);
		g.drawRect(iX(p1.x), iY(p1.y), iD(u), iD(u));
	}
	
	/*********************/
	
	// draw tunable bars
	void drawTunableOptions() {
		Iterator<Parameter<? extends Number>> itr = t.params.values().iterator();
		for (int r = 0; r < 6; r++) {
			drawTunable(markGroups[r], itr.next());
		}
	}
	
	<T extends Number> void drawTunable(Point.Float[] marks, Parameter<T> param) {
		int nc = marks.length;
		int valueIdx = param.getValueIdx();
		T[] range = param.getRange();
		Point.Float start = marks[0], end = marks[nc-1], curr = marks[valueIdx];
		Point.Float middle = new Point.Float(start.x+(end.x-start.x)/2, start.y);
		// draw lines
		g.drawLine(iX(start.x), iY(start.y), iX(end.x), iY(end.y));
		// draw tick
		g.fillRect(iX(curr.x)-1, iY(curr.y+u*0.25f), 5, iD(u*0.5f));
		// draw labels
		String text = param.getName();
		int th = g.getFontMetrics().getHeight();
		int tw = g.getFontMetrics().stringWidth(text);
		g.drawString(text, iX(middle.x)-tw/2, iY(middle.y+u*0.5f));
		
		text = range[0].toString();
		tw = g.getFontMetrics().stringWidth(text);
		g.drawString(text, iX(start.x-u*0.5f)-tw, iY(start.y)+th/2);
		
		text = range[nc-1].toString();
		tw = g.getFontMetrics().stringWidth(text);
		g.drawString(text, iX(end.x+u*0.5f), iY(end.y)+th/2);
		
		text = range[valueIdx].toString();
		tw = g.getFontMetrics().stringWidth(text);
		g.drawString(text, iX(curr.x)-tw/2, iY(curr.y-u*0.25f)+th);
	}
	
	/*********************/
	
	// draw a button
		void drawButton(Rectangle.Float rect, String text) {
			// write text
			Font prevFont = g.getFont();
			g.setFont(new Font("Dialog", Font.BOLD, iD(rect.height/2)));
			drawCenteredString(rect, text);
			g.setFont(prevFont);
			// draw border following text
			g.drawRect(iX(rect.x), iY(rect.y), iD(rect.width), iD(rect.height));
		}
		
		void drawButton(Rectangle.Float rect, String text, Color color) {
			Color prevColor = g.getColor();
			g.setColor(color);
			drawButton(rect, text);
			g.setColor(prevColor);
		}
	
	/*********************/
	
	// draw info and string
	void displayInfo(Rectangle.Float rect) {
		// write text
		String[] info = {
				"Level:             " + t.level,
				"Lines:             " + t.linesTotal,
				"Score:             " + t.score
		};
		int n = 3;
		float segment = rect.height / n;
		Font prevFont = g.getFont();
		g.setFont(new Font("Dialog", Font.BOLD, iD(rect.height/n/3)));
		
		for (int i = 0; i < n; i++) {
			float yBase = rect.y - rect.height + (0.5f+i)*segment;
			g.drawString(info[n-1-i], iX(rect.x), iY(yBase));
		}
		g.setFont(prevFont);
	}
	
	void drawCenteredString(Rectangle.Float rect, String text) {
		float xCenter = rect.x+rect.width/2, yCenter = rect.y-rect.height/2;
		int fw = g.getFontMetrics().stringWidth(text), fh = g.getFontMetrics().getHeight();
		int XCenter = iX(xCenter-fd(0.5f*fw));
		int YCenter = iY(yCenter-fd(0.5f*fh));
		g.drawString(text, XCenter, YCenter);
	}
	
	/*********************/
	/*********************/
}
