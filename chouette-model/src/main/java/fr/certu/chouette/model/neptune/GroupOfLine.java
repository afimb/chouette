package fr.certu.chouette.model.neptune;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

/**
 * Chouette GroupOfLine : to associate lines with common purpose
 * <p/>
 * Neptune mapping : GroupOfLine <br/>
 * 
 */
@Entity
@Table(name = "group_of_lines")
@NoArgsConstructor
@Log4j
public class GroupOfLine extends NeptuneIdentifiedObject
{

   private static final long serialVersionUID = 2900948915585746984L;

   /**
    * name
    * 
    * @return The actual value
    */
   @Getter
   @Column(name = "name", nullable = false)
   private String name;

   /**
    * set name <br/>
    * truncated to 255 characters if too long
    * 
    * @param value
    *           New value
    */
   public void setName(String value)
   {
      name = dataBaseSizeProtectedValue(value, "name", log);
   }

   /**
    * comment
    * 
    * @return The actual value
    */
   @Getter
   @Column(name = "comment")
   private String comment;

   /**
    * set comment <br/>
    * truncated to 255 characters if too long
    * 
    * @param value
    *           New value
    */
   public void setComment(String value)
   {
      comment = dataBaseSizeProtectedValue(value, "comment", log);
   }

   /**
    * grouped Lines
    * 
    * @param lines
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   @ManyToMany(mappedBy = "groupOfLines")
   private List<Line> lines = new ArrayList<Line>(0);

   /**
    * grouped Lines ObjectIds <br/>
    * (import/export purpose)
    * 
    * @param lineIds
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   @Transient
   private List<String> lineIds;

   /**
    * add a lineId to list only if not already present <br/>
    * do not affect lines list
    * 
    * @param lineId
    *           lineId to remove
    */
   public void addLineId(String lineId)
   {
      if (lineIds == null)
         lineIds = new ArrayList<String>();
      if (!lineIds.contains(lineId))
         lineIds.add(lineId);
   }

   /**
    * add a line to list only if not already present <br/>
    * do not affect lineIds list
    * 
    * @param line
    *           line to add
    */
   public void addLine(Line line)
   {
      if (lines == null)
         lines = new ArrayList<Line>();
      if (!lines.contains(line))
         lines.add(line);
   }

   /**
    * remove a line from the group
    * 
    * @param line
    */
   public void removeLine(Line line)
   {
      if (lines == null)
         lines = new ArrayList<Line>();
      if (lines.contains(line))
         lines.remove(line);
   }

   /*
    * (non-Javadoc)
    * 
    * @see fr.certu.chouette.model.neptune.NeptuneIdentifiedObject#complete()
    */
   @Override
   public void complete()
   {
      if (isCompleted())
         return;
      super.complete();

      if (lines != null)
      {
         lineIds = extractObjectIds(lines);
      }
      else
      {
         lineIds = new ArrayList<String>();
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see
    * fr.certu.chouette.model.neptune.NeptuneObject#compareAttributes(fr.certu
    * .chouette.model.neptune.NeptuneObject)
    */
   @Override
   public <T extends NeptuneObject> boolean compareAttributes(T anotherObject)
   {
      if (anotherObject instanceof GroupOfLine)
      {
         GroupOfLine another = (GroupOfLine) anotherObject;
         if (!sameValue(this.getObjectId(), another.getObjectId()))
            return false;
         if (!sameValue(this.getObjectVersion(), another.getObjectVersion()))
            return false;
         if (!sameValue(this.getName(), another.getName()))
            return false;
         if (!sameValue(this.getComment(), another.getComment()))
            return false;
         if (!sameValue(this.getRegistrationNumber(), another.getRegistrationNumber()))
            return false;

         return true;
      }
      else
      {
         return false;
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see fr.certu.chouette.model.neptune.NeptuneIdentifiedObject#toURL()
    */
   @Override
   public String toURL()
   {
      return "group_of_lines/" + getId();
   }

}
