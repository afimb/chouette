package fr.certu.chouette.exchange.netex.exporter;

import fr.certu.chouette.exchange.netex.EnumTranslator;
import java.io.IOException;
import java.util.List;

import fr.certu.chouette.model.neptune.Line;

import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.StopPoint;
import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
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

    private void prepareModel(Line line)
    {
        model.put("enumTranslator", new EnumTranslator());
        model.put("line", line);
        model.put("network", line.getPtNetwork());
        model.put("company", line.getCompany());
        model.put("connectionLinks", line.getConnectionLinks());        
        model.put("accessLinks", line.getAccessLinks());
        model.put("accessPoints", line.getAccessPoints());
        
        // For ServiceFrame need to have for each tariff stop points associated
        model.put("tariffStopPoints", tariffStopPoints(line));        
        
        // For SiteFrame need to have stop areas type StopPlace and CommercialStopPoint only
        List<StopArea> stopAreaWithoutQuays = new ArrayList<StopArea>();
        stopAreaWithoutQuays.addAll( line.getStopPlaces());
        stopAreaWithoutQuays.addAll( line.getCommercialStopPoints());
        model.put("stopPlaces", stopAreaWithoutQuays);
        
        // For TimetableFrame need to have vehicle journeys
        model.put("vehicleJourneys", line.getVehicleJourneys()); 
        
        // For ServiceCalendarFrame need to have time tables
        model.put("timetables", line.getTimetables()); 
                

        model.put("date", new DateTool());
        model.put("esc", new EscapeTool());
        model.put("dateFormat", "yyyy-MM-dd'T'HH:mm:ss'Z'");
        model.put("shortDateFormat", "yyyy-MM-dd");
    }
    
    public ZipEntry writeZipEntry(Line line, String entryName, ZipOutputStream zipFile) throws IOException 
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
        List<StopPoint> stopPoints = line.getStopPoints();        
        
        for (int i = 0; i < stopPoints.size(); i++) {
            StopPoint stopPoint = stopPoints.get(i);
            StopArea physical = stopPoint.getContainedInStopArea();
            
            if( physical != null)
            {                    
                if( physical.getFareCode() != null )
                    addStopPointToTariffStopPoints(physical.getFareCode(), stopPoint, tariffStopPoints);
                else 
                {
                    StopArea commercial = physical.getParent();
                    if( commercial != null && commercial.getFareCode() != null )
                        addStopPointToTariffStopPoints(commercial.getFareCode(), stopPoint, tariffStopPoints);
                }            
            }            
        }
        return tariffStopPoints;
    }
    
    private Map<Integer, List<StopPoint>> addStopPointToTariffStopPoints(Integer fareCode, StopPoint stopPoint, Map<Integer, List<StopPoint>> tariffStopPoints)
    {                                           
        if( !tariffStopPoints.containsKey(fareCode) )
        {
            tariffStopPoints.put(fareCode, new ArrayList<StopPoint>());
            tariffStopPoints.get(fareCode).add(stopPoint);
        }
        else{
            tariffStopPoints.get(fareCode).add(stopPoint);
        }
        
        return tariffStopPoints;        
    }
              
}
