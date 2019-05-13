package synthesijer.hdl;

/**
 * interface of expression in HDL
 */
public interface HDLExpr extends HDLTree{

	/**
	 *
	 * @return expression in VHDL style
	 */
	public String getVHDL();

	/**
	 *
	 * @return expression in Verilog-HDL style
	 */
	public String getVerilogHDL();

	/**
	 *
	 * @return result expression
	 */
	public HDLExpr getResultExpr();

	/**
	 *
	 * @return result type of this expression
	 */
	public HDLType getType();

	/**
	 *
	 * @return an array of source signals of this expression
	 */
	public HDLSignal[] getSrcSignals();

}
