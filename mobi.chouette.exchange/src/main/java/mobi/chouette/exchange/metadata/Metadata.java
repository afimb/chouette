/**
 * 
 */
package mobi.chouette.exchange.metadata;

import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author michel
 * 
 */
public class Metadata {
	/**
	 * spatial coverage, bounding box including all exported stop areas
	 */
	@Getter
	private Box spatialCoverage = new Box();
	/**
	 * temporal coverage, calendar period covered by exported timetables
	 */
	@Getter
	private Period temporalCoverage = new Period();

	/**
	 * owner of exported data, organization name
	 */
	@Getter
	@Setter
	private String creator;
	/**
	 * export date
	 */
	@Getter
	@Setter
	private Calendar date = Calendar.getInstance();

	/**
	 * content description if resources not available
	 */
	@Getter
	@Setter
	private String description;
	/**
	 * list of exported data, replace description if not empty
	 */
	@Getter
	private List<Resource> resources = new ArrayList<>();

	/**
	 * export files format (MIME Type)
	 */
	@Getter
	@Setter
	private String format;

	/**
	 * language (default = fr_FR.utf8)
	 */
	@Getter
	@Setter
	private String language = "fr_FR.utf8";

	/**
	 * export publisher, user name
	 */
	@Getter
	@Setter
	private String publisher;

	/**
	 * data format specification pointer
	 */
	@Getter
	@Setter
	private URL relation;

	/**
	 * export description
	 */
	@Getter
	@Setter
	private String title;

	/**
	 * data Dublin-Core type; forced to Dataset
	 */
	@Getter
	@Setter
	private String type = "Dataset";

	/**
	 * spatial coverage
	 * 
	 */
	public class Box {
		/**
		 * north limit of data
		 */
		@Getter
		private double northLimit;
		/**
		 * south limit of data
		 */
		@Getter
		private double southLimit;
		/**
		 * east limit of data
		 */
		@Getter
		private double eastLimit;
		/**
		 * west limit of data
		 */
		@Getter
		private double westLimit;

		/**
		 * initialize box with first point
		 * 
		 * @param longitude
		 *            point longitude
		 * @param latitude
		 *            point latitude
		 */
		private Box() {
			northLimit = -90;
			southLimit = 90;
			eastLimit = -180;
			westLimit = 180;
		}

		/**
		 * update box when point out of limits
		 * 
		 * @param longitude
		 *            point longitude
		 * @param latitude
		 *            point latitude
		 */
		public void update(double longitude, double latitude) {
			if (longitude < westLimit)
				westLimit = longitude;
			if (longitude > eastLimit)
				eastLimit = longitude;
			if (latitude < southLimit)
				southLimit = latitude;
			if (latitude > northLimit)
				northLimit = latitude;
		}

		public boolean isSet() {
			return northLimit >= southLimit && eastLimit >= westLimit;
		}

	}

	/**
	 * tempral coverage
	 * 
	 */
	public class Period {
		/**
		 * minimum date covered by data
		 */
		@Getter
		private Calendar start;
		/**
		 * maximum date covered by data
		 */
		@Getter
		private Calendar end;

		private Period() {
		}

		/**
		 * update period if timetable overlaps it
		 * 
		 * @param startDate
		 *            timetable minimal date
		 * @param endDate
		 *            timetable maximal date
		 */
		public void update(final Calendar startDate, final Calendar endDate) {
			if (start == null || startDate.before(start))
				start = (Calendar) startDate.clone();
			if (end == null || endDate.after(end))
				end = (Calendar) endDate.clone();
		}

		/**
		 * update period if timetable overlaps it
		 * 
		 * @param startDate
		 *            timetable minimal date
		 * @param endDate
		 *            timetable maximal date
		 */
		public void update(final Date startDate, final Date endDate) {
			if (startDate != null && endDate != null) {
				Calendar startCal = Calendar.getInstance();
				startCal.setTime(startDate);
				Calendar endCal = Calendar.getInstance();
				endCal.setTime(endDate);
				update(startCal, endCal);
			}
		}

		public void force(Date startDate, Date endDate) {
			if (startDate != null) {
				start = Calendar.getInstance();
				start.setTime(startDate);
			}
			if (endDate != null) {
				end = Calendar.getInstance();
				end.setTime(endDate);
			}
		}

		public boolean isSet() {
			return start != null && end != null;
		}

	}

	/**
	 * exported resource exported
	 * 
	 */
	@AllArgsConstructor
	public class Resource {
		/**
		 * exported resource localization, if resources are in separate files
		 */
		@Getter
		private String fileName;
		/**
		 * Public transport network name
		 */
		@Getter
		private String networkName;
		/**
		 * line name
		 */
		@Getter
		private String lineName;

		/**
		 * build resource without fileName
		 * 
		 * @param networkName
		 *            public transport network name
		 * @param lineName
		 *            line name
		 */
		public Resource(String networkName, String lineName) {
			this.networkName = networkName;
			this.lineName = lineName;
		}
	}
}
