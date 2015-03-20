package mobi.chouette.exchange.kml.exporter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.collections.map.ListOrderedMap;

public class KmlData {
	@Getter
	@Setter
	private String name;
	@Getter
	@Setter
	private List<KmlItem> items = new ArrayList<>();

	public class KmlItem {
		@Getter
		@Setter
		private String name;
		@Getter
		@Setter
		private String objectId;
		@Getter
		@Setter
		private ListOrderedMap attributes = new ListOrderedMap();
		@Getter
		@Setter
		private List<KmlPoint> lineString;
		@Getter
		@Setter
		private KmlPoint point;
		
		public void addAttribute(String key, Object value)
		{
			attributes.put(key,value);
		}
		
		public void setPoint(BigDecimal latitude, BigDecimal longitude)
		{
			point = new KmlPoint(latitude,longitude);
		}
		
		public void addPoint(BigDecimal latitude, BigDecimal longitude)
		{
			if (lineString == null) lineString = new ArrayList<>();
			lineString.add(new KmlPoint(latitude, longitude));
		}
	}

	public class KmlPoint {
		@Getter
		@Setter
		private double latitude;
		@Getter
		@Setter
		private double longitude;
		
		public KmlPoint(BigDecimal latitude, BigDecimal longitude)
		{
			this.latitude = latitude.doubleValue(); 
			this.longitude = longitude.doubleValue(); 
		}
	}

}
