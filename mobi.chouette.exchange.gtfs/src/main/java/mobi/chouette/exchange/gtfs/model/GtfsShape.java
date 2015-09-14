package mobi.chouette.exchange.gtfs.model;

import java.io.Serializable;
import java.math.BigDecimal;

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
public class GtfsShape extends GtfsObject implements Serializable {

	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	private String shapeId;

	@Getter
	@Setter
	private BigDecimal shapePtLat;

	@Getter
	@Setter
	private BigDecimal shapePtLon;

	@Getter
	@Setter
	private Integer shapePtSequence;

	@Getter
	@Setter
	private Float shapeDistTraveled;
}
