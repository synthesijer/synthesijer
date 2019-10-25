package synthesijer.algorithms;

import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.Assert.*;

import java.util.Hashtable;
import java.util.ArrayList;

public class DominatorTreeTest{

	private ArrayList<DominatorTreeNode<String>> genTestPattern1(){
		String[] s = {"0", "1", "2", "3", "4", "5", "6", "7"};
		ArrayList<DominatorTreeNode<String>> nodes = new ArrayList<>();
		for(int i = 0; i < s.length; i++){
			nodes.add(new DominatorTreeNode<String>(s[i]));
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
		var dt = new DominatorTree<String>(nodes.get(0));
		
        org.junit.Assert.assertEquals(0, dt.getDfsNum(nodes.get(0)));
        org.junit.Assert.assertEquals(1, dt.getDfsNum(nodes.get(1)));
        org.junit.Assert.assertEquals(2, dt.getDfsNum(nodes.get(2)));
        org.junit.Assert.assertEquals(3, dt.getDfsNum(nodes.get(3)));
        org.junit.Assert.assertEquals(4, dt.getDfsNum(nodes.get(4)));
        org.junit.Assert.assertEquals(5, dt.getDfsNum(nodes.get(5)));
        org.junit.Assert.assertEquals(6, dt.getDfsNum(nodes.get(6)));
        org.junit.Assert.assertEquals(7, dt.getDfsNum(nodes.get(7)));

		org.junit.Assert.assertEquals(nodes.get(0), dt.dominatorOf(nodes.get(1)).get());
        org.junit.Assert.assertEquals(nodes.get(1), dt.dominatorOf(nodes.get(2)).get());
        org.junit.Assert.assertEquals(nodes.get(1), dt.dominatorOf(nodes.get(3)).get());
        org.junit.Assert.assertEquals(nodes.get(3), dt.dominatorOf(nodes.get(4)).get());
        org.junit.Assert.assertEquals(nodes.get(1), dt.dominatorOf(nodes.get(5)).get());
        org.junit.Assert.assertEquals(nodes.get(2), dt.dominatorOf(nodes.get(6)).get());
        org.junit.Assert.assertEquals(nodes.get(6), dt.dominatorOf(nodes.get(7)).get());
	}

	private Hashtable<String, DominatorTreeNode<String>> genTestPattern2(){
		String[] s = {"R", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L"};
		Hashtable<String, DominatorTreeNode<String>> T = new Hashtable<>();
		for(int i = 0; i < s.length; i++){
			var n = new DominatorTreeNode<String>(s[i]);
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
		var dt = new DominatorTree<String>(T.get("R"));
		
        org.junit.Assert.assertEquals(T.get("R"), dt.dominatorOf(T.get("I")).get());
        org.junit.Assert.assertEquals(T.get("R"), dt.dominatorOf(T.get("K")).get());
        org.junit.Assert.assertEquals(T.get("R"), dt.dominatorOf(T.get("C")).get());
        org.junit.Assert.assertEquals(T.get("R"), dt.dominatorOf(T.get("H")).get());
        org.junit.Assert.assertEquals(T.get("R"), dt.dominatorOf(T.get("E")).get());
        org.junit.Assert.assertEquals(T.get("R"), dt.dominatorOf(T.get("A")).get());
        org.junit.Assert.assertEquals(T.get("R"), dt.dominatorOf(T.get("D")).get());
        org.junit.Assert.assertEquals(T.get("R"), dt.dominatorOf(T.get("B")).get());

        org.junit.Assert.assertEquals(T.get("C"), dt.dominatorOf(T.get("F")).get());
        org.junit.Assert.assertEquals(T.get("C"), dt.dominatorOf(T.get("G")).get());
		
        org.junit.Assert.assertEquals(T.get("G"), dt.dominatorOf(T.get("J")).get());

		org.junit.Assert.assertEquals(T.get("D"), dt.dominatorOf(T.get("L")).get());
    }

	@Test
    public void dfTest1(){
		String[] s = {"1", "2", "3", "4", "5", "6", "7"};
		Hashtable<String, DominatorTreeNode<String>> T = new Hashtable<>();
		for(int i = 0; i < s.length; i++){
			var n = new DominatorTreeNode<String>(s[i]);
			T.put(s[i], n);
		}
		T.get("1").addSucc(T.get("2"));
		T.get("2").addSucc(T.get("3"));
		T.get("2").addSucc(T.get("7"));
		T.get("3").addSucc(T.get("4"));
		T.get("3").addSucc(T.get("5"));
		T.get("4").addSucc(T.get("6"));
		T.get("5").addSucc(T.get("6"));
		T.get("6").addSucc(T.get("2"));

		var dt = new DominatorTree<String>(T.get("1"));

		org.junit.Assert.assertEquals(T.get("1"), dt.dominatorOf(T.get("2")).get());
		org.junit.Assert.assertEquals(T.get("2"), dt.dominatorOf(T.get("7")).get());
		org.junit.Assert.assertEquals(T.get("2"), dt.dominatorOf(T.get("3")).get());
		org.junit.Assert.assertEquals(T.get("3"), dt.dominatorOf(T.get("4")).get());
		org.junit.Assert.assertEquals(T.get("3"), dt.dominatorOf(T.get("5")).get());
		org.junit.Assert.assertEquals(T.get("3"), dt.dominatorOf(T.get("6")).get());

		org.junit.Assert.assertEquals(0, dt.dominanceFrontierOf(T.get("7")).size());
		org.junit.Assert.assertEquals(1, dt.dominanceFrontierOf(T.get("6")).size());
		org.junit.Assert.assertEquals(1, dt.dominanceFrontierOf(T.get("5")).size());
		org.junit.Assert.assertEquals(1, dt.dominanceFrontierOf(T.get("4")).size());
		org.junit.Assert.assertEquals(1, dt.dominanceFrontierOf(T.get("3")).size());
		org.junit.Assert.assertEquals(1, dt.dominanceFrontierOf(T.get("2")).size());
		org.junit.Assert.assertEquals(0, dt.dominanceFrontierOf(T.get("1")).size());
		
		org.junit.Assert.assertEquals(T.get("2"), dt.dominanceFrontierOf(T.get("6")).get(0));
		org.junit.Assert.assertEquals(T.get("6"), dt.dominanceFrontierOf(T.get("5")).get(0));
		org.junit.Assert.assertEquals(T.get("6"), dt.dominanceFrontierOf(T.get("4")).get(0));
		org.junit.Assert.assertEquals(T.get("2"), dt.dominanceFrontierOf(T.get("3")).get(0));
		org.junit.Assert.assertEquals(T.get("2"), dt.dominanceFrontierOf(T.get("2")).get(0));
	}

}
