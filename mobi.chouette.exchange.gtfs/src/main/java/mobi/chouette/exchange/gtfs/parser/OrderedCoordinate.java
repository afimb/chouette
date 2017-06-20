package mobi.chouette.exchange.gtfs.parser;

import com.vividsolutions.jts.geom.Coordinate;

class OrderedCoordinate extends Coordinate {
	private static final long serialVersionUID = 1L;
	public int order;

	public OrderedCoordinate(double x, double y, Integer order) {
		this.x = x;
		this.y = y;
		this.order = order.intValue();
	}
}