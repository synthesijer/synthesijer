
public class Firefly extends Thread{

  boolean flag = false;
  final int PERIOD = 500;

  pivate void sleep(){
    for(int k = 0; k < 400; k++){ ; }
  }

  public void run(){
    while(true){
      for(int i = 0; i < PERIOD; i++){
	for(int j = 0; j < i; j++){
	  flag = true;
	  sleep();
	}
	for(int j = 0; j < PERIOD-i; j++){
	  flag = false;
	  sleep();
	}
      }
      for(int i = 0; i < PERIOD; i++){
	for(int j = 0; j < PERIOD-i; j++){
	  flag = true;
	  sleep();
	}
	for(int j = 0; j < i; j++){
	  flag = false;
	  sleep();
	}
      }
      sleep();
      sleep();
    }
  }


}
