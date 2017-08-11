package mobi.chouette.exchange.gtfs.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import org.joda.time.LocalTime;

@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class GtfsTime {

	@Getter
	@Setter
	private LocalTime time;

	@Getter
	@Setter
	private Integer day = 0;

	public boolean moreOneDay() {
		return (day != 0);
	}

}
