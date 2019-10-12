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
trait Kind extends Node {
}
trait Expr extends Node {
}

case class StdLogic() extends Kind
case class VectorKind(name:String, step:String, b:String, e:String) extends Kind
case class UserTypeKind(name:String) extends Kind

case class Library(s : String) extends Node
case class Use(s : String) extends Node
case class Entity(s:String, ports:Option[List[PortItem]]) extends Node
case class PortItem(name : String, dir : String, kind : Node) extends Node

case class Architecture(kind:String, name:String, decls:List[Node]) extends Node
case class Attribute(name:String, kind:String) extends Node
case class ComponentDecl(name:String, ports:Option[List[PortItem]]) extends Node
case class Signal(name:String, kind:Kind, init:Option[String]) extends Node
case class UserType(name:String, items:List[String]) extends Node

case class AssignStatement(dest:String, src:Expr) extends Node
case class ProcessStatement(sensitivities:Option[List[String]], label:Option[String]) extends Node
case class IfStatement(cond:Expr, thenPart:List[Node], elifPart:List[IfStatement], elsePart:Option[List[Node]]) extends Node
case class CaseStatement(cond:Expr, whenPart:List[CaseWhenClause]) extends Node
case class CaseWhenClause(label:Expr, stmts:List[Node]) extends Node
case class NullStatement() extends Node

case class Constant(value:String) extends Expr
case class Ident(name:String) extends Expr
case class BinaryExpr(op:String, lhs:Expr, rhs:Expr) extends Expr
case class CallExpr(name:String, args:List[String]) extends Expr // TODO args should be List of Exprs


//class VHDLParser extends RegexParsers {
class VHDLParser extends JavaTokenParsers {

  def design_file = design_unit ~ design_unit.*

  //TODO def design_unit = context_clause ~ library_unit
  def design_unit = context_clause

  def context_clause = context_item.*

  def context_item = library_clause | use_clause

  def library_clause = "LIBRARY" ~ logical_name_list <~ ";" ^^ { case x~y => y }

  def use_clause = "USE" ~> selected_name ~ ("," ~ selected_name).* <~ ";" ^^ {
    case x~y => {
      new Use(x) :: ( for(yy <- y) yield { new Use(yy._2) } )
    }
  }

  def logical_name2 = identifier ~ identifier

  def logical_name = identifier

  def logical_name_list = logical_name ~ ("," ~ logical_name).* ^^ {
    case x~y => {
      new Library(x) :: ( for(yy <- y) yield { new Library(yy._2) } )
    }
  }

  //def identifier = basic_identifier ^^ {res => res}
  def identifier = ident

  def basic_identifier = letter ~ ( "_" | letter_or_digit ).* ^^ {
    case x~y => {
      (x :: (for(yy <- y) yield { yy })).mkString("")
    }
  }

  def letter_or_digit = letter | digit

  def letter = upper_case_letter | lower_case_letter

  def upper_case_letter = "[A-Z]+".r
  def lower_case_letter = "[a-z]+".r
  def space_character = " " | "\t" | "\r" | "\n"
  def digit = "[0-9]+".r

  def selected_name = identifier ~ "." ~ (identifier ~ ".").* ~ suffix ^^ {
    case x~"."~y~z =>
      x + "." + (for(yy <- y) yield { yy._1 + "."}).mkString("") + z
  }
  def suffix = identifier | "ALL"

  def long_name = identifier ~ ("." ~ identifier).* ^^ {
    case x~y =>
      x + (for(yy <- y) yield { "." + yy._2 }).mkString("")
  }

  def entity_decl =
    "ENTITY" ~> long_name ~ "IS" ~ port_item_list.? <~ "END" ~ "ENTITY".? ~ long_name.? ~ ";" ^^ {
      case x~_~ports => {
        new Entity(x, ports)
      }
    }

  def kind_std_logic = "STD_LOGIC" ^^ {_ => new StdLogic() }

  def simple_expression : Parser[String] = (decimalNumber | identifier) ~ (("+"|"-") ~ simple_expression).* ^^ {
    case x~y => x + ( for(yy <- y) yield { yy._1 + yy._2 } ).mkString("")
  }

  def index_value = simple_expression | identifier | decimalNumber

  def bit_value = "'" ~> letter_or_digit <~ "'" ^^ { case x => s"'$x'"}

  def others_decl = "(" ~ "OTHERS" ~ "=>" ~> bit_value <~ ")" ^^ {case x => s"(others=>$x)" }

  def signal_value = bit_value | others_decl | identifier

  def init_value = ":=" ~> signal_value ^^ { case x => x }

  def step_dir = "DOWNTO" | "UPTO"

