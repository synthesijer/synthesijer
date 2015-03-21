import synthesijer.lib.axi.*;
import synthesijer.rt.*;

public class AXIHP_MEMCPY2{

  private final AXILiteSlave32RTL s0 = new AXILiteSlave32RTL();
  private final AXIMemIface32RTLTest m0 = new AXIMemIface32RTLTest();

  private void run(){
    int src_addr = s0.data[1];
    int dest_addr = s0.data[2];
    m0.fetch(src_addr, 256);
    m0.flush(dest_addr, 256);
  }

  @auto
  public void main(){
    s0.data[0] = 0x00000000;
      while(s0.data[0] == 0x00000000) ; // wait for kick from PS
      run();
      s0.data[0] = 0x00000000; // to notify DONE to PS
  }
}

