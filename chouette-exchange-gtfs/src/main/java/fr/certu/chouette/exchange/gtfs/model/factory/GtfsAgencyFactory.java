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

import fr.certu.chouette.exchange.gtfs.model.GtfsAgency;
import fr.certu.chouette.plugin.exchange.report.ExchangeReportItem;
import fr.certu.chouette.plugin.report.Report;

/**
 * factory to build agency from csv line of GTFS agency.txt file
 * 
 * @author michel
 *
 */
public class GtfsAgencyFactory extends GtfsBeanFactory<GtfsAgency> 
{

	private static final Logger logger = Logger.getLogger(GtfsAgencyFactory.class);


	@Getter private final String dropSql = "drop table if exists agency;";
	@Getter private final String createSql = "create table agency (num, id, name,url,timezone,lang,phone);";
	private final String insertSQL = "insert into agency (num, id, name,url,timezone,lang,phone) values (?, ?, ?, ?, ?, ?, ?)";
	@Getter private final String selectSql = "select num, id, name,url,timezone,lang,phone from agency ";
	@Getter private final String createIndexSql = "create index agency_id_idx on agency (id)" ; 
	@Getter private final String[] dbHeader = new String[]{"num","agency_id","agency_name","agency_url","agency_timezone","agency_lang","agency_phone"};
	public GtfsAgencyFactory() {
		super(GtfsAgency.class);
	}
	/* (non-Javadoc)
	 * @see fr.certu.chouette.exchange.gtfs.model.factory.GtfsBeanFactory#getNewGtfsBean(int, java.lang.String[])
	 */
	@Override
	public GtfsAgency getNewGtfsBean(int lineNumber, String[] csvLine,Report report) 
	{
		GtfsAgency bean = getNewGtfsBean(GtfsAgency.class);
		
		bean.setFileLineNumber(lineNumber);
		String agencyId = getValue("agency_id", csvLine);
		if (agencyId == null || agencyId.isEmpty()) agencyId = "default";
		bean.setAgencyId(agencyId);

		bean.setAgencyName(getValue("agency_name", csvLine));
		bean.setAgencyURL(getUrlValue("agency_url", csvLine,logger));
		bean.setAgencyTimezone(getTimeZoneValue("agency_timezone", csvLine));
		bean.setAgencyLang(getValue("agency_lang", csvLine));
		bean.setAgencyPhone(getValue("agency_phone", csvLine));
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
				logger.warn("agency.txt : Line "+lineNumber+" missing required data = "+data);
			}
			return null;
		}
		return bean;
	}

	@Override
	public void saveAll(Connection conn, List<GtfsAgency> beans)
	{ // id, name,url,timezone,lang,phone
		try
		{
			PreparedStatement prep = conn.prepareStatement(insertSQL);
			for (GtfsAgency bean : beans)
			{
				setStringOrNull(prep,1, bean.getFileLineNumber());
				setStringOrNull(prep,2, bean.getAgencyId());
				setStringOrNull(prep,3, bean.getAgencyName());
				setStringOrNull(prep,4, bean.getAgencyURL().toString());
				if (bean.getAgencyTimezone() != null)
					setStringOrNull(prep,5, bean.getAgencyTimezone().getID());
				else
					setStringOrNull(prep,5, null);
				setStringOrNull(prep,6, bean.getAgencyLang());
				setStringOrNull(prep,7, bean.getAgencyPhone());
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
	public String getId(GtfsAgency bean)
	{
		return bean.getAgencyId();
	}

	@Override
	public String getParentId(GtfsAgency bean)
	{
		return null;
	}

	@Override
	protected String getParentId()
	{
		return null;
	}



}
