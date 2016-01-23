public class Test004 extends Thread{
	private int i;
	public void run(){
		run_body();
	}

	private void run_body(){
		i = 0;
		for(int j = 0; j < 1000; j++){
			i++;
		}
	}

	public void break_test(){
		i = 0;
		while(true){
		  i++;
		  if(i == 10) break;
		}
	}

	public void continue_test(){
		i = 0;
		while(true){
		  if(i == 10) continue;
		  i++;
		}
	}
}
