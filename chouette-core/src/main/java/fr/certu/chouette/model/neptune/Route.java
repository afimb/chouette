/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */
package fr.certu.chouette.model.neptune;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import fr.certu.chouette.filter.DetailLevelEnum;
import fr.certu.chouette.model.neptune.type.PTDirectionEnum;


/**
 * 
 */
@NoArgsConstructor
public class Route extends NeptuneIdentifiedObject
{
	@Getter @Setter private Long oppositeRouteId; // FK 
	@Getter @Setter private Long lineId;          // FK 
	@Getter @Setter private String publishedName; // BD
	@Getter @Setter private String number;        //  BD
	@Getter @Setter private PTDirectionEnum direction; // BD 
	@Getter @Setter private String comment; // BD
	@Getter @Setter private String wayBack; // BD 
	@Getter @Setter private List<String> journeyPatternIds; 
	@Getter @Setter private List<JourneyPattern> journeyPatterns; // FK inverse manquante à ajouter 
	@Getter @Setter private List<String> ptLinkIds; 
	@Getter @Setter private List<PTLink> ptLinks; // Table + FK inverse 
	
	
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
			journeyPatterns = null;
			ptLinks = null;
			break;
		case NARROW_DEPENDENCIES : 
			if (getJourneyPatterns() != null)
			{
				for (JourneyPattern journeyPattern : getJourneyPatterns())
				{
					journeyPattern.expand(DetailLevelEnum.ATTRIBUTE);
				}
			}
			if (getPtLinks() != null)
			{
				for (PTLink ptLink : getPtLinks())
				{
					ptLink.expand(DetailLevelEnum.ATTRIBUTE);
				}
			}
			break;
		case STRUCTURAL_DEPENDENCIES : 
		case ALL_DEPENDENCIES :
			if (getJourneyPatterns() != null)
			{
				for (JourneyPattern journeyPattern : getJourneyPatterns())
				{
					journeyPattern.expand(level);
				}
			}
			if (getPtLinks() != null)
			{
				for (PTLink ptLink : getPtLinks())
				{
					ptLink.expand(level);
				}
			}

		}
	} 

	
	/* (non-Javadoc)
	 * @see fr.certu.chouette.model.neptune.NeptuneIdentifiedObject#toString(java.lang.String, int)
	 */
	@Override
	public String toString(String indent,int level)
	{
		StringBuilder sb = new StringBuilder(super.toString(indent,level));
		sb.append("\n").append(indent).append("oppositeRouteId = ").append(oppositeRouteId);
		sb.append("\n").append(indent).append("lineId = ").append(lineId);
		sb.append("\n").append(indent).append("publishedName = ").append(publishedName);
		sb.append("\n").append(indent).append("number = ").append(number);
		sb.append("\n").append(indent).append("direction = ").append(direction);
		sb.append("\n").append(indent).append("comment = ").append(comment);
		sb.append("\n").append(indent).append("wayBack = ").append(wayBack);

		if (journeyPatternIds != null)
		{
			sb.append("\n").append(indent).append(CHILD_ARROW).append("journeyPatternIds");
			for (String journeyPatternId : journeyPatternIds)
			{
				sb.append("\n").append(indent).append(CHILD_LIST_ARROW).append(journeyPatternId);
			}
		}
		if (ptLinkIds != null)
		{
			sb.append("\n").append(indent).append(CHILD_ARROW).append("ptLinkIds");
			for (String ptLinkid : ptLinkIds)
			{
				sb.append("\n").append(indent).append(CHILD_LIST_ARROW).append(ptLinkid);
			}
		}
		if (level > 0)
		{
			int childLevel = level -1;
			String childIndent = indent + CHILD_LIST_INDENT;
			if (journeyPatterns != null)
			{
				sb.append("\n").append(indent).append(CHILD_ARROW).append("journey patterns");
				for (JourneyPattern journeyPattern : journeyPatterns)
				{
					sb.append("\n").append(indent).append(CHILD_LIST_ARROW).append(journeyPattern.toString(childIndent,childLevel));
				}
			}
			if (ptLinks != null)
			{
				sb.append("\n").append(indent).append(CHILD_ARROW).append("pt links");
				for (PTLink ptLink : ptLinks)
				{
					sb.append("\n").append(indent).append(CHILD_LIST_ARROW).append(ptLink.toString(childIndent,childLevel));
				}
			}
		}
		
		return sb.toString();
	}

	public void addJourneyPatternId(String journeyPatternId){
		if(journeyPatternIds == null) journeyPatternIds = new ArrayList<String>();
		journeyPatternIds.add(journeyPatternId);
	}
	
	public void addJourneyPattern(JourneyPattern journeyPattern){
		if(journeyPatterns == null) journeyPatterns = new ArrayList<JourneyPattern>();
		journeyPatterns.add(journeyPattern);
	}
	
	public void addPTLinkId(String ptLinkId){
		if(ptLinkIds == null) ptLinkIds = new ArrayList<String>();
		ptLinkIds.add(ptLinkId);
	}
	
	public void addPTLink(PTLink ptLink){
		if(ptLinks == null) ptLinks = new ArrayList<PTLink>();
		ptLinks.add(ptLink);
	}
	
	public void removeJourneyPatternId(String journeyPatternId){
		if(journeyPatternIds != null) journeyPatternIds.remove(journeyPatternId);
	}
	
	public void removeJourneyPattern(JourneyPattern journeyPattern){
		if(journeyPatterns != null) journeyPatterns.remove(journeyPattern);
	}
	
	public void removePTLinkId(String ptLinkId){
		if(ptLinkIds != null) ptLinkIds.remove(ptLinkId);
		ptLinkIds.add(ptLinkId);
	}
	
	public void removePTLink(PTLink ptLink){
		if(ptLinks != null) ptLinks.remove(ptLink);
	}
	
	
}
