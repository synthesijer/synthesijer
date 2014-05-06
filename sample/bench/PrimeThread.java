import java.util.Calendar;

public class PrimeThread extends Thread{

	private int id;
	private boolean flag;

	public PrimeThread(int id) {
		this.id = id;
		this.flag = false;
	}

	public void run() {
		Prime p = new Prime();
		p.init();
		//System.out.println("start: " + id);
		int result = p.test(65536);
		//int result = p.test(10);
		//System.out.println(result);
		this.flag = true;
	}

	public static void main(String[] args) {
		long t0, t1;
		PrimeThread[] threads = new PrimeThread[Integer.parseInt(args[0])];
		for(int i = 0; i < threads.length; i++){
			threads[i] = new PrimeThread(i);
		}
		//t0 = Calendar.getInstance().getTimeInMillis();
		t0 = System.nanoTime();
		for (int i = 0; i < threads.length; i++) {
			threads[i].start();
		}
		for(int i = 0; i < threads.length; i++){
			while(!threads[i].flag){
				try{Thread.sleep(0);}catch (Exception e) { }
			}
		}
		//t1 = Calendar.getInstance().getTimeInMillis();
		t1 = System.nanoTime();
		System.out.printf("%d\n", t1 - t0);
	}
}
