library IEEE;
use IEEE.STD_LOGIC_1164.ALL;
use ieee.numeric_std.all;

entity RGBTest_sim is
end RGBTest_sim;

architecture RTL of RGBTest_sim is

  component RGBTest
    port (
      clk                                : in  std_logic;
      reset                              : in  std_logic;
      obj_obj_obj_forbid_exp             : in  std_logic;
      obj_obj_obj_axi_reader_ARADDR_exp  : out std_logic_vector(32-1 downto 0);
      obj_obj_obj_axi_reader_ARLEN_exp   : out std_logic_vector(8-1 downto 0);
      obj_obj_obj_axi_reader_ARVALID_exp : out std_logic;
      obj_obj_obj_axi_reader_ARREADY_exp : in  std_logic;
      obj_obj_obj_axi_reader_ARSIZE_exp  : out std_logic_vector(3-1 downto 0);
      obj_obj_obj_axi_reader_ARBURST_exp : out std_logic_vector(2-1 downto 0);
      obj_obj_obj_axi_reader_ARCACHE_exp : out std_logic_vector(4-1 downto 0);
      obj_obj_obj_axi_reader_ARPROT_exp  : out std_logic_vector(3-1 downto 0);
      obj_obj_obj_axi_reader_RDATA_exp   : in  std_logic_vector(32-1 downto 0);
      obj_obj_obj_axi_reader_RRESP_exp   : in  std_logic_vector(2-1 downto 0);
      obj_obj_obj_axi_reader_RLAST_exp   : in  std_logic;
      obj_obj_obj_axi_reader_RVALID_exp  : in  std_logic;
      obj_obj_obj_axi_reader_RREADY_exp  : out std_logic;
      obj_obj_obj_axi_writer_AWADDR_exp  : out std_logic_vector(32-1 downto 0);
      obj_obj_obj_axi_writer_AWLEN_exp   : out std_logic_vector(8-1 downto 0);
      obj_obj_obj_axi_writer_AWVALID_exp : out std_logic;
      obj_obj_obj_axi_writer_AWSIZE_exp  : out std_logic_vector(3-1 downto 0);
      obj_obj_obj_axi_writer_AWBURST_exp : out std_logic_vector(2-1 downto 0);
      obj_obj_obj_axi_writer_AWCACHE_exp : out std_logic_vector(4-1 downto 0);
      obj_obj_obj_axi_writer_AWPROT_exp  : out std_logic_vector(3-1 downto 0);
      obj_obj_obj_axi_writer_AWREADY_exp : in  std_logic;
      obj_obj_obj_axi_writer_WDATA_exp   : out std_logic_vector(32-1 downto 0);
      obj_obj_obj_axi_writer_WLAST_exp   : out std_logic;
      obj_obj_obj_axi_writer_WVALID_exp  : out std_logic;
      obj_obj_obj_axi_writer_WREADY_exp  : in  std_logic;
      obj_obj_obj_axi_writer_WSTRB_exp   : out std_logic_vector(4-1 downto 0);
      obj_obj_obj_axi_writer_BRESP_exp   : in  std_logic_vector(2-1 downto 0);
      obj_obj_obj_axi_writer_BVALID_exp  : in  std_logic;
      obj_obj_obj_axi_writer_BREADY_exp  : out std_logic;
      run_req                            : in  std_logic;
      run_busy                           : out std_logic
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
    obj_obj_obj_forbid_exp => '0',
    obj_obj_obj_axi_reader_ARADDR_exp  => open,
    obj_obj_obj_axi_reader_ARLEN_exp   => open,
    obj_obj_obj_axi_reader_ARVALID_exp => open,
    obj_obj_obj_axi_reader_ARREADY_exp => '1',
    obj_obj_obj_axi_reader_ARSIZE_exp  => open,
    obj_obj_obj_axi_reader_ARBURST_exp => open,
    obj_obj_obj_axi_reader_ARCACHE_exp => open,
    obj_obj_obj_axi_reader_ARPROT_exp  => open,
    obj_obj_obj_axi_reader_RDATA_exp   => X"55555555",
    obj_obj_obj_axi_reader_RRESP_exp   => (others => '0'),
    obj_obj_obj_axi_reader_RLAST_exp   => '1',
    obj_obj_obj_axi_reader_RVALID_exp  => '1',
    obj_obj_obj_axi_reader_RREADY_exp  => open,
    obj_obj_obj_axi_writer_AWADDR_exp  => open,
    obj_obj_obj_axi_writer_AWLEN_exp   => open,
    obj_obj_obj_axi_writer_AWVALID_exp => open,
    obj_obj_obj_axi_writer_AWSIZE_exp  => open,
    obj_obj_obj_axi_writer_AWBURST_exp => open,
    obj_obj_obj_axi_writer_AWCACHE_exp => open,
    obj_obj_obj_axi_writer_AWPROT_exp  => open,
    obj_obj_obj_axi_writer_AWREADY_exp => '1',
    obj_obj_obj_axi_writer_WDATA_exp   => open,
    obj_obj_obj_axi_writer_WLAST_exp   => open,
    obj_obj_obj_axi_writer_WVALID_exp  => open,
    obj_obj_obj_axi_writer_WREADY_exp  => '1',
    obj_obj_obj_axi_writer_WSTRB_exp   => open,
    obj_obj_obj_axi_writer_BRESP_exp   => (others => '0'),
    obj_obj_obj_axi_writer_BVALID_exp  => '0',
    obj_obj_obj_axi_writer_BREADY_exp  => open,
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
