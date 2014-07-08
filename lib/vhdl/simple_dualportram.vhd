library IEEE;
use ieee.std_logic_1164.all;
use IEEE.numeric_std.all;

entity simple_dualportram is
  
  generic (
    DEPTH : integer := 10;
    WIDTH : integer := 32;
    WORDS : integer := 1024
  );

  port (
    clk      : in  std_logic;
    reset    : in  std_logic;
    we       : in  std_logic;
    length   : out signed(31 downto 0);
    raddress : in  signed(31 downto 0);
    dout     : out signed(WIDTH-1 downto 0);
    waddress : in  signed(31 downto 0);
    din      : in  signed(WIDTH-1 downto 0)
    );

end simple_dualportram;

architecture RTL of simple_dualportram is

  type ram_type is array (WORDS-1 downto 0) of std_logic_vector (WIDTH-1 downto 0);
  signal RAM: ram_type;

  signal q : std_logic_vector(WIDTH-1 downto 0);

begin  -- RTL

  length <= to_signed(WORDS, length'length);
  dout <= signed(q);

  process (clk)
  begin  -- process
    if clk'event and clk = '1' then  -- rising clock edge
      if we = '1' then
        RAM(to_integer(waddress)) <= std_logic_vector(din);
      end if;
    end if;
  end process;

  process (clk)
  begin  -- process
    if clk'event and clk = '1' then  -- rising clock edge
      q <= RAM(to_integer(raddress));
    end if;
  end process;

end RTL;
