package synthesijer.scala.vhdl

import scala.util.parsing.combinator._
import scala.language.postfixOps

trait NodeVisitor {
  def visit(e:Node) : Unit
}

trait Node {
  def accept(visitor : NodeVisitor) : Unit = {
    visitor.visit(this)
  }
}
trait Kind extends Node
trait Expr extends Node

case class DesignUnit(context:List[List[Node]], entity:Entity, arch:Option[Architecture])
case class LibraryUnit(entity:Entity, arch:Option[Architecture])

case class StdLogic() extends Kind
case class VectorKind(name:String, step:String, b:Expr, e:Expr) extends Kind
case class UserTypeKind(name:String) extends Kind
case class IntegerKind() extends Kind

case class Library(s : String) extends Node
case class Use(s : String) extends Node
case class Entity(s:String, ports:Option[List[PortItem]], params:Option[List[ParamItem]] = None) extends Node
case class PortItem(name:String, dir:String, kind:Node, init:Option[Expr] = None) extends Node
case class ParamItem(name:String, kind:Kind, value:Expr) extends Node

case class Architecture(kind:String, name:String, decls:List[Node], body:List[Node]) extends Node
case class Attribute(name:String, kind:String) extends Node
case class ComponentDecl(name:String, ports:Option[List[PortItem]], params:Option[List[ParamItem]]) extends Node
case class Signal(name:String, kind:Kind, init:Option[Expr] = None) extends Node
case class UserType(name:String, items:List[Ident]) extends Node
case class ArrayType(name:String, step:String, b:Expr, e:Expr, kind:Kind) extends Node
case class SetAttribute(key:String, target:String, value:Expr) extends Node
case class Variable(name:String, kind:Kind, init:Option[Expr] = None) extends Node
case class FileDecl(name:String, kind:String, io:String, src:String) extends Node
case class ConstantDecl(name:String, kind:Kind, init:Expr) extends Node

case class ProcessStatement(sensitivities:Option[List[Ident]], label:Option[String], body:List[Node], variables:List[Node] = List()) extends Node

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
case class CallExpr(name:Ident, args:List[Expr]) extends Expr
case class WhenExpr(cond:Expr, thenExpr:Expr, elseExpr:Expr) extends Expr
case class BitPaddingExpr(step:String, b:Expr, e:Expr, value:Expr) extends Expr
case class BitVectorSelect(ident:Ident, step:String, b:Expr, e:Expr) extends Expr

class VHDLParser extends JavaTokenParsers {

  override protected val whiteSpace = """(\s|--.*)+""".r

  def digit = ( "[0-9]+".r )
  def upper_case_letter = ( "[A-Z]+".r )
  def lower_case_letter = ( "[a-z]+".r )
  def space_character = ( " " | "\t" | "\r" | "\n" )

  def letter = ( upper_case_letter | lower_case_letter )

  def letter_or_digit = ( letter | digit )

  def identifier = ( ident ) // defined in JavaTokenParsers

  def logical_name = ( identifier )

  def suffix = ( identifier | "ALL" )

  def selected_name = ( identifier ~ "." ~ (identifier ~ ".").* ~ suffix ^^ {
    case x~"."~y~z =>
      x + "." + (for(yy <- y) yield { yy._1 + "."}).mkString("") + z
  } )

  def logical_name_list = ( logical_name ~ ("," ~ logical_name).* ^^ {
    case x~y => x :: ( for(yy <- y) yield { yy._2 } )
  } )

  def library_clause = ( "LIBRARY" ~> logical_name_list <~ ";" ^^ {
    case x => for(xx <- x) yield { new Library(xx) }
  } )

  def use_clause = ( "USE" ~> selected_name ~ ("," ~ selected_name).* <~ ";" ^^ {
    case x~y => {
      new Use(x) :: ( for(yy <- y) yield { new Use(yy._2) } )
    }
  } )

  def long_name = ( identifier ~ ("." ~ identifier).* ^^ {
    case x~y => x + (for(yy <- y) yield { "." + yy._2 }).mkString("")
  } )

