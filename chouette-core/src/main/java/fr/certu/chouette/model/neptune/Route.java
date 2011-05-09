/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */
package fr.certu.chouette.model.neptune;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import fr.certu.chouette.filter.DetailLevelEnum;
import fr.certu.chouette.model.neptune.type.PTDirectionEnum;


/**
 * Neptune Route 
 * <p/>
 * Note for fields comment : <br/>
 * when readable is added to comment, a implicit getter is available <br/>
 * when writable is added to comment, a implicit setter is available
 */
@NoArgsConstructor
public class Route extends NeptuneIdentifiedObject
{
	private static final long serialVersionUID = -2249654966081042738L;
	/**
	 * Database foreign key referring to the route's wayback route<br/>
	 * Meaningless after import action
	 * <br/><i>readable/writable</i>
	 */
	@Getter @Setter private Long oppositeRouteId; // FK 
	/**
	 * Database foreign key referring to the route's line<br/>
	 * Meaningless after import action
	 * <br/><i>readable/writable</i>
	 */
	@Getter @Setter private Long lineId;          // FK 
	
	@Getter @Setter private Line line;
	/**
	 * Public name for travellers
	 * <br/><i>readable/writable</i>
	 */
	@Getter @Setter private String publishedName; // BD
	/**
	 * Number of the route (characters) 
	 * <br/><i>readable/writable</i>
	 */
	@Getter @Setter private String number;        //  BD
	/**
	 * Direction (geographical, clockwise or logical)
	 * <br/><i>readable/writable</i>
	 */
	@Getter @Setter private PTDirectionEnum direction; // BD 
	/**
	 * Comment
	 * <br/><i>readable/writable</i>
	 */
	@Getter @Setter private String comment; // BD
	/**
	 * A logical direction (French extension)
	 * <ul>
	 * <li>A for Outward (Aller)</li>
	 * <li>R for Return  (Retour)</li>
	 * </ul>
	 * <br/><i>readable/writable</i>
	 */
	@Getter @Setter private String wayBack; // BD 
	/**
	 * Neptune identification referring to the wayBackRoute of the route<br/>
	 * Meaningless after database read (see oppositeRouteId)
	 * <br/>Changes have no effect on database
	 * <br/><i>readable/writable</i>
	 */
	@Getter @Setter private String wayBackRouteId; 
	/**
	 * Neptune identification referring to the JourneyPatterns of the route<br/>
	 * Meaningless after database read (see journeyPatterns)
	 * <br/>Changes have no effect on database
	 * <br/><i>readable/writable</i>
	 */
	@Getter @Setter private List<String> journeyPatternIds; 
	/**
	 * The route's journey patterns objects <br/>
	 * Available on database read only if DetailLevel is at least NARROW_DEPENDENCIES
	 * <br/><i>readable/writable</i>
	 */
	@Getter @Setter private List<JourneyPattern> journeyPatterns; // FK inverse manquante à ajouter 
	/**
	 * Neptune identification referring to the PTLinks of the route<br/>
	 * Meaningless after database read (see ptLinks)
	 * <br/>Changes have no effect on database
	 * <br/><i>readable/writable</i>
	 */
	@Getter @Setter private List<String> ptLinkIds; 
	/**
	 * The route's ptLink objects <br/>
	 * Available on database read only if DetailLevel is at least NARROW_DEPENDENCIES
	 * <br/><i>readable/writable</i>
	 */
	@Getter @Setter private List<PTLink> ptLinks; // Table + FK inverse 
	
	
	@Getter @Setter private List<StopPoint> stopPoints;
	
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

	/**
	 * add a journeyPatternId to list only if not already present
	 * @param journeyPatternId
	 */
	public void addJourneyPatternId(String journeyPatternId){
		if(journeyPatternIds == null) journeyPatternIds = new ArrayList<String>();
		journeyPatternIds.add(journeyPatternId);
	}
	
	/**
	 * add a journeyPattern to list only if not already present
	 * @param journeyPattern
	 */
	public void addJourneyPattern(JourneyPattern journeyPattern){
		if(journeyPatterns == null) journeyPatterns = new ArrayList<JourneyPattern>();
		journeyPatterns.add(journeyPattern);
	}
	
	/**
	 * add a ptLinkId to list only if not already present
	 * @param ptLinkId
	 */
	public void addPTLinkId(String ptLinkId){
		if(ptLinkIds == null) ptLinkIds = new ArrayList<String>();
		ptLinkIds.add(ptLinkId);
	}
	
	/**
	 * add a ptLink to list only if not already present
	 * @param ptLink
	 */
	public void addPTLink(PTLink ptLink){
		if(ptLinks == null) ptLinks = new ArrayList<PTLink>();
		ptLinks.add(ptLink);
	}
	
	/**
	 * remove a journeyPatternId from list if present
	 * @param journeyPatternId
	 */
	public void removeJourneyPatternId(String journeyPatternId){
		if(journeyPatternIds != null) journeyPatternIds.remove(journeyPatternId);
	}
	
	/**
	 * remove a journeyPattern from list if present
	 * @param journeyPattern
	 */
	public void removeJourneyPattern(JourneyPattern journeyPattern){
		if(journeyPatterns != null) journeyPatterns.remove(journeyPattern);
	}
	
	/**
	 * remove a ptLinkId from list if present
	 * @param ptLinkId
	 */
	public void removePTLinkId(String ptLinkId){
		if(ptLinkIds != null) ptLinkIds.remove(ptLinkId);
		ptLinkIds.add(ptLinkId);
	}
	
	/**
	 * remove a ptLink from list if present
	 * @param ptLink
	 */
	public void removePTLink(PTLink ptLink){
		if(ptLinks != null) ptLinks.remove(ptLink);
	}
	
	@Override
	public boolean clean() {
		if(journeyPatterns == null){
			return false;
		}
		for (Iterator<JourneyPattern> iterator = journeyPatterns.iterator(); iterator.hasNext();) {
			JourneyPattern journeyPattern = iterator.next();
			if(journeyPattern == null || !journeyPattern.clean()){
				iterator.remove();
			}
		}
		if(journeyPatterns.isEmpty()){
			return false;
		}
		return true;
	}
}
