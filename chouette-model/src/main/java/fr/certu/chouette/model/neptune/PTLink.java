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

/**
 * Chouette PTLink : a link between 2 successive StopPoints in a route
 * <br/>
 * Note: this object is only used for Neptune import, export and validation purpose
 * <p/>
 * <p/>
 * Neptune mapping : PtLink <br/>
 * Gtfs mapping : none <br/>
 */
@Entity
@Table(name = "pt_links")
@NoArgsConstructor
public class PTLink extends NeptuneIdentifiedObject
{
   private static final long serialVersionUID = -3089442100133439163L;

   /**
    * name
    * 
    * @param name
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   @Column(name = "name")
   private String name;

   /**
    * comment
    * 
    * @param comment
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   @Column(name = "comment")
   private String comment;

   /**
    * link length in meters
    * 
    * @param linkDistance
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   @Column(name = "link_distance")
   private BigDecimal linkDistance;

   /**
    * start of link
    * 
    * @param startOfLink
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "start_of_link_id")
   private StopPoint startOfLink;

   /**
    * end of link
    * 
    * @param endOfLink
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "end_of_link_id")
   private StopPoint endOfLink;

   /**
    * route
    * 
    * @param route
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "route_id")
   private Route route;

   /**
    * Neptune ObjectId for Start of Link StopPoint <br/>
    * 
    * @param startOfLinkId
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   @Transient
   private String startOfLinkId;

   /**
    * Neptune ObjectId for End of Link StopPoint <br/>
    * (import/export usage) <br/>
    * 
    * @param endOfLinkId
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   @Transient
   private String endOfLinkId;

   /**
    * Neptune ObjectId for Route <br/>
    * (import/export usage) <br/>
    * 
    * @param routeId
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   @Transient
   private String routeId;


   /* (non-Javadoc)
    * @see fr.certu.chouette.model.neptune.NeptuneIdentifiedObject#toString(java.lang.String, int)
    */
   @Override
   public String toString(String indent, int level)
   {
      StringBuilder sb = new StringBuilder(super.toString(indent, level));
      sb.append("\n").append(indent).append("startOfLinkId = ")
            .append(startOfLinkId);
      sb.append("\n").append(indent).append("endOfLinkId = ")
            .append(endOfLinkId);
      if (linkDistance != null)
      {
         sb.append("\n").append(indent).append("linkDistance = ")
               .append(linkDistance.toPlainString());
      }
      sb.append("\n").append(indent).append("comment = ").append(comment);

      if (level > 0)
      {
         String childIndent = indent + CHILD_INDENT;
         if (startOfLink != null)
         {
            sb.append("\n").append(indent).append(CHILD_ARROW)
                  .append(startOfLink.toString(childIndent, 0));
         }
         if (endOfLink != null)
         {
            sb.append("\n").append(indent).append(CHILD_ARROW)
                  .append(endOfLink.toString(childIndent, 0));
         }
      }

      return sb.toString();
   }

   /*
    * (non-Javadoc)
    * 
    * @see fr.certu.chouette.model.neptune.NeptuneIdentifiedObject#complete()
    */
   /* (non-Javadoc)
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

   /* (non-Javadoc)
    * @see fr.certu.chouette.model.neptune.NeptuneObject#compareAttributes(fr.certu.chouette.model.neptune.NeptuneObject)
    */
   @Override
   public <T extends NeptuneObject> boolean compareAttributes(T anotherObject)
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
         if (!sameValue(this.getRegistrationNumber(),
               another.getRegistrationNumber()))
            return false;
         if (!sameValue(this.getLinkDistance(), another.getLinkDistance()))
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
      return null;
   }

}
