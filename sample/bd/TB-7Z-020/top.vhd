library IEEE;
use IEEE.STD_LOGIC_1164.ALL;
use IEEE.numeric_std.ALL;

library UNISIM;
use UNISIM.VCOMPONENTS.ALL;
entity top is
  port (
    DDR_addr : inout STD_LOGIC_VECTOR ( 14 downto 0 );
    DDR_ba : inout STD_LOGIC_VECTOR ( 2 downto 0 );
    DDR_cas_n : inout STD_LOGIC;
    DDR_ck_n : inout STD_LOGIC;
    DDR_ck_p : inout STD_LOGIC;
    DDR_cke : inout STD_LOGIC;
    DDR_cs_n : inout STD_LOGIC;
    DDR_dm : inout STD_LOGIC_VECTOR ( 3 downto 0 );
    DDR_dq : inout STD_LOGIC_VECTOR ( 31 downto 0 );
    DDR_dqs_n : inout STD_LOGIC_VECTOR ( 3 downto 0 );
    DDR_dqs_p : inout STD_LOGIC_VECTOR ( 3 downto 0 );
    DDR_odt : inout STD_LOGIC;
    DDR_ras_n : inout STD_LOGIC;
    DDR_reset_n : inout STD_LOGIC;
    DDR_we_n : inout STD_LOGIC;
    FIXED_IO_ddr_vrn : inout STD_LOGIC;
    FIXED_IO_ddr_vrp : inout STD_LOGIC;
    FIXED_IO_mio : inout STD_LOGIC_VECTOR ( 53 downto 0 );
    FIXED_IO_ps_clk : inout STD_LOGIC;
    FIXED_IO_ps_porb : inout STD_LOGIC;
    FIXED_IO_ps_srstb : inout STD_LOGIC;
    LED : out STD_LOGIC_VECTOR(7 downto 0);

    SYS_CLK3: in STD_LOGIC;
    
    DVI_R  : out STD_LOGIC_VECTOR(7 downto 0);
    DVI_G  : out STD_LOGIC_VECTOR(7 downto 0);
    DVI_B  : out STD_LOGIC_VECTOR(7 downto 0);
    DVI_DE : out STD_LOGIC;
    DVI_VS : out STD_LOGIC;
    DVI_HS : out STD_LOGIC;
    
    DVI_PWRDN : out STD_LOGIC;
    DVI_DKEN  : out STD_LOGIC;
    DVI_CTRL  : out STD_LOGIC_VECTOR(2 downto 0);
    DVI_ISEL  : out STD_LOGIC;
    DVI_MSEN  : out STD_LOGIC;
    DVI_CLK : out STD_LOGIC
  );
end top;

