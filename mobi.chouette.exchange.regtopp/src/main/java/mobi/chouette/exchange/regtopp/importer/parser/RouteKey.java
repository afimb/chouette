package mobi.chouette.exchange.regtopp.importer.parser;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import mobi.chouette.exchange.regtopp.model.enums.DirectionType;

@AllArgsConstructor
@EqualsAndHashCode
public class RouteKey {
	@Getter
	String lineId;
	@Getter
	DirectionType direction;
	@Getter
	String routeId;

	@Override
	public String toString() {
		return lineId + direction + routeId;
	}

	public RouteKey(String combined) {
		lineId = combined.substring(0, 4);
		direction = DirectionType.parseString(combined.substring(4, 5));
		routeId = combined.substring(5, 7);
	}
}
