package fr.certu.chouette.service.export.geoportail;

import fr.certu.chouette.echange.ILectureEchange;
import fr.certu.chouette.modele.PositionGeographique;
import fr.certu.chouette.modele.Reseau;
import fr.certu.chouette.service.commun.CodeIncident;
import fr.certu.chouette.service.commun.ServiceException;
import fr.certu.chouette.service.export.geoportail.model.Aot;
import fr.certu.chouette.service.export.geoportail.model.ChouetteMetadata;
import fr.certu.chouette.service.export.geoportail.model.Picto;
import fr.certu.chouette.service.export.geoportail.model.TCPoint;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

public class GeoportailFileWriter implements IGeoportailFileWriter {
    
    private static final Logger logger         = Logger.getLogger(fr.certu.chouette.service.export.geoportail.GeoportailFileWriter.class);
    private static final String JEU_CARACTERES = "UTF-8";

    @Override
    public void write(List<ILectureEchange> lecturesEchanges, File _temp, String _nomFichier) {
        FileOutputStream   fileOutputStream   = null;
        OutputStreamWriter outputStreamWriter = null;
        try {
            fileOutputStream   = new FileOutputStream(_temp);
            outputStreamWriter = new OutputStreamWriter(fileOutputStream, JEU_CARACTERES);
            if ("aot".equals(_nomFichier))
                writeAotCSVLines(lecturesEchanges, outputStreamWriter);
            else if ("chouette_metadata".equals(_nomFichier))
                writeChouetteMetadataCSVLines(lecturesEchanges, outputStreamWriter);
            else if ("pictos".equals(_nomFichier))
                writePictosCSVLines(lecturesEchanges, outputStreamWriter);
            else if ("tc_points".equals(_nomFichier))
                writeTCPointsCSVLines(lecturesEchanges, outputStreamWriter);
            outputStreamWriter.close();
            fileOutputStream.close();
        }
        catch(IOException e) {
            throw new ServiceException(CodeIncident.ERR_XML_ECRITURE,  e);
        }
    }

    private void writeAotCSVLines(List<ILectureEchange> lecturesEchanges, OutputStreamWriter outputStreamWriter) throws IOException {
        Collection<Aot> aots = getAots(lecturesEchanges);
        outputStreamWriter.write("source,logo,url,urllegalinformation,legalinformation\n");
        for (Aot aot : aots)
            outputStreamWriter.write(aot.getCSVLine() + "\n");
    }

    private void writeChouetteMetadataCSVLines(List<ILectureEchange> lecturesEchanges, OutputStreamWriter outputStreamWriter) throws IOException {
        Collection<ChouetteMetadata> chouetteMetadatas = getChouetteMetadatas(lecturesEchanges);
        outputStreamWriter.write("layer,name,address,email,telephone,datestamp,lastupdatestamp,usernote,minlongitude,minlatitude,maxlongitude,maxlatitude\n");
        for (ChouetteMetadata chouetteMetadata : chouetteMetadatas)
            outputStreamWriter.write(chouetteMetadata.getCSVLine() + "\n");
    }

    private void writePictosCSVLines(List<ILectureEchange> lecturesEchanges, OutputStreamWriter outputStreamWriter) throws IOException {
        Collection<Picto> pictos = getPictos(lecturesEchanges);
        outputStreamWriter.write("pointtype,picto,minscale,maxscale\n");
        for (Picto picto : pictos)
            outputStreamWriter.write(picto.getCSVLine() + "\n");
    }

    private void writeTCPointsCSVLines(List<ILectureEchange> lecturesEchanges, OutputStreamWriter outputStreamWriter) throws IOException {
        Collection<TCPoint> tCPoints = getTCPoints(lecturesEchanges);
        outputStreamWriter.write("objectid,geom_longitude,geom_latitude,registrationnumber,name,tcpointtype,fullname,comment,countrycode,creationtime,creatorid,longlattype,longitude,latitude,projectiontype,x,y,objectversion,streetname\n");
        for (TCPoint tCPoint : tCPoints)
            outputStreamWriter.write(tCPoint.getCSVLine() + "\n");
    }

