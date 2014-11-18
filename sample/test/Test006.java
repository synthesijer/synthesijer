public class Test006{
	
	private boolean check(int c){
		if(c == 10+20+40+50+70+80+100+110){
			return true;
		}else{
			return false;
		}
	}

	public void test(){
		boolean success = false;
		int a = 10;
		int b = 20;
		int c = 0;
		int d = 40;
		int e = 50;
		int f = 0;
		int g = 70;
		int h = 80;
		int i = 0;
		int j = 100;
		int k = 110;
		int l = 0;
		c = a + b;
		f = d + e;
		i = g + h;
		l = j + k;
		c = c + f;
		l = l + i;
		c = c + l;
		if(c == 10+20+40+50+70+80+100+110) success = true;
		boolean success2 = check(c);
	}
}
