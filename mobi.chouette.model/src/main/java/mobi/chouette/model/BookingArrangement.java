package mobi.chouette.model;

import java.util.List;

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
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import mobi.chouette.model.type.BookingAccessEnum;
import mobi.chouette.model.type.BookingMethodEnum;
import mobi.chouette.model.type.PurchaseMomentEnum;
import mobi.chouette.model.type.PurchaseWhenEnum;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.joda.time.Duration;
import org.joda.time.LocalTime;

@Entity
@Table(name = "booking_arrangements")
@NoArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = true)
public class BookingArrangement extends NeptuneObject {

	@Getter
	@Setter
	@GenericGenerator(name = "booking_arrangements_id_seq", strategy = "mobi.chouette.persistence.hibernate.ChouetteIdentifierGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "booking_arrangements_id_seq"),
			@Parameter(name = "increment_size", value = "100")})
	@GeneratedValue(generator = "booking_arrangements_id_seq")
	@Id
	@Column(name = "id", nullable = false)
	protected Long id;

	@Getter
	@Setter
	@ElementCollection(fetch = FetchType.LAZY)
	@Enumerated(EnumType.STRING)
	@CollectionTable(name = "booking_arrangements_booking_methods", joinColumns = @JoinColumn(name = "booking_arrangement_id"))
	@Column(name = "booking_method", nullable = false)
	private List<BookingMethodEnum> bookingMethods;

	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	@Column(name = "booking_access")
	private BookingAccessEnum bookingAccess;

	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	@Column(name = "book_when")
	private PurchaseWhenEnum bookWhen;


	@Getter
	@Setter
	@Column(name = "latest_booking_time")
	private LocalTime latestBookingTime;

	@Getter
	@Setter
	@Column(name = "minimum_booking_period")
	@Type(type = "mobi.chouette.jadira.PersistentDurationAsSqlTime")
	private Duration minimumBookingPeriod;


	@Getter
	@Setter
	@ElementCollection(fetch = FetchType.LAZY)
	@Enumerated(EnumType.STRING)
	@CollectionTable(name = "booking_arrangements_buy_when", joinColumns = @JoinColumn(name = "booking_arrangement_id"))
	@Column(name = "buy_when", nullable = false)
	private List<PurchaseMomentEnum> buyWhen;

	@Getter
	@Setter
	@Column(name = "booking_note")
	private String bookingNote;

	@Getter
	@Setter
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "booking_contact_id")
	private ContactStructure bookingContact;
}
