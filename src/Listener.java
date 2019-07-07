

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.Timer;

class Listener implements MouseListener, MouseWheelListener, MouseMotionListener, KeyListener, ActionListener {
	Tetris t;
	CvTetris c;
	Timer timer;
	
	boolean inShape = false;
	
	Listener(Tetris tetris, Timer timer) {
		t = tetris;
		c = t.canvas;
		this.timer = timer;
	}
	
	@Override
	public void mousePressed(MouseEvent evt) {
		// global triggers
		float x = c.fx(evt.getX()), y = c.fy(evt.getY());
		if (inOrOnRect(x, y, c.qRect)) {		// quit button
			System.out.println("Quit button pressed!");
			System.exit(0);
		}
		// sub status
		if (t.status != Status.OPTIONS) {
			if (inOrOnRect(x, y, c.oRect)) {			// options button
				System.out.println("Options button pressed!");
				t.status = Status.OPTIONS;
			}
			if (t.status == Status.ON && !inOrOnRect(x, y, c.mRect)) {		// not in main area
				int button = evt.getButton();
				if (button == 1) { 
					shiftLeft();
				}
				if (button == 2) {
					speedUp();
				}
				if (button == 3) {
					shiftRight();
				}
			}
		} else if (t.status == Status.OPTIONS){		// OPTIONS status
			if (inOrOnRect(x, y, c.oRect)) {			// resume button
				t.status = Status.ON;
			} else if (inOrOnRect(x, y, c.rRect)) {	// restart button
				t.reset();
			} else if (inOrOnRect(x, y, c.tRect)) {	// tunable bars
				Rectangle.Float rect = c.tRect;
				int r = (int) ((rect.y - y) / (rect.height / 6));
				Point.Float[] marks = c.markGroups[r];
				int nc = marks.length;
				float minDist = marks[nc-1].x - marks[0].x;
				int nearestIdx = 0;
				for (int c = 0; c < nc; c++) {
					float dist = Math.abs(marks[c].x-x);
					if (dist < minDist) {
						minDist = dist;
						nearestIdx = c;
					}
				}
				// set parameter 0-2
				t.params.get(t.paramKeys[r]).setValueIdx(nearestIdx);
				// set parameter 3-4
				if (r == 3 || r == 4) {
					t.reset();
				}
				// set parameter 5
				if (r == 5) t.setSize();
			} else if (inOrOnRect(x, y, c.mRect)) {		// choose optional shapes
					Rectangle.Float rect = c.mRect;
					float xM = rect.x, yM = rect.y, 
							wCell = rect.width/2, hCell = rect.height/4;
					float xRelative = x - xM, yRelative = y - yM;
					int c = (int) (xRelative / wCell); if (c == 2) c--;
					int r = (int) (-yRelative / hCell); if (r == 4) r--;
					int idx = c + r * 2;
					String shapeName = Shape.EXT_NAMES[idx];
					if (Shape.shapePool.contains(shapeName)) {
						Shape.shapePool.remove(shapeName);
					} else {
						Shape.shapePool.add(shapeName);
					}
					Shape.namePool = Shape.shapePool.toArray(new String[Shape.shapePool.size()]);
			}
		}
		repaint();
	}
	@Override
	public void mouseReleased(MouseEvent evt) {
		if (t.status != Status.ON) return;
		
		float x = c.fx(evt.getX()), y = c.fy(evt.getY());
		if (!inOrOnRect(x, y, c.mRect)) {
			int button = evt.getButton();
			if (button == 2) {
				resumeSpeed();
			}
		}
	}
	

