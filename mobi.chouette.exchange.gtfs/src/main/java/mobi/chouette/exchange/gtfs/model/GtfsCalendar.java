package mobi.chouette.exchange.gtfs.model;

import java.io.Serializable;
import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class GtfsCalendar extends GtfsObject implements Serializable {

	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	private String serviceId;

	@Getter
	@Setter
	private Boolean monday;

	@Getter
	@Setter
	private Boolean tuesday;

	@Getter
	@Setter
	private Boolean wednesday;

	@Getter
	@Setter
	private Boolean thursday;

	@Getter
	@Setter
	private Boolean friday;

	@Getter
	@Setter
	private Boolean saturday;

	@Getter
	@Setter
	private Boolean sunday;

	@Getter
	@Setter
	private Date startDate;

	@Getter
	@Setter
	private Date endDate;

	/*
	 * @Override public String toString() { return id + ":" +
	 * CalendarExporter.CONVERTER.to(new Context(),this); }
	 */
}
