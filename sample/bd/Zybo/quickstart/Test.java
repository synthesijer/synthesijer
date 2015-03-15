import synthesijer.rt.*;
import synthesijer.lib.axi.*;

public class Test{

	private final AXILiteSlave32RTL s0 = new AXILiteSlave32RTL();
	private final SimpleAXIMemIface32RTLTest m0 = new SimpleAXIMemIface32RTLTest();

	private void run(){
		int src_addr = s0.data[1];
		int dest_addr = s0.data[2];
		for(int i = 0; i < 256; i++){
			int d = m0.read_data(src_addr + (i<<2));
			m0.write_data(dest_addr + (i<<2), d);
		}
	}

	@auto
	public void main(){
		s0.data[0] = 0x00000000;
		while(s0.data[0] == 0x00000000) ; // wait for kick from PS
		run();
		s0.data[0] = 0x00000000; // de-assert run flag in order to notify to PS
	}
}
