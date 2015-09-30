library IEEE;
use IEEE.std_logic_1164.all;
use IEEE.numeric_std.all;

entity microboard_top is
  port (
    CLOCK_Y3 : in std_logic;
    USER_RESET : in std_logic;
    USB_RS232_RXD : in std_logic;
    USB_RS232_TXD : out std_logic
  );
end microboard_top;

architecture RTL of microboard_top is

  attribute mark_debug : string;
  attribute keep : string;

  component ToUpper
    port (
      clk : in std_logic;
      reset : in std_logic;
      run_req : in std_logic;
      run_busy : out std_logic;
      class_tx_0002_dout_exp : out std_logic;
      class_rx_0000_din_exp : in std_logic
    );
  end component ToUpper;

  signal CLOCK_Y3_sig : std_logic;
  signal USER_RESET_sig : std_logic;
  signal USB_RS232_RXD_sig : std_logic;
  signal USB_RS232_TXD_sig : std_logic;

  signal U_clk : std_logic;
  signal U_reset : std_logic;
  signal U_run_req : std_logic;
  signal U_run_busy : std_logic;
  signal U_class_tx_0002_dout_exp : std_logic;
  signal U_class_rx_0000_din_exp : std_logic;

begin

  CLOCK_Y3_sig <= CLOCK_Y3;
  USER_RESET_sig <= USER_RESET;
  USB_RS232_RXD_sig <= USB_RS232_RXD;
  USB_RS232_TXD <= USB_RS232_TXD_sig;
  USB_RS232_TXD_sig <= U_class_tx_0002_dout_exp;


  -- expressions

  -- sequencers

  U_clk <= CLOCK_Y3_sig;

  U_reset <= USER_RESET_sig;

  U_run_req <= '1';

  U_class_rx_0000_din_exp <= USB_RS232_RXD_sig;


  inst_U : ToUpper
  port map(
    clk => U_clk,
    reset => U_reset,
    run_req => U_run_req,
    run_busy => U_run_busy,
    class_tx_0002_dout_exp => U_class_tx_0002_dout_exp,
    class_rx_0000_din_exp => U_class_rx_0000_din_exp
  );


end RTL;
