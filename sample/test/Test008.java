import synthesijer.lib.*;

public class Test008{

    public void run(){
	byte byte_value = 100;
	char char_value = 100;
	short short_value = 100;
	int int_value = 100;
	long long_value = 100;
	double double_value = 100;
	float float_value = 100;

	int_value = byte_value;
	byte_value = (byte)int_value;
	int_value = char_value;
	char_value = (char)int_value;
	int_value = short_value;
	short_value = (short)int_value;
	long_value = int_value;
	int_value = (int)long_value;

	float_value = byte_value;
	float_value = short_value;
	float_value = int_value;
	float_value = long_value;

	byte_value = (byte)float_value;
	short_value = (short)float_value;
	int_value = (int)float_value;
	long_value = (long)float_value;

	double_value = byte_value;
	double_value = short_value;
	double_value = int_value;
	double_value = long_value;

	byte_value = (byte)double_value;
	short_value = (short)double_value;
	int_value = (int)double_value;
	long_value = (long)double_value;

	float_value = (float)double_value;
	double_value = float_value;

	int_value = int_value;
	int_value = int_value + int_value;
	int_value = byte_value + int_value;
	int_value = int_value + byte_value;
    }
    
}
