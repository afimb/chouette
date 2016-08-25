package mobi.chouette.exchange.netexprofile.parser;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.model.Company;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;
import no.rutebanken.netex.model.Common_VersionFrameStructure;
import no.rutebanken.netex.model.CompositeFrame;
import no.rutebanken.netex.model.DataManagedObjectStructure;
import no.rutebanken.netex.model.Direction;
import no.rutebanken.netex.model.DirectionsInFrame_RelStructure;
import no.rutebanken.netex.model.Frames_RelStructure;
import no.rutebanken.netex.model.LinkSequence_VersionStructure;
import no.rutebanken.netex.model.Network;
import no.rutebanken.netex.model.OrganisationsInFrame_RelStructure;
import no.rutebanken.netex.model.PublicationDeliveryStructure;
import no.rutebanken.netex.model.ResourceFrame;
import no.rutebanken.netex.model.Route;
import no.rutebanken.netex.model.RoutesInFrame_RelStructure;
import no.rutebanken.netex.model.ServiceCalendarFrame;
import no.rutebanken.netex.model.ServiceFrame;
import no.rutebanken.netex.model.SiteFrame;
import no.rutebanken.netex.model.StopPlace;
import no.rutebanken.netex.model.StopPlacesInFrame_RelStructure;
import no.rutebanken.netex.model.TimetableFrame;

@Log4j
public class PublicationDeliveryParser implements Parser, Constant {

	@Override
	public void parse(Context context) throws Exception {

		// Convert from NETEX java objects to Chouette objects

		PublicationDeliveryStructure lineData = (PublicationDeliveryStructure) context.get(mobi.chouette.exchange.netexprofile.Constant.NETEX_LINE_DATA_JAVA);
		@SuppressWarnings("unchecked")
		List<PublicationDeliveryStructure> commonData = (List<PublicationDeliveryStructure>) context
				.get(mobi.chouette.exchange.netexprofile.Constant.NETEX_COMMON_DATA);

		Referential referential = (Referential) context.get(REFERENTIAL);

		for (Object frame : findFrames(ResourceFrame.class, lineData, commonData)) {
			parseResourceFrame(context, referential, lineData, commonData, (ResourceFrame) frame);
		}

		for (Object frame : findFrames(SiteFrame.class, lineData, commonData)) {
			parseSiteFrame(context, referential, lineData, commonData, (SiteFrame) frame);
		}

		for (Object frame : findFrames(ServiceCalendarFrame.class, lineData, commonData)) {
			parseServiceCalendarFrame(context, referential, lineData, commonData, (ServiceCalendarFrame) frame);
		}

		for (Object frame : findFrames(ServiceFrame.class, lineData, commonData)) {
			parseServiceFrame(context, referential, lineData, commonData, (ServiceFrame) frame);
		}

		for (Object frame : findFrames(TimetableFrame.class, lineData, commonData)) {
			parseTimetableFrame(context, referential, lineData, commonData, (TimetableFrame) frame);
		}

	}

	private List<Object> findFrames(Class class1, PublicationDeliveryStructure lineData, List<PublicationDeliveryStructure> commonData) {

		// TODO generify class

		List<Object> foundFrames = new ArrayList<Object>();
		if (commonData != null) {
			for (PublicationDeliveryStructure common : commonData) {
				foundFrames.addAll(findFrames(class1, common, null));
			}
		}

		for (JAXBElement<? extends Common_VersionFrameStructure> frame : lineData.getDataObjects().getCompositeFrameOrCommonFrame()) {
			if (frame.getValue() instanceof CompositeFrame) {
				CompositeFrame compositeFrame = (CompositeFrame) frame.getValue();
				Frames_RelStructure frames = compositeFrame.getFrames();
				List<JAXBElement<? extends Common_VersionFrameStructure>> commonFrames = frames.getCommonFrame();
				for (JAXBElement<? extends Common_VersionFrameStructure> commonFrame : commonFrames) {
					Common_VersionFrameStructure value = commonFrame.getValue();

					if (value.getClass().equals(class1)) {
						foundFrames.add(value);
					}
				}
			} else if (frame.getValue().equals(class1)) {
				foundFrames.add(frame.getValue());
			}
		}

		return foundFrames;
	}

	private void parseTimetableFrame(Context context, Referential referential, PublicationDeliveryStructure lineData,
			List<PublicationDeliveryStructure> commonData, TimetableFrame frame) {
		// TODO Auto-generated method stub

	}

	private void parseServiceCalendarFrame(Context context, Referential referential, PublicationDeliveryStructure lineData,
			List<PublicationDeliveryStructure> commonData, ServiceCalendarFrame serviceCalendarFrame) {
		// TODO Auto-generated method stub

	}

	private void parseSiteFrame(Context context, Referential referential, PublicationDeliveryStructure lineData, List<PublicationDeliveryStructure> commonData,
			SiteFrame siteFrame) {
		
		StopPlacesInFrame_RelStructure stopPlacesStruct = siteFrame.getStopPlaces();
		List<StopPlace> stopPlaces = stopPlacesStruct.getStopPlace();
		for(StopPlace stopPlace : stopPlaces) {
			StopArea s = ObjectFactory.getStopArea(referential, stopPlace.getId());
			s.setName(stopPlace.getName().getValue());
			s.setRegistrationNumber(stopPlace.getShortName().getValue());
		}
	}
	
	

	private void parseServiceFrame(Context context, Referential referential, PublicationDeliveryStructure lineData,
			List<PublicationDeliveryStructure> commonData, ServiceFrame serviceFrame) {

		// Parse network
		Network network = serviceFrame.getNetwork();
		mobi.chouette.model.Network ptNetwork = ObjectFactory.getPTNetwork(referential, network.getId());
		ptNetwork.setName(network.getName().getValue());
		
		// Parse directions
		DirectionsInFrame_RelStructure directions = serviceFrame.getDirections();
		List<Direction> directionsx = directions.getDirection();
		for(Direction d : directionsx) {
			
		}
		
		
		// Parse routes
		RoutesInFrame_RelStructure routes = serviceFrame.getRoutes();
		List<JAXBElement<? extends LinkSequence_VersionStructure>> route_ = routes.getRoute_();
		for(JAXBElement<? extends LinkSequence_VersionStructure> route : route_) {
			Route r = (Route) route.getValue();
		
			mobi.chouette.model.Route cR = ObjectFactory.getRoute(referential, r.getId());
			cR.setName(r.getName().getValue());
		//	cR.setDi
		
		}
		// TODO Auto-generated method stub

	}

	private void parseResourceFrame(Context context, Referential referential, PublicationDeliveryStructure lineData,
			List<PublicationDeliveryStructure> commonData, ResourceFrame resourceFrame) {

		
		// Instantiate OrganisationsParser
		
		OrganisationsInFrame_RelStructure organisationsStructure = resourceFrame.getOrganisations();
		List<JAXBElement<? extends DataManagedObjectStructure>> organisation_ = organisationsStructure.getOrganisation_();
		for(JAXBElement<? extends DataManagedObjectStructure> org : organisation_) {
			// TODO
			DataManagedObjectStructure value = org.getValue();
			Company c = ObjectFactory.getCompany(referential, value.getId());
		}

	}


	static {
		ParserFactory.register(PublicationDeliveryParser.class.getName(), new ParserFactory() {
			private PublicationDeliveryParser instance = new PublicationDeliveryParser();

			@Override
			protected Parser create() {
				return instance;
			}
		});
	}

}
