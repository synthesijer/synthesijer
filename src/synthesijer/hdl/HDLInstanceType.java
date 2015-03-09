package synthesijer.hdl;

public class HDLInstanceType implements HDLType{
	
	final HDLModule target;
	
	HDLInstanceType(HDLModule target){
		this.target = target;
	}
	
	@Override
	public boolean isEqual(HDLType t) {
		if(!(t instanceof HDLInstanceType)) return false;
		HDLInstanceType t0 = (HDLInstanceType)t;
		return target.getName().equals(t0.target.getName());
	};

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

}
