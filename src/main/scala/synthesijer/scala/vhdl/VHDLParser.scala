package synthesijer.scala.vhdl

import scala.util.parsing.combinator._
import scala.util.parsing.input._
import scala.language.postfixOps

trait NodeVisitor {
  def visit(e:Node) : Unit
}

trait Node extends Positional{
  def accept(visitor : NodeVisitor) : Unit = {
    visitor.visit(this)
  }
}
trait Kind extends Node
trait Expr extends Node
trait Range extends Node

case class LiteralNode(str:String) extends Node

case class DesignUnit(context:List[List[Node]], entity:Entity, arch:Option[Architecture]) extends Node
case class PackageUnit(context:List[List[Node]], pkg:PackageDecl) extends Node
case class LibraryUnit(entity:Entity, arch:Option[Architecture]) extends Node

case class StdLogic() extends Kind
case class VectorKind(name:String, step:String, b:Expr, e:Expr) extends Kind
case class UserTypeKind(name:String) extends Kind
case class IntegerKind() extends Kind
case class StringKind() extends Kind

case class Library(s : String) extends Node
case class Use(s : String) extends Node
case class Entity(s:String, ports:Option[List[PortItem]], params:Option[List[ParamItem]] = None) extends Node
case class PackageDecl(s:String, decl:List[Node]) extends Node
case class PortItem(name:String, dir:Option[String], kind:Node, init:Option[Expr]) extends Node{
  def this(name:String, dir:String, kind:Node, init:Option[Expr] = None) = {
    this(name, Some(dir), kind, init)
  }
}
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

case class NodeList(nodes:List[Node]) extends Positional
case class LiteralNodeList(nodes:List[LiteralNode]) extends Positional
case class ListNodeList(nodes:List[List[Node]]) extends Positional

class VHDLParser extends JavaTokenParsers{

  override protected val whiteSpace = """(\s|--.*)+""".r

  def digit = ( "[0-9]+".r )
  def upper_case_letter = ( "[A-Z]+".r )
  def lower_case_letter = ( "[a-z]+".r )
  def space_character = ( " " | "\t" | "\r" | "\n" )

  def letter = ( upper_case_letter | lower_case_letter )

  def letter_or_digit = ( letter | digit )

  def identifier = positioned( ident ^^ { case x => new LiteralNode(x) } ) // defined in JavaTokenParsers

  def logical_name = positioned ( identifier )

  def suffix = positioned ( "ALL" ^^ {_ => new LiteralNode("all") } | identifier )

  def selected_name = positioned ( identifier ~ "." ~ (identifier ~ ".").* ~ suffix ^^ {
    case x~"."~y~z =>
      new LiteralNode(x.str + "." + (for(yy <- y) yield { yy._1.str + "."}).mkString("") + z.str)
  } )

  def logical_name_list = positioned (
    logical_name ~ ("," ~ logical_name).* ^^ {
      case x~y => new LiteralNodeList(x :: ( for(yy <- y) yield { yy._2 } ))
    }
  )




  def library_clause = positioned ( "LIBRARY" ~> logical_name_list <~ ";" ^^ {
    case x => new NodeList(for(xx <- x.nodes) yield { new Library(xx.str) })
  } )

  def use_clause = positioned (
    "USE" ~> selected_name ~ ("," ~ selected_name).* <~ ";" ^^ {
      case x~y => {
        new NodeList (new Use(x.str) :: ( for(yy <- y) yield { new Use(yy._2.str) } ) )
      }
    }
  )

  def long_name = positioned ( identifier ~ ("." ~ identifier).* ^^ {
    case x~y => new LiteralNode(x.str + (for(yy <- y) yield { "." + yy._2.str }).mkString(""))
  } )

  def design_file : Parser[NodeList] = positioned ( design_unit ~ design_unit.* ^^ {
    case x~y => new NodeList(x :: y)
  } )

  def design_unit : Parser[Node] = positioned(
     context_clause ~ library_unit ^^ { case x~y => new DesignUnit(x.nodes, y.entity, y.arch) }
   | context_clause ~ package_decl ^^ { case x~y => new PackageUnit(x.nodes, y) }

  )

