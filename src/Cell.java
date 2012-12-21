import java.awt.Graphics;
import java.awt.Color;

public class Cell{
	private static final Color DEAD = Color.WHITE;
	private static final Color ALIVE = Color.BLACK;
	private int adjacent, state;

	public Cell(){
		state = 0;
		adjacent = 0;
	};
	
	public void paint(Graphics g, int x, int y){
		switch(state){
			case 0:
				g.setColor(DEAD);
				break;
			case 1:
				g.setColor(ALIVE);
				break;
		}
		g.fillRect(x, y, Main.CELL_SIZE, Main.CELL_SIZE);
	}

	public void increment(){
		adjacent++;
	}
	/**
	 * Sets the state and resets adjacent.
	 * @param nextState The state to advance to.
	 */
	public void advance(int nextState){
		state = nextState;
		adjacent = 0;
	}
	public String toString(){
		return ("State:" + state + "\tAdjacent:" + adjacent);
	}
	public void setAdjacent(int adjacent){
		this.adjacent = adjacent;
	}
	public void setState(int state){
		this.state = state;
	}
	public int getState(){
		return state;
	}
	public int getAdjacent(){
		return adjacent;
	}
}
