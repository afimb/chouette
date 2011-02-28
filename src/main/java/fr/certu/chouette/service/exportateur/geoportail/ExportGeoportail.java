/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.certu.chouette.service.exportateur.geoportail;

import fr.certu.chouette.modele.PositionGeographique;
import fr.certu.chouette.modele.Reseau;
import fr.certu.chouette.modele.Transporteur;
import fr.certu.chouette.service.database.IPositionGeographiqueManager;
import fr.certu.chouette.service.database.IReseauManager;
import fr.certu.chouette.service.database.ITransporteurManager;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author zbouziane
 */
public class ExportGeoportail {
    
    private IPositionGeographiqueManager positionGeographiqueManager;
    private IReseauManager               reseauManager;
    private ITransporteurManager         transporteurManager;
    private BigDecimal                  minlatitude;
    private BigDecimal                  maxlatitude;
    private BigDecimal                  minlongitude;
    private BigDecimal                  maxlongitude;
    private List<String[]>              tc_points;
    private List<String[]>              pictos;
    private List<String[]>              chouette_metadata;
    private List<String[]>              aot;
    
    public void export() {
        tc_points         = new ArrayList<String[]>();
        pictos            = new ArrayList<String[]>();
        chouette_metadata = new ArrayList<String[]>();
        aot               = new ArrayList<String[]>();
        readTcPoints();
        readPictos();
        readChouetteMetadata();
        readAOT();
    }
    
    private void readTcPoints() {
        List<PositionGeographique> stopareas = positionGeographiqueManager.lireArretsPhysiques();
        stopareas.addAll(positionGeographiqueManager.lireZones());
        String[] textLine = new String[19];
        textLine[ 0] = "objectid";
        textLine[ 1] = "geom.longitude";
        textLine[ 2] = "geom.latitude";
        textLine[ 3] = "registrationnumber";
        textLine[ 4] = "name";
        textLine[ 5] = "tcpointtype";
        textLine[ 6] = "fullname";
        textLine[ 7] = "comment";
        textLine[ 8] = "countrycode";
        textLine[ 9] = "creationtime";
        textLine[10] = "creatorid";
        textLine[11] = "longlattype";
        textLine[12] = "longitude";
        textLine[13] = "latitude";
        textLine[14] = "projectiontype";
        textLine[15] = "x";
        textLine[16] = "y";
        textLine[17] = "objectversion";
        textLine[18] = "streetname";
        tc_points.add(textLine);
        for (PositionGeographique stoparea : stopareas) {
            if (minlatitude == null)
                minlatitude = stoparea.getLatitude();
            else if (stoparea.getLatitude().compareTo(minlatitude) < 0)
                minlatitude = stoparea.getLatitude();
            if (maxlatitude == null)
                maxlatitude = stoparea.getLatitude();
            else if (stoparea.getLatitude().compareTo(maxlatitude) > 0)
                maxlatitude = stoparea.getLatitude();
            if (minlongitude == null)
                minlongitude = stoparea.getLongitude();
            else if (stoparea.getLongitude().compareTo(minlongitude) < 0)
                minlongitude = stoparea.getLongitude();
            if (maxlongitude == null)
                maxlongitude = stoparea.getLongitude();
            else if (stoparea.getLongitude().compareTo(minlatitude) > 0)
                maxlongitude = stoparea.getLongitude();
            textLine = new String[19];
            textLine[ 0] = stoparea.getObjectId();
            textLine[ 1] = stoparea.getLongitude().toString();
            textLine[ 2] = stoparea.getLatitude().toString();
            textLine[ 3] = stoparea.getRegistrationNumber();
            textLine[ 4] = stoparea.getName();
            textLine[ 5] = stoparea.getAreaType().toString();
            textLine[ 6] = stoparea.getFullName();
            textLine[ 7] = stoparea.getComment();
            textLine[ 8] = stoparea.getCountryCode();
            textLine[ 9] = stoparea.getCreationTime().toString();
            textLine[10] = stoparea.getCreatorId();
            textLine[11] = stoparea.getLongLatType().toString();
            textLine[12] = stoparea.getLongitude().toString();
            textLine[13] = stoparea.getLatitude().toString();
            textLine[14] = stoparea.getProjectionType();
            textLine[15] = stoparea.getX().toString();
            textLine[16] = stoparea.getY().toString();
            textLine[17] = String.valueOf(stoparea.getObjectVersion());
            textLine[18] = stoparea.getStreetName();
            tc_points.add(textLine);
        }
    }

