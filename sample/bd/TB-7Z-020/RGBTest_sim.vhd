library IEEE;
use IEEE.STD_LOGIC_1164.ALL;
use ieee.numeric_std.all;

entity RGBTest_sim is
end RGBTest_sim;

architecture RTL of RGBTest_sim is

component RGBTest
  port (
    clk : in std_logic;
    reset : in std_logic;
    obj_obj_obj_forbid : in std_logic;
    obj_obj_obj_axi_reader_ARADDR : out std_logic_vector(32-1 downto 0);
    obj_obj_obj_axi_reader_ARLEN : out std_logic_vector(8-1 downto 0);
    obj_obj_obj_axi_reader_ARVALID : out std_logic;
    obj_obj_obj_axi_reader_ARREADY : in std_logic;
    obj_obj_obj_axi_reader_ARSIZE : out std_logic_vector(3-1 downto 0);
    obj_obj_obj_axi_reader_ARBURST : out std_logic_vector(2-1 downto 0);
    obj_obj_obj_axi_reader_ARCACHE : out std_logic_vector(4-1 downto 0);
    obj_obj_obj_axi_reader_ARPROT : out std_logic_vector(3-1 downto 0);
    obj_obj_obj_axi_reader_RDATA : in std_logic_vector(32-1 downto 0);
    obj_obj_obj_axi_reader_RRESP : in std_logic_vector(2-1 downto 0);
    obj_obj_obj_axi_reader_RLAST : in std_logic;
    obj_obj_obj_axi_reader_RVALID : in std_logic;
    obj_obj_obj_axi_reader_RREADY : out std_logic;
    obj_obj_obj_axi_writer_AWADDR : out std_logic_vector(32-1 downto 0);
    obj_obj_obj_axi_writer_AWLEN : out std_logic_vector(8-1 downto 0);
    obj_obj_obj_axi_writer_AWVALID : out std_logic;
    obj_obj_obj_axi_writer_AWSIZE : out std_logic_vector(3-1 downto 0);
    obj_obj_obj_axi_writer_AWBURST : out std_logic_vector(2-1 downto 0);
    obj_obj_obj_axi_writer_AWCACHE : out std_logic_vector(4-1 downto 0);
    obj_obj_obj_axi_writer_AWPROT : out std_logic_vector(3-1 downto 0);
    obj_obj_obj_axi_writer_AWREADY : in std_logic;
    obj_obj_obj_axi_writer_WDATA : out std_logic_vector(32-1 downto 0);
    obj_obj_obj_axi_writer_WLAST : out std_logic;
    obj_obj_obj_axi_writer_WVALID : out std_logic;
    obj_obj_obj_axi_writer_WREADY : in std_logic;
    obj_obj_obj_axi_writer_WSTRB : out std_logic_vector(4-1 downto 0);
    obj_obj_obj_axi_writer_BRESP : in std_logic_vector(2-1 downto 0);
    obj_obj_obj_axi_writer_BVALID : in std_logic;
    obj_obj_obj_axi_writer_BREADY : out std_logic;
    run_req : in std_logic;
    run_busy : out std_logic
  );
end component RGBTest;

  signal clk : std_logic := '0';
  signal reset : std_logic := '0';
  signal counter : signed(32-1 downto 0) := (others => '0');
  type Type_main is (
    main_IDLE,
    S0  
  );
  signal main : Type_main := main_IDLE;
  signal main_delay : signed(32-1 downto 0) := (others => '0');
  signal tmp_0001 : signed(32-1 downto 0);
  signal tmp_0002 : std_logic;
  signal tmp_0003 : std_logic;
  signal tmp_0004 : std_logic;
  signal tmp_0005 : std_logic;

begin

U : RGBTest
  port map(
    clk                        => clk,
    reset                      => reset,
    obj_obj_obj_forbid             => '0',
    obj_obj_obj_axi_reader_ARADDR  => open,
    obj_obj_obj_axi_reader_ARLEN   => open,
    obj_obj_obj_axi_reader_ARVALID => open,
    obj_obj_obj_axi_reader_ARREADY => '1',
    obj_obj_obj_axi_reader_ARSIZE  => open,
    obj_obj_obj_axi_reader_ARBURST => open,
    obj_obj_obj_axi_reader_ARCACHE => open,
    obj_obj_obj_axi_reader_ARPROT  => open,
    obj_obj_obj_axi_reader_RDATA   => X"55555555",
    obj_obj_obj_axi_reader_RRESP   => (others => '0'),
    obj_obj_obj_axi_reader_RLAST   => '1',
    obj_obj_obj_axi_reader_RVALID  => '1',
    obj_obj_obj_axi_reader_RREADY  => open,
    obj_obj_obj_axi_writer_AWADDR  => open,
    obj_obj_obj_axi_writer_AWLEN   => open,
    obj_obj_obj_axi_writer_AWVALID => open,
    obj_obj_obj_axi_writer_AWSIZE  => open,
    obj_obj_obj_axi_writer_AWBURST => open,
    obj_obj_obj_axi_writer_AWCACHE => open,
    obj_obj_obj_axi_writer_AWPROT  => open,
    obj_obj_obj_axi_writer_AWREADY => '1',
    obj_obj_obj_axi_writer_WDATA   => open,
    obj_obj_obj_axi_writer_WLAST   => open,
    obj_obj_obj_axi_writer_WVALID  => open,
    obj_obj_obj_axi_writer_WREADY  => '1',
    obj_obj_obj_axi_writer_WSTRB   => open,
    obj_obj_obj_axi_writer_BRESP   => (others => '0'),
    obj_obj_obj_axi_writer_BVALID  => '0',
    obj_obj_obj_axi_writer_BREADY  => open,
    run_req                   => '1',
    run_busy                  => open
  );
  
  tmp_0001 <= counter + 1;
  tmp_0002 <= '1' when counter > 3 else '0';
  tmp_0003 <= '1' when counter < 8 else '0';
  tmp_0004 <= tmp_0002 and tmp_0003;
  tmp_0005 <= '1' when tmp_0004 = '1' else '0';

  process
  begin
    -- state main = main_IDLE
    main <= S0;
    wait for 10 ns;
    -- state main = S0
    main <= main_IDLE;
    wait for 10 ns;
  end process;


  process(main)
  begin
    if main = main_IDLE then
      clk <= '0';
    elsif main = S0 then
      clk <= '1';
    end if;
  end process;

  process(main)
  begin
    reset <= tmp_0005;
  end process;

  process(main)
  begin
    if main = main_IDLE then
      counter <= tmp_0001;
    elsif main = S0 then
      counter <= tmp_0001;
    end if;
  end process;

end RTL;
