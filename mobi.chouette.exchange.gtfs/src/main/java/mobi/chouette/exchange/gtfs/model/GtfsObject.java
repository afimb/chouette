package mobi.chouette.exchange.gtfs.model;

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
   protected Integer id;
}
