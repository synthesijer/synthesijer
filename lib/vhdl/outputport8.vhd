library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;

entity outputport8 is
  generic (
    WIDTH : integer := 8
    );
  port (
    clk   : in std_logic;
    reset : in std_logic;

    dout  : out std_logic_vector(WIDTH-1 downto 0);
    value : in  signed(WIDTH-1 downto 0)
    );
end outputport8;

architecture RTL of outputport8 is
begin

  dout <= std_logic_vector(value);
  
end RTL;
