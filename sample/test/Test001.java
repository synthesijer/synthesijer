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
}
