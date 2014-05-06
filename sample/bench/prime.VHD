library IEEE;
use IEEE.std_logic_1164.all;
use IEEE.std_logic_arith.all;

entity BENCH is
end BENCH;

architecture SIM of BENCH is

  constant STEP  : time := 10 ns;

  signal clk : std_logic := '0';
  signal reset : std_logic := '0';

component primesim
  port (
    notify_method_busy : OUT std_logic;
    notify_method_request : IN std_logic;
    clk : IN std_logic;
    reset : IN std_logic
  );
end component;

begin

  U: primesim port map(
      notify_method_busy => open,
      notify_method_request => '0',
      clk => clk,
      reset => reset
  );

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
