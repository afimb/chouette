package fr.certu.chouette.model.neptune;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import fr.certu.chouette.model.neptune.type.AccessPointTypeEnum;

/**
 * Chouette AccessPoint : relation between an AccessPoint and a StopArea
 * <p/>
 * Neptune mapping : PTAccessPoint <br/>
 * Gtfs mapping : none <br/>
 * 
 */

@Entity
@Table(name = "access_points")
@NoArgsConstructor
@Log4j
public class AccessPoint extends NeptuneLocalizedObject
{

   private static final long serialVersionUID = 7520070228185917225L;

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
    * contained in StopArea id (deprecated in next release)
    * 
    * @param containedInStopArea
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   @Column(name = "contained_in")
   private String containedInStopArea;

   /**
    * access point opening time
    * 
    * @param openingTime
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   @Column(name = "openning_time")
   private Time openingTime;

   /**
    * access point closing time
    * 
    * @param closingTime
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   @Column(name = "closing_time")
   private Time closingTime;

   /**
    * access type
    * 
    * @param type
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   @Enumerated(EnumType.STRING)
   @Column(name = "access_type")
   private AccessPointTypeEnum type;

   /**
    * lift indicator <br/>
    * 
    * <ul>
    * <li>true if a lift is available on this access point</li>
    * <li>false if no lift is available on this access point</li>
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
    * <li>true if wheel chairs can follow this access point</li>
    * <li>false if wheel chairs can't follow this access point</li>
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
    * <li>true if a stairs are presents on this access point</li>
    * <li>false if no stairs are presents on this access point</li>
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
    * access point owner <br/>
    * should be a logical stop area such as commercial stop point or stop place <br/>
    * access links from or to this access should reach only this stop area or
    * it's children
    * 
    * @param containedIn
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "stop_area_id")
   private StopArea containedIn;

   /**
    * access links reaching this access point <br/>
    * 
    * @param accessLinks
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   @OneToMany(mappedBy = "accessPoint")
   private List<AccessLink> accessLinks = new ArrayList<AccessLink>(0);

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

   /*
    * (non-Javadoc)
    * 
    * @see
    * fr.certu.chouette.model.neptune.NeptuneLocalizedObject#toString(java.lang
    * .String, int)
    */
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

   /*
    * (non-Javadoc)
    * 
    * @see fr.certu.chouette.model.neptune.NeptuneLocalizedObject#complete()
    */
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

   /*
    * (non-Javadoc)
    * 
    * @see fr.certu.chouette.model.neptune.NeptuneIdentifiedObject#toURL()
    */
   @Override
   public String toURL()
   {
      return getContainedIn().toURL() + "/access_points/" + getId();
   }

}
