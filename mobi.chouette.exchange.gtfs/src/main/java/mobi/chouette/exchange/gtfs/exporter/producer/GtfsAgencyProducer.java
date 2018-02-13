/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package mobi.chouette.exchange.gtfs.exporter.producer;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;

import lombok.extern.log4j.Log4j;
import mobi.chouette.exchange.gtfs.model.GtfsAgency;
import mobi.chouette.exchange.gtfs.model.exporter.GtfsExporterInterface;
import mobi.chouette.model.Company;

import org.apache.commons.lang3.StringUtils;

import static mobi.chouette.common.PropertyNames.GTFS_AGENCY_PHONE_DEFAULTS;
import static mobi.chouette.common.PropertyNames.GTFS_AGENCY_URL_DEFAULTS;

/**
 * convert Timetable to Gtfs Calendar and CalendarDate
 * <p>
 * optimise multiple period timetable with calendarDate inclusion or exclusion
 */
@Log4j
public class GtfsAgencyProducer extends AbstractProducer
{
   public GtfsAgencyProducer(GtfsExporterInterface exporter)
   {
      super(exporter);
   }

   private GtfsAgency agency = new GtfsAgency();


   public boolean save(Company neptuneObject, String prefix, TimeZone timeZone, boolean keepOriginalId)
   {
      agency.setAgencyId(toGtfsId(neptuneObject.getObjectId(),prefix,keepOriginalId));

      String name = neptuneObject.getName();
      if (name.trim().isEmpty())
      {
         log.error("no name for " + neptuneObject.getObjectId());
//         GtfsReportItem item = new GtfsReportItem(
//               GtfsReportItem.KEY.MISSING_DATA, STATE.ERROR, "Company",
//               neptuneObject.getObjectId(), "Name");
//         report.addItem(item);
         return false;
      }

      agency.setAgencyName(name);

      // manage agency_timezone
      TimeZone tz = timeZone;
      if (!isEmpty(neptuneObject.getTimeZone()))
      {
         tz = TimeZone.getTimeZone(neptuneObject.getTimeZone());
      }
      if (tz == null)
      {
         tz = TimeZone.getDefault();
      }
      agency.setAgencyTimezone(tz);

      // manage agency_url mandatory
      // String urlData = "Url";
      String url = sanitizeUrl(getValue(neptuneObject.getUrl()));
      if (url == null)
      {
         url = createURLFromProviderDefaults(neptuneObject);
      }
      try
      {
         agency.setAgencyUrl(new URL(url));
      } catch (MalformedURLException e)
      {
         log.error("malformed URL " + url + " creating url from organisation unit as replacement");
		  String replacementUrl = createURLFromProviderDefaults(neptuneObject);
		  try {
            agency.setAgencyUrl(new URL(replacementUrl));
         } catch (MalformedURLException e2) {
            log.error("malformed replacementUrl " + replacementUrl + " ignoring agency");
            return false;
         }

//         GtfsReportItem item = new GtfsReportItem(
//               GtfsReportItem.KEY.INVALID_DATA, STATE.ERROR, "Company",
//               neptuneObject.getName(), urlData, url);
//         report.addItem(item);
      }

      if (neptuneObject.getPhone() != null) {
		  agency.setAgencyPhone(neptuneObject.getPhone());
	  } else {
		  agency.setAgencyPhone(createPhoneFromProviderDefaults(neptuneObject));
	  }

      // unmanaged attributes
      agency.setAgencyLang(null);
      agency.setAgencyFareUrl(null);
      
      try
      {
         getExporter().getAgencyExporter().export(agency);
      }
      catch (Exception e)
      {
         log.error("fail to produce agency "+e.getClass().getName()+" "+e.getMessage());
         return false;
      }
      return true;
   }

	private String sanitizeUrl(String url) {
		String sanitized = url;
		if (sanitized != null) {

			sanitized = sanitized.trim();
			if (!sanitized.toLowerCase().startsWith("http")) {
				sanitized = "http://" + sanitized;
			}
		}
		return sanitized;
	}

	String createURLFromProviderDefaults(Company neptuneObject) {
		String urlDefaults = System.getProperty(GTFS_AGENCY_URL_DEFAULTS);

		String defaultUrl = getDefaultValueForProvider(neptuneObject, urlDefaults);
		if (defaultUrl != null) return sanitizeUrl(defaultUrl);

		return createURLFromOrganisationalUnit(neptuneObject);
	}

	private String getDefaultValueForProvider(Company neptuneObject, String defaultValues) {
		if (defaultValues != null) {
			Map<String, String> urlsPerCodeSpace = Arrays.stream(defaultValues.split(",")).filter(codeSpaceEqualsUrl -> codeSpaceEqualsUrl != null && codeSpaceEqualsUrl.contains("=")).map(codeSpaceEqualsUrl -> codeSpaceEqualsUrl.split("=")).collect(Collectors.toMap(codeSpaceEqualsUrl -> codeSpaceEqualsUrl[0],
					codeSpaceEqualsUrl -> codeSpaceEqualsUrl[1]));
			return urlsPerCodeSpace.get(neptuneObject.objectIdPrefix());

		}
		return null;
	}

	String createPhoneFromProviderDefaults(Company neptuneObject) {
		String urlDefaults = System.getProperty(GTFS_AGENCY_PHONE_DEFAULTS);
		return getDefaultValueForProvider(neptuneObject, urlDefaults);
	}

   String createURLFromOrganisationalUnit(Company neptuneObject) {
      String url;
      if (neptuneObject.getOrganisationalUnit() != null
			&& neptuneObject.getOrganisationalUnit().startsWith("http"))
	  {
		 // urlData = "OrganisationalUnit";
         url = neptuneObject.getOrganisationalUnit();
      } else {
         String hostName = "unknown";
         if (!StringUtils.isEmpty(neptuneObject.getShortName())) {
            hostName = neptuneObject.getShortName();
         } else if (!StringUtils.isEmpty(neptuneObject.getName())) {
            hostName = neptuneObject.getName();
         }

         url = "http://www." + hostName.replaceAll("[^A-Za-z0-9]", "") + ".com";
      }
      return url;
   }

}
