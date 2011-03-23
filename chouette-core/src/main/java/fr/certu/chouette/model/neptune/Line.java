/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */
package fr.certu.chouette.model.neptune;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import fr.certu.chouette.filter.DetailLevelEnum;
import fr.certu.chouette.model.neptune.type.ImportedItems;
import fr.certu.chouette.model.neptune.type.TransportModeNameEnum;
import fr.certu.chouette.model.neptune.type.UserNeedEnum;

/**
 * Neptune Line 
 * <p/>
 * Note for fields comment : <br/>
 * when readable is added to comment, a implicit getter is available <br/>
 * when writable is added to comment, a implicit setter is available
 */
@NoArgsConstructor
public class Line extends NeptuneIdentifiedObject
{
	private static final long serialVersionUID = -8086291270595894778L;
	/**
	 * Database foreign key referring to the line's network<br/>
	 * Meaningless after import action
	 * <br/><i>readable/writable</i>
	 */
	@Getter @Setter private Long ptNetworkId; // BD FK directe
	/**
	 * Database foreign key referring to the line's company<br/>
	 * Not in Neptune model, it's a shortcut to default VehicleJourney operatorId <br/>
	 * Meaningless after import action
	 * <br/><i>readable/writable</i>
	 */
	@Getter @Setter private Long companyId;   // BD Fk directe hors modèle (compagnie par défaut à usage interne) 
	/**
	 * Number of the line (characters) 
	 * <br/><i>readable/writable</i>
	 */
	@Getter @Setter private String number;    // BD
	/**
	 * Public name for travellers
	 * <br/><i>readable/writable</i>
	 */
	@Getter @Setter private String publishedName; // BD
	/**
	 * Transport Mode (Bus, Train, ...)
	 * <br/><i>readable/writable</i>
	 */
	@Getter @Setter private TransportModeNameEnum transportModeName; // BD
	/**
	 * Registration number
	 * <br/><i>readable/writable</i>
	 */
	@Getter @Setter private String registrationNumber; // BD 
	/**
	 * Comment
	 * <br/><i>readable/writable</i>
	 */
	@Getter @Setter private String comment; // BD
	/**
	 * Neptune identification referring to the line's network
	 * <br/><i>readable/writable</i>
	 * 
	 */
	@Getter @Setter private String ptNetworkIdShortcut; // Hors BD, habillé par la relation FK
	/**
	 * Neptune identification referring to the line's routes
	 * <br/>Changes have no effect on database (see ptNetwork)
	 * <br/><i>readable/writable</i>
	 */
	@Getter @Setter private List<String> routeIds; // résolu par la FK
	/**
	 * Neptune identification referring to the departures/arrivals stoppoints of the line's JourneyPatterns<br/>
	 * Meaningless after database read<br/>
	 * Changes have no effect on database (see routes)
	 * <br/><i>readable/writable</i>
	 */
	@Getter @Setter private List<String> lineEnds; // calculé quand nécessaire (StopPoints)
	/**
	 * The line's network object <br/>
	 * Available on database read only if DetailLevel is at least NARROW_DEPENDENCIES<br/>
	 * Changes have no effect on database
	 * <br/><i>readable/writable</i>
	 */
	@Getter @Setter private PTNetwork ptNetwork; // FK
	/**
	 * The line's company object <br/>
	 * Available on database read only if DetailLevel is at least NARROW_DEPENDENCIES
	 * <br/><i>readable/writable</i>
	 */
	@Getter @Setter private Company company; // FK 
	/**
	 * The line's route objects <br/>
	 * Available on database read only if DetailLevel is at least NARROW_DEPENDENCIES
	 * <br/><i>readable/writable</i>
	 */
	@Getter @Setter private List<Route> routes; // FK 
	/**
	 * Indicate whenever the line is suitable for mobility restricted persons
	 * <br/><i>readable/writable</i>
	 */
	@Getter @Setter boolean mobilityRestrictedSuitable; // Ajout en base init à false
	/**
	 * List of the specific user needs available
	 * <br/><i>readable/writable</i>
	 */
	@Getter @Setter List<UserNeedEnum> userNeeds; // Ajout dans la base colonne UserNeeds  masque binaire 32 bits