architecture STRUCTURE of top is

  attribute MARK_DEBUG : string;
  attribute KEEP : string;
  
  component design_1 is
  port (
    DDR_cas_n : inout STD_LOGIC;
    DDR_cke : inout STD_LOGIC;
    DDR_ck_n : inout STD_LOGIC;
    DDR_ck_p : inout STD_LOGIC;
    DDR_cs_n : inout STD_LOGIC;
    DDR_reset_n : inout STD_LOGIC;
    DDR_odt : inout STD_LOGIC;
    DDR_ras_n : inout STD_LOGIC;
    DDR_we_n : inout STD_LOGIC;
    DDR_ba : inout STD_LOGIC_VECTOR ( 2 downto 0 );
    DDR_addr : inout STD_LOGIC_VECTOR ( 14 downto 0 );
    DDR_dm : inout STD_LOGIC_VECTOR ( 3 downto 0 );
    DDR_dq : inout STD_LOGIC_VECTOR ( 31 downto 0 );
    DDR_dqs_n : inout STD_LOGIC_VECTOR ( 3 downto 0 );
    DDR_dqs_p : inout STD_LOGIC_VECTOR ( 3 downto 0 );
    FIXED_IO_mio : inout STD_LOGIC_VECTOR ( 53 downto 0 );
    FIXED_IO_ddr_vrn : inout STD_LOGIC;
    FIXED_IO_ddr_vrp : inout STD_LOGIC;
    FIXED_IO_ps_srstb : inout STD_LOGIC;
    FIXED_IO_ps_clk : inout STD_LOGIC;
    FIXED_IO_ps_porb : inout STD_LOGIC;
    FCLK_CLK0 : out STD_LOGIC;
    FCLK_RESET0_N : out STD_LOGIC;
    FCLK_CLK1 : out STD_LOGIC;
    FCLK_RESET1_N : out STD_LOGIC;
    S_AXI_HP0_FIFO_CTRL_rcount : out STD_LOGIC_VECTOR ( 7 downto 0 );
    S_AXI_HP0_FIFO_CTRL_wcount : out STD_LOGIC_VECTOR ( 7 downto 0 );
    S_AXI_HP0_FIFO_CTRL_racount : out STD_LOGIC_VECTOR ( 2 downto 0 );
    S_AXI_HP0_FIFO_CTRL_wacount : out STD_LOGIC_VECTOR ( 5 downto 0 );
    S_AXI_HP0_FIFO_CTRL_rdissuecapen : in STD_LOGIC;
    S_AXI_HP0_FIFO_CTRL_wrissuecapen : in STD_LOGIC;
    S_AXI_HP2_FIFO_CTRL_racount : out STD_LOGIC_VECTOR ( 2 downto 0 );
    S_AXI_HP2_FIFO_CTRL_rcount : out STD_LOGIC_VECTOR ( 7 downto 0 );
    S_AXI_HP2_FIFO_CTRL_rdissuecapen : in STD_LOGIC;
    S_AXI_HP2_FIFO_CTRL_wacount : out STD_LOGIC_VECTOR ( 5 downto 0 );
    S_AXI_HP2_FIFO_CTRL_wcount : out STD_LOGIC_VECTOR ( 7 downto 0 );
    S_AXI_HP2_FIFO_CTRL_wrissuecapen : in STD_LOGIC;
    S_AXI_HP0_arready : out STD_LOGIC;
    S_AXI_HP0_awready : out STD_LOGIC;
    S_AXI_HP0_bvalid : out STD_LOGIC;
    S_AXI_HP0_rlast : out STD_LOGIC;
    S_AXI_HP0_rvalid : out STD_LOGIC;
    S_AXI_HP0_wready : out STD_LOGIC;
    S_AXI_HP0_bresp : out STD_LOGIC_VECTOR ( 1 downto 0 );
    S_AXI_HP0_rresp : out STD_LOGIC_VECTOR ( 1 downto 0 );
    S_AXI_HP0_bid : out STD_LOGIC_VECTOR ( 5 downto 0 );
    S_AXI_HP0_rid : out STD_LOGIC_VECTOR ( 5 downto 0 );
    S_AXI_HP0_rdata : out STD_LOGIC_VECTOR ( 63 downto 0 );
    S_AXI_HP0_arvalid : in STD_LOGIC;
    S_AXI_HP0_awvalid : in STD_LOGIC;
    S_AXI_HP0_bready : in STD_LOGIC;
    S_AXI_HP0_rready : in STD_LOGIC;
    S_AXI_HP0_wlast : in STD_LOGIC;
    S_AXI_HP0_wvalid : in STD_LOGIC;
    S_AXI_HP0_arburst : in STD_LOGIC_VECTOR ( 1 downto 0 );
    S_AXI_HP0_arlock : in STD_LOGIC_VECTOR ( 1 downto 0 );
    S_AXI_HP0_arsize : in STD_LOGIC_VECTOR ( 2 downto 0 );
    S_AXI_HP0_awburst : in STD_LOGIC_VECTOR ( 1 downto 0 );
    S_AXI_HP0_awlock : in STD_LOGIC_VECTOR ( 1 downto 0 );
    S_AXI_HP0_awsize : in STD_LOGIC_VECTOR ( 2 downto 0 );
    S_AXI_HP0_arprot : in STD_LOGIC_VECTOR ( 2 downto 0 );
    S_AXI_HP0_awprot : in STD_LOGIC_VECTOR ( 2 downto 0 );
    S_AXI_HP0_araddr : in STD_LOGIC_VECTOR ( 31 downto 0 );
    S_AXI_HP0_awaddr : in STD_LOGIC_VECTOR ( 31 downto 0 );
    S_AXI_HP0_arcache : in STD_LOGIC_VECTOR ( 3 downto 0 );
    S_AXI_HP0_arlen : in STD_LOGIC_VECTOR ( 3 downto 0 );
    S_AXI_HP0_arqos : in STD_LOGIC_VECTOR ( 3 downto 0 );
    S_AXI_HP0_awcache : in STD_LOGIC_VECTOR ( 3 downto 0 );
    S_AXI_HP0_awlen : in STD_LOGIC_VECTOR ( 3 downto 0 );
    S_AXI_HP0_awqos : in STD_LOGIC_VECTOR ( 3 downto 0 );
    S_AXI_HP0_arid : in STD_LOGIC_VECTOR ( 5 downto 0 );
    S_AXI_HP0_awid : in STD_LOGIC_VECTOR ( 5 downto 0 );
    S_AXI_HP0_wid : in STD_LOGIC_VECTOR ( 5 downto 0 );
    S_AXI_HP0_wdata : in STD_LOGIC_VECTOR ( 63 downto 0 );
    S_AXI_HP0_wstrb : in STD_LOGIC_VECTOR ( 7 downto 0 );
    S_AXI_HP2_arready : out STD_LOGIC;
    S_AXI_HP2_awready : out STD_LOGIC;
    S_AXI_HP2_bvalid : out STD_LOGIC;
    S_AXI_HP2_rlast : out STD_LOGIC;
    S_AXI_HP2_rvalid : out STD_LOGIC;
    S_AXI_HP2_wready : out STD_LOGIC;
    S_AXI_HP2_bresp : out STD_LOGIC_VECTOR ( 1 downto 0 );
    S_AXI_HP2_rresp : out STD_LOGIC_VECTOR ( 1 downto 0 );
    S_AXI_HP2_bid : out STD_LOGIC_VECTOR ( 5 downto 0 );
    S_AXI_HP2_rid : out STD_LOGIC_VECTOR ( 5 downto 0 );
    S_AXI_HP2_rdata : out STD_LOGIC_VECTOR ( 31 downto 0 );
    S_AXI_HP2_arvalid : in STD_LOGIC;
    S_AXI_HP2_awvalid : in STD_LOGIC;
    S_AXI_HP2_bready : in STD_LOGIC;
    S_AXI_HP2_rready : in STD_LOGIC;
    S_AXI_HP2_wlast : in STD_LOGIC;
    S_AXI_HP2_wvalid : in STD_LOGIC;
    S_AXI_HP2_arburst : in STD_LOGIC_VECTOR ( 1 downto 0 );
    S_AXI_HP2_arlock : in STD_LOGIC_VECTOR ( 1 downto 0 );
    S_AXI_HP2_arsize : in STD_LOGIC_VECTOR ( 2 downto 0 );
    S_AXI_HP2_awburst : in STD_LOGIC_VECTOR ( 1 downto 0 );
    S_AXI_HP2_awlock : in STD_LOGIC_VECTOR ( 1 downto 0 );
    S_AXI_HP2_awsize : in STD_LOGIC_VECTOR ( 2 downto 0 );
    S_AXI_HP2_arprot : in STD_LOGIC_VECTOR ( 2 downto 0 );
    S_AXI_HP2_awprot : in STD_LOGIC_VECTOR ( 2 downto 0 );
    S_AXI_HP2_araddr : in STD_LOGIC_VECTOR ( 31 downto 0 );
    S_AXI_HP2_awaddr : in STD_LOGIC_VECTOR ( 31 downto 0 );
    S_AXI_HP2_arcache : in STD_LOGIC_VECTOR ( 3 downto 0 );
    S_AXI_HP2_arlen : in STD_LOGIC_VECTOR ( 3 downto 0 );
    S_AXI_HP2_arqos : in STD_LOGIC_VECTOR ( 3 downto 0 );
    S_AXI_HP2_awcache : in STD_LOGIC_VECTOR ( 3 downto 0 );
    S_AXI_HP2_awlen : in STD_LOGIC_VECTOR ( 3 downto 0 );
    S_AXI_HP2_awqos : in STD_LOGIC_VECTOR ( 3 downto 0 );
    S_AXI_HP2_arid : in STD_LOGIC_VECTOR ( 5 downto 0 );
    S_AXI_HP2_awid : in STD_LOGIC_VECTOR ( 5 downto 0 );
    S_AXI_HP2_wid : in STD_LOGIC_VECTOR ( 5 downto 0 );
    S_AXI_HP2_wdata : in STD_LOGIC_VECTOR ( 31 downto 0 );
    S_AXI_HP2_wstrb : in STD_LOGIC_VECTOR ( 3 downto 0 );
    ext_reset_in : in STD_LOGIC;
    gpio_tri_o : out STD_LOGIC_VECTOR ( 31 downto 0 )
  );
  end component design_1;

  signal FCLK_CLK0 : std_logic;
  signal FCLK_RESET0_N : std_logic;
  signal counter : unsigned(31 downto 0) := (others => '0');

  component sync_generator
    port (
      clk : in std_logic;
      reset : in std_logic;
      VSYNC : out std_logic;
      HSYNC : out std_logic;
      DE : out std_logic;
      DATA : out std_logic_vector(24-1 downto 0);
      fifo_rd : out std_logic;
      fifo_din : in std_logic_vector(24-1 downto 0);
      wakeup : in std_logic
      );
  end component sync_generator;

  signal fifo2dvi_reset : std_logic := '1';

  signal s_syncgen_vs : STD_LOGIC;
  signal s_syncgen_hs : STD_LOGIC;
  signal s_syncgen_de : STD_LOGIC;
  signal s_syncgen_da : STD_LOGIC_VECTOR(23 downto 0);

  signal r_dvit_vs_1t : STD_LOGIC;
  signal r_dvit_hs_1t : STD_LOGIC;
  signal r_dvit_de_1t : STD_LOGIC;
  signal r_dvit_da_1t : STD_LOGIC_VECTOR(23 downto 0);

  signal r_dvit_vs_2t : STD_LOGIC;
  signal r_dvit_hs_2t : STD_LOGIC;
  signal r_dvit_de_2t : STD_LOGIC;
  signal r_dvit_da_2t : STD_LOGIC_VECTOR(23 downto 0);
  
  signal r_dvit_vs_3t : STD_LOGIC;
  signal r_dvit_hs_3t : STD_LOGIC;
  signal r_dvit_de_3t : STD_LOGIC;
  signal r_dvit_da_3t : STD_LOGIC_VECTOR(23 downto 0);

  signal clk_ibufg : std_logic;


  signal FCLK_CLK1 : std_logic;
  signal FCLK_RESET1_N : std_logic;

  signal S_AXI_HP0_FIFO_CTRL_racount      : STD_LOGIC_VECTOR (2 downto 0)  := (others => '0');
  signal S_AXI_HP0_FIFO_CTRL_rcount       : STD_LOGIC_VECTOR (7 downto 0)  := (others => '0');
  signal S_AXI_HP0_FIFO_CTRL_rdissuecapen : STD_LOGIC := '0';
  signal S_AXI_HP0_FIFO_CTRL_wacount      : STD_LOGIC_VECTOR (5 downto 0)  := (others => '0');
  signal S_AXI_HP0_FIFO_CTRL_wcount       : STD_LOGIC_VECTOR (7 downto 0)  := (others => '0');
  signal S_AXI_HP0_FIFO_CTRL_wrissuecapen : STD_LOGIC := '0';
  signal S_AXI_HP0_araddr                 : STD_LOGIC_VECTOR (31 downto 0) := (others => '0');
  signal S_AXI_HP0_arburst                : STD_LOGIC_VECTOR (1 downto 0)  := (others => '0');
  signal S_AXI_HP0_arcache                : STD_LOGIC_VECTOR (3 downto 0)  := (others => '0');
  signal S_AXI_HP0_arid                   : STD_LOGIC_VECTOR (5 downto 0)  := (others => '0');
  signal S_AXI_HP0_arlen                  : STD_LOGIC_VECTOR (3 downto 0)  := (others => '0');
  signal S_AXI_HP0_arlock                 : STD_LOGIC_VECTOR (1 downto 0)  := (others => '0');
  signal S_AXI_HP0_arprot                 : STD_LOGIC_VECTOR (2 downto 0)  := (others => '0');
  signal S_AXI_HP0_arqos                  : STD_LOGIC_VECTOR (3 downto 0)  := (others => '0');
  signal S_AXI_HP0_arready                : STD_LOGIC := '0';
  signal S_AXI_HP0_arsize                 : STD_LOGIC_VECTOR (2 downto 0)  := (others => '0');
  signal S_AXI_HP0_arvalid                : STD_LOGIC := '0';
  signal S_AXI_HP0_awaddr                 : STD_LOGIC_VECTOR (31 downto 0) := (others => '0');
  signal S_AXI_HP0_awburst                : STD_LOGIC_VECTOR (1 downto 0)  := (others => '0');
  signal S_AXI_HP0_awcache                : STD_LOGIC_VECTOR (3 downto 0)  := (others => '0');
  signal S_AXI_HP0_awid                   : STD_LOGIC_VECTOR (5 downto 0)  := (others => '0');
  signal S_AXI_HP0_awlen                  : STD_LOGIC_VECTOR (3 downto 0)  := (others => '0');
  signal S_AXI_HP0_awlock                 : STD_LOGIC_VECTOR (1 downto 0)  := (others => '0');
  signal S_AXI_HP0_awprot                 : STD_LOGIC_VECTOR (2 downto 0)  := (others => '0');
  signal S_AXI_HP0_awqos                  : STD_LOGIC_VECTOR (3 downto 0)  := (others => '0');
  signal S_AXI_HP0_awready                : STD_LOGIC := '0';
  signal S_AXI_HP0_awsize                 : STD_LOGIC_VECTOR (2 downto 0)  := (others => '0');
  signal S_AXI_HP0_awvalid                : STD_LOGIC := '0';
  signal S_AXI_HP0_bid                    : STD_LOGIC_VECTOR (5 downto 0)  := (others => '0');
  signal S_AXI_HP0_bready                 : STD_LOGIC := '0';
  signal S_AXI_HP0_bresp                  : STD_LOGIC_VECTOR (1 downto 0)  := (others => '0');
  signal S_AXI_HP0_bvalid                 : STD_LOGIC := '0';
  signal S_AXI_HP0_rdata                  : STD_LOGIC_VECTOR (63 downto 0) := (others => '0');
  signal S_AXI_HP0_rid                    : STD_LOGIC_VECTOR (5 downto 0)  := (others => '0');
  signal S_AXI_HP0_rlast                  : STD_LOGIC := '0';
  signal S_AXI_HP0_rready                 : STD_LOGIC := '0';
  signal S_AXI_HP0_rresp                  : STD_LOGIC_VECTOR (1 downto 0)  := (others => '0');
  signal S_AXI_HP0_rvalid                 : STD_LOGIC := '0';
  signal S_AXI_HP0_wdata                  : STD_LOGIC_VECTOR (63 downto 0) := (others => '0');
  signal S_AXI_HP0_wid                    : STD_LOGIC_VECTOR (5 downto 0)  := (others => '0');
  signal S_AXI_HP0_wlast                  : STD_LOGIC := '0';
  signal S_AXI_HP0_wready                 : STD_LOGIC := '0';
  signal S_AXI_HP0_wstrb                  : STD_LOGIC_VECTOR (7 downto 0)  := (others => '0');
  signal S_AXI_HP0_wvalid                 : STD_LOGIC := '0';
  signal S_AXI_HP2_FIFO_CTRL_racount      : STD_LOGIC_VECTOR (2 downto 0)  := (others => '0');
  signal S_AXI_HP2_FIFO_CTRL_rcount       : STD_LOGIC_VECTOR (7 downto 0)  := (others => '0');
  signal S_AXI_HP2_FIFO_CTRL_rdissuecapen : STD_LOGIC := '0';
  signal S_AXI_HP2_FIFO_CTRL_wacount      : STD_LOGIC_VECTOR (5 downto 0)  := (others => '0');
  signal S_AXI_HP2_FIFO_CTRL_wcount       : STD_LOGIC_VECTOR (7 downto 0)  := (others => '0');
  signal S_AXI_HP2_FIFO_CTRL_wrissuecapen : STD_LOGIC := '0';
  signal S_AXI_HP2_araddr                 : STD_LOGIC_VECTOR (31 downto 0) := (others => '0');
  signal S_AXI_HP2_arburst                : STD_LOGIC_VECTOR (1 downto 0)  := (others => '0');
  signal S_AXI_HP2_arcache                : STD_LOGIC_VECTOR (3 downto 0)  := (others => '0');
  signal S_AXI_HP2_arid                   : STD_LOGIC_VECTOR (5 downto 0)  := (others => '0');
  signal S_AXI_HP2_arlen                  : STD_LOGIC_VECTOR (3 downto 0)  := (others => '0');
  signal S_AXI_HP2_arlock                 : STD_LOGIC_VECTOR (1 downto 0)  := (others => '0');
  signal S_AXI_HP2_arprot                 : STD_LOGIC_VECTOR (2 downto 0)  := (others => '0');
  signal S_AXI_HP2_arqos                  : STD_LOGIC_VECTOR (3 downto 0)  := (others => '0');
  signal S_AXI_HP2_arready                : STD_LOGIC := '0';
  signal S_AXI_HP2_arsize                 : STD_LOGIC_VECTOR (2 downto 0)  := (others => '0');
  signal S_AXI_HP2_arvalid                : STD_LOGIC;
  signal S_AXI_HP2_awaddr                 : STD_LOGIC_VECTOR (31 downto 0) := (others => '0');
  signal S_AXI_HP2_awburst                : STD_LOGIC_VECTOR (1 downto 0)  := (others => '0');
  signal S_AXI_HP2_awcache                : STD_LOGIC_VECTOR (3 downto 0)  := (others => '0');
  signal S_AXI_HP2_awid                   : STD_LOGIC_VECTOR (5 downto 0)  := (others => '0');
  signal S_AXI_HP2_awlen                  : STD_LOGIC_VECTOR (3 downto 0)  := (others => '0');
  signal S_AXI_HP2_awlock                 : STD_LOGIC_VECTOR (1 downto 0)  := (others => '0');
  signal S_AXI_HP2_awprot                 : STD_LOGIC_VECTOR (2 downto 0)  := (others => '0');
  signal S_AXI_HP2_awqos                  : STD_LOGIC_VECTOR (3 downto 0)  := (others => '0');
  signal S_AXI_HP2_awready                : STD_LOGIC := '0';
  signal S_AXI_HP2_awsize                 : STD_LOGIC_VECTOR (2 downto 0)  := (others => '0');
  signal S_AXI_HP2_awvalid                : STD_LOGIC := '0';
  signal S_AXI_HP2_bid                    : STD_LOGIC_VECTOR (5 downto 0)  := (others => '0');
  signal S_AXI_HP2_bready                 : STD_LOGIC := '0';
  signal S_AXI_HP2_bresp                  : STD_LOGIC_VECTOR (1 downto 0)  := (others => '0');
  signal S_AXI_HP2_bvalid                 : STD_LOGIC := '0';
  signal S_AXI_HP2_rdata                  : STD_LOGIC_VECTOR (31 downto 0) := (others => '0');
  signal S_AXI_HP2_rid                    : STD_LOGIC_VECTOR (5 downto 0)  := (others => '0');
  signal S_AXI_HP2_rlast                  : STD_LOGIC := '0';
  signal S_AXI_HP2_rready                 : STD_LOGIC := '0';
  signal S_AXI_HP2_rresp                  : STD_LOGIC_VECTOR (1 downto 0)  := (others => '0');
  signal S_AXI_HP2_rvalid                 : STD_LOGIC := '0';
  signal S_AXI_HP2_wdata                  : STD_LOGIC_VECTOR (31 downto 0) := (others => '0');
  signal S_AXI_HP2_wid                    : STD_LOGIC_VECTOR (5 downto 0)  := (others => '0');
  signal S_AXI_HP2_wlast                  : STD_LOGIC := '0';
  signal S_AXI_HP2_wready                 : STD_LOGIC := '0';
  signal S_AXI_HP2_wstrb                  : STD_LOGIC_VECTOR (3 downto 0)  := (others => '0');
  signal S_AXI_HP2_wvalid                 : STD_LOGIC := '0';

  signal fifo_dout      : std_logic_vector(23 downto 0);
  signal fifo_rd_en     : std_logic;
  signal fifo_din       : std_logic_vector(23 downto 0);
  signal fifo_wr_en     : std_logic;
  signal fifo_full      : std_logic;
  signal fifo_empty     : std_logic;
  signal fifo_prog_full : std_logic;
  signal fifo_data_count : std_logic_vector(12 downto 0);

  signal gpio_tri_o : std_logic_vector(31 downto 0);
  signal color : std_logic_vector(23 downto 0);

  signal axi_reader_busy      : std_logic;
  signal axi_reader_request   : std_logic;
  signal axi_reader_addr      : unsigned(31 downto 0);
  signal axi_reader_addr_next : unsigned(31 downto 0);
  signal axi_reader_fifo_dout : std_logic_vector(63 downto 0);
  signal axi_reader_fifo_we   : std_logic;

  component fifo_generator_0
    port(
      wr_clk        : in  std_logic;
      rd_clk        : in  std_logic;
      rst           : in  std_logic;
      full          : out std_logic;
      din           : in  std_logic_vector(23 downto 0);
      wr_en         : in  std_logic;
      empty         : out std_logic;
      dout          : out std_logic_vector(23 downto 0);
      rd_en         : in  std_logic;
      prog_full     : out std_logic;
      rd_data_count : out std_logic_vector(12 downto 0);
      wr_data_count : out std_logic_vector(12 downto 0)
      );
  end component fifo_generator_0;

  component fifo_generator_1
    port(
      wr_clk        : in  std_logic;
      rd_clk        : in  std_logic;
      rst           : in  std_logic;
      full          : out std_logic;
      din           : in  std_logic_vector(63 downto 0);
      wr_en         : in  std_logic;
      empty         : out std_logic;
      dout          : out std_logic_vector(63 downto 0);
      rd_en         : in  std_logic;
      prog_full     : out std_logic;
      rd_data_count : out std_logic_vector(11 downto 0);
      wr_data_count : out std_logic_vector(11 downto 0)
      );
  end component fifo_generator_1;

  signal fifo64_full      : std_logic;
  signal fifo64_din       : std_logic_vector(63 downto 0);
  signal fifo64_wr_en     : std_logic;
  signal fifo64_empty     : std_logic;
  signal fifo64_dout      : std_logic_vector(63 downto 0);
  signal fifo64_rd_en     : std_logic;
  signal fifo64_prog_full : std_logic;
  signal fifo64_rd_count  : std_logic_vector(11 downto 0);
  signal fifo64_wr_count  : std_logic_vector(11 downto 0);

  component axi_reader_64
    port (
      clk : in std_logic;
      reset : in std_logic;
      fifo_dout : out std_logic_vector(64-1 downto 0);
      fifo_we : out std_logic;
      fifo_wclk : out std_logic;
      fifo_length : in std_logic_vector(32-1 downto 0);
      fifo_full : in std_logic;
      S_AXI_ARADDR : out std_logic_vector(32-1 downto 0);
      S_AXI_ARLEN : out std_logic_vector(8-1 downto 0);
      S_AXI_ARSIZE : out std_logic_vector(3-1 downto 0);
      S_AXI_ARBURST : out std_logic_vector(2-1 downto 0);
      S_AXI_ARCACHE : out std_logic_vector(4-1 downto 0);
      S_AXI_ARPROT : out std_logic_vector(3-1 downto 0);
      S_AXI_ARVALID : out std_logic;
      S_AXI_ARREADY : in std_logic;
      S_AXI_RDATA : in std_logic_vector(64-1 downto 0);
      S_AXI_RRESP : in std_logic_vector(2-1 downto 0);
      S_AXI_RLAST : in std_logic;
      S_AXI_RVALID : in std_logic;
      S_AXI_RREADY : out std_logic;
      request : in std_logic;
      busy : out std_logic;
      addr : in std_logic_vector(32-1 downto 0);
      len : in std_logic_vector(8-1 downto 0)
      );
  end component axi_reader_64;

  component conv64to24
    port (
      clk : in std_logic;
      reset : in std_logic;
      fifo64_re : out std_logic;
      fifo64_din : in std_logic_vector(64-1 downto 0);
      fifo64_empty : in std_logic;
      fifo64_rd_count : in std_logic_vector(32-1 downto 0);
      fifo24_we : out std_logic;
      fifo24_dout : out std_logic_vector(24-1 downto 0);
      fifo24_full : in std_logic;
      fifo24_wr_count : in std_logic_vector(32-1 downto 0)
      );
  end component conv64to24;

  signal test_counter : unsigned(31 downto 0) := (others => '0');
  signal fifo_empty_reg : std_logic := '0';
  signal fifo_prog_full_counter : unsigned(31 downto 0) := (others => '0');
  signal fifo2dvi_wakeup : std_logic := '0';

  signal gpio_tri_o_clk_ibufg_0 : std_logic_vector(31 downto 0);
  signal gpio_tri_o_clk_ibufg_1 : std_logic_vector(31 downto 0);

  component synthesijer_lib_axi_SimpleAXIMemIface32RTLTest
    port (
      clk                    : in  std_logic;
      reset                  : in  std_logic;
      obj_forbid : in std_logic;
      obj_axi_reader_ARADDR  : out std_logic_vector(32-1 downto 0);
      obj_axi_reader_ARLEN   : out std_logic_vector(8-1 downto 0);
      obj_axi_reader_ARVALID : out std_logic;
      obj_axi_reader_ARREADY : in  std_logic;
      obj_axi_reader_ARSIZE  : out std_logic_vector(3-1 downto 0);
      obj_axi_reader_ARBURST : out std_logic_vector(2-1 downto 0);
      obj_axi_reader_ARCACHE : out std_logic_vector(4-1 downto 0);
      obj_axi_reader_ARPROT  : out std_logic_vector(3-1 downto 0);
      obj_axi_reader_RDATA   : in  std_logic_vector(32-1 downto 0);
      obj_axi_reader_RRESP   : in  std_logic_vector(2-1 downto 0);
      obj_axi_reader_RLAST   : in  std_logic;
      obj_axi_reader_RVALID  : in  std_logic;
      obj_axi_reader_RREADY  : out std_logic;
      obj_axi_writer_AWADDR  : out std_logic_vector(32-1 downto 0);
      obj_axi_writer_AWLEN   : out std_logic_vector(8-1 downto 0);
      obj_axi_writer_AWVALID : out std_logic;
      obj_axi_writer_AWSIZE  : out std_logic_vector(3-1 downto 0);
      obj_axi_writer_AWBURST : out std_logic_vector(2-1 downto 0);
      obj_axi_writer_AWCACHE : out std_logic_vector(4-1 downto 0);
      obj_axi_writer_AWPROT  : out std_logic_vector(3-1 downto 0);
      obj_axi_writer_AWREADY : in  std_logic;
      obj_axi_writer_WDATA   : out std_logic_vector(32-1 downto 0);
      obj_axi_writer_WLAST   : out std_logic;
      obj_axi_writer_WVALID  : out std_logic;
      obj_axi_writer_WREADY  : in  std_logic;
      obj_axi_writer_WSTRB   : out std_logic_vector(4-1 downto 0);
      obj_axi_writer_BRESP   : in  std_logic_vector(2-1 downto 0);
      obj_axi_writer_BVALID  : in  std_logic;
      obj_axi_writer_BREADY  : out std_logic;
      write_data_addr_in     : in  signed(32-1 downto 0);
      write_data_data_in     : in  signed(32-1 downto 0);
      write_data_req         : in  std_logic;
      write_data_busy        : out std_logic;
      read_data_addr_in      : in  signed(32-1 downto 0);
      read_data_return       : out signed(32-1 downto 0);
      read_data_req          : in  std_logic;
      read_data_busy         : out std_logic;
      test_req               : in  std_logic;
      test_busy              : out std_logic
      );
  end component synthesijer_lib_axi_SimpleAXIMemIface32RTLTest;

  component RGBTest
    port (
      clk : in std_logic;
      reset : in std_logic;
      obj_obj_obj_forbid : in std_logic;
      obj_obj_obj_axi_reader_ARADDR : out std_logic_vector(32-1 downto 0);
      obj_obj_obj_axi_reader_ARLEN : out std_logic_vector(8-1 downto 0);
      obj_obj_obj_axi_reader_ARVALID : out std_logic;
      obj_obj_obj_axi_reader_ARREADY : in std_logic;
      obj_obj_obj_axi_reader_ARSIZE : out std_logic_vector(3-1 downto 0);
      obj_obj_obj_axi_reader_ARBURST : out std_logic_vector(2-1 downto 0);
      obj_obj_obj_axi_reader_ARCACHE : out std_logic_vector(4-1 downto 0);
      obj_obj_obj_axi_reader_ARPROT : out std_logic_vector(3-1 downto 0);
      obj_obj_obj_axi_reader_RDATA : in std_logic_vector(32-1 downto 0);
      obj_obj_obj_axi_reader_RRESP : in std_logic_vector(2-1 downto 0);
      obj_obj_obj_axi_reader_RLAST : in std_logic;
      obj_obj_obj_axi_reader_RVALID : in std_logic;
      obj_obj_obj_axi_reader_RREADY : out std_logic;
      obj_obj_obj_axi_writer_AWADDR : out std_logic_vector(32-1 downto 0);
      obj_obj_obj_axi_writer_AWLEN : out std_logic_vector(8-1 downto 0);
      obj_obj_obj_axi_writer_AWVALID : out std_logic;
      obj_obj_obj_axi_writer_AWSIZE : out std_logic_vector(3-1 downto 0);
      obj_obj_obj_axi_writer_AWBURST : out std_logic_vector(2-1 downto 0);
      obj_obj_obj_axi_writer_AWCACHE : out std_logic_vector(4-1 downto 0);
      obj_obj_obj_axi_writer_AWPROT : out std_logic_vector(3-1 downto 0);
      obj_obj_obj_axi_writer_AWREADY : in std_logic;
      obj_obj_obj_axi_writer_WDATA : out std_logic_vector(32-1 downto 0);
      obj_obj_obj_axi_writer_WLAST : out std_logic;
      obj_obj_obj_axi_writer_WVALID : out std_logic;
      obj_obj_obj_axi_writer_WREADY : in std_logic;
      obj_obj_obj_axi_writer_WSTRB : out std_logic_vector(4-1 downto 0);
      obj_obj_obj_axi_writer_BRESP : in std_logic_vector(2-1 downto 0);
      obj_obj_obj_axi_writer_BVALID : in std_logic;
      obj_obj_obj_axi_writer_BREADY : out std_logic;
      run_req : in std_logic;
      run_busy : out std_logic
      );
  end component RGBTest;

  signal obj_axi_reader_ARADDR  : std_logic_vector(32-1 downto 0) := (others => '0');
  signal obj_axi_reader_ARLEN   : std_logic_vector(8-1 downto 0) := (others => '0');
  signal obj_axi_reader_ARVALID : std_logic := '0';
  signal obj_axi_reader_ARREADY : std_logic := '0';
  signal obj_axi_reader_ARSIZE  : std_logic_vector(3-1 downto 0) := (others => '0');
  signal obj_axi_reader_ARBURST : std_logic_vector(2-1 downto 0) := (others => '0');
  signal obj_axi_reader_ARCACHE : std_logic_vector(4-1 downto 0) := (others => '0');
  signal obj_axi_reader_ARPROT  : std_logic_vector(3-1 downto 0) := (others => '0');
  signal obj_axi_reader_RDATA   : std_logic_vector(32-1 downto 0) := (others => '0');
  signal obj_axi_reader_RRESP   : std_logic_vector(2-1 downto 0) := (others => '0');
  signal obj_axi_reader_RLAST   : std_logic := '0';
  signal obj_axi_reader_RVALID  : std_logic := '0';
  signal obj_axi_reader_RREADY  : std_logic := '0';
  signal obj_axi_writer_AWADDR  : std_logic_vector(32-1 downto 0) := (others => '0');
  signal obj_axi_writer_AWLEN   : std_logic_vector(8-1 downto 0) := (others => '0');
  signal obj_axi_writer_AWVALID : std_logic := '0';
  signal obj_axi_writer_AWSIZE  : std_logic_vector(3-1 downto 0) := (others => '0');
  signal obj_axi_writer_AWBURST : std_logic_vector(2-1 downto 0) := (others => '0');
  signal obj_axi_writer_AWCACHE : std_logic_vector(4-1 downto 0) := (others => '0');
  signal obj_axi_writer_AWPROT  : std_logic_vector(3-1 downto 0) := (others => '0');
  signal obj_axi_writer_AWREADY : std_logic := '0';
  signal obj_axi_writer_WDATA   : std_logic_vector(32-1 downto 0) := (others => '0');
  signal obj_axi_writer_WLAST   : std_logic := '0';
  signal obj_axi_writer_WVALID  : std_logic := '0';
  signal obj_axi_writer_WREADY  : std_logic := '0';
  signal obj_axi_writer_WSTRB   : std_logic_vector(4-1 downto 0) := (others => '0');
  signal obj_axi_writer_BRESP   : std_logic_vector(2-1 downto 0) := (others => '0');
  signal obj_axi_writer_BVALID  : std_logic := '0';
  signal obj_axi_writer_BREADY  : std_logic := '0';
  signal write_data_addr_in     : signed(32-1 downto 0) := (others => '0');
  signal write_data_data_in     : signed(32-1 downto 0) := (others => '0');
  signal write_data_req         : std_logic := '0';
  signal write_data_busy        : std_logic := '0';
  signal read_data_addr_in      : signed(32-1 downto 0) := (others => '0');
  signal read_data_return       : signed(32-1 downto 0) := (others => '0');
  signal read_data_req          : std_logic := '0';
  signal read_data_busy         : std_logic := '0';
  signal test_req               : std_logic := '0';
  signal test_busy              : std_logic := '0';
  
  attribute MARK_DEBUG of obj_axi_reader_ARADDR  : signal is "TRUE";
  attribute MARK_DEBUG of obj_axi_reader_ARLEN   : signal is "TRUE";
  attribute MARK_DEBUG of obj_axi_reader_ARVALID : signal is "TRUE";
  attribute MARK_DEBUG of obj_axi_reader_ARREADY : signal is "TRUE";
  attribute MARK_DEBUG of obj_axi_reader_ARSIZE  : signal is "TRUE";
  attribute MARK_DEBUG of obj_axi_reader_ARBURST : signal is "TRUE";
  attribute MARK_DEBUG of obj_axi_reader_ARCACHE : signal is "TRUE";
  attribute MARK_DEBUG of obj_axi_reader_ARPROT  : signal is "TRUE";
  attribute MARK_DEBUG of obj_axi_reader_RDATA   : signal is "TRUE";
  attribute MARK_DEBUG of obj_axi_reader_RRESP   : signal is "TRUE";
  attribute MARK_DEBUG of obj_axi_reader_RLAST   : signal is "TRUE";
  attribute MARK_DEBUG of obj_axi_reader_RVALID  : signal is "TRUE";
  attribute MARK_DEBUG of obj_axi_reader_RREADY  : signal is "TRUE";
  attribute MARK_DEBUG of obj_axi_writer_AWADDR  : signal is "TRUE";
  attribute MARK_DEBUG of obj_axi_writer_AWLEN   : signal is "TRUE";
  attribute MARK_DEBUG of obj_axi_writer_AWVALID : signal is "TRUE";
  attribute MARK_DEBUG of obj_axi_writer_AWSIZE  : signal is "TRUE";
  attribute MARK_DEBUG of obj_axi_writer_AWBURST : signal is "TRUE";
  attribute MARK_DEBUG of obj_axi_writer_AWCACHE : signal is "TRUE";
  attribute MARK_DEBUG of obj_axi_writer_AWPROT  : signal is "TRUE";
  attribute MARK_DEBUG of obj_axi_writer_AWREADY : signal is "TRUE";
  attribute MARK_DEBUG of obj_axi_writer_WDATA   : signal is "TRUE";
  attribute MARK_DEBUG of obj_axi_writer_WLAST   : signal is "TRUE";
  attribute MARK_DEBUG of obj_axi_writer_WVALID  : signal is "TRUE";
  attribute MARK_DEBUG of obj_axi_writer_WREADY  : signal is "TRUE";
  attribute MARK_DEBUG of obj_axi_writer_WSTRB   : signal is "TRUE";
  attribute MARK_DEBUG of obj_axi_writer_BRESP   : signal is "TRUE";
  attribute MARK_DEBUG of obj_axi_writer_BVALID  : signal is "TRUE";
  attribute MARK_DEBUG of obj_axi_writer_BREADY  : signal is "TRUE";
  attribute MARK_DEBUG of write_data_addr_in     : signal is "TRUE";
  attribute MARK_DEBUG of write_data_data_in     : signal is "TRUE";
  attribute MARK_DEBUG of write_data_req         : signal is "TRUE";
  attribute MARK_DEBUG of write_data_busy        : signal is "TRUE";
  attribute MARK_DEBUG of read_data_addr_in      : signal is "TRUE";
  attribute MARK_DEBUG of read_data_return       : signal is "TRUE";
  attribute MARK_DEBUG of read_data_req          : signal is "TRUE";
  attribute MARK_DEBUG of read_data_busy         : signal is "TRUE";
  attribute MARK_DEBUG of test_req               : signal is "TRUE";
  attribute MARK_DEBUG of test_busy              : signal is "TRUE";

  attribute KEEP of obj_axi_reader_ARADDR  : signal is "TRUE";
  attribute KEEP of obj_axi_reader_ARLEN   : signal is "TRUE";
  attribute KEEP of obj_axi_reader_ARVALID : signal is "TRUE";
  attribute KEEP of obj_axi_reader_ARREADY : signal is "TRUE";
  attribute KEEP of obj_axi_reader_ARSIZE  : signal is "TRUE";
  attribute KEEP of obj_axi_reader_ARBURST : signal is "TRUE";
  attribute KEEP of obj_axi_reader_ARCACHE : signal is "TRUE";
  attribute KEEP of obj_axi_reader_ARPROT  : signal is "TRUE";
  attribute KEEP of obj_axi_reader_RDATA   : signal is "TRUE";
  attribute KEEP of obj_axi_reader_RRESP   : signal is "TRUE";
  attribute KEEP of obj_axi_reader_RLAST   : signal is "TRUE";
  attribute KEEP of obj_axi_reader_RVALID  : signal is "TRUE";
  attribute KEEP of obj_axi_reader_RREADY  : signal is "TRUE";
  attribute KEEP of obj_axi_writer_AWADDR  : signal is "TRUE";
  attribute KEEP of obj_axi_writer_AWLEN   : signal is "TRUE";
  attribute KEEP of obj_axi_writer_AWVALID : signal is "TRUE";
  attribute KEEP of obj_axi_writer_AWSIZE  : signal is "TRUE";
  attribute KEEP of obj_axi_writer_AWBURST : signal is "TRUE";
  attribute KEEP of obj_axi_writer_AWCACHE : signal is "TRUE";
  attribute KEEP of obj_axi_writer_AWPROT  : signal is "TRUE";
  attribute KEEP of obj_axi_writer_AWREADY : signal is "TRUE";
  attribute KEEP of obj_axi_writer_WDATA   : signal is "TRUE";
  attribute KEEP of obj_axi_writer_WLAST   : signal is "TRUE";
  attribute KEEP of obj_axi_writer_WVALID  : signal is "TRUE";
  attribute KEEP of obj_axi_writer_WREADY  : signal is "TRUE";
  attribute KEEP of obj_axi_writer_WSTRB   : signal is "TRUE";
  attribute KEEP of obj_axi_writer_BRESP   : signal is "TRUE";
  attribute KEEP of obj_axi_writer_BVALID  : signal is "TRUE";
  attribute KEEP of obj_axi_writer_BREADY  : signal is "TRUE";
  attribute KEEP of write_data_addr_in     : signal is "TRUE";
  attribute KEEP of write_data_data_in     : signal is "TRUE";
  attribute KEEP of write_data_req         : signal is "TRUE";
  attribute KEEP of write_data_busy        : signal is "TRUE";
  attribute KEEP of read_data_addr_in      : signal is "TRUE";
  attribute KEEP of read_data_return       : signal is "TRUE";
  attribute KEEP of read_data_req          : signal is "TRUE";
  attribute KEEP of read_data_busy         : signal is "TRUE";
  attribute KEEP of test_req               : signal is "TRUE";
  attribute KEEP of test_busy              : signal is "TRUE";

  signal obj_forbid : std_logic := '1';

  signal fifo2dvi_wakeup_clk0_0, fifo2dvi_wakeup_clk0_1 : std_logic := '0';

  component TestCanvas
  port (
    clk : in std_logic;
    reset : in std_logic;
    obj_obj_forbid : in std_logic;
    obj_obj_axi_reader_ARADDR : out std_logic_vector(32-1 downto 0);
    obj_obj_axi_reader_ARLEN : out std_logic_vector(8-1 downto 0);
    obj_obj_axi_reader_ARVALID : out std_logic;
    obj_obj_axi_reader_ARREADY : in std_logic;
    obj_obj_axi_reader_ARSIZE : out std_logic_vector(3-1 downto 0);
    obj_obj_axi_reader_ARBURST : out std_logic_vector(2-1 downto 0);
    obj_obj_axi_reader_ARCACHE : out std_logic_vector(4-1 downto 0);
    obj_obj_axi_reader_ARPROT : out std_logic_vector(3-1 downto 0);
    obj_obj_axi_reader_RDATA : in std_logic_vector(32-1 downto 0);
    obj_obj_axi_reader_RRESP : in std_logic_vector(2-1 downto 0);
    obj_obj_axi_reader_RLAST : in std_logic;
    obj_obj_axi_reader_RVALID : in std_logic;
    obj_obj_axi_reader_RREADY : out std_logic;
    obj_obj_axi_writer_AWADDR : out std_logic_vector(32-1 downto 0);
    obj_obj_axi_writer_AWLEN : out std_logic_vector(8-1 downto 0);
    obj_obj_axi_writer_AWVALID : out std_logic;
    obj_obj_axi_writer_AWSIZE : out std_logic_vector(3-1 downto 0);
    obj_obj_axi_writer_AWBURST : out std_logic_vector(2-1 downto 0);
    obj_obj_axi_writer_AWCACHE : out std_logic_vector(4-1 downto 0);
    obj_obj_axi_writer_AWPROT : out std_logic_vector(3-1 downto 0);
    obj_obj_axi_writer_AWREADY : in std_logic;
    obj_obj_axi_writer_WDATA : out std_logic_vector(32-1 downto 0);
    obj_obj_axi_writer_WLAST : out std_logic;
    obj_obj_axi_writer_WVALID : out std_logic;
    obj_obj_axi_writer_WREADY : in std_logic;
    obj_obj_axi_writer_WSTRB : out std_logic_vector(4-1 downto 0);
    obj_obj_axi_writer_BRESP : in std_logic_vector(2-1 downto 0);
    obj_obj_axi_writer_BVALID : in std_logic;
    obj_obj_axi_writer_BREADY : out std_logic;
    pset_x_in : in signed(32-1 downto 0);
    pset_y_in : in signed(32-1 downto 0);
    pset_color_in : in signed(32-1 downto 0);
    pset_req : in std_logic;
    pset_busy : out std_logic;
    test_req : in std_logic;
    test_busy : out std_logic
  );
  end component TestCanvas;

