package fr.certu.chouette.model.neptune;

import java.math.BigDecimal;

import fr.certu.chouette.model.neptune.type.Address;
import fr.certu.chouette.model.neptune.type.LongLatTypeEnum;
import fr.certu.chouette.model.neptune.type.ProjectedPoint;
import lombok.Getter;
import lombok.Setter;

public class AreaCentroid extends NeptuneIdentifiedObject {
	@Getter @Setter private Address address;
	@Getter @Setter private LongLatTypeEnum longLatType;
	@Getter @Setter private BigDecimal latitude;
	@Getter @Setter private BigDecimal longitude;
	@Getter @Setter private ProjectedPoint projectedPoint;
	@Getter @Setter private String comment;
	@Getter @Setter private String containedInStopAreaId;
	@Getter @Setter private StopArea containedInStopArea;
}
