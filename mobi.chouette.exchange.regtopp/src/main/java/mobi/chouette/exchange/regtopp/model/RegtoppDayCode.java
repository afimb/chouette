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
@Record(minOccurs = 1, order=2)
public class RegtoppDayCode extends RegtoppObject implements Serializable
{

   private static final long serialVersionUID = 1L;
   
   // TODO first line different
   
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
   @Field(length = 4)
   private String dayCodeId;
   
   @Getter
   @Setter
   @Field(length = 392)
   private String dayCode;
 
   
  

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
