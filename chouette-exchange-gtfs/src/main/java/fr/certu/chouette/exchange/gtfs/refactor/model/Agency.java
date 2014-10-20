package fr.certu.chouette.exchange.gtfs.refactor.model;

import java.io.Serializable;
import java.net.URL;
import java.util.TimeZone;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class Agency extends GtfsObject implements Serializable
{

   private static final long serialVersionUID = 1L;

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

}
