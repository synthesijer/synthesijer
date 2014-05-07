package synthesijer.hdl.literal;

import synthesijer.hdl.HDLLiteral;

public class HDLSymbol implements HDLLiteral{
	
	private final String sym;
	
	public HDLSymbol(String s){
		this.sym = s;
	}
	
	public String getSymbol(){
		return sym;
	}

	@Override
	public String getVHDL() {
		return sym;
	}

	@Override
	public String getVerilogHDL() {
		return sym;
	}

}
