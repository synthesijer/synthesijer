public class Test001Sim{
	
	
	public static void main(String... args){
	 Test001 obj = new Test001();
for(int i = 0; i < 10; i++){
		int v = obj.add(100, 200);
System.out.println("v="+v);
		v = obj.acc(v);
System.out.println("acc: v="+v);
		v = obj.acc2(10, v);
System.out.println("acc2: v="+v);
		v = obj.acc3(10, v);
System.out.println("acc3: v="+v);
		v = obj.acc4(10, v);
System.out.println("acc4: v="+v);
                v = obj.add2(50, 400);
System.out.println("v="+v);
		int x = obj.switch_test(0);
System.out.println("x="+x);
		x = obj.switch_test(1);
System.out.println("x="+x);
		x = obj.switch_test(2);
System.out.println("x="+x);
		x = obj.switch_test(3);
System.out.println("x="+x);
		x = obj.switch_test(4);
System.out.println("x="+x);
		x = obj.switch_test(5);
System.out.println("x="+x);
	}
}
}
