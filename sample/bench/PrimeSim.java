public class PrimeSim extends Thread{
	
	Prime p = new Prime();
	boolean finish_flag = false;
	public int result = 0;
	
	public void run(){
		finish_flag = false;
		p.init();
		result = p.test(65536);
		finish_flag = true;
	}

}
