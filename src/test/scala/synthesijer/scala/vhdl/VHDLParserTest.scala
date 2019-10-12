package synthesijer.scala.vhdl

import org.scalatest._

class VHDLParserTest extends FlatSpec with Matchers {

  "identifiers" should "be parsed" in {
    val obj = new VHDLParser()
    obj.parseAll(obj.identifier, "ident").get should be ("ident")
    obj.parseAll(obj.identifier, "iDENT").get should be ("iDENT")
    obj.parseAll(obj.identifier, "ident0123").get should be ("ident0123")
    obj.parseAll(obj.identifier, "ident ").get should be ("ident")
  }

  "long names" should "be parsed" in {
    val obj = new VHDLParser()
    obj.parseAll(obj.long_name, "a").get should be ("a")
    obj.parseAll(obj.long_name, "Test000").get should be ("Test000")
    obj.parseAll(obj.long_name, "a.bb.cCcc").get should be ("a.bb.cCcc")
    obj.parseAll(obj.long_name, "ieee.std_logic_1164").get should be ("ieee.std_logic_1164")
  }

  "selected names" should "be parsed" in {
    val obj = new VHDLParser()
    obj.parseAll(obj.selected_name, "a.bb.cCcc.all").get should be ("a.bb.cCcc.all")
    obj.parseAll(obj.selected_name, "ieee.std_logic_1164.all").get should be ("ieee.std_logic_1164.all")
  }

  "use-statement" should "be parsed" in {
    val obj = new VHDLParser()
    obj.parseAll(obj.use_clause, "use ieee.std_logic_1164.all;").get should be (List(new Use("ieee.std_logic_1164.all")))
  }

  "a library (with lower-case)" should "be parsed" in {
    val obj = new VHDLParser()
    obj.parseAll(obj.library_clause, "library ieee;").get should be (List(new Library("ieee")))
  }

  "a library (with capital)" should "be parsed" in {
    val obj = new VHDLParser()
    obj.parseAll(obj.library_clause, "LIBRARY ieee;").get should be (List(new Library("ieee")))
  }

  "a library (with mixed-case)" should "be parsed" in {
    val obj = new VHDLParser()
    obj.parseAll(obj.library_clause, "lIBRary ieee;").get should be (List(new Library("ieee")))
  }

  "2 libraries" should "be parsed" in {
    val obj = new VHDLParser()
    obj.parseAll(obj.library_clause, "LIBRARY ieee, Work;").get should be (List(new Library("ieee"), new Library("Work")))
  }

  "3 libraries" should "be parsed" in {
    val obj = new VHDLParser()
    obj.parseAll(obj.library_clause, "LIBRARY ieee, work, test0123;").get should be (List(new Library("ieee"), new Library("work"), new Library("test0123")))
  }

  "simple entity decl" should "be parsed" in {
    val obj = new VHDLParser()
    obj.parse(obj.entity_decl, "entity Test000 is end;")
      .get should be (
        new Entity("Test000", None)
      )
  }

  "entity decl" should "be parsed" in {
    val obj = new VHDLParser()
    obj.parse(obj.entity_decl, "entity Test000 is end Test000;")
      .get should be (
        new Entity("Test000", None)
      )
  }

  "full entity decl" should "be parsed" in {
    val obj = new VHDLParser()
    obj.parse(obj.entity_decl, "entity Test000 is end entity Test000;")
      .get should be (
        new Entity("Test000", None)
      )
  }

  "index_value" should "parses 3 as String(3)" in {
    val obj = new VHDLParser()
    obj.parse(obj.index_value, "3").get should be ("3")
  }

  "index_value" should "parses WIDTH as String(WIDTH)" in {
    val obj = new VHDLParser()
    obj.parse(obj.index_value, "WIDTH").get should be ("WIDTH")
  }

  "index_value" should "parses 3+5 as String(3+5)" in {
    val obj = new VHDLParser()
    obj.parse(obj.index_value, "3+5").get should be ("3+5")
  }

  "index_value" should "parses 32-1 as String(32-1)" in {
    val obj = new VHDLParser()
    obj.parse(obj.index_value, "32-1").get should be ("32-1")
  }

  "kind (std_logic)" should "be parsed" in {
    val obj = new VHDLParser()
    obj.parse(obj.kind, "std_logic")
      .get should be (new StdLogic())
  }

  "kind (std_logic_vector)" should "be parsed" in {
    val obj = new VHDLParser()
    obj.parse(obj.kind, "std_logic_vector(3 downto 0)")
      .get should be (new VectorKind("std_logic_vector", "downto", "3", "0"))
  }

  "kind (signed)" should "be parsed" in {
    val obj = new VHDLParser()
    obj.parse(obj.kind, "signed(3 downto 0)")
      .get should be (new VectorKind("signed", "downto", "3", "0"))
  }

  "kind (unsigned)" should "be parsed" in {
    val obj = new VHDLParser()
    obj.parse(obj.kind, "unsigned(3 downto 0)")
      .get should be (new VectorKind("unsigned", "downto", "3", "0"))
  }

