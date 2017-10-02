package mobi.chouette.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mobi.chouette.model.type.AlightingPossibilityEnum;
import mobi.chouette.model.type.BoardingPossibilityEnum;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
@Table(name = "scheduled_stop_points")
@NoArgsConstructor
public class ScheduledStopPoint extends NeptuneIdentifiedObject {

	private static final long serialVersionUID = -4913573673645997423L;

	@Getter
	@Setter
	@GenericGenerator(name = "scheduled_stop_points_id_seq", strategy = "mobi.chouette.persistence.hibernate.ChouetteIdentifierGenerator",
			parameters = {
					@Parameter(name = "sequence_name", value = "scheduled_stop_points_id_seq"),
					@Parameter(name = "increment_size", value = "100")})
	@GeneratedValue(generator = "scheduled_stop_points_id_seq")
	@Id
	@Column(name = "id", nullable = false)
	protected Long id;

	/**
	 * name
	 *
	 * @return The actual value
	 */
	@Getter
	@Setter
	@Column(name = "name")
	private String name;


	/**
	 * stopPoints
	 *
	 * @param stopPoints
	 * New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@OneToMany(mappedBy = "scheduledStopPoint")
	private List<StopPoint> stopPoints = new ArrayList<>(0);

	@Column(name = "stop_area_objectid_key")
	private String containedInStopAreaObjectId;

	/**
	 * stop area container
	 *
	 * @return The actual value
	 */
	@Transient
	private ObjectReference<StopArea> containedInStopAreaRef;


	/**
	 * list of interchanges where this stop point participates as the feeder
	 */
	@Getter
	@Setter
	@OneToMany(mappedBy = "feederStopPoint", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
	private List<Interchange> feederInterchanges = new ArrayList<>(0);


	/**
	 * list of interchanges where this stop point participates as the consumer
	 */
	@Getter
	@Setter
	@OneToMany(mappedBy = "consumerStopPoint", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
	private List<Interchange> consumerInterchanges = new ArrayList<>(0);

	@Transient
	public ObjectReference<StopArea> getContainedInStopAreaRef() {
		if (this.containedInStopAreaRef == null) {
			this.containedInStopAreaRef = new SimpleObjectReference<>(null);
		}
		return this.containedInStopAreaRef;
	}

	@Transient
	public void setContainedInStopAreaRef(ObjectReference<StopArea> containedInStopAreaRef) {
		if (this.containedInStopAreaRef != null && this.containedInStopAreaRef.isLoaded() && this.containedInStopAreaRef.getObject() != null) {
			this.containedInStopAreaRef.getObject().getContainedScheduledStopPoints().remove(this);
		}


		this.containedInStopAreaRef = containedInStopAreaRef;
		if (containedInStopAreaRef != null) {
			this.containedInStopAreaObjectId = containedInStopAreaRef.getObjectId();

			if (containedInStopAreaRef.isLoaded() && containedInStopAreaRef.getObject() != null) {
				containedInStopAreaRef.getObject().getContainedScheduledStopPoints().add(this);
			}

		} else {
			this.containedInStopAreaObjectId = null;
		}
	}
}
