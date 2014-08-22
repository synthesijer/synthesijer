library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;

entity inputflag is
  generic (
    WIDTH : integer := 32
    );
  port (
    clk   : in std_logic;
    reset : in std_logic;

    din  : in  std_logic;
    flag : out std_logic
    );
end inputflag;

architecture RTL of inputflag is
begin

  flag <= din;
  
end RTL;
