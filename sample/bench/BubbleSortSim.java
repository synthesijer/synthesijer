
public class BubbleSortSim extends Thread{
    
    private BubbleSort p = new BubbleSort();
    public boolean finish_flag = false;
        
    private final int ar[] = new int[512];

    private void init(){
	for(int i = 0; i < ar.length; i++){
	    ar[i] = ar.length - 1 - i;
	}
    }

    private void check(){
	for(int i = 0; i < ar.length; i++){
	    int v = ar[i];
	}
    }

    public void run(){
	finish_flag = false;
	init();
	check();
	p.test(ar);
	check();
	finish_flag = true;
    }

}
