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
public class RegtoppInterchange extends RegtoppObject implements Serializable
{

   private static final long serialVersionUID = 1L;
   
   @Getter
   @Setter
   @Field(length = 3)
   private Integer administrationCode1;

   @Getter
   @Setter
   @Field(length = 1)
   private Integer counter1;

   @Getter
   @Setter
   @Field(length = 4)
   private Integer lineNumber1;

   @Getter
   @Setter
   @Field(length = 4)
   private Integer tripNumber1;

   @Getter
   @Setter
   @Field(length = 3)
   private Integer typeOfTraffic1;

   @Getter
   @Setter
   @Field(length = 3)
   private Integer administrationCode2;

   @Getter
   @Setter
   @Field(length = 1)
   private Integer counter2;

   @Getter
   @Setter
   @Field(length = 4)
   private Integer lineNumber2;

   @Getter
   @Setter
   @Field(length = 4)
   private Integer tripNumber2;

   @Getter
   @Setter
   @Field(length = 3)
   private Integer typeOfTraffic2;

   @Getter
   @Setter
   @Field(length = 4)
   private Integer arrivalTime1;

   @Getter
   @Setter
   @Field(length = 4)
   private Integer departureTime2;

   @Getter
   @Setter
   @Field(length = 3)
   private String notInUse;

   @Getter
   @Setter
   @Field(length = 8)
   private Integer stopId;
   
   @Getter
   @Setter
   @Field(length = 1)
   private Integer interchangeCode;

  


   
}
