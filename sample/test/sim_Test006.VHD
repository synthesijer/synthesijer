library IEEE;
use IEEE.std_logic_1164.all;
use IEEE.numeric_std.all;

entity sim_Test006 is
end sim_Test006;

architecture RTL of sim_Test006 is

  signal clk : std_logic := '0';
  signal reset : std_logic := '0';
  signal counter : signed(32-1 downto 0) := (others => '0');
  type Type_main is (
    main_IDLE,
    main_S0  
    );
  signal main : Type_main := main_IDLE;
  signal main_delay : signed(32-1 downto 0) := (others => '0');
  signal tmp_0001 : signed(32-1 downto 0) := (others => '0');
  signal tmp_0002 : std_logic := '0';
  signal tmp_0003 : std_logic := '0';
  signal tmp_0004 : std_logic := '0';
  signal tmp_0005 : std_logic := '0';

  component Test006
    port (
      clk : in std_logic;
      reset : in std_logic;
      test_return : out std_logic;
      test_busy : out std_logic;
      test_req : in std_logic
      );
  end component Test006;

  signal run_req, run_busy: std_logic := '0';
  signal test_return : std_logic := '0';

  signal flag : std_logic := '0';
  signal finish_flag : std_logic := '0';
  signal end_of_simulation : std_logic := '0';

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
  U: Test006
    port map(
      clk => clk,
      reset => reset,
      test_return => test_return,
      test_busy => run_busy,
      test_req => run_req
      );

  finish_flag <= '1' when counter > 1000000 else
                 '1' when run_busy = '0' and counter > 105 else
                 '0';
  process(clk)
  begin
   if clk'event and clk = '1' then
     if finish_flag = '1' then
       if(test_return = '1') then
         report "Test006: TEST SUCCESS";
       else
         report "Test006: TEST *** FAILURE ***";
       end if;
       end_of_simulation <= '1';
       --assert (false) report "Simulation End!" severity failure;
     end if;
   end if;
  end process;

end RTL;
