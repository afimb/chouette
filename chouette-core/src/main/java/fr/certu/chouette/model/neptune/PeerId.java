package fr.certu.chouette.model.neptune;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * PeerId : couple of database Id and ObjectId
 * <p/>
 * Note for fields comment : <br/>
 * when readable is added to comment, a implicit getter is available <br/>
 * when writable is added to comment, a implicit setter is available
 */

public class PeerId implements Serializable
{

   private static final long serialVersionUID = -8800619354993403437L;

   /**
    * id <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private Long              id;
   /**
    * object id <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private String            objectid;

   /**
    * default constructor
    */
   public PeerId()
   {
   }

   /**
    * complete constructor
    * 
    * @param id
    * @param objecid
    */
   public PeerId(Long id, String objecid)
   {
      this.id = id;
      this.objectid = objecid;
   }
}
