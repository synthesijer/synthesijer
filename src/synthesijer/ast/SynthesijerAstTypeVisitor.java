package synthesijer.ast;

import synthesijer.ast.type.ArrayRef;
import synthesijer.ast.type.ArrayType;
import synthesijer.ast.type.ComponentRef;
import synthesijer.ast.type.ComponentType;
import synthesijer.ast.type.MySelfType;
import synthesijer.ast.type.PrimitiveTypeKind;

public interface SynthesijerAstTypeVisitor {

	public void visitArrayType(ArrayType o);

	public void visitArrayRef(ArrayRef o);

	public void visitComponentType(ComponentType o);

	public void visitComponentRef(ComponentRef o);
	
	public void visitMySelfType(MySelfType o);

	public void visitPrimitiveTypeKind(PrimitiveTypeKind o);

}
