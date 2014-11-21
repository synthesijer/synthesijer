library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;

entity synthesijer_logic_rshift64 is
  port (
    clk    : in  std_logic;
    reset  : in  std_logic;
    a      : in  signed(64-1 downto 0);
    b      : in  signed(64-1 downto 0);
    nd     : in  std_logic;
    result : out signed(64-1 downto 0);
    valid  : out std_logic
    );
end synthesijer_logic_rshift64;

architecture RTL of synthesijer_logic_rshift64 is

  signal result_tmp : unsigned(63 downto 0);

begin

  result_tmp <= shift_right(unsigned(a), to_integer(unsigned(b(5 downto 0))));
  result <= signed(result_tmp);
  valid <= '1';

end RTL;
