library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;

entity synthesijer_mul64 is
  port (
    clk    : in  std_logic;
    reset  : in  std_logic;
    a      : in  signed(64-1 downto 0);
    b      : in  signed(64-1 downto 0);
    nd     : in  std_logic;
    result : out signed(64-1 downto 0);
    valid  : out std_logic
    );
end synthesijer_mul64;

architecture RTL of synthesijer_mul64 is

  signal tmp : signed(127 downto 0);

begin

  tmp <= a * b;
  result <= tmp(63 downto 0);
  valid <= '1';

end RTL;
