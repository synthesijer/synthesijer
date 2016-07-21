library IEEE;
use IEEE.std_logic_1164.all;
use IEEE.numeric_std.all;

entity bubblesortsim_top is
end bubblesortsim_top;

architecture RTL of bubblesortsim_top is

  signal clk : std_logic := '0';
  signal reset : std_logic := '0';
  signal counter : unsigned(32-1 downto 0) := (others => '0');
  type Type_main is (
    main_IDLE,
    main_S0  
  );
  signal main : Type_main := main_IDLE;
  signal main_delay : signed(32-1 downto 0) := (others => '0');
  signal tmp_0001 : unsigned(32-1 downto 0) := (others => '0');
  signal tmp_0002 : std_logic;
  signal tmp_0003 : std_logic;
  signal tmp_0004 : std_logic;
  signal tmp_0005 : std_logic;

  component BubbleSortSim
    port (
      clk : in std_logic;
      reset : in std_logic;
      finish_flag_out : out std_logic;
      finish_flag_in : in std_logic;
      finish_flag_we : in std_logic;
      run_req : in std_logic;
      run_busy : out std_logic;
      test_return : out std_logic;
      test_busy : out std_logic;
      test_req : in std_logic;
      start_req : in std_logic;
      start_busy : out std_logic;
      join_req : in std_logic;
      join_busy : out std_logic;
      yield_req : in std_logic;
      yield_busy : out std_logic
      );
  end component BubbleSortSim;

  signal finish_flag       : std_logic := '0';
  signal test_return       : std_logic := '0';
  signal run_busy          : std_logic := '0';
  signal run_req           : std_logic := '0';
  signal end_of_simulation : std_logic := '0';

begin

  U: BubbleSortSim port map(
    clk => clk,
    reset => reset,
    finish_flag_in => '0',
    finish_flag_we => '0',
    finish_flag_out => open,
    run_req => '0',
    run_busy => open,
    test_return => test_return,
    test_busy => run_busy,
    test_req => run_req,
    start_req => '0',
    start_busy => open,
    join_req => '0',
    join_busy => open,
    yield_req => '0',
    yield_busy => open
  );

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
    if end_of_simulation = '1' then
      wait;
    end if;
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

  run_req <= '1' when counter > 100 else '0';

  finish_flag <= '1' when counter > 400000000 else
                 '1' when run_busy = '0' and counter > 105 else
                 '0';
  process(clk)
  begin
   if clk'event and clk = '1' then
     if finish_flag = '1' then
       if(test_return = '1') then
         report "BubbleSortSim: TEST SUCCESS";
       else
         report "BubbleSortSim: TEST *** FAILURE ***";
       end if;
       end_of_simulation <= '1';
       --assert (false) report "Simulation End!" severity failure;
     end if;
   end if;
  end process;

end RTL;
