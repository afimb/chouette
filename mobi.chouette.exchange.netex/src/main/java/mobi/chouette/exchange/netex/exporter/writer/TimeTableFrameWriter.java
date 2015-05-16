package mobi.chouette.exchange.netex.exporter.writer;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import mobi.chouette.exchange.netex.exporter.ExportableData;
import mobi.chouette.exchange.netex.exporter.ModelTranslator;
import mobi.chouette.model.Line;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.VehicleJourneyAtStop;

public class TimeTableFrameWriter extends AbstractWriter{
	

	
	public static void write(Writer writer, ExportableData data ) throws IOException 
	{
		Line line = data.getLine();
		List<VehicleJourney> vehicleJourneys = data.getVehicleJourneys();
		ModelTranslator modelTranslator = new ModelTranslator();
		List<Long> vehicleNumbers = new ArrayList<Long>();

		writer.write("\n");
		writer.write("<!-- =========================================== -->\n");
		writer.write("<TimetableFrame version=\"any\" id=\""+line.objectIdPrefix()+":TimetableFrame:TF02\"> \n");
		writer.write("  <vehicleJourneys>\n");
		//    #foreach( $vehicleJourney in $vehicleJourneys )
		for (VehicleJourney vehicleJourney : vehicleJourneys) {
		writer.write("    <ServiceJourney version=\""+vehicleJourney.getObjectVersion()+"\" id=\""+modelTranslator.netexId(vehicleJourney)+"\">\n");
		//      #if ( $vehicleJourney.publishedJourneyName)
		if (isSet(vehicleJourney.getPublishedJourneyName()))
		writer.write("      <Name>"+toXml(vehicleJourney.getPublishedJourneyName())+"</Name>\n");
		//      #end
		//      #if ( $vehicleJourney.publishedJourneyIdentifier)
		if (isSet(vehicleJourney.getPublishedJourneyIdentifier()))
		writer.write("      <ShortName>"+toXml(vehicleJourney.getPublishedJourneyIdentifier())+"</ShortName>\n");
		//      #end
		//      #if ( $vehicleJourney.comment)
		if (isSet(vehicleJourney.getComment()))
		writer.write("      <Description>"+toXml(vehicleJourney.getComment())+"</Description>\n");
		//      #end
		writer.write("      <!--\n");
		writer.write("      <netex:TransportSubmode>\n");
		writer.write("        <netex:RailSubmode>unknown</netex:RailSubmode>\n");
		writer.write("      </netex:TransportSubmode>\n");
		writer.write("      -->\n");
		writer.write("      <dayTypes>\n");
		//        #foreach( $timetable in $vehicleJourney.timetables )
		for (Timetable timetable : vehicleJourney.getTimetables()) {
		writer.write("        <DayTypeRef version=\""+timetable.getObjectVersion()+"\" ref=\""+modelTranslator.netexId(timetable)+"\"/>\n");
		}
		//        #end 
		writer.write("      </dayTypes>\n");
		//      #if($vehicleJourney.route) 
		writer.write("      <RouteRef version=\""+vehicleJourney.getRoute().getObjectVersion()+"\" ref=\""+modelTranslator.netexId(vehicleJourney.getRoute())+"\"/>\n");
		//      #end
		//      #if($vehicleJourney.journeyPattern) 
		writer.write("      <ServicePatternRef ref=\""+modelTranslator.netexId(vehicleJourney.getJourneyPattern())+"\" />\n");
		//      #end
		//      #if ( $vehicleJourney.company)
		if (isSet(vehicleJourney.getCompany()))
		writer.write("      <OperatorRef version=\""+vehicleJourney.getCompany().getObjectVersion()+"\" ref=\""+modelTranslator.netexId(vehicleJourney.getCompany())+"\" />\n");
		//      #end
		//      #if ( $vehicleJourney.number) 
		if (isSet(vehicleJourney.getNumber())) {
			vehicleNumbers.add(vehicleJourney.getNumber());
		writer.write("      <trainNumbers>\n");
		writer.write("        <TrainNumberRef ref=\""+modelTranslator.trainNumberId(vehicleJourney.getNumber())+"\" />\n");
		writer.write("      </trainNumbers>\n");
		}
		//      #end
		writer.write("      <calls>\n");
		//        #foreach( $vehicleJourneyAtStop in $vehicleJourney.vehicleJourneyAtStops ) 
		for (VehicleJourneyAtStop vehicleJourneyAtStop : vehicleJourney.getVehicleJourneyAtStops()) {
		writer.write("        <Call>\n");
		writer.write("          <ScheduledStopPointRef version=\""+vehicleJourneyAtStop.getStopPoint().getObjectVersion()+"\" ref=\""+modelTranslator.netexId(vehicleJourneyAtStop.getStopPoint())+"\"/>\n");
		writer.write("          <Arrival>\n");
		writer.write("            <Time>"+vehicleJourneyAtStop.getArrivalTime()+"</Time>\n");
		writer.write("          </Arrival>\n");
		writer.write("          <Departure>\n");
		writer.write("            <Time>"+vehicleJourneyAtStop.getDepartureTime()+"</Time>\n");
		writer.write("          </Departure>\n");
		writer.write("        </Call>	\n");	
		}
		//        #end
		writer.write("      </calls>\n");
		writer.write("    </ServiceJourney>\n");
		}
		//    #end
		writer.write("  </vehicleJourneys>\n");
		//  #if ( $vehicleNumbers && $vehicleNumbers.size() > 0 )
		if (nonEmpty(vehicleNumbers)) {
		writer.write("  <trainNumbers>\n");
		//    #foreach( $vehicleNumber in $vehicleNumbers )
		for (Long vehicleNumber : vehicleNumbers) {
		writer.write("    <TrainNumber version=\"1\" id=\""+modelTranslator.trainNumberId(vehicleNumber)+"\">\n");
		writer.write("      <Description>"+vehicleNumber+"</Description>\n");
		writer.write("    </TrainNumber>\n");
		}
		//    #end
		writer.write("  </trainNumbers>\n");
		}
		//  #end
		writer.write("</TimetableFrame>\n");


	}

}