  "port item" should "be parsed" in {
    val obj = new VHDLParser()
    obj.parse(obj.port_item, "clk : in std_logic")
      .get should be (new PortItem("clk", "in", new StdLogic()))
    obj.parse(obj.port_item, "q : out std_logic_vector(3 downto 0)")
      .get should be (new PortItem("q", "out", new VectorKind("std_logic_vector", "downto", "3", "0")))
  }

  "port item list" should "be parsed" in {
    val obj = new VHDLParser()
    obj.parse(obj.port_item_list, """
port (
clk   : in std_logic;
reset : in std_logic;
sw    : in std_logic_vector(3 downto 0);
q     : out std_logic
);
"""
    )
      .get should be (
        List(
          new PortItem("clk",   "in",  new StdLogic()),
          new PortItem("reset", "in",  new StdLogic()),
          new PortItem("sw",    "in",  new VectorKind("std_logic_vector", "downto", "3", "0")),
          new PortItem("q",     "out", new StdLogic())
        )
      )
  }

  "entity" should "be parsed" in {
    val obj = new VHDLParser()
    obj.parse(obj.entity_decl, """
entity Test000 is
  port (
    clk : in std_logic;
    reset : in std_logic;
    ic_in : in signed(32-1 downto 0);
    ic_we : in std_logic;
    ic_out : out signed(32-1 downto 0);
    lc_in : in signed(64-1 downto 0);
    lc_we : in std_logic;
    lc_out : out signed(64-1 downto 0);
    x_in : in signed(32-1 downto 0);
    x_we : in std_logic;
    x_out : out signed(32-1 downto 0);
    y_in : in signed(64-1 downto 0);
    y_we : in std_logic;
    y_out : out signed(64-1 downto 0);
    test_ia : in signed(32-1 downto 0);
    test_ib : in signed(32-1 downto 0);
    test_la : in signed(64-1 downto 0);
    test_lb : in signed(64-1 downto 0);
    test_busy : out std_logic;
    test_req : in std_logic
  );
end Test000;
"""
    )
      .get should be (
        new Entity("Test000",
          Some(List(
            new PortItem("clk",    "in",  new StdLogic()),
            new PortItem("reset",  "in",  new StdLogic()),
            new PortItem("ic_in",  "in",  new VectorKind("signed", "downto", "32-1", "0")),
            new PortItem("ic_we",  "in",  new StdLogic()),
            new PortItem("ic_out", "out", new VectorKind("signed", "downto", "32-1", "0")),
            new PortItem("lc_in",  "in",  new VectorKind("signed", "downto", "64-1", "0")),
            new PortItem("lc_we",  "in",  new StdLogic()),
            new PortItem("lc_out", "out", new VectorKind("signed", "downto", "64-1", "0")),
            new PortItem("x_in",  "in",  new VectorKind("signed", "downto", "32-1", "0")),
            new PortItem("x_we",  "in",  new StdLogic()),
            new PortItem("x_out", "out", new VectorKind("signed", "downto", "32-1", "0")),
            new PortItem("y_in",  "in",  new VectorKind("signed", "downto", "64-1", "0")),
            new PortItem("y_we",  "in",  new StdLogic()),
            new PortItem("y_out", "out", new VectorKind("signed", "downto", "64-1", "0")),
            new PortItem("test_ia", "in", new VectorKind("signed", "downto", "32-1", "0")),
            new PortItem("test_ib", "in", new VectorKind("signed", "downto", "32-1", "0")),
            new PortItem("test_la", "in", new VectorKind("signed", "downto", "64-1", "0")),
            new PortItem("test_lb", "in", new VectorKind("signed", "downto", "64-1", "0")),
            new PortItem("test_busy", "out", new StdLogic()),
            new PortItem("test_req", "in", new StdLogic())
          )
          )
        )
      )
  }

  "architecture_decl" should "be parsed" in {
    val obj = new VHDLParser()
    obj.parse(obj.architecture_decl, """
architecture RTL of Test000 is
begin
end RTL;
""").get should be (new Architecture("RTL", "Test000", List()))
  }

  "attribute_decl" should " be parsed" in {
    val obj = new VHDLParser()
    obj.parse(obj.attribute_decl, "attribute mark_debug : string;").
      get should be (new Attribute("mark_debug", "string"))
  }

  "component_decl (null)" should " be parsed" in {
    val obj = new VHDLParser()
    obj.parse(obj.component_decl, """
component null_component
end component null_component;
"""
    ).get should be(new ComponentDecl("null_component", None))
  }

