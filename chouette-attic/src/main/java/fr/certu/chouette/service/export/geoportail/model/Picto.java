package fr.certu.chouette.service.export.geoportail.model;

public class Picto extends GeoportailObject {
    
    private String pictoPointtype; 
    private String pictoFilename;
    private String pictoMinscale;
    private String pictoMaxscale;
    
    public void setPictoPointtype(String pictoPointtype) {
        this.pictoPointtype = pictoPointtype;
    }
    
    public String getPictoPointtype() {
        return pictoPointtype;
    }
    
    public void setPictoFilename(String pictoFilename) {
        this.pictoFilename = pictoFilename;
    }
    
    public String getPictoFilename() {
        return pictoFilename;
    }
    
    public void setPictoMinscale(String pictoMinscale) {
        this.pictoMinscale = pictoMinscale;
    }
    
    public String getPictoMinscale() {
        return pictoMinscale;
    }
    
    public void setPictoMaxscale(String pictoMaxscale) {
        this.pictoMaxscale = pictoMaxscale;
    }
    
    public String getPictoMaxscale() {
        return pictoMaxscale;
    }
    
    public String getCSVLine() {
        String csvLine = "";
        if (pictoPointtype != null)
            csvLine += pictoPointtype;
        csvLine +=  ",";
        if (pictoFilename != null)
            csvLine += pictoFilename;
        csvLine +=  ",";
        if (pictoMinscale != null)
            csvLine += pictoMinscale;
        csvLine +=  ",";
        if (pictoMaxscale != null)
            csvLine += pictoMaxscale;
        csvLine +=  "\n";
        return csvLine;
    }
}