	@Override
	public void mouseWheelMoved(MouseWheelEvent evt) {
		if (t.status != Status.ON) return;
		
		float x = c.fx(evt.getX()), y = c.fy(evt.getY());
		if (!inOrOnRect(x, y, c.mRect)) {
			int notches = evt.getWheelRotation();
//			System.out.println("rotation amount: " + notches);
			int threshold = 1;
			if (notches >= threshold) {			// rotate left
				rotateLeft();
			} else if (notches <= -threshold) {		// rotate right
				rotateRight();
			}
			repaint();
		}
	}
	@Override
	public void mouseMoved(MouseEvent evt) {
		if (t.status != Status.ON && t.status != Status.PAUSE) return;
		
		float x = c.fx(evt.getX()), y = c.fy(evt.getY());
		Status prevStatus = t.status;
		if (inOrOnRect(x, y, c.mRect)) {				// move into main area
			t.status = Status.PAUSE;
			boolean prevInShapeStatus = inShape;
			if (inOrOnShape(x, y, c.shape, c.pos.x, c.pos.y)) {
				inShape = true;
			} else inShape = false;
			if (!prevInShapeStatus && inShape) {		// penalty
				Shape prevShape = c.shape;
				boolean nextShapeExists = skipToNextShape();
				while (nextShapeExists && c.shape.name == prevShape.name) { 
					nextShapeExists = skipToNextShape();
				}
				if (nextShapeExists) t.penalize();
				repaint();
			}
		} else {										// move out of main area
			t.status = Status.ON;
		}
		if (t.status != prevStatus) {
			repaint();
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
//		System.out.println("key pressed!");
		if (t.status != Status.ON) return;
		
		int keyCode = e.getKeyCode();
		if (keyCode == KeyEvent.VK_DOWN) {
			speedUp();
		} else if (keyCode == KeyEvent.VK_UP) {
			rotateRight();
		} else if (keyCode == KeyEvent.VK_LEFT) {
			shiftLeft();
		} else if (keyCode == KeyEvent.VK_RIGHT) {
			shiftRight();
		} else if (keyCode == KeyEvent.VK_SPACE) {
			// TODO pause
		}
		repaint();
	}

	@Override
	public void keyReleased(KeyEvent e) {
//		System.out.println("key released!");
		if (t.status != Status.ON) return;
		
		int keyCode = e.getKeyCode();
		if (keyCode == KeyEvent.VK_DOWN) {
			resumeSpeed();
		}
	}
	
	// for timer
	@Override
	public void actionPerformed(ActionEvent evt) {
//		System.out.println("Timer's action performed!");
		if (t.status == Status.ON) {
			// adjust y
			if (t.space.fit(c.float2Row(c.pos.y-1f), c.float2Col(c.pos.x), c.shape)) {
				c.pos.y--;
			} else {		// cannot move downwards any more
				t.space.fill(c.float2Row(c.pos.y), c.float2Col(c.pos.x), c.shape);
				
				c.pos.x = -5f;
				c.pos.y = Math.max(10f-c.nextShape.top, -10f);	// TODO
				
				switchToNextShape();
				
				// full row is erased
				t.updateStats(t.space.countAndRemoveFullRows());
			}
			repaint();
		}
	}
	
	private void repaint() {
		c.repaint();
	}
	
	private boolean inOrOnRect(float x, float y, Rectangle.Float rect) {
		return rect.x <= x && x <= rect.x+rect.width && rect.y-rect.height <= y && y <= rect.y;
	}
	
	private boolean inOrOnShape(float x, float y, Shape shape, float xAt, float yAt) {
		boolean inOrOn = false;
		for (int[] offset: shape.offsets) {
			float xOffset = xAt + c.u * offset[0];
			float yOffset = yAt + c.u * offset[1];
			if (inOrOnBlock(x, y, xOffset, yOffset)) inOrOn = true;
		}
		return inOrOn;
	}
	
	private boolean inOrOnBlock(float x, float y, float xAt, float yAt) {
		return x >= xAt && x <= xAt + c.u && y >= yAt - c.u && y <= yAt;
	}
	
	private void speedUp() {
		timer.setDelay(Math.min(t.speed, 100));
	}
	
	private void resumeSpeed() {
		timer.setDelay(t.speed);
	}
	private void shiftLeft() {
		if (t.space.fit(c.float2Row(c.pos.y), c.float2Col(c.pos.x-1f), c.shape)) {
//			System.out.println("shift left to (" + convert2Row(pos.y) + ", " + convert2Col(pos.x-1f) + ")");
			c.pos.x -= 1f;
		}
	}
	private void shiftRight() {
		if (t.space.fit(c.float2Row(c.pos.y), c.float2Col(c.pos.x+1f), c.shape)) {
//			System.out.println("shift right to (" + convert2Row(pos.y) + ", " + convert2Col(pos.x+1f) + ")");
			c.pos.x += 1f;
		}
	}
	private void rotateLeft() {
		Shape shapeAfterRotation = c.shape;
		shapeAfterRotation = c.shape.rotateLeft();
		if (t.space.fit(c.float2Row(c.pos.y), c.float2Col(c.pos.x), shapeAfterRotation)) {
			c.shape = shapeAfterRotation;
		}
	}
	private void rotateRight() {
		Shape shapeAfterRotation = c.shape;
		shapeAfterRotation = c.shape.rotateRight();
		if (t.space.fit(c.float2Row(c.pos.y), c.float2Col(c.pos.x), shapeAfterRotation)) {
			c.shape = shapeAfterRotation;
		}
	}
	
	private void switchToNextShape() {
		if (!t.space.fit(c.float2Row(c.pos.y), c.float2Col(c.pos.x), c.nextShape)) {	// if end of game
			t.status = Status.END;
			timer.stop();
		}
		c.shape = c.nextShape;
		c.nextShape = Shape.newRandInstance();
	}
	
	private boolean skipToNextShape() {
		if (t.space.fit(c.float2Row(c.pos.y), c.float2Col(c.pos.x), c.nextShape)) {	// if end of game
			c.shape = c.nextShape;
			c.nextShape = Shape.newRandInstance();
			return true;
		}
		return false;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}
