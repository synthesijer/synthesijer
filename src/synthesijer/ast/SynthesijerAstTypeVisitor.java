package synthesijer.ast;

import synthesijer.ast.type.ArrayType;
import synthesijer.ast.type.ComponentType;
import synthesijer.ast.type.MySelfType;
import synthesijer.ast.type.PrimitiveTypeKind;

public interface SynthesijerAstTypeVisitor {

	public void visitArrayType(ArrayType o);

	public void visitComponentType(ComponentType o);

	public void visitMySelfType(MySelfType o);

	public void visitPrimitiveTypeKind(PrimitiveTypeKind o);

}
