package synthesijer.ast.statement;

import java.io.PrintWriter;

import synthesijer.ast.Scope;

public class SynchronizedBlock extends BlockStatement{
	
	public SynchronizedBlock(Scope scope){
		super(scope);
	}

	public void dumpAsXML(PrintWriter dest){
		dest.printf("<statement type=\"synchronized\">\n");
		super.dumpAsXML(dest);
		dest.printf("</statement>>\n");
	}
}