    private Collection<Aot> getAots(List<ILectureEchange> lecturesEchanges) {
        Map<String, Aot> aots = new HashMap<String, Aot>();
        for (ILectureEchange lectureEchange : lecturesEchanges) {
            Reseau reseau = lectureEchange.getReseau();
            String objId = reseau.getObjectId();
            Aot aot = aots.get(objId);
            if (aot == null) {
                aot = new Aot();
                aot.setObjectId(objId);
                String reg = reseau.getRegistrationNumber();
                reg = reg.replace(' ', '_');
                try {
                    aot.setAotSource(System.getProperty("export.geoportail.source."+reg));
                }
                catch(NullPointerException e) {
                    logger.error("No property for key export.geoportail.source."+reg);
                }
                catch(IllegalArgumentException e) {
                    logger.error("Empty property for key export.geoportail.source."+reg);                    
                }
                try {
                    aot.setAotLogoFileName(System.getProperty("export.geoportail.logo."+reg));
                }
                catch(NullPointerException e) {
                    logger.error("No property for key export.geoportail.logo."+reg);
                }
                catch(IllegalArgumentException e) {
                    logger.error("Empty property for key export.geoportail.logo."+reg);                    
                }
                try {
                    aot.setAotURL(new URL(System.getProperty("export.geoportail.url."+reg)));
                }
                catch(NullPointerException e) {
                    logger.error("No property for key export.geoportail.url."+reg);
                }
                catch(IllegalArgumentException e) {
                    logger.error("Empty property for key export.geoportail.url."+reg);                    
                }
                catch(MalformedURLException e) {
                    logger.error("Malformed URL "+System.getProperty("export.geoportail.url."+reg));
                }
                try {
                    aot.setAotURLLegalInformation(new URL(System.getProperty("export.geoportail.url_legal_information."+reg)));
                }
                catch(NullPointerException e) {
                    logger.error("No property for key export.geoportail.url_legal_information."+reg);
                }
                catch(IllegalArgumentException e) {
                    logger.error("Empty property for key export.geoportail.url_legal_information."+reg);                    
                }
                catch(MalformedURLException e) {
                    logger.error("Malformed URL "+System.getProperty("export.geoportail.url_legal_information."+reg));
                }
                try {
                    aot.setAotLegalInformation(System.getProperty("export.geoportail.legal_information."+reg));
                }
                catch(NullPointerException e) {
                    logger.error("No property for key export.geoportail.legal_information."+reg);
                }
                catch(IllegalArgumentException e) {
                    logger.error("Empty property for key export.geoportail.legal_information."+reg);                    
                }
                aots.put(objId, aot);
            }
        }
        return aots.values();
    }

