library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;

entity synthesijer_fconv_f2d is
  port (
    clk    : in  std_logic;
    reset  : in  std_logic;
    a      : in  std_logic_vector(32-1 downto 0);
    nd     : in  std_logic;
    result : out std_logic_vector(64-1 downto 0);
    valid  : out std_logic
    );
end synthesijer_fconv_f2d;

architecture RTL of synthesijer_fconv_f2d is

  component fconv_f2d_ip
    port (
      clock                 : in  std_logic;
      dataa       : in  std_logic_vector(32-1 downto 0);
      result  : out std_logic_vector(64-1 downto 0)
    );
  end component fconv_f2d_ip;

begin

  U: fconv_f2d_ip port map(
    clock                 => clk,
    dataa       => a,
    result  => result
  );

end RTL;
