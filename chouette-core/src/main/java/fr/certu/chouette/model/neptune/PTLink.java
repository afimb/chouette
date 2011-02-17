package fr.certu.chouette.model.neptune;

import java.math.BigDecimal;

import fr.certu.chouette.filter.DetailLevelEnum;

import lombok.Getter;
import lombok.Setter;

public class PTLink extends NeptuneIdentifiedObject
{// TODO add table  PTLink
	@Getter @Setter private String comment;  // BD
	@Getter @Setter private BigDecimal linkDistance; // BD 
	@Getter @Setter private String startOfLinkId;
	@Getter @Setter private StopPoint startOfLink; // FK 
	@Getter @Setter private String endOfLinkId;
	@Getter @Setter private StopPoint endOfLink; // FK 
	@Getter @Setter private String routeId; 
	@Getter @Setter private Route route; // FK 
	
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
			startOfLink = null;
			endOfLink = null;
			break;
		case NARROW_DEPENDENCIES : 
			if (getStartOfLink() != null) getStartOfLink().expand(DetailLevelEnum.ATTRIBUTE);
			if (getEndOfLink() != null) getEndOfLink().expand(DetailLevelEnum.ATTRIBUTE);
			break;
		case STRUCTURAL_DEPENDENCIES : 
		case ALL_DEPENDENCIES :
			if (getStartOfLink() != null) getStartOfLink().expand(level);
			if (getEndOfLink() != null) getEndOfLink().expand(level);
		}
	}
	
	@Override
	public String toString(String indent, int level) {
		StringBuilder sb = new StringBuilder(super.toString(indent,level));
		sb.append("\n").append(indent).append("startOfLinkId = ").append(startOfLinkId);
		sb.append("\n").append(indent).append("endOfLinkId = ").append(endOfLinkId);
		if(linkDistance != null){
			sb.append("\n").append(indent).append("linkDistance = ").append(linkDistance.toPlainString());			
		}
		sb.append("\n").append(indent).append("comment = ").append(comment);
		
		if (level > 0)
		{
			String childIndent = indent + CHILD_INDENT;
			if (startOfLink != null) 
			{
				sb.append("\n").append(indent).append(CHILD_ARROW).append(startOfLink.toString(childIndent,0));
			}
			if (endOfLink != null) 
			{
				sb.append("\n").append(indent).append(CHILD_ARROW).append(endOfLink.toString(childIndent,0));
			}
		}

		return sb.toString();
	}
}
