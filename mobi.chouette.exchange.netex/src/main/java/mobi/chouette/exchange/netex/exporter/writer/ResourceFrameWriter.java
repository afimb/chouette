package mobi.chouette.exchange.netex.exporter.writer;

import java.io.IOException;
import java.io.Writer;

import mobi.chouette.exchange.netex.exporter.ExportableData;
import mobi.chouette.model.Company;
import mobi.chouette.model.Line;
import mobi.chouette.model.Network;

public class ResourceFrameWriter extends AbstractWriter{

	// public static final DatatypeFactory durationFactory =  DatatypeFactory.newInstance();


	public static void write(Writer writer, ExportableData data ) throws IOException 
	{
		Line line = data.getLine();
		Network network = line.getNetwork();
		Company company = line.getCompany();
		writer.write("<!-- =========================================== -->\n");
		writer.write("<!-- Resourceframe to map the Company NEPTUNE Object -->\n");
		writer.write("<ResourceFrame version=\"any\" id=\"Neptune:ResourceFrame:FrameProfile01\">\n");
		writer.write("  <Name>"+line.objectIdPrefix()+"--Neptune-Line Profile </Name>\n");
		writer.write("  <codespaces>\n");
		writer.write("    <Codespace id=\"Neptune\">\n");
		writer.write("      <Xmlns>Neptune</Xmlns>\n");
		writer.write("      <XmlnsUrl>http://www.Neptune.fr/Neptune</XmlnsUrl>\n");
		writer.write("      <Description>Neptune data </Description>\n");
		writer.write("    </Codespace>\n");
		writer.write("  </codespaces>\n");
		writer.write("  <FrameDefaults>\n");
		writer.write("    <DefaultCodespaceRef ref=\"Neptune\"/>\n");
		writer.write("  </FrameDefaults>\n");
		if (isSet(network.getSourceIdentifier(),network.getSourceName(),network.getSourceType()))
		{
			//  #if ( $network.sourceIdentifier || $network.sourceName || $network.sourceType )
			writer.write("  <dataSources>\n");
			writer.write("      <DataSource" + (isSet(network.getSourceIdentifier())? " id=\""+toXml(network.getSourceIdentifier())+"\" version=\"1\" ":"")+
					">\n");
			if (isSet(network.getSourceName()))
				//        #if ( $network.sourceName )
			{
				writer.write("        <Name>"+toXml(network.getSourceName())+"</Name>\n");
			}
			//        #end
			//        #if ( $network.sourceType )
			writer.write("        <Description>"+toXml(network.getSourceType())+"</Description>\n");
			//        #end
			writer.write("      </DataSource>\n");
			writer.write("  </dataSources>\n");
			//  #end
		}
		writer.write("  <typesOfValue>\n");
		writer.write("    <TypeOfValidity version=\"01\" id=\"Neptune:TypeOfValidity:WeeklyPlanned\">\n");
		writer.write("      <Name>Weekly export of Neptune  Planned data</Name>\n");
		writer.write("      <Periodicity>P7D</Periodicity>\n");
		writer.write("      <Nature>planned</Nature>\n");
		writer.write("    </TypeOfValidity>\n");
		writer.write("    <TypeOfFrame version=\"01\" id=\"Neptune:TypeOfFrame:CompositeFrame\">\n");
		writer.write("      <Name>Neptune Profile:  Composite Frame </Name>\n");
		writer.write("      <Description>"+line.objectIdPrefix()+"--Neptune-Line-xxxxxx  frames will be composite frame containing Service, Timetable (and Service Calendar) and Resource frames with all the usual NEPTUNE attributes filled in, for a given line xxxxx.</Description>\n");
		writer.write("      <TypeOfValidityRef version=\"01\" ref=\"Neptune:TypeOfValidity:WeeklyPlanned\"/>\n");
		writer.write("      <FrameClassRef nameOfClass=\"CompositeFrame\"/>\n");
		writer.write("      <classes>\n");
		writer.write("        <ClassInContextRef nameOfClass=\"AvailabilityCondition\" classRefType=\"memberReferences\"/>\n");
		writer.write("      </classes>\n");
		writer.write("      <typesOfFrame>\n");
		writer.write("        <TypeOfFrame version=\"01\" id=\"Neptune:TypeOfFrame:TimeTableFrame\">\n");
		writer.write("          <Name>Neptune Profile:ServiceCalendar  Frame </Name>\n");
		writer.write("          <FrameClassRef nameOfClass=\"TimeTableFrame\"/>\n");
		writer.write("          <classes>\n");
		writer.write("            <ClassInContextRef classRefType=\"members\" nameOfClass=\"TimeTableFrame\"/>\n");
		writer.write("            <ClassInContextRef classRefType=\"members\" nameOfClass=\"ServiceJourney\"/>\n");
		writer.write("            <ClassInContextRef classRefType=\"members\" nameOfClass=\"JourneyPart\"/>\n");
		writer.write("            <ClassInContextRef classRefType=\"members\" nameOfClass=\"JourneyPart\"/>\n");
		writer.write("          </classes>\n");
		writer.write("        </TypeOfFrame>\n");
		writer.write("        <TypeOfFrame version=\"01\" id=\"Neptune:TypeOfFrame:ServiceFrame\">\n");
		writer.write("          <Name>Neptune Profile:  Service  Frame </Name>\n");
		writer.write("          <FrameClassRef nameOfClass=\"ServiceFrame\"/>\n");
		writer.write("          <classes>\n");
		writer.write("            <ClassInContextRef classRefType=\"memberReferences\" nameOfClass=\"ScheduledStopPoint\"/>\n");
		writer.write("            <ClassInContextRef classRefType=\"memberReferences\" nameOfClass=\"ServicePattern\"/>\n");
		writer.write("            <ClassInContextRef classRefType=\"memberReferences\" nameOfClass=\"Network\"/>\n");
		writer.write("            <ClassInContextRef classRefType=\"memberReferences\" nameOfClass=\"RoutePoint\"/>\n");
		writer.write("            <ClassInContextRef classRefType=\"memberReferences\" nameOfClass=\"RouteLink\"/>\n");
		writer.write("            <ClassInContextRef classRefType=\"memberReferences\" nameOfClass=\"Line\"/>\n");
		writer.write("            <ClassInContextRef classRefType=\"memberReferences\" nameOfClass=\"Route\"/>\n");
		writer.write("            <ClassInContextRef classRefType=\"memberReferences\" nameOfClass=\"Direction\"/>\n");
		writer.write("            <ClassInContextRef classRefType=\"memberReferences\" nameOfClass=\"TariffZone\"/>\n");
		writer.write("            <ClassInContextRef classRefType=\"memberReferences\" nameOfClass=\"PassengerStopAssignment\"/>\n");
		writer.write("          </classes>\n");
		writer.write("        </TypeOfFrame>\n");
		writer.write("        <TypeOfFrame version=\"01\" id=\"Neptune:TypeOfFrame:SiteFrame\">\n");
		writer.write("          <Name>Neptune Profile:  Site  Frame </Name>\n");
		writer.write("          <FrameClassRef nameOfClass=\"SiteFrame\"/>\n");
		writer.write("          <classes>\n");
		writer.write("            <ClassInContextRef classRefType=\"memberReferences\" nameOfClass=\"StopPlace\"/>\n");
		writer.write("          </classes>\n");
		writer.write("        </TypeOfFrame>\n");
		writer.write("        <TypeOfFrame version=\"01\" id=\"Neptune:TypeOfFrame:ServiceCalendarFrame\">\n");
		writer.write("          <Name>Neptune Profile:ServceCalendar  Frame </Name>\n");
		writer.write("          <FrameClassRef nameOfClass=\"ServiceCalendarFrame\"/>\n");
		writer.write("          <classes>\n");
		writer.write("            <ClassInContextRef classRefType=\"memberReferences\" nameOfClass=\"ServiceCalendar\"/>\n");
		writer.write("            <ClassInContextRef classRefType=\"memberReferences\" nameOfClass=\"DayType\"/>\n");
		writer.write("            <ClassInContextRef classRefType=\"memberReferences\" nameOfClass=\"DayTypeAssignment\"/>\n");
		writer.write("          </classes>\n");
		writer.write("        </TypeOfFrame>\n");
		writer.write("        <TypeOfFrame version=\"01\" id=\"Neptune:TypeOfFrame:ResourceFrame\">\n");
		writer.write("          <Name>Neptune Profile:  Resource Frame </Name>\n");
		writer.write("          <FrameClassRef nameOfClass=\"ResourceFrame\"/>\n");
		writer.write("          <classes>\n");
		writer.write("            <ClassInContextRef classRefType=\"memberReferences\" nameOfClass=\"TypeOfFrame\"/>\n");
		writer.write("            <ClassInContextRef classRefType=\"memberReferences\" nameOfClass=\"Operator\"/>\n");
		writer.write("            <ClassInContextRef classRefType=\"memberReferences\" nameOfClass=\"Authority\"/>\n");
		writer.write("          </classes>\n");
		writer.write("        </TypeOfFrame>\n");
		writer.write("      </typesOfFrame>\n");
		writer.write("    </TypeOfFrame>\n");
		writer.write("  </typesOfValue>\n");
		writer.write("  <organisations> <!-- TODO : Prendre en compte plusieurs companies -->\n");
		writer.write("    <Operator version=\""+company.getObjectVersion()+"\" id=\""+company.objectIdPrefix()+":Company:"+company.objectIdSuffix()+"\">\n");
		//      #if ( $company.code )
		if (isSet(company.getCode()))
			writer.write("      <PublicCode>"+toXml(company.getCode())+"</PublicCode>\n");
		//      #end
		//      #if ( $company.registrationNumber )
		if (isSet(company.getRegistrationNumber()))
			writer.write("      <CompanyNumber>"+toXml(company.getRegistrationNumber())+"</CompanyNumber>\n");
		//      #end
		//      #if ( $company.name )
		if (isSet(company.getName()))
			writer.write("      <Name>"+toXml(company.getName())+"</Name>\n");
		//      #end
		//      #if ( $company.shortName )
		if (isSet(company.getShortName()))
			writer.write("      <ShortName>"+toXml(company.getShortName())+"</ShortName>\n");
		//      #end
		if (isSet(company.getEmail(),company.getPhone(),company.getFax()))
		{
			//      #if ( $company.email || $company.phone || $company.fax )
			writer.write("      <ContactDetails>\n");
			//          #if ( $company.email )
			if (isSet(company.getEmail()))
				writer.write("          <Email>"+toXml(company.getEmail())+"</Email>\n");
			//         #end
			//          #if ( $company.phone )
			if (isSet(company.getPhone()))
				writer.write("          <Phone>"+toXml(company.getPhone())+"</Phone>\n");
			//         #end
			//          #if ( $company.fax )
			if (isSet(company.getFax()))
				writer.write("          <Fax>"+toXml(company.getFax())+"</Fax>\n");
			//          #end
			writer.write("      </ContactDetails>\n");
			//      #end
		}
		//      #if ( $company.organisationalUnit )
		if (isSet(company.getOrganisationalUnit()))
		{
			writer.write("      <parts>\n");
			writer.write("          <OrganisationPart version=\""+company.getObjectVersion()+"\" id=\""+company.objectIdPrefix()+":OrganisationPart:"+company.objectIdSuffix()+"\">\n");
			writer.write("              <Name>"+toXml(company.getOrganisationalUnit())+"</Name>\n");
			writer.write("          </OrganisationPart>\n");
			writer.write("      </parts>\n");
		}
		//      #end
		//      #if ( $company.operatingDepartmentName )
		if (isSet(company.getOperatingDepartmentName()))
		{
			writer.write("      <departments>\n");
			writer.write("          <Department id=\""+company.objectIdPrefix()+":Department:"+company.objectIdSuffix()+"\" version=\"1\">\n");
			writer.write("              <Name>"+toXml(company.getOperatingDepartmentName())+"</Name>\n");
			writer.write("          </Department>\n");
			writer.write("      </departments>\n");
		}
		//      #end
		writer.write("    </Operator>\n");
		writer.write("  </organisations>\n");
		writer.write("</ResourceFrame>\n");		
	}

}
