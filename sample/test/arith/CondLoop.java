import synthesijer.rt.*;

public class CondLoop extends Thread {

  public void run()
  {
    int a = 10;
    while (true) {
      if (a < 0) {
        break;
      }
      a--;
    }
  }
}

