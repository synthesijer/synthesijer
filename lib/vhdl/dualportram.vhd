library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;
 
entity dualportram is
  generic (
    DEPTH : integer := 10;
    WIDTH : integer := 32;
    WORDS : integer := 1024
    );
  port (
    clk     : in  std_logic;
    reset  : in  std_logic;
    
    we      : in  std_logic;
    oe      : in  std_logic;
    address : in  signed(31 downto 0);
    din     : in  signed(WIDTH-1 downto 0);
    dout    : out signed(WIDTH-1 downto 0);

    we_b      : in  std_logic;
    oe_b      : in  std_logic;
    address_b : in  signed(31 downto 0);
    din_b     : in  signed(WIDTH-1 downto 0);
    dout_b    : out signed(WIDTH-1 downto 0);

    length : out signed(31 downto 0)
    );
end dualportram;
 
architecture RTL of dualportram is
  -- Shared memory
  type MEM_TYPE is array ( WORDS-1 downto 0 ) of std_logic_vector(WIDTH-1 downto 0);
  shared variable mem : MEM_TYPE := (others => (others => '0'));

  signal q, q_b : std_logic_vector(WIDTH-1 downto 0) := (others => '0');
  
begin

  length <= to_signed(WORDS, length'length);

  dout <= signed(q);
  dout_b <= signed(q_b);
 
  process(clk)
  begin
    if clk'event and clk = '1' then
      if we = '1' then
        mem(to_integer(unsigned(address(DEPTH-1 downto 0)))) := std_logic_vector(din);
      end if;
      q <= mem(to_integer(unsigned(address(DEPTH-1 downto 0))));
    end if;
  end process;
  
  process(clk)
  begin
    if clk'event and clk = '1' then
      if we_b = '1' then
        mem(to_integer(unsigned(address_b(DEPTH-1 downto 0)))) := std_logic_vector(din_b);
      end if;
      q_b <= mem(to_integer(unsigned(address_b(DEPTH-1 downto 0))));
    end if;
  end process;
  
end RTL;
