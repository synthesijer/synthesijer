-- -*- coding: sjis-dos -*- --
library ieee;
use ieee.std_logic_1164.all;
use ieee.std_logic_unsigned.all;
use ieee.std_logic_arith.all;

entity sc1602 is

  port (
    pClk   : in std_logic;              -- 65usくらい

    pLCD_E : out std_logic;
    pLCD_RS : out std_logic;
    pLCD_DB : out std_logic_vector(3 downto 0);

    pReq  : in std_logic;
    pBusy : out std_logic;
    
    pWrClk  : in std_logic;
    pWrData : in std_logic_vector(7 downto 0);
    pWrAddr : in std_logic_vector(6 downto 0);
    pWrWe   : in std_logic_vector(0 downto 0);

    pReset : in std_logic
    );

end sc1602;

architecture RTL of sc1602 is

  signal iStateCounter : std_logic_vector(4 downto 0);

  signal iSubStateCounter : std_logic_vector(2 downto 0);

  type SC1620_DATA_OUTPUT_STATE is (ADDROUT, DATAOUT, STEPADDR, MEMWAIT);
  signal data_state : SC1620_DATA_OUTPUT_STATE;

  ------------------------------------------------
  -- SC1602内部のメモリ相当の領域
  -------------------------------------------------
  component blockram
    generic (
      DEPTH : integer := 10;
      WIDTH : integer := 32
    );
    port (
      clka  : in  std_logic;
      dina  : in  std_logic_VECTOR(7 downto 0);
      addra : in  std_logic_VECTOR(6 downto 0);
      wea   : in  std_logic_VECTOR(0 downto 0);
      clkb  : in  std_logic;
      addrb : in  std_logic_VECTOR(6 downto 0);
      doutb : out std_logic_VECTOR(7 downto 0)
      );
  end component;

  signal cRdAddr : std_logic_vector(6 downto 0);
  signal cRdData : std_logic_vector(7 downto 0);
  
  signal cWaitData : std_logic_vector(7 downto 0);
  signal cWaitReq  : std_logic;
  signal cWaitBusy : std_logic;

  signal cSC1602_4_Req   : std_logic;
  signal cSC1602_4_Busy  : std_logic;
  signal cSC1602_4_Ready : std_logic;
  signal cSC1602_4_4Flag : std_logic;
  signal cDB             : std_logic_vector(7 downto 0);
  signal cRS             : std_logic;

  signal iCounter     : std_logic_vector(0 downto 0);
  signal cWaitCounter : std_logic_vector(7 downto 0);
  signal cWaitReady   : std_logic;

