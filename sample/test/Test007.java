import synthesijer.lib.*;

public class Test007{

    private final INPUT1 in = new INPUT1();
    private final OUTPUT1 out = new OUTPUT1();
    private final INPUT16 in16 = new INPUT16();
    private final OUTPUT16 out16 = new OUTPUT16();
    private final INPUT32 in32 = new INPUT32();
    private final OUTPUT32 out32 = new OUTPUT32();

    public void run(){
	out.flag = in.flag;
	out16.value = in16.value;
	out32.value = out32.value;
    }
    
}
