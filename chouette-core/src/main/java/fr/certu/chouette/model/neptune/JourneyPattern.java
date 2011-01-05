package fr.certu.chouette.model.neptune;

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
		String s = super.toString(indent,level);
		s += "\n"+indent+"routeId = "+routeId;
		s += "\n"+indent+"publishedName = "+publishedName;
		s += "\n"+indent+"origin = "+origin;
		s += "\n"+indent+"destination = "+destination;
		s += "\n"+indent+"registrationNumber = "+registrationNumber;
		s += "\n"+indent+"comment = "+comment;

		if (level > 0)
		{
			if (stopPoints != null)
			{
				s+= "\n"+indent+CHILD_ARROW+"stopPointsIds";
				for (StopPoint stopPoint : getStopPoints())
				{
					s+= "\n"+indent+CHILD_LIST_ARROW+""+stopPoint.getObjectId();
				}
			}
		}
		return s;
	}


}
