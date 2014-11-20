library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;

entity synthesijer_fconv_d2l is
  port (
    clk    : in  std_logic;
    reset  : in  std_logic;
    a      : in  std_logic_vector(64-1 downto 0);
    nd     : in  std_logic;
    result : out signed(64-1 downto 0);
    valid  : out std_logic
    );
end synthesijer_fconv_d2l;

architecture RTL of synthesijer_fconv_d2l is

  component fconv_d2l_ip
    port (
      aclk                 : in  std_logic;
      s_axis_a_tdata       : in  std_logic_vector(64-1 downto 0);
      s_axis_a_tvalid      : in  std_logic;
      m_axis_result_tvalid : out std_logic;
      m_axis_result_tdata  : out std_logic_vector(64-1 downto 0)
      );
  end component fconv_d2l_ip;

  signal result_tmp : std_logic_vector(63 downto 0);

begin

  U: fconv_d2l_ip port map(
    aclk                 => clk,
    s_axis_a_tdata       => a,
    s_axis_a_tvalid      => nd,
    m_axis_result_tvalid => valid,
    m_axis_result_tdata  => result_tmp
    );

  result <= signed(result_tmp);
  
end RTL;
