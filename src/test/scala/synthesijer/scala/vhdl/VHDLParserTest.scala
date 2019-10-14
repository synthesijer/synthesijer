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

  "defined a library (with lower-case)" should "be parsed" in {
    val obj = new VHDLParser()
    obj.parseAll(obj.library_clause, "library ieee;").get should be (List(new Library("ieee")))
  }

  "defined a library (with capital)" should "be parsed" in {
    val obj = new VHDLParser()
    obj.parseAll(obj.library_clause, "LIBRARY ieee;").get should be (List(new Library("ieee")))
  }

  "defined a library (with mixed-case)" should "be parsed" in {
    val obj = new VHDLParser()
    obj.parseAll(obj.library_clause, "lIBRary ieee;").get should be (List(new Library("ieee")))
  }

  "defined 2 libraries" should "be parsed" in {
    val obj = new VHDLParser()
    obj.parseAll(obj.library_clause, "LIBRARY ieee, Work;").get should be (List(new Library("ieee"), new Library("Work")))
  }

  "defined 3 libraries" should "be parsed" in {
    val obj = new VHDLParser()
    obj.parseAll(obj.library_clause, "LIBRARY ieee, work, test0123;").get should be (List(new Library("ieee"), new Library("work"), new Library("test0123")))
  }

  "package (simple)" should "be parsed" in {
    val obj = new VHDLParser()
    obj.parseAll(obj.package_decl, """
  package my_command is
  end package my_command;
""").get should be (
      new PackageDecl("my_command", List())
    )

  }


  "simple entity decl" should "be parsed" in {
    val obj = new VHDLParser()
    obj.parseAll(obj.entity_decl, "entity Test000 is end;")
      .get should be (
        new Entity("Test000", None)
      )
  }

  "entity decl" should "be parsed" in {
    val obj = new VHDLParser()
    obj.parseAll(obj.entity_decl, "entity Test000 is end Test000;")
      .get should be (
        new Entity("Test000", None)
      )
  }

  "full entity decl" should "be parsed" in {
    val obj = new VHDLParser()
    obj.parseAll(obj.entity_decl, "entity Test000 is end entity Test000;")
      .get should be (
        new Entity("Test000", None)
      )
  }

  "kind (std_logic)" should "be parsed" in {
    val obj = new VHDLParser()
    obj.parseAll(obj.kind, "std_logic")
      .get should be (new StdLogic())
  }

  "kind (std_logic_vector)" should "be parsed" in {
    val obj = new VHDLParser()
    obj.parseAll(obj.kind, "std_logic_vector(3 downto 0)")
      .get should be (new VectorKind("std_logic_vector", "downto", new Constant("3"), new Constant("0")))
  }

  "kind (signed)" should "be parsed" in {
    val obj = new VHDLParser()
    obj.parseAll(obj.kind, "signed(3 downto 0)")
      .get should be (new VectorKind("signed", "downto", new Constant("3"), new Constant("0")))
  }

  "kind (unsigned)" should "be parsed" in {
    val obj = new VHDLParser()
    obj.parseAll(obj.kind, "unsigned(3 downto 0)")
      .get should be (new VectorKind("unsigned", "downto", new Constant("3"), new Constant("0")))
  }

  "port item" should "be parsed" in {
    val obj = new VHDLParser()
    obj.parseAll(obj.port_item, "clk : in std_logic")
      .get should be (new PortItem("clk", "in", new StdLogic()))
    obj.parseAll(obj.port_item, "q : out std_logic_vector(3 downto 0)")
      .get should be (new PortItem("q", "out", new VectorKind("std_logic_vector", "downto", new Constant("3"), new Constant("0"))))
    obj.parseAll(obj.port_item, "gamma_signal : in std_logic_vector(31 downto 0) := (others => '0')")
      .get should be (new PortItem("gamma_signal", "in", new VectorKind("std_logic_vector", "downto", new Constant("31"), new Constant("0")), Some(new Others(new Constant("'0'")))))
  }

  "port item list" should "be parsed" in {
    val obj = new VHDLParser()
    obj.parseAll(obj.port_item_list, """
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
          new PortItem("sw",    "in",  new VectorKind("std_logic_vector", "downto", new Constant("3"), new Constant("0"))),
          new PortItem("q",     "out", new StdLogic())
        )
      )
  }

  "entity" should "be parsed" in {
    val obj = new VHDLParser()
    obj.parseAll(obj.entity_decl, """
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
            new PortItem("ic_in",  "in",  new VectorKind("signed", "downto", new BinaryExpr("-", new Constant("32"), new Constant("1")), new Constant("0"))),
            new PortItem("ic_we",  "in",  new StdLogic()),
            new PortItem("ic_out", "out", new VectorKind("signed", "downto", new BinaryExpr("-", new Constant("32"), new Constant("1")), new Constant("0"))),
            new PortItem("lc_in",  "in",  new VectorKind("signed", "downto", new BinaryExpr("-", new Constant("64"), new Constant("1")), new Constant("0"))),
            new PortItem("lc_we",  "in",  new StdLogic()),
            new PortItem("lc_out", "out", new VectorKind("signed", "downto", new BinaryExpr("-", new Constant("64"), new Constant("1")), new Constant("0"))),
            new PortItem("x_in",  "in",  new VectorKind("signed", "downto", new BinaryExpr("-", new Constant("32"), new Constant("1")), new Constant("0"))),
            new PortItem("x_we",  "in",  new StdLogic()),
            new PortItem("x_out", "out", new VectorKind("signed", "downto", new BinaryExpr("-", new Constant("32"), new Constant("1")), new Constant("0"))),
            new PortItem("y_in",  "in",  new VectorKind("signed", "downto", new BinaryExpr("-", new Constant("64"), new Constant("1")), new Constant("0"))),
            new PortItem("y_we",  "in",  new StdLogic()),
            new PortItem("y_out", "out", new VectorKind("signed", "downto", new BinaryExpr("-", new Constant("64"), new Constant("1")), new Constant("0"))),
            new PortItem("test_ia", "in", new VectorKind("signed", "downto", new BinaryExpr("-", new Constant("32"), new Constant("1")), new Constant("0"))),
            new PortItem("test_ib", "in", new VectorKind("signed", "downto", new BinaryExpr("-", new Constant("32"), new Constant("1")), new Constant("0"))),
            new PortItem("test_la", "in", new VectorKind("signed", "downto", new BinaryExpr("-", new Constant("64"), new Constant("1")), new Constant("0"))),
            new PortItem("test_lb", "in", new VectorKind("signed", "downto", new BinaryExpr("-", new Constant("64"), new Constant("1")), new Constant("0"))),
            new PortItem("test_busy", "out", new StdLogic()),
            new PortItem("test_req", "in", new StdLogic())
          )
          )
        )
      )
  }

  "entity with generic" should "be parsed" in {
    val obj = new VHDLParser()
    obj.parseAll(obj.entity_decl, """
entity Test000 is
  generic(
    LOOP_NUM_MAX     : integer          := 1024;
    CALC_NUM_MAX     : integer          := 1024;
    FRAME_TYPE_VALUE : std_logic_vector(15 downto 0) := X"0003"
    );
  port (
    clk : in std_logic;
    reset : in std_logic
  );
end Test000;
"""
    )
      .get should be (
        new Entity("Test000",
          Some(List(
            new PortItem("clk",    "in",  new StdLogic()),
            new PortItem("reset",  "in",  new StdLogic()),
          )),
          Some(List(
            new ParamItem("LOOP_NUM_MAX", new IntegerKind(), new Constant("1024")),
            new ParamItem("CALC_NUM_MAX", new IntegerKind(), new Constant("1024")),
            new ParamItem("FRAME_TYPE_VALUE",
              new VectorKind("std_logic_vector", "downto", new Constant("15"), new Constant("0")),
              new BasedValue("\"0003\"", 16))))
        )
      )
  }

  "architecture_decl" should "be parsed" in {
    val obj = new VHDLParser()
    obj.parseAll(obj.architecture_decl, """
architecture RTL of Test000 is
begin
end RTL;
""").get should be (new Architecture("RTL", "Test000", List(), List()))
  }

  "attribute_decl" should " be parsed" in {
    val obj = new VHDLParser()
    obj.parseAll(obj.attribute_decl, "attribute mark_debug : string;").
      get should be (new Attribute("mark_debug", "string"))
  }

  "component_decl (null)" should " be parsed" in {
    val obj = new VHDLParser()
    obj.parseAll(obj.component_decl, """
component null_component
end component null_component;
"""
    ).get should be(new ComponentDecl("null_component", None, None))
  }

  "component_decl" should " be parsed" in {
    val obj = new VHDLParser()
    obj.parseAll(obj.component_decl, """
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
          new PortItem("a", "in", new VectorKind("signed", "downto", new BinaryExpr("-", new Constant("32"), new Constant("1")), new Constant("0"))),
          new PortItem("b", "in", new VectorKind("signed", "downto", new BinaryExpr("-", new Constant("32"), new Constant("1")), new Constant("0"))),
          new PortItem("nd", "in", new StdLogic()),
          new PortItem("result", "out", new VectorKind("signed", "downto", new BinaryExpr("-", new Constant("32"), new Constant("1")), new Constant("0"))),
          new PortItem("valid", "out", new StdLogic())
        )),
        None
      )
    )
  }

  "component_decl with generic" should " be parsed" in {
    val obj = new VHDLParser()
    obj.parseAll(obj.component_decl, """
  component dualportram
    generic (
      WIDTH : integer := 32;
      DEPTH : integer := 10;
      WORDS : integer := 1024
    );
    port (
      clk : in std_logic;
      reset : in std_logic;
      length : out signed(31 downto 0);
      address : in signed(31 downto 0);
      din : in signed(WIDTH-1 downto 0);
      dout : out signed(WIDTH-1 downto 0);
      we : in std_logic;
      oe : in std_logic;
      address_b : in signed(31 downto 0);
      din_b : in signed(WIDTH-1 downto 0);
      dout_b : out signed(WIDTH-1 downto 0);
      we_b : in std_logic;
      oe_b : in std_logic
    );
  end component dualportram;
"""
    ).get should be(
      new ComponentDecl("dualportram",
        Some(List(
          new PortItem("clk", "in", new StdLogic()),
          new PortItem("reset", "in", new StdLogic()),
          new PortItem("length", "out", new VectorKind("signed", "downto", new Constant("31"), new Constant("0"))),
          new PortItem("address", "in", new VectorKind("signed", "downto", new Constant("31"), new Constant("0"))),
          new PortItem("din", "in",
            new VectorKind("signed", "downto", new BinaryExpr("-", new Ident("WIDTH"), new Constant("1")), new Constant("0"))),
          new PortItem("dout", "out",
            new VectorKind("signed", "downto", new BinaryExpr("-", new Ident("WIDTH"), new Constant("1")), new Constant("0"))),
          new PortItem("we", "in", new StdLogic()),
          new PortItem("oe", "in", new StdLogic()),
          new PortItem("address_b", "in", new VectorKind("signed", "downto", new Constant("31"), new Constant("0"))),
          new PortItem("din_b", "in",
            new VectorKind("signed", "downto", new BinaryExpr("-", new Ident("WIDTH"), new Constant("1")), new Constant("0"))),
          new PortItem("dout_b", "out",
            new VectorKind("signed", "downto", new BinaryExpr("-", new Ident("WIDTH"), new Constant("1")), new Constant("0"))),
          new PortItem("we_b", "in", new StdLogic()),
          new PortItem("oe_b", "in", new StdLogic())
        )),
        Some(List(
          new ParamItem("WIDTH", new IntegerKind(), new Constant("32")),
          new ParamItem("DEPTH", new IntegerKind(), new Constant("10")),
          new ParamItem("WORDS", new IntegerKind(), new Constant("1024"))))))
  }

  "signal decl (std_logic without init)" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parseAll(obj.signal_decl, """signal clk_sig : std_logic;""").
      get should be ( new Signal("clk_sig", new StdLogic()) )
  }

  "multiple signal decl (such as, \"clk, reset\")" should " be parsed as combined named signal (such as, \"clk,signal\")" in
  {
    val obj = new VHDLParser()
    obj.parseAll(obj.signal_decl, """signal clk, reset : std_logic;""").
      get should be ( new Signal("clk,reset", new StdLogic()) )
  }

  "bit value" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parseAll(obj.bit_value, "'0'").get should be ( "'0'" )
  }

  "init value" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parseAll(obj.init_value, ":= '0'").get should be ( new Constant("'0'") )
  }

  "init value (others => '0')" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parseAll(obj.init_value, ":= (others => '0')").get should be (new Others(new Constant("'0'")))
  }
  
  "signal decl (std_logic with init)" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parseAll(obj.signal_decl, "signal clk_sig : std_logic := '0';").
      get should be ( new Signal("clk_sig", new StdLogic(), Some(new Constant("'0'"))) )
  }

  "signal decl (signed with init)" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parseAll(obj.signal_decl, "signal ic_in_sig : signed(32-1 downto 0) := (others => '0');").
      get should be (
        new Signal("ic_in_sig",
          new VectorKind(
            "signed",
            "downto",
            new BinaryExpr("-", new Constant("32"), new Constant("1")),
            new Constant("0")),
          Some(new Others(new Constant("'0'"))))
      )
  }

  "signal decl (std_logic_vector with init)" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parseAll(obj.signal_decl, "signal test_fc_0064 : std_logic_vector(32-1 downto 0) := (others => '0');").
      get should be (
        new Signal("test_fc_0064",
          new VectorKind(
            "std_logic_vector",
            "downto",
            new BinaryExpr("-", new Constant("32"), new Constant("1")),
            new Constant("0")),
          Some(new Others(new Constant("'0'"))))
      )
  }

  "attribute keep of addr : signal is \"true\";" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parseAll(obj.declarations, """
  attribute keep of addr : signal is "true";
""").get should be (
      new SetAttribute("keep", "addr", new Constant("\"true\"")))
  }

  "variable tmp : unsigned(15 downto 0);" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parseAll(obj.process_decl, "variable tmp : unsigned(15 downto 0);").
      get should be (
        new Variable("tmp",
          new VectorKind(
            "unsigned",
            "downto",
            new Constant("15"),
            new Constant("0")),
          None
        )
      )
  }
  
  "file output_file : text is out \"out2.vec\";" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parseAll(obj.process_decl, "file output_file : text is out \"out2.vec\";").
      get should be (
        new FileDecl(
          "output_file",
          "text",
          "out",
          "\"out2.vec\""
        )
      )
  }


  "symbol list" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parseAll(obj.symbol_list,
      "test_method_IDLE, test_method_S_0000, test_method_S_0001").
      get should be ( List(new Ident("test_method_IDLE"), new Ident("test_method_S_0000"), new Ident("test_method_S_0001")))
  }

  "type decl" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parseAll(obj.type_decl,
      "type Type_test_method is (test_method_IDLE, test_method_S_0000, test_method_S_0001);").
      get should be ( new UserType("Type_test_method", List(new Ident("test_method_IDLE"), new Ident("test_method_S_0000"), new Ident("test_method_S_0001"))))
  }

  "type decl (array)" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parseAll(obj.type_decl, """
    type ram_type is array (WORDS-1 downto 0) of std_logic_vector (WIDTH-1 downto 0);
""").get should be (
      new ArrayType("ram_type",
        "downto",
        new BinaryExpr("-", new Ident("WORDS"), new Constant("1")),
        new Constant("0"),
        new VectorKind(
          "std_logic_vector",
          "downto",
          new BinaryExpr("-", new Ident("WIDTH"), new Constant("1")),
          new Constant("0"))))
  }


  "signal decl (user defined type without init)" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parseAll(obj.signal_decl, "signal test_method : Type_test_method;").
      get should be ( new Signal("test_method", new UserTypeKind("Type_test_method")))
  }

  "signal decl (user defined type with init)" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parseAll(obj.signal_decl, "signal test_method : Type_test_method := test_method_IDLE;").
      get should be ( new Signal("test_method", new UserTypeKind("Type_test_method"), Some(new Ident("test_method_IDLE"))) )
  }

  "signal decl (with init by hex value)" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parseAll(obj.signal_decl, """
    signal u_synthesijer_div64_test_b : signed(64-1 downto 0) := X"0000000000000001";
""").
      get should be (
        new Signal("u_synthesijer_div64_test_b",
          new VectorKind("signed", "downto", new BinaryExpr("-", new Constant("64"), new Constant("1")), new Constant("0")),
          Some(new BasedValue("\"0000000000000001\"", 16)))
      )
  }

  "constant LOOP_NUM_MAX : integer := 1024;" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parseAll(obj.declarations, """
constant LOOP_NUM_MAX : integer := 1024;
""").
      get should be (
        new ConstantDecl("LOOP_NUM_MAX", new IntegerKind(), new Constant("1024"))
      )
  }

  "wait;" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parseAll(obj.statement_in_process, "wait;").
      get should be ( new WaitStatement("", null ))
  }

  "wait for 10 ns;" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parseAll(obj.statement_in_process, "wait for 10 ns;").
      get should be ( new WaitStatement("for", new TimeUnit(Constant("10"), "ns") ))
  }

  "report \"Test_ArithInt: TEST *** FAILURE ***\";" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parseAll(obj.statement_in_process, """report "Test_ArithInt: TEST *** FAILURE ***";""").
      get should be ( new ReportStatement("\"Test_ArithInt: TEST *** FAILURE ***\""))
  }

  "clk_sig <= clk;" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parseAll(obj.assign_statement, "clk_sig <= clk;").
      get should be ( new AssignStatement(new Ident("clk_sig"), new Ident("clk")) )
  }

  "clk_sig <= clk; -- with comment" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parseAll(obj.assign_statement, "clk_sig <= clk;  -- with comment").
      get should be ( new AssignStatement(new Ident("clk_sig"), new Ident("clk")) )
  }

  "id := counter;" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parseAll(obj.statement_in_process, "id := counter;").
      get should be ( new BlockingAssignStatement(new Ident("id"), new Ident("counter")) )
  }

  "dest(0)  <= src(0);" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parseAll(obj.statement_in_process, "dest(0)  <= src(0);").
      get should be (
        new AssignStatement(
          new CallExpr(new Ident("dest"), List(new Constant("0"))),
          new CallExpr(new Ident("src"), List(new Constant("0")))))
  }

  "write(a);" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parseAll(obj.call_statement, "write(a);").get should be(
      new CallStatement(new CallExpr(new Ident("write"), List(new Ident("a")))),
    )
  }

  "process, \"process begin end process;\"" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parseAll(obj.process_statement, "process begin end process;").
      get should be ( new ProcessStatement(None, None, List()) )
  }

  "sensitivity_list \"()\"" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parseAll(obj.sensitivity_list, "()").get should be (List())
  }

  "sensitivity_list \"(clk)\"" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parseAll(obj.sensitivity_list, "(clk)").get should be (List(new Ident("clk")))
  }

  "sensitivity_list \"(clk, reset)\"" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parseAll(obj.sensitivity_list, "(clk, reset)").get should be (List(new Ident("clk"), new Ident("reset")))
  }

  "process \"process() begin end process;\"" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parseAll(obj.process_statement, "process() begin end process;").
      get should be ( new ProcessStatement(Some(List()), None, List()) )
  }

  "process \"process() begin end process;\" with variable" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parseAll(obj.process_statement, """
process()
  variable tmp : unsigned(15 downto 0);
begin
end process;
""").
      get should be (
        new ProcessStatement(
          Some(List()),
          None,
          List(),
          List(
            new Variable("tmp",
              new VectorKind(
                "unsigned",
                "downto",
                new Constant("15"),
                new Constant("0")),
              None
            )
          )
        )
      )
  }

  "process (empty, with label)" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parseAll(obj.process_statement, "LABEL: process() begin end process LABEL;").
      get should be ( new ProcessStatement(Some(List()), Some("LABEL"), List()) )
  }

  "process (with clk)" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parseAll(obj.process_statement, "process(clk) begin end process;").
      get should be ( new ProcessStatement(Some(List(new Ident("clk"))), None, List()) )
  }

  "process (with clk and reset)" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parseAll(obj.process_statement, "process(clk, reset) begin end process;").
      get should be ( new ProcessStatement(Some(List(new Ident("clk"), new Ident("reset"))), None, List()) )
  }

  "process (with multiple)" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parseAll(obj.process_statement, "process(a, b, c) begin end process;").
      get should be ( new ProcessStatement(Some(List(new Ident("a"),new Ident("b"),new Ident("c"))), None, List()) )
  }

  "function call rising_edge(clk)" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parseAll(obj.prime_expression, """
  rising_edge(clk)
""").
      get should be (
        new CallExpr(new Ident("rising_edge"), List(new Ident("clk"))))
  }

  "(ohters => test)" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parseAll(obj.value_expression, "(others => test)").
      get should be (new Others(new Ident("test")))
  }


  "process (with clk and body)" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parseAll(obj.process_statement, """
process(clk)
begin
  if rising_edge(clk) then
  end if;
end process;
""").
      get should be (
        new ProcessStatement(
          Some(List(new Ident("clk"))),
          None,
          List(new IfStatement(
            new CallExpr(new Ident("rising_edge"), List(new Ident("clk"))),
            List(),
            List(),
            None
          )
          )
        )
      )
  }

  "expr '1'" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parseAll(obj.expression, "'1'").get should be (new Constant("'1'"))
  }

  "expr clk" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parseAll(obj.expression, "clk").get should be (new Ident("clk"))
  }

  "expr clk'event" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parseAll(obj.expression, "clk'event").get should be (new Ident("clk'event"))
  }

  "expr clk = '1'" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parseAll(obj.expression, "clk = '1'").get should be (new BinaryExpr("=", new Ident("clk"), new Constant("'1'")))
  }

  "expr clk'event and clk = '1'" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parseAll(obj.expression, "clk'event and clk = '1'").
      get should be (new BinaryExpr("and", new Ident("clk'event"), new BinaryExpr("=", new Ident("clk"), new Constant("'1'"))))
  }

  "expr rising_edge(clk)" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parseAll(obj.expression, "rising_edge(clk)").get should be (new CallExpr(new Ident("rising_edge"), List(new Ident("clk"))))
  }

  "compare =" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parseAll(obj.compare_operation, "=").get should be ( "=" )
  }

  "compare /=" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parseAll(obj.compare_operation, "/=").get should be ( "/=" )
  }

  "when expr 1" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parseAll(obj.when_expression, "ic_in_sig when ic_we_sig = '1' else class_ic_0000").
      get should be (
        new WhenExpr(
          new BinaryExpr("=", new Ident("ic_we_sig"), new Constant("'1'")), // cond
          new Ident("ic_in_sig"),    // then-expr
          new Ident("class_ic_0000") // else-expr
        )
      )
  }

  "when expr 2" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parseAll(obj.when_expression, "'1' when test_method = test_method_S_0000 else '0'").
      get should be (
        new WhenExpr(
          new BinaryExpr("=", new Ident("test_method"), new Ident("test_method_S_0000")), // cond
          new Constant("'1'"),    // then-expr
          new Constant("'0'") // else-expr
        )
      )
  }

  "assignement with when" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parseAll(obj.assign_statement, "tmp_0001 <= ic_in_sig when ic_we_sig = '1' else class_ic_0000;").
      get should be (
        new AssignStatement(
          new Ident("tmp_0001"),
          new WhenExpr(
            new BinaryExpr("=", new Ident("ic_we_sig"), new Constant("'1'")), // cond
            new Ident("ic_in_sig"),    // then-expr
            new Ident("class_ic_0000") // else-expr
          )
        )
      )
  }

  "expression concat and bit-padding" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parseAll(obj.binary_expression, """
    (32-1 downto 30 => test_ia_0004(31)) & test_ia_0004(31 downto 2)
""").get should be(
      new BinaryExpr("&",
        new BitPaddingExpr("downto",
          new BinaryExpr("-", new Constant("32"), new Constant("1")),
          new Constant("30"),
          new CallExpr(new Ident("test_ia_0004"), List(new Constant("31")))),
        new BitVectorSelect(
          new Ident("test_ia_0004"),
          "downto",
          new Constant("31"),
          new Constant("2"))))
  }

  "assignement with bit-padding" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parseAll(obj.assign_statement, """
    tmp_0021 <= (32-1 downto 30 => test_ia_0004(31)) & test_ia_0004(31 downto 2);
""").get should be(
      new AssignStatement(
        new Ident("tmp_0021"),
        new BinaryExpr("&",
          new BitPaddingExpr("downto",
            new BinaryExpr("-", new Constant("32"), new Constant("1")),
            new Constant("30"),
            new CallExpr(new Ident("test_ia_0004"), List(new Constant("31")))),
          new BitVectorSelect(
            new Ident("test_ia_0004"),
            "downto",
            new Constant("31"),
            new Constant("2")))))
  }

  "expr or/and" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parseAll(obj.expression, "test_req_local or test_req_sig").
      get should be (
        new BinaryExpr("or", new Ident("test_req_local"), new Ident("test_req_sig"))
      )
    obj.parseAll(obj.expression, "test_req_flag and tmp_0006").
      get should be (
        new BinaryExpr("and", new Ident("test_req_flag"), new Ident("tmp_0006"))
      )
  }

  "expr not" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parseAll(obj.expression, "not test_req_flag_d").
      get should be (
        new UnaryExpr("not", new Ident("test_req_flag_d"))
      )
  }

  "expr +/-" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parseAll(obj.expression, "test_ia_0004 + test_ib_0005").
      get should be (
        new BinaryExpr("+", new Ident("test_ia_0004"), new Ident("test_ib_0005"))
      )
    obj.parseAll(obj.expression, "test_ia_0004 - test_ib_0005").
      get should be (
        new BinaryExpr("-", new Ident("test_ia_0004"), new Ident("test_ib_0005"))
      )
  }

  "expr hex-value" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parseAll(obj.expression, "X\"00000000\"").
      get should be (
        new BasedValue("\"00000000\"", 16)
      )
  }

  "expr concast" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parseAll(obj.expression, "a & b").
      get should be (
        new BinaryExpr("&", new Ident("a"), new Ident("b"))
      )
  }

  "unary " should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parseAll(obj.expression, "(not test_da_0077(64-1))").
      get should be (
        new UnaryExpr("not", new CallExpr(new Ident("test_da_0077"), List(new BinaryExpr("-", new Constant("64"), new Constant("1"))))))
  }

  "expr complex concat" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parseAll(obj.expression, "(not test_da_0077(64-1)) & test_da_0077(64-2 downto 0)").
      get should be (
        new BinaryExpr("&",
          new UnaryExpr("not",
            new CallExpr(new Ident("test_da_0077"), List(new BinaryExpr("-", new Constant("64"), new Constant("1"))))),
          new BitVectorSelect(
            new Ident("test_da_0077"),
            "downto",
            new BinaryExpr("-", new Constant("64"), new Constant("2")),
            new Constant("0"))))
  }

  "expr bit-padding" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parseAll(obj.expression, "(32-1 downto 30 => '0')").
      get should be (
        new BitPaddingExpr("downto",
          new BinaryExpr("-", new Constant("32"), new Constant("1")),
          new Constant("30"),
          new Constant("'0'"))
      )
  }

  "prime_expression" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parseAll(obj.prime_expression, """
    test_ia_0004(31)
""").get should be(
      new CallExpr(new Ident("test_ia_0004"), List(new Constant("31"))))
  }

  "complex bit-padding" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parseAll(obj.bit_padding_expression, """
    (32-1 downto 30 => test_ia_0004(31))
""").get should be(
      new BitPaddingExpr("downto",
        new BinaryExpr("-", new Constant("32"), new Constant("1")),
        new Constant("30"),
        new CallExpr(new Ident("test_ia_0004"), List(new Constant("31")))))
  }

  "expr bit-vector-select" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parseAll(obj.bit_vector_select, "unary_expr_00017(31 downto 2)").
      get should be (
        new BitVectorSelect(
          new Ident("unary_expr_00017"),
          "downto",
          new Constant("31"),
          new Constant("2"))
      )
  }

  "expr bit-select" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parseAll(obj.expression, "unary_expr_00017(2)").
      get should be (
        new CallExpr(new Ident("unary_expr_00017"), List(new Constant("2")))
      )
  }

  "expr bit-select with binary-expression" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parseAll(obj.expression, "unary_expr_00017(32-4)").
      get should be (
        new CallExpr(new Ident("unary_expr_00017"), List(new BinaryExpr("-", new Constant("32"), new Constant("4"))))
      )
  }

  "if \"if clk'event and clk = '1' then...end if;\"" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parseAll(obj.if_statement, """
if clk'event and clk = '1' then
end if;
""").get should be (
      new IfStatement(
        new BinaryExpr("and", new Ident("clk'event"), new BinaryExpr("=", new Ident("clk"), new Constant("'1'"))),
        List(),
        List(),
        None
      )
    )
  }

  "signed(class_char_value_0003)" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parseAll(obj.expression, """
signed(class_char_value_0003)
""").get should be (
      new CallExpr(new Ident("signed"), List(new Ident("class_char_value_0003"))))
  }

  "tmp_0060 <= '1' when signed(class_char_value_0003) /= X\"0064\" else '0'" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parseAll(obj.expression, """
'1' when signed(class_char_value_0003) /= X"0064" else '0'
""").get should be (
      new WhenExpr(
        new BinaryExpr("/=",
          new CallExpr(new Ident("signed"), List(new Ident("class_char_value_0003"))),
          new BasedValue("\"0064\"", 16)),
        new Constant("'1'"),    // then-expr
        new Constant("'0'") // else-expr
      )
    )
  }

  "if \"if rising_edge(clk) then...end if;\"" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parseAll(obj.if_statement, """
if rising_edge(clk) then
end if;
""").get should be (
      new IfStatement(
        new CallExpr(new Ident("rising_edge"), List(new Ident("clk"))),
        List(),
        List(),
        None
      )
    )
  }


  "if if-then" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parseAll(obj.if_statement, """
        if test_method = test_method_S_0000 then
          test_busy_sig <= '0';
        end if;
""").get should be (
      new IfStatement(
        new BinaryExpr("=", new Ident("test_method"), new Ident("test_method_S_0000")),
        List(new AssignStatement(new Ident("test_busy_sig"), new Constant("'0'"))),
        List(),
        None
      )
    )
  }

  "if if-then-else" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parseAll(obj.if_statement, """
        if test_method = test_method_S_0000 then
          test_busy_sig <= '0';
        else
          test_busy_sig <= '1';
        end if;
""").get should be (
      new IfStatement(
        new BinaryExpr("=", new Ident("test_method"), new Ident("test_method_S_0000")),
        List(new AssignStatement(new Ident("test_busy_sig"), new Constant("'0'"))),
        List(),
        Some(List(new AssignStatement(new Ident("test_busy_sig"), new Constant("'1'")))),
      )
    )
  }

  "if if-then-elsif-else" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parseAll(obj.if_statement, """
        if test_method = test_method_S_0000 then
          test_busy_sig <= '0';
        elsif test_method = test_method_S_0001 then
          test_busy_sig <= '1';
        else
          test_busy_sig <= '1';
        end if;
""").get should be (
      new IfStatement(
        new BinaryExpr("=", new Ident("test_method"), new Ident("test_method_S_0000")),
        List(new AssignStatement(new Ident("test_busy_sig"), new Constant("'0'"))),
        List(
          new IfStatement(
            new BinaryExpr("=", new Ident("test_method"), new Ident("test_method_S_0001")),
            List(new AssignStatement(new Ident("test_busy_sig"), new Constant("'1'"))),
            List(),
            None
          )),
        Some(List(new AssignStatement(new Ident("test_busy_sig"), new Constant("'1'")))),
      )
    )
  }

  "if if-then-elsif-elsif-else" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parseAll(obj.if_statement, """
        if test_method = test_method_S_0000 then
          test_busy_sig <= '0';
        elsif test_method = test_method_S_0001 then
          test_busy_sig <= '1';
        elsif test_method = test_method_S_0002 then
          test_busy_sig <= '0';
        else
          test_busy_sig <= '1';
        end if;
""").get should be (
      new IfStatement(
        new BinaryExpr("=", new Ident("test_method"), new Ident("test_method_S_0000")),
        List(new AssignStatement(new Ident("test_busy_sig"), new Constant("'0'"))),
        List(
          new IfStatement(
            new BinaryExpr("=", new Ident("test_method"), new Ident("test_method_S_0001")),
            List(new AssignStatement(new Ident("test_busy_sig"), new Constant("'1'"))),
            List(),
            None
          ),
          new IfStatement(
            new BinaryExpr("=", new Ident("test_method"), new Ident("test_method_S_0002")),
            List(new AssignStatement(new Ident("test_busy_sig"), new Constant("'0'"))),
            List(),
            None
          )
        ),
        Some(List(new AssignStatement(new Ident("test_busy_sig"), new Constant("'1'")))),
      )
    )
  }

  "if if-then-elsif" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parseAll(obj.if_statement, """
        if test_method = test_method_S_0000 then
          test_busy_sig <= '0';
        elsif test_method = test_method_S_0001 then
          test_busy_sig <= '1';
        end if;
""").get should be (
      new IfStatement(
        new BinaryExpr("=", new Ident("test_method"), new Ident("test_method_S_0000")),
        List(new AssignStatement(new Ident("test_busy_sig"), new Constant("'0'"))),
        List(
          new IfStatement(
            new BinaryExpr("=", new Ident("test_method"), new Ident("test_method_S_0001")),
            List(new AssignStatement(new Ident("test_busy_sig"), new Constant("'1'"))),
            List(),
            None
          )),
        None
      )
    )
  }

  "case statements minimal" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parseAll(obj.statement_in_process, """
        case (test_method) is
        end case;
""").get should be (
      new CaseStatement(new Ident("test_method"),
        List()
      )
    )
  }

  "case statements with a when-clause" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parseAll(obj.statement_in_process, """
        case (test_method) is
          when others => null;
        end case;
""").get should be (
      new CaseStatement(new Ident("test_method"),
        List(new CaseWhenClause(new Ident("others"), List(new NullStatement())))
      )
    )
  }

  "case statements" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parseAll(obj.statement_in_process, """
        case (test_method) is
          when test_method_IDLE => 
            test_method <= test_method_S_0000;
          when test_method_S_0000 => 
            test_method <= test_method_S_0001;
          when test_method_S_0001 => 
            if tmp_0008 = '1' then
              test_method <= test_method_S_0002;
            end if;
          when others => null;
        end case;
""").get should be (
      new CaseStatement(new Ident("test_method"),
        List(
          new CaseWhenClause(
            new Ident("test_method_IDLE"),
            List(new AssignStatement(new Ident("test_method"), new Ident("test_method_S_0000")))),
          new CaseWhenClause(
            new Ident("test_method_S_0000"),
            List(new AssignStatement(new Ident("test_method"), new Ident("test_method_S_0001")))),
          new CaseWhenClause(
            new Ident("test_method_S_0001"),
            List(
              new IfStatement(new BinaryExpr("=", new Ident("tmp_0008"), new Constant("'1'")),
                List(new AssignStatement(new Ident("test_method"), new Ident("test_method_S_0002"))),
                List(),
                None))),
          new CaseWhenClause(new Ident("others"), List(new NullStatement()))
        )
      )
    )
  }

  "module instantiation (minimal)" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parseAll(obj.instantiation_statement, """
  inst_u_synthesijer_fsub64_test : synthesijer_fsub64
  port map(
  );
""").get should be (
      new InstanceStatement(
        new Ident("inst_u_synthesijer_fsub64_test"),
        new Ident("synthesijer_fsub64"),
        List(),
        None
      )
    )
  }

  "generate" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parseAll(obj.architecture_statement, """
  U_GEN : for i in 0 to NUM_OF_UNITS-1 generate
  begin
  end generate;
""").get should be (
      new GenerateFor(
        new Ident("U_GEN"),
        new Ident("i"),
        "to",
        new Constant("0"),
        new BinaryExpr("-", new Ident("NUM_OF_UNITS"), new Constant("1")),
        List()
      )
    )
  }

  "module instantiation" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parseAll(obj.instantiation_statement, """
  inst_u_synthesijer_fsub64_test : synthesijer_fsub64
  port map(
    clk => clk,
    reset => reset,
    a => u_synthesijer_fsub64_test_a,
    b(0) => u_synthesijer_fsub64_test_b(0),
    nd => u_synthesijer_fsub64_test_nd,
    result => u_synthesijer_fsub64_test_result,
    valid => u_synthesijer_fsub64_test_valid
  );
""").get should be (
      new InstanceStatement(
        new Ident("inst_u_synthesijer_fsub64_test"),
        new Ident("synthesijer_fsub64"),
        List(
          new PortMapItem(new Ident("clk"),    new Ident("clk")),
          new PortMapItem(new Ident("reset"),  new Ident("reset")),
          new PortMapItem(new Ident("a"),      new Ident("u_synthesijer_fsub64_test_a")),
          new PortMapItem(
            new CallExpr(new Ident("b"), List(new Constant("0"))),
            new CallExpr(new Ident("u_synthesijer_fsub64_test_b"), List(new Constant("0")))),
          new PortMapItem(new Ident("nd"),     new Ident("u_synthesijer_fsub64_test_nd")),
          new PortMapItem(new Ident("result"), new Ident("u_synthesijer_fsub64_test_result")),
          new PortMapItem(new Ident("valid"),  new Ident("u_synthesijer_fsub64_test_valid"))
        ),
        None
      )
    )
  }

  "module instantiation with generic map" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parseAll(obj.instantiation_statement, """
  inst_class_a_0002 : dualportram
  generic map(
    WIDTH => 32,
    DEPTH => 7,
    WORDS => 128
  )
  port map(
    clk => clk,
    reset => reset
  );
""").get should be (
      new InstanceStatement(
        new Ident("inst_class_a_0002"),
        new Ident("dualportram"),
        List(
          new PortMapItem(new Ident("clk"),   new Ident("clk")),
          new PortMapItem(new Ident("reset"), new Ident("reset"))
        ),
        Some(List(
          new PortMapItem(new Ident("WIDTH"), new Constant("32")),
          new PortMapItem(new Ident("DEPTH"), new Constant("7")),
          new PortMapItem(new Ident("WORDS"), new Constant("128"))
        ))
      )
    )
  }

  "architecture" should " be parsed" in
  {
    val obj = new VHDLParser()
    obj.parseAll(obj.architecture_decl, """
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

    clk_sig <= clk;
    process begin end process;

    process(clk)
    begin
      if rising_edge(clk) then
        if test_method = test_method_S_0000 then
          test_busy_sig <= '0';
        elsif test_method = test_method_S_0001 then
          test_busy_sig <= '1';
        else
          test_busy_sig <= '1';
        end if;
      end if;
    end process;

    inst_u_synthesijer_fsub64_test : synthesijer_fsub64
    port map(
      clk => clk,
      reset => reset,
      a => u_synthesijer_fsub64_test_a,
      b => u_synthesijer_fsub64_test_b,
      nd => u_synthesijer_fsub64_test_nd,
      result => u_synthesijer_fsub64_test_result,
      valid => u_synthesijer_fsub64_test_valid
    );

end RTL;
""").get should be(
      new Architecture("RTL", "Test000",
        List(
          new Attribute("mark_debug", "string"),
          new ComponentDecl("synthesijer_mul32",
            Some(List(
              new PortItem("clk", "in", new StdLogic()),
              new PortItem("reset", "in", new StdLogic()),
              new PortItem("a", "in",
                new VectorKind(
                  "signed",
                  "downto",
                  new BinaryExpr("-", new Constant("32"), new Constant("1")),
                  new Constant("0"))),
              new PortItem("b", "in",
                new VectorKind(
                  "signed",
                  "downto",
                  new BinaryExpr("-", new Constant("32"), new Constant("1")),
                  new Constant("0"))),
              new PortItem("nd", "in", new StdLogic()),
              new PortItem("result", "out",
                new VectorKind(
                  "signed",
                  "downto",
                  new BinaryExpr("-", new Constant("32"), new Constant("1")),
                  new Constant("0"))),
              new PortItem("valid", "out", new StdLogic())
            )),
            None
          ),
          new Signal("y_we_sig", new StdLogic(), Some(new Constant("'0'"))),
          new Signal("binary_expr_00031",
            new VectorKind(
              "signed",
              "downto",
              new BinaryExpr("-", new Constant("32"), new Constant("1")),
              new Constant("0")),
            Some(new Others(new Constant("'0'")))),
          new Signal("test_dc_0079",
            new VectorKind(
              "std_logic_vector",
              "downto",
              new BinaryExpr("-", new Constant("64"), new Constant("1")),
              new Constant("0")),
            Some(new Others(new Constant("'0'")))),
          new UserType("Type_test_method", List(new Ident("test_method_IDLE"), new Ident("test_method_S_0000"), new Ident("test_method_S_0001"))),
          new Signal("test_method", new UserTypeKind("Type_test_method"), Some(new Ident("test_method_IDLE")))
        ),
        List(
          new AssignStatement(new Ident("clk_sig"), new Ident("clk")),
          new ProcessStatement(None, None, List()),
          new ProcessStatement(
            Some(List(new Ident("clk"))),
            None,
            List(new IfStatement(
              new CallExpr(new Ident("rising_edge"), List(new Ident("clk"))),
              List(
                new IfStatement(
                  new BinaryExpr("=", new Ident("test_method"), new Ident("test_method_S_0000")),
                  List(new AssignStatement(new Ident("test_busy_sig"), new Constant("'0'"))),
                  List(
                    new IfStatement(
                      new BinaryExpr("=", new Ident("test_method"), new Ident("test_method_S_0001")),
                      List(new AssignStatement(new Ident("test_busy_sig"), new Constant("'1'"))),
                      List(),
                      None
                    )),
                  Some(List(new AssignStatement(new Ident("test_busy_sig"), new Constant("'1'")))),
                )
              ),
              List(),
              None
            )
            )
          ),
          new InstanceStatement(
            new Ident("inst_u_synthesijer_fsub64_test"),
            new Ident("synthesijer_fsub64"),
            List(
              new PortMapItem(new Ident("clk"),    new Ident("clk")),
              new PortMapItem(new Ident("reset"),  new Ident("reset")),
              new PortMapItem(new Ident("a"),      new Ident("u_synthesijer_fsub64_test_a")),
              new PortMapItem(new Ident("b"),      new Ident("u_synthesijer_fsub64_test_b")),
              new PortMapItem(new Ident("nd"),     new Ident("u_synthesijer_fsub64_test_nd")),
              new PortMapItem(new Ident("result"), new Ident("u_synthesijer_fsub64_test_result")),
              new PortMapItem(new Ident("valid"),  new Ident("u_synthesijer_fsub64_test_valid"))
            ),
            None
          )
        )
      )
    )
  }

  "library unit (mini)" should "be parsed" in {
    val obj = new VHDLParser()
    obj.parseAll(obj.library_unit, """
entity Test is end entity Test;
architecture RTL of Test is begin end RTL;
""").get should be (
      new LibraryUnit(new Entity("Test", None), Some(new Architecture("RTL", "Test", List(), List())))
    )
  }

  "design unit (mini)" should "be parsed" in {
    val obj = new VHDLParser()
    obj.parseAll(obj.design_unit, """
LIBRARY ieee, work, test0123;
use ieee.std_logic_1164.all;
entity Test is end;
architecture RTL of Test is begin end RTL;
""").get should be (
      new DesignUnit(
        List(
          List(new Library("ieee"), new Library("work"), new Library("test0123")),
          List(new Use("ieee.std_logic_1164.all"))),
        new Entity("Test", None),
        Some(new Architecture("RTL", "Test", List(), List()))
      )
    )
  }

  "design unit" should "be parsed" in {
    val obj = new VHDLParser()
    obj.parseAll(obj.design_unit, """
LIBRARY ieee, work, test0123;
use ieee.std_logic_1164.all;

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

    clk_sig <= clk;
    process begin end process;

    process(clk)
    begin
      if rising_edge(clk) then
        if test_method = test_method_S_0000 then
          test_busy_sig <= '0';
        elsif test_method = test_method_S_0001 then
          test_busy_sig <= '1';
        else
          test_busy_sig <= '1';
        end if;
      end if;
    end process;

    inst_u_synthesijer_fsub64_test : synthesijer_fsub64
    port map(
      clk => clk,
      reset => reset,
      a => u_synthesijer_fsub64_test_a,
      b => u_synthesijer_fsub64_test_b,
      nd => u_synthesijer_fsub64_test_nd,
      result => u_synthesijer_fsub64_test_result,
      valid => u_synthesijer_fsub64_test_valid
    );

end RTL;
"""
    )
      .get should be (
        new DesignUnit(
          List(
            List(new Library("ieee"), new Library("work"), new Library("test0123")),
            List(new Use("ieee.std_logic_1164.all"))),
          new Entity("Test000",
            Some(List(
              new PortItem("clk",    "in",  new StdLogic()),
              new PortItem("reset",  "in",  new StdLogic()),
              new PortItem("ic_in",  "in",
                new VectorKind(
                  "signed",
                  "downto",
                  new BinaryExpr("-", new Constant("32"), new Constant("1")),
                  new Constant("0"))),
              new PortItem("ic_we",  "in",  new StdLogic()),
              new PortItem("ic_out",  "out",
                new VectorKind(
                  "signed",
                  "downto",
                  new BinaryExpr("-", new Constant("32"), new Constant("1")),
                  new Constant("0"))),
              new PortItem("lc_in",  "in",
                new VectorKind(
                  "signed",
                  "downto",
                  new BinaryExpr("-", new Constant("64"), new Constant("1")),
                  new Constant("0"))),
              new PortItem("lc_we",  "in",  new StdLogic()),
              new PortItem("lc_out",  "out",
                new VectorKind(
                  "signed",
                  "downto",
                  new BinaryExpr("-", new Constant("64"), new Constant("1")),
                  new Constant("0"))),
              new PortItem("x_in",  "in",
                new VectorKind(
                  "signed",
                  "downto",
                  new BinaryExpr("-", new Constant("32"), new Constant("1")),
                  new Constant("0"))),
              new PortItem("x_we",  "in",  new StdLogic()),
              new PortItem("x_out", "out",
                new VectorKind(
                  "signed",
                  "downto",
                  new BinaryExpr("-", new Constant("32"), new Constant("1")),
                  new Constant("0"))),
              new PortItem("y_in",  "in",
                new VectorKind(
                  "signed",
                  "downto",
                  new BinaryExpr("-", new Constant("64"), new Constant("1")),
                  new Constant("0"))),
              new PortItem("y_we",  "in",  new StdLogic()),
              new PortItem("y_out", "out",
                new VectorKind(
                  "signed",
                  "downto",
                  new BinaryExpr("-", new Constant("64"), new Constant("1")),
                  new Constant("0"))),
              new PortItem("test_ia", "in",
                new VectorKind(
                  "signed",
                  "downto",
                  new BinaryExpr("-", new Constant("32"), new Constant("1")),
                  new Constant("0"))),
              new PortItem("test_ib", "in",
                new VectorKind(
                  "signed",
                  "downto",
                  new BinaryExpr("-", new Constant("32"), new Constant("1")),
                  new Constant("0"))),
              new PortItem("test_la", "in",
                new VectorKind(
                  "signed",
                  "downto",
                  new BinaryExpr("-", new Constant("64"), new Constant("1")),
                  new Constant("0"))),
              new PortItem("test_lb", "in",
                new VectorKind(
                  "signed",
                  "downto",
                  new BinaryExpr("-", new Constant("64"), new Constant("1")),
                  new Constant("0"))),
              new PortItem("test_busy", "out", new StdLogic()),
              new PortItem("test_req", "in", new StdLogic()))
            )
          ),
          Some(new Architecture("RTL", "Test000",
            List(
              new Attribute("mark_debug", "string"),
              new ComponentDecl("synthesijer_mul32",
                Some(List(
                  new PortItem("clk", "in", new StdLogic()),
                  new PortItem("reset", "in", new StdLogic()),
                  new PortItem("a", "in",
                    new VectorKind(
                      "signed",
                      "downto",
                      new BinaryExpr("-", new Constant("32"), new Constant("1")),
                      new Constant("0"))),
                  new PortItem("b", "in",
                    new VectorKind(
                      "signed",
                      "downto",
                      new BinaryExpr("-", new Constant("32"), new Constant("1")),
                      new Constant("0"))),
                  new PortItem("nd", "in", new StdLogic()),
                  new PortItem("result", "out",
                    new VectorKind(
                      "signed",
                      "downto",
                      new BinaryExpr("-", new Constant("32"), new Constant("1")),
                      new Constant("0"))),
                  new PortItem("valid", "out", new StdLogic())
                )),
                None
              ),
              new Signal("y_we_sig", new StdLogic(), Some(new Constant("'0'"))),
              new Signal("binary_expr_00031",
                new VectorKind(
                  "signed",
                  "downto",
                  new BinaryExpr("-", new Constant("32"), new Constant("1")),
                  new Constant("0")),
                Some(new Others(new Constant("'0'")))),
              new Signal("test_dc_0079",
                new VectorKind(
                  "std_logic_vector",
                  "downto",
                  new BinaryExpr("-", new Constant("64"), new Constant("1")),
                  new Constant("0")),
                Some(new Others(new Constant("'0'")))),
              new UserType("Type_test_method", List(new Ident("test_method_IDLE"), new Ident("test_method_S_0000"), new Ident("test_method_S_0001"))),
              new Signal("test_method", new UserTypeKind("Type_test_method"), Some(new Ident("test_method_IDLE")))
            ),
            List(
              new AssignStatement(new Ident("clk_sig"), new Ident("clk")),
              new ProcessStatement(None, None, List()),
              new ProcessStatement(
                Some(List(new Ident("clk"))),
                None,
                List(new IfStatement(
                  new CallExpr(new Ident("rising_edge"), List(new Ident("clk"))),
                  List(
                    new IfStatement(
                      new BinaryExpr("=", new Ident("test_method"), new Ident("test_method_S_0000")),
                      List(new AssignStatement(new Ident("test_busy_sig"), new Constant("'0'"))),
                      List(
                        new IfStatement(
                          new BinaryExpr("=", new Ident("test_method"), new Ident("test_method_S_0001")),
                          List(new AssignStatement(new Ident("test_busy_sig"), new Constant("'1'"))),
                          List(),
                          None
                        )),
                      Some(List(new AssignStatement(new Ident("test_busy_sig"), new Constant("'1'")))),
                    )
                  ),
                  List(),
                  None
                )
                )
              ),
              new InstanceStatement(
                new Ident("inst_u_synthesijer_fsub64_test"),
                new Ident("synthesijer_fsub64"),
                List(
                  new PortMapItem(new Ident("clk"),    new Ident("clk")),
                  new PortMapItem(new Ident("reset"),  new Ident("reset")),
                  new PortMapItem(new Ident("a"),      new Ident("u_synthesijer_fsub64_test_a")),
                  new PortMapItem(new Ident("b"),      new Ident("u_synthesijer_fsub64_test_b")),
                  new PortMapItem(new Ident("nd"),     new Ident("u_synthesijer_fsub64_test_nd")),
                  new PortMapItem(new Ident("result"), new Ident("u_synthesijer_fsub64_test_result")),
                  new PortMapItem(new Ident("valid"),  new Ident("u_synthesijer_fsub64_test_valid"))
                ),
                None
              )
            )
          ))
        )
      )
  }

  "design file (mini)" should "be parsed" in {
    val obj = new VHDLParser()
    obj.parse("""
LIBRARY ieee, work, test0123;
use ieee.std_logic_1164.all;
entity Test is end;
architecture RTL of Test is begin end RTL;
-- hoge hoge
LIBRARY ieee, work, test0123;
use ieee.std_logic_1164.all;
entity Test is end;
architecture RTL of Test is begin end RTL;
""").get should be (
      List(
        new DesignUnit(
          List(
            List(new Library("ieee"), new Library("work"), new Library("test0123")),
            List(new Use("ieee.std_logic_1164.all"))),
          new Entity("Test", None),
          Some(new Architecture("RTL", "Test", List(), List()))
        ),
        new DesignUnit(
          List(
            List(new Library("ieee"), new Library("work"), new Library("test0123")),
            List(new Use("ieee.std_logic_1164.all"))),
          new Entity("Test", None),
          Some(new Architecture("RTL", "Test", List(), List()))
        )
      )
    )
  }


  "for loop" should "be parsed" in {
    val obj = new VHDLParser()
    obj.parseAll(obj.architecture_statement, """
    for i in slv'range loop
      res_v := res_v and slv(i);
    end loop;
""").get should be (
      new ForLoop(
        None,
        new Ident("i"),
        new SymbolRange(new Ident("slv'range")),
        List(
          new BlockingAssignStatement(
            new Ident("res_v"),
            new BinaryExpr("and", new Ident("res_v"), new CallExpr(new Ident("slv"), List(new Ident("i"))))
          ))))
  }

  "for loop with 'for i in 0 to (NUM_OF_PRIMS-1) loop'" should "be parsed" in {
    val obj = new VHDLParser()
    obj.parseAll(obj.architecture_statement, """
    for i in 0 to (NUM_OF_PRIMS-1) loop
      if tmp(i) = '1' then
        result := '1';
      end if;
    end loop;
""").get should be (
      new ForLoop(
        None,
        new Ident("i"),
        new RegionRange("to", new Constant("0"), new BinaryExpr("-", new Ident("NUM_OF_PRIMS"), new Constant("1"))),
        List(
          new IfStatement(
            new BinaryExpr("=", new CallExpr(new Ident("tmp"), List(new Ident("i"))), new Constant("'1'")),
            List(new BlockingAssignStatement(new Ident("result"), new Constant("'1'"))),
            List(),
            None
          ))))
  }

  "function " should "be parsed" in {
    val obj = new VHDLParser()
    obj.parseAll(obj.declarations, """
  function and_reduct(slv : in std_logic_vector) return std_logic is
    variable res_v : std_logic := '1';  -- Null slv vector will also return '1'
  begin
    for i in slv'range loop
      res_v := res_v and slv(i);
    end loop;
    return res_v;
  end function;
""").get should be (
      new FunctionDecl(
        "and_reduct", // name
        List(new PortItem("slv", "in", new VectorKind("std_logic_vector", "", new NoExpr(), new NoExpr()))), // arguments
        new StdLogic(), // return
        List(new Variable("res_v", new StdLogic(), Some(new Constant("'1'")))), // var decl
        List(
          new ForLoop(
            None,
            new Ident("i"),
            new SymbolRange(new Ident("slv'range")),
            List(
              new BlockingAssignStatement(
                new Ident("res_v"),
                new BinaryExpr("and", new Ident("res_v"), new CallExpr(new Ident("slv"), List(new Ident("i"))))
              ))),
          new ReturnStatement(new Ident("res_v"))
        )))
  }


}
