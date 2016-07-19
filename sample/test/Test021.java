/** test for loop pipelining **/
import synthesijer.rt.*;

public class Test021 {

	public void add(int[] a, int[] b, int[] c, int num){
		for(int i = 0; i < num; i++){
			c[i] = a[i] + b[i];
		}
	}

	@unsynthesizable
	public static void main(String... args){
		int[] a = new int[]{1, 2};
		int[] b = new int[]{1, 2};
		int[] c = new int[2];
		Test021 t = new Test021();
		t.add(a, b, c, 2);
		System.out.println(c[0]);
		System.out.println(c[1]);
	}

}
