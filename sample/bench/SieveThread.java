public class SieveThread extends Thread{

  public void run() {
    Sieve p = new Sieve();
    p.perform(512);
  }

  public static void main(String[] args) {
    long t0, t1;
    SieveThread[] threads = new SieveThread[Integer.parseInt(args[0])];
    for(int i = 0; i < threads.length; i++){
      threads[i] = new SieveThread();
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
    System.out.printf("time: %d ns\n", t1 - t0);
  }
}