  def context_clause = positioned ( context_item.* ^^ { x => new ListNodeList(for(xx <- x) yield { xx.nodes }) } )

  def context_item = positioned ( library_clause | use_clause )

  def library_unit = positioned (
    entity_decl ~ architecture_decl.? ^^ {
      case x~y => new LibraryUnit(x, y)
    }
  )

  def package_decl = positioned(
    "PACKAGE" ~> long_name ~ "IS" ~ declarations.* ~ "END" ~ "PACKAGE" ~ long_name.? <~ ";" ^^ {
      case name~_~decls~_~_~name2 => new PackageDecl(name.str, decls)
    }
  )

  def function_name = positioned( long_name )

  def function_call : Parser[CallExpr] = positioned ( function_name ~ function_argument_list ^^ {
    case x~y => new CallExpr(new Ident(x.str), y)
  } )

  def kind_std_logic = ( "STD_LOGIC" ^^ {_ => new StdLogic() } )


  def entity_decl = positioned(
    "ENTITY" ~> long_name ~ "IS" ~ param_item_list.? ~ port_item_list.? <~ "END" ~ "ENTITY".? ~ long_name.? ~ ";" ^^ {
      case x~_~params~ports => {
        new Entity(x.str, ports, params)
      }
    }
  )

  def bit_value = ( "'" ~> letter_or_digit <~ "'" ^^ { case x => s"'$x'"} )

  def others_decl = ( "(" ~ "OTHERS" ~ "=>" ~> bit_value <~ ")" ^^ {case x => s"(others=>$x)" } )

  def signal_value = ( value_expression )

  def init_value = ":=" ~> ( prime_expression )

  def step_dir = ( "DOWNTO" | "TO" )

  def vector_type = ( "STD_LOGIC_VECTOR" | "SIGNED" | "UNSIGNED" )

  def kind_integer = ( "INTEGER" ^^ { _ => new IntegerKind()} )

  def kind_std_logic_vector = (
      vector_type ~ "(" ~ prime_expression ~ step_dir ~ prime_expression <~ ")" ^^ {
        case name~_~b~step~e => new VectorKind(name, step, b, e)
      }
    | vector_type ^^ {
      case name => new VectorKind(name, "", new NoExpr(), new NoExpr())
      }
  )

  def user_defined_type_kind = ( identifier ^^ {case x => new UserTypeKind(x.str) } )

  def kind = ( kind_std_logic_vector | kind_std_logic | kind_integer | user_defined_type_kind )

  def port_item = (
    long_name_list ~ ":" ~ identifier.? ~ kind ~ init_value.? ^^ {
      case name~_~dir~kind~init => {
        dir match {
          case Some(d) => new PortItem(name, d.str, kind, init)
          case None => new PortItem(name, None, kind, init)
        }
      }
    }
  |
    long_name_list ~ ":" ~ kind ~ init_value.? ^^ {
      case name~_~kind~init => new PortItem(name, None, kind, init)
    }
  )

  def param_item = ( long_name ~ ":" ~ kind ~ init_value ^^ {
    case name~_~kind~value => new ParamItem(name.str, kind, value)
  } )

  def port_item_list_body = "(" ~> port_item ~ ( ";" ~ port_item ).* <~ ")" ^^ {
    case x~y => x :: ( for(yy <- y) yield { yy._2 } )
  }

  def port_item_list = ( "PORT" ~> port_item_list_body <~ ";" )

  def param_item_list = ( "GENERIC" ~ "(" ~> param_item ~ ( ";" ~ param_item ).* <~ ")" ~ ";" ^^ {
    case x~y => x :: ( for(yy <- y) yield { yy._2 } )
  } )

  def declarations : Parser[Node] = (
    attribute_decl | component_decl | signal_decl | type_decl | set_attribute_decl | constant_decl | function_decl | shared_variable_decl
  )

  def architecture_statement : Parser[Node] = positioned (
    process_statement | instantiation_statement | assign_statement | generate_statement | block_decl
  )

