library IEEE;
use IEEE.std_logic_1164.all;
use IEEE.numeric_std.all;

entity sim015 is
end sim015;

architecture RTL of sim015 is

  signal clk     : std_logic             := '0';
  signal reset   : std_logic             := '0';
  signal counter : signed(32-1 downto 0) := (others => '0');

  component Test015
    port (
      clk : in std_logic;
      reset : in std_logic;
      v_in : in signed(32-1 downto 0);
      v_we : in std_logic;
      v_out : out signed(32-1 downto 0);
      v1_in : in signed(32-1 downto 0);
      v1_we : in std_logic;
      v1_out : out signed(32-1 downto 0);
      test_busy : out std_logic;
      test_req : in std_logic
      );
  end component Test015;
  
  signal req : std_logic := '0';

begin

  process
  begin
    clk <= '0';
    wait for 10 ns;
    clk <= '1';
    wait for 10 ns;
  end process;

  process(clk)
  begin
    if clk'event and clk = '1' then
      counter <= counter + 1;
    end if;
    if counter = 5 then
      reset <= '1';
    elsif counter = 10 then
      reset <= '0';
    end if;
    if counter = 20 then
      req <= '1';
    else
      req <= '0';
    end if;
  end process;

  U : Test015 port map(
    clk       => clk,
    reset     => reset,
    v_in      => (others => '0'),
    v_we      => '0',
    v_out     => open,
    v1_in      => (others => '0'),
    v1_we      => '0',
    v1_out     => open,
    test_busy => open,
    test_req  => req
    );
  
end RTL;
