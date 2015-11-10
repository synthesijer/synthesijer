#include "xil_printf.h"
int main() {
  Xil_DCacheDisable();
  int i, mismatch = 0;
  volatile unsigned int src_data[256], dst_data[256];
  for(i = 0; i < 256; i++) src_data[i] = i;
  unsigned int *baseaddr = (unsigned int*)0x43c00000;
  xil_printf("rn");
  baseaddr[1] = (unsigned int)src_data;
  baseaddr[2] = (unsigned int)dst_data;
  baseaddr[0] = 0xFFFFFFFF;
  xil_printf("memcpy start, src=%08x dest=%08xnr", src_data, dst_data);
  while(baseaddr[0] != 0) ;
  xil_printf("memcpy donenr");
  for(i = 0; i < 256; i++){
    xil_printf("src_data[%d] = %d, ", i, src_data[i]);
    xil_printf("dst_data[%d] = %dnr", i, dst_data[i]);
    if(src_data[i] != dst_data[i]) mismatch = 1;
  }
  (mismatch==0)? xil_printf("memcpy success!nr") : xil_printf("memcpy failnr"); return 0;
}
