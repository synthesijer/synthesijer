

public class EchoTest{
	
	private final RS232C_RX_Wrapper rx = new RS232C_RX_Wrapper("sys_clk", "25000000", "rate", "9600");
	private final RS232C_TX_Wrapper tx = new RS232C_TX_Wrapper("sys_clk", "25000000", "rate", "9600");
	
	public void run(){
		tx.wr = false;
		while(true){
			while(rx.rd == false) ;
			tx.din = rx.dout;
			tx.wr = true;
			tx.wr = false;
			while(tx.ready == false) ;
		}
	}
	
}
