public class Test004 extends Thread{
	private int i;
	public void run(){
		while(true) i++;
	}

	public void break_test(){
		int i = 0;
		while(true){
		  i++;
		  if(i == 10) break;
		}
	}

	public void continue_test(){
		int i = 0;
		while(true){
		  if(i == 10) continue;
		  i++;
		}
	}
}
