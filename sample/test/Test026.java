import java.util.Arrays;
import java.util.List;

import synthesijer.rt.*;

public class Test026 {
 
    public int test026_1(List<Integer> lst) { 
        return lst
		.stream()
		.mapToInt(i -> i)
		.sum();
    }

    public int test026_2(List<Integer> lst) { 
        return lst
		.stream()
		.filter(x -> x % 2 == 1)
		.mapToInt(i -> i)
		.sum();
    }
	
    public int test026_3(List<Integer> lst) { 
        return lst
		.stream()
		.filter(x -> x % 3 == 1)
		.mapToInt(i -> i)
		.sum();
    }

    public int test026_4(List<Integer> lst) { 
        return lst
		.stream()
		.filter(x -> x % 2 == 1)
		.mapToInt(i -> i)
		.sum();
    }
	
    @unsynthesizable
    public static void main(String... args){
	Test026 t = new Test026();
	Integer[] num = {1, 2, 3, 4, 5, 6};
        List<Integer> l = Arrays.asList(num);
	System.out.println(t.test026_1(l));
	System.out.println(t.test026_2(l));
	System.out.println(t.test026_3(l));
	System.out.println(t.test026_4(l));
    }

}
