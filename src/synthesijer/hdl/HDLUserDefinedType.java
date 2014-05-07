package synthesijer.hdl;

import synthesijer.hdl.literal.HDLSymbol;

public class HDLUserDefinedType extends HDLType implements HDLTree{

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
	
	public HDLSymbol[] getItems(){
		return items;
	}
		
	@Override
	public void accept(HDLTreeVisitor v) {
		v.visitHDLUserDefinedType(this);
	}
}
