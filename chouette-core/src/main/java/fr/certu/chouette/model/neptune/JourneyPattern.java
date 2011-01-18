package fr.certu.chouette.model.neptune;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import fr.certu.chouette.filter.DetailLevelEnum;

public class JourneyPattern extends NeptuneIdentifiedObject
{

	@Getter @Setter private String registrationNumber;
	@Getter @Setter private String comment;
	@Getter @Setter private String origin;
	@Getter @Setter private String destination;
	@Getter @Setter private String publishedName;
	@Getter @Setter private List<StopPoint>	stopPoints;
	@Getter @Setter private List<String>	stopPointIds;
	@Getter @Setter private String lineIdShortcut; // a confirmer
	@Getter @Setter private String routeId;
	@Getter @Setter private Route route;
	/* (non-Javadoc)
	 * @see fr.certu.chouette.model.neptune.NeptuneBean#expand(fr.certu.chouette.manager.NeptuneBeanManager.DETAIL_LEVEL)
	 */
	@Override
	public void expand(DetailLevelEnum level)
	{
		// to avoid circular call check if level is already set according to this level
		if (getLevel().ordinal() >= level.ordinal()) return;
		super.expand(level);
		switch (level)
		{
		case ATTRIBUTE : 
			stopPoints = null;
			route = null;
			break;
		case NARROW_DEPENDENCIES : 
			getRoute().expand(DetailLevelEnum.ATTRIBUTE);
			for (StopPoint stopPoint : getStopPoints())
			{
				stopPoint.expand(DetailLevelEnum.ATTRIBUTE);
			}
			break;
		case STRUCTURAL_DEPENDENCIES : 
		case ALL_DEPENDENCIES :
			getRoute().expand(DetailLevelEnum.ATTRIBUTE);
			for (StopPoint stopPoint : getStopPoints())
			{
				stopPoint.expand(level);
			}

		}
	} 

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(String indent,int level)
	{
		StringBuilder sb = new StringBuilder(super.toString(indent,level));
		sb.append("\n").append(indent).append("routeId = ").append(routeId);
		sb.append("\n").append(indent).append("publishedName = ").append(publishedName);
		sb.append("\n").append(indent).append("origin = ").append(origin);
		sb.append("\n").append(indent).append("destination = ").append(destination);
		sb.append("\n").append(indent).append("registrationNumber = ").append(registrationNumber);
		sb.append("\n").append(indent).append("comment = ").append(comment);

		if (stopPointIds != null)
		{
			sb.append("\n").append(indent).append(CHILD_ARROW).append("stopPointIds");
			for (String stopPointId : stopPointIds)
			{
				sb.append("\n").append(indent).append(CHILD_LIST_ARROW).append(stopPointId);
			}
		}
		if (level > 0)
		{
			if (stopPoints != null)
			{
				sb.append("\n").append(indent).append(CHILD_ARROW).append("stopPointsIds");
				for (StopPoint stopPoint : getStopPoints())
				{
					sb.append("\n").append(indent).append(CHILD_LIST_ARROW).append(stopPoint.getObjectId());
				}
			}
		}
		return sb.toString();
	}

	public void addStopPointId(String stopPointId)
	{
		if (stopPointIds== null) stopPointIds = new ArrayList<String>();
		stopPointIds.add(stopPointId);
	}
	
	public void addStopPoint(StopPoint stopPoint)
	{
		if (stopPoints== null) stopPoints = new ArrayList<StopPoint>();
		stopPoints.add(stopPoint);
	}
}
