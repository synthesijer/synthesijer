package synthesijer.hdl;

public enum HDLOp {
	
	ADD("+"),
	REF();

	private final String vhdlSym, verilogSym;
	
	private HDLOp(String vhdlSym, String verilogSym){
		this.vhdlSym = vhdlSym;
		this.verilogSym = verilogSym;
	}
	
	private HDLOp(String sym){
		this(sym, sym);
	}

	private HDLOp(){
		this("", "");
	}
	
	public String getVHDL(){
		return vhdlSym;
	}

	public String getVerilogHDL(){
		return verilogSym;
	}

}
