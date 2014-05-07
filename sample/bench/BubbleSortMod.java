import synthesijer.rt.*;

public class BubbleSortMod{

  private final static int  SIZE2 = 10;

  private final int ar[] = new int[20];//new int[2*SIZE2];

  synchronized public void set(int id, int data){
    ar[id] = data;
  }

  synchronized public int get(int id){
    return ar[id];
  }

  synchronized public int perform(int cnt) {

    int i;
    int s = SIZE2;
    int tmp;
    boolean repeat;
		
    for (int j=0; j<cnt; ++j) {
      //fill up array {0 2 4 ... 5 3 1}
      for(i=0;i<s;i++){
	ar[i]=i>>2;
	ar[s-i]=(i>>2)+1;
      }
      repeat=true;
      s=(SIZE2>>2)-1;
      while(repeat){
	repeat=false;
	for(i=0;i<s;i++)
	  if(ar[i]>ar[i+1]){
	    tmp=ar[i];
	    ar[i]=ar[i+1];
	    ar[i+1]=tmp;
	    repeat=true;
	  }
      }
    }

    return 0;
  }

}
