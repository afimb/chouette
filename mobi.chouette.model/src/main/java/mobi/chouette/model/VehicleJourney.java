package mobi.chouette.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.log4j.Log4j;
import mobi.chouette.model.type.JourneyCategoryEnum;
import mobi.chouette.model.type.PublicationEnum;
import mobi.chouette.model.type.ServiceAlterationEnum;
import mobi.chouette.model.type.TransportModeNameEnum;
import mobi.chouette.model.type.TransportSubModeNameEnum;
import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.joda.time.LocalDate;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

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
@ToString(callSuper = true, exclude = { "journeyPattern", "route", "timetables", "consumerInterchanges", "feederInterchanges" })
@Log4j
public class VehicleJourney extends NeptuneIdentifiedObject {

	private static final long serialVersionUID = 304336286208135064L;

	@Getter
	@Setter
	@GenericGenerator(name = "vehicle_journeys_id_seq", strategy = "mobi.chouette.persistence.hibernate.ChouetteIdentifierGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "vehicle_journeys_id_seq"),
			@Parameter(name = "increment_size", value = "100") })
	@GeneratedValue(generator = "vehicle_journeys_id_seq")
	@Id
	@Column(name = "id", nullable = false)
	protected Long id;

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
	 * Transport mode when different from line transport mode
	 *
	 * @param transportMode
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	@Column(name = "publication")
	private PublicationEnum publication;

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
	 * Transport sub mode 
	 * 
	 */
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	@Column(name = "transport_submode_name")
	private TransportSubModeNameEnum transportSubMode = null;

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
	 * Identification of journey, not intended for the public.
	 *
	 * @param privateCode
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@Column(name = "private_code")
	private String privateCode;

	/**
	 * set private code <br/>
	 * truncated to 255 characters if too long
	 *
	 * @param value
	 *            New value
	 */
	public void setPrivateCode(String value) {
		privateCode = StringUtils.abbreviate(value, 255);

	}

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
	 * Type of Service alteration. Default is planned.
	 *
	 * @param serviceAlteration
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	@Column(name = "service_alteration")
	private ServiceAlterationEnum serviceAlteration;

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
	 * datedServiceJourneys
	 *
	 * @param da
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@OneToMany(mappedBy = "vehicleJourney", cascade = { CascadeType.PERSIST})
	private List<DatedServiceJourney> datedServiceJourneys = new ArrayList<DatedServiceJourney>(
			0);

	/**
	 * Blocks referencing this .
	 */

	@Getter
	@ManyToMany(mappedBy = "vehicleJourneys")
	private List<Block> blocks = new ArrayList<>();

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
	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST })
	@JoinColumn(name = "company_id")
	private Company company;

	/**
	 * footnotes refs
	 * 
	 * @param footnotes
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@ManyToMany( cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinTable(name = "footnotes_vehicle_journeys", joinColumns = { @JoinColumn(name = "vehicle_journey_id") }, inverseJoinColumns = { @JoinColumn(name = "footnote_id") })
	private List<Footnote> footnotes = new ArrayList<>(0);

	/**
	 * keyvalues
	 *
	 * @param keyvalue
	 * New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable(name = "vehicle_journeys_key_values", joinColumns = @JoinColumn(name = "vehicle_journey_id"))
	private List<KeyValue> keyValues = new ArrayList<>(0);

	/**
	 * timetables
	 * 
	 * @param timetables
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@ManyToMany(cascade = { CascadeType.PERSIST }, fetch = FetchType.LAZY)
	@JoinTable(name = "time_tables_vehicle_journeys", joinColumns = { @JoinColumn(name = "vehicle_journey_id", nullable = false, updatable = false) }, inverseJoinColumns = { @JoinColumn(name = "time_table_id", nullable = false, updatable = false) })
	private List<Timetable> timetables = new ArrayList<Timetable>(0);

	/**
	 * vehicle journey at stops : passing times
	 * 
	 * @return The actual value
	 */
	@Getter
	@Setter
	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
	@JoinColumn(name = "vehicle_journey_id", updatable = false)
	private List<VehicleJourneyAtStop> vehicleJourneyAtStops = new ArrayList<VehicleJourneyAtStop>(0);
	
	/**
	 * To distinguish the timesheets journeys and the frequencies ones. Defaults to Timesheet.
	 * 
	 * @param journeyCategory
	 *         The new vehicle journey category
	 * @return The actual vehicle journey category
	 * @since 3.2.0
	 */
	@Getter
	@Setter
	@Enumerated(EnumType.ORDINAL)
	@Column(name = "journey_category")
	private JourneyCategoryEnum journeyCategory = JourneyCategoryEnum.Timesheet;
	
	/**
	 * For frequencies journeys, applicable periods
	 * 
	 * @param journeyFrequencies
	 *         The new vehicle journey frequencies
	 * @return The actual vehicle journey category
	 * @since 3.2.0
	 */
	@Getter
	@Setter
	@OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST })
	@JoinColumn(name = "vehicle_journey_id", updatable = false)
	private List<JourneyFrequency> journeyFrequencies = new ArrayList<JourneyFrequency>(0);
	
	/**
	 * list of interchanges where this vehicle journey participates as the feeder
	 * 
	 */
	@Getter
	@Setter
	@OneToMany(mappedBy = "feederVehicleJourney", fetch = FetchType.LAZY)
	private List<Interchange> feederInterchanges = new ArrayList<>(0);


	/**
	 * list of interchanges where this vehicle journey participates as the consumer
	 * 
	 */
	@Getter
	@Setter
	@OneToMany(mappedBy = "consumerVehicleJourney", cascade = { CascadeType.PERSIST, CascadeType.MERGE },fetch = FetchType.LAZY)
	private List<Interchange> consumerInterchanges = new ArrayList<>(0);

	@Getter
	@Setter
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "flexible_service_properties_id")
	private FlexibleServiceProperties flexibleServiceProperties;


	public SortedSet<LocalDate> getActiveDates() {

		if (hasTimetables()) {
			Set<LocalDate> includedDates = getTimetables().stream().map(Timetable::getActiveDates).flatMap(Set::stream).collect(Collectors.toSet());

			// Assuming exclusions across Timetables take precedent, so need to make sure all excluded dates are actually excluded
			Set<LocalDate> excludedDates = getTimetables().stream().map(Timetable::getExcludedDates).flatMap(List::stream).collect(Collectors.toSet());

			includedDates.removeAll(excludedDates);
			return new TreeSet<>(includedDates);
		} else if (hasDatedServiceJourneys()) {
			return getDatedServiceJourneys().stream().filter(DatedServiceJourney::isNeitherCancelledNorReplaced).map(DatedServiceJourney::getOperatingDay).collect(Collectors.toCollection(TreeSet::new));
		} else {
			return new TreeSet<>();
		}
	}

	public boolean hasStops() {
		return !getVehicleJourneyAtStops().isEmpty();
	}

	public boolean hasTimetables() {
		return !getTimetables().isEmpty();
	}


	public boolean hasDatedServiceJourneys() {
		return !getDatedServiceJourneys().isEmpty();
	}

	/**
	 * Return the day offset at the last stop.
	 */
	private int getDayOffSetAtLastStop() {
		return getVehicleJourneyAtStops().stream().filter(vjas -> vjas.getArrivalTime() != null).map(VehicleJourneyAtStop::getArrivalDayOffset).max(Integer::compare).orElse(0);
	}

	/**
	 * Return the day offset at the first stop.
	 */
	private int getDayOffSetAtFirstStop() {
		return getVehicleJourneyAtStops().stream().filter(vjas -> vjas.getDepartureTime() != null).map(VehicleJourneyAtStop::getDepartureDayOffset).min(Integer::compare).orElse(0);
	}

	/**
	 * Get the effective start date of a period, taking into account the day offset at last stop.
	 *
	 * @param startDate the start date of the period (inclusive).
	 * @return the effective start date of the period, taking into account the day offset at last stop.
	 */
	private LocalDate getEffectiveStartDate(LocalDate startDate) {
		final LocalDate effectiveStartDate;
		if (startDate != null) {
			int dayOffSetAtLastStop = getDayOffSetAtLastStop();
			effectiveStartDate = startDate.minusDays(dayOffSetAtLastStop);
			if (dayOffSetAtLastStop != 0 && log.isTraceEnabled()) {
				log.trace("VJ " + getObjectId() + ": Day offset at last stop: " + dayOffSetAtLastStop + " day(s), shifting effective start date of active period: " + startDate + " --> " + effectiveStartDate);
			}
		} else {
			effectiveStartDate = null;
		}
		return effectiveStartDate;
	}

	/**
	 * Get the effective end date of a period, taking into account the day offset at first stop.
	 *
	 * @param endDate the end date of the period  (inclusive).
	 * @return the effective end date of the period, taking into account the day offset at last stop.
	 */
	private LocalDate getEffectiveEndDate(LocalDate endDate) {
		final LocalDate effectiveEndDate;
		if (endDate != null) {
			int dayOffSetAtFirstStop = getDayOffSetAtFirstStop();
			effectiveEndDate = endDate.minusDays(dayOffSetAtFirstStop);
			if (dayOffSetAtFirstStop != 0 && log.isTraceEnabled()) {
				log.trace("VJ " + getObjectId() + ": Day offset at first stop: " + dayOffSetAtFirstStop + " day(s), shifting effective end date of active period: " + endDate + " --> " + effectiveEndDate);
			}
		} else {
			effectiveEndDate = null;
		}
		return effectiveEndDate;
	}

	/**
	 * Retrieve the list of active timetables on the period, taking into account the day offset at first stop and last stop.
	 *
	 * @param startDate the start date of the period (inclusive).
	 * @param endDate   the end date of the period (inclusive).
	 * @return the list of timetables active on the period, taking into account the day offset at first stop and last stop.
	 */
	public List<Timetable> getActiveTimetablesOnPeriod(LocalDate startDate, LocalDate endDate) {
		final LocalDate effectiveStartDate = getEffectiveStartDate(startDate);
		final LocalDate effectiveEndDate = getEffectiveEndDate(endDate);
		return getTimetables().stream().filter(t -> t.isActiveOnPeriod(effectiveStartDate, effectiveEndDate)).collect(Collectors.toList());
	}

	public boolean hasActiveTimetablesOnPeriod(LocalDate startDate, LocalDate endDate) {
		return !getActiveTimetablesOnPeriod(startDate, endDate).isEmpty();
	}

	/**
	 * Retrieve the list of active dated service journeys on the period, taking into account the day offset at first stop and last stop.
	 * @param startDate the start date of the period (inclusive).
	 * @param endDate the end date of the period (exclusive).
	 * @return the list of dated service journeys active on the period, taking into account the day offset at first stop and last stop.
	 */
	public List<DatedServiceJourney> getActiveDatedServiceJourneysOnPeriod(LocalDate startDate, LocalDate endDate) {
		final LocalDate effectiveStartDate = getEffectiveStartDate(startDate);
		final LocalDate effectiveEndDate = getEffectiveEndDate(endDate);
		return getDatedServiceJourneys().stream().filter(dsj->dsj.isValidOnPeriod(effectiveStartDate, effectiveEndDate )).collect(Collectors.toList());
	}

	private boolean hasActiveDatedServiceJourneysOnPeriod(LocalDate startDate, LocalDate endDate) {
		return !getActiveDatedServiceJourneysOnPeriod(startDate, endDate).isEmpty();
	}

	public boolean isActiveOnPeriod(LocalDate startDate, LocalDate endDate) {
		return hasActiveTimetablesOnPeriod(startDate, endDate) || hasActiveDatedServiceJourneysOnPeriod(startDate, endDate);
	}

	public boolean isNeitherCancelledNorReplaced() {
		ServiceAlterationEnum serviceAlterationEnum = getServiceAlteration();
		return ServiceAlterationEnum.Cancellation != serviceAlterationEnum && ServiceAlterationEnum.Replaced != serviceAlterationEnum;
	}
}
