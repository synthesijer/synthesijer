package synthesijer.hdl;

import synthesijer.SynthesijerUtils;

public class HDLTreeVisitorBase implements HDLTreeVisitor{

	public void visitHDLTree(HDLTree o){
		SynthesijerUtils.warn("unhandled syntax");
		SynthesijerUtils.dump(o);
		throw new RuntimeException();
	}

	@Override
	public void visitHDLExpr(HDLExpr o) {
		visitHDLTree(o);
	}

	@Override
	public void visitHDLInstance(HDLInstance o) {
		visitHDLTree(o);
	}

	@Override
	public void visitHDLInstanceRef(HDLInstanceRef o) {
		visitHDLTree(o);
	}

	@Override
	public void visitHDLLitral(HDLLiteral o) {
		visitHDLTree(o);
	}

	@Override
	public void visitHDLModule(HDLModule o) {
		visitHDLTree(o);
	}

	@Override
	public void visitHDLPort(HDLPort o) {
		visitHDLTree(o);
	}

	@Override
	public void visitHDLParameter(HDLParameter o) {
		visitHDLTree(o);
	}

	@Override
	public void visitHDLSequencer(HDLSequencer o) {
		visitHDLTree(o);
	}

	@Override
	public void visitHDLSignal(HDLSignal o) {
		visitHDLTree(o);
	}

	@Override
	public void visitHDLType(HDLPrimitiveType o) {
		visitHDLTree(o);
	}

	@Override
	public void visitHDLUserDefinedType(HDLUserDefinedType o) {
		visitHDLTree(o);
	}


}
