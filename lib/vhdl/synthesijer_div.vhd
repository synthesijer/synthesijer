library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;

entity synthesijer_div is

  generic (
    WIDTH : integer := 32
    );
  port (
    clk       : in  std_logic;
    reset     : in  std_logic;
    a         : in  signed(WIDTH-1 downto 0);
    b         : in  signed(WIDTH-1 downto 0);
    nd        : in  std_logic;
    quantient : out signed(WIDTH-1 downto 0);
    remainder : out signed(WIDTH-1 downto 0);
    valid     : out std_logic
    );
end synthesijer_div;

architecture RTL of synthesijer_div is
  
  signal counter : unsigned(7 downto 0) := (others => '0');
  signal oe      : std_logic := '0';

  signal b_reg  : signed(WIDTH-1 downto 0) := (others => '0');
  signal reg    : signed(2*WIDTH-1+1 downto 0) := (others => '0');
  signal q_sign : std_logic;
  signal a_sign : std_logic;

  function with_sign (v : signed; s : std_logic) return signed is
    variable ret : signed(WIDTH-1 downto 0);
  begin
    if s = '0' then
      ret := v;
    else
      ret := not(v) + 1;
    end if;
    return ret;
  end function with_sign;

begin
  
  quantient <= with_sign(reg(WIDTH-1 downto 0), q_sign);
  remainder <= with_sign(reg(2*WIDTH downto WIDTH+1), a_sign);
  valid     <= oe;

  process (clk)
  begin
    if rising_edge(clk) then
      if reset = '1' then
        counter <= (others => '0');
      else
        if nd = '1' then
          counter <= to_unsigned(WIDTH + 1, counter'length);
        elsif counter > 0 then
          counter <= counter - 1;
        end if;
      end if;
    end if;
  end process;

  process (clk)
  begin
    if rising_edge(clk) then
      if reset = '1' then
        oe <= '0';
      elsif counter = 1 then
        oe <= '1';
      else
        oe <= '0';
      end if;
    end if;
  end process;

  process (clk)
    variable tmp : signed(WIDTH-1+1 downto 0);
  begin
    if rising_edge(clk) then
      if counter = 0 then
        reg(2*WIDTH downto WIDTH) <= (others => '0');
        reg(WIDTH-1 downto 0) <= abs(a);
        b_reg <= abs(b);
        q_sign <= a(WIDTH-1) xor b(WIDTH-1);
        a_sign <= a(WIDTH-1);
      else -- counter > 0
        tmp := reg(2 * WIDTH downto WIDTH) - ("0" & b_reg);
        if tmp(WIDTH) = '0' then
          reg <= tmp(WIDTH-1 downto 0) & reg(WIDTH-1 downto 0) & "1";
        else
          reg <= reg(2*WIDTH-1 downto 0) & "0";
        end if;
      end if;
    end if;
  end process;

end RTL;
