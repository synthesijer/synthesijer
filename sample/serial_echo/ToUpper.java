

public class ToUpper{
	
	private final RS232C_RX_Wrapper rx = new RS232C_RX_Wrapper("sys_clk", "100000000", "rate", "9600");
	private final RS232C_TX_Wrapper tx = new RS232C_TX_Wrapper("sys_clk", "100000000", "rate", "9600");
	
	public void run(){
		tx.wr = false;
		while(true){
			while(rx.rd == false) ;
			byte b = rx.dout;
			if(b >= (byte)'a' && b <= (byte)'z'){
				tx.din = (byte)(b - (byte)0x20);
			}else{
				tx.din = b;
			}
			tx.wr = true;
			tx.wr = false;
			while(tx.ready == false) ;
		}
	}
	
}
