library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;

entity synthesijer_fmul32 is
  port (
    clk    : in  std_logic;
    reset  : in  std_logic;
    a      : in  std_logic_vector(32-1 downto 0);
    b      : in  std_logic_vector(32-1 downto 0);
    nd     : in  std_logic;
    result : out std_logic_vector(32-1 downto 0);
    valid  : out std_logic
    );
end synthesijer_fmul32;

architecture RTL of synthesijer_fmul32 is

  component fmul32_ip
    port (
      clock		: IN STD_LOGIC ;
      dataa		: IN STD_LOGIC_VECTOR (31 DOWNTO 0);
      datab		: IN STD_LOGIC_VECTOR (31 DOWNTO 0);
      result		: OUT STD_LOGIC_VECTOR (31 DOWNTO 0)
    );
  end component fmul32_ip;

begin
  --OK
  process (clk)
  begin
    if nd = '1' then
      valid <= '1' after 1.051 ns;
    end if;
  end process;

  U: fmul32_ip port map(
    clock       => clk,
    dataa       => a,
    datab       => b,
    result      => result
  );

end RTL;
