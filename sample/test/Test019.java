import synthesijer.rt.*;

public class Test019{
	
	public int g(int n){
		return n-1;
	}

	public int f(int n){
		if(n == 0) return 0;
		f(g(n-1));
	}
	

	public void main(){
		f(10);
	}
}