    private Collection<ChouetteMetadata> getChouetteMetadatas(List<ILectureEchange> lecturesEchanges) {
        Map<String, ChouetteMetadata> chouetteMetadatas = new HashMap<String, ChouetteMetadata>();
        for (ILectureEchange lectureEchange : lecturesEchanges) {
            Reseau reseau = lectureEchange.getReseau();
            String objId = reseau.getObjectId();
            ChouetteMetadata chouetteMetadata = chouetteMetadatas.get(objId);
            if (chouetteMetadata == null) {
                chouetteMetadata = new ChouetteMetadata();
                ChouetteMetadata chouetteMetadata2 = new ChouetteMetadata();
                chouetteMetadata.setObjectId(objId);
                chouetteMetadata2.setObjectId(objId);
                String reg = reseau.getRegistrationNumber();
                reg = reg.replace(' ', '_');
                chouetteMetadata.setChouetteMetadataLayer("stoparea");
                chouetteMetadata2.setChouetteMetadataLayer("accesspoint");
                try {
                    chouetteMetadata.setChouetteMetadataName(System.getProperty("export.geoportail.source."+reg));
                    chouetteMetadata2.setChouetteMetadataName(System.getProperty("export.geoportail.source."+reg));
                }
                catch(NullPointerException e) {
                    logger.error("No property for key export.geoportail.source."+reg);
                }
                catch(IllegalArgumentException e) {
                    logger.error("Empty property for key export.geoportail.source."+reg);                    
                }
                try {
                    chouetteMetadata.setChouetteMetadataAddress(System.getProperty("export.geoportail.address."+reg));
                    chouetteMetadata2.setChouetteMetadataAddress(System.getProperty("export.geoportail.address."+reg));
                }
                catch(NullPointerException e) {
                    logger.error("No property for key export.geoportail.address."+reg);
                }
                catch(IllegalArgumentException e) {
                    logger.error("Empty property for key export.geoportail.address."+reg);                    
                }
                try {
                    chouetteMetadata.setChouetteMetadataEmail(System.getProperty("export.geoportail.email."+reg));
                    chouetteMetadata2.setChouetteMetadataEmail(System.getProperty("export.geoportail.email."+reg));
                }
                catch(NullPointerException e) {
                    logger.error("No property for key export.geoportail.email."+reg);
                }
                catch(IllegalArgumentException e) {
                    logger.error("Empty property for key export.geoportail.email."+reg);                    
                }
                try {
                    chouetteMetadata.setChouetteMetadataTelephone(System.getProperty("export.geoportail.telephone."+reg));
                    chouetteMetadata2.setChouetteMetadataTelephone(System.getProperty("export.geoportail.telephone."+reg));
                }
                catch(NullPointerException e) {
                    logger.error("No property for key export.geoportail.telephone."+reg);
                }
                catch(IllegalArgumentException e) {
                    logger.error("Empty property for key export.geoportail.telephone."+reg);                    
                }
                try {
                    chouetteMetadata.setChouetteMetadataDatestamp(System.getProperty("export.geoportail.date1."+reg));
                }
                catch(NullPointerException e) {
                    logger.error("No property for key export.geoportail.date1."+reg);
                }
                catch(IllegalArgumentException e) {
                    logger.error("Empty property for key export.geoportail.date1."+reg);                    
                }
                try {
                    chouetteMetadata2.setChouetteMetadataDatestamp(System.getProperty("export.geoportail.date2."+reg));
                }
                catch(NullPointerException e) {
                    logger.error("No property for key export.geoportail.date2."+reg);
                }
                catch(IllegalArgumentException e) {
                    logger.error("Empty property for key export.geoportail.date2."+reg);                    
                }
                try {
                    chouetteMetadata.setChouetteMetadataLastupdatestamp(System.getProperty("export.geoportail.date3."+reg));
                }
                catch(NullPointerException e) {
                    logger.error("No property for key export.geoportail.date3."+reg);
                }
                catch(IllegalArgumentException e) {
                    logger.error("Empty property for key export.geoportail.date3."+reg);                    
                }
                try {
                    chouetteMetadata2.setChouetteMetadataLastupdatestamp(System.getProperty("export.geoportail.date4."+reg));
                }
                catch(NullPointerException e) {
                    logger.error("No property for key export.geoportail.date4."+reg);
                }
                catch(IllegalArgumentException e) {
                    logger.error("Empty property for key export.geoportail.date4."+reg);                    
                }
                try {
                    chouetteMetadata.setChouetteMetadataUsernote(System.getProperty("export.geoportail.usernote1."+reg));
                }
                catch(NullPointerException e) {
                    logger.error("No property for key export.geoportail.usernote1."+reg);
                }
                catch(IllegalArgumentException e) {
                    logger.error("Empty property for key export.geoportail.usernote1."+reg);                    
                }
                try {
                    chouetteMetadata2.setChouetteMetadataUsernote(System.getProperty("export.geoportail.usernote2."+reg));
                }
                catch(NullPointerException e) {
                    logger.error("No property for key export.geoportail.usernote2."+reg);
                }
                catch(IllegalArgumentException e) {
                    logger.error("Empty property for key export.geoportail.usernote2."+reg);                    
                }
                try {
                    chouetteMetadata.setChouetteMetadataMinlongitude(System.getProperty("export.geoportail.minlongitude1."+reg));
                }
                catch(NullPointerException e) {
                    logger.error("No property for key export.geoportail.minlongitude1."+reg);
                }
                catch(IllegalArgumentException e) {
                    logger.error("Empty property for key export.geoportail.minlongitude1."+reg);                    
                }
                try {
                    chouetteMetadata2.setChouetteMetadataMinlongitude(System.getProperty("export.geoportail.minlongitude2."+reg));
                }
                catch(NullPointerException e) {
                    logger.error("No property for key export.geoportail.minlongitude2."+reg);
                }
                catch(IllegalArgumentException e) {
                    logger.error("Empty property for key export.geoportail.minlongitude2."+reg);                    
                }
                try {
                    chouetteMetadata.setChouetteMetadataMinlatitude(System.getProperty("export.geoportail.minlatitude1."+reg));
                }
                catch(NullPointerException e) {
                    logger.error("No property for key export.geoportail.minlatitude1."+reg);
                }
                catch(IllegalArgumentException e) {
                    logger.error("Empty property for key export.geoportail.minlatitude1."+reg);                    
                }
                try {
                    chouetteMetadata2.setChouetteMetadataMinlatitude(System.getProperty("export.geoportail.minlatitude2."+reg));
                }
                catch(NullPointerException e) {
                    logger.error("No property for key export.geoportail.minlatitude2."+reg);
                }
                catch(IllegalArgumentException e) {
                    logger.error("Empty property for key export.geoportail.minlatitude2."+reg);                    
                }
                try {
                    chouetteMetadata.setChouetteMetadataMaxlongitude(System.getProperty("export.geoportail.maxlongitude1."+reg));
                }
                catch(NullPointerException e) {
                    logger.error("No property for key export.geoportail.maxlongitude1."+reg);
                }
                catch(IllegalArgumentException e) {
                    logger.error("Empty property for key export.geoportail.maxlongitude1."+reg);                    
                }
                try {
                    chouetteMetadata2.setChouetteMetadataMaxlongitude(System.getProperty("export.geoportail.maxlongitude2."+reg));
                }
                catch(NullPointerException e) {
                    logger.error("No property for key export.geoportail.maxlongitude2."+reg);
                }
                catch(IllegalArgumentException e) {
                    logger.error("Empty property for key export.geoportail.maxlongitude2."+reg);                    
                }
                try {
                    chouetteMetadata.setChouetteMetadataMaxlatitude(System.getProperty("export.geoportail.maxlatitude1."+reg));
                }
                catch(NullPointerException e) {
                    logger.error("No property for key export.geoportail.maxlatitude1."+reg);
                }
                catch(IllegalArgumentException e) {
                    logger.error("Empty property for key export.geoportail.maxlatitude1."+reg);                    
                }
                try {
                    chouetteMetadata2.setChouetteMetadataMaxlatitude(System.getProperty("export.geoportail.maxlatitude2."+reg));
                }
                catch(NullPointerException e) {
                    logger.error("No property for key export.geoportail.maxlatitude2."+reg);
                }
                catch(IllegalArgumentException e) {
                    logger.error("Empty property for key export.geoportail.maxlatitude2."+reg);                    
                }                
                chouetteMetadatas.put(objId, chouetteMetadata);
                chouetteMetadatas.put(objId+":"+objId, chouetteMetadata2);
            }
        }
        return chouetteMetadatas.values();
    }

