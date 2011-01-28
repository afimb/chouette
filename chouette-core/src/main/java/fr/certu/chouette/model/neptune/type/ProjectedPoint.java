package fr.certu.chouette.model.neptune.type;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

public class ProjectedPoint {
	@Getter @Setter BigDecimal x;
	@Getter @Setter BigDecimal y;
	@Getter @Setter String projectionType;
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("x=").append(x).append(" y=").append(y).append(" projection=").append(projectionType);
		return sb.toString();
	}
}