begin  -- RTL

  U_BRAM_7_8: blockram
  generic map(
    DEPTH => 7,
    WIDTH => 8)
  port map(
    clka       => pWrClk,
    dina       => pWrData,
    addra      => pWrAddr,
    wea        => pWrWe,
    clkb       => pClk,
    addrb      => cRdAddr,
    doutb      => cRdData
    );

  -------------------------------------------------------------------------------
  -- ビジーウェイトカウンタ
  -------------------------------------------------------------------------------
  process (pClk, pReset)
  begin  -- process
    if pReset = '1' then                -- asynchronous reset (active high)
      iCounter <= (others => '0');
      cWaitCounter <= (others => '0');
      cWaitReady <= '1';
      cWaitCounter <= (others => '0');
    elsif pClk'event and pClk = '1' then  -- rising clock edge
      case conv_integer(iCounter) is
        when 0 =>
          if cWaitReq = '1' then
            iCounter <= iCounter + 1;
            cWaitReady <= '0';
          else
            cWaitReady <= '1';
          end if;
          cWaitCounter <= (others => '0');
        when 1 =>
          if cWaitCounter = cWaitData then
            iCounter <= (others => '0');
          else
            cWaitCounter <= cWaitCounter + 1;
          end if;
        when others =>
          iCounter <= (others => '0');
      end case;
    end if;
  end process;
  cWaitBusy <= (not cWaitReady) or cWaitReq;

  process (pClk, pReset)
  begin
    if pReset = '1' then                -- asynchronous reset (active high)
      iStateCounter <= (others => '0');
      pBusy <= '1';
      cWaitReq <= '0';
    elsif pClk'event and pClk = '1' then  -- rising clock edge
      case conv_integer(iStateCounter) is
        -----------------------------------------------------------
        -- 15ms待つ
        -----------------------------------------------------------
        when 0 =>
          pBusy   <= '1';
          cWaitData <= conv_std_logic_vector(230, 8);
          cWaitReq <= '1';
          iStateCounter <= iStateCounter + 1;
        -----------------------------------------------------------
        -- "0011" (1)
        -----------------------------------------------------------
        when 1 =>
          if cWaitBusy = '0' and cSC1602_4_Busy = '0' then
            cDB <= "00110000";
            cRS <= '0';
            cSC1602_4_4Flag <= '0';
            cSC1602_4_Req <= '1';
            iStateCounter <= iStateCounter + 1;
          else
            cWaitReq <= '0';
            cSC1602_4_Req <= '0';
          end if;
        -----------------------------------------------------------
        -- 4.1ms待つ
        -----------------------------------------------------------
        when 2 =>
          cSC1602_4_Req <= '0';
          cWaitData <= conv_std_logic_vector(63, 8);
          cWaitReq <= '1';
          iStateCounter <= iStateCounter + 1;
        -----------------------------------------------------------
        -- "0011" (2)
        -----------------------------------------------------------
        when 3 =>
          if cWaitBusy = '0' and cSC1602_4_Busy = '0' then
            cDB <= "00110000";
            cRS <= '0';
            cSC1602_4_4Flag <= '0';
            cSC1602_4_Req <= '1';
            iStateCounter <= iStateCounter + 1;
          else
            cWaitReq <= '0';
            cSC1602_4_Req <= '0';
          end if;
        -----------------------------------------------------------
        -- 100us待つ
        -----------------------------------------------------------
        when 4 =>
          cSC1602_4_Req <= '0';
          cWaitData <= conv_std_logic_vector(1, 8);
          cWaitReq <= '1';
          iStateCounter <= iStateCounter + 1;
        -----------------------------------------------------------
        -- "0011" (3)
        -----------------------------------------------------------
        when 5 =>
          if cWaitBusy = '0' and cSC1602_4_Busy = '0' then
            cDB <= "00110000";
            cRS <= '0';
            cSC1602_4_4Flag <= '0';
            cSC1602_4_Req <= '1';
            iStateCounter <= iStateCounter + 1;
          else
            cWaitReq <= '0';
            cSC1602_4_Req <= '0';
          end if;
        -----------------------------------------------------------
        -- "0010"
        -- 4bitモードに遷移
        -----------------------------------------------------------
        when 6 =>
          if cSC1602_4_Busy = '0' then
            cDB <= "00100000";
            cRS <= '0';
            cSC1602_4_4Flag <= '0';
            cSC1602_4_Req <= '1';
            iStateCounter <= iStateCounter + 1;
          else
            cSC1602_4_Req <= '0';
          end if;
        -----------------------------------------------------------
        -- モードセット
        -- 4bit/2行/5x7ドット
        -----------------------------------------------------------
        when 7 =>
          if cSC1602_4_Busy = '0' then
            cDB <= "00101100";
            cRS <= '0';
            cSC1602_4_4Flag <= '1';
            cSC1602_4_Req <= '1';
            iStateCounter <= iStateCounter + 1;
          else
            cSC1602_4_Req <= '0';
          end if;
        -----------------------------------------------------------
        -- ディスプレイの消灯
        -----------------------------------------------------------
        when 8 =>
          if cSC1602_4_Busy = '0' then
            cDB <= "00001000";
            cRS <= '0';
            cSC1602_4_4Flag <= '1';
            cSC1602_4_Req <= '1';
            iStateCounter <= iStateCounter + 1;
          else
            cSC1602_4_Req <= '0';
          end if;
        -----------------------------------------------------------
        -- ディスプレイクリア
        -----------------------------------------------------------
        when 9 =>
          if cSC1602_4_Busy = '0' then
            cDB <= "00000001";
            cRS <= '0';
            cSC1602_4_4Flag <= '1';
            cSC1602_4_Req <= '1';
            iStateCounter <= iStateCounter + 1;
          else
            cSC1602_4_Req <= '0';
          end if;
        -----------------------------------------------------------
        -- 1.6ms待つ
        -----------------------------------------------------------
        when 10 =>
          pBusy   <= '1';
          cWaitData <= conv_std_logic_vector(1, 8);
          cWaitReq <= '1';
          iStateCounter <= iStateCounter + 1;
        -----------------------------------------------------------
        -- ディスプレイ点灯
        -----------------------------------------------------------
        when 11 =>
          if cWaitBusy = '0' and cSC1602_4_Busy = '0' then
            cDB <= "00001100";
            cRS <= '0';
            cSC1602_4_4Flag <= '1';
            cSC1602_4_Req <= '1';
            iStateCounter <= iStateCounter + 1;
          else
            cWaitReq <= '0';
            cSC1602_4_Req <= '0';
          end if;
        -----------------------------------------------------------
        -- モードセット
        -----------------------------------------------------------
        when 12 =>
          if cSC1602_4_Busy = '0' then
            cDB <= "00000110";
            cRS <= '0';
            cSC1602_4_4Flag <= '1';
            cSC1602_4_Req <= '1';
            iStateCounter <= iStateCounter + 1;
          else
            cWaitReq <= '0';
            cSC1602_4_Req <= '0';
          end if;
        -----------------------------------------------------------
        -- SC1602へのデータ出力のリクエストを待つ
        -----------------------------------------------------------
        when 13 =>
          if pReq = '1' then
            pBusy <= '1';
            cRdAddr <= (others => '0');
            iStateCounter <= iStateCounter + 1;
            data_state <= ADDROUT;
          else
            pBusy <= '0';
          end if;
        -----------------------------------------------------------
        -- データを出力
        -----------------------------------------------------------
        when 14 =>
          case data_state is
            -- DDRAMのアドレスを指定する
            when ADDROUT =>
              if cSC1602_4_Busy = '0' then
                cDB <= "1" & cRdAddr(6) & "000000";
                cRS <= '0';
                cSC1602_4_4Flag <= '1';
                cSC1602_4_Req <= '1';
                data_state <= DATAOUT;
              else
                cSC1602_4_Req <= '0';
              end if;
            -- DDRAMにデータを書き込む
            when DATAOUT =>
              if cSC1602_4_Busy = '0' then
                cDB <= cRdData;
                cRS <= '1';
                cSC1602_4_4Flag <= '1';
                cSC1602_4_Req <= '1';
                data_state <= STEPADDR;
              else
                cSC1602_4_Req <= '0';
              end if;
            when STEPADDR =>
              if cSC1602_4_Busy = '0' then
                if conv_integer(cRdAddr) = 39 then  -- 次の行へ
                  cRdAddr <= "1000000";
                  -- アドレス指定に遷移する
                  data_state <= ADDROUT;
                elsif conv_integer(cRdAddr) = 103 then  -- おしまい．
                  cRdAddr <= (others => '0');
                  iStateCounter <= conv_std_logic_vector(13, 5);
                  data_state <= ADDROUT;
                else
                  cRdAddr <= cRdAddr + 1;
                  data_state <= MEMWAIT;
                end if;
              else
                cSC1602_4_Req <= '0';
              end if;
            when MEMWAIT =>
              data_state <= DATAOUT;
            when others =>
              data_state <= ADDROUT;
              iStateCounter <= conv_std_logic_vector(13, 5);
          end case;
        when others =>
          iStateCounter <= conv_std_logic_vector(13, 5);
      end case;
    end if;
  end process;

  cSC1602_4_Busy <= (not cSC1602_4_Ready) or cSC1602_4_Req;

  SC1602_4: process (pClk, pReset)
  begin
    if pReset = '1' then                -- asynchronous reset (active high)
      iSubStateCounter <= (others => '0');
      pLCD_E <= '0';
      cSC1602_4_Ready <= '1';
    elsif pClk'event and pClk = '1' then  -- rising clock edge
      case conv_integer(iSubStateCounter) is
        when 0 =>
          if cSC1602_4_Req = '1' then
            iSubStateCounter <= iSubStateCounter + 1;
            cSC1602_4_Ready <= '0';
            pLCD_E <= '0';
            pLCD_RS <= cRS;
            pLCD_DB <= cDB(7 downto 4);
          else
            cSC1602_4_Ready <= '1';
          end if;
        when 1 =>
          pLCD_E <= '1';
          iSubStateCounter <= iSubStateCounter + 1;
        when 2 =>
          pLCD_E <= '0';
          if cSC1602_4_4Flag = '1' then
            iSubStateCounter <= iSubStateCounter + 1;
          else
            iSubStateCounter <= (others => '0');
          end if;
        when 3 =>
          pLCD_RS <= cRS;
          pLCD_DB <= cDB(3 downto 0);
          iSubStateCounter <= iSubStateCounter + 1;
        when 4 =>
          pLCD_E <= '1';
          iSubStateCounter <= iSubStateCounter + 1;
        when 5 =>
          pLCD_E <= '0';
          iSubStateCounter <= (others => '0');
        when others =>
          iSubStateCounter <= (others => '0');
      end case;
    end if;
  end process;

end RTL;
