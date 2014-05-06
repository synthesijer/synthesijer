import synthesijer.rt.*;

public class Prime{
	
  byte[] flag = new byte[65536];
	
  public void init(){
    for(int i = 0; i < flag.length; i++){
      flag[i] = 0;
    }
    return;
  }
			
  public byte get(int id){
    return flag[id];
  }

  public void set(int id, byte value){
    flag[id] = value;
  }

  public int test(int max){
    int result = 1;
    //int k = 0;
    for(int i = 2; i < max; i++){
      byte v = flag[i]; 
      if(v == 0){
	result = i;
	int j = i;
	while(j < max){
	  flag[j] = 1;
	  j = j + i;
	  //k++;
	}
      }
    }
    //System.out.println(k);
    return result;
  }
	
  @unsynthesizable
  public static void main(String[] args){
    long t0, t1;
    Prime s = new Prime();
    s.init();
    t0 = System.nanoTime();
    s.test(65535);
    t1 = System.nanoTime();
    System.out.println(t1-t0);
  }

}
