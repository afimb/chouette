package mobi.chouette.exchange.gtfs.model;

import java.io.Serializable;

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
public class GtfsTransfer extends GtfsObject implements Serializable
{

   private static final long serialVersionUID = 1L;

   @Getter
   @Setter
   private String fromStopId;

   @Getter
   @Setter
   private String toStopId;

   @Getter
   @Setter
   private TransferType transferType;

   @Getter
   @Setter
   private Integer minTransferTime;

   public enum TransferType implements Serializable
   {
      Recommended, Timed, Minimal, NoAllowed;

   }

   // @Override
   // public String toString()
   // {
   // return id + ":" + TransferExporter.CONVERTER.to(new Context(),this);
   // }
}
