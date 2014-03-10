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

import fr.certu.chouette.exchange.gtfs.model.GtfsCalendar;
import fr.certu.chouette.plugin.exchange.report.ExchangeReportItem;
import fr.certu.chouette.plugin.report.Report;

/**
 * factory to build calendar from csv line of GTFS calendar.txt file
 * 
 * @author michel
 *
 */
public class GtfsCalendarFactory extends GtfsBeanFactory<GtfsCalendar> 
{

	private static final Logger logger = Logger.getLogger(GtfsCalendarFactory.class);

	@Getter private final String dropSql = "drop table if exists calendar;";
	@Getter private final String createSql = "create table calendar (num, id, monday,tuesday,wednesday,thursday,friday,saturday,sunday,startdate,enddate);";
	@Getter private final String createIndexSql = "create index calendar_id_idx on calendar (id)" ; 
	private final String insertSQL = "insert into calendar (num, id, monday,tuesday,wednesday,thursday,friday,saturday,sunday,startdate,enddate) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";
	@Getter private final String selectSql = "select num, id, monday,tuesday,wednesday,thursday,friday,saturday,sunday,startdate,enddate from calendar ";
	@Getter private final String[] dbHeader = new String[]{"num","service_id","monday","tuesday","wednesday","thursday","friday","saturday","sunday","start_date","end_date"};

	public GtfsCalendarFactory() {
		super(GtfsCalendar.class);
	}
	@Override
	public GtfsCalendar getNewGtfsBean(int lineNumber, String[] csvLine,Report report) {
		GtfsCalendar bean = getNewGtfsBean(GtfsCalendar.class);
		bean.setFileLineNumber(lineNumber);
		bean.setServiceId(getValue("service_id", csvLine));
		bean.setMonday(asBool(getValue("monday", csvLine)));
		bean.setTuesday(asBool(getValue("tuesday", csvLine)));
		bean.setWednesday(asBool(getValue("wednesday", csvLine)));
		bean.setThursday(asBool(getValue("thursday", csvLine)));
		bean.setFriday(asBool(getValue("friday", csvLine)));
		bean.setSaturday(asBool(getValue("saturday", csvLine)));
		bean.setSunday(asBool(getValue("sunday", csvLine)));
		bean.setStartDate(getDateValue("start_date", csvLine,logger));
		bean.setEndDate(getDateValue("end_date", csvLine,logger));
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
				logger.warn("calendar.txt : Line "+lineNumber+" missing required data = "+data);
			}
			return null;
		}
		return bean;
	}

	/**
	 * convert 0/1 value in boolean
	 * 
	 * @param value 0/1 value
	 * @return true if 1, false otherwise
	 */
	private boolean asBool(String value)
	{
		return "1".equals(value);
	}

	private String toString(boolean bool)
	{
		if (bool) return "1";
		return "0";
	}

	@Override
	public void saveAll(Connection conn, List<GtfsCalendar> beans)
	{
		// id, monday,tuesday,wednesday,thursday,friday,saturday,sunday,startdate,enddate
		try
		{
			PreparedStatement prep = conn.prepareStatement(insertSQL);
			for (GtfsCalendar bean : beans)
			{
				setStringOrNull(prep,1, bean.getFileLineNumber());
				setStringOrNull(prep,2, bean.getServiceId());
				setStringOrNull(prep,3, toString(bean.isMonday()));
				setStringOrNull(prep,4, toString(bean.isTuesday()));
				setStringOrNull(prep,5, toString(bean.isWednesday()));
				setStringOrNull(prep,6, toString(bean.isThursday()));
				setStringOrNull(prep,7, toString(bean.isFriday()));
				setStringOrNull(prep,8, toString(bean.isSaturday()));
				setStringOrNull(prep,9, toString(bean.isSunday()));
				setStringOrNull(prep,10, toString(bean.getStartDate()));
				setStringOrNull(prep,11, toString(bean.getEndDate()));
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
	public String getId(GtfsCalendar bean)
	{
		return bean.getServiceId();
	}

	@Override
	public String getParentId(GtfsCalendar bean)
	{
		return null;
	}

	@Override
	protected String getParentId()
	{
		return null;
	}



}
