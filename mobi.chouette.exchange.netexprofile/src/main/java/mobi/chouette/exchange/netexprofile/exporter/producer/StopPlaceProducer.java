package mobi.chouette.exchange.netexprofile.exporter.producer;

import static mobi.chouette.exchange.netexprofile.Constant.NETEX_REFERENTIAL;
import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducerUtils.isSet;

import org.apache.commons.collections.CollectionUtils;
import org.rutebanken.netex.model.LocationStructure;
import org.rutebanken.netex.model.PrivateCodeStructure;
import org.rutebanken.netex.model.Quay;
import org.rutebanken.netex.model.Quays_RelStructure;
import org.rutebanken.netex.model.SimplePoint_VersionStructure;
import org.rutebanken.netex.model.StopPlace;
import org.rutebanken.netex.model.ZoneRefStructure;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.netexprofile.util.NetexObjectUtil;
import mobi.chouette.exchange.netexprofile.util.NetexReferential;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.type.ChouetteAreaEnum;

public class StopPlaceProducer extends NetexProducer implements NetexEntityProducer<StopPlace, StopArea> {

	private static final String DEFAULT_COORDINATE_SYSTEM = "WGS84";

	@Override
	public StopPlace produce(Context context, StopArea stopArea) {
		NetexReferential netexReferential = (NetexReferential) context.get(NETEX_REFERENTIAL);
		StopPlace stopPlace = netexFactory.createStopPlace();

		NetexProducerUtils.populateId(stopArea, stopPlace);
		stopPlace.setName(getMultilingualString(stopArea.getName()));
		stopPlace.setDescription(getMultilingualString(stopArea.getComment()));

		if (isSet(stopArea.getRegistrationNumber())) {
			PrivateCodeStructure privateCodeStruct = netexFactory.createPrivateCodeStructure();
			privateCodeStruct.setValue(stopArea.getRegistrationNumber());
			stopPlace.setPrivateCode(privateCodeStruct);
		}

		if (stopArea.hasCoordinates()) {
			SimplePoint_VersionStructure pointStruct = netexFactory.createSimplePoint_VersionStructure();
			LocationStructure locationStruct = netexFactory.createLocationStructure().withSrsName(DEFAULT_COORDINATE_SYSTEM);

			if (stopArea.hasCoordinates()) {
				locationStruct.setLatitude(stopArea.getLatitude());
				locationStruct.setLongitude(stopArea.getLongitude());
			}

			pointStruct.setLocation(locationStruct);
			stopPlace.setCentroid(pointStruct);
		}

		if (isSet(stopArea.getParent())) {
			ZoneRefStructure zoneRefStruct = netexFactory.createZoneRefStructure();
			NetexProducerUtils.populateReference(stopArea.getParent(), zoneRefStruct, true);
			stopPlace.setParentZoneRef(zoneRefStruct);
		}

		if (stopArea.getAreaType().equals(ChouetteAreaEnum.CommercialStopPoint) && CollectionUtils.isNotEmpty(stopArea.getContainedStopAreas())) {
			Quays_RelStructure quayStruct = netexFactory.createQuays_RelStructure();

			for (StopArea containedStopArea : stopArea.getContainedStopAreas()) {
				Quay quay = netexFactory.createQuay();
				NetexProducerUtils.populateId(containedStopArea, quay);

				quay.setName(getMultilingualString(containedStopArea.getName()));
				quay.setDescription(getMultilingualString(containedStopArea.getComment()));

				if (isSet(containedStopArea.getRegistrationNumber())) {
					PrivateCodeStructure privateCodeStruct = netexFactory.createPrivateCodeStructure();
					privateCodeStruct.setValue(containedStopArea.getRegistrationNumber());
					quay.setPrivateCode(privateCodeStruct);
				}

				if (containedStopArea.hasCoordinates()) {
					SimplePoint_VersionStructure pointStruct = netexFactory.createSimplePoint_VersionStructure();
					LocationStructure locationStruct = netexFactory.createLocationStructure().withSrsName(DEFAULT_COORDINATE_SYSTEM);

					if (containedStopArea.hasCoordinates()) {
						locationStruct.setLatitude(containedStopArea.getLatitude());
						locationStruct.setLongitude(containedStopArea.getLongitude());
					}

					pointStruct.setLocation(locationStruct);
					quay.setCentroid(pointStruct);
				}

				quayStruct.getQuayRefOrQuay().add(quay);
			}

			stopPlace.setQuays(quayStruct);
		}

		NetexObjectUtil.addSharedStopPlace(netexReferential, stopPlace.getId(), stopPlace);
		return stopPlace;
	}

}
