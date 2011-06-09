package fr.certu.chouette.model.neptune;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import fr.certu.chouette.filter.DetailLevelEnum;
import fr.certu.chouette.model.neptune.type.Address;
import fr.certu.chouette.model.neptune.type.LongLatTypeEnum;
import fr.certu.chouette.model.neptune.type.ProjectedPoint;

public class StopPoint extends NeptuneIdentifiedObject
{
	private static final long serialVersionUID = -4913573673645997423L;
	@Getter @Setter private Address address;
	@Getter @Setter private LongLatTypeEnum longLatType;
	@Getter @Setter private BigDecimal latitude;
	@Getter @Setter private BigDecimal longitude;
	@Getter @Setter private ProjectedPoint projectedPoint;
	@Getter @Setter private String comment;
	@Getter @Setter private String containedInStopAreaId;
	@Getter @Setter private StopArea containedInStopArea;
	@Getter @Setter private String lineIdShortcut;
	@Getter @Setter private Line line;
	@Getter @Setter private String ptNetworkIdShortcut;
	@Getter @Setter private PTNetwork ptNetwork;
	@Getter @Setter private Integer position;
	@Getter @Setter private Route route;
	
	@Getter @Setter private List<Facility> facilities;

	public void addFacility(Facility facility)
	{
		if(facilities == null) facilities = new ArrayList<Facility>();
		if(!facilities.contains(facility)) facilities.add(facility);
	}
//	/* (non-Javadoc)
//	 * @see fr.certu.chouette.model.neptune.NeptuneBean#expand(fr.certu.chouette.manager.NeptuneBeanManager.DETAIL_LEVEL)
//	 */
//	@Override
//	public void expand(DetailLevelEnum level)
//	{
//		// to avoid circular call check if level is already set according to this level
//		if (getLevel().ordinal() >= level.ordinal()) return;
//		super.expand(level);
//		switch (level)
//		{
//		case ATTRIBUTE : 
//			ptNetwork = null;
//			line = null;
//			containedInStopArea = null;
//			break;
//		case NARROW_DEPENDENCIES : 
//			if (getPtNetwork() != null) getPtNetwork().expand(DetailLevelEnum.ATTRIBUTE);
//			if (getLine() != null) getLine().expand(DetailLevelEnum.ATTRIBUTE);
//			if (getContainedInStopArea() != null) getContainedInStopArea().expand(DetailLevelEnum.ATTRIBUTE);
//			break;
//		case STRUCTURAL_DEPENDENCIES : 
//		case ALL_DEPENDENCIES :
//			if (getPtNetwork() != null) getPtNetwork().expand(DetailLevelEnum.ATTRIBUTE);
//			if (getLine() != null) getLine().expand(DetailLevelEnum.ATTRIBUTE);
//			if (getContainedInStopArea() != null) getContainedInStopArea().expand(level);
//		}
//	}

	@Override
	public String toString(String indent,int level)
	{
		StringBuilder sb = new StringBuilder(super.toString(indent,level));

		if (address != null) {
			sb.append("\n").append(indent).append("  address = ").append(address);			
		}

		if(longLatType != null){
			sb.append("\n").append(indent).append("  longLatType = ").append(longLatType);			
		}

		sb.append("\n").append(indent).append("  latitude = ").append(latitude);
		sb.append("\n").append(indent).append("  longitude = ").append(longitude);

		if(projectedPoint != null){
			sb.append("\n").append(indent).append("  projectedPoint = ").append(projectedPoint);
		}

		sb.append("\n").append(indent).append("  comment = ").append(comment);
		sb.append("\n").append(indent).append("  containedInStopAreaId = ").append(containedInStopAreaId);
		sb.append("\n").append(indent).append("  lineIdShortcut = ").append(lineIdShortcut);
		sb.append("\n").append(indent).append("  ptNetworkIdShortcut = ").append(ptNetworkIdShortcut);

		if (level > 0)
		{
			int childLevel = level -1;
			String childIndent = indent + CHILD_INDENT;
			if (containedInStopArea != null) 
			{
				sb.append("\n").append(indent).append(CHILD_ARROW).append(containedInStopArea.toString(childIndent,childLevel));
			}
		}

		return sb.toString();
	}
}
