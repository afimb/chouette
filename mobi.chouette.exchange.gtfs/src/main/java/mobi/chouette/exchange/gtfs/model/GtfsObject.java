package mobi.chouette.exchange.gtfs.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import mobi.chouette.exchange.gtfs.model.importer.GtfsException;
import mobi.chouette.exchange.gtfs.model.importer.GtfsWarning;

@ToString
@NoArgsConstructor
public abstract class GtfsObject
{

   @Getter
   @Setter
   protected Integer id;
   
   @Getter
   protected List<GtfsException> errors = new ArrayList<>();
}