    private void readPictos() {
        String[] textLine1 = new String[4];
        textLine1[ 0] = "pointtype";
        textLine1[ 1] = "picto";
        textLine1[ 2] = "minscale";
        textLine1[ 3] = "maxscale";
        pictos.add(textLine1);
        String[] textLine2 = new String[3];
        textLine2[ 0] = "Quay";
        textLine2[ 1] = "quai-50.png";
        textLine2[ 2] = "2000";
        pictos.add(textLine2);
        String[] textLine3 = new String[3];
        textLine3[ 0] = "BoardingPosition";
        textLine3[ 1] = "point-embarquement-50.png";
        textLine3[ 2] = "2000";
        pictos.add(textLine3);
        String[] textLine4 = new String[4];
        textLine4[ 0] = "CommercialStopPoint";
        textLine4[ 1] = "zonecommerciale-50.png";
        textLine4[ 2] = "16000";
        textLine4[ 3] = "4000";
        pictos.add(textLine4);
        String[] textLine5 = new String[4];
        textLine5[ 0] = "StopPlace";
        textLine5[ 1] = "pole-echange-50.png";
        textLine5[ 2] = "16000";
        textLine5[ 3] = "4000";
        pictos.add(textLine5);
        String[] textLine6 = new String[3];
        textLine6[ 0] = "AccessPoint";
        textLine6[ 1] = "access-50.png";
        textLine6[ 2] = "1000";
        pictos.add(textLine6);
    }

    private void readChouetteMetadata() {
        List<Reseau> reseaux = reseauManager.lire();
        String[] textLine1 = new String[12];
        textLine1[ 0] = "layer";
        textLine1[ 1] = "name";
        textLine1[ 2] = "address";
        textLine1[ 3] = "email";
        textLine1[ 4] = "telephone";
        textLine1[ 5] = "datestamp";
        textLine1[ 6] = "lastupdatestamp";
        textLine1[ 7] = "usernote";
        textLine1[ 8] = "minlongitude";
        textLine1[ 9] = "minlatitude";
        textLine1[10] = "maxlongitude";
        textLine1[11] = "maxlatitude";
        chouette_metadata.add(textLine1);
        for (Reseau reseau : reseaux) {
            String[] textLine2 = new String[12];
            textLine2[ 0] = "stoparea";
            textLine2[ 1] = reseau.getName();
            textLine2[ 2] = "";
            textLine2[ 3] = "";
            textLine2[ 4] = "";
            textLine2[ 5] = reseau.getCreationTime().toString();
            textLine2[ 6] = reseau.getVersionDate().toString();
            textLine2[ 7] = "";
            textLine2[ 8] = minlongitude.toString();
            textLine2[ 9] = minlatitude.toString();
            textLine2[10] = maxlongitude.toString();
            textLine2[11] = maxlatitude.toString();
            chouette_metadata.add(textLine2);
        }
    }

    private void readAOT() {
        List<Transporteur> transporteurs = transporteurManager.lire();
        String[] textLine = new String[5];
        textLine[0] = "source";
        textLine[1] = "logo";
        textLine[2] = "url";
        textLine[3] = "urllegalinformation";
        textLine[4] = "legalinformation";
        aot.add(textLine);
        for (Transporteur transporteur : transporteurs) {
            textLine = new String[5];
            textLine[0] = transporteur.getShortName();
            textLine[1] = "transporteur.jpg";
            textLine[2] = "";
            textLine[3] = "";
            textLine[4] = transporteur.getName();
            aot.add(textLine);
        }
    }

    public void setPositionGeographiqueManager(IPositionGeographiqueManager positionGeographiqueManager) {
        this.positionGeographiqueManager = positionGeographiqueManager;
    }

    public IPositionGeographiqueManager getPositionGeographiqueManager() {
        return positionGeographiqueManager;
    }
    
    public void setReseauManager(IReseauManager reseauManager) {
        this.reseauManager = reseauManager;
    }
    
    public IReseauManager getReseauManager() {
        return reseauManager;
    }
    
    public void setTransporteurManager(ITransporteurManager transporteurManager) {
        this.transporteurManager = transporteurManager;
    }
    
    public ITransporteurManager getTransporteurManager() {
        return transporteurManager;
    }
}
