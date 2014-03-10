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

import fr.certu.chouette.exchange.gtfs.model.GtfsStopTime;
import fr.certu.chouette.plugin.exchange.report.ExchangeReportItem;
import fr.certu.chouette.plugin.report.Report;

/**
 * factory to build stoptime from csv line of GTFS stoptime.txt file
 * 
 * @author michel
 *
 */
public class GtfsStopTimeFactory extends GtfsBeanFactory<GtfsStopTime> 
{

	private static final Logger logger = Logger.getLogger(GtfsStopTimeFactory.class);
	@Getter private final String dropSql = "drop table if exists stoptime;";
	@Getter private final String createSql = "create table stoptime (num, tripid, arrivaltime,departuretime,stopid,stopsequence,stopheadsign,pickuptype,dropoftype,shapedisttraveled);";
	@Getter private final String createParentIndexSql = "create index stoptime_tripid_idx on stoptime (tripid)" ; 
	private final String insertSQL = "insert into stoptime (num, tripid, arrivaltime,departuretime,stopid,stopsequence,stopheadsign,pickuptype,dropoftype,shapedisttraveled) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	@Getter private final String selectSql = "select num, tripid, arrivaltime,departuretime,stopid,stopsequence,stopheadsign,pickuptype,dropoftype,shapedisttraveled from stoptime ";
	@Getter private final String[] dbHeader = new String[]{"num", "trip_id","arrival_time","departure_time","stop_id","stop_sequence","stop_headsign","pickup_type","drop_off_type","shape_dist_traveled"};
	public GtfsStopTimeFactory() {
		super(GtfsStopTime.class);
	}
	@Override
	public GtfsStopTime getNewGtfsBean(int lineNumber, String[] csvLine,Report report) 
	{
		GtfsStopTime bean = getNewGtfsBean(GtfsStopTime.class);
		bean.setFileLineNumber(lineNumber);
		bean.setTripId(getValue("trip_id", csvLine));
		bean.setArrivalTime(getTimeValue("arrival_time", csvLine));
		bean.setDepartureTime(getTimeValue("departure_time", csvLine));
		bean.setStopId(getValue("stop_id", csvLine));
		bean.setStopSequence(getIntValue("stop_sequence", csvLine,-1));
		bean.setStopHeadsign(getValue("stop_headsign", csvLine));
		bean.setPickupType(getIntValue("pickup_type", csvLine,0));
		bean.setDropOffType(getIntValue("drop_off_type", csvLine,0));
		bean.setShapeDistTraveled(getDoubleValue("shape_dist_traveled", csvLine,(double)0.0));
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
				logger.warn("stop_times.txt : Line "+lineNumber+" missing required data = "+data);
			}
			return null;
		}
		return bean;
	}
	@Override
	public void saveAll(Connection conn, List<GtfsStopTime> beans)
	{
		// tripid, arrivaltime,departuretime,stopid,stopsequence,stopheadsign,pickuptype,dropoftype,shapedisttraveled
		try
		{
			PreparedStatement prep = conn.prepareStatement(insertSQL);
			for (GtfsStopTime bean : beans)
			{
				setStringOrNull(prep,1, bean.getFileLineNumber());
				setStringOrNull(prep,2, bean.getTripId());
				setStringOrNull(prep,3, bean.getArrivalTime());
				setStringOrNull(prep,4, bean.getDepartureTime());
				setStringOrNull(prep,5, bean.getStopId());
				setStringOrNull(prep,6, Integer.toString(bean.getStopSequence()));
				setStringOrNull(prep,7, bean.getStopHeadsign());
				setStringOrNull(prep,8, Integer.toString(bean.getPickupType()));
				setStringOrNull(prep,9, Integer.toString(bean.getDropOffType()));
				setStringOrNull(prep,10, Double.toString(bean.getShapeDistTraveled()));
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
	public String getId(GtfsStopTime bean)
	{
		return null;
	}

	@Override
	public String getParentId(GtfsStopTime bean)
	{
		return bean.getTripId();
	}

	@Override
	protected String getParentId()
	{
		// TODO Auto-generated method stub
		return "tripid";
	}


}
