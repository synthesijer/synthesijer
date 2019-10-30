import synthesijer.rt.*;

public class Test001{
	
	@synthesijer.rt.Debug
	private int x = 0;
	public int acc(int y){
		x += y; 
		return x;
	}
	
	public int add(int x, int y){
		return x + y;
	}
	
	public int add2(int x, int y){
		return add(x, y);
	}

	public int acc2(int num, int y){
		for(int i = 0; i < num; i++){
			x += y;
		}
		return x;
	}
	
	public int acc3(int num, int y){
		int i = 0;
		while(i < num){
			x += y;
			i++;
		}
		return x;
	}

	public int acc4(int num, int y){
		int i = 0;
		do{
			x += y;
			i++;
		}while(i < num);
		return x;
	}

	public int switch_test(int x){
	    int value = 0;
	    switch(x){
		case 0:
			return 10;
		case 1:
			return 3 + 4;
		case 2:
		{
			int i = 300;
			int j = 400;
			return i + j;
		}
		case 3:
			value = 100;
			break;
		case 4:
		{
			value = 50;
			value = 70;
		}
		case 5:
			value = 10;
		case 6:
			value = 10;
		default:
			value = 200;
		}
		return value;
	}

	public boolean test(){
		int v;
		v = add(100, 200);
		if(v != 300) return false;
		v = acc(v);
		if(v != 300) return false;
		v = acc2(10, v);
		if(v != 3300) return false;
		v = acc3(10, v);
		if(v != 36300) return false;
		v = acc4(10, v);
		if(v != 399300) return false;
		v = add2(50, 400);
		if(v != 450) return false;
		int x;
		x = switch_test(0);
		if(x != 10) return false;
		x = switch_test(1);
		if(x != 7) return false;
		x = switch_test(2);
		if(x != 700) return false;
		x = switch_test(3);
		if(x != 100) return false;
		x = switch_test(4);
		if(x != 200) return false;
		x = switch_test(5);
		if(x != 200) return false;
		return true;
	}

	@unsynthesizable
	public static void main(String[] args){
		Test001 obj = new Test001();
		System.out.println(obj.test());
		int v;
		v = obj.add(100, 200);
		System.out.println(v);
		v = obj.acc(v);
		System.out.println(v);
		v = obj.acc2(10, v);
		System.out.println(v);
		v = obj.acc3(10, v);
		System.out.println(v);
		v = obj.acc4(10, v);
		System.out.println(v);
		v = obj.add2(50, 400);
		System.out.println(v);
		int x;
		x = obj.switch_test(0);
		System.out.println(x);
		x = obj.switch_test(1);
		System.out.println(x);
		x = obj.switch_test(2);
		System.out.println(x);
		x = obj.switch_test(3);
		System.out.println(x);
		x = obj.switch_test(4);
		System.out.println(x);
		x = obj.switch_test(5);
		System.out.println(x);
	}
	
}