begin
  
  design_1_i : component design_1
    port map (
      DDR_addr(14 downto 0)                   => DDR_addr(14 downto 0),
      DDR_ba(2 downto 0)                      => DDR_ba(2 downto 0),
      DDR_cas_n                               => DDR_cas_n,
      DDR_ck_n                                => DDR_ck_n,
      DDR_ck_p                                => DDR_ck_p,
      DDR_cke                                 => DDR_cke,
      DDR_cs_n                                => DDR_cs_n,
      DDR_dm(3 downto 0)                      => DDR_dm(3 downto 0),
      DDR_dq(31 downto 0)                     => DDR_dq(31 downto 0),
      DDR_dqs_n(3 downto 0)                   => DDR_dqs_n(3 downto 0),
      DDR_dqs_p(3 downto 0)                   => DDR_dqs_p(3 downto 0),
      DDR_odt                                 => DDR_odt,
      DDR_ras_n                               => DDR_ras_n,
      DDR_reset_n                             => DDR_reset_n,
      DDR_we_n                                => DDR_we_n,
      FCLK_CLK0                               => FCLK_CLK0,
      FCLK_RESET0_N                           => FCLK_RESET0_N,
      FIXED_IO_ddr_vrn                        => FIXED_IO_ddr_vrn,
      FIXED_IO_ddr_vrp                        => FIXED_IO_ddr_vrp,
      FIXED_IO_mio(53 downto 0)               => FIXED_IO_mio(53 downto 0),
      FIXED_IO_ps_clk                         => FIXED_IO_ps_clk,
      FIXED_IO_ps_porb                        => FIXED_IO_ps_porb,
      FIXED_IO_ps_srstb                       => FIXED_IO_ps_srstb,
      FCLK_CLK1                               => FCLK_CLK1,
      FCLK_RESET1_N                           => FCLK_RESET1_N,
      S_AXI_HP0_FIFO_CTRL_racount(2 downto 0) => S_AXI_HP0_FIFO_CTRL_racount(2 downto 0),
      S_AXI_HP0_FIFO_CTRL_rcount(7 downto 0)  => S_AXI_HP0_FIFO_CTRL_rcount(7 downto 0),
      S_AXI_HP0_FIFO_CTRL_rdissuecapen        => S_AXI_HP0_FIFO_CTRL_rdissuecapen,
      S_AXI_HP0_FIFO_CTRL_wacount(5 downto 0) => S_AXI_HP0_FIFO_CTRL_wacount(5 downto 0),
      S_AXI_HP0_FIFO_CTRL_wcount(7 downto 0)  => S_AXI_HP0_FIFO_CTRL_wcount(7 downto 0),
      S_AXI_HP0_FIFO_CTRL_wrissuecapen        => S_AXI_HP0_FIFO_CTRL_wrissuecapen,
      S_AXI_HP0_araddr(31 downto 0)           => S_AXI_HP0_araddr(31 downto 0),
      S_AXI_HP0_arburst(1 downto 0)           => S_AXI_HP0_arburst(1 downto 0),
      S_AXI_HP0_arcache(3 downto 0)           => S_AXI_HP0_arcache(3 downto 0),
      S_AXI_HP0_arid(5 downto 0)              => S_AXI_HP0_arid(5 downto 0),
      S_AXI_HP0_arlen(3 downto 0)             => S_AXI_HP0_arlen(3 downto 0),
      S_AXI_HP0_arlock(1 downto 0)            => S_AXI_HP0_arlock(1 downto 0),
      S_AXI_HP0_arprot(2 downto 0)            => S_AXI_HP0_arprot(2 downto 0),
      S_AXI_HP0_arqos(3 downto 0)             => S_AXI_HP0_arqos(3 downto 0),
      S_AXI_HP0_arready                       => S_AXI_HP0_arready,
      S_AXI_HP0_arsize(2 downto 0)            => S_AXI_HP0_arsize(2 downto 0),
      S_AXI_HP0_arvalid                       => S_AXI_HP0_arvalid,
      S_AXI_HP0_awaddr(31 downto 0)           => S_AXI_HP0_awaddr(31 downto 0),
      S_AXI_HP0_awburst(1 downto 0)           => S_AXI_HP0_awburst(1 downto 0),
      S_AXI_HP0_awcache(3 downto 0)           => S_AXI_HP0_awcache(3 downto 0),
      S_AXI_HP0_awid(5 downto 0)              => S_AXI_HP0_awid(5 downto 0),
      S_AXI_HP0_awlen(3 downto 0)             => S_AXI_HP0_awlen(3 downto 0),
      S_AXI_HP0_awlock(1 downto 0)            => S_AXI_HP0_awlock(1 downto 0),
      S_AXI_HP0_awprot(2 downto 0)            => S_AXI_HP0_awprot(2 downto 0),
      S_AXI_HP0_awqos(3 downto 0)             => S_AXI_HP0_awqos(3 downto 0),
      S_AXI_HP0_awready                       => S_AXI_HP0_awready,
      S_AXI_HP0_awsize(2 downto 0)            => S_AXI_HP0_awsize(2 downto 0),
      S_AXI_HP0_awvalid                       => S_AXI_HP0_awvalid,
      S_AXI_HP0_bid(5 downto 0)               => S_AXI_HP0_bid(5 downto 0),
      S_AXI_HP0_bready                        => S_AXI_HP0_bready,
      S_AXI_HP0_bresp(1 downto 0)             => S_AXI_HP0_bresp(1 downto 0),
      S_AXI_HP0_bvalid                        => S_AXI_HP0_bvalid,
      S_AXI_HP0_rdata(63 downto 0)            => S_AXI_HP0_rdata(63 downto 0),
      S_AXI_HP0_rid(5 downto 0)               => S_AXI_HP0_rid(5 downto 0),
      S_AXI_HP0_rlast                         => S_AXI_HP0_rlast,
      S_AXI_HP0_rready                        => S_AXI_HP0_rready,
      S_AXI_HP0_rresp(1 downto 0)             => S_AXI_HP0_rresp(1 downto 0),
      S_AXI_HP0_rvalid                        => S_AXI_HP0_rvalid,
      S_AXI_HP0_wdata(63 downto 0)            => S_AXI_HP0_wdata(63 downto 0),
      S_AXI_HP0_wid(5 downto 0)               => S_AXI_HP0_wid(5 downto 0),
      S_AXI_HP0_wlast                         => S_AXI_HP0_wlast,
      S_AXI_HP0_wready                        => S_AXI_HP0_wready,
      S_AXI_HP0_wstrb(7 downto 0)             => S_AXI_HP0_wstrb(7 downto 0),
      S_AXI_HP0_wvalid                        => S_AXI_HP0_wvalid,
      S_AXI_HP2_FIFO_CTRL_racount(2 downto 0) => S_AXI_HP2_FIFO_CTRL_racount(2 downto 0),
      S_AXI_HP2_FIFO_CTRL_rcount(7 downto 0)  => S_AXI_HP2_FIFO_CTRL_rcount(7 downto 0),
      S_AXI_HP2_FIFO_CTRL_rdissuecapen        => S_AXI_HP2_FIFO_CTRL_rdissuecapen,
      S_AXI_HP2_FIFO_CTRL_wacount(5 downto 0) => S_AXI_HP2_FIFO_CTRL_wacount(5 downto 0),
      S_AXI_HP2_FIFO_CTRL_wcount(7 downto 0)  => S_AXI_HP2_FIFO_CTRL_wcount(7 downto 0),
      S_AXI_HP2_FIFO_CTRL_wrissuecapen        => S_AXI_HP2_FIFO_CTRL_wrissuecapen,
      S_AXI_HP2_araddr(31 downto 0)           => S_AXI_HP2_araddr(31 downto 0),
      S_AXI_HP2_arburst(1 downto 0)           => S_AXI_HP2_arburst(1 downto 0),
      S_AXI_HP2_arcache(3 downto 0)           => S_AXI_HP2_arcache(3 downto 0),
      S_AXI_HP2_arid(5 downto 0)              => S_AXI_HP2_arid(5 downto 0),
      S_AXI_HP2_arlen(3 downto 0)             => S_AXI_HP2_arlen(3 downto 0),
      S_AXI_HP2_arlock(1 downto 0)            => S_AXI_HP2_arlock(1 downto 0),
      S_AXI_HP2_arprot(2 downto 0)            => S_AXI_HP2_arprot(2 downto 0),
      S_AXI_HP2_arqos(3 downto 0)             => S_AXI_HP2_arqos(3 downto 0),
      S_AXI_HP2_arready                       => S_AXI_HP2_arready,
      S_AXI_HP2_arsize(2 downto 0)            => S_AXI_HP2_arsize(2 downto 0),
      S_AXI_HP2_arvalid                       => S_AXI_HP2_arvalid,
      S_AXI_HP2_awaddr(31 downto 0)           => S_AXI_HP2_awaddr(31 downto 0),
      S_AXI_HP2_awburst(1 downto 0)           => S_AXI_HP2_awburst(1 downto 0),
      S_AXI_HP2_awcache(3 downto 0)           => S_AXI_HP2_awcache(3 downto 0),
      S_AXI_HP2_awid(5 downto 0)              => S_AXI_HP2_awid(5 downto 0),
      S_AXI_HP2_awlen(3 downto 0)             => S_AXI_HP2_awlen(3 downto 0),
      S_AXI_HP2_awlock(1 downto 0)            => S_AXI_HP2_awlock(1 downto 0),
      S_AXI_HP2_awprot(2 downto 0)            => S_AXI_HP2_awprot(2 downto 0),
      S_AXI_HP2_awqos(3 downto 0)             => S_AXI_HP2_awqos(3 downto 0),
      S_AXI_HP2_awready                       => S_AXI_HP2_awready,
      S_AXI_HP2_awsize(2 downto 0)            => S_AXI_HP2_awsize(2 downto 0),
      S_AXI_HP2_awvalid                       => S_AXI_HP2_awvalid,
      S_AXI_HP2_bid(5 downto 0)               => S_AXI_HP2_bid(5 downto 0),
      S_AXI_HP2_bready                        => S_AXI_HP2_bready,
      S_AXI_HP2_bresp(1 downto 0)             => S_AXI_HP2_bresp(1 downto 0),
      S_AXI_HP2_bvalid                        => S_AXI_HP2_bvalid,
      S_AXI_HP2_rdata(31 downto 0)            => S_AXI_HP2_rdata(31 downto 0),
      S_AXI_HP2_rid(5 downto 0)               => S_AXI_HP2_rid(5 downto 0),
      S_AXI_HP2_rlast                         => S_AXI_HP2_rlast,
      S_AXI_HP2_rready                        => S_AXI_HP2_rready,
      S_AXI_HP2_rresp(1 downto 0)             => S_AXI_HP2_rresp(1 downto 0),
      S_AXI_HP2_rvalid                        => S_AXI_HP2_rvalid,
      S_AXI_HP2_wdata(31 downto 0)            => S_AXI_HP2_wdata(31 downto 0),
      S_AXI_HP2_wid(5 downto 0)               => S_AXI_HP2_wid(5 downto 0),
      S_AXI_HP2_wlast                         => S_AXI_HP2_wlast,
      S_AXI_HP2_wready                        => S_AXI_HP2_wready,
      S_AXI_HP2_wstrb(3 downto 0)             => S_AXI_HP2_wstrb(3 downto 0),
      S_AXI_HP2_wvalid                        => S_AXI_HP2_wvalid,
      ext_reset_in => '1',
      gpio_tri_o => gpio_tri_o
      );

  LED(7) <= '0' when counter(25) = '1' else '1';
  LED(6) <= not axi_reader_request;
  LED(5) <= not axi_reader_busy;
  LED(4) <= not fifo64_empty;
  LED(3) <= not fifo_wr_en;
  LED(2) <= not fifo_rd_en;
