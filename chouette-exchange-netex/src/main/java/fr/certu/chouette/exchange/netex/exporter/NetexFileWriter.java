package fr.certu.chouette.exchange.netex.exporter;

import fr.certu.chouette.exchange.netex.ModelTranslator;
import java.io.IOException;
import java.util.List;

import fr.certu.chouette.model.neptune.Line;

import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.model.neptune.VehicleJourney;
import fr.certu.chouette.model.neptune.type.ChouetteAreaEnum;
import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.tools.generic.DateTool;
import org.apache.velocity.tools.generic.EscapeTool;
import org.springframework.ui.velocity.VelocityEngineUtils;

public class NetexFileWriter {

    private static final Logger logger = Logger.getLogger(NetexFileWriter.class);
    @Getter @Setter private VelocityEngine velocityEngine; 
    // Prepare the model for velocity
    private Map model = new HashMap();
    
    public NetexFileWriter() {        
    }

    private void prepareModel(Line line) throws DatatypeConfigurationException
    {        
        model.put("date", new DateTool());
        model.put("esc", new EscapeTool());
        model.put("dateFormat", "yyyy-MM-dd'T'HH:mm:ss'Z'");
        model.put("shortDateFormat", "yyyy-MM-dd");
        model.put("dateTimeFormat", "yyyy-MM-dd'T'HH:mm:ss");
        model.put("durationFactory", DatatypeFactory.newInstance());
        
        model.put("modelTranslator", new ModelTranslator());

        model.put("line", line);
        model.put("network", line.getPtNetwork());
        model.put("company", line.getCompany());
        model.put("connectionLinks", line.getConnectionLinks());        
        model.put("accessLinks", line.getAccessLinks());
        
        // For ServiceFrame need to have for each tariff stop points associated
        model.put("tariffs", tariffs(line));       
        
        // For TimetableFrame need to have for trainNumbers
        model.put("vehicleNumbers", vehicleNumbers(line));        
        
        // Be careful line return attributes address
        List<StopArea> stopAreaWithoutQuays = new ArrayList<StopArea>();
        stopAreaWithoutQuays.addAll( line.getStopPlaces());
        stopAreaWithoutQuays.addAll( line.getCommercialStopPoints());
        model.put("stopPlaces", stopAreaWithoutQuays);
        
        // For ITL
        model.put("routingConstraints", line.getRoutingConstraints());
        
        // For TimetableFrame need to have vehicle journeys
        model.put("vehicleJourneys", line.getVehicleJourneys()); 
        
        // For ServiceCalendarFrame need to have time tables
        model.put("timetables", line.getTimetables());                 
    }
    
    public ZipEntry writeZipEntry(Line line, String entryName, ZipOutputStream zipFile) throws IOException, DatatypeConfigurationException 
    {              
        // Prepare the model for velocity
        prepareModel(line);
                
        StringWriter output = new StringWriter();
        //VelocityEngineUtils.mergeTemplate(velocityEngine, "templates/line.vm", "UTF-8", model, output);
        
        VelocityContext velocityContext = new VelocityContext(model);
        velocityContext.put("esc", new EscapeTool());

        velocityEngine.mergeTemplate( "templates/line.vm", "UTF-8", velocityContext, output);
                
        logger.info("exporting " + line.getName() + " (" + line.getObjectId() + ")");

        // Add ZIP entry to zipFileput stream.
        ZipEntry entry = new ZipEntry(entryName);
        zipFile.putNextEntry(entry);

        zipFile.write(output.toString().getBytes("UTF-8"));

        // Complete the entry
        zipFile.closeEntry();
        
        return entry;
    }
    
    public File writeXmlFile(Line line, String filename) throws IOException, DatatypeConfigurationException 
    {                                    
        // Prepare the model for velocity
        prepareModel(line);       
        
        StringWriter output = new StringWriter();
        VelocityEngineUtils.mergeTemplate(velocityEngine, "templates/line.vm", "UTF-8", model, output);

        File file = new File(filename);      
        FileUtils.write(file, output.toString(), "UTF-8");        
                
        logger.debug("File : " + filename  + "created");
        
        return file;
    }
    
    private List<Long> vehicleNumbers( Line line) {
        List<Long> result = new ArrayList<Long>();
        
        List<VehicleJourney> vehicles = line.getVehicleJourneys();
        for ( VehicleJourney vehicle : vehicles) {
            if ( !result.contains( vehicle.getNumber())) {
                result.add( vehicle.getNumber());
            }
        }
        return result;
    }
    
    private List<Integer> tariffs(Line line)
    {
        List<Integer> tariffs = new ArrayList<Integer>();
        
        // Be careful line return attributes address
        List<StopArea> stopAreas = new ArrayList<StopArea>(); 
        stopAreas.addAll( line.getCommercialStopPoints());
        stopAreas.addAll( line.getQuays());
        stopAreas.addAll( line.getBoardingPositions());
        
        for (int i = 0; i < stopAreas.size(); i++) {
            StopArea stopArea = stopAreas.get(i);
            if ( stopArea.getFareCode()!=null && !tariffs.contains( stopArea.getFareCode()))
                tariffs.add( stopArea.getFareCode());
        }
        return tariffs;
    }
    
}