  def architecture_decl = positioned (
    "ARCHITECTURE" ~> identifier ~ "OF" ~ long_name ~ "IS" ~
    declarations.* ~
    "BEGIN" ~
    architecture_statement.* <~
    "END" ~ "ARCHITECTURE".? ~ long_name.? ~ ";" ^^ {
      case kind~_~name~_~decls~_~body => {
        new Architecture(kind.str, name.str, decls, body)
      }
    }
  )

  def block_decl = positioned(
    statement_label.? ~ "BLOCK" ~ declarations.* ~ "BEGIN" ~ architecture_statement.* ~ "END" ~ "BLOCK" ~ long_name.? <~ ";" ^^ {
      case label~_~decls~_~body~_~_~name2 =>
        {
          label match{
            case Some(l) => new Block(Some(l.str), decls, body)
            case None => new Block(None, decls, body)
          }
        }
    }
  )

  def attribute_decl = positioned ( "ATTRIBUTE" ~> identifier ~ ":" ~ identifier <~ ";" ^^{
    case x~_~y => new Attribute(x.str, y.str)
  } )

  def component_decl = positioned (
    "COMPONENT" ~> long_name ~ "IS".? ~ param_item_list.? ~ port_item_list.? ~ "END" ~ "COMPONENT" ~ long_name.? <~ ";" ^^ {
      case name~_~params~ports~_~_~name2 => new ComponentDecl(name.str, ports, params)
    }
  )

  def long_name_list = (
    long_name ~ ("," ~ long_name).* ^^ {
      case x~y => ( x.str + ( for(yy <- y) yield { "," + yy._2.str } ).mkString("") )
    }
  )

  def signal_decl = positioned (
    "SIGNAL" ~> long_name_list ~ ":" ~ kind ~ init_value.? <~ ";" ^^ {
      case name~_~kind~init => new Signal(name, kind, init)
    }
  )

  def shared_variable_decl = positioned(
    "SHARED" ~ "VARIABLE" ~> long_name_list ~ ":" ~ kind ~ init_value.? <~ ";" ^^ {
      case name~_~kind~init => new SharedVariable(name, kind, init)
    }
  )

  def constant_decl = positioned (
    "CONSTANT" ~> long_name_list ~ ":" ~ kind ~ init_value <~ ";" ^^ {
      case name~_~kind~init => new ConstantDecl(name, kind, init)
    }
  )

  def variable_decl = positioned (
    "VARIABLE" ~> long_name_list ~ ":" ~ kind ~ init_value.? <~ ";" ^^ {
      case name~_~kind~init => new Variable(name, kind, init)
    }
  )

  def direction = ( "OUT" | "IN" | "INOUT" )
  def filepath = ( stringLiteral )

  def file_decl = positioned (
    "FILE" ~> long_name_list ~ ":" ~ identifier ~ "IS" ~ direction ~ filepath <~ ";" ^^ {
      case x~_~y~_~d~s => new FileDecl(x, y.str, d, s)
    }
  )

  def symbol_list = (
    identifier ~ ("," ~ identifier).* ^^ {
      case x~y => new Ident(x.str) :: (for (yy <- y) yield { new Ident(yy._2.str) })
    }
  )

  def type_decl = positioned(
      "TYPE" ~> identifier ~ "IS" ~ "(" ~ symbol_list ~ ")" <~ ";" ^^ {
        case x~_~_~l~_ => new UserType(x.str, l)
      }
    | "TYPE" ~> identifier ~ "IS" ~ "ARRAY" ~
      "(" ~ prime_expression ~ step_dir ~ prime_expression ~ ")" ~ "OF" ~ kind <~ ";" ^^ {
        case x~_~_~_~y~z~w~_~_~v => new ArrayType(x.str, z, y, w, v)
      }
  )

  def call_statement = positioned(
    function_call <~ ";" ^^ {
      case x => new CallStatement(x)
    }
  )

  def assign_statement = positioned(
    extended_value_expression ~ "<=" ~ expression <~ ";" ^^ {
      case lhs~_~rhs => new AssignStatement(lhs, rhs)
    }
  )

  def blocking_assign_statement = positioned(
    extended_value_expression ~ ":=" ~ expression <~ ";" ^^ {
      case lhs~_~rhs => new BlockingAssignStatement(lhs, rhs)
    }
  )

