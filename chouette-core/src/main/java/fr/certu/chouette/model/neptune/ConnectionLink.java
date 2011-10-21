package fr.certu.chouette.model.neptune;

import java.math.BigDecimal;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import fr.certu.chouette.filter.Filter;
import fr.certu.chouette.model.neptune.type.ConnectionLinkTypeEnum;
import fr.certu.chouette.model.neptune.type.UserNeedEnum;

/**
 * Neptune ConnectionLink : a link between 2 StopArea
 * <p/>
 * Note for fields comment : <br/>
 * when readable is added to comment, a implicit getter is available <br/>
 * when writable is added to comment, a implicit setter is available
 */
public class ConnectionLink extends NeptuneIdentifiedObject
{
   private static final long      serialVersionUID    = 8490105295077539089L;
   // TODO constant for persistence fields
   /**
    * name of comment attribute for {@link Filter} attributeName construction
    */
   public static final String     COMMENT             = "comment";
   /**
    * name of linkDistance attribute for {@link Filter} attributeName
    * construction
    */
   public static final String     DISTANCE            = "linkDistance";
   /**
    * name of startOfLink attribute for {@link Filter} attributeName
    * construction
    */
   public static final String     START               = "startOfLink";
   /**
    * name of endOfLink attribute for {@link Filter} attributeName construction
    */
   public static final String     END                 = "endOfLink";
   /**
    * name of liftAvailable attribute for {@link Filter} attributeName
    * construction
    */
   public static final String     LIFT                = "liftAvailable";
   /**
    * name of mobilityRestrictedSuitable attribute for {@link Filter}
    * attributeName construction
    */
   public static final String     MOBILITY_RESTRICTED = "mobilityRestrictedSuitable";
   /**
    * name of UserNeeds attribute for {@link Filter} attributeName construction
    */
   public static final String     USERNEEDS_MASK      = "intUserNeeds";
   /**
    * name of stairsAvailable attribute for {@link Filter} attributeName
    * construction
    */
   public static final String     STAIRS              = "stairsAvailable";
   /**
    * name of defaultDuration attribute for {@link Filter} attributeName
    * construction
    */
   public static final String     DEFAULT_DURATION    = "defaultDuration";
   /**
    * name of frequentTravellerDuration attribute for {@link Filter}
    * attributeName construction
    */
   public static final String     FREQUENT_DURATION   = "frequentTravellerDuration";
   /**
    * name of occasionalTravellerDuration attribute for {@link Filter}
    * attributeName construction
    */
   public static final String     OCCASIONAL_DURATION = "occasionalTravellerDuration";
   /**
    * name of mobilityRestrictedTravellerDuration attribute for {@link Filter}
    * attributeName construction
    */
   public static final String     MOBILITY_DURATION   = "mobilityRestrictedTravellerDuration";
   /**
    * name of linkType attribute for {@link Filter} attributeName construction
    */
   public static final String     TYPE                = "linkType";
   /**
    * name of facilities attribute for {@link Filter} attributeName construction
    */
   public static final String     FACILITIES          = "facilities";

   /**
    * Comment <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private String                 comment;
   /**
    * Link Distance in meters (To be confirmed) <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private BigDecimal             linkDistance;
   /**
    * Neptune Id for Start of Link StopArea <br/>
    * (import/export usage) <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private String                 startOfLinkId;
   /**
    * Start of Link StopArea <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private StopArea               startOfLink;
   /**
    * Neptune Id for End of Link StopArea <br/>
    * (import/export usage) <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private String                 endOfLinkId;
   /**
    * End of Link StopArea <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private StopArea               endOfLink;
   /**
    * Indicate if a Lift is available <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private boolean                liftAvailable;
   /**
    * indicate if the link is equipped for mobility restricted persons <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private boolean                mobilityRestrictedSuitable;
   /**
    * indicate if stairs are present on the link <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private boolean                stairsAvailable;
   /**
    * give a list of specific User needs available <br/>
    * <i>readable/writable</i>
    */
   private List<UserNeedEnum>     userNeeds;                                                  // Never
   // be
   // persisted
   /**
    * 
    */
   @Getter
   @Setter
   private Integer                intUserNeeds;                                               // BD
   /**
    * Duration of link <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private Time                   defaultDuration;
   /**
    * Duration of link for frequent travelers <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private Time                   frequentTravellerDuration;
   /**
    * Duration of link for occasional travelers <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private Time                   occasionalTravellerDuration;
   /**
    * Duration of link for mobility restricted travelers <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private Time                   mobilityRestrictedTravellerDuration;
   /**
    * Link type <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private ConnectionLinkTypeEnum linkType;

   @Getter
   @Setter
   private List<Facility>         facilities;

   /**
    * @param facility
    */
   public void addFacility(Facility facility)
   {
      if (facilities == null)
         facilities = new ArrayList<Facility>();
      if (!facilities.contains(facility))
         facilities.add(facility);
   }

