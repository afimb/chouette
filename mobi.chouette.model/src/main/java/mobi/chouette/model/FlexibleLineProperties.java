package mobi.chouette.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import mobi.chouette.model.type.BookingAccessEnum;
import mobi.chouette.model.type.BookingMethodEnum;
import mobi.chouette.model.type.FlexibleLineTypeEnum;
import mobi.chouette.model.type.PurchaseMomentEnum;
import mobi.chouette.model.type.PurchaseWhenEnum;

import org.hibernate.annotations.Type;
import org.joda.time.Duration;
import org.joda.time.LocalTime;

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
	@ElementCollection(fetch = FetchType.LAZY)
	@Enumerated(EnumType.STRING)
	@CollectionTable(name = "lines_booking_methods", joinColumns = @JoinColumn(name = "line_id"))
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
	@CollectionTable(name = "lines_buy_when", joinColumns = @JoinColumn(name = "line_id"))
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
