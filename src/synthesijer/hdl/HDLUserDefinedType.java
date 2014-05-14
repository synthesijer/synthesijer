package synthesijer.hdl;

import java.util.ArrayList;

import synthesijer.hdl.expr.HDLValue;


public class HDLUserDefinedType implements HDLTree, HDLType{

	private final String base;
	private final int defaultIndex;
	private final ArrayList<HDLValue> items = new ArrayList<HDLValue>();
	private final KIND kind;
	
	HDLUserDefinedType(String base, String[] items, int defaultIndex) {
		this.base = "Type_" + base;
		if(items != null){
			for(String s: items){ this.items.add(new HDLValue(s, HDLPrimitiveType.genStringType())); }
		}
		this.defaultIndex = defaultIndex;
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
		return "[31:0]";
	}
	
	public HDLLiteral getDefaultValue(){
		if(items.size() > defaultIndex){
			return items.get(defaultIndex);
		}else{
			return null;
		}
	}
	
	public HDLValue[] getItems(){
		return items.toArray(new HDLValue[]{});
	}
	
	private boolean isDefined(String s){
		for(HDLValue v: items){
			if(v.getValue().equals(s)){
				return true;
			}
		}
		return false;
	}
	
	public void addItem(String s){
		if(isDefined(s)) return;
		items.add(new HDLValue(s, HDLPrimitiveType.genStringType()));
	}
		
	@Override
	public void accept(HDLTreeVisitor v) {
		v.visitHDLUserDefinedType(this);
	}
	
	public boolean isBit(){
		return false;
	}

}
