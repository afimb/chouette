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


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProjectedPoint other = (ProjectedPoint) obj;
		if (projectionType == null) {
			if (other.projectionType != null)
				return false;
		} else if (!projectionType.equals(other.projectionType))
			return false;
		if (x == null) {
			if (other.x != null)
				return false;
		} else if (!x.equals(other.x))
			return false;
		if (y == null) {
			if (other.y != null)
				return false;
		} else if (!y.equals(other.y))
			return false;
		return true;
	}
	
}
