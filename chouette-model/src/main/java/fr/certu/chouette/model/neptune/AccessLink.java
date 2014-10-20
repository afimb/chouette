package fr.certu.chouette.model.neptune;

import java.math.BigDecimal;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import fr.certu.chouette.model.neptune.type.ConnectionLinkTypeEnum;
import fr.certu.chouette.model.neptune.type.LinkOrientationEnum;
import fr.certu.chouette.model.neptune.type.UserNeedEnum;

/**
 * Neptune AccessLink
 * <p/>
 * Note for fields comment : <br/>
 * when readable is added to comment, a implicit getter is available <br/>
 * when writable is added to comment, a implicit setter is available
 * 
 */
@Entity
@Table(name = "access_links")
@NoArgsConstructor
@Log4j
public class AccessLink extends NeptuneIdentifiedObject
{
   private static final long serialVersionUID = 7835556134861322471L;

   @Getter
   @Column(name = "name")
   private String name;
   public void setName(String value)
   {
      name = dataBaseSizeProtectedValue(value,"name",log);
   }

   @Getter
   @Column(name = "comment")
   private String comment;
   public void setComment(String value)
   {
      comment = dataBaseSizeProtectedValue(value,"comment",log);
   }

   @Getter
   @Setter
   @Column(name = "link_distance")
   private BigDecimal linkDistance;

   @Getter
   @Setter
   @Column(name = "lift_availability")
   private boolean liftAvailable = false;

   @Getter
   @Setter
   @Column(name = "mobility_restricted_suitability")
   private boolean mobilityRestrictedSuitable = false;

   @Getter
   @Setter
   @Column(name = "stairs_availability")
   private boolean stairsAvailable = false;

   @Getter
   @Setter
   @Column(name = "default_duration")
   private Time defaultDuration;

   @Getter
   @Setter
   @Column(name = "frequent_traveller_duration")
   private Time frequentTravellerDuration;

   @Getter
   @Setter
   @Column(name = "occasional_traveller_duration")
   private Time occasionalTravellerDuration;

   @Getter
   @Setter
   @Column(name = "mobility_restricted_traveller_duration")
   private Time mobilityRestrictedTravellerDuration;

   @Getter
   @Setter
   @Enumerated(EnumType.STRING)
   @Column(name = "link_type")
   private ConnectionLinkTypeEnum linkType;

   @Getter
   @Setter
   @Column(name = "int_user_needs")
   private Integer intUserNeeds = 0;

   @Getter
   @Setter
   @Enumerated(EnumType.STRING)
   @Column(name = "link_orientation")
   private LinkOrientationEnum linkOrientation;

