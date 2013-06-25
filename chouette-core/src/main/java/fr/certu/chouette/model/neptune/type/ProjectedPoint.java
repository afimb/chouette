package fr.certu.chouette.model.neptune.type;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import fr.certu.chouette.model.neptune.StopArea;

/**
 * coordinates when an alternative projection referential is available
 * 
 * @author michel
 *
 */
@NoArgsConstructor
public class ProjectedPoint implements Serializable
{
	private static final long serialVersionUID = -2981993156157564399L;

	// constant for persistence fields

	/**
	 * x coordinate
	 */
	@Getter @Setter private BigDecimal x;
	/**
	 * y coordinate
	 */
	@Getter @Setter private BigDecimal y;
	/**
	 * projection system name (f.e. : epgs:27578)
	 */
	@Getter @Setter private String projectionType;
	
	public ProjectedPoint(StopArea area) 
	{
		x = area.getX();
		y = area.getY();
		projectionType = area.getProjectionType();
				
	}

	
	public void populateStopArea(StopArea area)
	{
		area.setX(x);
		area.setY(y);
		area.setProjectionType(projectionType);
	}
	
	
	@Override
	public String toString() 
	{
		StringBuilder sb = new StringBuilder();
		sb.append("x=").append(x).append(" y=").append(y).append(" projection=").append(projectionType);
		return sb.toString();
	}
}
