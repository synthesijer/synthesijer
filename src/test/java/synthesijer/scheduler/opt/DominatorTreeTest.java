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

	private ArrayList<DominatorTreeNode<String>> genTestPattern1(){
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
    public void dominantsTest1(){
		var nodes = genTestPattern1();
		var dtg = new DominatorTreeGenerator<String>(nodes.get(0));
		
        org.junit.Assert.assertEquals(0, dtg.getDfsNum(nodes.get(0)));
        org.junit.Assert.assertEquals(1, dtg.getDfsNum(nodes.get(1)));
        org.junit.Assert.assertEquals(2, dtg.getDfsNum(nodes.get(2)));
        org.junit.Assert.assertEquals(3, dtg.getDfsNum(nodes.get(3)));
        org.junit.Assert.assertEquals(4, dtg.getDfsNum(nodes.get(4)));
        org.junit.Assert.assertEquals(5, dtg.getDfsNum(nodes.get(5)));
        org.junit.Assert.assertEquals(6, dtg.getDfsNum(nodes.get(6)));
        org.junit.Assert.assertEquals(7, dtg.getDfsNum(nodes.get(7)));

		org.junit.Assert.assertEquals(nodes.get(0), dtg.dominatorOf(nodes.get(1)));
        org.junit.Assert.assertEquals(nodes.get(1), dtg.dominatorOf(nodes.get(2)));
        org.junit.Assert.assertEquals(nodes.get(1), dtg.dominatorOf(nodes.get(3)));
        org.junit.Assert.assertEquals(nodes.get(3), dtg.dominatorOf(nodes.get(4)));
        org.junit.Assert.assertEquals(nodes.get(1), dtg.dominatorOf(nodes.get(5)));
        org.junit.Assert.assertEquals(nodes.get(2), dtg.dominatorOf(nodes.get(6)));
        org.junit.Assert.assertEquals(nodes.get(6), dtg.dominatorOf(nodes.get(7)));
	}

	private Hashtable<String, DominatorTreeNode<String>> genTestPattern2(){
		String[] s = {"R", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L"};
		Hashtable<String, DominatorTreeNode<String>> T = new Hashtable<>();
		for(int i = 0; i < s.length; i++){
			var n = new DominatorTreeNode<String>(s[i], s[i]);
			T.put(s[i], n);
		}
		
		T.get("R").addSucc(T.get("A"));
		T.get("R").addSucc(T.get("B"));
		T.get("R").addSucc(T.get("C"));
		
		T.get("A").addSucc(T.get("D"));

		T.get("B").addSucc(T.get("A"));
		T.get("B").addSucc(T.get("D"));
		T.get("B").addSucc(T.get("E"));
		
		T.get("C").addSucc(T.get("F"));
		T.get("C").addSucc(T.get("G"));

		T.get("D").addSucc(T.get("L"));
		
		T.get("E").addSucc(T.get("H"));
		
		T.get("F").addSucc(T.get("I"));
		
		T.get("G").addSucc(T.get("I"));
		T.get("G").addSucc(T.get("J"));
		
		T.get("H").addSucc(T.get("E"));
		T.get("H").addSucc(T.get("K"));
		
		T.get("I").addSucc(T.get("K"));
		
		T.get("J").addSucc(T.get("I"));
		
		T.get("K").addSucc(T.get("R"));
		T.get("K").addSucc(T.get("I"));
		
		T.get("L").addSucc(T.get("H"));
		
		return T;
	}

    @Test
    public void dominantsTest2(){
		var T = genTestPattern2();
		var dtg = new DominatorTreeGenerator<String>(T.get("R"));
		
        org.junit.Assert.assertEquals(T.get("R"), dtg.dominatorOf(T.get("I")));
        org.junit.Assert.assertEquals(T.get("R"), dtg.dominatorOf(T.get("K")));
        org.junit.Assert.assertEquals(T.get("R"), dtg.dominatorOf(T.get("C")));
        org.junit.Assert.assertEquals(T.get("R"), dtg.dominatorOf(T.get("H")));
        org.junit.Assert.assertEquals(T.get("R"), dtg.dominatorOf(T.get("E")));
        org.junit.Assert.assertEquals(T.get("R"), dtg.dominatorOf(T.get("A")));
        org.junit.Assert.assertEquals(T.get("R"), dtg.dominatorOf(T.get("D")));
        org.junit.Assert.assertEquals(T.get("R"), dtg.dominatorOf(T.get("B")));

        org.junit.Assert.assertEquals(T.get("C"), dtg.dominatorOf(T.get("F")));
        org.junit.Assert.assertEquals(T.get("C"), dtg.dominatorOf(T.get("G")));
		
        org.junit.Assert.assertEquals(T.get("G"), dtg.dominatorOf(T.get("J")));

		org.junit.Assert.assertEquals(T.get("D"), dtg.dominatorOf(T.get("L")));
    }

}
