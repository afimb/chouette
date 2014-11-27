package fr.certu.chouette.model.neptune;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * PeerId : couple of database Id and ObjectId
 */

public class PeerId implements Serializable
{

   private static final long serialVersionUID = -8800619354993403437L;

   /**
    * id 
    * 
    * @param id
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   private Long id;
   /**
    * object id 
    * 
    * @param objectid
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   private String objectid;

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
    * @param objectid
    */
   public PeerId(Long id, String objectid)
   {
      this.id = id;
      this.objectid = objectid;
   }
}
