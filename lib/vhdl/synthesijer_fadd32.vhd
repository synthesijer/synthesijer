library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;

entity synthesijer_fadd32 is
  port (
    clk    : in  std_logic;
    reset  : in  std_logic;
    a      : in  std_logic_vector(32-1 downto 0);
    b      : in  std_logic_vector(32-1 downto 0);
    nd     : in  std_logic;
    result : out std_logic_vector(32-1 downto 0);
    valid  : out std_logic
    );
end synthesijer_fadd32;

architecture RTL of synthesijer_fadd32 is

  component fadd32_ip
    port (
      clock                 : in  std_logic;
      dataa       : in  std_logic_vector(32-1 downto 0);
      datab       : in  std_logic_vector(32-1 downto 0);
      result  : out std_logic_vector(32-1 downto 0)
    );
  end component fadd32_ip;

begin
  process (clk)
  begin
    if nd = '1' then
      valid <= '1' after 1.351 ns;
    end if;
  end process;

  U: fadd32_ip port map(
    clock                 => clk,
    dataa       => a,
    datab       => b,
    result => result
  );

end RTL;
