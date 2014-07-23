public class IO{

  public void putchar(byte c){
    System.out.print((char)c);
  }

  public byte getchar(){
    try{
      return (byte)(System.in.read());
    }catch(Exception e){
      throw new RuntimeException(e);
    }
  }

}