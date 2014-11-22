library IEEE;
use IEEE.std_logic_1164.all;
use IEEE.numeric_std.all;

entity sim000 is
end sim000;

architecture RTL of sim000 is

  signal clk : std_logic := '0';
  signal reset : std_logic := '1';
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

 component Test000
  port (
    clk       : in  std_logic;
    reset     : in  std_logic;
    x_in      : in  signed(32-1 downto 0);
    x_we      : in  std_logic;
    x_out     : out signed(32-1 downto 0);
    ic_in     : in  signed(32-1 downto 0);
    ic_we     : in  std_logic;
    ic_out    : out signed(32-1 downto 0);
    lc_in     : in  signed(64-1 downto 0);
    lc_we     : in  std_logic;
    lc_out    : out signed(64-1 downto 0);
    y_in      : in  signed(64-1 downto 0);
    y_we      : in  std_logic;
    y_out     : out signed(64-1 downto 0);
    test_ib   : in  signed(32-1 downto 0);
    test_ia   : in  signed(32-1 downto 0);
    test_lb   : in  signed(64-1 downto 0);
    test_la   : in  signed(64-1 downto 0);
    test_req  : in  std_logic;
    test_busy : out std_logic
  );
 end component Test000;

 signal req_000 : std_logic := '0';

begin


  tmp_0001 <= counter + 1;
  tmp_0002 <= '1' when counter > 3 else '0';
  tmp_0003 <= '1' when counter < 50 else '0';
  tmp_0004 <= tmp_0002 and tmp_0003;
--  tmp_0005 <= '1' when tmp_0004 = '1' else '0';
  tmp_0005 <= '1' when counter < 50 else '0';

  process
  begin
    -- state main = main_IDLE
    main <= main_S0;
    wait for 20 ns;
    -- state main = main_S0
    main <= main_IDLE;
    wait for 20 ns;
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

 req_000 <= '1' when counter > 100 else '0';
 U000 : Test000
  port map(
    clk       => clk,
    reset     => reset,
    x_in      => (others => '0'),
    x_we      => '0',
    x_out     => open,
    y_in      => (others => '0'),
    y_we      => '0',
    y_out     => open,
    ic_in     => (others => '0'),
    ic_we     => '0',
    ic_out    => open,
    lc_in     => (others => '0'),
    lc_we     => '0',
    lc_out    => open,
    test_ib   => to_signed(3, 32),
    test_ia   => to_signed(100, 32),
    test_lb   => to_signed(3, 64),
    test_la   => to_signed(100, 64),
    test_req  => req_000,
    test_busy => open
  );
  
end RTL;