    private Collection<Picto> getPictos(List<ILectureEchange> lecturesEchanges) {
        Map<String, Picto> pictos = new HashMap<String, Picto>();
        for (ILectureEchange lectureEchange : lecturesEchanges) {
            Reseau reseau = lectureEchange.getReseau();
            String objId = reseau.getObjectId();
            Picto picto = pictos.get(objId);
            if (picto == null) {
                picto = new Picto();
                Picto picto1 = new Picto();
                Picto picto2 = new Picto();
                Picto picto3 = new Picto();
                Picto picto4 = new Picto();
                picto.setObjectId(objId);
                picto1.setObjectId(objId);
                picto2.setObjectId(objId);
                picto3.setObjectId(objId);
                picto4.setObjectId(objId);
                String reg = reseau.getRegistrationNumber();
                reg = reg.replace(' ', '_');
                picto.setPictoPointtype("Quai");
                picto1.setPictoPointtype("BoardingPosition");
                picto2.setPictoPointtype("CommercialStopPoint");
                picto3.setPictoPointtype("StopPlace");
                picto4.setPictoPointtype("AccessPoint");
                try {
                    picto.setPictoFilename(System.getProperty("export.geoportail.quayfilename."+reg));
                }
                catch(NullPointerException e) {
                    logger.error("No property for key export.geoportail.quayfilename."+reg);
                }
                catch(IllegalArgumentException e) {
                    logger.error("Empty property for key export.geoportail.quayfilename."+reg);                    
                }
                try {
                    picto1.setPictoFilename(System.getProperty("export.geoportail.boardingpositionfilename."+reg));
                }
                catch(NullPointerException e) {
                    logger.error("No property for key export.geoportail.boardingpositionfilename."+reg);
                }
                catch(IllegalArgumentException e) {
                    logger.error("Empty property for key export.geoportail.boardingpositionfilename."+reg);                    
                }
                try {
                    picto2.setPictoFilename(System.getProperty("export.geoportail.commercialstoppointfilename."+reg));
                }
                catch(NullPointerException e) {
                    logger.error("No property for key export.geoportail.commercialstoppointfilename."+reg);
                }
                catch(IllegalArgumentException e) {
                    logger.error("Empty property for key export.geoportail.commercialstoppointfilename."+reg);                    
                }
                try {
                    picto3.setPictoFilename(System.getProperty("export.geoportail.stopplacefilename."+reg));
                }
                catch(NullPointerException e) {
                    logger.error("No property for key export.geoportail.stopplacefilename."+reg);
                }
                catch(IllegalArgumentException e) {
                    logger.error("Empty property for key export.geoportail.stopplacefilename."+reg);                    
                }
                try {
                    picto4.setPictoFilename(System.getProperty("export.geoportail.accesspointfilename."+reg));
                }
                catch(NullPointerException e) {
                    logger.error("No property for key export.geoportail.accesspointfilename."+reg);
                }
                catch(IllegalArgumentException e) {
                    logger.error("Empty property for key export.geoportail.accesspointfilename."+reg);                    
                }
                try {
                    picto.setPictoMinscale(System.getProperty("export.geoportail.minscale1."+reg));
                }
                catch(NullPointerException e) {
                    logger.error("No property for key export.geoportail.minscale1."+reg);
                }
                catch(IllegalArgumentException e) {
                    logger.error("Empty property for key export.geoportail.minscale1."+reg);                    
                }
                try {
                    picto.setPictoMaxscale(System.getProperty("export.geoportail.maxscale1."+reg));
                }
                catch(NullPointerException e) {
                    logger.error("No property for key export.geoportail.maxscale1."+reg);
                }
                catch(IllegalArgumentException e) {
                    logger.error("Empty property for key export.geoportail.maxscale1."+reg);                    
                }
                try {
                    picto1.setPictoMinscale(System.getProperty("export.geoportail.minscale2."+reg));
                }
                catch(NullPointerException e) {
                    logger.error("No property for key export.geoportail.minscale2."+reg);
                }
                catch(IllegalArgumentException e) {
                    logger.error("Empty property for key export.geoportail.minscale2."+reg);                    
                }
                try {
                    picto1.setPictoMaxscale(System.getProperty("export.geoportail.maxscale2."+reg));
                }
                catch(NullPointerException e) {
                    logger.error("No property for key export.geoportail.maxscale2."+reg);
                }
                catch(IllegalArgumentException e) {
                    logger.error("Empty property for key export.geoportail.maxscale2."+reg);                    
                }
                try {
                    picto2.setPictoMinscale(System.getProperty("export.geoportail.minscale3."+reg));
                }
                catch(NullPointerException e) {
                    logger.error("No property for key export.geoportail.minscale3."+reg);
                }
                catch(IllegalArgumentException e) {
                    logger.error("Empty property for key export.geoportail.minscale3."+reg);                    
                }
                try {
                    picto2.setPictoMaxscale(System.getProperty("export.geoportail.maxscale3."+reg));
                }
                catch(NullPointerException e) {
                    logger.error("No property for key export.geoportail.maxscale3."+reg);
                }
                catch(IllegalArgumentException e) {
                    logger.error("Empty property for key export.geoportail.maxscale3."+reg);                    
                }
                try {
                    picto3.setPictoMinscale(System.getProperty("export.geoportail.minscale4."+reg));
                }
                catch(NullPointerException e) {
                    logger.error("No property for key export.geoportail.minscale4."+reg);
                }
                catch(IllegalArgumentException e) {
                    logger.error("Empty property for key export.geoportail.minscale4."+reg);                    
                }
                try {
                    picto3.setPictoMaxscale(System.getProperty("export.geoportail.maxscale4."+reg));
                }
                catch(NullPointerException e) {
                    logger.error("No property for key export.geoportail.maxscale4."+reg);
                }
                catch(IllegalArgumentException e) {
                    logger.error("Empty property for key export.geoportail.maxscale4."+reg);                    
                }
                try {
                    picto4.setPictoMinscale(System.getProperty("export.geoportail.minscale5."+reg));
                }
                catch(NullPointerException e) {
                    logger.error("No property for key export.geoportail.minscale5."+reg);
                }
                catch(IllegalArgumentException e) {
                    logger.error("Empty property for key export.geoportail.minscale5."+reg);                    
                }
                try {
                    picto4.setPictoMaxscale(System.getProperty("export.geoportail.maxscale5."+reg));
                }
                catch(NullPointerException e) {
                    logger.error("No property for key export.geoportail.maxscale5."+reg);
                }
                catch(IllegalArgumentException e) {
                    logger.error("Empty property for key export.geoportail.maxscale5."+reg);                    
                }
                pictos.put(objId, picto);
                pictos.put(objId+":1", picto1);
                pictos.put(objId+":2", picto2);
                pictos.put(objId+":3", picto3);
                pictos.put(objId+":4", picto4);
            }
        }
        return pictos.values();
    }

