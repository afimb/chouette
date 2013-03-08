package fr.certu.chouette.exchange.netex.exporter;

import java.io.IOException;
import java.util.List;

import fr.certu.chouette.model.neptune.Line;

import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.StopPoint;
import java.io.File;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.tools.generic.DateTool;
import org.springframework.ui.velocity.VelocityEngineUtils;

public class NetexFileWriter {

    private static final Logger logger = Logger.getLogger(NetexFileWriter.class);
    @Getter @Setter private VelocityEngine velocityEngine; 
    // Prepare the model for velocity
    private Map model = new HashMap();
    
    public NetexFileWriter() {        
    }

    private void prepareModel(Line line)
    {
        model.put("line", line);
        model.put("network", line.getPtNetwork());
        model.put("company", line.getCompany());
        
        // For ServiceFrame need to have for each tariff stop points associated
        model.put("tariffStopPoints", tariffStopPoints(line));
        
        // For SiteFrame need to have stop areas type StopPlace and CommercialStopPoint only
        model.put("stopPlaces", line.getStopPlaces());        
        model.put("commercialStopPoints", line.getCommercialStopPoints()); 
        
        // For TimeTableFrame need to have vehicle journeys
        model.put("vehicleJourneys", line.getVehicleJourneys()); 
        
        model.put("date", new DateTool());
        model.put("dateFormat", "yyyy-MM-d'T'HH:mm:ss'Z'");
    }
    
    public ZipEntry writeZipEntry(Line line, String entryName, ZipOutputStream zipFile) throws IOException 
    {              
        // Prepare the model for velocity
        prepareModel(line);
                
        StringWriter output = new StringWriter();
        VelocityEngineUtils.mergeTemplate(velocityEngine, "templates/line.vm", "UTF-8", model, output);
                
        logger.info("exporting " + line.getName() + " (" + line.getObjectId() + ")");

        // Add ZIP entry to zipFileput stream.
        ZipEntry entry = new ZipEntry(entryName);
        zipFile.putNextEntry(entry);

        zipFile.write(output.toString().getBytes("UTF-8"));

        // Complete the entry
        zipFile.closeEntry();
        
        return entry;
    }
    
    public File writeXmlFile(Line line, String filename) throws IOException 
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
    
    private Map<Integer, List<StopPoint>> tariffStopPoints(Line line)
    {
        Map<Integer, List<StopPoint>> tariffStopPoints = new HashMap<Integer, List<StopPoint>>();
        
        for (int i = 0; i < line.getStopAreas().size(); i++) {
            StopArea stopArea = line.getStopAreas().get(i);
            
            if( !tariffStopPoints.containsKey(stopArea.getFareCode()) )
            {
                tariffStopPoints.put(stopArea.getFareCode(), new ArrayList<StopPoint>());
                tariffStopPoints.get(stopArea.getFareCode()).addAll(stopArea.getContainedStopPoints());
            }
            else{
                tariffStopPoints.get(stopArea.getFareCode()).addAll(stopArea.getContainedStopPoints());
            }
        }
                        
        return tariffStopPoints;
    }
              
}
