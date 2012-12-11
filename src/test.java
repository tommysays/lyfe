import java.util.Timer;
import java.util.TimerTask;

public class test{
	private static int counter = 0;
	private static Timer tmr;
	private static TimerTask task;
	private static boolean notset = true;
	public static void main(String[] args){
		tmr = new Timer();
		task = new TimerTask(){
			public void run(){
				animate();
			}
		};
		tmr.schedule(task, 0, 1000);
		
	}
	public static  void animate(){
		System.out.println(counter++);
		if (counter > 4 && notset){
			task.cancel();
			task = new TimerTask(){
				public void run(){
					animate();
				}
			};
			tmr.schedule(task, 0, 300);
			notset = false;
		}
	}

}