  def vector_type = "STD_LOGIC_VECTOR" | "SIGNED" | "UNSIGNED"

  def kind_std_logic_vector =
    vector_type ~ "(" ~ index_value ~ step_dir ~ index_value <~ ")" ^^ {
      case name~_~b~step~e => new VectorKind(name, step, b, e)
    }

  def user_defined_type_kind = identifier ^^ {case x => new UserTypeKind(x) }

  def kind = kind_std_logic_vector | kind_std_logic | user_defined_type_kind

  def port_item = long_name ~ ":" ~ identifier ~ kind ^^ {
    case name~_~dir~kind => new PortItem(name, dir, kind)
  }

  def port_item_list = "PORT" ~ "(" ~> port_item ~ ( ";" ~ port_item ).* <~ ")" ~ ";" ^^ {
    case x~y => x :: ( for(yy <- y) yield { yy._2 } )
  }

  def delarations : Parser[Node] = attribute_decl | component_decl | signal_decl | type_decl

  def architecture_decl =
    "ARCHITECTURE" ~> identifier ~ "OF" ~ long_name ~ "IS" ~
    delarations.* ~
    "BEGIN" <~
    "END" ~ "ARCHITECTURE".? ~ long_name.? ~ ";" ^^ {
      case kind~_~name~_~decls~_ => {
        new Architecture(kind, name, decls)
      }
    }

  def attribute_decl = "ATTRIBUTE" ~> identifier ~ ":" ~ identifier <~ ";" ^^{
    case x~_~y => new Attribute(x, y)
  }

  def component_decl =
    "COMPONENT" ~> long_name ~ port_item_list.? ~ "END" ~ "COMPONENT" ~ long_name.? <~ ";" ^^{
      case name~ports~_~_~name2 => new ComponentDecl(name, ports)
  }

  def signal_decl =
    "SIGNAL" ~> long_name ~ ":" ~ kind ~ init_value.? <~ ";" ^^ {
      case name~_~kind~init => new Signal(name, kind, init)
    }

  def symbol_list = identifier ~ ("," ~ identifier).* ^^ {
    case x~y => x :: (for (yy <- y) yield { yy._2 })
  }

  def type_decl = "TYPE" ~> identifier ~ "IS" ~ "(" ~ symbol_list ~ ")" <~ ";" ^^ {
    case x~_~_~l~_ => new UserType(x, l)
  }

  def assign_statement = identifier ~ "<=" ~ expression <~ ";" ^^ {
    case lhs~_~rhs => new AssignStatement(lhs, rhs)
  }

  def sensitivity_list = "(" ~> symbol_list <~ ")" ^^ { case x => x } |
                         "(" ~ ")" ^^ { case _ => List() }

  def statement_label = long_name <~ ":" ^^ { case x => x }

  def process_statement =
    statement_label.? ~ "PROCESS" ~ sensitivity_list.? ~
    "BEGIN" ~
    "END" ~ "PROCESS" ~ long_name.? <~ ";" ^^ {
      case name~_~sensitivities~_~_~_~name2 => new ProcessStatement(sensitivities, name)
    }

  def binary_operation = compare_operation | logic_operation

  def compare_operation = "=" | "/="

  def logic_operation = "AND" | "OR"

  def value_expression = 
    bit_value   ^^ { case x => new Constant(x) } |
    others_decl ^^ { case x => new Constant(x) } |
    identifier ~ "'" ~ identifier ^^ { case x~_~y => new Ident(s"$x'$y") } |
    identifier  ^^ { case x => new Ident(x) }

  def expression : Parser[Expr] =
    "(" ~> expression <~ ")" ^^ {case x => x } | 
    identifier ~ sensitivity_list ^^ {case x~y => new CallExpr(x, y) } |
    value_expression ~ binary_operation ~ expression ^^ {case x~op~y => new BinaryExpr(op, x, y) } |
    value_expression

  def null_statement = "NULL" ~ ";" ^^ { _ => new NullStatement() }

  def statement_in_process : Parser[Node] = if_statement | assign_statement | null_statement

  def else_clause_in_if_statement : Parser[List[Node]] = "ELSE" ~> statement_in_process.* ^^ { case x => x }

  def elsif_clause_in_if_statement : Parser[IfStatement] =
    "ELSIF" ~> expression ~ "THEN" ~ statement_in_process.* ^^ {
      case x~_~y => new IfStatement(x, y, List(), None)
    }

  def if_statement =
    "IF" ~> expression ~ "THEN" ~
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

  def parse( input: String ) = parseAll(design_unit, input) match {
    case Success( result, _ ) => Option(result)
    case _                    => None
  }

  def parseSelectedName( input: String ) = parseAll(selected_name, input) match {
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