  def sensitivity_list = (
      "(" ~> symbol_list <~ ")" ^^ { case x => x }
    | "(" ~ ")" ^^ { case _ => List() }
  )

  def expression_list = (
    prime_expression ~ ("," ~ prime_expression).* ^^ {
      case x~y => x :: (for (yy <- y) yield { yy._2 })
    }
  )

  def function_argument_list = (
      "(" ~> expression_list <~ ")" ^^ { case x => x }
    | "(" ~ ")" ^^ { case _ => List() }
  )

  def statement_label = long_name <~ ":" ^^ { case x => x }

  def process_decl = file_decl | variable_decl

  def process_statement = positioned(
    statement_label.? ~ "PROCESS" ~ sensitivity_list.? ~
    process_decl.* ~
    "BEGIN" ~
    statement_in_process.* ~
    "END" ~ "PROCESS" ~ long_name.? <~ ";" ^^ {
      case name~_~sensitivities~variables~_~stmts~_~_~name2 => {
        name match {
          case Some(n) => new ProcessStatement(sensitivities, Some(n.str), stmts, variables)
          case None => new ProcessStatement(sensitivities, None, stmts, variables)
        }
      }
    }
  )

  def generate_statement = positioned ( generate_for_statement | generate_for_loop | generate_if_statement )

  def generate_for_statement = positioned (
    identifier ~ ":" ~ "FOR" ~ identifier ~ "IN" ~ prime_expression ~ step_dir ~ prime_expression ~ "GENERATE" ~
    "BEGIN" ~ architecture_statement.* ~ "END" ~ "GENERATE" ~ identifier.? <~ ";" ^^ {
      case name~_~_~i~_~b~s~e~_~_~lst~_~_~name2 => new GenerateFor(new Ident(name.str), new Ident(i.str), s, b, e, lst)
    }
  )

  def generate_if_statement = positioned (
    identifier ~ ":" ~ "IF" ~ prime_expression ~ "GENERATE" ~
    "BEGIN".? ~ architecture_statement.* ~ "END" ~ "GENERATE" ~ identifier.? <~ ";" ^^ {
      case name~_~_~expr~_~_~lst~_~_~name2 => new GenerateIf(new Ident(name.str), expr, lst)
    }
  |
    identifier ~ ":" ~ "IF" ~ prime_expression ~ "GENERATE" ~
    "BEGIN".? ~ architecture_statement ^^ {
      case name~_~_~expr~_~_~stmt => new GenerateIf(new Ident(name.str), expr, List(stmt))
    }
  )

  def unary_expression = ("-"|"NOT") ~ expression ^^ { case x~y => new UnaryExpr(x, y) }

  def binary_operation = compare_operation | mul_div_operation | add_sub_operation | logic_operation | concat_operation

  def compare_operation = ( "=" | ">=" | "<=" | ">" | "<" ) ^^ { case x => x } |
                          "/" ~ "=" ^^ { case x~y => x+y }

  def logic_operation = "AND" | "OR" | "XOR"

  def mul_div_operation = "*" | "/" | "MOD"

  def add_sub_operation = "+" | "-"

  def concat_operation = "&"

  def hex_value = ("X"|"x") ~> stringLiteral ^^ { case x => new BasedValue(x, 16) }
  def bin_value = ("B"|"b") ~> stringLiteral ^^ { case x => new BasedValue(x, 2) }

  def bit_padding_expression = positioned (
    "(" ~ prime_expression ~ step_dir ~ prime_expression ~ "=>" ~ prime_expression ~ ")" ^^ {
      case _~b~step~e~_~v~_ => new BitPaddingExpr(step, b, e, v)
    }
  )

  def others_expr = "(" ~ "OTHERS" ~ "=>" ~> extended_value_expression <~ ")" ^^ {
    case x => new Others(x)
  }

