library IEEE;
use IEEE.std_logic_1164.all;
use IEEE.std_logic_arith.all;
use IEEE.std_logic_unsigned.all;

entity blockram is
  
  generic (
    DEPTH : integer := 10;
    WIDTH : integer := 32);

  port (
    clka  : in  std_logic;
    dina  : in  std_logic_VECTOR(WIDTH-1 downto 0);
    addra : in  std_logic_VECTOR(DEPTH-1 downto 0);
    wea   : in  std_logic_VECTOR(0 downto 0);
    clkb  : in  std_logic;
    addrb : in  std_logic_VECTOR(DEPTH-1 downto 0);
    doutb : out std_logic_VECTOR(WIDTH-1 downto 0)
  );

end blockram;

architecture RTL of blockram is

  type ram_type is array (2**DEPTH-1 downto 0) of std_logic_vector (WIDTH-1 downto 0);
  signal RAM: ram_type;

  signal q : std_logic_vector(WIDTH-1 downto 0);

begin  -- RTL

  doutb <= q;

  process (clka)
  begin  -- process
    if clka'event and clka = '1' then  -- rising clock edge
      if wea(0) = '1' then
        RAM(conv_integer(addra)) <= dina;
      end if;
    end if;
  end process;

  process (clkb)
  begin  -- process
    if clkb'event and clkb = '1' then  -- rising clock edge
      q <= RAM(conv_integer(addrb));
    end if;
  end process;

end RTL;
