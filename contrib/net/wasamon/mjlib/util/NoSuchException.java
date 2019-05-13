package net.wasamon.mjlib.util;

/**
 * 指定した名前のオブジェクトが見付からない場合に発生する例外
 *
 * @version $Id: NoSuchException.java,v 1.1.1.1 2003/12/29 11:46:16 miyo Exp $
 * @author Takefumi MIYOSHI
 *
 */
public class NoSuchException extends Exception{

  public NoSuchException(){
    super();
  }

  public NoSuchException(String s){
    super(s);
  }

  public NoSuchException(Throwable e){
    super(e);
  }

}
