import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Main extends JPanel implements ActionListener, MouseListener, MouseMotionListener, KeyListener{
	public static int CELL_SIZE = 15;
	private int interval = 80;
	private Cell[][] cells = null;
	private Cell[][] original = null;
	private final Color BG_COLOR = new Color(150,150,100);
	private final Color GRID_COLOR = new Color(200,200,255);
	private int xDisplace = 0, yDisplace = 0, buttonHeld = 0;
	private boolean running = false;
	public static Timer tmr;
	public static TimerTask task;
	private ArrayList<Integer> survive, born;

	public static void main(String[] args){
		JFrame frm = new JFrame();
		frm.setTitle("Lyfe: A Cell Automaton Simulator");
		frm.setSize(600,600);
		frm.setLayout(new BorderLayout());
		frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Main pnl = new Main();
		pnl.initMain();

		//Menu things:
		JMenuBar bar = new JMenuBar();
		JMenu fileM = new JMenu("File");
		JMenuItem newM = new JMenuItem("New");
		JMenuItem openM = new JMenuItem("Open");
		JMenuItem saveM = new JMenuItem("Save");
		JMenuItem exitM = new JMenuItem("Exit");
		newM.addActionListener(pnl);
		openM.addActionListener(pnl);
		saveM.addActionListener(pnl);
		exitM.addActionListener(pnl);
		newM.setActionCommand("new");
		openM.setActionCommand("open");
		saveM.setActionCommand("save");
		exitM.setActionCommand("exit");
		fileM.add(newM);
		fileM.add(openM);
		fileM.add(saveM);
		fileM.add(exitM);
		bar.add(fileM);
		frm.setJMenuBar(bar);
		
		//Option panel stuff:
		JPanel optionPnl = new JPanel();
		optionPnl.setLayout(new GridLayout(2, 3));
		JButton start = new JButton("Start");
		JButton stop = new JButton("Stop");
		JButton zoomIn = new JButton("Zoom in");
		JButton zoomOut = new JButton("Zoom out");
		JButton faster = new JButton("Faster");
		JButton slower = new JButton("Slower");
		start.addActionListener(pnl);
		stop.addActionListener(pnl);
		zoomIn.addActionListener(pnl);
		zoomOut.addActionListener(pnl);
		faster.addActionListener(pnl);
		slower.addActionListener(pnl);
		start.setActionCommand("start");
		stop.setActionCommand("stop");
		zoomIn.setActionCommand("in");
		zoomOut.setActionCommand("out");
		faster.setActionCommand("faster");
		slower.setActionCommand("slower");
		start.setFocusable(false);
		stop.setFocusable(false);
		zoomIn.setFocusable(false);
		zoomOut.setFocusable(false);
		faster.setFocusable(false);
		slower.setFocusable(false);
		optionPnl.add(start);
		optionPnl.add(zoomIn);
		optionPnl.add(faster);
		optionPnl.add(stop);
		optionPnl.add(zoomOut);
		optionPnl.add(slower);


		frm.add(pnl);
		frm.add(optionPnl, BorderLayout.SOUTH);
		frm.addKeyListener(pnl);

		frm.setVisible(true);
	}
	/**
	 * Initializes variables and sets up listeners for the Main panel.
	 */
	public void initMain(){
		addMouseListener(this);
		addMouseMotionListener(this);
		survive = new ArrayList<Integer>();
		born = new ArrayList<Integer>();
		tmr = new Timer();
	}
	/**
	 * Starts ticking at the adjustable interval.
	 */
	public void startCycle(){
		//Don't know if this is really needed.
		stopCycle();
		tmr.purge();
		task = new TimerTask(){
			public void run(){
				tick();
			}
		};
		tmr.schedule(task, 0, interval);
		running = true;
	}
	/**
	 * Stops the animation task.
	 */
	public void stopCycle(){
		if (task != null){
			task.cancel();
		}
		running = false;
	}
	public void paint(Graphics g){
		g.setColor(BG_COLOR);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());

		if (cells == null){
			return;
		}
		
		//Paint cells:
		for (int i = 0; i < cells.length; ++i){
			for (int j = 0; j < cells[0].length; ++j){
				int x = CELL_SIZE * (i + xDisplace);
				int y = CELL_SIZE * (j + yDisplace);
				cells[i][j].paint(g, x, y);
			}
		}

		//Paint grid:
		g.setColor(GRID_COLOR);
		for (int i = 0; i < cells.length; ++i){
			g.drawLine((i + xDisplace) * CELL_SIZE,
				yDisplace * CELL_SIZE,
				(i + xDisplace) * CELL_SIZE,
				(cells[0].length - 1 + yDisplace) * CELL_SIZE);
		}
		for (int j = 0; j < cells[0].length; ++j){
			g.drawLine(xDisplace * CELL_SIZE,
				(j + yDisplace) * CELL_SIZE, 
				(cells.length - 1 + xDisplace) * CELL_SIZE,
				(j + yDisplace) * CELL_SIZE);
		}
	}
	/**
	 * Advances cells one generation.
	 */
	private void tick(){
		if (cells == null) {
			return;
		}
		updateAdjacent();
		advance();
		repaint();
	}
	/**
	 * Updates cells' adjacent counters.
	 */
	private void updateAdjacent(){
		for (int i = 0; i < cells.length; ++i){
			for (int j = 0; j < cells[0].length; ++j){
				if (cells[i][j].getState() == 1){
					if (i - 1 > 0){
						if (j - 1 > 0){
							cells[i-1][j-1].increment();
						}
						cells[i-1][j].increment();
						if (j + 1 < cells[0].length){
							cells[i-1][j+1].increment();
						}
					}
					if (j - 1 > 0){
						cells[i][j-1].increment();
					}
					if (j + 1 < cells[0].length){
						cells[i][j+1].increment();
					}
					if (i + 1 < cells.length){
						if (j - 1 > 0){
							cells[i+1][j-1].increment();
						}
						cells[i+1][j].increment();
						if (j + 1 < cells[0].length){
							cells[i+1][j+1].increment();
						}
					}
				}
			}
		}
		repaint();
	}
	/**
	 * Advances all cells to their next states.
	 */
	private void advance(){
		for (int i = 0; i < cells.length; ++i){
			for (int j = 0; j < cells[0].length; ++j){
				int adjacent = cells[i][j].getAdjacent();
				int state = cells[i][j].getState();
				if (state == 1){
					if (survive.contains(adjacent)){
						cells[i][j].advance(1);
					} else{
						cells[i][j].advance(0);
					}
				} else{
					if (born.contains(adjacent)){
						cells[i][j].advance(1);
					} else{
						cells[i][j].advance(0);
					}
				}
			}
		}
	}

