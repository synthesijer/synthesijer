library IEEE;
use IEEE.std_logic_1164.all;
use IEEE.numeric_std.all;

entity sintable_rom is
  port(
    clk              : in std_logic;
    reset            : in std_logic;
    sintable_address : in std_logic_vector(31 downto 0);
    sintable_din     : in std_logic_vector(31 downto 0);
    sintable_dout    : out std_logic_vector(31 downto 0);
    sintable_oe      : in std_logic;
    sintable_we      : in std_logic;
    sintable_length  : out std_logic_vector(31 downto 0)
    );
end sintable_rom;

architecture RTL of sintable_rom is

  subtype MEM is std_logic_vector(31 downto 0);
  type ROM is array ( 0 to 127 ) of MEM;

  constant data : ROM := (
    X"000001f4",
    X"00000205",
    X"0000021f",
    X"00000239",
    X"00000253",
    X"0000026c",
    X"0000027d",
    X"00000296",
    X"000002af",
    X"000002c7",
    X"000002de",
    X"000002ed",
    X"00000304",
    X"00000319",
    X"0000032e",
    X"00000342",
    X"00000355",
    X"00000361",
    X"00000373",
    X"00000383",
    X"00000392",
    X"000003a0",
    X"000003a9",
    X"000003b5",
    X"000003c0",
    X"000003c9",
    X"000003d2",
    X"000003d6",
    X"000003dd",
    X"000003e1",
    X"000003e5",
    X"000003e7",
    X"000003e7",
    X"000003e7",
    X"000003e6",
    X"000003e3",
    X"000003de",
    X"000003d9",
    X"000003d4",
    X"000003cc",
    X"000003c3",
    X"000003b9",
    X"000003ad",
    X"000003a5",
    X"00000397",
    X"00000388",
    X"00000378",
    X"00000367",
    X"00000355",
    X"00000348",
    X"00000335",
    X"00000320",
    X"0000030b",
    X"000002f5",
    X"000002e6",
    X"000002cf",
    X"000002b7",
    X"0000029f",
    X"00000286",
    X"00000275",
    X"0000025b",
    X"00000242",
    X"00000228",
    X"0000020e",
    X"000001f4",
    X"000001e2",
    X"000001c8",
    X"000001ae",
    X"00000194",
    X"0000017b",
    X"0000016a",
    X"00000151",
    X"00000138",
    X"00000120",
    X"00000109",
    X"000000fa",
    X"000000e3",
    X"000000ce",
    X"000000b9",
    X"000000a5",
    X"00000092",
    X"00000086",
    X"00000074",
    X"00000064",
    X"00000055",
    X"00000047",
    X"0000003e",
    X"00000032",
    X"00000027",
    X"0000001e",
    X"00000015",
    X"00000011",
    X"0000000a",
    X"00000006",
    X"00000002",
    X"00000000",
    X"00000000",
    X"00000000",
    X"00000001",
    X"00000004",
    X"00000009",
    X"0000000e",
    X"00000013",
    X"0000001b",
    X"00000024",
    X"0000002e",
    X"0000003a",
    X"00000042",
    X"00000050",
    X"0000005f",
    X"0000006f",
    X"00000080",
    X"00000092",
    X"0000009f",
    X"000000b2",
    X"000000c7",
    X"000000dc",
    X"000000f2",
    X"00000101",
    X"00000118",
    X"00000130",
    X"00000148",
    X"00000161",
    X"00000172",
    X"0000018c",
    X"000001a5",
    X"000001bf",
    X"000001d9"
    );

begin
  
  sintable_length <= std_logic_vector(to_unsigned(128, 32));
  
  process(clk)
  begin
    if (clk'event and clk = '1') then
      sintable_dout <= data(to_integer(unsigned(sintable_address)));
    end if;
  end process;
end RTL;

