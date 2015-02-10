package mobi.chouette.exchange.gtfs.model;

import java.sql.Time;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class GtfsTime {

	@Getter
	@Setter
	private Time time;

	@Getter
	@Setter
	private Integer day = 0;

	public boolean moreOneDay() {
		return (day != 0);
	}

}
