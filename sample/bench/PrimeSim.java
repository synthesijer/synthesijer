public class PrimeSim extends Thread{
	
	Prime p = new Prime();
	boolean finish_flag = false;
	
	public void run(){
		finish_flag = false;
		p.init();
		p.test(65536);
		finish_flag = true;
	}

}
