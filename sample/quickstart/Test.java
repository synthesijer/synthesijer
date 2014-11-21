public class Test{
  public boolean flag;
  private int count;

  public void run(){
    while(true){
      count++;
      if(count > 5){
	count = 0;
	flag = !flag;
      }
    }
  }
}
