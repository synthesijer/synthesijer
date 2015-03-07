--
--  RS232C_CLK
--
library ieee;
use ieee.std_logic_1164.ALL;
use ieee.numeric_std.all;

entity clk_div is
  port(
    clk     : in  std_logic;
    rst     : in  std_logic;
    div     : in  std_logic_vector(15 downto 0);
    clk_out : out std_logic
    );
end clk_div;

architecture RTL of clk_div is

  signal counter : unsigned(15 downto 0) := (others => '0');

begin

  process(clk)
  begin
    if(clk'event and clk = '1') then
      if(rst = '1') then
        counter <= (others => '0');
      elsif (counter = unsigned(div)) then
        counter <= (others => '0');
        clk_out <= '1';
      else
        counter <= counter + 1;
        clk_out  <= '0';
      end if;
    end if; 
  end process;

end RTL;
