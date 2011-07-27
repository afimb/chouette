package fr.certu.chouette.model.neptune;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import fr.certu.chouette.model.neptune.type.FacilityLocation;
import fr.certu.chouette.model.neptune.type.facility.FacilityFeature;
/**
 * 
 * @author mamadou keira
 *
 */
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
	@Getter @Setter private Boolean freeAccess;
	/**
     * keeps track of state for field: _freeAccess
     */
	// @Getter @Setter private boolean has_freeAccess;

	@Getter @Setter private String comment;
	@Getter @Setter private FacilityLocation facilityLocation;
	@Getter @Setter private List<FacilityFeature> facilityFeatures;	
	
	
	public void addFacilityFeature(FacilityFeature facilityFeature)
	{
		if (facilityFeatures == null) facilityFeatures = new ArrayList<FacilityFeature>();
		if (!facilityFeatures.contains(facilityFeature)) facilityFeatures.add(facilityFeature);
	}
	public void removeFacilityFeature(FacilityFeature facilityFeature)
	{
		if (facilityFeatures == null) facilityFeatures = new ArrayList<FacilityFeature>();
		if (facilityFeatures.contains(facilityFeature)) facilityFeatures.remove(facilityFeature);
	}
}
