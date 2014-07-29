public class Test001{
	
	int x = 0;
	public int acc(int y){
		x += y; 
		return x;
	}
	
	public int add(int x, int y){
		return x + y;
	}
	
	public int acc2(int num, int y){
		for(int i = 0; i < num; i++){
			x += y;
		}
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
		default:
			value = 200;
		}
		return value;
	}

	
}
