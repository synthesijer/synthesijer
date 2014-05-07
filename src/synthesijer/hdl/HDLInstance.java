package synthesijer.hdl;

public class HDLInstance implements HDLTree{

	@Override
	public void accept(HDLTreeVisitor v) {
		v.visitHDLInstance(this);
	}

}
