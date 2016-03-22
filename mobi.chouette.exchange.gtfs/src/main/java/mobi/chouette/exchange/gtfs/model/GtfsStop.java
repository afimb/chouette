package mobi.chouette.exchange.gtfs.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.net.URL;
import java.util.TimeZone;

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
public class GtfsStop extends GtfsObject implements Serializable
{

   private static final long serialVersionUID = 1L;

   @Getter
   @Setter
   private String stopId;

   @Getter
   @Setter
   private String stopCode;

   @Getter
   @Setter
   private String stopName;

   @Getter
   @Setter
   private String stopDesc;

   @Getter
   @Setter
   private BigDecimal stopLat;

   @Getter
   @Setter
   private BigDecimal stopLon;

   @Getter
   @Setter
   private String zoneId;

   @Getter
   @Setter
   private URL stopUrl;

   @Getter
   @Setter
   private LocationType locationType;

   @Getter
   @Setter
   private String parentStation;

   @Getter
   @Setter
   private TimeZone stopTimezone;

   @Getter
   @Setter
   private WheelchairBoardingType wheelchairBoarding;
   
   @Getter
   @Setter
   private String addressLine;
   
   @Getter
   @Setter
   private String locality;
   
   @Getter
   @Setter
   private String postalCode;

   // @Override
   // public String toString()
   // {
   // return id + ":" + StopExporter.CONVERTER.to(new Context(),this);
   // }

   public GtfsStop(GtfsStop bean) {
	   this(bean.getStopId(), bean.getStopCode(), bean.getStopName(), bean.getStopDesc(),
			   bean.getStopLat(), bean.getStopLon(), bean.getZoneId(), bean.getStopUrl(), bean.getLocationType(),
			   bean.getParentStation(), bean.getStopTimezone(), bean.getWheelchairBoarding(),
			   bean.getAddressLine(), bean.getLocality(), bean.getPostalCode());
	   this.setId(bean.getId());
   }

   public enum LocationType implements Serializable 
   {
      Stop, Station, Access;

   }

   public enum WheelchairBoardingType implements Serializable
   {
      NoInformation, Allowed, NoAllowed;

   }

}
