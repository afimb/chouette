package fr.certu.chouette.service.validation.amivif;

import java.util.ArrayList;
import java.util.List;

public class Route extends TridentObject {
	
	private String					name;													// 0..1
	private String					publishedName;											// 0..1
	private String					number;													// 0..1
	private Direction				direction;												// 0..1
	private List<String>			pTLinkIds			= new ArrayList<String>();			// 1..w
	private List<PTLink>			pTLinks				= new ArrayList<PTLink>();			// 1..w
	private List<String>			journeyPatternIds	= new ArrayList<String>();			// 1..w
	private List<JourneyPattern>	journeyPatterns		= new ArrayList<JourneyPattern>();	// 1..w
	private String					wayBackRouteId;											// 0..1
	private Route					wayBackRoute;											// 0..1
	private String					comment;												// 0..1
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setPublishedName(String publishedName) {
		this.publishedName = publishedName;
	}
	
	public String getPublishedName() {
		return publishedName;
	}
	
	public void setNumber(String number) {
		this.number = number;
	}
	
	public String getNumber() {
		return number;
	}
	
	public void setDirection(Direction direction) {
		this.direction = direction;
	}
	
	public Direction getDirection() {
		return direction;
	}
	
	public List<String> getPTLinkIds() {
		return pTLinkIds;
	}
	
	public void addPTLinkId(String pTLinkId) {
		pTLinkIds.add(pTLinkId);
	}
	
	public void removePTLinkId(String pTLinkId) {
		pTLinkIds.remove(pTLinkId);
	}
	
	public void removePTLinkId(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getPTLinkIdsCount()))
			throw new IndexOutOfBoundsException();
		pTLinkIds.remove(i);
	}
	
	public String getPTLinkId(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getPTLinkIdsCount()))
			throw new IndexOutOfBoundsException();
		return (String)pTLinkIds.get(i);
	}
	
	public int getPTLinkIdsCount() {
		if (pTLinkIds == null)
			return 0;
		return pTLinkIds.size();
	}
	
	public void setPTLinks(List<PTLink> pTLinks) {
		this.pTLinks = pTLinks;
	}
	
	public List<PTLink> getPTLinks() {
		return pTLinks;
	}
	
	public void addPTLink(PTLink pTLink) {
		pTLinks.add(pTLink);
	}
	
	public void removePTLink(PTLink pTLink) {
		pTLinks.remove(pTLink);
	}
	
	public void removePTLink(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getPTLinksCount()))
			throw new IndexOutOfBoundsException();
		pTLinks.remove(i);
	}
	
	public PTLink getPTLink(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getPTLinksCount()))
			throw new IndexOutOfBoundsException();
		return (PTLink)pTLinks.get(i);
	}
	
	public int getPTLinksCount() {
		if (pTLinks == null)
			return 0;
		return pTLinks.size();
	}
	
	public List<String> getJourneyPatternIds() {
		return journeyPatternIds;
	}
	
	public void addJourneyPatternId(String journeyPatternId) {
		journeyPatternIds.add(journeyPatternId);
	}
	
	public void removeJourneyPatternId(String journeyPatternId) {
		journeyPatternIds.remove(journeyPatternId);
	}
	
	public void removeJourneyPatternId(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getJourneyPatternIdsCount()))
			throw new IndexOutOfBoundsException();
		journeyPatternIds.remove(i);
	}
	
	public String getJourneyPatternId(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getJourneyPatternIdsCount()))
			throw new IndexOutOfBoundsException();
		return (String)journeyPatternIds.get(i);
	}
	
	public int getJourneyPatternIdsCount() {
		if (journeyPatternIds == null)
			return 0;
		return journeyPatternIds.size();
	}
	
	public void setJourneyPatterns(List<JourneyPattern> journeyPatterns) {
		this.journeyPatterns = journeyPatterns;
	}
	
	public List<JourneyPattern> getJourneyPatterns() {
		return journeyPatterns;
	}
	
	public void addJourneyPattern(JourneyPattern journeyPattern) {
		journeyPatterns.add(journeyPattern);
	}
	
	public void removeJourneyPattern(JourneyPattern journeyPattern) {
		journeyPatterns.remove(journeyPattern);
	}
	
	public void removeJourneyPattern(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getJourneyPatternsCount()))
			throw new IndexOutOfBoundsException();
		journeyPatterns.remove(i);
	}
	
	public JourneyPattern getJourneyPattern(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getJourneyPatternsCount()))
			throw new IndexOutOfBoundsException();
		return (JourneyPattern)journeyPatterns.get(i);
	}
	
	public int getJourneyPatternsCount() {
		if (journeyPatterns == null)
			return 0;
		return journeyPatterns.size();
	}
	
	public void setWayBackRouteId(String wayBackRouteId) {
		this.wayBackRouteId = wayBackRouteId;
	}
	
	public String getWayBackRouteId() {
		return wayBackRouteId;
	}
	
	public void setWayBackRoute(Route wayBackRoute) {
		this.wayBackRoute = wayBackRoute;
	}
	
	public Route getWayBackRoute() {
		return wayBackRoute;
	}
	
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public String getComment() {
		return comment;
	}
	
	public enum Direction {
        North,
        NorthEast,
        East,
        SouthEast,
        South,
        SouthWest,
        West,
        NorthWest,
        ClockWise,
        CounterClockWise,
        A,
        R
	}
}
