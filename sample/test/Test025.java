import java.util.Arrays;
import java.util.List;

import synthesijer.rt.*;

public class Test025 {
 
    public int test025_1(List<Integer> lst) { 
        return lst
		.stream()
		.mapToInt(i -> i)
		.sum();
    }

    public int test025_2(List<Integer> lst) { 
        return lst
		.stream()
		.filter(x -> x % 2 == 1)
		.mapToInt(i -> i)
		.sum();
    }
	
    @unsynthesizable
    public static void main(String... args){
	Test025 t = new Test025();
	Integer[] num = {1, 2, 3, 4, 5, 6};
        List<Integer> l = Arrays.asList(num);
	System.out.println(t.test025_1(l));
	System.out.println(t.test025_2(l));
    }

}
