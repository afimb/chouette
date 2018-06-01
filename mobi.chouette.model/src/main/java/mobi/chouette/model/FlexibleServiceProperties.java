package mobi.chouette.model;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import mobi.chouette.model.type.FlexibleServiceTypeEnum;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
@Table(name = "flexible_service_properties")
@Cacheable
@NoArgsConstructor
@ToString(callSuper=true)
public class FlexibleServiceProperties extends NeptuneIdentifiedObject {


	@Getter
	@Setter
	@GenericGenerator(name = "flexible_service_properties_id_seq", strategy = "mobi.chouette.persistence.hibernate.ChouetteIdentifierGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "flexible_service_properties_id_seq"),
			@Parameter(name = "increment_size", value = "100")})
	@GeneratedValue(generator = "flexible_service_properties_id_seq")
	@Id
	@Column(name = "id", nullable = false)
	protected Long id;

	@Getter
	@Setter
	@Column(name = "cancellation_possible")
	private Boolean cancellationPossible;

	@Getter
	@Setter
	@Column(name = "change_of_time_possible")
	private Boolean changeOfTimePossible;


	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	@Column(name = "flexible_service_type")
	private FlexibleServiceTypeEnum flexibleServiceType;


	@Getter
	@Setter
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "booking_arrangement_id")
	private BookingArrangement bookingArrangement;

}
