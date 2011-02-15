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
import fr.certu.chouette.model.neptune.type.TransportModeNameEnum;
import fr.certu.chouette.model.neptune.type.UserNeedEnum;

/**
 * 
 */
@NoArgsConstructor
public class Line extends NeptuneIdentifiedObject
{

	@Getter @Setter private Long ptNetworkId;
	@Getter @Setter private Long companyId;
	@Getter @Setter private String number;
	@Getter @Setter private String publishedName;
	@Getter @Setter private TransportModeNameEnum transportModeName;
	@Getter @Setter private String registrationNumber;
	@Getter @Setter private String comment;
	@Getter @Setter private String ptNetworkIdShortcut;
	@Getter @Setter private List<String> routeIds;
	@Getter @Setter private List<String> lineEnds;
	@Getter @Setter private PTNetwork ptNetwork;
	@Getter @Setter private Company company;
	@Getter @Setter private List<Route> routes;
	@Getter @Setter boolean mobilityRestrictedSuitable;
	@Getter @Setter List<UserNeedEnum> userNeeds;

	public void addUserNeed(UserNeedEnum userNeed){
		if(userNeeds == null) userNeeds = new ArrayList<UserNeedEnum>();
		userNeeds.add(userNeed);
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
			if (getPtNetwork() != null) getPtNetwork().expand(level);
			if (getCompany() != null) getCompany().expand(level);
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

	public void addRoute(Route route)
	{
		if (routes == null) routes = new ArrayList<Route>();
		routes.add(route);
	}
	
	public void addRouteId(String routeId)
	{
		if (routeIds== null) routeIds = new ArrayList<String>();
		routeIds.add(routeId);
	}
	
	public void addLineEnd(String lineEndId)
	{
		if (lineEnds== null) lineEnds = new ArrayList<String>();
		lineEnds.add(lineEndId);
	}

}