   /**
    * @param facility
    */
   public void removeFacility(Facility facility)
   {
      if (facilities == null)
         facilities = new ArrayList<Facility>();
      if (facilities.contains(facility))
         facilities.remove(facility);
   }
   /**
    * @param userNeed
    */
   public void addUserNeed(UserNeedEnum userNeed)
   {
      if (userNeeds == null)
         userNeeds = new ArrayList<UserNeedEnum>();
      if (!userNeeds.contains(userNeed))
      {
         userNeeds.add(userNeed);
         synchronizeUserNeeds();
      }
   }

   /**
    * @param userNeed
    */
   public void removeUserNeed(UserNeedEnum userNeed)
   {
      if (userNeeds == null)
         userNeeds = new ArrayList<UserNeedEnum>();
      userNeeds.remove(userNeed);
      synchronizeUserNeeds();
   }

   /* (non-Javadoc)
    * @see fr.certu.chouette.model.neptune.NeptuneIdentifiedObject#toString(java.lang.String, int)
    */
   @Override
   public String toString(String indent, int level)
   {
      StringBuilder sb = new StringBuilder(super.toString(indent, level));
      sb.append("\n").append(indent).append("  startOfLinkId = ").append(startOfLinkId);
      sb.append("\n").append(indent).append("  endOfLinkId = ").append(endOfLinkId);
      if (linkDistance != null)
      {
         sb.append("\n").append(indent).append("  linkDistance = ").append(linkDistance.toPlainString());
      }
      sb.append("\n").append(indent).append("  comment = ").append(comment);
      sb.append("\n").append(indent).append("  liftAvailable = ").append(liftAvailable);
      sb.append("\n").append(indent).append("  mobilityRestrictedSuitable = ").append(mobilityRestrictedSuitable);
      sb.append("\n").append(indent).append("  stairsAvailable = ").append(stairsAvailable);
      sb.append("\n").append(indent).append("  defaultDuration = ").append(formatDate(defaultDuration));
      sb.append("\n").append(indent).append("  frequentTravellerDuration = ")
            .append(formatDate(frequentTravellerDuration));
      sb.append("\n").append(indent).append("  occasionalTravellerDuration = ")
            .append(formatDate(occasionalTravellerDuration));
      sb.append("\n").append(indent).append("  mobilityRestrictedTravellerDuration = ")
            .append(formatDate(mobilityRestrictedTravellerDuration));
      sb.append("\n").append(indent).append("  linkType = ").append(linkType);

      if (userNeeds != null)
      {
         sb.append("\n").append(indent).append(CHILD_ARROW).append("userNeeds");
         for (UserNeedEnum userNeed : getUserNeeds())
         {
            sb.append("\n").append(indent).append(CHILD_LIST_ARROW).append(userNeed);
         }
      }

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

   /**
    * @param date
    * @return
    */
   private String formatDate(Date date)
   {
      DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
      if (date != null)
      {
         return dateFormat.format(date);
      }
      else
      {
         return null;
      }
   }

   /**
    * @return
    */
   public List<UserNeedEnum> getUserNeeds()
   {
      if (intUserNeeds == null)
         return userNeeds;
      
      UserNeedEnum[] userNeedEnums = UserNeedEnum.values();
      for (UserNeedEnum userNeedEnum : userNeedEnums)
      {
         int filtre = (int) Math.pow(2, userNeedEnum.ordinal());
         if (filtre == (intUserNeeds.intValue() & filtre))
         {
            addUserNeed(userNeedEnum);
         }
      }
      return userNeeds;
   }

   /**
    * @param userNeedEnums
    */
   public void setUserNeeds(List<UserNeedEnum> userNeedEnums)
   {
      userNeeds = userNeedEnums;
      
      synchronizeUserNeeds();
   }

   /**
    * 
    */
   private void synchronizeUserNeeds()
   {
      intUserNeeds = 0;
      if (userNeeds == null)
         return;

      for (UserNeedEnum userNeedEnum : userNeeds)
      {
         intUserNeeds += (int) Math.pow(2, userNeedEnum.ordinal());
      }
   }
}
