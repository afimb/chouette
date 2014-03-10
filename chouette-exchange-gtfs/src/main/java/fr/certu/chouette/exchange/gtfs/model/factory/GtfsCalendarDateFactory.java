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

import fr.certu.chouette.exchange.gtfs.model.GtfsCalendarDate;
import fr.certu.chouette.plugin.exchange.report.ExchangeReportItem;
import fr.certu.chouette.plugin.report.Report;

/**
 * factory to build calendar_date from csv line of GTFS calendar_date.txt file
 * 
 * @author michel
 *
 */
public class GtfsCalendarDateFactory extends GtfsBeanFactory<GtfsCalendarDate> 
{

	private static final Logger logger = Logger.getLogger(GtfsCalendarDateFactory.class);

	@Getter private final String dropSql = "drop table if exists calendardate;";
	@Getter private final String createSql = "create table calendardate (num, id, date,mode);";
	@Getter private final String createParentIndexSql = "create index calendardate_id_idx on calendardate (id)" ; 
	private final String insertSQL = "insert into calendardate (num, id, date,mode) values (?, ?, ?, ?)";
	@Getter private final String selectSql = "select num, id, date,mode from calendardate ";
	@Getter private final String[] dbHeader = new String[]{"num","service_id","date","exception_type"};

	public GtfsCalendarDateFactory() {
		super(GtfsCalendarDate.class);
	}

	@Override
	public GtfsCalendarDate getNewGtfsBean(int lineNumber, String[] csvLine,Report report) 
	{
		GtfsCalendarDate bean = getNewGtfsBean(GtfsCalendarDate.class);
		bean.setFileLineNumber(lineNumber);
		bean.setServiceId(getValue("service_id", csvLine));
		bean.setDate(getDateValue("date", csvLine,logger));
		bean.setExceptionType(getIntValue("exception_type", csvLine,0));
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
				logger.warn("calendar_dates.txt : Line "+lineNumber+" missing required data = "+data);
			}
			return null;
		}
		return bean;
	}


	@Override
	protected String getId()
	{
		return null;
	}

	@Override
	protected String getParentId()
	{
		return "id";
	}
	@Override
	public void saveAll(Connection conn, List<GtfsCalendarDate> beans)
	{ 
		// id, date,mode
		try
		{
			PreparedStatement prep = conn.prepareStatement(insertSQL);
			for (GtfsCalendarDate bean : beans)
			{
				setStringOrNull(prep,1, bean.getFileLineNumber());
				setStringOrNull(prep,2, bean.getServiceId());
				setStringOrNull(prep,3, toString(bean.getDate()));
				setStringOrNull(prep,4, Integer.toString(bean.getExceptionType()));
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
	public String getId(GtfsCalendarDate bean)
	{
		return null;
	}

	@Override
	public String getParentId(GtfsCalendarDate bean)
	{
		return bean.getServiceId();
	}
}
