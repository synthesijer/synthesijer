import synthesijer.rt.*;

@synthesijerhdl
public class Top{
  private final Test test = new Test();

  @auto
  public boolean flag(){
    return test.flag;
  }

  @auto
  public void main(){
    test.run();
  }
}
