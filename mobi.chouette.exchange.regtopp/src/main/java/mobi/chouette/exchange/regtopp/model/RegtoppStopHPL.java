package mobi.chouette.exchange.regtopp.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.net.URL;
import java.util.TimeZone;

import org.beanio.annotation.Field;
import org.beanio.annotation.Record;

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
@Record(minOccurs = 1)
public class RegtoppStopHPL extends RegtoppObject implements Serializable
{

	
   private static final long serialVersionUID = 1L;

   @Getter
   @Setter
   @Field(length = 3)
   private Integer adminCode;

   @Getter
   @Setter
   @Field(length = 1)
   private Integer counter;

   @Getter
   @Setter
   @Field(length = 8)
   private String stopId;

   @Getter
   @Setter
   @Field(length = 30)
   private String fullName;

   @Getter
   @Setter
   @Field(length = 5)
   private String shortName;

   @Getter
   @Setter
   @Field(length = 6)
   private String zoneShortName;


   @Getter
   @Setter
   @Field(length = 10)
   private BigDecimal stopLat;

   @Getter
   @Setter
   @Field(length = 10)
   private BigDecimal stopLon;

   @Getter
   @Setter
   @Field(length = 5)
   private Integer zoneId1;

   @Getter
   @Setter
   @Field(length = 5)
   private Integer zoneId2;

   @Getter
   @Setter
   @Field(length = 1)
   private Integer interchangeType;

   @Getter
   @Setter
   @Field(length = 2)
   private Integer interchangeMinutes;

   @Getter
   @Setter
   @Field(length = 1)
   private Integer coachClass;



   public RegtoppStopHPL(RegtoppStopHPL bean) {
	   throw new RuntimeException("Copy constructor not implemented");
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