  def design_file = ( design_unit ~ design_unit.* ^^ {
    case x~y => x :: y
  } )

  def design_unit = ( context_clause ~ library_unit ^^ {
    case x~y => new DesignUnit(x, y.entity, y.arch)
  } )

  def context_clause = ( context_item.* )

  def context_item = ( library_clause | use_clause )

  def library_unit = ( entity_decl ~ architecture_decl.? ^^ {
    case x~y => new LibraryUnit(x, y)
  } )

  def function_name = ( long_name )

  def function_call : Parser[CallExpr] = ( function_name ~ function_argument_list ^^ {
    case x~y => new CallExpr(new Ident(x), y)
  } )

  def kind_std_logic = ( "STD_LOGIC" ^^ {_ => new StdLogic() } )


  def entity_decl = (
    "ENTITY" ~> long_name ~ "IS" ~ param_item_list.? ~ port_item_list.? <~ "END" ~ "ENTITY".? ~ long_name.? ~ ";" ^^ {
      case x~_~params~ports => {
        new Entity(x, ports, params)
      }
    }
  )

  def bit_value = ( "'" ~> letter_or_digit <~ "'" ^^ { case x => s"'$x'"} )

  def others_decl = ( "(" ~ "OTHERS" ~ "=>" ~> bit_value <~ ")" ^^ {case x => s"(others=>$x)" } )

  def signal_value = ( value_expression )

  def init_value = ":=" ~> ( prime_expression )

  def step_dir = ( "DOWNTO" | "UPTO" )

  def vector_type = ( "STD_LOGIC_VECTOR" | "SIGNED" | "UNSIGNED" )

  def kind_integer = ( "INTEGER" ^^ { _ => new IntegerKind()} )

  def kind_std_logic_vector = (
      vector_type ~ "(" ~ prime_expression ~ step_dir ~ prime_expression <~ ")" ^^ {
        case name~_~b~step~e => new VectorKind(name, step, b, e)
      }
    | vector_type ^^ {
      case name => new VectorKind(name, "", null, null)
      }
  )

  def user_defined_type_kind = ( identifier ^^ {case x => new UserTypeKind(x) } )

  def kind = ( kind_std_logic_vector | kind_std_logic | kind_integer | user_defined_type_kind )

  def port_item = ( long_name ~ ":" ~ identifier ~ kind ~ init_value.? ^^ {
    case name~_~dir~kind~init => new PortItem(name, dir, kind, init)
  } )

  def param_item = ( long_name ~ ":" ~ kind ~ init_value ^^ {
    case name~_~kind~value => new ParamItem(name, kind, value)
  } )

  def port_item_list = ( "PORT" ~ "(" ~> port_item ~ ( ";" ~ port_item ).* <~ ")" ~ ";" ^^ {
    case x~y => x :: ( for(yy <- y) yield { yy._2 } )
  } )

  def param_item_list = ( "GENERIC" ~ "(" ~> param_item ~ ( ";" ~ param_item ).* <~ ")" ~ ";" ^^ {
    case x~y => x :: ( for(yy <- y) yield { yy._2 } )
  } )

  def declarations : Parser[Node] = (
    attribute_decl | component_decl | signal_decl | type_decl | set_attribute_decl | constant_decl
  )

  def architecture_decl = (
    "ARCHITECTURE" ~> identifier ~ "OF" ~ long_name ~ "IS" ~
    declarations.* ~
    "BEGIN" ~
    (process_statement | instantiation_statement | assign_statement).* <~
    "END" ~ "ARCHITECTURE".? ~ long_name.? ~ ";" ^^ {
      case kind~_~name~_~decls~_~body => {
        new Architecture(kind, name, decls, body)
      }
    }
  )

  def attribute_decl = ( "ATTRIBUTE" ~> identifier ~ ":" ~ identifier <~ ";" ^^{
    case x~_~y => new Attribute(x, y)
  } )

