package synthesijer;

import com.sun.source.tree.ClassTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.JavacTask;
import com.sun.source.util.Plugin;
import com.sun.source.util.TaskListener;
import com.sun.source.util.TaskEvent;
import com.sun.source.util.TreeScanner;

/**
 * javac plugin to generate Synthesijer-IR from Java source code
 * 
 * @author miyo
 *
 */
public class StreamAnalysisPlugin implements Plugin, TaskListener{

	@Override
	public String getName(){
		return "StreamAnalysis";
	}

	@Override
	public void init(JavacTask task, String... args){
		System.out.println("Stream Analysis Plugin");
		task.addTaskListener(this);
	}

	@Override
	public void started(TaskEvent e){
		if (e.getKind() == TaskEvent.Kind.GENERATE){
			System.out.println(e.getCompilationUnit());
			TopVisitor top = new TopVisitor();
			e.getCompilationUnit().accept(top, null);
        }
	}

	@Override
	public void finished(TaskEvent e){
	}

}

class TopVisitor extends TreeScanner<Void, Void>{

	public TopVisitor(){

	}

	@Override
	public  Void visitClass(ClassTree t, Void aVoid){
		return super.visitOther(t, aVoid);
	}
	
	@Override
	public Void visitMethod(MethodTree t, Void aVoid){
		return super.visitOther(t, aVoid);
	}

	@Override
	public Void visitOther(Tree t, Void aVoid){
		return super.visitOther(t, aVoid);
	}
	
}
