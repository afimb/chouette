package fr.certu.chouette.exchange.netex;

import java.io.IOException;
import java.util.List;
import java.io.File;
import java.io.OutputStream;

import fr.certu.chouette.model.neptune.Line;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import lombok.Getter;
import lombok.Setter;
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
        logger.error(lines.get(0));
        model.put("line", lines.get(0));
        model.put("network", lines.get(0).getPtNetwork());
        model.put("company", lines.get(0).getCompany());
        model.put("dateFormat", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"));
        
        FileWriter fw = null;
        try {
            fw = new FileWriter(filename);
            
            // le BufferedWriter output auquel on donne comme argument le FileWriter fw cree juste au dessus
            BufferedWriter output = new BufferedWriter(fw);        
            
            VelocityEngineUtils.mergeTemplate(velocityEngine, "templates/line.vm", "UTF-8", model, output);
            
            output.flush();
            //ensuite flush envoie dans le fichier, ne pas oublier cette methode pour le BufferedWriter
            
            output.close();
            //et on le ferme
            System.out.println("fichier créé");
        } catch (IOException ex) {
            logger.error(ex);
        }        
    }

    public void write(List<Line> lines, OutputStream fileOutputStream) {
    }
}
