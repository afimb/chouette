/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package mobi.chouette.exchange.gtfs.exporter.producer;

import java.util.Collection;
import java.util.TimeZone;

import mobi.chouette.exchange.gtfs.model.GtfsStop;
import mobi.chouette.exchange.gtfs.model.GtfsStop.WheelchairBoardingType;
import mobi.chouette.exchange.gtfs.model.exporter.GtfsExporterInterface;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.type.ChouetteAreaEnum;

/**
 * convert Timetable to Gtfs Calendar and CalendarDate
 * <p>
 * optimise multiple period timetable with calendarDate inclusion or exclusion
 */
public class GtfsStopProducer extends AbstractProducer
{
	GtfsStop stop = new GtfsStop();


	public GtfsStopProducer(GtfsExporterInterface exporter)
	{
		super(exporter);
	}

	public boolean save(StopArea neptuneObject, String prefix, Collection<StopArea> validParents, boolean keepOriginalId)
	{
		ChouetteAreaEnum chouetteAreaType = neptuneObject.getAreaType();
		if (chouetteAreaType.compareTo(ChouetteAreaEnum.BoardingPosition) == 0)
			stop.setLocationType(GtfsStop.LocationType.Stop);
		else if (chouetteAreaType.compareTo(ChouetteAreaEnum.Quay) == 0)
			stop.setLocationType(GtfsStop.LocationType.Stop);
		else if (chouetteAreaType.compareTo(ChouetteAreaEnum.CommercialStopPoint) == 0)
			stop.setLocationType(GtfsStop.LocationType.Station);
		// else if(chouetteAreaType.compareTo(ChouetteAreaEnum.STOPPLACE) == 0)
		// stop.setLocationType(GtfsStop.STATION);
		else
			return false; // StopPlaces and ITL type not available
		stop.setStopId(toGtfsId(neptuneObject.getObjectId(),prefix, keepOriginalId));
		if (neptuneObject.getName() == null)
		{
			//         GtfsReportItem item = new GtfsReportItem(
			//               GtfsReportItem.KEY.MISSING_DATA, STATE.ERROR, "StopArea",
			//               neptuneObject.getObjectId(), "Name");
			//         report.addItem(item);
			return false;
		}
		stop.setStopName(neptuneObject.getName());

		if (neptuneObject.getLatitude() == null)
		{
			//         GtfsReportItem item = new GtfsReportItem(
			//               GtfsReportItem.KEY.MISSING_DATA, STATE.ERROR, "StopArea",
			//               neptuneObject.getName(), "Latitude");
			//         report.addItem(item);
			return false;
		}
		stop.setStopLat(neptuneObject.getLatitude());
		if (neptuneObject.getLongitude() == null)
		{
			//         GtfsReportItem item = new GtfsReportItem(
			//               GtfsReportItem.KEY.MISSING_DATA, STATE.ERROR, "StopArea",
			//               neptuneObject.getName(), "Longitude");
			//         report.addItem(item);
			return false;
		}
		stop.setStopLon(neptuneObject.getLongitude());
		stop.setStopCode(neptuneObject.getRegistrationNumber());
		
		// name and description must be different
		if (neptuneObject.getName().equals(neptuneObject.getComment()))
		{
			stop.setStopDesc(null);
		}
		else
		{
		    stop.setStopDesc(neptuneObject.getComment());
		}
		stop.setStopUrl(getUrl(neptuneObject.getUrl()));
		// manage stop_timezone
		stop.setStopTimezone(null);
		if (!isEmpty(neptuneObject.getTimeZone()))
		{
			TimeZone tz = TimeZone.getTimeZone(neptuneObject.getTimeZone());
			if (tz != null)
			{
				stop.setStopTimezone(tz);
			}
		}

		stop.setParentStation(null);
		if (stop.getLocationType().equals(GtfsStop.LocationType.Stop))
		{
			if (neptuneObject.getParent() != null && validParents.contains(neptuneObject.getParent()))
			{
				stop.setParentStation(toGtfsId(neptuneObject.getParent()
						.getObjectId(),prefix, keepOriginalId));
			}
		}

		if (neptuneObject.getMobilityRestrictedSuitable() != null)
		{
			if (neptuneObject.getMobilityRestrictedSuitable())
			{
				stop.setWheelchairBoarding(WheelchairBoardingType.Allowed);
			}
			else
			{
				stop.setWheelchairBoarding(WheelchairBoardingType.NoAllowed);
			}
		}
		else
		{
			stop.setWheelchairBoarding(null);
		}

		try
		{
			getExporter().getStopExporter().export(stop);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
