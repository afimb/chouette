package mobi.chouette.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import mobi.chouette.model.type.ServiceStatusValueEnum;
import mobi.chouette.model.type.TransportModeNameEnum;

import org.apache.commons.lang.StringUtils;

/**
 * Chouette VehicleJourney
 * <p/>
 * <b>Note</b> VehicleJourney class contains method to manipulate
 * VehicleJourneyAtStop in logic with StopPoint's position on Route and
 * StopPoint list in JourneyPatterns <br/>
 * it is mandatory to respect instruction on each of these methods
 * <p/>
 * Neptune mapping : VehicleJourney <br/>
 * Gtfs mapping : trip <br/>
 */

@Entity
@Table(name = "vehicle_journeys")
@NoArgsConstructor
@Log4j
public class VehicleJourney extends NeptuneIdentifiedObject {
	private static final long serialVersionUID = 304336286208135064L;

	/**
	 * comment
	 * 
	 * @return The actual value
	 */
	@Getter
	@Column(name = "comment")
	private String comment;

	/**
	 * set comment <br/>
	 * truncated to 255 characters if too long
	 * 
	 * @param value
	 *            New value
	 */
	public void setComment(String value) {
		comment = StringUtils.abbreviate(value, 255);
	}

	/**
	 * service status
	 * 
	 * @param serviceStatusValue
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	@Column(name = "status_value")
	@Deprecated
	private ServiceStatusValueEnum serviceStatusValue;

	/**
	 * Transport mode when different from line transport mode
	 * 
	 * @param transportMode
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	@Column(name = "transport_mode")
	private TransportModeNameEnum transportMode;

	/**
	 * published journey name
	 * 
	 * @return The actual value
	 */
	@Getter
	@Column(name = "published_journey_name")
	private String publishedJourneyName;

	/**
	 * set published journey name <br/>
	 * truncated to 255 characters if too long
	 * 
	 * @param value
	 *            New value
	 */
	public void setPublishedJourneyName(String value) {
		publishedJourneyName = StringUtils.abbreviate(value, 255);

	}

	/**
	 * published journey identifier
	 * 
	 * @return The actual value
	 */
	@Getter
	@Column(name = "published_journey_identifier")
	private String publishedJourneyIdentifier;

	/**
	 * set published journey identifier <br/>
	 * truncated to 255 characters if too long
	 * 
	 * @param value
	 *            New value
	 */
	public void setPublishedJourneyIdentifier(String value) {
		publishedJourneyIdentifier = StringUtils.abbreviate(value, 255);

	}

	/**
	 * facility
	 * 
	 * @return The actual value
	 */
	@Getter
	@Column(name = "facility")
	private String facility;

	/**
	 * set facility <br/>
	 * truncated to 255 characters if too long
	 * 
	 * @param value
	 *            New value
	 */
	public void setFacility(String value) {
		facility = StringUtils.abbreviate(value, 255);
	}

	/**
	 * vehicle type identifier
	 * 
	 * @return The actual value
	 */
	@Getter
	@Column(name = "vehicle_type_identifier")
	private String vehicleTypeIdentifier;

	/**
	 * set vehicle type identifier <br/>
	 * truncated to 255 characters if too long
	 * 
	 * @param value
	 *            New value
	 */
	public void setVehicleTypeIdentifier(String value) {
		vehicleTypeIdentifier = StringUtils.abbreviate(value, 255);

	}

	/**
	 * number
	 * 
	 * @param number
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@Column(name = "number")
	private Long number;

	/**
	 * mobility restriction indicator (such as wheel chairs) <br/>
	 * 
	 * <ul>
	 * <li>null if unknown
	 * <li>true if wheel chairs can use this line</li>
	 * <li>false if wheel chairs can't use this line</li>
	 * </ul>
	 * 
	 * @param mobilityRestrictedSuitability
	 *            New state for mobility restriction indicator
	 * @return The actual mobility restriction indicator
	 */
	@Getter
	@Setter
	@Column(name = "mobility_restricted_suitability")
	private Boolean mobilityRestrictedSuitability;

	/**
	 * flexible service <br/>
	 * 
	 * <ul>
	 * <li>null if unknown or inherited from line
	 * <li>true for flexible service</li>
	 * <li>false for regular service</li>
	 * </ul>
	 * 
	 * @param flexibleService
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@Column(name = "flexible_service")
	private Boolean flexibleService;

	/**
	 * route reference
	 * 
	 * @param route
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "route_id")
	private Route route;

	/**
	 * journey pattern reference
	 * 
	 * @param journeyPattern
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "journey_pattern_id")
	private JourneyPattern journeyPattern;

	/**
	 * set journey pattern reference
	 * 
	 * @param journeyPattern
	 */
	public void setJourneyPattern(JourneyPattern journeyPattern) {
		if (this.journeyPattern != null) {
			this.journeyPattern.getVehicleJourneys().remove(this);
		}
		this.journeyPattern = journeyPattern;
		if (journeyPattern != null) {
			journeyPattern.getVehicleJourneys().add(this);
		}
	}

	/**
	 * company reference<br/>
	 * if different from line company
	 * 
	 * @param company
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "company_id")
	private Company company;

	/**
	 * timtables
	 * 
	 * @param timetables
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@ManyToMany
	@JoinTable(name = "time_tables_vehicle_journeys", joinColumns = { @JoinColumn(name = "vehicle_journey_id", nullable = false, updatable = false) }, inverseJoinColumns = { @JoinColumn(name = "time_table_id", nullable = false, updatable = false) })
	private List<Timetable> timetables = new ArrayList<Timetable>(0);

	/**
	 * vehicle journey at stops : passing times
	 * 
	 * @return The actual value
	 */
	@Getter
	@OneToMany(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST,
			CascadeType.MERGE })
	@JoinColumn(name = "vehicle_journey_id", updatable = false)
	private List<VehicleJourneyAtStop> vehicleJourneyAtStops = new ArrayList<VehicleJourneyAtStop>(
			0);

	/**
	 * add a timeTable if not already presents
	 * 
	 * @param timetable
	 *            to add
	 */
	public void addTimetable(Timetable timetable) {
		if (!getTimetables().contains(timetable)) {
			getTimetables().add(timetable);
		}
		if (!timetable.getVehicleJourneys().contains(timetable)) {
			timetable.getVehicleJourneys().add(this);
		}

	}

	/**
	 * remove a timetable if present
	 * 
	 * @param timetable
	 */
	public void removeTimetable(Timetable timetable) {
		getTimetables().remove(timetable);
		timetable.getVehicleJourneys().remove(this);
	}

}
