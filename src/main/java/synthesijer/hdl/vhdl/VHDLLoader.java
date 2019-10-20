package synthesijer.hdl.vhdl;

import java.io.*;
import java.nio.*;
import java.nio.file.*;
import java.util.ArrayList;

import scala.Option;
import scala.collection.immutable.List;
import scala.collection.immutable.Vector;
import synthesijer.SynthesijerUtils;
import synthesijer.hdl.*;
import synthesijer.scala.vhdl.*;

import net.wasamon.mjlib.util.GetOpt;

public class VHDLLoader{

	Option<List<Node>> parseResult;
	
	public ArrayList<HDLModule> modules = new ArrayList<>();

	public VHDLLoader(String pathStr) throws IOException{
		Path path = Paths.get(pathStr);
		byte[] bcontent = Files.readAllBytes(path);
		String content = new String(bcontent);
		VHDLParser obj = new VHDLParser();
		this.parseResult = obj.parse(content);
		if(parseResult.isEmpty() == false){
			loadModule(parseResult.get(), pathStr);
		}else{
			SynthesijerUtils.warn("cannot parse VHDL-file: " + path);
		}
	}

	private void loadModule(List<Node> result, String path) {
		var it = result.iterator();
		while(it.hasNext()){
			var n = it.next();
			if(n instanceof DesignUnit) {
				HDLModule hm = loadDesignUnit((DesignUnit) n);
				modules.add(hm);
			}else{
				SynthesijerUtils.warn("cannot parse VHDL-file: " + path);
			}
		}
	}

	private HDLModule loadDesignUnit(DesignUnit unit){
		HDLModule hm = loadModule(unit.entity());
		if(unit.arch().isEmpty() == false){
			loadArchitecture(unit.arch().get());
		}
		return hm;
	}

	private HDLModule loadModule(Entity entity){
		System.out.println("load module : " + entity.name());
		HDLModule hm = new HDLModule(entity.name());
		if(entity.ports().isEmpty() == false){
			var it = entity.ports().get().iterator();
			while(it.hasNext()){
				var n = it.next();
				loadPortItem(hm, n);
			}
		}
		return hm;
	}

	private void loadPortItem(HDLModule hm, PortItem n){
		var name = n.name();
		var dir = n.dir();
		var kind = n.kind();
		var init = n.init();
		hm.newPort(name, convStrToDir(dir), loadKind(kind));
	}

	private HDLPort.DIR convStrToDir(Option<String> s){
		if(s.isEmpty()){
			return HDLPort.DIR.IN; // default
		}else if(s.get().toUpperCase().equals("IN")){
			return HDLPort.DIR.IN;
		}else if(s.get().toUpperCase().equals("OUT")){
			return HDLPort.DIR.OUT;
		}else{
			return HDLPort.DIR.INOUT;
		}
	}

	private HDLType loadKind(Kind k){
		if(k instanceof StdLogic){
			return HDLPrimitiveType.genBitType();
		}else if(k instanceof VectorKind){
			var kk = (VectorKind)k;
			if(kk.name().toUpperCase().equals("SIGNED")) {
				return HDLPrimitiveType.genSignedType(0, convExprToStr(kk.b()), convExprToStr(kk.e()));
			}else if(kk.name().toUpperCase().equals("UNSIGNED")){
				return HDLPrimitiveType.genUnsignedType(0, convExprToStr(kk.b()), convExprToStr(kk.e()));
			}else if(kk.name().toUpperCase().equals("STD_LOGIC_VECTOR")){
				return HDLPrimitiveType.genVectorType(0, convExprToStr(kk.b()), convExprToStr(kk.e()));
			}
		}else if(k instanceof IntegerKind){
			return HDLPrimitiveType.genIntegerType();
		}else if(k instanceof StringKind){
			return HDLPrimitiveType.genStringType();
		}else if(k instanceof UserTypeKind){
			var kk = (UserTypeKind)k;
			return new HDLUserDefinedType(kk.name(), new String[]{}, 0);
		}
		return HDLPrimitiveType.genUnknowType();
	}

	private String convExprToStr(Expr expr){
		return "";
	}

	private void loadArchitecture(Architecture arch){

	}

