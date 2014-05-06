package net.wasamon.mjlib.util;


/**
 * 名前つきオブジェクトの基底クラス
 * 
 * @version $Id: NamedObject.java,v 1.1.1.1 2003/12/29 11:46:16 miyo Exp $
 * @author Takefumi MIYOSHI
 * 
 */
public class NamedObject extends Object{

  private String name;

  /**
   * 名前を登録し、インタンスを生成
   * @param name このオブジェクトにつける名前
   */
  public NamedObject(String name){
    this.name = name;
  }

  /**
   * このインスタンスの名前を返す
   */
  public String getName(){
    return name;
  }

  public boolean equals(String n){
    return name.equals(n);
  }

}
