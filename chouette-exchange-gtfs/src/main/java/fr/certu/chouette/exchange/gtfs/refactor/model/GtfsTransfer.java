package fr.certu.chouette.exchange.gtfs.refactor.model;

import java.io.Serializable;
import java.util.Date;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
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
   private Date minTransferTime;

   public enum TransferType implements Serializable
   {
      Recommended, Timed, Minimal, NoAllowed;

   }
}
