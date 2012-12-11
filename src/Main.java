import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Main extends JPanel implements ActionListener, MouseListener, MouseMotionListener{
	public static int CELL_SIZE = 5;
	private int interval = 20;
	private Cell[][] cells = null;
	private final Color BG_COLOR = new Color(150,150,100);
	private int xDisplace = 0, yDisplace = 0;
	private boolean go = false;
	public static Timer tmr;
	public static TimerTask task;
	private ArrayList<Integer> survive, born;

	public static void main(String[] args){
		JFrame frm = new JFrame();
		frm.setTitle("Lyfe: A Cell Automaton Simulator");
		frm.setSize(600,600);
		frm.setLayout(new BorderLayout());
		Main pnl = new Main();

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

		pnl.init();

		frm.add(pnl);

		frm.setVisible(true);
	}
	//Used to initialize things.
	public void init(){
		survive = new ArrayList<Integer>();
		born = new ArrayList<Integer>();
		tmr = new Timer();
		task = new TimerTask(){
			public void run(){
				tick();
			}
		};
		tmr.schedule(task, 0, interval);
	}

	public void paint(Graphics g){
		g.setColor(BG_COLOR);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());

		if (cells == null){
			return;
		}
		
		for (int i = 0; i < cells.length; ++i){
			for (int j = 0; j < cells[0].length; ++j){
				int x = CELL_SIZE * i + xDisplace;
				int y = CELL_SIZE * j + yDisplace;

				cells[i][j].paint(g, x, y);
			}
		}
	}
	private void updateTimer(){
		task.cancel();
		task = new TimerTask(){
			public void run(){
				tick();
			}
		};
		tmr.schedule(task, 0, interval);
	}
	private void tick(){
		if (cells == null || !go) {
			return;
		}
		updateAdjacent();
		advance();
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
			//TODO
		} else if (command.equals("open")){
			//TODO
		} else if (command.equals("save")){
			//TODO
		} else if (command.equals("exit")){
			System.exit(0);
		} else if (command.equals("start")){
			//TODO
		} else if (command.equals("stop")){
			//TODO
		} else if (command.equals("out")){
			if (CELL_SIZE > 1){
				CELL_SIZE--;
			}
		} else if (command.equals("in")){
			if (CELL_SIZE < 20){
				CELL_SIZE++;
			}
		} else if (command.equals("slower")){
			if (interval < 640){
				interval *= 2;
				updateTimer();
			}
		} else if (command.equals("faster")){
			if (interval > 5){
				interval /= 2;
				updateTimer();
			}
		}
	}
	public void mousePressed(MouseEvent me){

	}
	public void mouseReleased(MouseEvent me){

	}
	public void mouseClicked(MouseEvent me){

	}
	public void mouseEntered(MouseEvent me){

	}
	public void mouseExited(MouseEvent me){

	}
	public void mouseMoved(MouseEvent me){

	}
	public void mouseDragged(MouseEvent me){

	}
}
