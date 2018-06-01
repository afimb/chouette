package mobi.chouette.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import mobi.chouette.model.type.FlexibleLineTypeEnum;

@Embeddable
@EqualsAndHashCode
@ToString
public class FlexibleLineProperties {

	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	@Column(name = "flexible_line_type")
	private FlexibleLineTypeEnum flexibleLineType;


	@Getter
	@Setter
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "booking_arrangement_id")
	private BookingArrangement bookingArrangement;
}
