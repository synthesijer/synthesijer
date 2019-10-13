package net.wasamon.mjlib.util;


/**
 * Execption in GetOpt
 *
 * @version $Id: GetOptException.java,v 1.1.1.1 2003/12/29 11:46:15 miyo Exp $
 * @author Takefumi MIYOSHI
 *
 */
public class GetOptException extends Exception{

  public GetOptException(Throwable e){
    super(e);
  }

  public GetOptException(String s){
    super(s);
  }

  public GetOptException(){
    super();
  }


}