	/*
case class LiteralNode(str:String) extends Node

case class DesignUnit(context:List[List[Node]], entity:Entity, arch:Option[Architecture]) extends Node
case class PackageUnit(context:List[List[Node]], pkg:PackageDecl) extends Node
case class LibraryUnit(entity:Entity, arch:Option[Architecture]) extends Node

case class Library(s : String) extends Node
case class Use(s : String) extends Node
case class Entity(name:String, ports:Option[List[PortItem]], params:Option[List[ParamItem]] = None) extends Node
case class PackageDecl(s:String, decl:List[Node]) extends Node
case class ParamItem(name:String, kind:Kind, value:Expr) extends Node

case class Architecture(kind:String, name:String, decls:List[Node], body:List[Node]) extends Node
case class Attribute(name:String, kind:String) extends Node
case class ComponentDecl(name:String, ports:Option[List[PortItem]], params:Option[List[ParamItem]]) extends Node
case class Signal(name:String, kind:Kind, init:Option[Expr] = None) extends Node
case class UserType(name:String, items:List[Ident]) extends Node
case class ArrayType(name:String, step:String, b:Expr, e:Expr, kind:Kind) extends Node
case class SetAttribute(key:String, target:String, value:Expr) extends Node
case class Variable(name:String, kind:Kind, init:Option[Expr] = None) extends Node
case class SharedVariable(name:String, kind:Kind, init:Option[Expr] = None) extends Node
case class FileDecl(name:String, kind:String, io:String, src:String) extends Node
case class ConstantDecl(name:String, kind:Kind, init:Expr) extends Node

case class Block(label:Option[String], decls:List[Node], body:List[Node]) extends Node
case class ProcessStatement(sensitivities:Option[List[Ident]], label:Option[String], body:List[Node], variables:List[Node] = List()) extends Node
case class GenerateFor(label:Ident, index:Ident, step:String, b:Expr, e:Expr, body:List[Node]) extends Node
case class GenerateIf(label:Ident, expr:Expr, body:List[Node]) extends Node

case class InstanceStatement(name:Ident, module:Ident, ports:List[PortMapItem], params:Option[List[PortMapItem]]) extends Node
case class PortMapItem(name:Expr, expr:Expr) extends Node

case class AssignStatement(dest:Expr, src:Expr) extends Node
case class CallStatement(expr:CallExpr) extends Node
case class BlockingAssignStatement(dest:Expr, src:Expr) extends Node
case class IfStatement(cond:Expr, thenPart:List[Node], elifPart:List[IfStatement], elsePart:Option[List[Node]]) extends Node
case class CaseStatement(cond:Expr, whenPart:List[CaseWhenClause]) extends Node
case class CaseWhenClause(label:Expr, stmts:List[Node]) extends Node
case class NullStatement() extends Node
case class WaitStatement(trigger:String, value:TimeUnit) extends Node
case class TimeUnit(value:Expr, unit:String) extends Node
case class ReportStatement(mesg:String) extends Node

case class BasedValue(value:String, base:Int) extends Expr
case class Constant(value:String) extends Expr
case class Others(value:Expr) extends Expr
case class Ident(name:String) extends Expr
case class BinaryExpr(op:String, lhs:Expr, rhs:Expr) extends Expr
case class UnaryExpr(op:String, expr:Expr) extends Expr
case class CallExpr(name:Expr, args:List[Expr]) extends Expr
case class WhenExpr(cond:Expr, thenExpr:Expr, elseExpr:Expr) extends Expr
case class BitPaddingExpr(step:String, b:Expr, e:Expr, value:Expr) extends Expr
case class BitVectorSelect(ident:Expr, step:String, b:Expr, e:Expr) extends Expr
case class NoExpr() extends Expr

case class ForLoop(name:Option[String], idx:Ident, range:Range, body:List[Node]) extends Expr
case class SymbolRange(value:Expr) extends Range
case class RegionRange(step:String, b:Expr, e:Expr) extends Range
case class FunctionDecl(name:String, args:List[PortItem], retKind:Kind, vars:List[Node], body:List[Node]) extends Node
case class ReturnStatement(vale:Expr) extends Node
case class CommandStatement(str:String) extends Node
	*/

	private void loadModule(Node node){

	}

	public static void main(String... args) throws IOException{
		GetOpt opt = new GetOpt("",	"check,error-only,vhdl,verilog", args);
		
		String path = opt.getArgs()[0];

		if (opt.flag("check") && (opt.flag("error-only") == false)){
			System.out.println(path);
		}

		VHDLLoader loader = new VHDLLoader(path);
		var result = loader.parseResult;
		
		if (opt.flag("check")){
			if(result.isEmpty()){
				if (opt.flag("error-only")) System.out.println(path);
				System.out.println("..." + result);
			}else if(opt.flag("error-only") == false){
				System.out.println("..." + "OK");
			}
		}

		if (opt.flag("vhdl")){
			for(HDLModule hm: loader.modules){
				hm.genVHDL(new PrintWriter(System.out, true));
			}
		}
		if (opt.flag("verilog")){
			for(HDLModule hm: loader.modules){
				hm.genVHDL(new PrintWriter(System.out, true));
			}
		}

		if(opt.flag("vhdl") == false && opt.flag("verilog") == false){
			System.out.println(result);
		}

	}

}
