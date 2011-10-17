package fr.certu.chouette.model.neptune;

import java.math.BigDecimal;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import fr.certu.chouette.model.neptune.type.ConnectionLinkTypeEnum;
import fr.certu.chouette.model.neptune.type.LinkOrientationEnum;
import fr.certu.chouette.model.neptune.type.UserNeedEnum;

import lombok.Getter;
import lombok.Setter;

/**
 * Neptune AccessLink
 * <p/>
 * Note for fields comment : <br/>
 * when readable is added to comment, a implicit getter is available <br/>
 * when writable is added to comment, a implicit setter is available
 * 
 */
public class AccessLink extends NeptuneIdentifiedObject
{
   private static final long      serialVersionUID = 7835556134861322471L;
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
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private String                 startOfLinkId;
   /**
    * StopArea <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private StopArea               stopArea;
   /**
    * Neptune Id for End of Link StopArea <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private String                 endOfLinkId;
   /**
    * AccessPoint <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private AccessPoint            accessPoint;
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
   private List<UserNeedEnum>     userNeeds;                              // Never
                                                                           // be
                                                                           // persisted
   /**
    * encoded form of userNeeds for database purpose
    */
   @Getter
   @Setter
   private Integer                intUserNeeds;                           // BD
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
   /**
    * Link Orientation (for Database purpose)
    * <p/>
    * database can't save startLink or endlink because they may be of
    * alternative types<br/>
    * linkOrientation helps to know which one (stopArea or accessPoint) is
    * startOfLink. <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private LinkOrientationEnum    linkOrientation;

   /**
    * add a userNeed value in userNeeds collection if not already present <br/>
    * intUserNeeds will be automatically synchronized <br/>
    * <i>readable/writable</i>
    * 
    * @param userNeed
    *           the userNeed to add
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
    * add a collection of userNeed values in userNeeds collection if not already
    * present <br/>
    * intUserNeeds will be automatically synchronized
    * 
    * @param userNeedCollection
    *           the userNeeds to add
    */
   public void addAllUserNeed(Collection<UserNeedEnum> userNeedCollection)
   {
      if (userNeeds == null)
         userNeeds = new ArrayList<UserNeedEnum>();
      boolean added = false;
      for (UserNeedEnum userNeed : userNeedCollection)
      {
         if (!userNeeds.contains(userNeed))
         {
            userNeeds.add(userNeed);
            added = true;
         }
      }
      if (added)
      {
         synchronizeUserNeeds();
      }
   }

   /**
    * remove a userNeed value for userNeeds collection if present <br/>
    * intUserNeeds will be automatically synchronized
    * 
    * @param userNeed
    *           the userNeed to remove
    */
   public void removeUserNeed(UserNeedEnum userNeed)
   {
      if (userNeeds == null)
         userNeeds = new ArrayList<UserNeedEnum>();

      if (userNeeds.remove(userNeed))
      {
         synchronizeUserNeeds();
      }
   }

   /**
    * get UserNeeds list
    * 
    * @return userNeeds
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
    * set the userNeeds list <br/>
    * intUserNeeds will be automatically synchronized
    * 
    * @param userNeedEnums
    *           list of UserNeeds to set
    */
   public void setUserNeeds(List<UserNeedEnum> userNeedEnums)
   {
      userNeeds = userNeedEnums;

      synchronizeUserNeeds();
   }

   /**
    * synchronize intUserNeeds with userNeeds List content
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

      return sb.toString();
   }

   /**
    * format durations for toString()
    * 
    * @param date
    *           duration in Date format
    * @return duration in String format
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
      if (getLinkOrientation() != null)
      {
         if (getLinkOrientation().equals(LinkOrientationEnum.ACCESSPOINT_TO_STOPAREA))
         {
            if (getAccessPoint() != null)
               setStartOfLinkId(getAccessPoint().getObjectId());
            if (getStopArea() != null)
               setEndOfLinkId(getStopArea().getObjectId());
         }
         else if (getLinkOrientation().equals(LinkOrientationEnum.STOPAREA_TO_ACCESSPOINT))
         {
            if (getAccessPoint() != null)
               setEndOfLinkId(getAccessPoint().getObjectId());
            if (getStopArea() != null)
               setStartOfLinkId(getStopArea().getObjectId());
         }
      }
   }

}
