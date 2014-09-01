package synthesijer.model;

import java.util.ArrayList;

import synthesijer.ast.Expr;
import synthesijer.ast.Op;
import synthesijer.ast.Variable;

/**
 * This class is a basic item managed by SynthesisTable.
 * computation flow
 * 
 * @author miyo
 *
 */
public class SynthesisTableItem {
	
	class Entry{
		Op op;
		Variable dest;
		ArrayList<Expr> src = new ArrayList<>();
		State next;
		
		public void addSrc(Expr expr){
			src.add(expr);
		}
		
		public String toString(){
			String s = "Entry: ";
			s += "op=" + op;
			if(dest != null) s += ", dest=" + dest;
			if(next != null) s += ", next=" + next.getId();
			String sep = ", src=";
			for(Expr expr: src){
				s += sep + expr;
				sep = ", ";
			}
			return s;
		}
	}
	
	State state;
	ArrayList<Entry> entries = new ArrayList<>();
	
	public SynthesisTableItem(State state){
		this.state = state; 
	}
	
	public Entry newEntry(){
		Entry entry = new Entry();
		entries.add(entry);
		return entry;
	}
	
	public String toString(){
		String s = String.format("Item: state=%s\n", state.getId());
		for(Entry entry: entries){
			s += " " + entry.toString() + "\n";
		}
		return s;
	}

}
