
public class Firefly extends Thread{

  boolean flag = false;
  private final static int PERIOD = 500;

  private void my_sleep(){
    for(int k = 0; k < 400; k++){ ; }
  }

  public void run(){
    while(true){
      for(int i = 0; i < PERIOD; i++){
	for(int j = 0; j < i; j++){
	  flag = true;
	  my_sleep();
	}
	for(int j = 0; j < PERIOD-i; j++){
	  flag = false;
	  my_sleep();
	}
      }
      for(int i = 0; i < PERIOD; i++){
	for(int j = 0; j < PERIOD-i; j++){
	  flag = true;
	  my_sleep();
	}
	for(int j = 0; j < i; j++){
	  flag = false;
	  my_sleep();
	}
      }
      my_sleep();
      my_sleep();
    }
  }


}
