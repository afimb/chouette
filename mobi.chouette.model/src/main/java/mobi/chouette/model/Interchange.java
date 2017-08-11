package mobi.chouette.model;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.joda.time.Duration;

/**
 * Interchange between 2 service journeys at given points
 * 
 * 
 */

@Entity
@Table(name = "interchanges")
@NoArgsConstructor
@ToString(exclude = { "consumerVehicleJourney", "feederVehicleJourney", "feederStopPoint", "consumerStopPoint" })
public class Interchange extends NeptuneIdentifiedObject {
	private static final long serialVersionUID = 84905295077539089L;

	@Getter
	@Setter
	@GenericGenerator(name = "interchanges_id_seq", strategy = "mobi.chouette.persistence.hibernate.ChouetteIdentifierGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "interchanges_id_seq"),
			@Parameter(name = "increment_size", value = "100") })
	@GeneratedValue(generator = "interchanges_id_seq")
	@Id
	@Column(name = "id", nullable = false)
	protected Long id;

	@Getter
	@Setter
	@Column(name = "name")
	private String name;

	@Getter
	@Setter
	@Column(name = "priority")
	private Integer priority;

	@Getter
	@Setter
	@Column(name = "planned")
	private Boolean planned;

	@Getter
	@Setter
	@Column(name = "guaranteed")
	private Boolean guaranteed;

	@Getter
	@Setter
	@Column(name = "advertised")
	private Boolean advertised;

	@Getter
	@Setter
	@Column(name = "stay_seated")
	private Boolean staySeated;

	@Getter
	@Setter
	@Column(name = "maximum_wait_time")
	@Type(type = "mobi.chouette.jadira.PersistentDurationAsSqlTime")
	private Duration maximumWaitTime;

	@Getter
	@Setter
	@Column(name = "minimum_transfer_time")
	@Type(type = "mobi.chouette.jadira.PersistentDurationAsSqlTime")
	private Duration minimumTransferTime;

	// Field mapped twice in order to handle that the to_vehicle_journey
	// contains an objectid which may reference a non existent vehicle journey
	// in this space (but may be found in other spaces)
	@Getter
	@ManyToOne(fetch = FetchType.LAZY)
	@Access(AccessType.FIELD)
	@JoinColumn(name = "to_vehicle_journey", referencedColumnName = "objectid", insertable = false, updatable = false)
	private VehicleJourney consumerVehicleJourney;

	public void setConsumerVehicleJourney(VehicleJourney vj) {
		if (vj != null) {
			this.consumerVehicleJourneyObjectid = vj.getObjectId();
		} else {
			this.consumerVehicleJourneyObjectid = null;
		}
		this.consumerVehicleJourney = null;
	}

	@Getter
	@Column(name = "to_vehicle_journey", insertable = true, updatable = true)
	private String consumerVehicleJourneyObjectid;

	public void setConsumerVehicleJourneyObjectid(String objectid) {
		this.consumerVehicleJourneyObjectid = objectid;
		this.consumerVehicleJourney = null;
	}

	// Field mapped twice in order to handle that the to_vehicle_journey
	// contains an objectid which may reference a non existent vehicle journey
	// in this space (but may be found in other spaces)
	@Getter
	@ManyToOne(fetch = FetchType.LAZY)
	@Access(AccessType.FIELD)
	@JoinColumn(name = "from_vehicle_journey", referencedColumnName = "objectid", insertable = false, updatable = false)
	private VehicleJourney feederVehicleJourney;

	public void setFeederVehicleJourney(VehicleJourney vj) {
		if (vj != null) {
			this.feederVehicleJourneyObjectid = vj.getObjectId();
		} else {
			this.feederVehicleJourneyObjectid = null;
		}
		this.feederVehicleJourney = null;

	}

	@Getter
	@Column(name = "from_vehicle_journey", insertable = true, updatable = true)
	private String feederVehicleJourneyObjectid;

	public void setFeederVehicleJourneyObjectid(String objectid) {
		this.feederVehicleJourneyObjectid = objectid;
		this.feederVehicleJourney = null;
	}

	// Field mapped twice in order to handle that the to_vehicle_journey
	// contains an objectid which may reference a non existent vehicle journey
	// in this space (but may be found in other spaces)
	@Getter
	@ManyToOne(fetch = FetchType.LAZY)
	@Access(AccessType.FIELD)
	@JoinColumn(name = "to_point", referencedColumnName = "objectid", insertable = false, updatable = false)
	private StopPoint consumerStopPoint;

	public void setConsumerStopPoint(StopPoint vj) {

		if (vj != null) {
			this.consumerStopPointObjectid = vj.getObjectId();
		} else {
			this.consumerStopPointObjectid = null;
		}
		this.consumerStopPoint = null;
	}

	@Getter
	@Column(name = "to_point", insertable = true, updatable = true)
	private String consumerStopPointObjectid;

	public void setConsumerStopPointObjectid(String objectid) {
		this.consumerStopPointObjectid = objectid;
		this.consumerStopPoint = null;
	}

	@Getter
	@Setter
	@Column(name = "to_visit_number")
	private Integer consumerVisitNumber;

	// Field mapped twice in order to handle that the to_vehicle_journey
	// contains an objectid which may reference a non existent vehicle journey
	// in this space (but may be found in other spaces)
	@Getter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "from_point", referencedColumnName = "objectid", insertable = false, updatable = false)
	@Access(AccessType.FIELD)
	private StopPoint feederStopPoint;

	public void setFeederStopPoint(StopPoint vj) {
		if (vj != null) {
			this.feederStopPointObjectid = vj.getObjectId();
		} else {
			this.feederStopPointObjectid = null;
		}

		this.feederStopPoint = null;
	}

	@Getter
	@Column(name = "from_point", insertable = true, updatable = true)
	private String feederStopPointObjectid;

	public void setFeederStopPointObjectid(String objectid) {
		this.feederStopPointObjectid = objectid;
		this.feederStopPoint = null;
	}

	@Getter
	@Setter
	@Column(name = "from_visit_number")
	private Integer feederVisitNumber;

}
