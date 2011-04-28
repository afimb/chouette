package fr.certu.chouette.service.export.geoportail.model;

public class ChouetteMetadata extends GeoportailObject {
    
    private String chouetteMetadataLayer;
    private String chouetteMetadataName;
    private String chouetteMetadataAddress;
    private String chouetteMetadataEmail;
    private String chouetteMetadataTelephone;
    private String chouetteMetadataDatestamp;
    private String chouetteMetadataLastupdatestamp;
    private String chouetteMetadataUsernote;
    private String chouetteMetadataMinlongitude;
    private String chouetteMetadataMinlatitude;
    private String chouetteMetadataMaxlongitude;
    private String chouetteMetadataMaxlatitude;
    
    public void setChouetteMetadataLayer(String chouetteMetadataLayer) {
        this.chouetteMetadataLayer = chouetteMetadataLayer;
    } 
    
    public String getChouetteMetadataLayer() {
        return chouetteMetadataLayer;
    }
    
    public void setChouetteMetadataName(String chouetteMetadataName) {
        this.chouetteMetadataName = chouetteMetadataName;
    } 
    
    public String getChouetteMetadataName() {
        return chouetteMetadataName;
    }
    
    public void setChouetteMetadataAddress(String chouetteMetadataAddress) {
        this.chouetteMetadataAddress = chouetteMetadataAddress;
    } 
    
    public String getChouetteMetadataAddress() {
        return chouetteMetadataAddress;
    }
    
    public void setChouetteMetadataEmail(String chouetteMetadataEmail) {
        this.chouetteMetadataEmail = chouetteMetadataEmail;
    } 
    
    public String getChouetteMetadataEmail() {
        return chouetteMetadataEmail;
    }
    
    public void setChouetteMetadataTelephone(String chouetteMetadataTelephone) {
        this.chouetteMetadataTelephone = chouetteMetadataTelephone;
    } 
    
    public String getChouetteMetadataTelephone() {
        return chouetteMetadataTelephone;
    }
    
    public void setChouetteMetadataDatestamp(String chouetteMetadataDatestamp) {
        this.chouetteMetadataDatestamp = chouetteMetadataDatestamp;
    } 
    
    public String getChouetteMetadataDatestamp() {
        return chouetteMetadataDatestamp;
    }
    
    public void setChouetteMetadataLastupdatestamp(String chouetteMetadataLastupdatestamp) {
        this.chouetteMetadataLastupdatestamp = chouetteMetadataLastupdatestamp;
    } 
    
    public String getChouetteMetadataLastupdatestamp() {
        return chouetteMetadataLastupdatestamp;
    }
    
    public void setChouetteMetadataUsernote(String chouetteMetadataUsernote) {
        this.chouetteMetadataUsernote = chouetteMetadataUsernote;
    } 
    
    public String getChouetteMetadataUsernote() {
        return chouetteMetadataUsernote;
    }
    
    public void setChouetteMetadataMinlongitude(String chouetteMetadataMinlongitude) {
        this.chouetteMetadataMinlongitude = chouetteMetadataMinlongitude;
    } 
    
    public String getChouetteMetadataMinlongitude() {
        return chouetteMetadataMinlongitude;
    }
    
    public void setChouetteMetadataMinlatitude(String chouetteMetadataMinlatitude) {
        this.chouetteMetadataMinlatitude = chouetteMetadataMinlatitude;
    } 
    
    public String getChouetteMetadataMinlatitude() {
        return chouetteMetadataMinlatitude;
    }
    
    public void setChouetteMetadataMaxlongitude(String chouetteMetadataMaxlongitude) {
        this.chouetteMetadataMaxlongitude = chouetteMetadataMaxlongitude;
    } 
    
    public String getChouetteMetadataMaxlongitude() {
        return chouetteMetadataMaxlongitude;
    }
    
    public void setChouetteMetadataMaxlatitude(String chouetteMetadataMaxlatitude) {
        this.chouetteMetadataMaxlatitude = chouetteMetadataMaxlatitude;
    } 
    
    public String getChouetteMetadataMaxlatitude() {
        return chouetteMetadataMaxlatitude;
    }
    
    public String getCSVLine() {
        String csvLine = "";
        if (chouetteMetadataLayer != null)
            csvLine += chouetteMetadataLayer;
        csvLine +=  ",";
        if (chouetteMetadataName != null)
            csvLine += chouetteMetadataName;
        csvLine +=  ",";
        if (chouetteMetadataAddress != null)
            csvLine += chouetteMetadataAddress;
        csvLine +=  ",";
        if (chouetteMetadataEmail != null)
            csvLine += chouetteMetadataEmail;
        csvLine +=  ",";
        if (chouetteMetadataTelephone != null)
            csvLine += chouetteMetadataTelephone;
        csvLine +=  ",";
        if (chouetteMetadataDatestamp != null)
            csvLine += chouetteMetadataDatestamp;
        csvLine +=  ",";
        if (chouetteMetadataLastupdatestamp != null)
            csvLine += chouetteMetadataLastupdatestamp;
        csvLine +=  ",";
        if (chouetteMetadataUsernote != null)
            csvLine += chouetteMetadataUsernote;
        csvLine +=  ",";
        if (chouetteMetadataMinlongitude != null)
            csvLine += chouetteMetadataMinlongitude;
        csvLine +=  ",";
        if (chouetteMetadataMinlatitude != null)
            csvLine += chouetteMetadataMinlatitude;
        csvLine +=  ",";
        if (chouetteMetadataMaxlongitude != null)
            csvLine += chouetteMetadataMaxlongitude;
        csvLine +=  ",";
        if (chouetteMetadataMaxlatitude != null)
            csvLine += chouetteMetadataMaxlatitude;
        csvLine +=  "\n";
        return csvLine;
    }
}
