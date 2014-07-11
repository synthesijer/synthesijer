
public class led extends Thread{
  counter c = new counter();
  public boolean flag;
  
  public void run() {
    while(true){
      c.up();
      if (c.counter == 1000000) {
	c.clear();
	flag = !flag;
      }
    }
  }
  
}
