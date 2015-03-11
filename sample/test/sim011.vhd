library IEEE;
use IEEE.std_logic_1164.all;
use IEEE.numeric_std.all;

entity sim011 is
end sim011;

architecture RTL of sim011 is

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

 component Test011
  port (
    clk : in std_logic;
    reset : in std_logic;
    fact_x : in signed(32-1 downto 0);
    fib_n : in signed(32-1 downto 0);
    f_n : in signed(32-1 downto 0);
    g_n : in signed(32-1 downto 0);
    fact_return : out signed(32-1 downto 0);
    fact_busy : out std_logic;
    fact_req : in std_logic;
    fib_return : out signed(32-1 downto 0);
    fib_busy : out std_logic;
    fib_req : in std_logic;
    f_return : out signed(32-1 downto 0);
    f_busy : out std_logic;
    f_req : in std_logic;
    g_return : out signed(32-1 downto 0);
    g_busy : out std_logic;
    g_req : in std_logic
  );
 end component Test011;

 signal run_req, run_busy: std_logic;

 signal req_001 : std_logic := '0';

 signal step : signed(32-1 downto 0);

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

 req_001 <= '1' when counter > 50 else '0';
  
 U: Test011
  port map(
    clk => clk,
    reset => reset,
    fact_x => to_signed(6, 32),
    fib_n => to_signed(40, 32),
    f_n => to_signed(6, 32),
    g_n => to_signed(10, 32),
    fact_return => open,
    fact_busy => open,
    fact_req => req_001,
    fib_return => open,
    fib_busy => open,
    fib_req => req_001,
    f_return => open,
    f_busy => open,
    f_req => run_req,
    g_return => open,
    g_busy => open,
    g_req => run_req
  );

  process(clk)
  begin
    if clk'event and clk = '1' then
      if reset = '1' then
        step <= (others => '0');
      else
        step <= step + 1;
      end if;
    end if;
  end process;

end RTL;
