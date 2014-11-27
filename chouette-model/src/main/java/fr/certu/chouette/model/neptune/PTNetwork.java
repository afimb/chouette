/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */
package fr.certu.chouette.model.neptune;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import fr.certu.chouette.model.neptune.type.PTNetworkSourceTypeEnum;

/**
 * Chouette Public Transport Network : a set of lines
 * <p/>
 * Neptune mapping : PTNetwork <br/>
 * Gtfs mapping : none
 */
@Entity
@Table(name = "networks")
@NoArgsConstructor
@Log4j
public class PTNetwork extends NeptuneIdentifiedObject
{

   private static final long serialVersionUID = -8986371268064619423L;

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
    * version date
    * 
    * @param versionDate
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   @Temporal(TemporalType.DATE)
   @Column(name = "version_date")
   private Date versionDate;

   /**
    * description
    * 
    * @return The actual value
    */
   @Getter
   @Column(name = "description")
   private String description;
   /**
    * set description <br/>
    * truncated to 255 characters if too long
    * 
    * @param value
    *           New value
    */
   public void setDescription(String value)
   {
      description = dataBaseSizeProtectedValue(value,"description",log);
   }

   /**
    * registration number
    * 
    * @return The actual value
    */
   @Getter
   @Column(name = "registration_number")
   private String registrationNumber;

   /**
    * set registration number <br/>
    * truncated to 255 characters if too long
    * 
    * @param value
    *           New value
    */
   public void setRegistrationNumber(String value)
   {
      registrationNumber = dataBaseSizeProtectedValue(value, "registrationNumber", log);
   }

   /**
    * source type
    * 
    * @param sourceType
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   @Enumerated(EnumType.STRING)
   @Column(name = "source_type")
   private PTNetworkSourceTypeEnum sourceType;

   /**
    * source name
    * 
    * @return The actual value
    */
   @Getter
   @Column(name = "source_name")
   private String sourceName;
   /**
    * set source name <br/>
    * truncated to 255 characters if too long
    * 
    * @param value
    *           New value
    */
   public void setSourceName(String value)
   {
      sourceName = dataBaseSizeProtectedValue(value,"sourceName",log);
   }

   /**
    * source identifier
    * 
    * @return The actual value
    */
   @Getter
   @Column(name = "source_identifier")
   private String sourceIdentifier;
   /**
    * set source identifier <br/>
    * truncated to 255 characters if too long
    * 
    * @param value
    *           New value
    */
   public void setSourceIdentifier(String value)
   {
      sourceIdentifier = dataBaseSizeProtectedValue(value,"sourceIdentifier",log);
   }

   /**
    * lines
    * 
    * @param lines
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   @OneToMany(mappedBy = "ptNetwork")
   private List<Line> lines = new ArrayList<Line>(0);

   /**
    * List of the network lines Neptune Ids<br/>
    * After import, may content only lines imported<br/>
    * Meaningless after database loading <br/>
    * 
    * @param lineIds
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   @Transient
   private List<String> lineIds;


   /* (non-Javadoc)
    * @see fr.certu.chouette.model.neptune.NeptuneIdentifiedObject#toString(java.lang.String, int)
    */
   @Override
   public String toString(String indent, int level)
   {
      SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss");
      StringBuilder sb = new StringBuilder(super.toString(indent, level));
      if (versionDate != null)
         sb.append("\n").append(indent).append("versionDate = ")
               .append(f.format(versionDate));
      sb.append("\n").append(indent).append("description = ")
            .append(description);
      sb.append("\n").append(indent).append("registrationNumber = ")
            .append(registrationNumber);
      sb.append("\n").append(indent).append("sourceName = ").append(sourceName);
      sb.append("\n").append(indent).append("sourceIdentifier = ")
            .append(sourceIdentifier);
      sb.append("\n").append(indent).append("comment = ").append(comment);

      if (lineIds != null)
      {
         sb.append("\n").append(indent).append(CHILD_ARROW).append("lineIds");
         for (String lineId : lineIds)
         {
            sb.append("\n").append(indent).append(CHILD_LIST_ARROW)
                  .append(lineId);
         }
      }

      return sb.toString();
   }

   /**
    * add a line Id to the network
    * 
    * @param lineId
    *           the line id to add
    */
   public void addLineId(String lineId)
   {
      if (lineIds == null)
         lineIds = new ArrayList<String>();
      lineIds.add(lineId);
   }

   /**
    * add a line Id to the network
    * 
    * @param line
    *           the line to add
    */
   public void addLine(Line line)
   {
      if (lines == null)
         lines = new ArrayList<Line>();
      if (!lines.contains(line))
         lines.add(line);
   }

   /**
    * remove a line
    * 
    * @param line
    *           the lien to remove
    */
   public void removeLine(Line line)
   {
      if (lines == null)
         lines = new ArrayList<Line>();
      if (lines.contains(line))
         lines.remove(line);
   }

   /* (non-Javadoc)
    * @see fr.certu.chouette.model.neptune.NeptuneObject#compareAttributes(fr.certu.chouette.model.neptune.NeptuneObject)
    */
   @Override
   public <T extends NeptuneObject> boolean compareAttributes(T anotherObject)
   {
      if (anotherObject instanceof PTNetwork)
      {
         PTNetwork another = (PTNetwork) anotherObject;
         if (!sameValue(this.getObjectId(), another.getObjectId()))
            return false;
         if (!sameValue(this.getObjectVersion(), another.getObjectVersion()))
            return false;
         if (!sameValue(this.getName(), another.getName()))
            return false;
         if (!sameValue(this.getComment(), another.getComment()))
            return false;
         if (!sameValue(this.getRegistrationNumber(),
               another.getRegistrationNumber()))
            return false;

         if (!sameValue(this.getDescription(), another.getDescription()))
            return false;
         if (!sameValue(this.getSourceIdentifier(),
               another.getSourceIdentifier()))
            return false;
         if (!sameValue(this.getSourceName(), another.getSourceName()))
            return false;
         if (!sameValue(this.getSourceType(), another.getSourceType()))
            return false;
         if (!sameValue(this.getVersionDate(), another.getVersionDate()))
            return false;
         return true;
      } else
      {
         return false;
      }
   }

   /* (non-Javadoc)
    * @see fr.certu.chouette.model.neptune.NeptuneIdentifiedObject#toURL()
    */
   @Override
   public String toURL()
   {
      return "networks/" + getId();
   }

}