   @Getter
   @Setter
   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "access_point_id")
   private AccessPoint accessPoint;

   @Getter
   @Setter
   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "stop_area_id")
   private StopArea stopArea;

   /**
    * Neptune Id for End of Link StopArea <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   @Transient
   private String endOfLinkId;

   /**
    * give a list of specific User needs available <br/>
    * <i>readable/writable</i>
    */
   @Transient
   private List<UserNeedEnum> userNeeds;

   /**
    * Neptune Id for Start of Link StopArea <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   @Transient
   private String startOfLinkId;

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

   @Override
   public String toString(String indent, int level)
   {
      StringBuilder sb = new StringBuilder(super.toString(indent, level));
      sb.append("\n").append(indent).append("  startOfLinkId = ")
            .append(startOfLinkId);
      sb.append("\n").append(indent).append("  endOfLinkId = ")
            .append(endOfLinkId);
      if (linkDistance != null)
      {
         sb.append("\n").append(indent).append("  linkDistance = ")
               .append(linkDistance.toPlainString());
      }
      if (linkOrientation != null)
      {
         sb.append("\n").append(indent).append("  linkOrientation = ")
               .append(linkOrientation.toString());
      }
      sb.append("\n").append(indent).append("  comment = ").append(comment);
      sb.append("\n").append(indent).append("  liftAvailable = ")
            .append(liftAvailable);
      sb.append("\n").append(indent).append("  mobilityRestrictedSuitable = ")
            .append(mobilityRestrictedSuitable);
      sb.append("\n").append(indent).append("  stairsAvailable = ")
            .append(stairsAvailable);
      sb.append("\n").append(indent).append("  defaultDuration = ")
            .append(formatDate(defaultDuration));
      sb.append("\n").append(indent).append("  frequentTravellerDuration = ")
            .append(formatDate(frequentTravellerDuration));
      sb.append("\n").append(indent).append("  occasionalTravellerDuration = ")
            .append(formatDate(occasionalTravellerDuration));
      sb.append("\n").append(indent)
            .append("  mobilityRestrictedTravellerDuration = ")
            .append(formatDate(mobilityRestrictedTravellerDuration));
      sb.append("\n").append(indent).append("  linkType = ").append(linkType);

      if (userNeeds != null)
      {
         sb.append("\n").append(indent).append(CHILD_ARROW).append("userNeeds");
         for (UserNeedEnum userNeed : getUserNeeds())
         {
            sb.append("\n").append(indent).append(CHILD_LIST_ARROW)
                  .append(userNeed);
         }
      }
      if (level > 0)
      {
         String childIndent = indent + CHILD_INDENT;
         if (stopArea != null)
         {
            sb.append("\n").append(indent).append(CHILD_ARROW)
                  .append(stopArea.toString(childIndent, 0));
         }
         if (accessPoint != null)
         {
            sb.append("\n").append(indent).append(CHILD_ARROW)
                  .append(accessPoint.toString(childIndent, 0));
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
      } else
      {
         return null;
      }
   }

   @Override
   public void complete()
   {
      if (isCompleted())
         return;
      super.complete();
      if (getAccessPoint() != null)
         getAccessPoint().complete();
      if (getLinkOrientation() != null)
      {
         if (getLinkOrientation().equals(
               LinkOrientationEnum.AccessPointToStopArea))
         {
            if (getAccessPoint() != null)
               setStartOfLinkId(getAccessPoint().getObjectId());
            if (getStopArea() != null)
               setEndOfLinkId(getStopArea().getObjectId());
         } else if (getLinkOrientation().equals(
               LinkOrientationEnum.StopAreaToAccessPoint))
         {
            if (getAccessPoint() != null)
               setEndOfLinkId(getAccessPoint().getObjectId());
            if (getStopArea() != null)
               setStartOfLinkId(getStopArea().getObjectId());
         }
      }
   }

   @Override
   public <T extends NeptuneObject> boolean compareAttributes(T anotherObject)
   {
      if (anotherObject instanceof AccessLink)
      {
         AccessLink another = (AccessLink) anotherObject;
         if (!sameValue(this.getObjectId(), another.getObjectId()))
            return false;
         if (!sameValue(this.getObjectVersion(), another.getObjectVersion()))
            return false;
         if (!sameValue(this.getName(), another.getName()))
            return false;
         if (!sameValue(this.getComment(), another.getComment()))
            return false;
         if (!sameValue(this.getIntUserNeeds(), another.getIntUserNeeds()))
            return false;
         if (!sameValue(this.getRegistrationNumber(),
               another.getRegistrationNumber()))
            return false;
         if (!sameValue(this.getDefaultDuration(), another.getDefaultDuration()))
            return false;
         if (!sameValue(this.getFrequentTravellerDuration(),
               another.getFrequentTravellerDuration()))
            return false;
         if (!sameValue(this.getLinkDistance(), another.getLinkDistance()))
            return false;
         if (!sameValue(this.getLinkOrientation(), another.getLinkOrientation()))
            return false;
         if (!sameValue(this.getMobilityRestrictedTravellerDuration(),
               another.getMobilityRestrictedTravellerDuration()))
            return false;
         if (!sameValue(this.getOccasionalTravellerDuration(),
               another.getOccasionalTravellerDuration()))
            return false;
         return true;
      } else
      {
         return false;
      }
   }

   @Override
   public String toURL()
   {
      return "access_points/" + getAccessPoint().getId() + "/access_links/"
            + getId();
   }

}
