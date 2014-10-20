package fr.certu.chouette.exchange.gtfs.refactor.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@NoArgsConstructor
public abstract class GtfsObject
{

   @Getter
   @Setter
   private Integer id;
}
