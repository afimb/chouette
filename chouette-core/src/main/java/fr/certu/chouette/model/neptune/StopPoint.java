package fr.certu.chouette.model.neptune;

import java.math.BigDecimal;

import fr.certu.chouette.model.neptune.type.LongLatTypeEnum;

import lombok.Getter;
import lombok.Setter;

public class StopPoint extends NeptuneIdentifiedObject
{
	
	// private ChouetteLineDescription 	chouetteLineDescription;
	// private Address 					address;
	@Getter @Setter private LongLatTypeEnum 			longLatType;
	// private ProjectedPoint 				projectedPoint;
	@Getter @Setter private String 						comment;
	@Getter @Setter private BigDecimal 					latitude;
	@Getter @Setter private BigDecimal 					longitude;
	// private StopArea 					containedInStopArea 		= null;
	@Getter @Setter private String 						containedInStopAreaId;
	@Getter @Setter private String						lineIdShortcut;
	@Getter @Setter private Line						line;
	@Getter @Setter private String						ptNetworkIdShortcut;
	@Getter @Setter private PTNetwork					ptNetwork;
	
}
