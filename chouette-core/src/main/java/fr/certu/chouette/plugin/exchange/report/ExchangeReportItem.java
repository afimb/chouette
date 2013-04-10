/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.plugin.exchange.report;

import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.ReportItem;


/**
 * @author michel
 *
 */
public class ExchangeReportItem extends ReportItem 
{
	public enum KEY {
		ZIP_FILE,
		ZIP_ERROR,
		FILE,
		FILE_ERROR,
		FILE_IGNORED,
		VALIDATION_ERROR,
		VALIDATION_CAUSE, 
		IMPORTED_LINE,
		LINE_COUNT,
		TIME_TABLE_COUNT,
		ROUTE_COUNT,
		JOURNEY_PATTERN_COUNT,
		VEHICLE_JOURNEY_COUNT,
		STOP_AREA_COUNT,
		CONNECTION_LINK_COUNT,
		ACCES_POINT_COUNT,
		MANDATORY_TAG, 
		UNKNOWN_ENUM, 
		EMPTY_TAG,
		BAD_REFERENCE,
		EXPORTED_LINE, 
		EMPTY_LINE,
		EMPTY_ROUTE,
		EMPTY_JOURNEY_PATTERN,
		EMPTY_VEHICLE_JOURNEY,
		EMPTY_TIMETABLE
	} ;

	
	public ExchangeReportItem(KEY key,Report.STATE status, Object... args)
	{
		setStatus(status);
        setMessageKey(key.name());
        addMessageArgs(args);
	}
	
	@Override
	public void addItem(ReportItem item) 
	{
		super.addItem(item);
		updateStatus(item.getStatus());
	}
	
	
}
