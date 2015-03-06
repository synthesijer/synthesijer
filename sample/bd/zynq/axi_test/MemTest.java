
import synthesijer.lib.axi.SimpleAXIMemIface32RTLTest;

public class MemTest {
	
	SimpleAXIMemIface32RTLTest obj = new SimpleAXIMemIface32RTLTest();
	
	private int[] data = new int[1024];
	public void test(int offset){
		for(int i = offset; i < data.length; i++){
			data[i] = obj.read_data(i<<2);
		}
	}

}
