--
--  RS232C_TX
--  シリアル通信送信モジュール
--  スタートビット(0), 8bitデータ(LSBからMSBの順に), ストップビット(1)の順に送信

-- おまじない(ライブラリ呼び出し)
library ieee;
use ieee.std_logic_1164.all;
use ieee.std_logic_arith.all;
use ieee.std_logic_unsigned.all;

entity rs232c_tx is
  -- 定数宣言
  generic (
    sys_clk : integer := 14000000;             --クロック周波数
    rate    : integer := 9600                  --転送レート,単位はbps(ビット毎秒)
    );
  -- 入出力ポート宣言
  port (
    clk   : in  std_logic;                     -- クロック
    reset : in  std_logic;                     -- リセット
    wr    : in  std_logic;                     -- 送信要求
    din   : in  std_logic_vector(7 downto 0);  --送信データ
    dout  : out std_logic := '1';              --シリアル出力
    ready : out std_logic                      --送信要求を受け付けることができるかどうか
    );
end rs232c_tx;

architecture rtl of rs232c_tx is
  -- クロック分周モジュールの呼び出しの準備
  component clk_div is
    port (clk      : in  std_logic;
           rst     : in  std_logic;
           div     : in  std_logic_vector(15 downto 0);
           clk_out : out std_logic);
  end component;
  -- 内部変数定義
  signal in_din : std_logic_vector(7 downto 0);  -- 送信データ一時保存用レジスタ
  signal buf    : std_logic_vector(7 downto 0);  -- 一時的にしようするバッファ
  signal load   : std_logic := '0';              -- 送信データを読み込んだかどうか
  signal cbit   : std_logic_vector(2 downto 0) := (others => '0');  -- 何ビット目を送信しているか

  signal run : std_logic := '0';        -- 送信状態にあるかどうか

  signal tx_en  : std_logic; -- 送信用クロック
  signal tx_div : std_logic_vector(15 downto 0); -- クロック分周の倍率

  signal status : std_logic_vector(1 downto 0); -- 状態遷移用レジスタ

begin
  -- クロック分周モジュールの呼び出し
  -- clk_divの入出力ポートにこのモジュールの内部変数を接続
  tx_div <= conv_std_logic_vector((sys_clk / rate) - 1, 16);
  U0: clk_div port map (clk, reset, tx_div, tx_en);
  -- readyへの代入, 常時値を更新している
  ready <= '1' when (wr = '0' and run = '0' and load = '0') else '0';

  process(clk) --変化を監視する信号を記述する, この場合クロック
  begin
    if rising_edge(clk) then
      if(reset = '1') then --リセット時の動作, 初期値の設定
        load <= '0';
      elsif(clk'event and clk='1') then --クロックの立ち上がり時の動作
        if(wr = '1' and run = '0') then --送信要求があり，かつ送信中でない場合
          load   <= '1'; --データを取り込んだことを示すフラグを立てる
          in_din <= din; --一時保存用レジスタに値を格納
        end if;
        if(load = '1' and run = '1') then --送信中で，かつデータを取り込んだ
                                          --ことを示すフラグが立っている場合
          load <= '0'; --データを取り込んだことを示すフラグを下げる
        end if;
      end if;
    end if;
  end process;

  process(clk) --変化を監視する信号を記述する, この場合送信用クロックを監視
  begin
    if rising_edge(clk) then
      if reset = '1' then --リセット時の動作, 初期値の設定
        dout <= '1';
        cbit <= (others => '0');
        status <= (others => '0');
        run <= '0';
      elsif tx_en = '1' then --送信用クロックの立ち上がり時の動作
        case conv_integer(status) is --statusの値に応じて動作が異なる
          when 0 => --初期状態
            cbit <= (others => '0'); --カウンタをクリア
            if load = '1' then -- データを取り込んでいる場合
              dout <= '0'; -- スタートビット0を出力
              status <= status + 1; -- 次の状態へ
              buf <= in_din; -- 送信データを一時バッファに退避
              run <= '1'; -- 送信中の状態へ遷移
            else --なにもしない状態へ遷移
              dout <= '1';
              run <= '0'; --送信要求受付可能状態へ
            end if;
          when 1 => --データをLSBから順番に送信
            cbit <= cbit + 1; -- カウンタをインクリメント
            dout <= buf(conv_integer(cbit)); --一時バッファからcbit目を抽出し出力する
            if(conv_integer(cbit) = 7) then -- データの8ビット目を送信したら,
                                            -- ストップビットを送る状態へ遷移
              status <= status + 1;
            end if;
          when 2 => -- ストップビットを送信
            dout <= '1'; --ストップビット1
            status <= (others => '0'); --初期状態へ
          when others => --その他の状態の場合
            status <= (others => '0'); -- 初期状態へ遷移
        end case;
      end if;
    end if;
  end process;

end rtl;
