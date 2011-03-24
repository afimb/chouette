package fr.certu.chouette.model.neptune;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import fr.certu.chouette.model.neptune.type.Address;
import fr.certu.chouette.model.neptune.type.LongLatTypeEnum;
import fr.certu.chouette.model.neptune.type.ProjectedPoint;

/**
 * 
 * @author mamadou keira
 *
 */
public class AccessPoint extends NeptuneIdentifiedObject{
	private static final long serialVersionUID = 7520070228185917225L;
	@Getter @Setter private Address address;
	@Getter @Setter private LongLatTypeEnum longLatType;
	@Getter @Setter private BigDecimal latitude;
	@Getter @Setter private BigDecimal longitude;
	@Getter @Setter private ProjectedPoint projectedPoint;
	@Getter @Setter private String comment;
	@Getter @Setter private String containedIn;
	@Getter @Setter private List<AccessLink> accessLinks;
	@Getter @Setter private Date openningTime;
	@Getter @Setter private Date closingTime;
	@Getter @Setter private String type;
	/**
	 * Indicate if a Lift is available 
	 * <br/><i>readable/writable</i>
	 */
	@Getter @Setter boolean liftAvailable;
	/**
	 * indicate if the link is equipped for mobility restricted persons 
	 * <br/><i>readable/writable</i>
	 */
	@Getter @Setter boolean mobilityRestrictedSuitable;
	/**
	 * indicate if stairs are present on the link 
	 * <br/><i>readable/writable</i>
	 */
	@Getter @Setter boolean stairsAvailable;
	
	public void addAccessLink(AccessLink accessLink){
		if (accessLinks == null) accessLinks = new ArrayList<AccessLink>();
		accessLinks.add(accessLink);
	}
}
