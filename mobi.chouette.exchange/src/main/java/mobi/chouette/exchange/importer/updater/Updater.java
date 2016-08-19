package mobi.chouette.exchange.importer.updater;

import javax.ejb.Local;

import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;

@Local
public interface Updater<T> extends Constant {
	// test keys
	public static final String DATABASE_LINE_1 = "2-Line-1";
	public static final String DATABASE_LINE_2 = "2-Line-2";
	public static final String DATABASE_ROUTE_1 = "2-Route-1";
	public static final String DATABASE_JOURNEY_PATTERN_1 = "2-JourneyPattern-1";
	public static final String DATABASE_VEHICLE_JOURNEY_1 = "2-VehicleJourney-1";
	public static final String DATABASE_VEHICLE_JOURNEY_2 = "2-VehicleJourney-2";
	public static final String DATABASE_STOP_POINT_1 = "2-StopPoint-1";
	public static final String DATABASE_STOP_POINT_2 = "2-StopPoint-2";
	public static final String DATABASE_STOP_POINT_3 = "2-StopPoint-3";
	public static final String DATABASE_STOP_AREA_1 = "2-StopArea-1";
	public static final String DATABASE_STOP_AREA_2 = "2-StopArea-2";
	public static final String DATABASE_ACCESS_POINT_1 = "2-AccessPoint-1";
	public static final String DATABASE_CONNECTION_LINK_1 = "2-ConnectionLink-1";
	
	void update(Context context, T oldValue, T newValue) throws Exception;

}
