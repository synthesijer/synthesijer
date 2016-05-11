import synthesijer.rt.*;

public class Test100{
	
	private int fact0_body(int x, int n){
		return x * n;
	}
	
	private int fact0(int x, int n){
		if(x == 1){
			return n;
		}else{
			int y = fact0_body(x, n);
			return fact0(x-1, y);
		}
	}
	public int fact(int x){
		return fact0(x, 1);
	}

	@CallStack(10000)
	private int g_body(int n){
		if(n == 0){
			return 1;
		}else if(n == 1){
			return 1;
		}else{
			int a = g(n-1);
			int b = g(n-2);
			return a + b;
		}
	}

	@CallStack(10000)
	public int g(int n){
		return g_body(n);
	}

	@unsynthesizable
	public static void main(String[] args){
		Test100 t = new Test100();
		System.out.println(t.fact(5));
		System.out.println(t.g(5));
	}

	
}
