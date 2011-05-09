package fr.certu.chouette.service.amivif;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import amivif.schema.Line;
import amivif.schema.RespPTDestrLine;
import amivif.schema.RespPTDestrLineTypeType;
import amivif.schema.RespPTLineStructTimetable;
import amivif.schema.RespPTLineStructTimetableTypeType;
import chouette.schema.AreaCentroid;
import chouette.schema.ChouetteArea;
import chouette.schema.ChouetteLineDescription;
import chouette.schema.ChouettePTNetwork;
import chouette.schema.ChouettePTNetworkTypeType;
import chouette.schema.ChouetteRemoveLineTypeType;
import chouette.schema.StopArea;
import fr.certu.chouette.service.amivif.base.CompanyConverter;
import fr.certu.chouette.service.amivif.base.ConnectionLinkConverter;
import fr.certu.chouette.service.amivif.base.GroupOfLineConverter;
import fr.certu.chouette.service.amivif.base.JourneyPatternConverter;
import fr.certu.chouette.service.amivif.base.LineConverter;
import fr.certu.chouette.service.amivif.base.PTLinkConverter;
import fr.certu.chouette.service.amivif.base.RouteConverter;
import fr.certu.chouette.service.amivif.base.StopAreaConverter;
import fr.certu.chouette.service.amivif.base.StopPointConverter;
import fr.certu.chouette.service.amivif.base.TimetableConverter;
import fr.certu.chouette.service.amivif.base.TransportNetworkConverter;
import fr.certu.chouette.service.amivif.base.VehicleConverter;

public class MainConverter implements IAmivifAdapter {

    private static final Logger logger = Logger.getLogger(MainConverter.class);
    private TransportNetworkConverter transportNetworkConverter = new TransportNetworkConverter();
    private GroupOfLineConverter groupOfLineConverter = new GroupOfLineConverter();
    private CompanyConverter companyConverter = new CompanyConverter();
    private StopPointConverter stopPointConverter = new StopPointConverter();
    private LineConverter lineConverter = new LineConverter();
    private PTLinkConverter pTLinkConverter = new PTLinkConverter();
    private VehicleConverter vehicleConverter = new VehicleConverter();
    private JourneyPatternConverter journeyPatternConverter = new JourneyPatternConverter();
    private RouteConverter routeConverter = new RouteConverter();
    private TimetableConverter timetableConverter = new TimetableConverter();
    private ConnectionLinkConverter connectionConverter = new ConnectionLinkConverter();
    private StopAreaConverter stopAreaConverter = new StopAreaConverter();
    //private StopAdapter 				stopAdapter 				= new StopAdapter();
    private UnfoldingTransformer unfoldingTransformer = new UnfoldingTransformer();
    private FoldingTransformer foldingTransformer = new FoldingTransformer();
    private JourneyPatternDesigner jpDesigner = new JourneyPatternDesigner();
    private UselessIdCleaner cleaner = new UselessIdCleaner();

    /* (non-Javadoc)
     * @see fr.certu.chouette.service.amivif.IAmivifAdapter#getATC(amivif.schema.RespPTLineStructTimetable)
     */
    @Override
    public ChouettePTNetworkTypeType getATC(RespPTLineStructTimetableTypeType amivif) {
        logger.debug("EVOCASTOR --> new ChouettePTNetwork()");
        ChouettePTNetworkTypeType chouette = new ChouettePTNetwork();
        chouette.setPTNetwork(transportNetworkConverter.atc(amivif.getTransportNetwork()));
        for (int i = 0; i < amivif.getGroupOfLineCount(); i++) {
            if (amivif.getGroupOfLine(i) != null) {
                chouette.addGroupOfLine(groupOfLineConverter.atc(amivif.getGroupOfLine(i)));
                break;
            }
        }
        chouette.setCompany(companyConverter.atc(amivif.getCompany()));

        ChouetteLineDescription chouetteLineDescription = new ChouetteLineDescription();

        chouetteLineDescription.setLine(lineConverter.atc(amivif.getLine()));
        chouetteLineDescription.setChouetteRoute(routeConverter.atc(amivif.getRoute(), amivif.getSubLine()));
        chouetteLineDescription.setStopPoint(stopPointConverter.atc(amivif.getStopPoint()));
        chouetteLineDescription.setPtLink(pTLinkConverter.atc(amivif.getPTLink()));
        chouetteLineDescription.setVehicleJourney(vehicleConverter.atc(amivif.getVehicleJourney()));
        chouetteLineDescription.setJourneyPattern(journeyPatternConverter.atc(amivif.getJourneyPattern()));

        chouette.setChouetteLineDescription(chouetteLineDescription);
        chouette.setTimetable(timetableConverter.atc(amivif.getTimetable()));
        chouette.setConnectionLink(connectionConverter.atc(amivif.getConnectionLink()));

        // recopie de ce qui devient les arrets physiques
        // et les zones
        ChouetteArea chouetteArea = new ChouetteArea();
        chouette.setChouetteArea(chouetteArea);

        List<StopArea> areas = new ArrayList<StopArea>();
        areas.addAll(Arrays.asList(stopPointConverter.atcArea(amivif.getStopPoint())));
        areas.addAll(Arrays.asList(stopAreaConverter.atc(amivif.getStopArea())));
        chouetteArea.setStopArea(areas.toArray(new StopArea[0]));

        List<AreaCentroid> centroids = new ArrayList<AreaCentroid>();
        centroids.addAll(Arrays.asList(stopPointConverter.atcPlace(amivif.getStopPoint())));
        centroids.addAll(Arrays.asList(stopAreaConverter.atcPlace(amivif.getStopArea())));
        chouetteArea.setAreaCentroid(centroids.toArray(new AreaCentroid[0]));

        //stopAdapter.setRoutes(Arrays.asList(chouetteLineDescription.getChouetteRoute()));
        //stopAdapter.setJourneys(Arrays.asList(chouetteLineDescription.getJourneyPattern()));
        //stopAdapter.setPtLinks(Arrays.asList(chouetteLineDescription.getPtLink()));
        //stopAdapter.setStopPoints(Arrays.asList(chouetteLineDescription.getStopPoint()));
        //stopAdapter.setVehicles(Arrays.asList(chouetteLineDescription.getVehicleJourney()));

        //stopAdapter.initialiser();

        //chouetteLineDescription.setStopPoint( stopAdapter.getStopPoints().toArray( new StopPoint[ 0]));
        //chouetteLineDescription.setPtLink( stopAdapter.getPtLinks().toArray( new PtLink[ 0]));

        unfoldingTransformer.setLineDescription(chouetteLineDescription);
        unfoldingTransformer.setChouetteArea(chouetteArea);
        unfoldingTransformer.transform();

        if (chouetteLineDescription.getJourneyPatternCount() == 0) {
            jpDesigner.setLineDescription(chouetteLineDescription);
            jpDesigner.transform();
        }
//		stopTransformer.transform2();

        cleaner.clean(chouette);

        return chouette;
    }