  def value_expression : Parser[Expr] = positioned(
    "(" ~> expression <~ ")" ^^ {case x => x }
    | hex_value   ^^ { case x => x }
    | bin_value   ^^ { case x => x }
    | bit_value   ^^ { case x => new Constant(x) }
    | others_expr ^^ { case x => x }
    | identifier ~ "'" ~ identifier ^^ { case x~_~y => new Ident(s"${x.str}'${y.str}") }
    | long_name   ^^ { case x => new Ident(x.str) }
    | identifier  ^^ { case x => new Ident(x.str) }
    | decimalNumber ^^ { case x => new Constant(x) }
    | stringLiteral ^^ { case x => new Constant(x) }
  )

  def extended_value_expression : Parser[Expr] = positioned (
    bit_vector_select | function_call | bit_padding_expression | value_expression
  )

  def when_expression : Parser[Expr] = positioned ( prime_expression ~ "WHEN" ~ prime_expression ~ "ELSE" ~ expression ^^ {
    case thenExpr~_~cond~_~elseExpr => new WhenExpr(cond, thenExpr, elseExpr)
  }
  )

  def bit_vector_select_arg_range = positioned (
     "(" ~> prime_expression ~ step_dir ~ prime_expression <~ ")" ^^ { case b~s~e => new BitVectorSelect(null, s, b, e) }
  )

  def bit_vector_select_arg = positioned (
        bit_vector_select_arg_range
      | "(" ~> prime_expression <~ ")" ^^ { case b => new BitVectorSelect(null, "at", b, b) }
  )

  def bit_vector_select = positioned (
    long_name ~ bit_vector_select_arg ~ bit_vector_select_arg.+ ^^ {
      case n~bs~bss => {
        // TODO: should be written with foldl
        var x = new BitVectorSelect(new Ident(n.str), bs.step, bs.b, bs.e)
        for(bss_i <- bss) {
          x = new BitVectorSelect(x, bss_i.step, bss_i.b, bss_i.e)
        }
        x
      }
    }
    |
    long_name ~ bit_vector_select_arg_range ^^ {
      case n~bs => new BitVectorSelect(new Ident(n.str), bs.step, bs.b, bs.e)
    }
  )

  def binary_expression = positioned (extended_value_expression ~ binary_operation ~ expression ^^ {case x~op~y => new BinaryExpr(op, x, y) })

  def prime_expression : Parser[Expr] = positioned ( unary_expression | binary_expression | extended_value_expression )

  def expression : Parser[Expr] = positioned ( when_expression | prime_expression )

  def null_statement = "NULL" ~ ";" ^^ { _ => new NullStatement() }

  def trigger = "FOR"
  def unit = "NS" | "PS"

  def wait_statement = (
       "WAIT" ~> trigger ~ prime_expression ~ unit <~ ";" ^^ { case x~y~z => new WaitStatement(x, new TimeUnit(y, z)) }
     | "WAIT" ~ ";" ^^ { case _ => new WaitStatement("", null) }
  )

  def report_statement = "REPORT" ~> stringLiteral <~ ";" ^^ { case x =>  new ReportStatement(x) }

  def statement_in_process : Parser[Node] = positioned (
        if_statement
      | assign_statement
      | blocking_assign_statement
      | call_statement
      | null_statement
      | case_statement
      | wait_statement
      | report_statement
      | generate_for_loop
  )

  def else_clause_in_if_statement : Parser[List[Node]] = "ELSE" ~> statement_in_process.* ^^ { case x => x }

  def elsif_clause_in_if_statement : Parser[IfStatement] =
    "ELSIF" ~> prime_expression ~ "THEN" ~ statement_in_process.* ^^ {
      case x~_~y => new IfStatement(x, y, List(), None)
    }

  def if_statement =
    "IF" ~> prime_expression ~ "THEN" ~
    statement_in_process.* ~
    elsif_clause_in_if_statement.* ~
    else_clause_in_if_statement.? ~
    "END" <~ "IF" ~ ";" ^^ {
      case cond~_~thenPart~elifPart~elsePart~_ => new IfStatement(cond, thenPart, elifPart, elsePart)
    }

  def case_when_clause = "WHEN" ~> expression ~ "=>" ~ statement_in_process.* ^^ {
    case label~_~stmts => new CaseWhenClause(label, stmts)
  }