  "component_decl" should " be parsed" in {
    val obj = new VHDLParser()
    obj.parse(obj.component_decl, """
component synthesijer_mul32
  port (
    clk : in std_logic;
    reset : in std_logic;
    a : in signed(32-1 downto 0);
    b : in signed(32-1 downto 0);
    nd : in std_logic;
    result : out signed(32-1 downto 0);
    valid : out std_logic
  );
end component synthesijer_mul32;
"""
    ).get should be(
      new ComponentDecl("synthesijer_mul32",
        Some(List(
          new PortItem("clk", "in", new StdLogic()),
          new PortItem("reset", "in", new StdLogic()),
          new PortItem("a", "in", new VectorKind("signed", "downto", "32-1", "0")),
          new PortItem("b", "in", new VectorKind("signed", "downto", "32-1", "0")),
          new PortItem("nd", "in", new StdLogic()),
          new PortItem("result", "out", new VectorKind("signed", "downto", "32-1", "0")),
          new PortItem("valid", "out", new StdLogic())
        ))
      )
    )
  }

  "signal decl (std_logic without init)" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parse(obj.signal_decl, """signal clk_sig : std_logic;""").
      get should be ( new Signal("clk_sig", new StdLogic(), None) )
  }

  "bit value" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parse(obj.bit_value, "'0'").get should be ( "'0'" )
  }

  "init value" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parse(obj.init_value, ":= '0'").get should be ( "'0'" )
  }

  "init value (others => '0')" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parse(obj.init_value, ":= (others => '0')").get should be ( "(others=>'0')" )
  }
  
  "signal decl (std_logic with init)" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parse(obj.signal_decl, "signal clk_sig : std_logic := '0';").
      get should be ( new Signal("clk_sig", new StdLogic(), Some("'0'")) )
  }

  "signal decl (signed with init)" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parse(obj.signal_decl, "signal ic_in_sig : signed(32-1 downto 0) := (others => '0');").
      get should be ( new Signal("ic_in_sig", new VectorKind("signed", "downto", "32-1", "0"), Some("(others=>'0')")) )
  }

  "signal decl (std_logic_vector with init)" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parse(obj.signal_decl, "signal test_fc_0064 : std_logic_vector(32-1 downto 0) := (others => '0');").
      get should be ( new Signal("test_fc_0064", new VectorKind("std_logic_vector", "downto", "32-1", "0"), Some("(others=>'0')")) )
  }

  "symbol list" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parse(obj.symbol_list,
      "test_method_IDLE, test_method_S_0000, test_method_S_0001").
      get should be ( List("test_method_IDLE", "test_method_S_0000", "test_method_S_0001"))
  }

  "type decl" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parse(obj.type_decl,
      "type Type_test_method is (test_method_IDLE, test_method_S_0000, test_method_S_0001);").
      get should be ( new UserType("Type_test_method", List("test_method_IDLE", "test_method_S_0000", "test_method_S_0001")))
  }

  "signal decl (user defined type without init)" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parse(obj.signal_decl, "signal test_method : Type_test_method;").
      get should be ( new Signal("test_method", new UserTypeKind("Type_test_method"), None))
  }

  "signal decl (user defined type with init)" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parse(obj.signal_decl, "signal test_method : Type_test_method := test_method_IDLE;").
      get should be ( new Signal("test_method", new UserTypeKind("Type_test_method"), Some("test_method_IDLE")) )
  }


  "architecture" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parse(obj.architecture_decl, """
architecture RTL of Test000 is

  attribute mark_debug : string;

  component synthesijer_mul32
    port (
      clk : in std_logic;
      reset : in std_logic;
      a : in signed(32-1 downto 0);
      b : in signed(32-1 downto 0);
      nd : in std_logic;
      result : out signed(32-1 downto 0);
      valid : out std_logic
    );
  end component synthesijer_mul32;

  signal y_we_sig : std_logic := '0';
  signal binary_expr_00031 : signed(32-1 downto 0) := (others => '0');
  signal test_dc_0079 : std_logic_vector(64-1 downto 0) := (others => '0');

  type Type_test_method is (
    test_method_IDLE,
    test_method_S_0000,
    test_method_S_0001
  );
  signal test_method : Type_test_method := test_method_IDLE;
begin

end RTL;
""").get should be(
      new Architecture("RTL", "Test000",
        List(
          new Attribute("mark_debug", "string"),
          new ComponentDecl("synthesijer_mul32",
            Some(List(
              new PortItem("clk", "in", new StdLogic()),
              new PortItem("reset", "in", new StdLogic()),
              new PortItem("a", "in", new VectorKind("signed", "downto", "32-1", "0")),
              new PortItem("b", "in", new VectorKind("signed", "downto", "32-1", "0")),
              new PortItem("nd", "in", new StdLogic()),
              new PortItem("result", "out", new VectorKind("signed", "downto", "32-1", "0")),
              new PortItem("valid", "out", new StdLogic())
            ))
          ),
          new Signal("y_we_sig", new StdLogic(), Some("'0'")),
          new Signal("binary_expr_00031", new VectorKind("signed", "downto", "32-1", "0"), Some("(others=>'0')")),
          new Signal("test_dc_0079", new VectorKind("std_logic_vector", "downto", "64-1", "0"), Some("(others=>'0')")),
          new UserType("Type_test_method", List("test_method_IDLE", "test_method_S_0000", "test_method_S_0001")),
          new Signal("test_method", new UserTypeKind("Type_test_method"), Some("test_method_IDLE"))
        )
      )
    )
  }

}
