package fr.certu.chouette.exchange.gtfs.refactor.model;

import java.io.Serializable;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class GtfsFrequency extends GtfsObject implements Serializable
{

   private static final long serialVersionUID = 1L;

   @Getter
   @Setter
   private String tripId;

   @Getter
   @Setter
   private GtfsTime startTime;

   @Getter
   @Setter
   private GtfsTime endTime;

   @Getter
   @Setter
   private Integer headwaySecs;

   @Getter
   @Setter
   private Boolean exactTimes;

}