    /* (non-Javadoc)
     * @see fr.certu.chouette.service.amivif.IAmivifAdapter#getCTA(chouette.schema.ChouettePTNetwork)
     */
    public RespPTDestrLineTypeType getCTA(ChouetteRemoveLineTypeType chouette) {
        logger.debug("EVOCASTOR --> new RespPTDestrLine()");
        RespPTDestrLineTypeType amivif = new RespPTDestrLine();

        Line line = lineConverter.cta(chouette.getLine());
        amivif.setLine(line);

        return amivif;
    }

    /* (non-Javadoc)
     * @see fr.certu.chouette.service.amivif.IAmivifAdapter#getCTA(chouette.schema.ChouettePTNetwork)
     */
    public RespPTLineStructTimetableTypeType getCTA(ChouettePTNetworkTypeType chouette) {
        logger.debug("EVOCASTOR --> new RespPTLineStructTimetable()");
        RespPTLineStructTimetableTypeType amivif = new RespPTLineStructTimetable();
        amivif.setTransportNetwork(transportNetworkConverter.cta(chouette.getPTNetwork()));
        List<chouette.schema.GroupOfLine> groupOfLines = chouette.getGroupOfLineAsReference();
        if (groupOfLines != null) {
            for (int i = 0; i < groupOfLines.size(); i++) {
                amivif.addGroupOfLine(groupOfLineConverter.cta(chouette.getGroupOfLine(i)));
            }
        }
        amivif.setCompany(companyConverter.cta(chouette.getCompany()));

        //OBLIGATION : chouette.getChouetteLineDescription() != null
        ChouetteLineDescription lineDescription = chouette.getChouetteLineDescription();
        amivif.setLine(lineConverter.cta(lineDescription.getLine()));
        amivif.setSubLine(routeConverter.ctaSubline(lineDescription.getChouetteRoute()));
        amivif.setRoute(routeConverter.cta(lineDescription.getChouetteRoute()));
        amivif.setTimetable(timetableConverter.cta(chouette.getTimetable()));
        amivif.setConnectionLink(connectionConverter.cta(chouette.getConnectionLink()));
        //chouette.getChouetteArea().getStopArea();
        //amivif.setStopArea();

        AccesseurAreaStop accesseurAreaStop = new AccesseurAreaStop();
        accesseurAreaStop.initialiser(chouette);

        amivif.setStopArea(stopAreaConverter.cta(chouette.getChouetteArea().getStopArea(), accesseurAreaStop));
        amivif.setStopPoint(stopPointConverter.cta(lineDescription.getStopPoint(), accesseurAreaStop));
        amivif.setPTLink(pTLinkConverter.cta(lineDescription.getPtLink(), accesseurAreaStop));
        amivif.setJourneyPattern(journeyPatternConverter.cta(lineDescription.getJourneyPattern(), accesseurAreaStop));
        amivif.setVehicleJourney(vehicleConverter.cta(lineDescription.getVehicleJourney(), accesseurAreaStop));

        foldingTransformer.setLineStrut(amivif);
        foldingTransformer.transform();

        return amivif;
    }
}
