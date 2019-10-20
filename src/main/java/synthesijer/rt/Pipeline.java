package synthesijer.rt;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.LOCAL_VARIABLE})
public @interface Pipeline {

}
