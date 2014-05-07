package synthesijer.hdl;

public interface HDLExpr extends HDLTree{

	public String getVHDL();
	public String getVerilogHDL();

}
