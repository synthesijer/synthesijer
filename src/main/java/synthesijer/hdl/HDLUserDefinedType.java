package synthesijer.hdl;

import java.util.ArrayList;
import java.util.Hashtable;

import synthesijer.hdl.expr.HDLValue;


public class HDLUserDefinedType implements HDLTree, HDLType{

    private final String base;
    private final int defaultIndex;
    private final ArrayList<HDLValue> items = new ArrayList<>();
    private final Hashtable<String, HDLValue> itemTable = new Hashtable<>();
    private final KIND kind;

    public HDLUserDefinedType(String base, String[] items, int defaultIndex) {
        this.base = "Type_" + base;
        if(items != null){
            for(String s: items){
                this.items.add(new HDLValue(s, HDLPrimitiveType.genStringType()));
            }
        }
        this.defaultIndex = defaultIndex;
        this.kind = KIND.USERDEF;
    }

    @Override
    public boolean isEqual(HDLType t) {
        if(!(t instanceof HDLUserDefinedType)) return false;
        HDLUserDefinedType t0 = (HDLUserDefinedType)t;
        return base.equals(t0.base);
    };

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
        return itemTable.containsKey(s);
    }

    private HDLValue getValueOfID(String s){
        return itemTable.get(s);
    }

    public HDLValue addItem(String s){
        if(isDefined(s)) return null;
        HDLValue v = new HDLValue(s, HDLPrimitiveType.genStringType());
        items.add(v);
        return v;
    }

    @Override
    public void accept(HDLTreeVisitor v) {
        v.visitHDLUserDefinedType(this);
    }

    public boolean isBit(){
        return false;
    }

    @Override
    public boolean isVector() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isSigned() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isInteger() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isDigit() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean hasWidth(){
        return false;
    }

    @Override
    public int getWidth(){
        return -1;
    }
}