    private Collection<TCPoint> getTCPoints(List<ILectureEchange> lecturesEchanges) {
        Map<String, TCPoint> tCPoints = new HashMap<String, TCPoint>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        for (ILectureEchange lectureEchange : lecturesEchanges) {
            List<PositionGeographique> positionsGeographiques = lectureEchange.getPositionsGeographiques();
            for (PositionGeographique positionGeographique : positionsGeographiques) {
                String objId = positionGeographique.getObjectId();
                TCPoint tCPoint = tCPoints.get(objId);
                if (tCPoint == null) {
                    tCPoint = new TCPoint();
                    tCPoint.setObjectId(objId);
                    if (positionGeographique.getLongitude() != null)
                        tCPoint.setTCPointGeomLongitude(positionGeographique.getLongitude().toString());
                    if (positionGeographique.getLatitude() != null)
                        tCPoint.setTCPointGeomLatitude(positionGeographique.getLatitude().toString());
                    tCPoint.setTCPointRegistrationnumber(positionGeographique.getRegistrationNumber());
                    tCPoint.setTCPointName(positionGeographique.getName());
                    switch(positionGeographique.getAreaType()) {
                        case QUAY :
                            tCPoint.setTCPointType("Quay");
                            break;
                        case BOARDINGPOSITION :
                            tCPoint.setTCPointType("BoardingPosition");
                            break;
                        case COMMERCIALSTOPPOINT :
                            tCPoint.setTCPointType("CommercialStopPoint");
                            break;
                        case STOPPLACE :
                            tCPoint.setTCPointType("StopPlace");
                            break;
                    }
                    tCPoint.setTCPointFullname(positionGeographique.getFullName());
                    tCPoint.setTCPointComment(positionGeographique.getComment());
                    tCPoint.setTCPointCountrycode(positionGeographique.getCountryCode());
                    if (positionGeographique.getCreationTime() != null)
                        tCPoint.setTCPointCreationtime(sdf.format(positionGeographique.getCreationTime()));
                    tCPoint.setTCPointCreatorid(positionGeographique.getCreatorId());
                    if (positionGeographique.getLongLatType() != null) {
                        switch(positionGeographique.getLongLatType()) {
                        case STANDARD :
                            tCPoint.setTCPointLonglattype("Standard");
                            break;
                        case WGS84 :
                            tCPoint.setTCPointLonglattype("WGS84");
                            break;
                        case WGS92 :
                            tCPoint.setTCPointLonglattype("WGS92");
                            break;
                        }
                    }
                    if (positionGeographique.getLongitude() != null)
                        tCPoint.setTCPointLongitude(positionGeographique.getLongitude().toString());
                    if (positionGeographique.getLatitude() != null)
                        tCPoint.setTCPointLatitude(positionGeographique.getLatitude().toString());
                    tCPoint.setTCPointProjectiontype(positionGeographique.getProjectionType());
                    if (positionGeographique.getX() != null)
                        tCPoint.setTCPointX(positionGeographique.getX().toString());
                    if (positionGeographique.getY() != null)
                        tCPoint.setTCPointY(positionGeographique.getY().toString());
                    tCPoint.setTCPointObjectversion(""+positionGeographique.getObjectVersion());
                    tCPoint.setTCPointStreetname(positionGeographique.getStreetName());
                    tCPoints.put(objId, tCPoint);
                }
            }
        }
        return tCPoints.values();
    }
}
