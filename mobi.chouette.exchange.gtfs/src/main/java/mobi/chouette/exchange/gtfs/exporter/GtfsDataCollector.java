package mobi.chouette.exchange.gtfs.exporter;

import java.util.Collection;

import lombok.extern.log4j.Log4j;
import mobi.chouette.exchange.exporter.DataCollector;
import mobi.chouette.model.Company;
import mobi.chouette.model.Line;
import mobi.chouette.model.Network;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.type.ChouetteAreaEnum;
import mobi.chouette.model.type.OrganisationTypeEnum;

import org.joda.time.LocalDate;

@Log4j
public class GtfsDataCollector extends DataCollector {
	public boolean collect(ExportableData collection, Line line, LocalDate startDate, LocalDate endDate) {
		boolean res = collect(collection, line, startDate, endDate, false, false);

		if (res) {
			collectAgencyCompany(line, collection);
		}
		return res;
	}

	public boolean collect(ExportableData collection, Collection<StopArea> stopAreas) {
		return collect(collection, stopAreas, false, false);

	}

	/**
	 * Google dislikes agency listings with no references to them.
	 * <p>
	 * Override default company collection process by only adding one company per line, the one that is actually referred to by the lines corresponding GTFS route.
	 * <p>
	 * 1. If the line's company is an authority this will be used
	 * 2. If the line has a non-authority company this will be used
	 * 3. If not 1. og 2. the lines network will be used as agency (if set).
	 */
	private void collectAgencyCompany(Line line, ExportableData collection) {
		Company company = line.getCompany();

		if (company == null || !OrganisationTypeEnum.Authority.equals(company.getOrganisationType())) {
			// Use network->authority as agency if it is an authority
			Network network = line.getNetwork();
			if (network != null && network.getCompany() != null) {
				if (OrganisationTypeEnum.Authority.equals(network.getCompany().getOrganisationType())) {
					company = network.getCompany();
				}
			}
		}

		if (company == null) {
			log.info("line " + line.getObjectId() + " : missing company, using network instead");
			company = new Company();
			company.setObjectId(line.getNetwork().getObjectId());
			company.setName(line.getNetwork().getName());
		}
		if (company != null) {
			collection.getAgencyCompanies().add(company);
		}
	}

	@Override
	protected void collectStopAreas(mobi.chouette.exchange.exporter.ExportableData collection, StopArea stopArea, boolean skipNoCoordinates, boolean followLinks) {
		if (stopArea.getAreaType().equals(ChouetteAreaEnum.BoardingPosition)
				|| stopArea.getAreaType().equals(ChouetteAreaEnum.Quay)) {
			if (stopArea.getParent() != null) {
				collectStopAreas(collection, stopArea.getParent(), skipNoCoordinates, followLinks);
			}
			if (collection.getPhysicalStops().contains(stopArea)) {
				return;
			}
			collection.getPhysicalStops().add(stopArea);
			addConnectionLinks(collection, stopArea.getConnectionStartLinks(), skipNoCoordinates, followLinks);
			addConnectionLinks(collection, stopArea.getConnectionEndLinks(), skipNoCoordinates, followLinks);
		} else if (stopArea.getAreaType().equals(ChouetteAreaEnum.CommercialStopPoint)) {
			if (collection.getCommercialStops().contains(stopArea)) {
				return;
			}
			collection.getCommercialStops().add(stopArea);
			addConnectionLinks(collection, stopArea.getConnectionStartLinks(), skipNoCoordinates, followLinks);
			addConnectionLinks(collection, stopArea.getConnectionEndLinks(), skipNoCoordinates, followLinks);
			for (StopArea sa : stopArea.getContainedStopAreas()) {
				collectStopAreas(collection, sa, skipNoCoordinates, followLinks);
			}
		}
	}


}
