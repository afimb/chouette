package fr.certu.chouette.service.export.geoportail.model;

import java.net.URL;

public class Aot extends GeoportailObject {
    
    private String aotSource;
    private String aotLogoFileName;
    private URL    aotURL;
    private URL    aotURLLegalInformation;
    private String aotLegalInformation;

    public void setAotSource(String aotSource) {
        this.aotSource = aotSource;
    }
    
    public String getAotSource() {
        return aotSource;
    }
    
    public void setAotLogoFileName(String aotLogoFileName) {
        this.aotLogoFileName = aotLogoFileName;
    }
    
    public String getAotLogoFileName() {
        return aotLogoFileName;
    }
    
    public void setAotURL(URL aotURL) {
        this.aotURL = aotURL;
    }
    
    public URL getAotURL() {
        return aotURL;
    }
    
    public void setAotURLLegalInformation(URL aotURLLegalInformation) {
        this.aotURLLegalInformation = aotURLLegalInformation;
    }
    
    public URL getAotURLLegalInformation() {
        return aotURLLegalInformation;
    }
    
    public void setAotLegalInformation(String aotLegalInformation) {
        this.aotLegalInformation = aotLegalInformation;
    }
    
    public String getAotLegalInformation() {
        return aotLegalInformation;
    }
    
    public String getCSVLine() {
        String csvLine = "";
        if (aotSource != null)
            csvLine += aotSource;
        csvLine +=  ",";
        if (aotLogoFileName != null)
            csvLine += aotLogoFileName;
        csvLine +=  ",";
        if (aotURL != null)
            csvLine += aotURL.toString();
        csvLine +=  ",";
        if (aotURLLegalInformation != null)
            csvLine += aotURLLegalInformation.toString();
        csvLine +=  ",";
        if (aotLegalInformation != null)
            csvLine += aotLegalInformation;
        csvLine +=  "\n";
        return csvLine;
    }
}
