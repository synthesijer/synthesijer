library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;

entity inputport16 is
  generic (
    WIDTH : integer := 16
    );
  port (
    clk   : in std_logic;
    reset : in std_logic;

    din   : in  std_logic_vector(WIDTH-1 downto 0);
    value : out signed(WIDTH-1 downto 0)
    );
end inputport16;

architecture RTL of inputport16 is
begin

  value <= signed(din);
  
end RTL;
