
public class BubbleSortSim extends Thread{
    
    private BubbleSort p = new BubbleSort();
    public boolean finish_flag = false;
        
    private final int ar[] = new int[512];

    private void init(){
		for(int i = 0; i < ar.length; i++){
			ar[i] = ar.length - 1 - i;
		}
    }

    private boolean check(){
		boolean flag = true;
		int v = ar[0];
		for(int i = 1; i < ar.length; i++){
			if(v > ar[i]) return false;
			v = ar[i];
		}
		return true;
    }

    public void run(){
		finish_flag = false;
		init();
		check();
		p.test(ar);
		check();
		finish_flag = true;
    }

	public boolean test(){
		run();
		return check();
	}

}
