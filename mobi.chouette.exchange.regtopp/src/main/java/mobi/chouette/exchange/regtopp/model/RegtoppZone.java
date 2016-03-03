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
public class RegtoppZone extends RegtoppObject implements Serializable
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
   @Field(length = 5)
   private Integer zoneId;
   
   @Getter
   @Setter
   @Field(length = 30)
   private String name;
 
   @Getter
   @Setter
   @Field(length = 5)
   private String zoneRef;
 
   
}
