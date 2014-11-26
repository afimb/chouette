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
 * Chouette AccessLink : relation between an AccessPoint and a StopArea
 * <p/>
 * Neptune mapping : PTAccessLink <br/>
 * Gtfs mapping : none <br/>
 * 
 */
@Entity
@Table(name = "access_links")
@NoArgsConstructor
@Log4j
public class AccessLink extends NeptuneIdentifiedObject
{
   private static final long serialVersionUID = 7835556134861322471L;

   /**
    * name
    * 
    * @return The actual value
    */
   @Getter
   @Column(name = "name")
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
    * lift indicator <br/>
    * 
    * <ul>
    * <li>true if a lift is available on this link</li>
    * <li>false if no lift is available on this link</li>
    * </ul>
    * 
    * @param liftAvailable
    *           New state for lift indicator
    * @return The actual lift indicator
    */
   @Getter
   @Setter
   @Column(name = "lift_availability")
   private boolean liftAvailable = false;

   /**
    * mobility restriction indicator (such as wheel chairs) <br/>
    * 
    * <ul>
    * <li>true if wheel chairs can follow this link</li>
    * <li>false if wheel chairs can't follow this link</li>
    * </ul>
    * 
    * @param mobilityRestrictedSuitable
    *           New state for mobility restriction indicator
    * @return The actual mobility restriction indicator
    */
   @Getter
   @Setter
   @Column(name = "mobility_restricted_suitability")
   private boolean mobilityRestrictedSuitable = false;

   /**
    * stairs indicator <br/>
    * 
    * <ul>
    * <li>true if a stairs are presents on this link</li>
    * <li>false if no stairs are presents on this link</li>
    * </ul>
    * 
    * @param stairsAvailable
    *           New state for stairs indicator
    * @return The actual stairs indicator
    */
   @Getter
   @Setter
   @Column(name = "stairs_availability")
   private boolean stairsAvailable = false;

   /**
    * medium time to follow the link <br/>
    * null if unknown
    * 
    * @param defaultDuration
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   @Column(name = "default_duration")
   private Time defaultDuration;

   /**
    * time to follow the link for a frequent traveller <br/>
    * null if unknown
    * 
    * @param frequentTravellerDuration
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   @Column(name = "frequent_traveller_duration")
   private Time frequentTravellerDuration;

   /**
    * time to follow the link for an occasional traveller <br/>
    * null if unknown
    * 
    * @param occasionalTravellerDuration
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   @Column(name = "occasional_traveller_duration")
   private Time occasionalTravellerDuration;

   /**
    * time to follow the link for a traveller with mobility restriction <br/>
    * null if unknown
    * 
    * @param mobilityRestrictedTravellerDuration
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   @Column(name = "mobility_restricted_traveller_duration")
   private Time mobilityRestrictedTravellerDuration;

   /**
    * link type
    * 
    * @param linkType
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   @Enumerated(EnumType.STRING)
   @Column(name = "link_type")
   private ConnectionLinkTypeEnum linkType;

   /**
    * coded user needs as binary map<br/>
    * 
    * use following methods for easier access :
    * <ul>
    * <li>getUserNeeds</li>
    * <li>setUserNeeds</li>
    * <li>addUserNeed</li>
    * <li>addAllUserNeed</li>
    * <li>removeUserNeed</li>
    * </ul>
    * 
    * @param intUserNeeds
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   @Column(name = "int_user_needs")
   private Integer intUserNeeds = 0;

   /**
    * access link orientation
    * 
    * @param linkOrientation
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   @Enumerated(EnumType.STRING)
   @Column(name = "link_orientation")
   private LinkOrientationEnum linkOrientation;

   /**
    * access point connected to link
    * 
    * @param accessPoint
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "access_point_id")
   private AccessPoint accessPoint;

   /**
    * stop area connected to link
    * 
    * @param stopArea
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "stop_area_id")
   private StopArea stopArea;

   /**
    * Neptune Object Id for End of Link <br/>
    * available only after calling method complete()
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
    * List of the specific user needs available
    */
   @Transient
   private List<UserNeedEnum> userNeeds;

   /**
    * Neptune Object Id for Start of Link <br/>
    * available only after calling method complete()
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
    * add a userNeed value in userNeeds collection if not already present <br/>
    * intUserNeeds will be automatically synchronized
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
    * give a list of specific User needs available
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
      if (linkOrientation != null)
      {
         sb.append("\n").append(indent).append("  linkOrientation = ").append(linkOrientation.toString());
      }
      sb.append("\n").append(indent).append("  comment = ").append(comment);
      sb.append("\n").append(indent).append("  liftAvailable = ").append(liftAvailable);
      sb.append("\n").append(indent).append("  mobilityRestrictedSuitable = ").append(mobilityRestrictedSuitable);
      sb.append("\n").append(indent).append("  stairsAvailable = ").append(stairsAvailable);
      sb.append("\n").append(indent).append("  defaultDuration = ").append(formatDate(defaultDuration));
      sb.append("\n").append(indent).append("  frequentTravellerDuration = ").append(formatDate(frequentTravellerDuration));
      sb.append("\n").append(indent).append("  occasionalTravellerDuration = ").append(formatDate(occasionalTravellerDuration));
      sb.append("\n").append(indent).append("  mobilityRestrictedTravellerDuration = ").append(formatDate(mobilityRestrictedTravellerDuration));
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
         if (stopArea != null)
         {
            sb.append("\n").append(indent).append(CHILD_ARROW).append(stopArea.toString(childIndent, 0));
         }
         if (accessPoint != null)
         {
            sb.append("\n").append(indent).append(CHILD_ARROW).append(accessPoint.toString(childIndent, 0));
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
      if (getAccessPoint() != null)
         getAccessPoint().complete();
      if (getLinkOrientation() != null)
      {
         if (getLinkOrientation().equals(LinkOrientationEnum.AccessPointToStopArea))
         {
            if (getAccessPoint() != null)
               setStartOfLinkId(getAccessPoint().getObjectId());
            if (getStopArea() != null)
               setEndOfLinkId(getStopArea().getObjectId());
         }
         else if (getLinkOrientation().equals(LinkOrientationEnum.StopAreaToAccessPoint))
         {
            if (getAccessPoint() != null)
               setEndOfLinkId(getAccessPoint().getObjectId());
            if (getStopArea() != null)
               setStartOfLinkId(getStopArea().getObjectId());
         }
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
         if (!sameValue(this.getRegistrationNumber(), another.getRegistrationNumber()))
            return false;
         if (!sameValue(this.getDefaultDuration(), another.getDefaultDuration()))
            return false;
         if (!sameValue(this.getFrequentTravellerDuration(), another.getFrequentTravellerDuration()))
            return false;
         if (!sameValue(this.getLinkDistance(), another.getLinkDistance()))
            return false;
         if (!sameValue(this.getLinkOrientation(), another.getLinkOrientation()))
            return false;
         if (!sameValue(this.getMobilityRestrictedTravellerDuration(), another.getMobilityRestrictedTravellerDuration()))
            return false;
         if (!sameValue(this.getOccasionalTravellerDuration(), another.getOccasionalTravellerDuration()))
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
      return "access_points/" + getAccessPoint().getId() + "/access_links/" + getId();
   }

}
