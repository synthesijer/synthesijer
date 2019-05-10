import java.util.Arrays;
import java.util.List;

import synthesijer.rt.*;

public class Test024 {
 
	public int test024_1(List<Integer> lst) { 
        return lst
			.stream()
			.filter(x -> x % 2 == 1)
			.mapToInt(i -> i)
			.sum();
    }
	
	@unsynthesizable
	public static void main(String... args){
		Test024 t = new Test024();
		Integer[] num = {1, 2, 3, 4, 5, 6};
                List<Integer> l = Arrays.asList(num);
		System.out.println(t.test024_1(l));
	}

}
