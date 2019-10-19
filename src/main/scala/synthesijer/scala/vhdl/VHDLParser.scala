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
case class CommandStatement(str:String) extends Node

case class NodeList(nodes:List[Node]) extends Positional
case class LiteralNodeList(nodes:List[LiteralNode]) extends Positional
case class ListNodeList(nodes:List[List[Node]]) extends Positional

class VHDLParser extends JavaTokenParsers with PackratParsers{

  override protected val whiteSpace = """(\s|--.*)+""".r

  // keywords
  lazy val ALL              = """(?i)\b\Qall\E\b""".r
  lazy val AND              = """(?i)\b\Qand\E\b""".r
  lazy val ARCHITECTURE     = """(?i)\b\Qarchitecture\E\b""".r
  lazy val ARRAY            = """(?i)\b\Qarray\E\b""".r
  lazy val ATTRIBUTE        = """(?i)\b\Qattribute\E\b""".r
  lazy val BEGIN            = """(?i)\b\Qbegin\E\b""".r
  lazy val BLOCK            = """(?i)\b\Qblock\E\b""".r
  lazy val CASE             = """(?i)\b\Qcase\E\b""".r
  lazy val COMPONENT        = """(?i)\b\Qcomponent\E\b""".r
  lazy val CONSTANT         = """(?i)\b\Qconstant\E\b""".r
  lazy val DOWNTO           = """(?i)\b\Qdownto\E\b""".r
  lazy val ELSIF            = """(?i)\b\Qelsif\E\b""".r
  lazy val ELSE             = """(?i)\b\Qelse\E\b""".r
  lazy val END              = """(?i)\b\Qend\E\b""".r
  lazy val ENTITY           = """(?i)\b\Qentity\E\b""".r
  lazy val FILE             = """(?i)\b\Qfile\E\b""".r
  lazy val FOR              = """(?i)\b\Qfor\E\b""".r
  lazy val FUNCTION         = """(?i)\b\Qfunction\E\b""".r
  lazy val GENERIC          = """(?i)\b\Qgeneric\E\b""".r
  lazy val GENERATE         = """(?i)\b\Qgenerate\E\b""".r
  lazy val IF               = """(?i)\b\Qif\E\b""".r
  lazy val IN               = """(?i)\b\Qin\E\b""".r
  lazy val INOUT            = """(?i)\b\Qinout\E\b""".r
  lazy val INTEGER          = """(?i)\b\Qinteger\E\b""".r
  lazy val IS               = """(?i)\b\Qis\E\b""".r
  lazy val LIBRARY          = """(?i)\b\Qlibrary\E\b""".r
  lazy val LOOP             = """(?i)\b\Qloop\E\b""".r
  lazy val MAP              = """(?i)\b\Qmap\E\b""".r
  lazy val MOD              = """(?i)\b\Qmod\E\b""".r
  lazy val NOT              = """(?i)\b\Qnot\E\b""".r
  lazy val NS               = """(?i)\b\Qns\E\b""".r
  lazy val NULL             = """(?i)\b\Qnull\E\b""".r
  lazy val OF               = """(?i)\b\Qof\E\b""".r
  lazy val OR               = """(?i)\b\Qor\E\b""".r
  lazy val OTHERS           = """(?i)\b\Qothers\E\b""".r
  lazy val OUT              = """(?i)\b\Qout\E\b""".r
  lazy val PACKAGE          = """(?i)\b\Qpackage\E\b""".r
  lazy val PORT             = """(?i)\b\Qport\E\b""".r
  lazy val PROCESS          = """(?i)\b\Qprocess\E\b""".r
  lazy val PS               = """(?i)\b\Qps\E\b""".r
  lazy val REPORT           = """(?i)\b\Qreport\E\b""".r
  lazy val RETURN           = """(?i)\b\Qreturn\E\b""".r
  lazy val SHARED           = """(?i)\b\Qshared\E\b""".r
  lazy val SIGNAL           = """(?i)\b\Qsignal\E\b""".r
  lazy val STD_LOGIC        = """(?i)\b\Qstd_logic\E\b""".r
  lazy val STD_LOGIC_VECTOR = """(?i)\b\Qstd_logic_vector\E\b""".r
  lazy val SIGNED           = """(?i)\b\Qsigned\E\b""".r
  lazy val STRING           = """(?i)\b\Qstring\E\b""".r
  lazy val THEN             = """(?i)\b\Qthen\E\b""".r
  lazy val TYPE             = """(?i)\b\Qtype\E\b""".r
  lazy val TO               = """(?i)\b\Qto\E\b""".r
  lazy val UNSIGNED         = """(?i)\b\Qunsigned\E\b""".r
  lazy val USE              = """(?i)\b\Quse\E\b""".r
  lazy val VARIABLE         = """(?i)\b\Qvariable\E\b""".r
  lazy val WAIT             = """(?i)\b\Qwait\E\b""".r
  lazy val WHEN             = """(?i)\b\Qwhen\E\b""".r
  lazy val XOR              = """(?i)\b\Qxor\E\b""".r

