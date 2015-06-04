public class arith {

	public int add_const_int(int a){
		return a + (-1);
	}

	public char add_const_char(char a){
		return (char)(a + 1);
	}

	public boolean cond_const_int(int a){
		if(a < 0){
			return true;
		}else{
			return false;
		}
	}

	public boolean cond_const_char(char a){
		if(a < 0){
			return true;
		}else{
			return false;
		}
	}
}
