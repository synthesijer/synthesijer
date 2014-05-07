package synthesijer.hdl;

import java.io.PrintWriter;

import synthesijer.hdl.literal.HDLSymbol;

class HDLUserDefinedType extends HDLType{

	private final String base;
	private final HDLSymbol defaultValue;
	private final HDLSymbol[] items;
	
	public HDLUserDefinedType(String base, HDLSymbol[] items, int defaultValueIndex) {
		super(HDLType.KIND.USERDEF, 0);
		this.base = "Type_" + base;
		this.items = items;
		this.defaultValue = items[defaultValueIndex];
	}
	
	public String getName(){
		return base;
	}
	
	public String getVHDL(){
		return base;
	}
	
	public String getVerilogHDL(){
		return base;
	}
	
	public HDLLiteral getDefaultValue(){
		return defaultValue;
	}
	
	public void genUserDefinedTypeAsVHDL(PrintWriter dest, int offset){
		HDLUtils.println(dest, offset, String.format("type %s is (", base));
		String sep = "";
		for(HDLSymbol s: items){
			HDLUtils.print(dest, 0, sep);
			HDLUtils.print(dest, offset+2, String.format("%s", s.getSymbol()));
			sep = ",\n";
		}
		HDLUtils.println(dest, offset, String.format("\n  );"));
	}
	
	public void genUserDefinedTypeAsVerilog(PrintWriter dest, int offset){
		for(int i = 0; i < items.length; i++){
			HDLUtils.println(dest, offset, String.format("parameter %s = 32'd%d;", items[i].getSymbol(), i));
		}
	}
	
}
