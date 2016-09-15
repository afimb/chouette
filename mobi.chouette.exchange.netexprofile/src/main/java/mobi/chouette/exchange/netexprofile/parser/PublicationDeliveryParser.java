package mobi.chouette.exchange.netexprofile.parser;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.model.Company;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;
import no.rutebanken.netex.model.*;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.List;

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

	private void parseSiteFrame(Context context, Referential referential, PublicationDeliveryStructure lineData, List<PublicationDeliveryStructure> commonData, SiteFrame siteFrame) {
		StopPlacesInFrame_RelStructure stopPlacesStruct = siteFrame.getStopPlaces();
		List<StopPlace> stopPlaces = stopPlacesStruct.getStopPlace();
		for(StopPlace stopPlace : stopPlaces) {
			StopArea s = ObjectFactory.getStopArea(referential, stopPlace.getId());
			s.setName(stopPlace.getName().getValue());
			s.setRegistrationNumber(stopPlace.getShortName().getValue());
		}
	}
	
	

	private void parseServiceFrame(Context context, Referential referential, PublicationDeliveryStructure lineData,
			List<PublicationDeliveryStructure> commonData, ServiceFrame serviceFrame) throws Exception {
		// Parse network
		Network network = serviceFrame.getNetwork();
		mobi.chouette.model.Network ptNetwork = ObjectFactory.getPTNetwork(referential, network.getId());
		ptNetwork.setName(network.getName().getValue());
		
		// Parse route points
		RoutePointsInFrame_RelStructure routePointsStructure = serviceFrame.getRoutePoints();
		context.put(NETEX_LINE_DATA_CONTEXT, routePointsStructure);
		Parser routePointsParser = ParserFactory.create(RoutePointsParser.class.getName());
		routePointsParser.parse(context);

		// Parse routes
		RoutesInFrame_RelStructure routesStructure = serviceFrame.getRoutes();
		context.put(NETEX_LINE_DATA_CONTEXT, routesStructure);
		Parser routesParser = ParserFactory.create(RoutesParser.class.getName());
		routesParser.parse(context);

		// Parse routes
		LinesInFrame_RelStructure linesStructure = serviceFrame.getLines();
		context.put(NETEX_LINE_DATA_CONTEXT, linesStructure);
		Parser linesParser = ParserFactory.create(LinesParser.class.getName());
		linesParser.parse(context);
	}

	private void parseResourceFrame(Context context, Referential referential, PublicationDeliveryStructure lineData,
			List<PublicationDeliveryStructure> commonData, ResourceFrame resourceFrame) throws Exception {
		OrganisationsInFrame_RelStructure organisationsStructure = resourceFrame.getOrganisations();
		context.put(NETEX_LINE_DATA_CONTEXT, organisationsStructure);
		Parser organisationsParser = ParserFactory.create(OrganisationsParser.class.getName());
		organisationsParser.parse(context);
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
