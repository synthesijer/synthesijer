package synthesijer.hdl.expr;

import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.Assert.*;

public class HDLValueTest{
	
    @Test
    public void firstTest(){
		HDLValue v1 = new HDLValue(10);
        org.junit.Assert.assertEquals("10", v1.getValue());
		
		HDLValue v2 = new HDLValue("10");
        org.junit.Assert.assertEquals("10", v2.getValue());
    }
}
