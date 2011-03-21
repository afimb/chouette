package fr.certu.chouette.model.neptune;

import java.util.List;

import fr.certu.chouette.model.neptune.type.facility.AllFacilitiesFeatureStructureType;

import lombok.Getter;
import lombok.Setter;

public class Facility extends NeptuneIdentifiedObject {
	private static final long serialVersionUID = -2150117548707325330L;
	@Getter @Setter private String stopAreaId;
	@Getter @Setter private String lineId;
	@Getter @Setter private String connectionLinkId;
	@Getter @Setter private String stopPointId;
	@Getter @Setter private String description;
	@Getter @Setter private boolean freeAccess;
	@Getter @Setter private String comment;
	@Getter @Setter private List<AllFacilitiesFeatureStructureType> facilityFeature;
	
}
