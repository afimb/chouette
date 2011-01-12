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
import fr.certu.chouette.model.neptune.type.PTDirectionEnum;


/**
 * 
 */
@NoArgsConstructor
public class Route extends NeptuneIdentifiedObject
{
	@Getter @Setter private Long oppositeRouteId;
	@Getter @Setter private Long lineId;
	@Getter @Setter private String publishedName;
	@Getter @Setter private String number; 
	@Getter @Setter private PTDirectionEnum direction;
	@Getter @Setter private String comment;
	@Getter @Setter private String wayBack;
	@Getter @Setter private List<String> journeyPatternIds;
	@Getter @Setter private List<JourneyPattern> journeyPatterns;
	@Getter @Setter private List<String> ptLinkIds;
	@Getter @Setter private List<PTLink> ptLinks;
	
	
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
