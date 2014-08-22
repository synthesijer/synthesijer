library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;

entity outputport is
  generic (
    WIDTH : integer := 32
    );
  port (
    clk   : in std_logic;
    reset : in std_logic;

    dout  : out std_logic_vector(WIDTH-1 downto 0);
    value : in  signed(WIDTH-1 downto 0)
    );
end inputport;

architecture RTL of inputport is
begin

  dout <= std_logic_vector(value);
  
end RTL;
