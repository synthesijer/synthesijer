

public class EchoTest{
	
	private final RS232C_RX rx = new RS232C_RX("sys_clk", "100000000", "rate", "9600");
	private final RS232C_TX tx = new RS232C_TX("sys_clk", "100000000", "rate", "9600");
	
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