	@Getter @Setter private ImportedItems importedItems;
	/**
	 * add a user needs enumeration value to the line<br/>
	 * do nothing if user need is already present
	 * 
	 * @param userNeed
	 */
	public void addUserNeed(UserNeedEnum userNeed)
	{
		if (userNeeds == null) userNeeds = new ArrayList<UserNeedEnum>();
		if (!userNeeds.contains(userNeed)) userNeeds.add(userNeed);
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
			ptNetwork = null;
			company = null;
			routes = null;
			break;
		case NARROW_DEPENDENCIES : 
			if (getPtNetwork() != null) getPtNetwork().expand(DetailLevelEnum.ATTRIBUTE);
			if (getCompany() != null) getCompany().expand(DetailLevelEnum.ATTRIBUTE);
			if (getRoutes() != null)
			{
				for (Route route : getRoutes())
				{
					route.expand(DetailLevelEnum.ATTRIBUTE);
				}
			}
			break;
		case STRUCTURAL_DEPENDENCIES : 
		case ALL_DEPENDENCIES :
			if (getPtNetwork() != null) getPtNetwork().expand(DetailLevelEnum.ATTRIBUTE);
			if (getCompany() != null) getCompany().expand(DetailLevelEnum.ATTRIBUTE);
			if (getRoutes() != null)
			{
				for (Route route : getRoutes())
				{
					route.expand(level);
				}
			}

		}
	} 

	@Override
	public String toString(String indent,int level)
	{
		StringBuilder sb = new StringBuilder(super.toString(indent,level));
		sb.append("\n").append(indent).append("  ptNetworkId = ").append(ptNetworkId);
		sb.append("\n").append(indent).append("  ptNetworkIdShortcut = ").append(ptNetworkIdShortcut);
		sb.append("\n").append(indent).append("  companyId = ").append(companyId);
		sb.append("\n").append(indent).append("  number = ").append(number);
		sb.append("\n").append(indent).append("  publishedName = ").append(publishedName);
		sb.append("\n").append(indent).append("  transportModeName = ").append(transportModeName);
		sb.append("\n").append(indent).append("  registrationNumber = ").append(registrationNumber);
		sb.append("\n").append(indent).append("  comment = ").append(comment);
		sb.append("\n").append(indent).append("  mobilityRestrictedSuitable = ").append(mobilityRestrictedSuitable);
		if (lineEnds != null)
		{
			sb.append("\n").append(indent).append(CHILD_ARROW).append("lineEnds");
			for (String lineEnd : getLineEnds())
			{
				sb.append("\n").append(indent).append(CHILD_LIST_ARROW).append(lineEnd);
			}
		}
		if (routeIds != null)
		{
			sb.append("\n").append(indent).append(CHILD_ARROW).append("routeIds");
			for (String routeid : getRouteIds())
			{
				sb.append("\n").append(indent).append(CHILD_LIST_ARROW).append(routeid);
			}
		}
		if (userNeeds != null)
		{
			sb.append("\n").append(indent).append(CHILD_ARROW).append("userNeeds");
			for (UserNeedEnum userNeed : getUserNeeds())
			{
				sb.append("\n").append(indent).append(CHILD_LIST_ARROW).append(userNeed);
			}
		}

		if (level > 0)
		{
			int childLevel = level -1;
			String childIndent = indent + CHILD_INDENT;
			if (ptNetwork != null) 
			{
				sb.append("\n").append(indent).append(CHILD_ARROW).append(ptNetwork.toString(childIndent,0));
			}
			if (company != null) 
			{
				sb.append("\n").append(indent).append(CHILD_ARROW).append(company.toString(childIndent,0));
			}
			childIndent = indent + CHILD_LIST_INDENT;
			if (routes != null)
			{
				sb.append("\n").append(indent).append(CHILD_ARROW).append("routes");
				for (Route route : getRoutes())
				{
					sb.append("\n").append(indent).append(CHILD_LIST_ARROW).append(route.toString(childIndent,childLevel));
				}
			}
		}

		return sb.toString();
	}

	/**
	 * add a route to the line
	 * 
	 * @param route the route to be added
	 */
	public void addRoute(Route route)
	{
		if (routes == null) routes = new ArrayList<Route>();
		routes.add(route);
	}

	/**
	 * add a routeid to the line
	 * 
	 * @param routeId the routeId to add
	 */
	public void addRouteId(String routeId)
	{
		if (routeIds== null) routeIds = new ArrayList<String>();
		routeIds.add(routeId);
	}

	/**
	 *  add a lienEndid to the line
	 * 
	 * @param lineEndId 
	 */
	public void addLineEnd(String lineEndId)
	{
		if (lineEnds== null) lineEnds = new ArrayList<String>();
		lineEnds.add(lineEndId);
	}

	/**
	 * return lineEndList built with PTLink relationship
	 * <p/>
	 * line must be loaded form database with DetailLevel of 
	 * STRUCTURAL_DEPENDENCIES minimun for this method to operate
	 * <p/>
	 * This method does not refresh lineEnds
	 */
	public List<StopPoint> getLineEndList()
	{
		List<StopPoint> stopPoints = new ArrayList<StopPoint>();
		if (routes != null)
		{
			for (Route route : routes) 
			{
				if (route.getPtLinks() != null)
				{
					Set<String> startStopPoints = new HashSet<String>();
					Set<String> endStopPoints = new HashSet<String>();
					for (PTLink link : route.getPtLinks()) 
					{
						if (link.getStartOfLink() != null)
							startStopPoints.add(link.getStartOfLink().getObjectId());
						if (link.getEndOfLink() != null)
							endStopPoints.add(link.getEndOfLink().getObjectId());
					}
					for (PTLink link : route.getPtLinks()) 
					{
						StopPoint start = link.getStartOfLink();
						if (start != null)
						{
							if (!endStopPoints.contains(start.getObjectId()))
							{
								stopPoints.add(start);
							}
						}
						StopPoint end = link.getStartOfLink();
						if (end != null)
						{
							if (!startStopPoints.contains(end.getObjectId()))
							{
								stopPoints.add(end);
							}
						}
					}
				}
			}
		}
		return stopPoints;
	}

	/**
	 * return stopPointList built with JourneyPattern relationship
	 * <p/>
	 * line must be loaded form database with DetailLevel of 
	 * STRUCTURAL_DEPENDENCIES minimun for this method to operate
	 * <p/>
	 * This method does not refresh anything
	 */
	public List<StopPoint> getStopPointList()
	{
		Set<StopPoint> stopPoints = new HashSet<StopPoint>();
		if (routes != null)
		{
			for (Route route : routes) 
			{
				if (route.getJourneyPatterns() != null)
				{
					for (JourneyPattern jp : route.getJourneyPatterns()) 
					{
						if (jp.getStopPoints() != null)
						{
							stopPoints.addAll(jp.getStopPoints());
						}
					}
				}
			}
		}
		return Arrays.asList(stopPoints.toArray(new StopPoint[0]));
	}

	@Override
	public boolean clean() {
		if(routes == null){
			return false;
		}
		for (Iterator<Route> iterator = routes.iterator(); iterator.hasNext();) {
			Route route = iterator.next();
			if(route == null || !route.clean()){
				iterator.remove();
			}
		}
		if(routes.isEmpty()){
			return false;
		}
		return true;
	}
}
