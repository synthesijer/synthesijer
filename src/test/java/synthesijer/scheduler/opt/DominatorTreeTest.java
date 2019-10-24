package synthesijer.scheduler.opt;

import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.Assert.*;

import java.util.Hashtable;
import java.util.ArrayList;

import synthesijer.ast.Module;
import synthesijer.scheduler.SchedulerInfo;

public class DominatorTreeTest{

	private ArrayList<DominatorTreeNode<String>> genTestPattern(){
		String[] s = {"0", "1", "2", "3", "4", "5", "6", "7"};
		ArrayList<DominatorTreeNode<String>> nodes = new ArrayList<>();
		for(int i = 0; i < s.length; i++){
			nodes.add(new DominatorTreeNode<String>(s[i], s[i]));
		}
		nodes.get(0).addSucc(nodes.get(1));
		nodes.get(1).addSucc(nodes.get(2));
		nodes.get(1).addSucc(nodes.get(3));
		nodes.get(2).addSucc(nodes.get(3));
		nodes.get(2).addSucc(nodes.get(6));
		nodes.get(3).addSucc(nodes.get(4));
		nodes.get(4).addSucc(nodes.get(5));
		nodes.get(6).addSucc(nodes.get(7));
		nodes.get(7).addSucc(nodes.get(5));
		return nodes;
	}
	
    @Test
    public void dfsTest1(){
		var nodes = genTestPattern();
		var dtg = new DominatorTreeGenerator<String>(nodes.get(0));
		
        org.junit.Assert.assertEquals(0, dtg.getDfsNum(nodes.get(0)));
        org.junit.Assert.assertEquals(1, dtg.getDfsNum(nodes.get(1)));
        org.junit.Assert.assertEquals(2, dtg.getDfsNum(nodes.get(2)));
        org.junit.Assert.assertEquals(3, dtg.getDfsNum(nodes.get(3)));
        org.junit.Assert.assertEquals(4, dtg.getDfsNum(nodes.get(4)));
        org.junit.Assert.assertEquals(5, dtg.getDfsNum(nodes.get(5)));
        org.junit.Assert.assertEquals(6, dtg.getDfsNum(nodes.get(6)));
        org.junit.Assert.assertEquals(7, dtg.getDfsNum(nodes.get(7)));
	}

    @Test
    public void dominantsTest1(){
		var nodes = genTestPattern();
		var dtg = new DominatorTreeGenerator<String>(nodes.get(0));
		
        org.junit.Assert.assertEquals(nodes.get(1), dtg.dominantsOf(nodes.get(0)).get(0));
        org.junit.Assert.assertEquals(nodes.get(2), dtg.dominantsOf(nodes.get(1)).get(0));
        org.junit.Assert.assertEquals(nodes.get(3), dtg.dominantsOf(nodes.get(1)).get(1));
        org.junit.Assert.assertEquals(nodes.get(4), dtg.dominantsOf(nodes.get(3)).get(0));
        org.junit.Assert.assertEquals(nodes.get(5), dtg.dominantsOf(nodes.get(1)).get(2));
        org.junit.Assert.assertEquals(nodes.get(6), dtg.dominantsOf(nodes.get(2)).get(0));
        org.junit.Assert.assertEquals(nodes.get(7), dtg.dominantsOf(nodes.get(6)).get(0));
    }

}
