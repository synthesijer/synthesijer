
public class rs232c{
	RS232C_TX tx = new RS232C_TX("sys_clk", "25000000", "rate", "115200");
	RS232C_RX rx = new RS232C_RX("sys_clk", "25000000", "rate", "115200");

	public byte read(){
		while(rx.rd != false) ;
		while(rx.rd != true) ;
		return rx.dout;
	}
	
	public void write(byte data){
		while(tx.ready == false) ;
		tx.din = data;
		tx.wr = true;
		tx.wr = false;
	}
	
}
