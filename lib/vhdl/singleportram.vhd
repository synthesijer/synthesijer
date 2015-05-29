library IEEE;
use ieee.std_logic_1164.all;
use IEEE.numeric_std.all;

entity singleportram is
  
  generic (
    DEPTH : integer := 10;
    WIDTH : integer := 32;
    WORDS : integer := 1024
  );

  port (
    clk       : in  std_logic;
    reset     : in  std_logic;
    we_b      : in  std_logic;
    oe_b      : in  std_logic;
    length    : out signed(31 downto 0);
    address_b : in  signed(31 downto 0);
    dout_b    : out signed(WIDTH-1 downto 0);
    din_b     : in  signed(WIDTH-1 downto 0)
    );

end singleportram;

architecture RTL of singleportram is

  type ram_type is array (WORDS-1 downto 0) of std_logic_vector (WIDTH-1 downto 0);
  signal RAM: ram_type := (others => (others => '0'));

  attribute ram_style : string;
  attribute ram_style of RAM : signal is "block";

  signal q : std_logic_vector(WIDTH-1 downto 0) := (others => '0');

begin  -- RTL

  length <= to_signed(WORDS, length'length);
  dout_b <= signed(q);

  process (clk)
  begin  -- process
    if clk'event and clk = '1' then  -- rising clock edge
      if we_b = '1' then
        RAM(to_integer(unsigned(address_b(DEPTH-1 downto 0)))) <= std_logic_vector(din_b);
      end if;
    end if;
  end process;

  process (clk)
  begin  -- process
    if clk'event and clk = '1' then  -- rising clock edge
      q <= RAM(to_integer(unsigned(address_b(DEPTH-1 downto 0))));
    end if;
  end process;

end RTL;
