/** test for loop pipelining **/

public class Test021 {

	public void add(int[] a, int[] b, int[] c, int num){
		for(int i = 0; i < num; i++){
			c[i] = a[i] + b[i];
		}
	}

}