  def digit = ( "[0-9]+".r )
  def upper_case_letter = ( "[A-Z]+".r )
  def lower_case_letter = ( "[a-z]+".r )
  def space_character = ( " " | "\t" | "\r" | "\n" )

  def letter = ( upper_case_letter | lower_case_letter )

  def letter_or_digit = ( letter | digit )

  def identifier : PackratParser[LiteralNode] = positioned( ident ^^ { case x => new LiteralNode(x) } ) // defined in JavaTokenParsers

  def logical_name = positioned ( identifier )

  def suffix = positioned ( ALL ^^ {_ => new LiteralNode("all") } | identifier )

  def selected_name = positioned ( identifier ~ "." ~ (identifier ~ ".").* ~ suffix ^^ {
    case x~"."~y~z =>
      new LiteralNode(x.str + "." + (for(yy <- y) yield { yy._1.str + "."}).mkString("") + z.str)
  } )

  def logical_name_list = positioned (
    logical_name ~ ("," ~ logical_name).* ^^ {
      case x~y => new LiteralNodeList(x :: ( for(yy <- y) yield { yy._2 } ))
    }
  )




  def library_clause = positioned ( LIBRARY ~> logical_name_list <~ ";" ^^ {
    case x => new NodeList(for(xx <- x.nodes) yield { new Library(xx.str) })
  } )

  def use_clause = positioned (
    USE ~> selected_name ~ ("," ~ selected_name).* <~ ";" ^^ {
      case x~y => {
        new NodeList (new Use(x.str) :: ( for(yy <- y) yield { new Use(yy._2.str) } ) )
      }
    }
  )

  def long_name = positioned ( identifier ~ ("." ~ identifier).* ^^ {
    case x~y => new LiteralNode(x.str + (for(yy <- y) yield { "." + yy._2.str }).mkString(""))
  } )

  def design_file : PackratParser[NodeList] = positioned ( design_unit ~ design_unit.* ^^ {
    case x~y => new NodeList(x :: y)
  } )

  def design_unit : PackratParser[Node] = positioned(
     context_clause ~ library_unit ^^ { case x~y => new DesignUnit(x.nodes, y.entity, y.arch) }
   | context_clause ~ package_decl ^^ { case x~y => new PackageUnit(x.nodes, y) }

  )

  def context_clause = positioned ( context_item.* ^^ { x => new ListNodeList(for(xx <- x) yield { xx.nodes }) } )

  def context_item = positioned ( library_clause | use_clause )

  def library_unit : PackratParser[LibraryUnit] = positioned (
    entity_decl ~ architecture_decl.? ^^ {
      case x~y => new LibraryUnit(x, y)
    }
  )

  def package_decl = positioned(
    PACKAGE ~> long_name ~ IS ~ declarations.* ~ END ~ PACKAGE ~ long_name.? <~ ";" ^^ {
      case name~_~decls~_~_~name2 => new PackageDecl(name.str, decls)
    }
  )

