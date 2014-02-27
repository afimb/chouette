package fr.certu.chouette.model.neptune;

import java.math.BigDecimal;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import lombok.Getter;
import lombok.NoArgsConstructor;
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

@Entity
@Table(name = "connection_links")
@NoArgsConstructor
public class ConnectionLink extends NeptuneIdentifiedObject
{
   private static final long serialVersionUID = 8490105295077539089L;

   /**
    * name of comment attribute for {@link Filter} attributeName construction
    */
   public static final String COMMENT = "comment";
   /**
    * name of linkDistance attribute for {@link Filter} attributeName
    * construction
    */
   public static final String DISTANCE = "linkDistance";
   /**
    * name of startOfLink attribute for {@link Filter} attributeName
    * construction
    */
   public static final String START = "startOfLink";
   /**
    * name of endOfLink attribute for {@link Filter} attributeName construction
    */
   public static final String END = "endOfLink";
   /**
    * name of liftAvailable attribute for {@link Filter} attributeName
    * construction
    */
   public static final String LIFT = "liftAvailable";
   /**
    * name of mobilityRestrictedSuitable attribute for {@link Filter}
    * attributeName construction
    */
   public static final String MOBILITY_RESTRICTED = "mobilityRestrictedSuitable";
   /**
    * name of UserNeeds attribute for {@link Filter} attributeName construction
    */
   public static final String USERNEEDS_MASK = "intUserNeeds";
   /**
    * name of stairsAvailable attribute for {@link Filter} attributeName
    * construction
    */
   public static final String STAIRS = "stairsAvailable";
   /**
    * name of defaultDuration attribute for {@link Filter} attributeName
    * construction
    */
   public static final String DEFAULT_DURATION = "defaultDuration";
   /**
    * name of frequentTravellerDuration attribute for {@link Filter}
    * attributeName construction
    */
   public static final String FREQUENT_DURATION = "frequentTravellerDuration";
   /**
    * name of occasionalTravellerDuration attribute for {@link Filter}
    * attributeName construction
    */
   public static final String OCCASIONAL_DURATION = "occasionalTravellerDuration";
   /**
    * name of mobilityRestrictedTravellerDuration attribute for {@link Filter}
    * attributeName construction
    */
   public static final String MOBILITY_DURATION = "mobilityRestrictedTravellerDuration";
   /**
    * name of linkType attribute for {@link Filter} attributeName construction
    */
   public static final String TYPE = "linkType";
   /**
    * name of facilities attribute for {@link Filter} attributeName construction
    */
   public static final String FACILITIES = "facilities";

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
   @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
   @OnDelete(action = OnDeleteAction.CASCADE)
   @JoinColumn(name = "departure_id")
   private StopArea startOfLink;

   @Getter
   @Setter
   @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
   @OnDelete(action = OnDeleteAction.CASCADE)
   @JoinColumn(name = "arrival_id")
   private StopArea endOfLink;

   /**
    * Neptune Id for Start of Link StopArea <br/>
    * (import/export usage) <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   @Transient
   private String startOfLinkId;

   /**
    * Neptune Id for End of Link StopArea <br/>
    * (import/export usage) <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   @Transient
   private String endOfLinkId;

   /**
    * List of the specific user needs available <br/>
    * <i>readable/writable</i>
    */
   @Transient
   private List<UserNeedEnum> userNeeds;

   @Getter
   @Setter
   @Transient
   private List<Facility> facilities;

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
    * add a userNeed value in userNeeds collection if not already present <br/>
    * intUserNeeds will be automatically synchronized <br/>
    * <i>readable/writable</i>
    * 
    * @param userNeed
    *           the userNeed to add
    */
   public synchronized void addUserNeed(UserNeedEnum userNeed)
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
   public synchronized void addAllUserNeed(Collection<UserNeedEnum> userNeedCollection)
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
    * get UserNeeds list
    * 
    * @return userNeeds
    */
   public synchronized List<UserNeedEnum> getUserNeeds()
   {
      // synchronise userNeeds with intUserNeeds
      if (intUserNeeds == null)
      {
         userNeeds = null;
         return userNeeds;
      }

      userNeeds = new ArrayList<UserNeedEnum>();
      UserNeedEnum[] userNeedEnums = UserNeedEnum.values();
      for (UserNeedEnum userNeed : userNeedEnums)
      {
         int filtre = (int) Math.pow(2, userNeed.ordinal());
         if (filtre == (intUserNeeds.intValue() & filtre))
         {
            if (!userNeeds.contains(userNeed))
            {
               userNeeds.add(userNeed);
            }
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
   public synchronized void setUserNeeds(List<UserNeedEnum> userNeedEnums)
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

   @Override
   public void complete()
   {
      if (isCompleted())
         return;
      super.complete();

      if (getStartOfLink() != null)
      {
         setStartOfLinkId(getStartOfLink().getObjectId());
      }
      if (getEndOfLink() != null)
      {
         setEndOfLinkId(getEndOfLink().getObjectId());
      }
   }

   @Override
   public <T extends NeptuneObject> boolean compareAttributes(
         T anotherObject)
   {
      if (anotherObject instanceof ConnectionLink)
      {
         ConnectionLink another = (ConnectionLink) anotherObject;
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
         if (!sameValue(this.getMobilityRestrictedTravellerDuration(), another.getMobilityRestrictedTravellerDuration()))
            return false;
         if (!sameValue(this.getOccasionalTravellerDuration(), another.getOccasionalTravellerDuration()))
            return false;

         if (!sameValue(this.getLinkType(), another.getLinkType()))
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
      return "connection_links/" + getId();
   }

}
