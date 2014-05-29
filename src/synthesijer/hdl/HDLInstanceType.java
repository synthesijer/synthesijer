package synthesijer.hdl;

public class HDLInstanceType implements HDLType{
	
	final HDLModule target;
	
	HDLInstanceType(HDLModule target){
		this.target = target;
	}

	@Override
	public HDLExpr getDefaultValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getVHDL() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getVerilogHDL() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public KIND getKind() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isBit() {
		// TODO Auto-generated method stub
		return false;
	}

}
