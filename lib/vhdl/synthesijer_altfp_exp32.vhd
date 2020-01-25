library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;

entity synthesijer_altfp_exp32 is
  port (
    clk    : in  std_logic;
    reset  : in  std_logic;
    a      : in  std_logic_vector(32-1 downto 0);
    nd     : in  std_logic;
    result : out std_logic_vector(32-1 downto 0);
    valid  : out std_logic
    );
end synthesijer_altfp_exp32;

architecture RTL of synthesijer_altfp_exp32 is

  component altfp_exp32_ip
    port (
      clock  : in std_logic;
      data   : in  std_logic_vector(32-1 downto 0);
      result : out std_logic_vector(32-1 downto 0)
      );
  end component altfp_exp32_ip;

begin
  process (clk)
  begin
    if nd = '1' then
      valid <= '1' after 1.651 ns;
    end if;
  end process;

  U: altfp_exp32_ip port map(
    clock => clk,
    data  => a,
    result => result
    );

end RTL;
