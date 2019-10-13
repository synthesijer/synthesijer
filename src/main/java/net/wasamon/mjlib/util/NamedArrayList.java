package net.wasamon.mjlib.util;

import java.util.ArrayList;

/**
 * ArrayList for Named Object
 *
 * @version $Id: NamedArrayList.java,v 1.1.1.1 2003/12/29 11:46:16 miyo Exp $
 * @author Takefumi MIYOSHI
 *
 */
public class NamedArrayList extends ArrayList<NamedObject>{

  /**
   * Constructor
   */
  public NamedArrayList(){
    super();
  }

  /**
   * search an object with the name from ArrayList
   * @param name name of the object to search
   * @return found object
   * @throws NoSuchException There is no such object in the storage
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