  def function_name = positioned( long_name )

  def function_call : PackratParser[CallExpr] = positioned ( function_name ~ function_argument_list ^^ {
    case x~y => new CallExpr(new Ident(x.str), y)
  } )

  def kind_std_logic = ( STD_LOGIC ^^ {_ => new StdLogic() } )


  def entity_decl = positioned(
    ENTITY ~> long_name ~ IS ~ param_item_list.? ~ port_item_list.? <~ END ~ ENTITY.? ~ long_name.? ~ ";" ^^ {
      case x~_~params~ports => {
        new Entity(x.str, ports, params)
      }
    }
  )

  def bit_value = ( "'" ~> letter_or_digit <~ "'" ^^ { case x => s"'$x'"} )

  def others_decl = ( "(" ~ OTHERS ~ "=>" ~> bit_value <~ ")" ^^ {case x => s"(others=>$x)" } )

  def signal_value = ( value_expression )

  def init_value = ":=" ~> ( prime_expression )

  def step_dir = ( DOWNTO | TO )

  def vector_type = ( STD_LOGIC_VECTOR | SIGNED | UNSIGNED )

  def kind_integer = ( INTEGER ^^ { _ => new IntegerKind()} )

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
          case Some(d) => new PortItem(name.str, d.str, kind, init)
          case None => new PortItem(name.str, None, kind, init)
        }
      }
    }
  |
    long_name_list ~ ":" ~ kind ~ init_value.? ^^ {
      case name~_~kind~init => new PortItem(name.str, None, kind, init)
    }
  )

  def param_item = positioned (
    long_name ~ ":" ~ direction.? ~ kind ~ init_value ^^ {
      case name~_~_~kind~value => new ParamItem(name.str, kind, value)
    }
  )

  def port_item_list_body = "(" ~> port_item ~ ( ";" ~ port_item ).* <~ ")" ^^ {
    case x~y => x :: ( for(yy <- y) yield { yy._2 } )
  }

  def port_item_list = ( PORT ~> port_item_list_body <~ ";" )

  def param_item_list = ( GENERIC ~ "(" ~> param_item ~ ( ";" ~ param_item ).* <~ ")" ~ ";" ^^ {
    case x~y => x :: ( for(yy <- y) yield { yy._2 } )
  } )

  def declarations : PackratParser[Node] = (
    attribute_decl | component_decl | signal_decl | type_decl | set_attribute_decl | constant_decl | function_decl | shared_variable_decl
  )

  def architecture_statement : PackratParser[Node] = positioned (
    process_statement | instantiation_statement | assign_statement | generate_statement | block_decl
  )

  def architecture_decl : PackratParser[Architecture] = positioned (
    ARCHITECTURE ~> identifier ~ OF ~ long_name ~ IS ~
    declarations.* ~
    BEGIN ~
    architecture_statement.* <~
    END ~ ARCHITECTURE.? ~ long_name.? ~ ";" ^^ {
      case kind~_~name~_~decls~_~body => {
        new Architecture(kind.str, name.str, decls, body)
      }
    }
  )

  def block_decl = positioned(
    statement_label.? ~ BLOCK ~ declarations.* ~ BEGIN ~ architecture_statement.* ~ END ~ BLOCK ~ long_name.? <~ ";" ^^ {
      case label~_~decls~_~body~_~_~name2 =>
        {
          label match{
            case Some(l) => new Block(Some(l.str), decls, body)
            case None => new Block(None, decls, body)
          }
        }
    }
  )

  def attribute_decl = positioned ( ATTRIBUTE ~> identifier ~ ":" ~ identifier <~ ";" ^^{
    case x~_~y => new Attribute(x.str, y.str)
  } )

  def component_decl = positioned (
    COMPONENT ~> long_name ~ IS.? ~ param_item_list.? ~ port_item_list.? ~ END ~ COMPONENT ~ long_name.? <~ ";" ^^ {
      case name~_~params~ports~_~_~name2 => new ComponentDecl(name.str, ports, params)
    }
  )

  def long_name_list = positioned (
    long_name ~ ("," ~ long_name).* ^^ {
      case x~y => new LiteralNode ( x.str + ( for(yy <- y) yield { "," + yy._2.str } ).mkString("") )
    }
  )

  def signal_decl = positioned (
    SIGNAL ~> long_name_list ~ ":" ~ kind ~ init_value.? <~ ";" ^^ {
      case name~_~kind~init => new Signal(name.str, kind, init)
    }
  )

  def shared_variable_decl = positioned(
    SHARED ~ VARIABLE ~> long_name_list ~ ":" ~ kind ~ init_value.? <~ ";" ^^ {
      case name~_~kind~init => new SharedVariable(name.str, kind, init)
    }
  )

  def constant_decl = positioned (
    CONSTANT ~> long_name_list ~ ":" ~ kind ~ init_value <~ ";" ^^ {
      case name~_~kind~init => new ConstantDecl(name.str, kind, init)
    }
  )

  def variable_decl = positioned (
    VARIABLE ~> long_name_list ~ ":" ~ kind ~ init_value.? <~ ";" ^^ {
      case name~_~kind~init => new Variable(name.str, kind, init)
    }
  )

  def direction = ( INOUT | OUT | IN )
  def filepath = ( stringLiteral )

  def file_decl = positioned (
    FILE ~> long_name_list ~ ":" ~ identifier ~ IS ~ direction ~ filepath <~ ";" ^^ {
      case x~_~y~_~d~s => new FileDecl(x.str, y.str, d, s)
    }
  )

  def symbol_list = (
    identifier ~ ("," ~ identifier).* ^^ {
      case x~y => new Ident(x.str) :: (for (yy <- y) yield { new Ident(yy._2.str) })
    }
  )

  def type_decl = positioned(
      TYPE ~> identifier ~ IS ~ "(" ~ symbol_list ~ ")" <~ ";" ^^ {
        case x~_~_~l~_ => new UserType(x.str, l)
      }
    | TYPE ~> identifier ~ IS ~ ARRAY ~
      "(" ~ prime_expression ~ step_dir ~ prime_expression ~ ")" ~ OF ~ kind <~ ";" ^^ {
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
    statement_label.? ~ PROCESS ~ sensitivity_list.? ~
    process_decl.* ~
    BEGIN ~
    statement_in_process.* ~
    END ~ PROCESS ~ long_name.? <~ ";" ^^ {
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
    identifier ~ ":" ~ FOR ~ identifier ~ IN ~ prime_expression ~ step_dir ~ prime_expression ~ GENERATE ~
    BEGIN.? ~ architecture_statement.* ~ END ~ GENERATE ~ identifier.? <~ ";" ^^ {
      case name~_~_~i~_~b~s~e~_~_~lst~_~_~name2 => new GenerateFor(new Ident(name.str), new Ident(i.str), s, b, e, lst)
    }
  )

  def generate_if_statement = positioned (
    identifier ~ ":" ~ IF ~ prime_expression ~ GENERATE ~
    BEGIN.? ~ architecture_statement.* ~ END ~ GENERATE ~ identifier.? <~ ";" ^^ {
      case name~_~_~expr~_~_~lst~_~_~name2 => new GenerateIf(new Ident(name.str), expr, lst)
    }
  |
    identifier ~ ":" ~ IF ~ prime_expression ~ GENERATE ~
    BEGIN.? ~ architecture_statement ^^ {
      case name~_~_~expr~_~_~stmt => new GenerateIf(new Ident(name.str), expr, List(stmt))
    }
  )

  def unary_expression = ("-"|NOT) ~ expression ^^ { case x~y => new UnaryExpr(x, y) }

  def compare_operation = ( "=" | ">=" | "<=" | ">" | "<" ) ^^ { case x => x } |
                          "/" ~ "=" ^^ { case x~y => x+y }

  def logic_operation = AND | OR | XOR | "|"

  def mul_div_operation = "*" | "/" | MOD

  def add_sub_operation = "+" | "-"

  def concat_operation = "&"

  def hex_value = ("X"|"x") ~> stringLiteral ^^ { case x => new BasedValue(x, 16) }
  def bin_value = ("B"|"b") ~> stringLiteral ^^ { case x => new BasedValue(x, 2) }

  def bit_padding_expression = positioned (
    "(" ~ prime_expression ~ step_dir ~ prime_expression ~ "=>" ~ prime_expression ~ ")" ^^ {
      case _~b~step~e~_~v~_ => new BitPaddingExpr(step, b, e, v)
    }
  )

  def others_expr = "(" ~ OTHERS ~ "=>" ~> extended_value_expression <~ ")" ^^ {
    case x => new Others(x)
  }

  def string_value = STRING ~ "'" ~ "(" ~ stringLiteral ~ ")" ^^ {
    case a~b~c~d~e => List(a,b,c,d,e).mkString("")
  }

  lazy val value_expression : PackratParser[Expr] = positioned(
    "(" ~> expression <~ ")" ^^ {case x => x }
    | hex_value   ^^ { case x => x }
    | bin_value   ^^ { case x => x }
    | bit_value   ^^ { case x => new Constant(x) }
    | string_value ^^ { case x => new Constant(x) }
    | others_expr ^^ { case x => x }
    | identifier ~ "'" ~ identifier ^^ { case x~_~y => new Ident(s"${x.str}'${y.str}") }
    | long_name   ^^ { case x => new Ident(x.str) }
    | identifier  ^^ { case x => new Ident(x.str) }
    | decimalNumber ^^ { case x => new Constant(x) }
    | stringLiteral ^^ { case x => new Constant(x) }
  )

  lazy val extended_value_expression : PackratParser[Expr] = positioned (
    unary_expression | bit_vector_select | function_call | bit_padding_expression | value_expression
  )

  def when_expression : PackratParser[Expr] = positioned ( prime_expression ~ WHEN ~ prime_expression ~ ELSE ~ expression ^^ {
    case thenExpr~_~cond~_~elseExpr => new WhenExpr(cond, thenExpr, elseExpr)
  }
  )

  def bit_vector_select_arg_range : PackratParser[BitVectorSelect] = positioned (
     "(" ~> prime_expression ~ step_dir ~ prime_expression <~ ")" ^^ { case b~s~e => new BitVectorSelect(null, s, b, e) }
  )

  def bit_vector_select_arg : PackratParser[BitVectorSelect] = positioned (
        bit_vector_select_arg_range
      | "(" ~> prime_expression <~ ")" ^^ { case b => new BitVectorSelect(null, "at", b, b) }
  )

  def bit_vector_select : PackratParser[BitVectorSelect] = positioned (
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

  def binary_operation = concat_operation | compare_operation | mul_div_operation | add_sub_operation | logic_operation

  lazy val binary_expression : PackratParser[Expr] = positioned (
    extended_value_expression ~ binary_operation ~ expression ^^ {case x~op~y => new BinaryExpr(op, x, y) }
  )

  lazy val prime_expression : PackratParser[Expr] = positioned ( binary_expression | extended_value_expression )

  lazy val expression : PackratParser[Expr] = positioned ( when_expression | prime_expression )

  def null_statement = NULL ~ ";" ^^ { _ => new NullStatement() }

  def trigger = FOR
  def unit = NS | PS

  def wait_statement = (
       WAIT ~> trigger ~ prime_expression ~ unit <~ ";" ^^ { case x~y~z => new WaitStatement(x, new TimeUnit(y, z)) }
     | WAIT ~ ";" ^^ { case _ => new WaitStatement("", null) }
  )

  def report_statement = REPORT ~> stringLiteral <~ ";" ^^ { case x =>  new ReportStatement(x) }

  lazy val statement_in_process : PackratParser[Node] = positioned (
        if_statement
      | assign_statement
      | blocking_assign_statement
      | call_statement
      | null_statement
      | case_statement
      | wait_statement
      | report_statement
      | generate_for_loop
      | command_statement
  )

  lazy val command_statement = positioned(
    long_name <~ ";" ^^ { case x => new CommandStatement(x.str) }
  )

  def else_clause_in_if_statement : PackratParser[List[Node]] = ELSE ~> statement_in_process.* ^^ { case x => x }

  def elsif_clause_in_if_statement : PackratParser[IfStatement] =
    ELSIF ~> prime_expression ~ THEN ~ statement_in_process.* ^^ {
      case x~_~y => new IfStatement(x, y, List(), None)
    }

  def if_statement =
    IF ~> prime_expression ~ THEN ~
    statement_in_process.* ~
    elsif_clause_in_if_statement.* ~
    else_clause_in_if_statement.? ~
    END <~ IF ~ ";" ^^ {
      case cond~_~thenPart~elifPart~elsePart~_ => new IfStatement(cond, thenPart, elifPart, elsePart)
    }

  def case_when_clause = WHEN ~> expression ~ "=>" ~ statement_in_process.* ^^ {
    case label~_~stmts => new CaseWhenClause(label, stmts)
  }

  def case_statement =
    CASE ~> expression ~ IS ~ case_when_clause.* <~ END ~ CASE ~ ";" ^^ {
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

  def genericmap = GENERIC ~ MAP ~> portmap_list ^^ { case x => x }

  def simple_portmap_list = "(" ~> prime_expression ~ ("," ~> prime_expression).* <~ ")" ^^ {
    case x~y => new PortMapItem(new NoExpr(), x) :: ( for(yy <- y) yield { new PortMapItem(new NoExpr(), yy) } )
  }

  def instantiation_statement = positioned (
    long_name ~ ":" ~ long_name ~ genericmap.? ~ PORT ~ MAP ~ portmap_list <~ ";" ^^ {
      case iname~_~mname~params~_~_~ports => new InstanceStatement(new Ident(iname.str), new Ident(mname.str), ports, params)
    }
  )

  def set_attribute_decl = positioned (
    ATTRIBUTE ~> identifier ~ OF ~ long_name_list ~ ":" ~ (SIGNAL|VARIABLE) ~ IS ~ value_expression <~ ";" ^^ {
      case x~_~y~_~_~_~z => new SetAttribute(x.str, y.str, z)
    }
  )

  def range = (
      prime_expression ~ step_dir ~ prime_expression ^^ { case x~y~z => new RegionRange(y, x, z) }
    | prime_expression ^^ { x => new SymbolRange(x) }
  )

  def generate_for_loop : PackratParser[ForLoop] = positioned(
    FOR ~> identifier ~ IN ~ range ~ LOOP ~ statement_in_process.* <~ END ~ LOOP ~ ";" ^^ {
      case x~_~y~_~z => new ForLoop(None, new Ident(x.str), y, z)
    }
  )

  def return_statement = RETURN ~> prime_expression <~ ";" ^^ { case x => new ReturnStatement(x) }

  def function_body_statement = statement_in_process | generate_statement | return_statement

  def function_decl = 
    FUNCTION ~ long_name ~ port_item_list_body ~ RETURN ~ kind ~ IS ~ variable_decl.* ~
    BEGIN ~ function_body_statement.* ~ END ~ FUNCTION ~ ";" ^^{
      case _~x~y~_~k~_~v~_~s~_~_~_ =>
        new FunctionDecl(x.str, y, k, v, s)
    }

  def parse( input: String ) = parseAll(design_file, input) match {
    case Success(result, _ ) => Option(result.nodes)
    case NoSuccess(msg, next) => println(msg); println("lines = " + next.pos.line + ", column = " + next.pos.column); None
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

