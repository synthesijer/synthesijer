import net.wasamon.javarock.rt.*;

@javarockhdl
public class PrimeSim implements Runnable{
	
	Prime p = new Prime();
	
	@auto
	public void run(){
		p.init();
		p.test(65536);
	}

}
