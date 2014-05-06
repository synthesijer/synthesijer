public class BubbleSortThread extends Thread{

  BubbleSort p = new BubbleSort();

  public void init(){
    p.init();
  }

  public void run() {
    p.test();
  }

  public static void main(String[] args) {
    long t0, t1;
    BubbleSortThread[] threads = new BubbleSortThread[Integer.parseInt(args[0])];
    for(int i = 0; i < threads.length; i++){
      threads[i] = new BubbleSortThread();
      threads[i].init();
    }
    t0 = System.nanoTime();
    for (int i = 0; i < threads.length; i++) {
      threads[i].start();
    }
    for(int i = 0; i < threads.length; i++){
      try{
	threads[i].join();
      }catch(InterruptedException e){
      }
    }
    t1 = System.nanoTime();
    System.out.printf("%d\n", t1 - t0);
  }
}
