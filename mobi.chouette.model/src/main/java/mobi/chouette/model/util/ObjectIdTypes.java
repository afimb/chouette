package mobi.chouette.model.util;

public interface ObjectIdTypes {
	// constants for ObjectId prefixes
	   /**
	    * default objectId type for access points
	    */
	   public static final String ACCESSPOINT_KEY = "AccessPoint";
	   /**
	    * default objectId type for access links
	    */
	   public static final String ACCESSLINK_KEY = "AccessLink";
	   /**
	    * default objectId type for area centroids (deprecated in next release)
	    */
	   public static final String AREACENTROID_KEY = "AreaCentroid";
	   /**
	    * default objectId type for companies
	    */
	   public static final String COMPANY_KEY = "Company";
	   /**
	    * default objectId type for connection links
	    */
	   public static final String CONNECTIONLINK_KEY = "ConnectionLink";
	   /**
	    * default objectId type for facilities
	    */
	   public static final String FACILITY_KEY = "Facility";
	   /**
	    * default objectId type for group of lines
	    */
	   public static final String GROUPOFLINE_KEY = "GroupOfLine";
	   /**
	    * default objectId type for journey patterns
	    */
	   public static final String JOURNEYPATTERN_KEY = "JourneyPattern";
	   /**
	    * default objectId type for lines
	    */
	   public static final String LINE_KEY = "Line";
	   /**
	    * default objectId type for ptlinks  (deprecated in next release)
	    */
	   public static final String PTLINK_KEY = "PtLink";
	   /**
	    * default objectId type for Networks
	    */
	   public static final String PTNETWORK_KEY = "Network";
	   /**
	    * default objectId type for route
	    */
	   public static final String ROUTE_SECTION_KEY = "RouteSection";
	   /**
	    * default objectId type for route
	    */
	   public static final String ROUTE_KEY = "Route";
	   /**
	    * default objectId type for stop areas
	    */
	   public static final String STOPAREA_KEY = "StopArea";
	   /**
	    * default objectId type for stop points
	    */
	   public static final String STOPPOINT_KEY = "StopPoint";
		/**
	 	* default objectId type for zcheduled stop points
	 	*/
		public static final String SCHEDULED_STOP_POINT_KEY = "ScheduledStopPoint";
	   /**
	    * default objectId type for time slots
	    */
	   public static final String TIMESLOT_KEY = "TimeSlot";
	   /**
	    * default objectId type for timetables
	    */
	   public static final String TIMETABLE_KEY = "Timetable";
	   /**
	    * default objectId type for timebands
	    */
	   public static final String TIMEBAND_KEY = "Timeband";
	   /**
	    * default objectId type for service journeys
	    */
	   public static final String SERVICEJOURNEY_KEY = "ServiceJourney";

		/**
		 * default objectId type for service journeys
		 */
		public static final String DEADRUN_KEY = "ServiceJourney";

	   /**
	    * default objectId type for vehicle journeys 
	    */
	   public static final String DESTINATIONDISPLAY_KEY = "DestinationDisplay";
	   /**
	    * default objectId type for interchanges
	    */
	   public static final String INTERCHANGE_KEY = "ServiceInterchange";
	   /**
	    * default objectId type for notices/footnotes
	    */
	   public static final String FOOTNOTE_KEY = "Notice";

	   /**
	    * default objectId type for timetabled passing times / vehicle journey at stop
	    */
	   public static final String VEHICLE_JOURNEY_AT_STOP_KEY = "TimetabledPassingTime";
	   
	   /**
	    * default objectId type for authorities
	    */
	   public static final String AUTHORITY_KEY = "Authority";

	   /**
	    * default objectId type for operators
	    */
	   public static final String OPERATOR_KEY = "Operator";

}
