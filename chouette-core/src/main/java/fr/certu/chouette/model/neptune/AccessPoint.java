package fr.certu.chouette.model.neptune;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import fr.certu.chouette.model.neptune.type.Address;
import fr.certu.chouette.model.neptune.type.LongLatTypeEnum;
import fr.certu.chouette.model.neptune.type.ProjectedPoint;

/**
 * Neptune AccessPoint  
 * <p/>
 * Note for fields comment : <br/>
 * when readable is added to comment, a implicit getter is available <br/>
 * when writable is added to comment, a implicit setter is available
 * 
 */
public class AccessPoint extends NeptuneIdentifiedObject{
	private static final long serialVersionUID = 7520070228185917225L;
	
	/**
	 * Address 
	 * <br/><i>readable/writable</i>
	 */
	@Getter @Setter private Address address;
	/**
	 * Geographic referential for coordinates
	 * <br/><i>readable/writable</i>
	 */
	@Getter @Setter private LongLatTypeEnum longLatType;
	/**
	 * Latitude
	 * <br/><i>readable/writable</i>
	 */
	@Getter @Setter private BigDecimal latitude;
	/**
	 * Longitude
	 * <br/><i>readable/writable</i>
	 */
	@Getter @Setter private BigDecimal longitude;
	/**
	 * Projected point
	 * <br/><i>readable/writable</i>
	 */
	@Getter @Setter private ProjectedPoint projectedPoint;
	/**
	 * Comment
	 * <br/><i>readable/writable</i>
	 */
	@Getter @Setter private String comment;
	/**
	 * ObjectId of container
	 * <br/><i>readable/writable</i>
	 */
	@Getter @Setter private String containedIn;
	/**
	 * List of linked StopArea informations
	 * <br/><i>readable/writable</i>
	 */
	@Getter @Setter private List<AccessLink> accessLinks;
	/**
	 * Time for opening the AccessPoint
	 * <br/><i>readable/writable</i>
	 */
	@Getter @Setter private Date openningTime;
	/**
	 * Time for closing the AccessPoint
	 * <br/><i>readable/writable</i>
	 */
	@Getter @Setter private Date closingTime;
	/**
	 * access type
	 * <br/><i>readable/writable</i>
	 */
	@Getter @Setter private String type;
	/**
	 * Indicate if a Lift is available 
	 * <br/><i>readable/writable</i>
	 */
	@Getter @Setter private boolean liftAvailable;
	/**
	 * indicate if the link is equipped for mobility restricted persons 
	 * <br/><i>readable/writable</i>
	 */
	@Getter @Setter private boolean mobilityRestrictedSuitable;
	/**
	 * indicate if stairs are present on the link 
	 * <br/><i>readable/writable</i>
	 */
	@Getter @Setter private boolean stairsAvailable;
	
	/**
	 * add an AccessLink to AccesPoint if not already present
	 * <br/> no control is made on AccessLink's start and end links
	 * 
	 * @param accessLink link to add
	 */
	public void addAccessLink(AccessLink accessLink)
	{
		if (accessLinks == null) accessLinks = new ArrayList<AccessLink>();
		if (!accessLinks.contains(accessLink)) accessLinks.add(accessLink);
	}
	
	/**
	 * add a collection of AccessLinks to AccesPoint if not already presents
	 * <br/> no control is made on AccessLink's start and end links
	 * 
	 * @param accessLinkCollection links to add
	 */
	public void addAccessLinks(Collection<AccessLink> accessLinkCollection)
	{
		if (accessLinks == null) accessLinks = new ArrayList<AccessLink>();
		
		for (AccessLink accessLink : accessLinkCollection) 
		{
			if (!accessLinks.contains(accessLink)) accessLinks.add(accessLink);
		}
		
	}
	
	/**
	 * remove an AccessLink from AccesPoint if present
	 * 
	 * @param accessLink to remove
	 */
	public void removeAccessLink(AccessLink accessLink)
	{
		if (accessLinks == null) accessLinks = new ArrayList<AccessLink>();
		if (accessLinks.contains(accessLink)) accessLinks.remove(accessLink);
	}
	
	
	
}
