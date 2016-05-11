import synthesijer.rt.*;

public class Test020{

	int a = 100;
	int b = 201;

	// these are computed at the compile time
	// it means that generated HDL does not require MUL, DIV, and MOD for them
	int c = b * a;
	int d = b / a;
	int e = b % a;

	// unary operations such as the followings have not been supported yet
	// private int f = a++;
	// private int g = a--;
	// private int h = ++a;
	// private int i = --a;
	// private int j = ~a;

	// variables and binary expressons are allowed to define memories
	private int[] mem0 = new int[a];
	private int[] mem1 = new int[d];
	public int[] mem2 = new int[10*30];

	public boolean test(){
		if(a != 100) return false;
		if(b != 201) return false;
		if(c != 20100) return false;
		if(d != 2) return false;
		if(e != 1) return false;
		if(mem0.length != a) return false;
		if(mem1.length != d) return false;
		if(mem2.length != 10*30) return false;
		d = b * a;
		if(d != 20100) return false;
		a = 40;
		b = 10;
		c = b * a;
		if(c != 400) return false;
		return true;
	}

	@unsynthesizable
	public static void main(String... args){
		Test020 t = new Test020();
		System.out.println(t.test());
	}
}
