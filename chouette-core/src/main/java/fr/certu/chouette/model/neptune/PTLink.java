package fr.certu.chouette.model.neptune;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import fr.certu.chouette.filter.Filter;

/**
 * Neptune PTLink : a link between 2 successive StopPoints in a route
 * <p/>
 * Note for fields comment : <br/>
 * when readable is added to comment, a implicit getter is available <br/>
 * when writable is added to comment, a implicit setter is available
 */
@Entity
@Table(name = "pt_links")
@NoArgsConstructor
@Log4j
public class PTLink extends NeptuneIdentifiedObject
{
   private static final long serialVersionUID = -3089442100133439163L;

   /**
    * name of comment attribute for {@link Filter} attributeName construction
    */
   public static final String COMMENT = "comment";

   @Getter
   @Column(name = "name")
   private String name;

   @Getter
   @Setter
   @Column(name = "comment")
   private String comment;

   @Getter
   @Setter
   @Column(name = "link_distance")
   private BigDecimal linkDistance;

   @Getter
   @Setter
   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "start_of_link_id")
   private StopPoint startOfLink;

   @Getter
   @Setter
   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "end_of_link_id")
   private StopPoint endOfLink;

   @Getter
   @Setter
   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "route_id")
   private Route route;

   /**
    * Neptune ObjectId for Start of Link StopPoint <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   @Transient
   private String startOfLinkId;

   /**
    * Neptune ObjectId for End of Link StopPoint <br/>
    * (import/export usage) <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   @Transient
   private String endOfLinkId;

   /**
    * Neptune ObjectId for Route <br/>
    * (import/export usage) <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   @Transient
   private String routeId;

   public void setName(String value)
   {
      if (value != null && value.length() > 255)
      {
         log.warn("name too long, truncated " + value);
         name = value.substring(0, 255);
      }
      else
      {
         name = value;
      }
   }

   @Override
   public String toString(String indent, int level)
   {
      StringBuilder sb = new StringBuilder(super.toString(indent, level));
      sb.append("\n").append(indent).append("startOfLinkId = ").append(startOfLinkId);
      sb.append("\n").append(indent).append("endOfLinkId = ").append(endOfLinkId);
      if (linkDistance != null)
      {
         sb.append("\n").append(indent).append("linkDistance = ").append(linkDistance.toPlainString());
      }
      sb.append("\n").append(indent).append("comment = ").append(comment);

      if (level > 0)
      {
         String childIndent = indent + CHILD_INDENT;
         if (startOfLink != null)
         {
            sb.append("\n").append(indent).append(CHILD_ARROW).append(startOfLink.toString(childIndent, 0));
         }
         if (endOfLink != null)
         {
            sb.append("\n").append(indent).append(CHILD_ARROW).append(endOfLink.toString(childIndent, 0));
         }
      }

      return sb.toString();
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
      if (startOfLink != null)
         startOfLinkId = startOfLink.getObjectId();
      if (endOfLink != null)
         endOfLinkId = endOfLink.getObjectId();

   }

   @Override
   public <T extends NeptuneObject> boolean compareAttributes(
         T anotherObject)
   {
      if (anotherObject instanceof PTLink)
      {
         PTLink another = (PTLink) anotherObject;
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
         if (!sameValue(this.getLinkDistance(), another.getLinkDistance()))
            return false;

         return true;
      }
      else
      {
         return false;
      }
   }

   @Override
   public String toURL()
   {
      return null;
   }

}
