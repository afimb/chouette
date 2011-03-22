package fr.certu.chouette.model.neptune;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import fr.certu.chouette.model.neptune.type.Address;
import fr.certu.chouette.model.neptune.type.LongLatTypeEnum;
import fr.certu.chouette.model.neptune.type.ProjectedPoint;
import fr.certu.chouette.model.neptune.type.facility.FacilityFeature;

public class Facility extends NeptuneIdentifiedObject {
	private static final long serialVersionUID = -2150117548707325330L;
	
	@Getter @Setter private String stopAreaId;
	@Getter @Setter private StopArea stopArea;
	@Getter @Setter private String lineId;
	@Getter @Setter private Line line;
	@Getter @Setter private String connectionLinkId;
	@Getter @Setter private ConnectionLink connectionLink;
	@Getter @Setter private String stopPointId;
	@Getter @Setter private StopPoint stopPoint;
	@Getter @Setter private String description;
	 /**
     * Is the access restricted or authorised to everybody
     */
	@Getter @Setter private boolean freeAccess;
	/**
     * keeps track of state for field: _freeAccess
     */
	@Getter @Setter private boolean has_freeAccess;

	@Getter @Setter private String comment;
	 /**
     * Field longitude.
     */
	@Getter @Setter private BigDecimal longitude;
    /**
     * Field latitude.
     */
	@Getter @Setter private BigDecimal latitude;
    /**
     * Field longLatType.
     */
	@Getter @Setter private LongLatTypeEnum longLatType;
    /**
     * Field address.
     */
	@Getter @Setter private Address address;
    /**
     * Field projectedPoint.
     */
	@Getter @Setter private ProjectedPoint projectedPoint;
    /**
     * Field containedIn.
     */
	@Getter @Setter private String containedIn;
	@Getter @Setter private List<FacilityFeature> facilityFeatures;	
	
	
	public void addFacilityFeature(FacilityFeature facilityFeature){
		if (facilityFeatures == null) facilityFeatures = new ArrayList<FacilityFeature>();
		facilityFeatures.add(facilityFeature);
	}
}
