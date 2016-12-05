package mobi.chouette.exchange.netexprofile.importer;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.Unmarshaller.Listener;

import org.w3c.dom.Node;

import mobi.chouette.exchange.validation.report.DataLocation;


public class LocationListener extends Listener {
    private Map<Object, DataLocation> locations;
    
    private String filename;

    public LocationListener() {
        this.locations = new HashMap<Object, DataLocation>();
    }

    @Override
    public void beforeUnmarshal(Object target, Object parent) {
        Node p = (Node) parent;
        Integer lineNumber = (Integer) p.getUserData(PositionalXMLReader.LINE_NUMBER_KEY_NAME);
        Integer columnNumber = (Integer) p.getUserData(PositionalXMLReader.COLUMN_NUMBER_KEY_NAME);
        
        DataLocation d = new DataLocation(filename, lineNumber != null ? lineNumber : -1, columnNumber != null? columnNumber : -1);
    	
        locations.put(target,d);
    }

    public DataLocation getLocation(Object o) {
        return locations.get(o);
    }
}
