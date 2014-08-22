library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;

entity outputflag is
  generic (
    WIDTH : integer := 32
    );
  port (
    clk   : in std_logic;
    reset : in std_logic;

    dout : out std_logic;
    flag : in  std_logic
    );
end outputflag;

architecture RTL of outputflag is
begin

  flag <= din;
  
end RTL;