  def component_decl = (
    "COMPONENT" ~> long_name ~ "IS".? ~ param_item_list.? ~ port_item_list.? ~ "END" ~ "COMPONENT" ~ long_name.? <~ ";" ^^ {
      case name~_~params~ports~_~_~name2 => new ComponentDecl(name, ports, params)
    }
  )

  def long_name_list = (
    long_name ~ ("," ~ long_name).* ^^ {
      case x~y => ( x + ( for(yy <- y) yield { "," + yy._2 } ).mkString("") )
    }
  )

  def signal_decl = (
    "SIGNAL" ~> long_name_list ~ ":" ~ kind ~ init_value.? <~ ";" ^^ {
      case name~_~kind~init => new Signal(name, kind, init)
    }
  )

  def constant_decl = (
    "CONSTANT" ~> long_name_list ~ ":" ~ kind ~ init_value <~ ";" ^^ {
      case name~_~kind~init => new ConstantDecl(name, kind, init)
    }
  )

  def variable_decl = (
    "VARIABLE" ~> long_name_list ~ ":" ~ kind ~ init_value.? <~ ";" ^^ {
      case name~_~kind~init => new Variable(name, kind, init)
    }
  )

  def direction = ( "OUT" | "IN" | "INOUT" )
  def filepath = ( stringLiteral )

  def file_decl = (
    "FILE" ~> long_name_list ~ ":" ~ identifier ~ "IS" ~ direction ~ filepath <~ ";" ^^ {
      case x~_~y~_~d~s => new FileDecl(x, y, d, s)
    }
  )

  def symbol_list = (
    identifier ~ ("," ~ identifier).* ^^ {
      case x~y => new Ident(x) :: (for (yy <- y) yield { new Ident(yy._2) })
    }
  )

  def type_decl = (
      "TYPE" ~> identifier ~ "IS" ~ "(" ~ symbol_list ~ ")" <~ ";" ^^ {
        case x~_~_~l~_ => new UserType(x, l)
      }
    | "TYPE" ~> identifier ~ "IS" ~ "ARRAY" ~
      "(" ~ prime_expression ~ step_dir ~ prime_expression ~ ")" ~ "OF" ~ kind <~ ";" ^^ {
        case x~_~_~_~y~z~w~_~_~v => new ArrayType(x, z, y, w, v)
      }
  )

  def call_statement = (
    function_call <~ ";" ^^ {
      case x => new CallStatement(x)
    }
  )

  def assign_statement = (
    extended_value_expression ~ "<=" ~ expression <~ ";" ^^ {
      case lhs~_~rhs => new AssignStatement(lhs, rhs)
    }
  )

