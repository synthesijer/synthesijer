
import synthesijer.rt.*;

public class Test030{

	public void test(int[] a, int[] b, int[] c){
		for(@Pipeline int i = 0; i < 1024; i ++){
			c[i] = a[i] + b[i];
		}
	}
	
}
