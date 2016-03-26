public class MergeSortSim extends Thread{
        
	MergeSort p = new MergeSort();
	boolean finish_flag = false;
        
	public void run(){
		finish_flag = false;
		p.init();
		p.check();
		p.test();
		p.check();
		finish_flag = true;
	}

	public boolean test(){
		run();
		return p.check();
	}

}