  def case_statement =
    "CASE" ~> expression ~ "IS" ~ case_when_clause.* <~ "END" ~ "CASE" ~ ";" ^^ {
      case cond~_~whenList => new CaseStatement(cond, whenList)
    }

  def portmap_list_item  =
    extended_value_expression ~ "=>" ~ prime_expression ~ ("," ~ extended_value_expression ~ "=>" ~ prime_expression).* ^^ {
      case x~_~y~z =>
        new PortMapItem(x, y) :: ( for(zz <- z) yield { new PortMapItem(zz._1._1._2, zz._2) } )
    }

  def portmap_list = "(" ~> portmap_list_item <~ ")" ^^ { case x => x } |
                     simple_portmap_list ^^ {case x => x } |
                     "(" ~ ")" ^^ { case _ => List() }

  def genericmap = "GENERIC" ~ "MAP" ~> portmap_list ^^ { case x => x }

  def simple_portmap_list = "(" ~> prime_expression ~ ("," ~> prime_expression).* <~ ")" ^^ {
    case x~y => new PortMapItem(new NoExpr(), x) :: ( for(yy <- y) yield { new PortMapItem(new NoExpr(), yy) } )
  }

  def instantiation_statement = positioned (
    long_name ~ ":" ~ long_name ~ genericmap.? ~ "PORT" ~ "MAP" ~ portmap_list <~ ";" ^^ {
      case iname~_~mname~params~_~_~ports => new InstanceStatement(new Ident(iname.str), new Ident(mname.str), ports, params)
    }
  )

  def set_attribute_decl =
    "ATTRIBUTE" ~> identifier ~ "OF" ~ long_name ~ ":" ~ ("SIGNAL"|"VARIABLE") ~ "IS" ~ value_expression <~ ";" ^^ {
      case x~_~y~_~_~_~z => new SetAttribute(x.str, y.str, z)
    }

  def range = (
      prime_expression ~ step_dir ~ prime_expression ^^ { case x~y~z => new RegionRange(y, x, z) }
    | prime_expression ^^ { x => new SymbolRange(x) }
  )

  def generate_for_loop = positioned(
    "FOR" ~> identifier ~ "IN" ~ range ~ "LOOP" ~ statement_in_process.* <~ "END" ~ "LOOP" ~ ";" ^^ {
      case x~_~y~_~z => new ForLoop(None, new Ident(x.str), y, z)
    }
  )

  def return_statement = "RETURN" ~> prime_expression <~ ";" ^^ { case x => new ReturnStatement(x) }

  def function_body_statement = statement_in_process | generate_statement | return_statement

  def function_decl = 
    "FUNCTION" ~ long_name ~ port_item_list_body ~ "RETURN" ~ kind ~ "IS" ~ variable_decl.* ~
    "BEGIN" ~ function_body_statement.* ~ "END" ~ "FUNCTION" ~ ";" ^^{
      case _~x~y~_~k~_~v~_~s~_~_~_ =>
        new FunctionDecl(x.str, y, k, v, s)
    }

  implicit override def literal(s: String): Parser[String] = new Parser[String] {
    def apply(in: Input) = {
      val source = in.source
      val offset = in.offset
      val start = handleWhiteSpace(source, offset)
      var i = 0
      var j = start
      while (i < s.length && j < source.length && s.charAt(i).toUpper == source.charAt(j).toUpper) {
        i += 1
        j += 1
      }
      if (i == s.length)
        Success(source.subSequence(start, j).toString, in.drop(j - offset))
      else  {
        val found = if (start == source.length()) "end of source" else "`"+source.charAt(start)+"'"
        Failure("`"+s+"' expected but "+found+" found", in.drop(start - offset))
      }
    }
  }

  def parse( input: String ) = parseAll(design_file, input) match {
    case Success(result, _ ) => Option(result.nodes)
    case NoSuccess(msg, next) => println(msg); println(next.pos.line); println(next.pos.column); None
    case Failure(e, _)       => println(e); None
    case _                   => None
  }

}

object VHDLParser{
  def main(args:Array[String]) = {
    val m = new VHDLParser()
    val ret = m.parse("LIBRARY IEEE ;")
    println(ret)
  }
}

