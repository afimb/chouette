package mobi.chouette.exchange.gtfs.model;

import java.util.Set;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import mobi.chouette.exchange.gtfs.model.importer.GtfsErrorsHashSet;
import mobi.chouette.exchange.gtfs.model.importer.GtfsException;
import mobi.chouette.exchange.gtfs.model.importer.GtfsExceptionsHashSet;

@ToString
@NoArgsConstructor
public abstract class GtfsObject
{

   @Getter
   @Setter
   protected Integer id;
   
   @Getter
   protected Set<GtfsException> errors = new GtfsExceptionsHashSet<>();
   
   @Getter
   protected Set<GtfsException.ERROR> okTests = new GtfsErrorsHashSet<>();
}
