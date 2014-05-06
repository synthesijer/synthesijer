package net.wasamon.mjlib.util;

import java.util.ArrayList;

/**
 * 名前付きのアレイリストクラス
 *
 * @version $Id: NamedArrayList.java,v 1.1.1.1 2003/12/29 11:46:16 miyo Exp $
 * @author Takefumi MIYOSHI
 *
 */
public class NamedArrayList extends ArrayList{
  
  /**
   * コンストラクタ
   */
  public NamedArrayList(){
    super();
  }

  /**
   * 名前をキーにしてArrayListよりオブジェクトを検索し見つけたものを返す。
   * @param name キーとなる名前
   * @return その名前のオブジェクト
   * @throws NoSuchException みつからなかった場合に発生する例外
   */
  public NamedObject search(String name) throws NoSuchException{
    NamedObject obj = null;
    for(int i = 0; i < super.size(); i++){
      obj = (NamedObject)(super.get(i));
      if(name.equals(obj.getName()) == true){
	return obj;
      }
    }
    throw new NoSuchException(name + " is not found.");
  }

  public boolean has(String name){
    try{
      search(name);
    }catch(NoSuchException e){
      return false;
    }
    return true;
  }

}

