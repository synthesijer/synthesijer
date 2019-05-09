package synthesijer.stream;

import com.sun.source.util.*;
import com.sun.source.tree.*;

import java.util.ArrayList;
import java.util.List;

public class StreamModuleScanner extends BaseScanner<Void, Void>{

	public ArrayList<StreamModule> modules = new ArrayList<>();

	@Override
	public Void visitCompilationUnit(CompilationUnitTree node, Void aVoid){
		for(var n: node.getTypeDecls()){
			n.accept(this, null);
		}
		return null;
	}
	
	@Override
	public Void visitClass(ClassTree node, Void aVoid){
		StreamModule m = new StreamModule(node.getSimpleName().toString());
		for(var n: node.getMembers()){
			n.accept(new StreamUnitScanner(m), null);
		}
		modules.add(m);
		return null;
	}

}

class TypeScanner extends BaseScanner<String, Void>{

	@Override
	public String visitPrimitiveType(PrimitiveTypeTree node, Void aVoid){
		return node.getPrimitiveTypeKind().name();
	}

}

class StreamUnitScanner extends BaseScanner<Void, Void>{

	StreamModule module;
	ArrayList<StreamUnit> units = new ArrayList<>();

	public StreamUnitScanner(StreamModule m){
		this.module = m;
	}
	
	@Override
	public Void visitMethod(MethodTree node, Void aVoid){
		if(isConstructor(node)){
			return null;
		}
		if(isUnsynthesizable(node)){
			return null;
		}
		StreamUnit unit = new StreamUnit(node.getName().toString());
		units.add(unit);
		
		System.out.println("       parameters: " + node.getParameters());
		for(var t: node.getParameters()){
			t.accept(this, null);
			StreamInput input = new StreamInput(t.getName().toString());
			unit.inputs.add(input);
		}

		StreamOutput output = new StreamOutput("return_" + unit.name);
		unit.outputs.add(output);
		String t = node.getReturnType().accept(new TypeScanner(), null);
		output.setType(t);

		node.getBody().accept(this, null);
		return null;
	}

	private boolean isConstructor(MethodTree t){
		return (t.getReturnType() == null);
	}

	private boolean isUnsynthesizable(MethodTree t){
		List<? extends AnnotationTree> lst = t.getModifiers().getAnnotations();
		for(AnnotationTree a: lst){
			if(a.getAnnotationType().toString().equals("unsynthesizable")){
				return true;
			}else if(a.getAnnotationType().toString().equals("synthesijer.rt.unsynthesizable")){
				return true;
			}
		}
		return false;
	}

}
/*
	@Override
	public Void visitVariable(VariableTree node, Void aVoid){
		System.out.println("variable name: " + node.getName());
		System.out.println("         modifiers: " + node.getModifiers());
		System.out.println("         init: " + node.getInitializer());
		System.out.println("         type: " + node.getType());
		return null;
	}


	@Override
	public Void visitOther(Tree t, Void aVoid){
		System.out.println("rule has not been implemented: " + t.getClass().getName());
		return null;
	}
	
}
*/
