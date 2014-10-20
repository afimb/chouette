package fr.certu.chouette.exchange.gtfs.refactor.model;

import java.io.Serializable;
import java.net.URL;
import java.util.TimeZone;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class Stop extends GtfsObject implements Serializable
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
   private Float stopLat;

   @Getter
   @Setter
   private Float stopLon;

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

   public enum LocationType implements Serializable
   {
      BoardingPosition, CommercialStopPoint;

   }

   public enum WheelchairBoardingType implements Serializable
   {
      NoInformation, Allowed, NoAllowed;

   }

}
