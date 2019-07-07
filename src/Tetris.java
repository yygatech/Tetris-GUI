

import java.awt.Frame;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.LinkedHashMap;

import javax.swing.Timer;

public class Tetris extends Frame {
	
	// tunable game parameters
	final String[] paramKeys = {"M", "N", "S", "W", "H", "Z"};
	final String[] paramTexts = {"Score multiplier", "Level threshold (row)", "Speed",
			"Width (block)", "Height (block)", "Magnifier"};
	LinkedHashMap<String, Parameter<? extends Number>> params = new LinkedHashMap<>();
	
	final int speed0 = 500;
	
	int level = 1;
	int score = 0;
	int linesTotal = 0;
	int linesAtCurrentLevel = 0;
	int speed = speed0;
	
	Space space;
	CvTetris canvas;
	Listener listener;
	Timer timer;
	
	// game status
	Status status;
	
	/*********************/
	/*********************/
	
	Tetris() {
		super("Tetris: interface");
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) { System.exit(0); }
		});
		
		params.put(paramKeys[0], new Parameter<Integer>(paramTexts[0], new Integer[] {1,2,3,4,5,6,7,8,9,10}, 0, 1));
		params.put(paramKeys[1], new Parameter<Integer>(paramTexts[1], new Integer[] {10,20,30,40,50}, 0, 2));
		params.put(paramKeys[2], new Parameter<Float>(paramTexts[2], new Float[] {0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 1.0f}, 4, 0.1f));
		params.put(paramKeys[3], new Parameter<Integer>(paramTexts[3], new Integer[] {10,12,14,16,18,20,22,24,26,28,30}, 0, 10));
		params.put(paramKeys[4], new Parameter<Integer>(paramTexts[4], new Integer[] {20,22,24,26,28,30}, 0, 20));
		params.put(paramKeys[5], new Parameter<Integer>(paramTexts[5], new Integer[] {1,2,3,4,5}, 1, 1));
		
		status = Status.INITIAL;
		
		// set timer
		timer = new Timer(speed0, null);
		timer.setInitialDelay(2000);
		timer.start();
		
		reset();
	}
	
	void reset() {
		// remove listeners from timer
		ActionListener[] listeners = timer.getActionListeners();
		for (ActionListener listener: listeners) timer.removeActionListener(listener);
		
		// set canvas
		setSpace();
		setSize();
		setCanvas();
		
		// set canvas and timer listener
		setListener();
		
		status = Status.ON;
	}
	
	void setSpace() {
		space = new Space(getH(), getW());
	}
	
	void setSize() {
		int base = (4 + getZ()) * 5;
		int h = base * (getH() + 2), w = base * (getW() + 12);
		setSize(w, h);
	}
	
	void setCanvas() {
		if (canvas != null) remove(canvas);
		canvas = new CvTetris(this);
		add("Center",canvas);
		setVisible(true);
	}
	
	void setListener() {
		listener = new Listener(this, timer);
		canvas.addMouseListener(listener);
		canvas.addMouseWheelListener(listener);
		canvas.addMouseMotionListener(listener);
		canvas.addKeyListener(listener);
		timer.addActionListener(listener);
	}
	
	/*********************/
	/*********************/
	
//	void setStatus(Status status) {
//		this.status = status;
//	}
	
	int getM() { return params.get(paramKeys[0]).getValue().intValue(); }
	int getN() { return params.get(paramKeys[1]).getValue().intValue(); }
	float getS() { return params.get(paramKeys[2]).getValue().floatValue(); }
	int getW() { return params.get(paramKeys[3]).getValue().intValue(); }
	int getH() { return params.get(paramKeys[4]).getValue().intValue(); }
	int getZ() { return params.get(paramKeys[5]).getValue().intValue(); }
	
	void updateStats(int inc) {
		int scoreIncrease = inc * (inc+1) / 2 * level * getM();
		System.out.println("Scored " + scoreIncrease);
		score += scoreIncrease;
		linesTotal += inc;
		linesAtCurrentLevel += inc;
		while (linesAtCurrentLevel >= getN()) {
			speed = (int) (speed0 / (getS() * level + 1));
			timer.setDelay(speed);
			level++;
			System.out.println("Level up to level " + level);
			linesAtCurrentLevel = 0;
			System.out.println("Speed: " + speed);
		}
		
	}
	
	void penalize() {
		int penalty = level * getM();
		System.out.println("Penalized for " + penalty);
		score -= penalty;
		if (score < 0) score = 0;
	}
	
	public static void main(String[] args) {
		new Tetris();
	}

}
