public class Test001Sim{
	
	private final Test001 obj = new Test001();
	
	public void main(){
		int v = obj.add(100, 200);
		v = obj.acc(v);
		v = obj.acc2(10, v);
		v = obj.add2(100, 200);
		int x = obj.switch_test(0);
		x = obj.switch_test(1);
		x = obj.switch_test(2);
		x = obj.switch_test(3);
		x = obj.switch_test(4);
		x = obj.switch_test(5);
	}

}
