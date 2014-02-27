package fr.certu.chouette.model.neptune;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import fr.certu.chouette.model.neptune.type.AccessPointTypeEnum;

/**
 * Neptune AccessPoint
 * <p/>
 * Note for fields comment : <br/>
 * when readable is added to comment, a implicit getter is available <br/>
 * when writable is added to comment, a implicit setter is available
 * 
 */

@Entity
@Table(name = "access_points")
@NoArgsConstructor
@Log4j
public class AccessPoint extends NeptuneLocalizedObject
{

   private static final long serialVersionUID = 7520070228185917225L;

   @Getter
   @Column(name = "comment")
   private String comment;

   @Getter
   @Setter
   @Column(name = "contained_in")
   private String containedInStopArea;

   @Getter
   @Setter
   @Column(name = "openning_time")
   private Time openingTime;

   @Getter
   @Setter
   @Column(name = "closing_time")
   private Time closingTime;

   @Getter
   @Setter
   @Enumerated(EnumType.STRING)
   @Column(name = "access_type")
   private AccessPointTypeEnum type;

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
   @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
   @OnDelete(action = OnDeleteAction.CASCADE)
   @JoinColumn(name = "stop_area_id")
   private StopArea containedIn;

   @Getter
   @Setter
   @OneToMany(mappedBy = "accessPoint")
   private List<AccessLink> accessLinks = new ArrayList<AccessLink>(0);

   public void setComment(String value)
   {
      if (value != null && value.length() > 255)
      {
         log.warn("comment too long, truncated " + value);
         comment = value.substring(0, 255);
      }
      else
      {
         comment = value;
      }
   }

   /**
    * add an AccessLink to AccesPoint if not already present <br/>
    * no control is made on AccessLink's start and end links
    * 
    * @param accessLink
    *           link to add
    */
   public void addAccessLink(AccessLink accessLink)
   {
      if (accessLinks == null)
         accessLinks = new ArrayList<AccessLink>();
      if (accessLink != null && !accessLinks.contains(accessLink))
      {
         accessLinks.add(accessLink);
         accessLink.setAccessPoint(this);
      }
   }

   /**
    * add a collection of AccessLinks to AccesPoint if not already presents <br/>
    * no control is made on AccessLink's start and end links
    * 
    * @param accessLinkCollection
    *           links to add
    */
   public void addAccessLinks(Collection<AccessLink> accessLinkCollection)
   {
      if (accessLinks == null)
         accessLinks = new ArrayList<AccessLink>();

      for (AccessLink accessLink : accessLinkCollection)
      {
         if (accessLink != null && !accessLinks.contains(accessLink))
         {
            accessLinks.add(accessLink);
            accessLink.setAccessPoint(this);
         }
      }

   }

   /**
    * remove an AccessLink from AccesPoint if present
    * 
    * @param accessLink
    *           to remove
    */
   public void removeAccessLink(AccessLink accessLink)
   {
      if (accessLinks == null)
         accessLinks = new ArrayList<AccessLink>();
      if (accessLinks.contains(accessLink))
         accessLinks.remove(accessLink);
   }

   @Override
   public String toString(String indent, int level)
   {
      StringBuilder sb = new StringBuilder(super.toString(indent, level));
      sb.append("\n").append(indent).append("  comment = ").append(comment);
      sb.append("\n").append(indent).append("  liftAvailable = ").append(liftAvailable);
      sb.append("\n").append(indent).append("  mobilityRestrictedSuitable = ").append(mobilityRestrictedSuitable);
      sb.append("\n").append(indent).append("  stairsAvailable = ").append(stairsAvailable);
      return sb.toString();
   }

   @Override
   public void complete()
   {
      if (isCompleted())
         return;
      super.complete();
      if (getContainedIn() != null)
      {
         containedInStopArea = getContainedIn().getObjectId();
         getContainedIn().addAccessPoint(this);
      }
      else
      {
         containedInStopArea = "NEPTUNE:StopArea:UnusedField";
      }
   }

   @Override
   public <T extends NeptuneObject> boolean compareAttributes(
         T anotherObject)
   {
      if (anotherObject instanceof AccessPoint)
      {
         AccessPoint another = (AccessPoint) anotherObject;
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

         if (!sameValue(this.getClosingTime(), another.getClosingTime()))
            return false;
         if (!sameValue(this.getOpeningTime(), another.getOpeningTime()))
            return false;
         if (!sameValue(this.getCountryCode(), another.getCountryCode()))
            return false;
         if (!sameValue(this.getStreetName(), another.getStreetName()))
            return false;
         if (!sameValue(this.getLatitude(), another.getLatitude()))
            return false;
         if (!sameValue(this.getLongitude(), another.getLongitude()))
            return false;
         if (!sameValue(this.getLongLatType(), another.getLongLatType()))
            return false;
         if (!sameValue(this.getType(), another.getType()))
            return false;
         if (!sameValue(this.getProjectionType(), another.getProjectionType()))
            return false;
         if (!sameValue(this.getX(), another.getX()))
            return false;
         if (!sameValue(this.getY(), another.getY()))
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
      return getContainedIn().toURL() + "/access_points/" + getId();
   }

}
