public class Test011{
	private int fact0(int x, int n){
		if(x == 1){
			return n;
		}else{
			return fact0(x-1, n * x);
		}
	}
	public int fact(int x){
		return fact0(x, 1);
	}

	public int fib(int n){
		return fib0(n, 0, 1);
	}

	private int fib0(int n, int a, int b){
		if(n >= 2) return fib0(n-1, b, a+b);
		if(n < 1) return a;
		return b;
	}
}
