package mobi.chouette.exchange.importer.updater;

import javax.ejb.Local;

import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;

@Local
public interface Updater<T> extends Constant {
	// test keys
	public static final String DATABASE_LINE_1 = "2-DATABASE-Line-1";
	public static final String DATABASE_LINE_2 = "2-DATABASE-Line-2";
	public static final String DATABASE_ROUTE_1 = "2-DATABASE-Route-1";
	public static final String DATABASE_JOURNEY_PATTERN_1 = "2-DATABASE-JourneyPattern-1";
	public static final String DATABASE_VEHICLE_JOURNEY_1 = "2-DATABASE-VehicleJourney-1";
	public static final String DATABASE_VEHICLE_JOURNEY_2 = "2-DATABASE-VehicleJourney-2";
	public static final String DATABASE_STOP_POINT_1 = "2-DATABASE-StopPoint-1";
	public static final String DATABASE_STOP_POINT_2 = "2-DATABASE-StopPoint-2";
	public static final String DATABASE_STOP_POINT_3 = "2-DATABASE-StopPoint-3";
	public static final String DATABASE_STOP_AREA_1 = "2-DATABASE-StopArea-1";
	public static final String DATABASE_STOP_AREA_2 = "2-DATABASE-StopArea-2";
	public static final String DATABASE_ACCESS_POINT_1 = "2-DATABASE-AccessPoint-1";
	public static final String DATABASE_CONNECTION_LINK_1_1 = "2-DATABASE-ConnectionLink-1-1";
	public static final String DATABASE_CONNECTION_LINK_1_2 = "2-DATABASE-ConnectionLink-1-2";
	
	void update(Context context, T oldValue, T newValue) throws Exception;

}
