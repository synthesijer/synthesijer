package net.wasamon.mjlib.util;


/**
 * Base class for named object
 *
 * @version $Id: NamedObject.java,v 1.1.1.1 2003/12/29 11:46:16 miyo Exp $
 * @author Takefumi MIYOSHI
 *
 */
public class NamedObject extends Object{

  private String name;

  /**
   * constructor
   * @param name name of the object
   */
  public NamedObject(String name){
    this.name = name;
  }

  /**
   * reutrn the name of this object
   */
  public String getName(){
    return name;
  }

  public boolean equals(String n){
    return name.equals(n);
  }

}
