package fr.certu.chouette.model.neptune;

import java.math.BigDecimal;

import fr.certu.chouette.filter.Filter;

import lombok.Getter;
import lombok.Setter;

/**
 * Neptune PTLink : a link between 2 successive StopPoints in a route
 * <p/>
 * Note for fields comment : <br/>
 * when readable is added to comment, a implicit getter is available <br/>
 * when writable is added to comment, a implicit setter is available
 */
public class PTLink extends NeptuneIdentifiedObject
{
   private static final long serialVersionUID = -3089442100133439163L;
   // TODO constant for persistence fields
   /**
    * name of comment attribute for {@link Filter} attributeName construction
    */
   public static final String    COMMENT                    = "comment";
   /**
    * Comment <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private String            comment;
   /**
    * link distance in meter <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private BigDecimal        linkDistance;
   /**
    * Neptune ObjectId for Start of Link StopPoint <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private String            startOfLinkId;
   /**
    * Start of Link StopPoint <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private StopPoint         startOfLink;
   /**
    * Neptune ObjectId for End of Link StopPoint <br/>
    * (import/export usage) <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private String            endOfLinkId;
   /**
    * End of Link StopPoint <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private StopPoint         endOfLink;
   /**
    * Neptune ObjectId for Route <br/>
    * (import/export usage) <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private String            routeId;
   /**
    * Route <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private Route             route;

   /*
    * (non-Javadoc)
    * 
    * @see
    * fr.certu.chouette.model.neptune.NeptuneIdentifiedObject#toString(java.
    * lang.String, int)
    */
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

}
