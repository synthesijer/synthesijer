library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;

entity synthesijer_fconv_i2f is
  port (
    clk    : in  std_logic;
    reset  : in  std_logic;
    a      : in  signed(32-1 downto 0);
    nd     : in  std_logic;
    result : out std_logic_vector(32-1 downto 0);
    valid  : out std_logic
    );
end synthesijer_fconv_i2f;

architecture RTL of synthesijer_fconv_i2f is

  component fconv_i2f_ip
    port (
      aclk                 : in  std_logic;
      s_axis_a_tdata       : in  std_logic_vector(32-1 downto 0);
      s_axis_a_tvalid      : in  std_logic;
      m_axis_result_tvalid : out std_logic;
      m_axis_result_tdata  : out std_logic_vector(32-1 downto 0)
      );
  end component fconv_i2f_ip;

begin

  U: fconv_i2f_ip port map(
    aclk                 => clk,
    s_axis_a_tdata       => std_logic_vector(a),
    s_axis_a_tvalid      => nd,
    m_axis_result_tvalid => valid,
    m_axis_result_tdata  => result
    );
  
end RTL;