  def blocking_assign_statement = (
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

  def process_statement =
    statement_label.? ~ "PROCESS" ~ sensitivity_list.? ~
    process_decl.* ~
    "BEGIN" ~
    statement_in_process.* ~
    "END" ~ "PROCESS" ~ long_name.? <~ ";" ^^ {
      case name~_~sensitivities~variables~_~stmts~_~_~name2 => new ProcessStatement(sensitivities, name, stmts, variables)
    }

  def unary_expression = ("-"|"NOT") ~ expression ^^ { case x~y => new UnaryExpr(x, y) }

  def binary_operation = compare_operation | mul_div_operation | add_sub_operation | logic_operation | concat_operation

  def compare_operation = ( "=" | ">=" | "<=" | ">" | "<" ) ^^ { case x => x } |
                          "/" ~ "=" ^^ { case x~y => x+y }

  def logic_operation = "AND" | "OR" | "XOR"

  def mul_div_operation = "*" | "/"

  def add_sub_operation = "+" | "-"

  def concat_operation = "&"

  def hex_value = ("X"|"x") ~> stringLiteral ^^ { case x => new BasedValue(x, 16) }

  def bit_padding_expression = (
    "(" ~ prime_expression ~ ("downto"|"upto") ~ prime_expression ~ "=>" ~ prime_expression ~ ")" ^^ {
      case _~b~step~e~_~v~_ => new BitPaddingExpr(step, b, e, v)
    }
  )

  def others_expr = "(" ~ "OTHERS" ~ "=>" ~> extended_value_expression <~ ")" ^^ {
    case x => new Others(x)
  }

  def value_expression : Parser[Expr] = (
    "(" ~> expression <~ ")" ^^ {case x => x }
    | hex_value   ^^ { case x => x }
    | bit_value   ^^ { case x => new Constant(x) }
    | others_expr ^^ { case x => x }
    | identifier ~ "'" ~ identifier ^^ { case x~_~y => new Ident(s"$x'$y") }
    | identifier  ^^ { case x => new Ident(x) }
    | decimalNumber ^^ { case x => new Constant(x) }
    | stringLiteral ^^ { case x => new Constant(x) }
  )

  def extended_value_expression : Parser[Expr] = function_call | bit_padding_expression | bit_vector_select | value_expression

  def when_expression : Parser[Expr] = prime_expression ~ "WHEN" ~ prime_expression ~ "ELSE" ~ expression ^^ {
    case thenExpr~_~cond~_~elseExpr => new WhenExpr(cond, thenExpr, elseExpr)
  }

  def bit_vector_select = long_name ~ "(" ~ prime_expression ~ ("downto"|"upto") ~ prime_expression ~ ")" ^^ {
    case n~_~b~step~e~_ => new BitVectorSelect(new Ident(n), step, b, e)
  }

  def binary_expression = extended_value_expression ~ binary_operation ~ expression ^^ {case x~op~y => new BinaryExpr(op, x, y) }

  def prime_expression : Parser[Expr] =
    unary_expression | binary_expression | extended_value_expression

  def expression : Parser[Expr] = when_expression | prime_expression

  def null_statement = "NULL" ~ ";" ^^ { _ => new NullStatement() }

  def trigger = "FOR"
  def unit = "NS" | "PS"

  def wait_statement = (
       "WAIT" ~> trigger ~ prime_expression ~ unit <~ ";" ^^ { case x~y~z => new WaitStatement(x, new TimeUnit(y, z)) }
     | "WAIT" ~ ";" ^^ { case _ => new WaitStatement("", null) }
  )

  def report_statement = "REPORT" ~> stringLiteral <~ ";" ^^ { case x =>  new ReportStatement(x) }

  def statement_in_process : Parser[Node] = (
        if_statement
      | assign_statement
      | blocking_assign_statement
      | call_statement
      | null_statement
      | case_statement
      | wait_statement
      | report_statement
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
    extended_value_expression ~ "=>" ~ expression ~ ("," ~ extended_value_expression ~ "=>" ~ expression).* ^^ {
      case x~_~y~z =>
        new PortMapItem(x, y) :: ( for(zz <- z) yield { new PortMapItem(zz._1._1._2, zz._2) } )
    }

  def portmap_list = "(" ~> portmap_list_item <~ ")" ^^ { case x => x } |
                     "(" ~ ")" ^^ { case _ => List() }

  def genericmap = "GENERIC" ~ "MAP" ~> portmap_list ^^ { case x => x }

  def instantiation_statement =
    long_name ~ ":" ~ long_name ~ genericmap.? ~ "PORT" ~ "MAP" ~ portmap_list <~ ";" ^^ {
      case iname~_~mname~params~_~_~ports => new InstanceStatement(new Ident(iname), new Ident(mname), ports, params)
    }

  def set_attribute_decl =
    "ATTRIBUTE" ~> identifier ~ "OF" ~ long_name ~ ":" ~ "SIGNAL" ~ "IS" ~ value_expression <~ ";" ^^ {
      case x~_~y~_~_~_~z => new SetAttribute(x, y, z)
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
    case Success( result, _ ) => Option(result)
    case _                    => None
  }

}

object VHDLParser{
  def main(args:Array[String]) = {
    val m = new VHDLParser()
    val ret = m.parse("LIBRARY IEEE ;")
    println(ret)
  }
}