//Listeners:
	public void actionPerformed(ActionEvent ae){
		String command = ae.getActionCommand();
		if (command.equals("new")){
			doNew();

		} else if (command.equals("open")){
			//TODO
		} else if (command.equals("save")){
			//TODO
		} else if (command.equals("exit")){
			System.exit(0);
		} else if (command.equals("start")){
			if (original != null){
				for (int i = 0; i < original.length; ++i){
					for (int j = 0; j < original[0].length; ++j){
						cells[i][j].setState(original[i][j].getState());
					}
				}
				startCycle();
			}
		} else if (command.equals("stop")){
			if (original != null){
				for (int i = 0; i < original.length; ++i){
					for (int j = 0; j < original[0].length; ++j){
						cells[i][j].setState(original[i][j].getState());
					}
				}
				stopCycle();
			}
		} else if (command.equals("out")){
			//TODO Change x/y diplacement to adjust better.
			if (CELL_SIZE == 1){
				return;
			}
			if (CELL_SIZE <= 5){
				CELL_SIZE--;
			} else {
				CELL_SIZE -= 5;
			}
		} else if (command.equals("in")){
			if (CELL_SIZE < 5){
				CELL_SIZE++;
			} else if (CELL_SIZE < 40){
				CELL_SIZE += 5;
			}
		} else if (command.equals("slower")){
			if (running && interval < 640){
				interval *= 2;
				startCycle();
			}
		} else if (command.equals("faster")){
			if (running && interval > 5){
				interval /= 2;
				startCycle();
			}
		}
		repaint();
	}
	/**
	 * Prompts user to enter specifications for a new Lyfe map, and resets the
	 * app to those specs.
	 */
	public void doNew(){
		JTextField ruleFld = new JTextField();
		JTextField xFld = new JTextField();
		JTextField yFld = new JTextField();
		JLabel ruleLbl = new JLabel("RuleSet (as survive/born)\n(Ex. 23/3):");
		JLabel xLbl = new JLabel("Width:");
		JLabel yLbl = new JLabel("Height:");
		JComponent[] cmp = new JComponent[]{
			ruleLbl, ruleFld, xLbl, xFld, yLbl, yFld};
		JOptionPane.showMessageDialog(null, cmp, "New Lyfe Map", JOptionPane.PLAIN_MESSAGE);
		original = null;
		cells = null;
		survive = new ArrayList<Integer>();
		born = new ArrayList<Integer>();
		xDisplace = 0;
		yDisplace = 0;
		try{
			String str = ruleFld.getText();
			boolean found = false;
			int i = 0;
			//Written to purposefully throw exception if '/' not found.
			while (!found){
				if (str.charAt(i) == '/'){
					found = true;
				} else{
					i++;
				}
			}
			for (int j = 0; j < i; ++j){
				survive.add(Integer.parseInt(str.charAt(j) + ""));
			}
			for (int j = i + 1; j < str.length(); ++j){
				born.add(Integer.parseInt(str.charAt(j) + ""));
			}
			str = xFld.getText();
			int width = Integer.parseInt(str);
			str = yFld.getText();
			int height = Integer.parseInt(str);
			original = new Cell[width][height];
			cells = new Cell[width][height];
			for (int x = 0; x < width; ++x){
				for (int y = 0; y < height; ++y){
					original[x][y] = new Cell();
					cells[x][y] = new Cell();
				}
			}
		} catch(Exception e){
			JOptionPane.showMessageDialog(null, "Invalid input!");
		}
		repaint();
	}
	public void mousePressed(MouseEvent me){
		if (original == null){
			return;
		}
		buttonHeld = me.getButton();
		Point pt = me.getPoint();
		int x = pt.x / CELL_SIZE - xDisplace;
		int y = pt.y / CELL_SIZE - yDisplace;
		if (x >= 0 && x < original.length && y >= 0 &&
				y < original[0].length){
			if (buttonHeld == MouseEvent.BUTTON1){
				original[x][y].setState(1);
				cells[x][y].setState(1);
			} else if (buttonHeld == MouseEvent.BUTTON3){
				original[x][y].setState(0);
				cells[x][y].setState(0);
			}
		}
		repaint();
	}
	public void mouseReleased(MouseEvent me){

	}
	public void mouseClicked(MouseEvent me){}
	public void mouseEntered(MouseEvent me){}
	public void mouseExited(MouseEvent me){}
	public void mouseMoved(MouseEvent me){}
	public void mouseDragged(MouseEvent me){
		if (original == null){
			return;
		}
		Point pt = me.getPoint();
		int x = pt.x / CELL_SIZE - xDisplace;
		int y = pt.y / CELL_SIZE - yDisplace;
		if (x >= 0 && x < original.length && y >= 0 &&
				y < original[0].length){
			if (buttonHeld == MouseEvent.BUTTON1){
				original[x][y].setState(1);
				cells[x][y].setState(1);
			} else if (buttonHeld == MouseEvent.BUTTON3){
				original[x][y].setState(0);
				cells[x][y].setState(0);
			}
		}
		repaint();
	}
	public void keyPressed(KeyEvent ke){
		int code = ke.getKeyCode();
		if (cells == null){
			return;
		}
		switch(code){
			case 38:
			case 87:
				if ((yDisplace + 1) * CELL_SIZE < this.getHeight()){
					yDisplace++;
				}
				break;
			case 40:
			case 83:
				if (yDisplace > 1 - cells[0].length){
					yDisplace--;
				}
				break;
			case 37:
			case 65:
				if ((xDisplace + 1) * CELL_SIZE < this.getWidth()){
					xDisplace++;
				}
				break;
			case 39:
			case 68:
				if (xDisplace > 1 - cells.length){
					xDisplace--;
				}
				break;
		}
		repaint();
	}
	public void keyReleased(KeyEvent ke){}
	public void keyTyped(KeyEvent ke){}
}
