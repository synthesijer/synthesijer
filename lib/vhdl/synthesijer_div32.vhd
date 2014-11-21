library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;

entity synthesijer_div32 is
  port (
    clk       : in  std_logic;
    reset     : in  std_logic;
    a         : in  signed(32-1 downto 0);
    b         : in  signed(32-1 downto 0);
    nd        : in  std_logic;
    quantient : out signed(32-1 downto 0);
    remainder : out signed(32-1 downto 0);
    valid     : out std_logic
    );
end synthesijer_div32;

architecture RTL of synthesijer_div32 is

  component div32_ip
    port (
      aclk                   : in  std_logic;
      aresetn                : in  std_logic;
      s_axis_dividend_tdata  : in  std_logic_vector(32-1 downto 0);
      s_axis_dividend_tvalid : in  std_logic;
      s_axis_dividend_tready : out std_logic;
      s_axis_divisor_tdata   : in  std_logic_vector(32-1 downto 0);
      s_axis_divisor_tvalid  : in  std_logic;
      s_axis_divisor_tready  : out std_logic;
      m_axis_dout_tvalid     : out std_logic;
      m_axis_dout_tdata      : out std_logic_vector(64-1 downto 0)
      );
  end component div32_ip;

  signal result_tmp : std_logic_vector(63 downto 0);
  signal valid_tmp : std_logic;

begin

  U : div32_ip port map(
    aclk                   => clk,
    aresetn                => not reset,
    s_axis_dividend_tdata  => std_logic_vector(a),
    s_axis_dividend_tvalid => nd,
    s_axis_dividend_tready => open,
    s_axis_divisor_tdata   => std_logic_vector(b),
    s_axis_divisor_tvalid  => nd,
    s_axis_divisor_tready  => open,
    m_axis_dout_tvalid     => valid_tmp,
    m_axis_dout_tdata      => result_tmp
    );

  valid <= valid_tmp;
  quantient <= signed(result_tmp(63 downto 32));
  remainder <= signed(result_tmp(31 downto 0));
  
end RTL;
