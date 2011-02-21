package fr.certu.chouette.exchange.xml.neptune.exporter;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import lombok.Setter;

import org.apache.log4j.Logger;

import chouette.schema.ChouetteArea;
import chouette.schema.ChouetteLineDescription;
import chouette.schema.ChouettePTNetwork;
import chouette.schema.ChouettePTNetworkTypeType;
import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.exchange.xml.neptune.exporter.producer.AreaCentroidProducer;
import fr.certu.chouette.exchange.xml.neptune.exporter.producer.CompanyProducer;
import fr.certu.chouette.exchange.xml.neptune.exporter.producer.JourneyPatternProducer;
import fr.certu.chouette.exchange.xml.neptune.exporter.producer.LineProducer;
import fr.certu.chouette.exchange.xml.neptune.exporter.producer.PTLinkProducer;
import fr.certu.chouette.exchange.xml.neptune.exporter.producer.PTNetworkProducer;
import fr.certu.chouette.exchange.xml.neptune.exporter.producer.RouteProducer;
import fr.certu.chouette.exchange.xml.neptune.exporter.producer.StopAreaProducer;
import fr.certu.chouette.exchange.xml.neptune.exporter.producer.StopPointProducer;
import fr.certu.chouette.exchange.xml.neptune.exporter.producer.VehicleJourneyProducer;
import fr.certu.chouette.model.neptune.AreaCentroid;
import fr.certu.chouette.model.neptune.Company;
import fr.certu.chouette.model.neptune.JourneyPattern;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.PTLink;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.model.neptune.VehicleJourney;
import fr.certu.chouette.plugin.exchange.FormatDescription;
import fr.certu.chouette.plugin.exchange.IExportPlugin;
import fr.certu.chouette.plugin.exchange.ParameterDescription;
import fr.certu.chouette.plugin.exchange.ParameterValue;
import fr.certu.chouette.plugin.exchange.SimpleParameterValue;
import fr.certu.chouette.plugin.report.ReportHolder;

public class XMLNeptuneExportLinePlugin implements IExportPlugin<Line> {

	private static final Logger logger = Logger.getLogger(XMLNeptuneExportLinePlugin.class);

	private FormatDescription description;
	@Setter private LineProducer lineProducer;
	@Setter private PTNetworkProducer networkProducer;
	@Setter private RouteProducer routeProducer;
	@Setter private JourneyPatternProducer journeyPatternProducer;
	@Setter private VehicleJourneyProducer vehicleJourneyProducer;
	@Setter private StopPointProducer stopPointProducer;
	@Setter private PTLinkProducer ptLinkProducer;
	@Setter private CompanyProducer companyProducer;
	@Setter private StopAreaProducer stopAreaProducer;
	@Setter private AreaCentroidProducer areaCentroidProducer;

	
	public XMLNeptuneExportLinePlugin() {
		description = new FormatDescription();
		description.setName("XMLNeptuneLine");
		List<ParameterDescription> params = new ArrayList<ParameterDescription>();
		ParameterDescription param1 = new ParameterDescription("outputFile", ParameterDescription.TYPE.FILENAME, false, true);
		param1.setAllowedExtensions(Arrays.asList(new String[]{"xml","zip"}));
		params.add(param1);
		description.setParameterDescriptions(params);
	}
	
	@Override
	public FormatDescription getDescription() {
		return description;
	}

	@Override
	public void doExport(List<Line> beans, List<ParameterValue> parameters,
			ReportHolder report) throws ChouetteException {
		String fileName = null;
		
		if(beans == null){
			throw new IllegalArgumentException("no beans to export");
		}
			
		for (ParameterValue value : parameters) 
		{
			if (value instanceof SimpleParameterValue)
			{
				SimpleParameterValue svalue = (SimpleParameterValue) value;
				if (svalue.getName().equals("outputFile"))
				{
					fileName = svalue.getFilenameValue();
				}

			}
		}
		if (fileName == null) 
		{
			throw new IllegalArgumentException("outputFile required");
		}
		
		String fileExtension = fileName.substring(fileName.lastIndexOf('.')+1).toLowerCase();
		
		if(beans.size() > 1 && fileExtension.equals("xml")){
			throw new IllegalArgumentException("cannot export multiple lines in one XML file");
		}
		
		File outputFile = new File(fileName);
		
		
		
		if(fileExtension.equals("xml")){
			ChouettePTNetworkTypeType rootObject = exportLine(beans.get(0));
			NeptuneFileWriter neptuneFileWriter = new NeptuneFileWriter();
			neptuneFileWriter.write(rootObject, outputFile);
		}else{
			//TODO : implement solution for multiple beans
		}
	}

