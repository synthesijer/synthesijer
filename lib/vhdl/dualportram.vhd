library IEEE;
use ieee.std_logic_1164.all;
use IEEE.numeric_std.all;

entity dualportram is
  
  generic (
    DEPTH : integer := 7;
    WIDTH : integer := 32
    );
  
  port (
    address_a : in std_logic_vector(DEPTH-1 downto 0);
    address_b : in std_logic_vector(DEPTH-1 downto 0);
    clock : in std_logic;
    data_a : in std_logic_vector(WIDTH-1 downto 0);
    data_b : in std_logic_vector(WIDTH-1 downto 0);
    wren_a : in std_logic;
    wren_b : in std_logic;
    q_a : out std_logic_vector(WIDTH-1 downto 0);
    q_b : out std_logic_vector(WIDTH-1 downto 0)
    );
  
end dualportram;

architecture RTL of dualportram is

  type t_mem is array (0 to 2**DEPTH-1) of std_logic_vector(WIDTH-1 downto 0);
  shared variable mem: t_mem := (others => std_logic_vector(to_unsigned(0, WIDTH)));  -- the memory storage

begin  -- RTL

  process (clock)
  begin
    if clock'event and clock = '1' then
      if wren_a = '1' then
        mem(to_integer(unsigned(address_a))) := data_a;
      end if;
      if wren_b = '1' then
        mem(to_integer(unsigned(address_b))) := data_b;
      end if;
      q_a <= mem(to_integer(unsigned(address_a)));
      q_b <= mem(to_integer(unsigned(address_b)));
    end if;      
  end process;

end RTL;
