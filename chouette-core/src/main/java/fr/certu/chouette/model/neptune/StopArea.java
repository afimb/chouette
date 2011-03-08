package fr.certu.chouette.model.neptune;

import java.util.ArrayList;
import java.util.List;

import fr.certu.chouette.filter.DetailLevelEnum;
import fr.certu.chouette.model.neptune.type.ChouetteAreaEnum;
import fr.certu.chouette.model.neptune.type.UserNeedEnum;

import lombok.Getter;
import lombok.Setter;

public class StopArea extends NeptuneIdentifiedObject {
	private static final long serialVersionUID = 4548672479038099240L;
	@Getter @Setter List<String> boundaryPoints;
	@Getter @Setter String areaCentroidId;
	@Getter @Setter AreaCentroid areaCentroid;
	@Getter @Setter String comment;
	@Getter @Setter List<String> containedStopIds;
	@Getter @Setter List<StopArea> containedStopAreas;
	@Getter @Setter List<StopPoint> containedStopPoints;
	@Getter @Setter StopArea parentStopArea;
	@Getter @Setter Long parentId;
	@Getter @Setter ChouetteAreaEnum areaType;
	@Getter @Setter int fareCode;
	@Getter @Setter boolean liftAvailable;
	@Getter @Setter boolean mobilityRestrictedSuitable;
	@Getter @Setter boolean stairsAvailable;
	@Getter @Setter String nearestTopicName;
	@Getter @Setter String registrationNumber;
	@Getter @Setter List<UserNeedEnum> userNeeds;
	@Getter @Setter List<ConnectionLink> connectionLinks;
	
	public void addBoundaryPoint(String boundaryPoint)
	{
		if (boundaryPoints == null) boundaryPoints = new ArrayList<String>();
		boundaryPoints.add(boundaryPoint);
	}

	public void addContainedStopAreaId(String containedStopAreaId)
	{
		if (containedStopIds == null) containedStopIds = new ArrayList<String>();
		containedStopIds.add(containedStopAreaId);
	}

	public void addContainedStopArea(StopArea containedStopArea)
	{
		if (containedStopAreas == null) containedStopAreas = new ArrayList<StopArea>();
		containedStopAreas.add(containedStopArea);
	}
	
	public void addContainedStopPoint(StopPoint containedStopPoint)
	{
		if (containedStopPoints == null) containedStopPoints = new ArrayList<StopPoint>();
		containedStopPoints.add(containedStopPoint);
	}
	
	public void addUserNeed(UserNeedEnum userNeed)
	{
		if (userNeeds == null) userNeeds = new ArrayList<UserNeedEnum>();
		userNeeds.add(userNeed);
	}
	
	public void addConnectionLink(ConnectionLink connectionLink)
	{
		if (connectionLinks == null) connectionLinks = new ArrayList<ConnectionLink>();
		connectionLinks.add(connectionLink);
	}
	
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
			containedStopAreas = null;
			containedStopPoints = null;
			parentStopArea = null;
			connectionLinks = null;
			break;
		case NARROW_DEPENDENCIES : 
			if (getParentStopArea() != null) getParentStopArea().expand(DetailLevelEnum.ATTRIBUTE);
			if (getContainedStopAreas() != null)
			{
				for (StopArea containedStopArea : getContainedStopAreas())
				{
					containedStopArea.expand(DetailLevelEnum.ATTRIBUTE);
				}
			}
			if (getContainedStopPoints() != null)
			{
				for (StopPoint containedStopPoint : getContainedStopPoints())
				{
					containedStopPoint.expand(DetailLevelEnum.ATTRIBUTE);
				}
			}
			if (getConnectionLinks() != null)
			{
				for (ConnectionLink connectionLink : getConnectionLinks())
				{
					connectionLink.expand(DetailLevelEnum.ATTRIBUTE);
				}
			}
			break;
		case STRUCTURAL_DEPENDENCIES : 
		case ALL_DEPENDENCIES :
			if (getParentStopArea() != null) getParentStopArea().expand(level);
			if (getContainedStopAreas() != null)
			{
				for (StopArea containedStopArea : getContainedStopAreas())
				{
					containedStopArea.expand(level);
				}
			}
			if (getContainedStopPoints() != null)
			{
				for (StopPoint containedStopPoint : getContainedStopPoints())
				{
					containedStopPoint.expand(DetailLevelEnum.ATTRIBUTE);
				}
			}
			if (getConnectionLinks() != null)
			{
				for (ConnectionLink connectionLink : getConnectionLinks())
				{
					connectionLink.expand(level);
				}
			}
		}
	} 
	
	@Override
	public String toString(String indent,int level)
	{
		StringBuilder sb = new StringBuilder(super.toString(indent,level));
		sb.append("\n").append(indent).append("  areaCentroidId = ").append(areaCentroidId);
		sb.append("\n").append(indent).append("  comment = ").append(comment);
		sb.append("\n").append(indent).append("  areaType = ").append(areaType);
		sb.append("\n").append(indent).append("  fareCode = ").append(fareCode);
		sb.append("\n").append(indent).append("  liftAvailable = ").append(liftAvailable);
		sb.append("\n").append(indent).append("  mobilityRestrictedSuitable = ").append(mobilityRestrictedSuitable);
		sb.append("\n").append(indent).append("  nearestTopicName = ").append(nearestTopicName);
		sb.append("\n").append(indent).append("  registrationNumber = ").append(registrationNumber);
		sb.append("\n").append(indent).append("  stairsAvailable = ").append(stairsAvailable);

		if(userNeeds != null){
			sb.append("\n").append(indent).append(CHILD_ARROW).append("userNeeds");
			for (UserNeedEnum userNeed : getUserNeeds())
			{
				sb.append("\n").append(indent).append(CHILD_LIST_ARROW).append(userNeed);
			}
		}

		if(boundaryPoints != null){
			sb.append("\n").append(indent).append(CHILD_ARROW).append("boundaryPoints");
			for (String boundaryPoint : getBoundaryPoints())
			{
				sb.append("\n").append(indent).append(CHILD_LIST_ARROW).append(boundaryPoint);
			}
		}
		
		if(containedStopIds != null){
			sb.append("\n").append(indent).append(CHILD_ARROW).append("containedStopIds");
			for (String containedStopId : getContainedStopIds())
			{
				sb.append("\n").append(indent).append(CHILD_LIST_ARROW).append(containedStopId);
			}
		}
		
		if (level > 0)
		{
			int childLevel = level -1;
			String childIndent = indent + CHILD_INDENT;
			if (areaCentroid != null) 
			{
				sb.append("\n").append(indent).append(CHILD_ARROW).append(areaCentroid.toString(childIndent,0));
			}
			if (parentStopArea != null)
			{
				sb.append("\n").append(indent).append(CHILD_ARROW).append(parentStopArea.toString(childIndent,childLevel));
				
			}
			childIndent = indent + CHILD_LIST_INDENT;
			if (connectionLinks != null)
			{
				sb.append("\n").append(indent).append(CHILD_ARROW).append("connectionLinks");
				for (ConnectionLink connectionLink : getConnectionLinks())
				{
					sb.append("\n").append(indent).append(CHILD_LIST_ARROW).append(connectionLink.toString(childIndent,0));
				}
			}
		}

		return sb.toString();
	}
}
