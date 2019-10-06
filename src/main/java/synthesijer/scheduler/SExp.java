package synthesijer.scheduler;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class SExp {

	private ArrayList<Object> nodes = new ArrayList<Object>();

	public SExp() {
	}

	public String toString() {
		String exp = "";
		for (Object o : nodes) {
			if (exp.length() != 0) {
				exp += " ";
			}
			exp += o.toString();
		}
		return "( " + exp + " )";
	}

	public String toTreeString() {
		return toTreeString("");
	}

	private String toTreeString(String tab) {
		String s = "";
		for (Object o : nodes) {
			if (s.length() != 0) {
				s += "\n";
			}
			if (o instanceof SExp) {
				s += ((SExp) o).toTreeString(" " + tab);
			} else {
				s += tab + o.toString();
			}
		}
		return s;
	}

	private boolean isWhite(String s){
		return (s.trim().length() == 0);
	}

	public int parse(String src) throws Exception {
		byte[] bytes = src.getBytes();
		int index = 0;
		for (; bytes.length != index && bytes[index] != '('; index++){
			;
		}
		if (bytes.length == index) {
			throw (new Exception("[SExp] could not open bracket"));
		}
		for (index++; bytes[index] == ' '; index++){
			;
		}
		int point0 = index;
		for (; bytes.length != index && bytes[index] != ')'; index++) {
			if (bytes[index] == ' ') {
				if (point0 < index) {
					String s = src.substring(point0, index).trim();
					if(isWhite(s) == false) nodes.add(s);
				}
				for (index++; bytes.length != index && bytes[index] == ' '; index++){
					;
				}
				point0 = index;
				index--;
			} else if (bytes[index] == '(') {
				SExp exp = new SExp();
				int prog_index = exp.parse(src.substring(index));
				nodes.add(exp);
				index += prog_index;
				point0 = index + 1;
			}
		}
		if (bytes.length == index) {
			throw (new Exception("[SExp] could not close bracket"));
		}
		if (point0 != index) {
			String s = src.substring(point0, index).trim();
			if(isWhite(s) == false) nodes.add(s);
		}
		return index;
	}

	public int size() {
		return nodes.size();
	}

	public Object get(int index) throws Exception {
		return nodes.get(index);
	}

	public Object car() throws Exception{
		if(nodes.size() == 0) return null;
		return nodes.get(0);
	}

	public List<Object> cdr() throws Exception{
		if(nodes.size() < 2) return null;
		return nodes.subList(1, nodes.size());
	}

	public void append(String s){
		nodes.add(s);
	}

	public void append(SExp s){
		nodes.add(s);
	}

	public boolean is_a(String key) throws Exception{
		Object o = car();
		if(o != null && o instanceof String && ((String)o).equals(key)){
			return true;
		}else{
			return false;
		}
	}

	public static SExp load(String path) throws Exception{
		Path src = Paths.get(path);
		String str = new String(Files.readAllBytes(src));
		str = str.replaceAll("\n", " ");
		SExp s = new SExp();
		int index = s.parse(str);
		return s;
	}

	public static void main(String... args) throws Exception{
		SExp s = SExp.load(args[0]);
		System.out.println(s);
	}

}
