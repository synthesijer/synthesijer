package synthesijer.jcfrontend;

import com.sun.source.tree.Tree;
import com.sun.source.tree.ClassTree;
import com.sun.source.util.TreeScanner;

public class PreScanner extends TreeScanner<Void, SourceInfo>{

	@Override
	public Void visitClass(ClassTree node, SourceInfo info) {
		info.className = node.getSimpleName().toString();
		return super.visitClass(node, null);
	}
	
}
