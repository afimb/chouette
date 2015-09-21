package mobi.chouette.exchange.gtfs.model;

import java.io.Serializable;
import java.net.URL;
import java.util.TimeZone;

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
public class GtfsAgency extends GtfsObject implements Serializable
{

   private static final long serialVersionUID = 1L;

   public static final String DEFAULT_ID = "default";

   @Getter
   @Setter
   private String agencyId;

   @Getter
   @Setter
   private String agencyName;

   @Getter
   @Setter
   private URL agencyUrl;

   @Getter
   @Setter
   private TimeZone agencyTimezone;

   @Getter
   @Setter
   private String agencyLang;

   @Getter
   @Setter
   private String agencyPhone;

   @Getter
   @Setter
   private URL agencyFareUrl;

   // @Override
   // public String toString()
   // {
   // return id + ":" + AgencyExporter.CONVERTER.to(new Context(), this);
   // }

}
