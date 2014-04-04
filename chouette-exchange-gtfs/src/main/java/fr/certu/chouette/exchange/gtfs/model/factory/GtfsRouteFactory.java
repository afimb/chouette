/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.exchange.gtfs.model.factory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import lombok.Getter;

import org.apache.log4j.Logger;

import fr.certu.chouette.exchange.gtfs.model.GtfsRoute;
import fr.certu.chouette.plugin.exchange.report.ExchangeReportItem;
import fr.certu.chouette.plugin.report.Report;

/**
 * factory to build route from csv line of GTFS route.txt file
 * 
 * @author michel
 *
 */
public class GtfsRouteFactory extends GtfsBeanFactory<GtfsRoute> 
{

	private static final Logger logger = Logger.getLogger(GtfsRoute.class);
	@Getter private final String dropSql = "drop table if exists route;";
	@Getter private final String createSql = "create table route (num, id, shortname,longname,desc,type,agencyid,url,color,textcolor);";
	@Getter private final String createIndexSql = "create index route_id_idx on route (id)" ; 
	private final String insertSQL = "insert into route (num, id, shortname,longname,desc,type,agencyid,url,color,textcolor) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	@Getter private final String selectSql = "select num, id, shortname,longname,desc,type,agencyid,url,color,textcolor from route ";
	@Getter private final String[] dbHeader = new String[]{"num", "route_id","route_short_name","route_long_name","route_desc","route_type","agency_id","route_url","route_color","route_text_color"};

	public GtfsRouteFactory() {
		super(GtfsRoute.class);
	}
	@Override
	public GtfsRoute getNewGtfsBean(int lineNumber, String[] csvLine,Report report) {
		GtfsRoute bean = getNewGtfsBean(GtfsRoute.class);
		bean.setFileLineNumber(lineNumber);
		bean.setRouteId(getValue("route_id", csvLine));
		String agencyId = getValue("agency_id", csvLine);
		if (agencyId == null) agencyId = "default";
		bean.setAgencyId(agencyId);
		bean.setRouteShortName(getValue("route_short_name", csvLine));
		bean.setRouteLongName(getValue("route_long_name", csvLine));
		bean.setRouteDesc(getValue("route_desc", csvLine));
		bean.setRouteType(getIntValue("route_type", csvLine,-1));
		bean.setRouteURL(getUrlValue("route_url", csvLine,logger));
		bean.setRouteColor(getColorValue("route_color", csvLine));
		bean.setRouteTextColor(getColorValue("route_text_color", csvLine));
		// check mandatory values
		if (!bean.isValid())		
		{
			String data = bean.getMissingData().toString();
			if (report != null)
			{
				ExchangeReportItem item = new ExchangeReportItem(ExchangeReportItem.KEY.MANDATORY_DATA,Report.STATE.WARNING,lineNumber,data);
				report.addItem(item);
			}
			else
			{
				logger.warn("routes.txt : Line "+lineNumber+" missing required data = "+data);
			}
			return null;
		}
		return bean;
	}
	@Override
	public void saveAll(Connection conn, List<GtfsRoute> beans)
	{
		// id, shortname,longname,desc,type,agencyid,url,color,textcolor
		try
		{
			PreparedStatement prep = conn.prepareStatement(insertSQL);
			for (GtfsRoute bean : beans)
			{
				setStringOrNull(prep,1, bean.getFileLineNumber());
				setStringOrNull(prep,2, bean.getRouteId());
				setStringOrNull(prep,3, bean.getRouteShortName());
				setStringOrNull(prep,4, bean.getRouteLongName());
				setStringOrNull(prep,5, bean.getRouteDesc());
				setStringOrNull(prep,6, Integer.toString(bean.getRouteType()));
				setStringOrNull(prep,7, bean.getAgencyId());
				setStringOrNull(prep,8, bean.getRouteURL());
				setStringOrNull(prep,9, toString(bean.getRouteColor()));
				setStringOrNull(prep,10, toString(bean.getRouteTextColor()));
				prep.addBatch();
			}

			prep.executeBatch();
			conn.commit();
		}
		catch (SQLException e)
		{
			logger.error("cannot save gtfs data",e);
			throw new RuntimeException(e.getMessage());
		}

	}

	@Override
	public String getId(GtfsRoute bean)
	{
		return bean.getRouteId();
	}

	@Override
	public String getParentId(GtfsRoute bean)
	{
		return null;
	}

	@Override
	protected String getParentId()
	{
		return null;
	}


}
