public class BubbleSort{

  private final int ar[] = new int[512];

  public synchronized void init(){
    for(int i = 0; i < ar.length; i++){
      ar[i] = ar.length - 1 - i;
    }
  }

  public synchronized void set(int id, int data){
    ar[id] = data;
  }

  public synchronized int get(int id){
    return ar[id];
  }

  public synchronized void test(){
    int tmp;
    int max_i = ar.length - 1;
    for(int i = 0; i <= max_i - 1; i++){
      int max_j = ar.length - 1 - i;
      for(int j = 1; j <= max_j; j++){
	int a = ar[j];
	int b = ar[j-1];
	if(a < b){
	  ar[j-1] = a;
	  ar[j] = b;
	}
      }
    }
  }
}
