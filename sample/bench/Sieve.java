import java.util.Calendar;
import synthesijer.rt.*;

public class Sieve{

	private final static int  SIZE = 100;

        private final char flags[] = new char[101]; // new boolean[SIZE+1];

	public int perform(int cnt) {

		int i, prime, k, count;
		count=0;

		for (int j=0; j<cnt; ++j) {
			count=0;
			for(i=0; i<=SIZE; i++){ flags[i]=1; }
			for (i=0; i<=SIZE; i++) {
			  char c = flags[i];
				if(c == 1) {
					prime=i+i+3;
					for(k=i+prime; k<=SIZE; k+=prime){
						flags[k]=0;
                                        }
					count++;
				}
			}
		}

		return count;
	}

  @unsynthesizable
  public static void main(String[] args){
    //Compiler.disable();
    long t0, t1;
    Sieve s = new Sieve();
    t0 = System.nanoTime();
    //for(int i = 0; i < 10000; i++)
    s.perform(512);
    t1 = System.nanoTime();
    System.out.println(t1-t0);
  }


}
