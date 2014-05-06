library IEEE;
use IEEE.std_logic_1164.all;
use IEEE.std_logic_arith.all;

entity SIMSIEVE is
end SIMSIEVE;

architecture SIM of SIMSIEVE is

  constant STEP  : time := 10 ns;

  signal clk : std_logic := '0';
  signal reset : std_logic := '0';

  component sievesim
    port (
      clk : IN std_logic;
      reset : IN std_logic
      );
  end component;

begin

  U: sievesim port map(clk, reset);

  process
  begin
    clk <= '1'; wait for STEP/2;
    clk <= '0'; reset <= '1'; wait for STEP/2;
    clk <= '1'; wait for STEP/2;
    clk <= '0'; wait for STEP/2;
    clk <= '1'; wait for STEP/2;
    clk <= '0'; wait for STEP/2;
    clk <= '1'; reset <= '0'; wait for STEP/2;
    clk <= '0'; wait for STEP/2;
    clk <= '1'; wait for STEP/2;
    clk <= '0'; wait for STEP/2;
    while true loop
      clk <= '1'; wait for STEP/2;
      clk <= '0'; wait for STEP/2;
    end loop;
  end process;


end SIM;
