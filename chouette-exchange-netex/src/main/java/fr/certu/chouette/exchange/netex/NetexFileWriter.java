package fr.certu.chouette.exchange.netex;

import java.io.IOException;
import java.util.List;
import java.io.OutputStream;

import fr.certu.chouette.model.neptune.Line;

import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.StopPoint;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.ui.velocity.VelocityEngineUtils;

public class NetexFileWriter {

    private static final Logger logger = Logger.getLogger(NetexFileWriter.class);
    @Getter @Setter private VelocityEngine velocityEngine; 
    
    public NetexFileWriter() {
    }

    public void write(List<Line> lines, String filename) {                
        Map model = new HashMap();
        Line line = lines.get(0);
        model.put("line", line);
        model.put("network", line.getPtNetwork());
        model.put("company", line.getCompany());
        model.put("dateFormat", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"));    
        
        // For ServiceFrame need to have for each tariff stop points associated
        model.put("tariffStopPoints", tariffStopPoints(line));
        
        // For SiteFrame need to have stop areas type StopPlace only
        model.put("stopPlaces", line.getStopPlaces());
        
        FileWriter fw = null;
        StringWriter output = new StringWriter();
        VelocityEngineUtils.mergeTemplate(velocityEngine, "templates/line.vm", "UTF-8", model, output);

        File f = new File(filename);
        try {
            FileUtils.write(f, output.toString(), "UTF-8");
        } catch (IOException ex) {
            logger.error(ex);
        }
        
        //et on le ferme
        System.out.println("fichier créé");
    }
    
    private Map<String, ArrayList<StopPoint>> tariffStopPoints(Line line)
    {
        Map<String, ArrayList<StopPoint>> tariffStopPoints = new HashMap<String, ArrayList<StopPoint>>();
        
        return tariffStopPoints;
    }
    

    public void write(List<Line> lines, OutputStream fileOutputStream) {
    }
}