	private ChouettePTNetworkTypeType exportLine(Line line) {
		ChouettePTNetwork rootObject = new ChouettePTNetwork();
		
		if(line != null){
			if(line.getPtNetwork() != null){
				rootObject.setPTNetwork(networkProducer.produce(line.getPtNetwork()));
			}
			
			//rootObject.setCompany();
			
			//ChouetteArea chouetteArea = new ChouetteArea();
			//chouetteArea.setAreaCentroid();
			//chouetteArea.setStopArea();
			//rootObject.setChouetteArea(chouetteArea);
			
			ChouetteLineDescription chouetteLineDescription = new ChouetteLineDescription();
			chouette.schema.Line castorLine = lineProducer.produce(line);
			chouetteLineDescription.setLine(castorLine);
			
			HashSet<JourneyPattern> journeyPatterns = new HashSet<JourneyPattern>();
			HashSet<PTLink> ptLinks = new HashSet<PTLink>();
			for(Route route : line.getRoutes()){
				chouetteLineDescription.addChouetteRoute(routeProducer.produce(route));
				if(route.getJourneyPatterns() != null){
					journeyPatterns.addAll(route.getJourneyPatterns());
				}
				if(route.getPtLinks() != null){
					ptLinks.addAll(route.getPtLinks());
				}
			}
			
			HashSet<VehicleJourney> vehicleJourneys = new HashSet<VehicleJourney>();
			HashSet<StopPoint> stopPoints = new HashSet<StopPoint>();
			for(JourneyPattern journeyPattern : journeyPatterns){
				chouetteLineDescription.addJourneyPattern(journeyPatternProducer.produce(journeyPattern));
				if(journeyPattern.getVehicleJourneys() != null){
					vehicleJourneys.addAll(journeyPattern.getVehicleJourneys());
				}
				if(journeyPattern.getStopPoints() != null){
					stopPoints.addAll(journeyPattern.getStopPoints());
				}
			}
			
			HashSet<Company> companies = new HashSet<Company>();
			for(VehicleJourney vehicleJourney : vehicleJourneys){
				chouetteLineDescription.addVehicleJourney(vehicleJourneyProducer.produce(vehicleJourney));
				if(vehicleJourney.getCompany() != null){
					companies.add(vehicleJourney.getCompany());
				}
			}
			
			HashSet<StopArea> stopAreas = new HashSet<StopArea>();
			for(StopPoint stopPoint : stopPoints){
				chouetteLineDescription.addStopPoint(stopPointProducer.produce(stopPoint));
				stopAreas.addAll(extractStopAreaHierarchy(stopPoint.getContainedInStopArea()));
			}
			
			for(PTLink ptLink : ptLinks){
				chouetteLineDescription.addPtLink(ptLinkProducer.produce(ptLink));
			}
			
			for(Company company : companies){
				rootObject.addCompany(companyProducer.produce(company));
			}
			
			ChouetteArea chouetteArea = new ChouetteArea();
			HashSet<AreaCentroid> areaCentroids = new HashSet<AreaCentroid>();
			for(StopArea stopArea : stopAreas){
				chouetteArea.addStopArea(stopAreaProducer.produce(stopArea));
				if(stopArea.getAreaCentroid() != null){
					areaCentroids.add(stopArea.getAreaCentroid());
				}
			}
			
			for(AreaCentroid areaCentroid : areaCentroids){
				chouetteArea.addAreaCentroid(areaCentroidProducer.produce(areaCentroid));
			}
			
			rootObject.setChouetteArea(chouetteArea);
			
			rootObject.setChouetteLineDescription(chouetteLineDescription);
		}
		
		return rootObject;
	}

	private List<StopArea> extractStopAreaHierarchy(StopArea stopArea){
		List<StopArea> stopAreas = new ArrayList<StopArea>();
		if(stopArea!= null){
			stopAreas.add(stopArea);
			StopArea parent = stopArea.getParentStopArea();
			while (parent != null) {
				stopAreas.add(parent);
				parent = parent.getParentStopArea();
			}
		}
		
		return stopAreas;
	}
}
