package fr.certu.chouette.service.export.geoportail.model;

public class TCPoint extends GeoportailObject {
    
    private String tCPointGeomLongitude;
    private String tCPointGeomLatitude;
    private String tCPointRegistrationnumber;
    private String tCPointName;
    private String tCPointType;
    private String tCPointFullname;
    private String tCPointComment;
    private String tCPointCountrycode;
    private String tCPointCreationtime;
    private String tCPointCreatorid;
    private String tCPointLonglattype;
    private String tCPointLongitude;
    private String tCPointLatitude;
    private String tCPointProjectiontype;
    private String tCPointX;
    private String tCPointY;
    private String tCPointObjectversion;
    private String tCPointStreetname;
    
    public void setTCPointGeomLongitude(String tCPointGeomLongitude) {
        this.tCPointGeomLongitude = tCPointGeomLongitude;
    }
    
    public String getTCPointGeomLongitude() {
        return tCPointGeomLongitude;
    }
    
    public void setTCPointGeomLatitude(String tCPointGeomLatitude) {
        this.tCPointGeomLatitude = tCPointGeomLatitude;
    }
    
    public String getTCPointGeomLatitude() {
        return tCPointGeomLatitude;
    }
    
    public void setTCPointRegistrationnumber(String tCPointRegistrationnumber) {
        this.tCPointRegistrationnumber = tCPointRegistrationnumber;
    }
    
    public String getTCPointRegistrationnumber() {
        return tCPointRegistrationnumber;
    }
    
    public void setTCPointName(String tCPointName) {
        this.tCPointName = tCPointName;
    }
    
    public String getTCPointName() {
        return tCPointName;
    }
    
    public void setTCPointType(String tCPointType) {
        this.tCPointType = tCPointType;
    }
    
    public String getTCPointType() {
        return tCPointType;
    }
    
    public void setTCPointFullname(String tCPointFullname) {
        this.tCPointFullname = tCPointFullname;
    }
    
    public String getTCPointFullname() {
        return tCPointFullname;
    }
    
    public void setTCPointComment(String tCPointComment) {
        this.tCPointComment = tCPointComment;
    }
    
    public String getTCPointComment() {
        return tCPointComment;
    }
    
    public void setTCPointCountrycode(String tCPointCountrycode) {
        this.tCPointCountrycode = tCPointCountrycode;
    }
    
    public String getTCPointCountrycode() {
        return tCPointCountrycode;
    }
    
    public void setTCPointCreationtime(String tCPointCreationtime) {
        this.tCPointCreationtime = tCPointCreationtime;
    }
    
    public String getTCPointCreationtime() {
        return tCPointCreationtime;
    }
    
    public void setTCPointCreatorid(String tCPointCreatorid) {
        this.tCPointCreatorid = tCPointCreatorid;
    }
    
    public String getTCPointCreatorid() {
        return tCPointCreatorid;
    }
    
    public void setTCPointLonglattype(String tCPointLonglattype) {
        this.tCPointLonglattype = tCPointLonglattype;
    }
    
    public String getTCPointLonglattype() {
        return tCPointLonglattype;
    }
    
    public void setTCPointLongitude(String tCPointLongitude) {
        this.tCPointLongitude = tCPointLongitude;
    }
    
    public String getTCPointLongitude() {
        return tCPointLongitude;
    }
    
    public void setTCPointLatitude(String tCPointLatitude) {
        this.tCPointLatitude = tCPointLatitude;
    }
    
    public String getTCPointLatitude() {
        return tCPointLatitude;
    }
    
    public void setTCPointProjectiontype(String tCPointProjectiontype) {
        this.tCPointProjectiontype = tCPointProjectiontype;
    }
    
    public String getTCPointProjectiontype() {
        return tCPointProjectiontype;
    }
    
    public void setTCPointX(String tCPointX) {
        this.tCPointX = tCPointX;
    }
    
    public String getTCPointX() {
        return tCPointX;
    }
    
    public void setTCPointY(String tCPointY) {
        this.tCPointY = tCPointY;
    }
    
    public String getTCPointY() {
        return tCPointY;
    }
    
    public void setTCPointObjectversion(String tCPointObjectversion) {
        this.tCPointObjectversion = tCPointObjectversion;
    }
    
    public String getTCPointObjectversion() {
        return tCPointObjectversion;
    }
    
    public void setTCPointStreetname(String tCPointStreetname) {
        this.tCPointStreetname = tCPointStreetname;
    }
    
    public String getTCPointStreetname() {
        return tCPointStreetname;
    }
    
    public String getCSVLine() {
        String csvLine = getObjectId()+",";
        if (tCPointGeomLongitude != null)
            csvLine += tCPointGeomLongitude;
        csvLine +=  ",";
        if (tCPointGeomLatitude != null)
            csvLine += tCPointGeomLatitude;
        csvLine +=  ",";
        if (tCPointRegistrationnumber != null)
            csvLine += tCPointRegistrationnumber;
        csvLine +=  ",";
        if (tCPointName != null)
            csvLine += tCPointName;
        csvLine +=  ",";
        if (tCPointType != null)
            csvLine += tCPointType;
        csvLine +=  ",";
        if (tCPointFullname != null)
            csvLine += tCPointFullname;
        csvLine +=  ",";
        if (tCPointComment != null)
            csvLine += tCPointComment;
        csvLine +=  ",";
        if (tCPointCountrycode != null)
            csvLine += tCPointCountrycode;
        csvLine +=  ",";
        if (tCPointCreationtime != null)
            csvLine += tCPointCreationtime;
        csvLine +=  ",";
        if (tCPointCreatorid != null)
            csvLine += tCPointCreatorid;
        csvLine +=  ",";
        if (tCPointLonglattype != null)
            csvLine += tCPointLonglattype;
        csvLine +=  ",";
        if (tCPointLongitude != null)
            csvLine += tCPointLongitude;
        csvLine +=  ",";
        if (tCPointLatitude != null)
            csvLine += tCPointLatitude;
        csvLine +=  ",";
        if (tCPointProjectiontype != null)
            csvLine += tCPointProjectiontype;
        csvLine +=  ",";
        if (tCPointX != null)
            csvLine += tCPointX;
        csvLine +=  ",";
        if (tCPointY != null)
            csvLine += tCPointY;
        csvLine +=  ",";
        if (tCPointObjectversion != null)
            csvLine += tCPointObjectversion;
        csvLine +=  ",";
        if (tCPointStreetname != null)
            csvLine += tCPointStreetname;
        csvLine +=  "\n";
        return csvLine;
    }    
}