--LED(1) <= not fifo_empty;
  LED(1) <= not fifo_empty_reg;
  LED(0) <= not fifo_prog_full;

--LED <= gpio_tri_o(7 downto 0);

  process(FCLK_CLK1)
  begin
    if FCLK_CLK1'event and FCLK_CLK1 = '1' then
      if FCLK_RESET1_N = '0' then
        counter <= (others => '0');
      else
        counter <= counter + 1;
      end if;
    end if;
  end process;

  U : sync_generator port map(
    clk      => clk_ibufg,
    reset    => not FCLK_RESET0_N,
    VSYNC    => s_syncgen_vs,
    HSYNC    => s_syncgen_hs,
    DE       => s_syncgen_de,
    DATA     => s_syncgen_da,
    fifo_rd  => fifo_rd_en,
    fifo_din => fifo_dout,
    wakeup   => fifo2dvi_wakeup
    );

  process(clk_ibufg)
  begin
    if clk_ibufg'event and clk_ibufg = '1' then
      gpio_tri_o_clk_ibufg_0 <= gpio_tri_o;
      gpio_tri_o_clk_ibufg_1 <= gpio_tri_o_clk_ibufg_0;
    end if;
  end process;

  process(clk_ibufg)
  begin
    if clk_ibufg'event and clk_ibufg = '1' then
      if FCLK_RESET0_N = '0' then
        fifo2dvi_wakeup <= '0';
        fifo_empty_reg <= '0';
        fifo_prog_full_counter <= (others => '0');
      else
        if fifo2dvi_wakeup = '0' then
          if fifo_prog_full = '1' and gpio_tri_o_clk_ibufg_1(0) = '1' then
            fifo_prog_full_counter <= fifo_prog_full_counter + 1;
          else
            fifo_prog_full_counter <= (others => '0');
          end if;
        end if;

        if fifo2dvi_wakeup = '0' and fifo_prog_full_counter > 200 then
          fifo2dvi_wakeup <= '1'; -- after first fifo_prog_full is asserted.
        end if;
        
        if fifo2dvi_wakeup = '0' then
          fifo_empty_reg <= '0';
        else
          if fifo_empty = '1' then
            fifo_empty_reg <= '1'; -- at least, fifo starves in a time.
          end if;
        end if;
      end if;
    end if;
  end process;

  DVI_R  <= r_dvit_da_3t(23 downto 16);
  DVI_G  <= r_dvit_da_3t(15 downto 8);
  DVI_B  <= r_dvit_da_3t(7 downto 0);
  DVI_DE <= r_dvit_de_3t;
  DVI_VS <= r_dvit_vs_3t;
  DVI_HS <= r_dvit_hs_3t;

  DVI_PWRDN <= '1';
  DVI_DKEN  <= '0';
  DVI_CTRL  <= "000";
  DVI_ISEL  <= '0';
  DVI_MSEN  <= '0';

  process(clk_ibufg)
  begin
    if clk_ibufg'event and clk_ibufg = '1' then
      if FCLK_RESET0_N = '0' then
        r_dvit_vs_1t <= '0';
        r_dvit_hs_1t <= '0';
        r_dvit_de_1t <= '0';
        r_dvit_da_1t <= X"000000";
        r_dvit_vs_2t <= '0';
        r_dvit_hs_2t <= '0';
        r_dvit_de_2t <= '0';
        r_dvit_da_2t <= X"000000";
        r_dvit_vs_3t <= '0';
        r_dvit_hs_3t <= '0';
        r_dvit_de_3t <= '0';
        r_dvit_da_3t <= X"000000";
      else
        r_dvit_vs_1t <= s_syncgen_vs;
        r_dvit_hs_1t <= s_syncgen_hs;
        r_dvit_de_1t <= s_syncgen_de;
        r_dvit_da_1t <= s_syncgen_da;
        r_dvit_vs_2t <= r_dvit_vs_1t;
        r_dvit_hs_2t <= r_dvit_hs_1t;
        r_dvit_de_2t <= r_dvit_de_1t;
        r_dvit_da_2t <= r_dvit_da_1t;
        r_dvit_vs_3t <= r_dvit_vs_2t;
        r_dvit_hs_3t <= r_dvit_hs_2t;
        r_dvit_de_3t <= r_dvit_de_2t;
        r_dvit_da_3t <= r_dvit_da_2t;
      end if;
    end if;
  end process;

  -- DVI clock
  U_DVITCLK_ODDR : ODDR
    generic map(
      DDR_CLK_EDGE => "SAME_EDGE",
      INIT         => '0',
      SRTYPE       => "SYNC"
      )
    port map(
      Q  => DVI_CLK,
      C  => clk_ibufg,
      CE => '1',
      D1 => '0',
      D2 => '1',
      R  => '0',
      S  => '0');

  U_BUFG_DVI_CLKIN : BUFG
    port map(
      I => SYS_CLK3,
      O => clk_ibufg
      );

  U_FIFO : fifo_generator_0
    port map(
      rd_clk        => clk_ibufg,
      wr_clk        => clk_ibufg,
      rst           => not FCLK_RESET0_N,
      full          => fifo_full,
      din           => fifo_din,
      wr_en         => fifo_wr_en,
      empty         => fifo_empty,
      dout          => fifo_dout,
      rd_en         => fifo_rd_en,
      prog_full     => fifo_prog_full,
      rd_data_count => open,
      wr_data_count => fifo_data_count
      );

  U_CONV64to24 : conv64to24 port map(
    clk                           => clk_ibufg,
    reset                         => not FCLK_RESET0_N,
    fifo64_re                     => fifo64_rd_en,
    fifo64_din                    => fifo64_dout,
    fifo64_empty                  => fifo64_empty,
    fifo64_rd_count(11 downto 0)  => fifo64_rd_count,
    fifo64_rd_count(31 downto 12) => (others => '0'),
    fifo24_we                     => fifo_wr_en,
    fifo24_dout                   => fifo_din,
    fifo24_full                   => fifo_prog_full,
    fifo24_wr_count(12 downto 0)  => fifo_data_count,
    fifo24_wr_count(31 downto 13) => (others => '0')
    );

  U_FIFO64 : fifo_generator_1
    port map(
      wr_clk        => FCLK_CLK1,
      rd_clk        => clk_ibufg,
      rst           => not FCLK_RESET1_N,
      full          => fifo64_full,
      din           => fifo64_din,
      wr_en         => fifo64_wr_en,
      empty         => fifo64_empty,
      dout          => fifo64_dout,
      rd_en         => fifo64_rd_en,
      prog_full     => fifo64_prog_full,
      rd_data_count => fifo64_rd_count,
      wr_data_count => fifo64_wr_count
      );

  fifo64_wr_en <= axi_reader_fifo_we;
  fifo64_din <= axi_reader_fifo_dout;

  U_AXI_READER : axi_reader_64 port map(
    clk                       => FCLK_CLK1,
    reset                     => not FCLK_RESET1_N,
    fifo_dout                 => axi_reader_fifo_dout,
    fifo_we                   => axi_reader_fifo_we,
    fifo_wclk                 => open,
    fifo_length(11 downto 0)  => fifo64_wr_count,
    fifo_length(31 downto 12) => (others => '0'),
    fifo_full                 => fifo64_prog_full,
    S_AXI_ARADDR              => S_AXI_HP0_araddr,
    S_AXI_ARLEN(3 downto 0)   => S_AXI_HP0_arlen,
    S_AXI_ARLEN(7 downto 4)   => open,
    S_AXI_ARSIZE              => S_AXI_HP0_arsize,
    S_AXI_ARBURST             => S_AXI_HP0_arburst,
    S_AXI_ARCACHE             => S_AXI_HP0_arcache,
    S_AXI_ARPROT              => S_AXI_HP0_arprot,
    S_AXI_ARVALID             => S_AXI_HP0_arvalid,
    S_AXI_ARREADY             => S_AXI_HP0_arready,
    S_AXI_RDATA               => S_AXI_HP0_rdata,
    S_AXI_RRESP               => S_AXI_HP0_rresp,
    S_AXI_RLAST               => S_AXI_HP0_rlast,
    S_AXI_RVALID              => S_AXI_HP0_rvalid,
    S_AXI_RREADY              => S_AXI_HP0_rready,
    request                   => axi_reader_request,
    busy                      => axi_reader_busy,
    addr                      => std_logic_vector(axi_reader_addr),
    len                       => X"0C"
    );

  process(FCLK_CLK1)
  begin
    if (FCLK_CLK1'event and FCLK_CLK1 = '1') then
      if FCLK_RESET1_N = '0' then
        axi_reader_addr <= X"3F000000";
        axi_reader_addr_next <= X"3F000000";
      else
        if axi_reader_busy = '0' and axi_reader_request = '0' and fifo64_prog_full = '0' and unsigned(fifo64_wr_count) < 3240 then
          axi_reader_request <= '1';
          axi_reader_addr <= axi_reader_addr_next;
          if axi_reader_addr_next < X"3F000000" + (1920 * 1080 * 3) - 96 then
            axi_reader_addr_next <= axi_reader_addr_next + 96; -- for next
          else
            axi_reader_addr_next <= X"3F000000";
          end if;
        else
          axi_reader_request <= '0';
        end if;
      end if;
    end if;
  end process;

  --U_CANVAS: TestCanvas
  --port map(
  --  clk                        => FCLK_CLK1,
  --  reset                      => not FCLK_RESET1_N,
  --  obj_obj_forbid             => obj_forbid,
  --  obj_obj_axi_reader_ARADDR  => obj_axi_reader_ARADDR,
  --  obj_obj_axi_reader_ARLEN   => obj_axi_reader_ARLEN,
  --  obj_obj_axi_reader_ARVALID => obj_axi_reader_ARVALID,
  --  obj_obj_axi_reader_ARREADY => obj_axi_reader_ARREADY,
  --  obj_obj_axi_reader_ARSIZE  => obj_axi_reader_ARSIZE,
  --  obj_obj_axi_reader_ARBURST => obj_axi_reader_ARBURST,
  --  obj_obj_axi_reader_ARCACHE => obj_axi_reader_ARCACHE,
  --  obj_obj_axi_reader_ARPROT  => obj_axi_reader_ARPROT,
  --  obj_obj_axi_reader_RDATA   => obj_axi_reader_RDATA,
  --  obj_obj_axi_reader_RRESP   => obj_axi_reader_RRESP,
  --  obj_obj_axi_reader_RLAST   => obj_axi_reader_RLAST,
  --  obj_obj_axi_reader_RVALID  => obj_axi_reader_RVALID,
  --  obj_obj_axi_reader_RREADY  => obj_axi_reader_RREADY,
  --  obj_obj_axi_writer_AWADDR  => obj_axi_writer_AWADDR,
  --  obj_obj_axi_writer_AWLEN   => obj_axi_writer_AWLEN,
  --  obj_obj_axi_writer_AWVALID => obj_axi_writer_AWVALID,
  --  obj_obj_axi_writer_AWSIZE  => obj_axi_writer_AWSIZE,
  --  obj_obj_axi_writer_AWBURST => obj_axi_writer_AWBURST,
  --  obj_obj_axi_writer_AWCACHE => obj_axi_writer_AWCACHE,
  --  obj_obj_axi_writer_AWPROT  => obj_axi_writer_AWPROT,
  --  obj_obj_axi_writer_AWREADY => obj_axi_writer_AWREADY,
  --  obj_obj_axi_writer_WDATA   => obj_axi_writer_WDATA,
  --  obj_obj_axi_writer_WLAST   => obj_axi_writer_WLAST,
  --  obj_obj_axi_writer_WVALID  => obj_axi_writer_WVALID,
  --  obj_obj_axi_writer_WREADY  => obj_axi_writer_WREADY,
  --  obj_obj_axi_writer_WSTRB   => obj_axi_writer_WSTRB,
  --  obj_obj_axi_writer_BRESP   => obj_axi_writer_BRESP,
  --  obj_obj_axi_writer_BVALID  => obj_axi_writer_BVALID,
  --  obj_obj_axi_writer_BREADY  => obj_axi_writer_BREADY,
  --  pset_x_in                  => (others => '0'),
  --  pset_y_in                  => (others => '0'),
  --  pset_color_in              => (others => '0'),
  --  pset_req                   => '0',
  --  pset_busy                  => open,
  --  test_req                   => test_req,
  --  test_busy                  => open
  --);

  U_TEST : RGBTest
    port map(
      clk                            => FCLK_CLK0,
      reset                          => not FCLK_RESET0_N,
      obj_obj_obj_forbid             => obj_forbid,
      obj_obj_obj_axi_reader_ARADDR  => obj_axi_reader_ARADDR,
      obj_obj_obj_axi_reader_ARLEN   => obj_axi_reader_ARLEN,
      obj_obj_obj_axi_reader_ARVALID => obj_axi_reader_ARVALID,
      obj_obj_obj_axi_reader_ARREADY => obj_axi_reader_ARREADY,
      obj_obj_obj_axi_reader_ARSIZE  => obj_axi_reader_ARSIZE,
      obj_obj_obj_axi_reader_ARBURST => obj_axi_reader_ARBURST,
      obj_obj_obj_axi_reader_ARCACHE => obj_axi_reader_ARCACHE,
      obj_obj_obj_axi_reader_ARPROT  => obj_axi_reader_ARPROT,
      obj_obj_obj_axi_reader_RDATA   => obj_axi_reader_RDATA,
      obj_obj_obj_axi_reader_RRESP   => obj_axi_reader_RRESP,
      obj_obj_obj_axi_reader_RLAST   => obj_axi_reader_RLAST,
      obj_obj_obj_axi_reader_RVALID  => obj_axi_reader_RVALID,
      obj_obj_obj_axi_reader_RREADY  => obj_axi_reader_RREADY,
      obj_obj_obj_axi_writer_AWADDR  => obj_axi_writer_AWADDR,
      obj_obj_obj_axi_writer_AWLEN   => obj_axi_writer_AWLEN,
      obj_obj_obj_axi_writer_AWVALID => obj_axi_writer_AWVALID,
      obj_obj_obj_axi_writer_AWSIZE  => obj_axi_writer_AWSIZE,
      obj_obj_obj_axi_writer_AWBURST => obj_axi_writer_AWBURST,
      obj_obj_obj_axi_writer_AWCACHE => obj_axi_writer_AWCACHE,
      obj_obj_obj_axi_writer_AWPROT  => obj_axi_writer_AWPROT,
      obj_obj_obj_axi_writer_AWREADY => obj_axi_writer_AWREADY,
      obj_obj_obj_axi_writer_WDATA   => obj_axi_writer_WDATA,
      obj_obj_obj_axi_writer_WLAST   => obj_axi_writer_WLAST,
      obj_obj_obj_axi_writer_WVALID  => obj_axi_writer_WVALID,
      obj_obj_obj_axi_writer_WREADY  => obj_axi_writer_WREADY,
      obj_obj_obj_axi_writer_WSTRB   => obj_axi_writer_WSTRB,
      obj_obj_obj_axi_writer_BRESP   => obj_axi_writer_BRESP,
      obj_obj_obj_axi_writer_BVALID  => obj_axi_writer_BVALID,
      obj_obj_obj_axi_writer_BREADY  => obj_axi_writer_BREADY,
      run_req                        => test_req,
      run_busy                       => open
      );

  --U_TEST: synthesijer_lib_axi_SimpleAXIMemIface32RTLTest port map(
  --  clk                    => FCLK_CLK1,
  --  reset                  => not FCLK_RESET1_N,
  --  obj_forbid => obj_forbid,
  --  obj_axi_reader_ARADDR  => obj_axi_reader_ARADDR,
  --  obj_axi_reader_ARLEN   => obj_axi_reader_ARLEN,
  --  obj_axi_reader_ARVALID => obj_axi_reader_ARVALID,
  --  obj_axi_reader_ARREADY => obj_axi_reader_ARREADY,
  --  obj_axi_reader_ARSIZE  => obj_axi_reader_ARSIZE,
  --  obj_axi_reader_ARBURST => obj_axi_reader_ARBURST,
  --  obj_axi_reader_ARCACHE => obj_axi_reader_ARCACHE,
  --  obj_axi_reader_ARPROT  => obj_axi_reader_ARPROT,
  --  obj_axi_reader_RDATA   => obj_axi_reader_RDATA,
  --  obj_axi_reader_RRESP   => obj_axi_reader_RRESP,
  --  obj_axi_reader_RLAST   => obj_axi_reader_RLAST,
  --  obj_axi_reader_RVALID  => obj_axi_reader_RVALID,
  --  obj_axi_reader_RREADY  => obj_axi_reader_RREADY,
  --  obj_axi_writer_AWADDR  => obj_axi_writer_AWADDR,
  --  obj_axi_writer_AWLEN   => obj_axi_writer_AWLEN,
  --  obj_axi_writer_AWVALID => obj_axi_writer_AWVALID,
  --  obj_axi_writer_AWSIZE  => obj_axi_writer_AWSIZE,
  --  obj_axi_writer_AWBURST => obj_axi_writer_AWBURST,
  --  obj_axi_writer_AWCACHE => obj_axi_writer_AWCACHE,
  --  obj_axi_writer_AWPROT  => obj_axi_writer_AWPROT,
  --  obj_axi_writer_AWREADY => obj_axi_writer_AWREADY,
  --  obj_axi_writer_WDATA   => obj_axi_writer_WDATA,
  --  obj_axi_writer_WLAST   => obj_axi_writer_WLAST,
  --  obj_axi_writer_WVALID  => obj_axi_writer_WVALID,
  --  obj_axi_writer_WREADY  => obj_axi_writer_WREADY,
  --  obj_axi_writer_WSTRB   => obj_axi_writer_WSTRB,
  --  obj_axi_writer_BRESP   => obj_axi_writer_BRESP,
  --  obj_axi_writer_BVALID  => obj_axi_writer_BVALID,
  --  obj_axi_writer_BREADY  => obj_axi_writer_BREADY,
  --  write_data_addr_in     => write_data_addr_in,
  --  write_data_data_in     => write_data_data_in,
  --  write_data_req         => write_data_req,
  --  write_data_busy        => write_data_busy,
  --  read_data_addr_in      => read_data_addr_in,
  --  read_data_return       => read_data_return,
  --  read_data_req          => read_data_req,
  --  read_data_busy         => read_data_busy,
  --  test_req               => test_req,
  --  test_busy              => test_busy
  --  );

  S_AXI_HP2_araddr  <= obj_axi_reader_ARADDR;
  S_AXI_HP2_arlen   <= obj_axi_reader_ARLEN(3 downto 0);
  S_AXI_HP2_arvalid <= obj_axi_reader_ARVALID;
  S_AXI_HP2_arsize  <= obj_axi_reader_ARSIZE;
  S_AXI_HP2_arburst <= obj_axi_reader_ARBURST;
  S_AXI_HP2_arcache <= obj_axi_reader_ARCACHE;
  S_AXI_HP2_arprot  <= obj_axi_reader_ARPROT;
  S_AXI_HP2_rready  <= obj_axi_reader_RREADY;
  S_AXI_HP2_awaddr  <= obj_axi_writer_AWADDR;
  S_AXI_HP2_awlen   <= obj_axi_writer_AWLEN(3 downto 0);
  S_AXI_HP2_awvalid <= obj_axi_writer_AWVALID;
  S_AXI_HP2_awsize  <= obj_axi_writer_AWSIZE;
  S_AXI_HP2_awburst <= obj_axi_writer_AWBURST;
  S_AXI_HP2_awcache <= obj_axi_writer_AWCACHE;
  S_AXI_HP2_awprot  <= obj_axi_writer_AWPROT;
  S_AXI_HP2_wdata   <= obj_axi_writer_WDATA;
  S_AXI_HP2_wlast   <= obj_axi_writer_WLAST;
  S_AXI_HP2_wvalid  <= obj_axi_writer_WVALID;
  S_AXI_HP2_wstrb   <= obj_axi_writer_WSTRB;
  S_AXI_HP2_bready  <= obj_axi_writer_BREADY;
  
  obj_axi_reader_ARREADY <= S_AXI_HP2_arready;
  obj_axi_reader_RDATA   <= S_AXI_HP2_rdata;
  obj_axi_reader_RRESP   <= S_AXI_HP2_rresp;
  obj_axi_reader_RLAST   <= S_AXI_HP2_rlast;
  obj_axi_reader_RVALID  <= S_AXI_HP2_rvalid;
  obj_axi_writer_AWREADY <= S_AXI_HP2_awready;
  obj_axi_writer_WREADY  <= S_AXI_HP2_wready;
  obj_axi_writer_BRESP   <= S_AXI_HP2_bresp;
  obj_axi_writer_BVALID  <= S_AXI_HP2_bvalid;

  test_req <= fifo2dvi_wakeup_clk0_1;
  
  process(FCLK_CLK0)
  begin
    if (FCLK_CLK0'event and FCLK_CLK0 = '1') then
      if FCLK_RESET0_N = '0' then
        fifo2dvi_wakeup_clk0_0 <= '0';
        fifo2dvi_wakeup_clk0_1 <= '0';
      else
        fifo2dvi_wakeup_clk0_0 <= fifo2dvi_wakeup;
        fifo2dvi_wakeup_clk0_1 <= fifo2dvi_wakeup_clk0_0;
      end if;
    end if;
  end process;
    
  process(FCLK_CLK0)
  begin
    if (FCLK_CLK0'event and FCLK_CLK0 = '1') then
      if FCLK_RESET0_N = '0' then
        obj_forbid <= '1';
      else
        if fifo2dvi_wakeup_clk0_1 = '1' and fifo_prog_full = '1' then
          obj_forbid <= '0';
        else
          obj_forbid <= '1';
        end if;
      end if;
    end if;
  end process;

end STRUCTURE;
