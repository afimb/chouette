package mobi.chouette.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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

	/**
	 * boarding possibility
	 *
	 * @param forBoarding
	 * New value
	 * @return The actual value
	 * @since 2.5.2
	 */
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	@Column(name = "for_boarding")
	private BoardingPossibilityEnum forBoarding;

	/**
	 * alighting possibility
	 *
	 * @param forAlighting
	 * New value
	 * @return The actual value
	 * @since 2.5.2
	 */
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	@Column(name = "for_alighting")
	private AlightingPossibilityEnum forAlighting;


	@Getter
	@Column(name = "stop_area_objectid_key")
	private String containedInStopAreaObjectId;

	/**
	 * stop area container
	 *
	 * @return The actual value
	 */
	@Getter
	@Transient
	private StopArea containedInStopArea;


	/**
	 * set stop area
	 *
	 * @param stopArea
	 */
	public void setContainedInStopArea(StopArea stopArea) {
		if (this.containedInStopArea != null) {
			this.containedInStopArea.getContainedScheduledStopPoints().remove(this);
		}
		this.containedInStopArea = stopArea;
		if (stopArea != null) {
			stopArea.getContainedScheduledStopPoints().add(this);
			containedInStopAreaObjectId = stopArea.getObjectId();
		}
	}

	/**
	 * ORM only setter for establishing stop area relation without loading lazy collections.
	 * <p>
	 * NB! Do not use except when loading objects.
	 */
	@Transient
	public void setContainedInStopAreaORMOnly(StopArea stopArea) {
		this.containedInStopArea = stopArea;
		if (stopArea != null) {
			containedInStopAreaObjectId = stopArea.getObjectId();
		}
	}

	public StopArea getContainedInStopAreaORMOnly() {
		return this.containedInStopArea;
	}


}
