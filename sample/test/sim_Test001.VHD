library IEEE;
use IEEE.std_logic_1164.all;
use IEEE.numeric_std.all;

entity sim_Test001 is
end sim_Test001;

architecture RTL of sim_Test001 is

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
  signal tmp_0002 : std_logic;
  signal tmp_0003 : std_logic;
  signal tmp_0004 : std_logic;
  signal tmp_0005 : std_logic;

  component Test001
    port (
      clk                : in  std_logic;
      reset              : in  std_logic;
      acc_y              : in  signed(32-1 downto 0);
      add_x              : in  signed(32-1 downto 0);
      add_y              : in  signed(32-1 downto 0);
      add2_x             : in  signed(32-1 downto 0);
      add2_y             : in  signed(32-1 downto 0);
      acc2_num           : in  signed(32-1 downto 0);
      acc2_y             : in  signed(32-1 downto 0);
      acc3_num           : in  signed(32-1 downto 0);
      acc3_y             : in  signed(32-1 downto 0);
      acc4_num           : in  signed(32-1 downto 0);
      acc4_y             : in  signed(32-1 downto 0);
      switch_test_x      : in  signed(32-1 downto 0);
      acc_return         : out signed(32-1 downto 0);
      acc_busy           : out std_logic;
      acc_req            : in  std_logic;
      add_return         : out signed(32-1 downto 0);
      add_busy           : out std_logic;
      add_req            : in  std_logic;
      add2_return        : out signed(32-1 downto 0);
      add2_busy          : out std_logic;
      add2_req           : in  std_logic;
      acc2_return        : out signed(32-1 downto 0);
      acc2_busy          : out std_logic;
      acc2_req           : in  std_logic;
      acc3_return        : out signed(32-1 downto 0);
      acc3_busy          : out std_logic;
      acc3_req           : in  std_logic;
      acc4_return        : out signed(32-1 downto 0);
      acc4_busy          : out std_logic;
      acc4_req           : in  std_logic;
      switch_test_return : out signed(32-1 downto 0);
      switch_test_busy   : out std_logic;
      switch_test_req    : in  std_logic;
      test_return        : out std_logic;
      test_busy          : out std_logic;
      test_req           : in  std_logic
      );
  end component Test001;

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
  
  U: Test001 port map(
      clk                => clk,
      reset              => reset,
      acc_y              => (others => '0'),
      add_x              => (others => '0'),
      add_y              => (others => '0'),
      add2_x             => (others => '0'),
      add2_y             => (others => '0'),
      acc2_num           => (others => '0'),
      acc2_y             => (others => '0'),
      acc3_num           => (others => '0'),
      acc3_y             => (others => '0'),
      acc4_num           => (others => '0'),
      acc4_y             => (others => '0'),
      switch_test_x      => (others => '0'),
      acc_return         => open,
      acc_busy           => open,
      acc_req            => '0',
      add_return         => open,
      add_busy           => open,
      add_req            => '0',
      add2_return        => open,
      add2_busy          => open,
      add2_req           => '0',
      acc2_return        => open,
      acc2_busy          => open,
      acc2_req           => '0',
      acc3_return        => open,
      acc3_busy          => open,
      acc3_req           => '0',
      acc4_return        => open,
      acc4_busy          => open,
      acc4_req           => '0',
      switch_test_return => open,
      switch_test_busy   => open,
      switch_test_req    => '0',
      test_return        => test_return,
      test_busy          => run_busy,
      test_req           => run_req
      );

  finish_flag <= '1' when counter > 10000 else
                 '1' when run_busy = '0' and counter > 105 else
                 '0';
  process(clk)
  begin
   if clk'event and clk = '1' then
     if finish_flag = '1' then
       if(test_return = '1') then
         report "Test001: TEST SUCCESS";
       else
         report "Test001: TEST *** FAILURE ***";
       end if;
       end_of_simulation <= '1';
       --assert (false) report "Simulation End!" severity failure;
     end if;
  end if;
end process;

end RTL;
