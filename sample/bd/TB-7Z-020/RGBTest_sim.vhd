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
      class_obj_0002_class_obj_0000_class_obj_0000_forbid_exp_exp_exp : in std_logic;
      class_obj_0002_class_obj_0000_class_obj_0000_axi_reader_ARADDR_exp_exp_exp : out std_logic_vector(32-1 downto 0);
      class_obj_0002_class_obj_0000_class_obj_0000_axi_reader_ARLEN_exp_exp_exp : out std_logic_vector(8-1 downto 0);
      class_obj_0002_class_obj_0000_class_obj_0000_axi_reader_ARVALID_exp_exp_exp : out std_logic;
      class_obj_0002_class_obj_0000_class_obj_0000_axi_reader_ARREADY_exp_exp_exp : in std_logic;
      class_obj_0002_class_obj_0000_class_obj_0000_axi_reader_ARSIZE_exp_exp_exp : out std_logic_vector(3-1 downto 0);
      class_obj_0002_class_obj_0000_class_obj_0000_axi_reader_ARBURST_exp_exp_exp : out std_logic_vector(2-1 downto 0);
      class_obj_0002_class_obj_0000_class_obj_0000_axi_reader_ARCACHE_exp_exp_exp : out std_logic_vector(4-1 downto 0);
      class_obj_0002_class_obj_0000_class_obj_0000_axi_reader_ARPROT_exp_exp_exp : out std_logic_vector(3-1 downto 0);
      class_obj_0002_class_obj_0000_class_obj_0000_axi_reader_RDATA_exp_exp_exp : in std_logic_vector(32-1 downto 0);
      class_obj_0002_class_obj_0000_class_obj_0000_axi_reader_RRESP_exp_exp_exp : in std_logic_vector(2-1 downto 0);
      class_obj_0002_class_obj_0000_class_obj_0000_axi_reader_RLAST_exp_exp_exp : in std_logic;
      class_obj_0002_class_obj_0000_class_obj_0000_axi_reader_RVALID_exp_exp_exp : in std_logic;
      class_obj_0002_class_obj_0000_class_obj_0000_axi_reader_RREADY_exp_exp_exp : out std_logic;
      class_obj_0002_class_obj_0000_class_obj_0000_axi_writer_AWADDR_exp_exp_exp : out std_logic_vector(32-1 downto 0);
      class_obj_0002_class_obj_0000_class_obj_0000_axi_writer_AWLEN_exp_exp_exp : out std_logic_vector(8-1 downto 0);
      class_obj_0002_class_obj_0000_class_obj_0000_axi_writer_AWVALID_exp_exp_exp : out std_logic;
      class_obj_0002_class_obj_0000_class_obj_0000_axi_writer_AWSIZE_exp_exp_exp : out std_logic_vector(3-1 downto 0);
      class_obj_0002_class_obj_0000_class_obj_0000_axi_writer_AWBURST_exp_exp_exp : out std_logic_vector(2-1 downto 0);
      class_obj_0002_class_obj_0000_class_obj_0000_axi_writer_AWCACHE_exp_exp_exp : out std_logic_vector(4-1 downto 0);
      class_obj_0002_class_obj_0000_class_obj_0000_axi_writer_AWPROT_exp_exp_exp : out std_logic_vector(3-1 downto 0);
      class_obj_0002_class_obj_0000_class_obj_0000_axi_writer_AWREADY_exp_exp_exp : in std_logic;
      class_obj_0002_class_obj_0000_class_obj_0000_axi_writer_WDATA_exp_exp_exp : out std_logic_vector(32-1 downto 0);
      class_obj_0002_class_obj_0000_class_obj_0000_axi_writer_WLAST_exp_exp_exp : out std_logic;
      class_obj_0002_class_obj_0000_class_obj_0000_axi_writer_WVALID_exp_exp_exp : out std_logic;
      class_obj_0002_class_obj_0000_class_obj_0000_axi_writer_WREADY_exp_exp_exp : in std_logic;
      class_obj_0002_class_obj_0000_class_obj_0000_axi_writer_WSTRB_exp_exp_exp : out std_logic_vector(4-1 downto 0);
      class_obj_0002_class_obj_0000_class_obj_0000_axi_writer_BRESP_exp_exp_exp : in std_logic_vector(2-1 downto 0);
      class_obj_0002_class_obj_0000_class_obj_0000_axi_writer_BVALID_exp_exp_exp : in std_logic;
      class_obj_0002_class_obj_0000_class_obj_0000_axi_writer_BREADY_exp_exp_exp : out std_logic;
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
    class_obj_0002_class_obj_0000_class_obj_0000_axi_writer_forbid_exp_exp_exp             => '0',
    class_obj_0002_class_obj_0000_class_obj_0000_axi_writer_axi_reader_ARADDR_exp_exp_exp  => open,
    class_obj_0002_class_obj_0000_class_obj_0000_axi_writer_axi_reader_ARLEN_exp_exp_exp   => open,
    class_obj_0002_class_obj_0000_class_obj_0000_axi_writer_axi_reader_ARVALID_exp_exp_exp => open,
    class_obj_0002_class_obj_0000_class_obj_0000_axi_writer_axi_reader_ARREADY_exp_exp_exp => '1',
    class_obj_0002_class_obj_0000_class_obj_0000_axi_writer_axi_reader_ARSIZE_exp_exp_exp  => open,
    class_obj_0002_class_obj_0000_class_obj_0000_axi_writer_axi_reader_ARBURST_exp_exp_exp => open,
    class_obj_0002_class_obj_0000_class_obj_0000_axi_writer_axi_reader_ARCACHE_exp_exp_exp => open,
    class_obj_0002_class_obj_0000_class_obj_0000_axi_writer_axi_reader_ARPROT_exp_exp_exp  => open,
    class_obj_0002_class_obj_0000_class_obj_0000_axi_writer_axi_reader_RDATA_exp_exp_exp   => X"55555555",
    class_obj_0002_class_obj_0000_class_obj_0000_axi_writer_axi_reader_RRESP_exp_exp_exp   => (others => '0'),
    class_obj_0002_class_obj_0000_class_obj_0000_axi_writer_axi_reader_RLAST_exp_exp_exp   => '1',
    class_obj_0002_class_obj_0000_class_obj_0000_axi_writer_axi_reader_RVALID_exp_exp_exp  => '1',
    class_obj_0002_class_obj_0000_class_obj_0000_axi_writer_axi_reader_RREADY_exp_exp_exp  => open,
    class_obj_0002_class_obj_0000_class_obj_0000_axi_writer_axi_writer_AWADDR_exp_exp_exp  => open,
    class_obj_0002_class_obj_0000_class_obj_0000_axi_writer_axi_writer_AWLEN_exp_exp_exp   => open,
    class_obj_0002_class_obj_0000_class_obj_0000_axi_writer_axi_writer_AWVALID_exp_exp_exp => open,
    class_obj_0002_class_obj_0000_class_obj_0000_axi_writer_axi_writer_AWSIZE_exp_exp_exp  => open,
    class_obj_0002_class_obj_0000_class_obj_0000_axi_writer_axi_writer_AWBURST_exp_exp_exp => open,
    class_obj_0002_class_obj_0000_class_obj_0000_axi_writer_axi_writer_AWCACHE_exp_exp_exp => open,
    class_obj_0002_class_obj_0000_class_obj_0000_axi_writer_axi_writer_AWPROT_exp_exp_exp  => open,
    class_obj_0002_class_obj_0000_class_obj_0000_axi_writer_axi_writer_AWREADY_exp_exp_exp => '1',
    class_obj_0002_class_obj_0000_class_obj_0000_axi_writer_axi_writer_WDATA_exp_exp_exp   => open,
    class_obj_0002_class_obj_0000_class_obj_0000_axi_writer_axi_writer_WLAST_exp_exp_exp   => open,
    class_obj_0002_class_obj_0000_class_obj_0000_axi_writer_axi_writer_WVALID_exp_exp_exp  => open,
    class_obj_0002_class_obj_0000_class_obj_0000_axi_writer_axi_writer_WREADY_exp_exp_exp  => '1',
    class_obj_0002_class_obj_0000_class_obj_0000_axi_writer_axi_writer_WSTRB_exp_exp_exp   => open,
    class_obj_0002_class_obj_0000_class_obj_0000_axi_writer_axi_writer_BRESP_exp_exp_exp   => (others => '0'),
    class_obj_0002_class_obj_0000_class_obj_0000_axi_writer_axi_writer_BVALID_exp_exp_exp  => '0',
    class_obj_0002_class_obj_0000_class_obj_0000_axi_writer_axi_writer_BREADY_exp_exp_exp  => open,
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
