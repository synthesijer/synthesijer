
import synthesijer.lib.axi.SimpleAXIMemIface32RTLTest;

public class TestCanvas {
	
	SimpleAXIMemIface32RTLTest obj = new SimpleAXIMemIface32RTLTest();
	
	private void set_color_0(int base, int color){
		int v = 0;
		v = obj.read_data(base);
		v = (v & 0xFF000000) | (color & 0x00FFFFFF);
		obj.write_data(base, v);
	}
	
	private void set_color_1(int base, int color){
		int v = 0; 
		v = obj.read_data(base);
		v = (v & 0x000000FF) | ((color & 0x00FFFFFF) << 8);
		obj.write_data(base, v);
	}
	
	private void set_color_2(int base, int color){
		int v = 0;
		v = obj.read_data(base);
		v = (v & 0x0000FFFF) | ((color & 0x0000FFFF) << 16);
		obj.write_data(base, v);
		v = obj.read_data(base + 4);
		v = (v & 0xFFFFFF00) | ((color & 0x00FF0000) >> 16);
		obj.write_data(base + 4, v);
	}

	private void set_color_3(int base, int color){
		int v = 0;
		v = obj.read_data(base);
		v = (v & 0x00FFFFFF) | ((color & 0x000000FF) << 24);
		obj.write_data(base, v);
		v = obj.read_data(base + 4);
		v = (v & 0xFFFF0000) | ((color & 0x00FFFF00) >> 8);
		obj.write_data(base + 4, v);
	}
	
	public void pset(int x, int y, int color){
		
		int offset = 0x3F000000;
		
		int pt = (y << 10) + (y << 9) + (y << 8) + (y << 7) + x; // y * 1920 + x
		int base = ((pt << 1) + pt) & 0xFFFFFFFC; // (pt * 3) & 0xFFFFFFFC
		int idx =  ((pt << 1) + pt) & 0x00000003; // (pt * 3) & 0x00000003
		
		base = base + offset;
		
		int v = 0;
		
		switch(idx){
		case 0:
			set_color_0(base, color);
			break;
		case 1:
			set_color_1(base, color);
			break;
		case 2:
			set_color_2(base, color);
			break;
		case 3:
			set_color_3(base, color);
			break;
		default: break;
		}
		return;
	}
	
	public void clear(){
		for(int i = 0; i < 1080; i++){
			for(int j = 0; j < 1920; j++){
				pset(i, j, 0x00000000);
			}
		}
	}
	
	public void test(){
		
		for(int i = 30; i < 120; i++){
			for(int j = 30; j < 120; j++){
				pset(i, j, 0x00FF0000);
			}
		}
		
	}

}
