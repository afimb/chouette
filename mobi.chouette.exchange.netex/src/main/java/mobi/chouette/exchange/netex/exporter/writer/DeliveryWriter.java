package mobi.chouette.exchange.netex.exporter.writer;

import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.xml.datatype.DatatypeConfigurationException;

import mobi.chouette.exchange.netex.exporter.ExportableData;
import mobi.chouette.model.Line;

public class DeliveryWriter extends AbstractWriter{
	
	public static void write(Writer writer, ExportableData data ) throws IOException, DatatypeConfigurationException 
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		Line line = data.getLine();
		Calendar now = Calendar.getInstance();
		writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		writer.write("<!-- \n");
		writer.write("This mapping involves :\n");
		writer.write("    ResourceFrame\n");
		writer.write("	* organisations\n");
		writer.write("	\n");
		writer.write("	ServiceFrame\n");
		writer.write("	* Network\n");
		writer.write("	* directions\n");
		writer.write("	* routePoints\n");
		writer.write("	* routeLinks\n");
		writer.write("	* routes\n");
		writer.write("	* lines\n");
		writer.write("	* scheduledStopPoints\n");
		writer.write("	* servicePatterns\n");
		writer.write("	* tariffZones\n");
		writer.write("	* stopAssignments\n");
		writer.write("	\n");
		writer.write("	SiteFrame\n");
		writer.write("	* stopPlaces\n");
		writer.write("	\n");
		writer.write("	ServiceCalendarFrame\n");
		writer.write("	* dayTypes\n");
		writer.write("	* dayTypeAssignments\n");
		writer.write("	\n");
		writer.write("	TimetableFrame\n");
		writer.write("	* vehicleJourneys\n");
		writer.write("	\n");
		writer.write("This higly commented XML file is 2111 lines long and 87 Ko, where the uncommented original NEPTUNE file is 1165 lines and 42 Ko.\n");
		writer.write("When compressed the NeTEx file is 8 Ko and NEPTUNE 4 Ko\n");
		writer.write("So the NeTEx File will probably be something like 50% bigger with the same level of comment.\n");
		writer.write("\n");
		writer.write("-->\n");
		writer.write("<PublicationDelivery version=\"1.0\" xmlns=\"http://www.netex.org.uk/netex\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"  " +
				"xsi:schemaLocation=\"http://www.netex.org.uk/netex ../../../xsd/NeTEx_publication.xsd\" xmlns:acsb=\"http://www.ifopt.org.uk/acsb\" " +
				"xmlns:ifopt=\"http://www.ifopt.org.uk/ifopt\" xmlns:gml=\"http://www.opengis.net/gml/3.2\" xmlns:siri=\"http://www.siri.org.uk/siri\"  >\n");
		writer.write("  <PublicationTimestamp>"+dateFormat.format(now.getTime())+"</PublicationTimestamp>\n");
		writer.write("  <ParticipantRef>SYS001</ParticipantRef>\n");
		writer.write("  <!--- ======WHAT WAS REQUESTED ========== -->\n");
		writer.write("  <PublicationRequest version=\"1.0\">\n");
		writer.write("    <RequestTimestamp>"+dateFormat.format(now.getTime())+"</RequestTimestamp>\n");
		writer.write("    <ParticipantRef>0</ParticipantRef>\n");
		writer.write("  </PublicationRequest>\n");
		writer.write("  <Description>Line export in Netex Format by Chouette systeme</Description>\n");
		writer.write("  <!--   -->\n");
		writer.write("  <!--- =============== RESULTS =========== -->\n");
		writer.write("  <dataObjects>\n");
		writer.write("    <!-- =========================================== -->    \n");   
		writer.write("    <CompositeFrame version=\"1\" created=\""+dateFormat.format(line.getNetwork().getVersionDate())+"\" " +
				"id=\""+line.objectIdPrefix()+":Neptune:CompositeFrame:"+line.objectIdSuffix()+"\">\n");
		writer.write("      <Name>NEPTUNE Mapping Frame</Name>\n");
		writer.write("      <!-- NEPTUNE [mapping:fixed] : This is a NEPTUNE to NeTEx mapping frame -->\n");
		writer.write("      <TypeOfFrameRef version=\"01\" ref=\"Neptune:TypeOfFrame:CompositeFrame\"/>\n");
		writer.write("      <codespaces>\n");
		writer.write("        <Codespace id=\""+line.objectIdPrefix()+"\">\n");
		writer.write("          <Xmlns>"+line.objectIdPrefix()+"</Xmlns>\n");
		writer.write("        </Codespace>\n");
		writer.write("      </codespaces>\n");
		writer.write("      <FrameDefaults>\n");
		writer.write("        <DefaultCodespaceRef ref=\""+line.objectIdPrefix()+"\"/>\n");
		writer.write("        <!-- NEPTUNE [mapping:fixed] : NEPTUNE is in French ! -->\n");
		writer.write("        <DefaultLocale>\n");
		writer.write("          <TimeZoneOffset>-1</TimeZoneOffset>\n");
		writer.write("          <SummerTimeZoneOffset>-2</SummerTimeZoneOffset>\n");
		writer.write("          <DefaultLanguage>fr</DefaultLanguage>\n");
		writer.write("        </DefaultLocale>\n");
		writer.write("        <!-- NEPTUNE [mapping:fixed] : EPSG:4326 is WGS84 which is the only mandatory location system for NEPTUNE -->\n");
		writer.write("        <DefaultLocationSystem>EPSG:4326</DefaultLocationSystem>\n");
		writer.write("      </FrameDefaults>\n");
		writer.write("      <frames>\n");
//		        #parse( "templates/resource_frame.vm" )
		ResourceFrameWriter.write(writer, data);
//		        #parse( "templates/service_frame.vm" )
		ServiceFrameWriter.write(writer, data);
//		        #parse( "templates/site_frame.vm" )
		SiteFrameWriter.write(writer, data);
//		        #parse( "templates/service_calendar_frame.vm" )
		ServiceCalendarFrameWriter.write(writer, data);
//		        #parse( "templates/time_table_frame.vm" )
		TimeTableFrameWriter.write(writer, data);
		writer.write("      </frames>\n");
		writer.write("    </CompositeFrame>\n");
		writer.write("  </dataObjects>\n");
		writer.write("</PublicationDelivery>\n");

	}

}
