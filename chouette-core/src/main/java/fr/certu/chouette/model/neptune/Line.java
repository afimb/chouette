/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */
package fr.certu.chouette.model.neptune;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import fr.certu.chouette.filter.DetailLevelEnum;
import fr.certu.chouette.model.neptune.type.TransportModeNameEnum;

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
		String s = super.toString(indent,level);
		s += "\n"+indent+"  ptNetworkId = "+ptNetworkId;
		s += "\n"+indent+"  ptNetworkIdShortcut = "+ptNetworkIdShortcut;
		s += "\n"+indent+"  companyId = "+companyId;
		s += "\n"+indent+"  number = "+number;
		s += "\n"+indent+"  publishedName = "+publishedName;
		s += "\n"+indent+"  transportModeName = "+transportModeName;
		s += "\n"+indent+"  registrationNumber = "+registrationNumber;
		s += "\n"+indent+"  comment = "+comment;
		if (lineEnds != null)
		{
			s+= "\n"+indent+CHILD_ARROW+"lineEnds";
			for (String lineEnd : getLineEnds())
			{
				s+= "\n"+indent+CHILD_LIST_ARROW+lineEnd;
			}
		}
		if (routeIds != null)
		{
			s+= "\n"+indent+CHILD_ARROW+"routeIds";
			for (String routeid : getRouteIds())
			{
				s+= "\n"+indent+CHILD_LIST_ARROW+routeid;
			}
		}

		if (level > 0)
		{
			int childLevel = level -1;
			String childIndent = indent + CHILD_INDENT;
			if (ptNetwork != null) 
			{
				s+= "\n"+indent+CHILD_ARROW+ptNetwork.toString(childIndent,0);
			}
			if (company != null) 
			{
				s+= "\n"+indent+CHILD_ARROW+company.toString(childIndent,0);
			}
			childIndent = indent + CHILD_LIST_INDENT;
			if (routes != null)
			{
				s+= "\n"+indent+CHILD_ARROW+"routes";
				for (Route route : getRoutes())
				{
					s+= "\n"+indent+CHILD_LIST_ARROW+route.toString(childIndent,childLevel);
				}
			}
		}

		return s;
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
