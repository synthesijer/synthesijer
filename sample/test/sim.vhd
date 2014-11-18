library IEEE;
use IEEE.std_logic_1164.all;
use IEEE.numeric_std.all;

entity sim is
end sim;

architecture RTL of sim is

  signal clk : std_logic := '0';
  signal reset : std_logic := '0';
  signal counter : signed(32-1 downto 0) := (others => '0');
  type Type_main is (
    main_IDLE,
    main_S0  
  );
  signal main : Type_main := main_IDLE;
  signal main_delay : signed(32-1 downto 0) := (others => '0');
  signal tmp_0001 : signed(32-1 downto 0);
  signal tmp_0002 : std_logic;
  signal tmp_0003 : std_logic;
  signal tmp_0004 : std_logic;
  signal tmp_0005 : std_logic;

 component Test005
  port (
    clk : in std_logic;
    reset : in std_logic;
    run_req : in std_logic;
    run_busy : out std_logic
  );
 end component Test005;

 component Test001Sim
  port (
    clk : in std_logic;
    reset : in std_logic;
    main_req : in std_logic;
    main_busy : out std_logic
  );
 end component Test001Sim;

 component Test003
  port (
    clk : in std_logic;
    reset : in std_logic;
    test_req : in std_logic;
    test_busy : out std_logic
  );
 end component Test003;

 component Test006
  port (
    clk : in std_logic;
    reset : in std_logic;
    test_req : in std_logic;
    test_busy : out std_logic
  );
 end component Test006;

 signal run_req, run_busy: std_logic;

 signal req_001 : std_logic := '0';

begin


  tmp_0001 <= counter + 1;
  tmp_0002 <= '1' when counter > 3 else '0';
  tmp_0003 <= '1' when counter < 8 else '0';
  tmp_0004 <= tmp_0002 and tmp_0003;
  tmp_0005 <= '1' when tmp_0004 = '1' else '0';

  process
  begin
    -- state main = main_IDLE
    main <= main_S0;
    wait for 10 ns;
    -- state main = main_S0
    main <= main_IDLE;
    wait for 10 ns;
  end process;


  process(main)
  begin
    if main = main_IDLE then
      clk <= '0';
    elsif main = main_S0 then
      clk <= '1';
    end if;
  end process;

  process(main)
  begin
    reset <= tmp_0005;
  end process;

  process(main)
  begin
    if main = main_IDLE then
      counter <= tmp_0001;
    elsif main = main_S0 then
      counter <= tmp_0001;
    end if;
  end process;

 run_req <= '1' when counter = 100 else '0';
 U: Test005
  port map(
    clk => clk,
    reset => reset,
    run_req => run_req,
    run_busy => run_busy
  );

 req_001 <= '1' when counter > 50 else '0';
 U001: Test001Sim
  port map(
    clk => clk,
    reset => reset,
    main_req => req_001,
    main_busy => open
  );

 U003: Test003
  port map(
    clk => clk,
    reset => reset,
    test_req => '1',
    test_busy => open
  );

 U004: Test006
  port map(
    clk => clk,
    reset => reset,
    test_req => '1',
    test_busy => open
  );

end RTL;
