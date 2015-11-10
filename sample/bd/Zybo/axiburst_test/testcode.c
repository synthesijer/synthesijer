#include "xil_printf.h"
int main() {
  Xil_DCacheDisable();
  int i, mismatch = 0;
  volatile unsigned int src_data[256], dst_data[256];
  for(i = 0; i < 256; i++) src_data[i] = i;
  unsigned int *baseaddr = (unsigned int*)0x43c00000;
  xil_printf("\r\n");
  baseaddr[1] = (unsigned int)src_data;
  baseaddr[2] = (unsigned int)dst_data;
  baseaddr[0] = 0xFFFFFFFF;
  xil_printf("memcpy start, src=%08x dest=%08x\n\r", src_data, dst_data);
  while(baseaddr[0] != 0) ;
  xil_printf("memcpy done\n\r");
  for(i = 0; i < 256; i++){
    xil_printf("src_data[%d] = %d, ", i, src_data[i]);
    xil_printf("dst_data[%d] = %d\n\r", i, dst_data[i]);
    if(src_data[i] != dst_data[i]) mismatch = 1;
  }
  (mismatch==0)? xil_printf("memcpy success!\n\r") : xil_printf("memcpy fail\n\r"); return 0;
}
