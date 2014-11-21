library IEEE;
use IEEE.std_logic_1164.all;
use IEEE.numeric_std.all;

entity Test000_dummy is
  port (
    clk : in std_logic;
    reset : in std_logic;
    isel : in std_logic_vector(3 downto 0);
    osel : in std_logic_vector(3 downto 0);
    data_in : in signed(64-1 downto 0);
    data_out : out signed(64-1 downto 0);
    ic_we : in std_logic;
    lc_we : in std_logic;
    test_req : in std_logic;
    test_busy : out std_logic
  );
end Test000_dummy;

architecture RTL of Test000_dummy is
 component Test000
  port (
    clk : in std_logic;
    reset : in std_logic;
    ic_in : in signed(32-1 downto 0);
    ic_we : in std_logic;
    ic_out : out signed(32-1 downto 0);
    lc_in : in signed(64-1 downto 0);
    lc_we : in std_logic;
    lc_out : out signed(64-1 downto 0);
    test_ib : in signed(32-1 downto 0);
    test_ia : in signed(32-1 downto 0);
    test_lb : in signed(64-1 downto 0);
    test_la : in signed(64-1 downto 0);
    test_req : in std_logic;
    test_busy : out std_logic
  );
 end component Test000;
    signal ic_in : signed(32-1 downto 0);
    signal ic_out : signed(32-1 downto 0);
    signal lc_in : signed(64-1 downto 0);
    signal lc_out : signed(64-1 downto 0);
    signal test_ib : signed(32-1 downto 0);
    signal test_ia : signed(32-1 downto 0);
    signal test_lb : signed(64-1 downto 0);
    signal test_la : signed(64-1 downto 0);
begin

   process(clk)
   begin
	if clk'event and clk = '1' then
	case to_integer(unsigned(isel)) is
        when 0 => ic_in <= data_in(31 downto 0);
        when 1 => test_ia <= data_in(31 downto 0);
        when 2 => test_ib <= data_in(31 downto 0);
        when 3 => lc_in <= data_in;
        when 4 => test_la <= data_in;
        when 5 => test_lb <= data_in;
        when others => null;
        end case;
	end if;
   end process;

   process(clk)
   begin
	if clk'event and clk = '1' then
	case to_integer(unsigned(osel)) is
        when 0 => data_out <= X"00000000" & ic_out;
        when others => data_out <= lc_out;
        end case;
	end if;
   end process;

 U: Test000
  port map(
    clk => clk,
    reset => reset,
    ic_in => ic_in,
    ic_we => ic_we,
    ic_out => ic_out,
    lc_in => lc_in,
    lc_we => lc_we,
    lc_out => lc_out,
    test_ib => test_ib,
    test_ia => test_ia,
    test_lb => test_lb,
    test_la => test_la,
    test_req => test_req,
    test_busy => test_busy
  );
end RTL;
