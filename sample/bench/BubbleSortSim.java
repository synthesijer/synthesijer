public class BubbleSortSim extends Thread{
        
  BubbleSort p = new BubbleSort();
  boolean finish_flag = false;
        
  public void run(){
    finish_flag = false;
    p.init();
    p.check();
    p.test();
    p.check();
    finish_flag = true;
  }

}
