package synthesijer.hdl;

import synthesijer.hdl.literal.HDLValue;


public class HDLUserDefinedType implements HDLTree, HDLType{

	private final String base;
	private final String defaultValue;
	private final String[] items;
	private final KIND kind;
	
	public HDLUserDefinedType(String base, String[] items, int defaultValueIndex) {
		this.base = "Type_" + base;
		this.items = items;
		this.defaultValue = items[defaultValueIndex];
		this.kind = KIND.USERDEF;
	}
	
	public String getName(){
		return base;
	}
	
	public KIND getKind(){
		return kind;
	}
	
	public String getVHDL(){
		return base;
	}
	
	public String getVerilogHDL(){
		return base;
	}
	
	public HDLLiteral getDefaultValue(){
		return new HDLValue(defaultValue, HDLPrimitiveType.genStringType());
	}
	
	public String[] getItems(){
		return items;
	}
		
	@Override
	public void accept(HDLTreeVisitor v) {
		v.visitHDLUserDefinedType(this);
	}
}
