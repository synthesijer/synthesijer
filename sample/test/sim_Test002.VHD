library IEEE;
use IEEE.std_logic_1164.all;
use IEEE.numeric_std.all;

entity sim_Test002 is
end sim_Test002;

architecture RTL of sim_Test002 is

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

  component Test002
    port (
      clk : in std_logic;
      reset : in std_logic;
      a_address : in signed(32-1 downto 0);
      a_we : in std_logic;
      a_oe : in std_logic;
      a_din : in signed(32-1 downto 0);
      a_dout : out signed(32-1 downto 0);
      a_length : out signed(32-1 downto 0);
      x_in : in signed(32-1 downto 0);
      x_we : in std_logic;
      x_out : out signed(32-1 downto 0);
      y_in : in signed(32-1 downto 0);
      y_we : in std_logic;
      y_out : out signed(32-1 downto 0);
      dec_i : in signed(32-1 downto 0);
      inc_i : in signed(32-1 downto 0);
      copy_i : in signed(32-1 downto 0);
      copy_j : in signed(32-1 downto 0);
      set_i : in signed(32-1 downto 0);
      set_v : in signed(32-1 downto 0);
      get_i : in signed(32-1 downto 0);
      switch_test_x : in signed(32-1 downto 0);
      init_busy : out std_logic;
      init_req : in std_logic;
      dec_return : out signed(32-1 downto 0);
      dec_busy : out std_logic;
      dec_req : in std_logic;
      inc_return : out signed(32-1 downto 0);
      inc_busy : out std_logic;
      inc_req : in std_logic;
      copy_busy : out std_logic;
      copy_req : in std_logic;
      set_busy : out std_logic;
      set_req : in std_logic;
      get_return : out signed(32-1 downto 0);
      get_busy : out std_logic;
      get_req : in std_logic;
      switch_test_return : out signed(32-1 downto 0);
      switch_test_busy : out std_logic;
      switch_test_req : in std_logic;
      sum_x_y_return : out signed(32-1 downto 0);
      sum_x_y_busy : out std_logic;
      sum_x_y_req : in std_logic;
      test_return : out std_logic;
      test_busy : out std_logic;
      test_req : in std_logic
      );
  end component Test002;

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
  
  U : Test002
    port map(
      clk                => clk,
      reset              => reset,
      a_address          => (others => '0'),
      a_we               => '0',
      a_oe               => '0',
      a_din              => (others => '0'),
      a_dout             => open,
      a_length           => open,
      x_in               => (others => '0'),
      x_we               => '0',
      x_out              => open,
      y_in               => (others => '0'),
      y_we               => '0',
      y_out              => open,
      dec_i              => (others => '0'),
      inc_i              => (others => '0'),
      copy_i             => (others => '0'),
      copy_j             => (others => '0'),
      set_i              => (others => '0'),
      set_v              => (others => '0'),
      get_i              => (others => '0'),
      switch_test_x      => (others => '0'),
      init_busy          => open,
      init_req           => '0',
      dec_return         => open,
      dec_busy           => open,
      dec_req            => '0',
      inc_return         => open,
      inc_busy           => open,
      inc_req            => '0',
      copy_busy          => open,
      copy_req           => '0',
      set_busy           => open,
      set_req            => '0',
      get_return         => open,
      get_busy           => open,
      get_req            => '0',
      switch_test_return => open,
      switch_test_busy   => open,
      switch_test_req    => '0',
      sum_x_y_return     => open,
      sum_x_y_busy       => open,
      sum_x_y_req        => '0',
      test_return        => test_return,
      test_busy          => run_busy,
      test_req           => run_req
      );

  finish_flag <= '1' when counter > 50000 else
                 '1' when run_busy = '0' and counter > 105 else
                 '0';
  process(clk)
  begin
   if clk'event and clk = '1' then
     if finish_flag = '1' then
       if(test_return = '1') then
         report "Test002: TEST SUCCESS";
       else
         report "Test002: TEST *** FAILURE ***";
         assert (false) report "Simulation End!" severity failure;
       end if;
       end_of_simulation <= '1';
       --assert (false) report "Simulation End!" severity failure;
     end if;
  end if;
end process;

end RTL;
