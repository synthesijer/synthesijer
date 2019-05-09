package synthesijer;

import com.sun.source.util.JavacTask;
import com.sun.source.util.Plugin;
import com.sun.source.util.TaskListener;
import com.sun.source.util.TaskEvent;
import com.sun.source.util.TreeScanner;

import java.util.ArrayList;
import java.util.List;

import synthesijer.stream.*;

/**
 * javac plugin to generate Synthesijer-IR from Java source code
 * 
 * @author miyo
 *
 */
public class StreamAnalysisPlugin implements Plugin, TaskListener{

	StreamModuleScanner top;
	
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
			//System.out.println(e.getCompilationUnit());
			top = new StreamModuleScanner();
			e.getCompilationUnit().accept(top, null);
        }
	}

	@Override
	public void finished(TaskEvent e){
		if (e.getKind() == TaskEvent.Kind.GENERATE){
			for(var m: top.modules){
				//m.output(System.out);
				System.out.println(m);
			}
		}
	}

}
