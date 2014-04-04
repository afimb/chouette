package fr.certu.chouette.plugin.model;

import java.util.Date;
import java.util.GregorianCalendar;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import lombok.Getter;
import lombok.Setter;
import fr.certu.chouette.model.neptune.NeptuneObject;

/**
 * @author michel
 * 
 */

@SuppressWarnings("serial")
@MappedSuperclass
public abstract class ActiveRecordObject extends NeptuneObject
{
   @Getter
   @Setter
   @Column(name = "created_at")
   private Date createdAt = GregorianCalendar.getInstance().getTime();

   @Getter
   @Setter
   @Column(name = "updated_at")
   private Date updatedAt = new Date(createdAt.getTime());

   @Override
   public <T extends NeptuneObject> boolean compareAttributes(T another)
   {
      return false;
   }

}
