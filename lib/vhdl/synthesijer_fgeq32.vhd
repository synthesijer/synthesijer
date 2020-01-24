library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;

entity synthesijer_fgeq32 is
  port (
    clk    : in  std_logic;
    reset  : in  std_logic;
    a      : in  std_logic_vector(32-1 downto 0);
    b      : in  std_logic_vector(32-1 downto 0);
    nd     : in  std_logic;
    result : out std_logic;
    valid  : out std_logic
    );
end synthesijer_fgeq32;

architecture RTL of synthesijer_fgeq32 is

  component fgeq32_ip
    port (
      clock                    : in  std_logic;
      dataa          : in  std_logic_vector(32-1 downto 0);
      datab          : in  std_logic_vector(32-1 downto 0);
      ageb    : out std_logic
    );
  end component fgeq32_ip;

  signal result_tmp : std_logic_vector(7 downto 0);

begin
  result <= result_tmp(0);
  process (clk)
  begin
    if nd = '1' then
      valid <= '1' after 0.251 ns;
    end if;
  end process;

  U: fgeq32_ip port map(
    clock                 => clk,
    dataa       => a,
    datab       => b,
    ageb => result_tmp(0)
  );
  
  result <= result_tmp(0);

end RTL;
