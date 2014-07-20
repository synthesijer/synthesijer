library IEEE;
use IEEE.std_logic_1164.all;
use IEEE.std_logic_arith.all;
use IEEE.std_logic_unsigned.all;

entity sc1602_wrapper is
  generic (
    CLKWAIT : integer := 16
    );
  port (
    clk    : in  std_logic;
    reset  : in  std_logic;
    
    pLCD_RS  : out std_logic;
    pLCD_E   : out std_logic;
    pLCD_DB  : out std_logic_vector(3 downto 0);
    pLCD_RW  : out std_logic;

    req           : in  std_logic;
    busy          : out std_logic;
    data_din      : in std_logic_vector(7 downto 0);
    data_waddress : in std_logic_vector(31 downto 0);
    data_we       : in std_logic
  );
end sc1602_wrapper;

architecture RTL of sc1602_wrapper is

  component sc1602if is
    generic (
      CLKWAIT : integer := 16
      );
    port (
      pClk     : in  std_logic;
      pLCD_RS  : out std_logic;
      pLCD_E   : out std_logic;
      pLCD_DB  : out std_logic_vector(3 downto 0);
      pLCD_RW  : out std_logic;
      pLED     : out std_logic_vector(3 downto 0);

      pReq    : in  std_logic;
      pBusy   : out std_logic;
      pWrData : in std_logic_vector(7 downto 0);
      pWrAddr : in std_logic_vector(6 downto 0);
      pWrWe   : in std_logic_vector(0 downto 0);

      pReset  : in  std_logic
      );
  end component;

  signal cWrWe : std_logic_vector(0 downto 0);

begin

  U: sc1602if
    generic map(
      CLKWAIT => CLKWAIT
      )
    port map(
      pClk     => clk,
      pLCD_RS  => pLCD_RS,
      pLCD_E   => pLCD_E,
      pLCD_DB  => pLCD_DB,
      pLCD_RW  => pLCD_RW,
      pLED     => open,
      pReq     => req,
      pBusy    => busy,
      pWrData  => data_din,
      pWrAddr  => data_waddress(6 downto 0),
      pWrWe(0) => data_we,
      pReset   => reset
      );

end RTL;
