package mobi.chouette.exchange.regtopp.model;

import java.io.Serializable;

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
public class RegtoppStopTime extends RegtoppObject implements Serializable
{

   private static final long serialVersionUID = 1L;

   @Getter
   @Setter
   @Field(length = 8)
   private Integer stopId;
   
   @Getter
   @Setter
   @Field(length = 3)
   private Integer arrivalOffset;
   
   @Getter
   @Setter
   @Field(length = 3)
   private Integer departureOffset;
   
   @Getter
   @Setter
   @Field(length = 6)
   private Integer distance10Meter;
   
  

   // @Override
   // public String toString()
   // {
   // return id + ":" + StopTimeExporter.CONVERTER.to(new Context(),this);
   // }

   public enum DropOffType implements Serializable
   {
      Scheduled, NoAvailable, AgencyCall, DriverCall;
   }

   public enum PickupType implements Serializable
   {
      Scheduled, NoAvailable, AgencyCall, DriverCall;
   }
}
