package fr.certu.chouette.service.importateur.multilignes.hastus.impl;

import chouette.schema.types.ChouetteAreaType;
import chouette.schema.types.LongLatTypeType;
import fr.certu.chouette.modele.PositionGeographique;
import fr.certu.chouette.service.importateur.multilignes.hastus.ILecteurArret;
import fr.certu.chouette.service.importateur.multilignes.hastus.commun.CodeIncident;
import fr.certu.chouette.service.importateur.multilignes.hastus.commun.ServiceException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;

public class LecteurArret extends Lecteur implements ILecteurArret {
    
    private static final Logger                            logger                      = Logger.getLogger(LecteurArret.class);
    private              int                               lineLength;
    private              String                            cleAreaType;                // "BoardingPosition"
    private              String                            cleLambert1;                // "LAMBERT I"
    private              String                            cleLambert2;                // "LAMBERT II"
    private              String                            cleLambert3;                // "LAMBERT III"
    private              String                            cleLambert4;                // "LAMBERT IV"
    private              String                            cleWGS84;                   // "WGS84"
    private              Map<String, PositionGeographique> zones;                      /// PositionGeographique (non arrêt physique) par name (LecteurZone)
    private              Map<String, PositionGeographique> arretsPhysiques;            /// PositionGeographique (arrêt physique) par name
    private              Map<String, PositionGeographique> arretsPhysiquesParObjectId; /// PositionGeographique (arrêt physique) par objectId
    private              Map<String, String>               objectIdParParentObjectId;  /// objectId (pêre : non arrêt physique) par objectId (fils : arrêt physique)
    private              Set<String>                       arretsNames;
    
    @Override
    public void reinit() {
        lineLength                 = 0;
        arretsPhysiques            = new HashMap<String, PositionGeographique>();
        arretsPhysiquesParObjectId = new HashMap<String, PositionGeographique>();
        objectIdParParentObjectId  = new HashMap<String, String>();
        arretsNames                = new HashSet<String>();
    }

    @Override
    public boolean isTitreReconnu(String[] ligneCSV) {
        if ((ligneCSV == null) || (ligneCSV.length == 0))
            return false;
        if (ligneCSV[0] == null)
            return false;
        return ligneCSV[0].trim().equals(getCleCode());
    }

    @Override
    public void lire(String[] ligneCSV) {
        if ((ligneCSV == null) || (ligneCSV.length == 0))
            return;
        if ((lineLength == 0) && ((ligneCSV.length == 11) || (ligneCSV.length == 12)))
            lineLength = ligneCSV.length;
        if (ligneCSV.length != lineLength)
            throw new ServiceException(CodeIncident.INVALIDE_LONGUEUR_ARRET, "FATAL01002 : Mauvais nombre de champs dans la section '02'.");
        if ((ligneCSV[1] == null) || (ligneCSV[1].trim().length() == 0))
            throw new ServiceException(CodeIncident.INVALIDE_REGISTRATION_ZONE, "ERROR02001 : Le second champs de la section '02' doit etre non null.");
        if ((ligneCSV[2] == null) || (ligneCSV[2].trim().length() == 0))
            throw new ServiceException(CodeIncident.INVALIDE_NAME_ZONE, "ERROR02002 : Le troisieme champs de la section '02' doit etre non null.");

        logger.debug("CREATION D'ARRET PHYSIQUE : "+ligneCSV[1]);
        PositionGeographique arretPhysique = new PositionGeographique();
        arretPhysique.setRegistrationNumber(ligneCSV[1].trim());
        arretPhysique.setObjectId(getIdentificationManager().getIdFonctionnel(getHastusCode(), "StopArea", "PHY"+toTrident(arretPhysique.getRegistrationNumber())));
        arretPhysique.setObjectVersion(1);
        arretPhysique.setCreationTime(new Date(System.currentTimeMillis()));
        arretPhysique.setName(ligneCSV[2].trim());
        int index = 3;
        boolean invalideType = false;
        if (lineLength == 12) {
            if ((ligneCSV[index] == null) || (!ligneCSV[index].trim().equals(getCleAreaType())))
                invalideType = true;
            index++;
        }
        arretPhysique.setAreaType(ChouetteAreaType.BOARDINGPOSITION);

        boolean firstProjTypeIsSetWrong    = false;//
        boolean firstCoordinatesNotSet     = false;//
        boolean firstXSetWrong             = false;//
        boolean firstYSetWrong             = false;//
        boolean firstProjTypeIsNotSetWrong = false;//
        boolean isFirstWGS                 = false;//
        boolean isFirstLambert             = false;//
        if ((ligneCSV[index] != null) && (ligneCSV[index].trim().length() > 0)) {
            if (ligneCSV[index].trim().equals(getCleLambert1())) {
                arretPhysique.setProjectionType(cleLambert1);
                isFirstLambert = true;
            }
            else if (ligneCSV[index].trim().equals(getCleLambert2())) {
                arretPhysique.setProjectionType(cleLambert2);
                isFirstLambert = true;
            }
            else if (ligneCSV[index].trim().equals(getCleLambert3())) {
                arretPhysique.setProjectionType(cleLambert3);
                isFirstLambert = true;
            }
            else if (ligneCSV[index].trim().equals(getCleLambert4())) {
                arretPhysique.setProjectionType(cleLambert4);
                isFirstLambert = true;
            }
            else if (ligneCSV[index].trim().equals(getCleWGS84())) {
                arretPhysique.setLongLatType(LongLatTypeType.WGS84);
                isFirstWGS = true;
            }
            else
                firstProjTypeIsSetWrong = true;
            if (!firstProjTypeIsSetWrong) {
                if ((ligneCSV[index+1] == null) || (ligneCSV[index+1].trim().length() == 0) || (ligneCSV[index+2] == null) || (ligneCSV[index+2].trim().length() == 0))
                    firstCoordinatesNotSet = true;
                if (!firstCoordinatesNotSet) {
                    try {
                        if ((ligneCSV[index+1] != null) && (ligneCSV[index+1].trim().length() > 0)) {
                            String latNumber = ligneCSV[index+1].trim();
                            latNumber = latNumber.replace(',', '.');
                            if ((ligneCSV[index].equals(getCleLambert1())) || (ligneCSV[index].equals(getCleLambert2())) || (ligneCSV[index].equals(getCleLambert3())) || (ligneCSV[index].equals(getCleLambert4())))
                                arretPhysique.setX(new BigDecimal(latNumber));
                            else if (ligneCSV[index].equals(getCleWGS84()))
                                arretPhysique.setLongitude(new BigDecimal(latNumber));
                        }
                    }
                    catch(NumberFormatException e) {
                        firstXSetWrong = true;
                    }
                    try {
                        if ((ligneCSV[index+2] != null) && (ligneCSV[index+2].trim().length() > 0)) {
                            String longNumber = ligneCSV[index+2].trim();
                            longNumber = longNumber.replace(',', '.');
                            if ((ligneCSV[index].equals(getCleLambert1())) || (ligneCSV[index].equals(getCleLambert2())) || (ligneCSV[index].equals(getCleLambert3())) || (ligneCSV[index].equals(getCleLambert4())))
                                arretPhysique.setY(new BigDecimal(longNumber));
                            else if (ligneCSV[index].equals(getCleWGS84()))
                                arretPhysique.setLatitude(new BigDecimal(longNumber));
                        }
                    }
                    catch(NumberFormatException e) {
                        firstYSetWrong = true;
                    }
                }
            }
        }
        else {
            if (ligneCSV[index+1] != null)
                if (ligneCSV[index+1].trim().length() > 0)
                    firstProjTypeIsNotSetWrong = true;
            if (ligneCSV[index+2] != null)
                if (ligneCSV[index+2].trim().length() > 0)
                    firstProjTypeIsNotSetWrong = true;
        }
        index += 3;

        boolean zoneNotSet        = false;//
        boolean zoneSetWrong      = false;//
        PositionGeographique zone = null;
        if ((ligneCSV[index] == null) || (ligneCSV[index].trim().length() == 0))
            zoneNotSet = true;
        if (!zoneNotSet) {
            zone = getZones().get(ligneCSV[index].trim());
            if (zone == null)
                zoneSetWrong = true;
        }
        index++;

        if ((ligneCSV[index] != null) || (ligneCSV[index].trim().length() != 0))
            arretPhysique.setComment(ligneCSV[index].trim());
        index++;

        boolean secondProjTypeIsSetWrong    = false;//
        boolean secondCoordinatesNotSet     = false;//
        boolean secondXSetWrong             = false;//
        boolean secondYSetWrong             = false;//
        boolean secondProjTypeIsNotSetWrong = false;//
        boolean isSecondWGS                 = false;//
        boolean isSecondLambert             = false;//
        if ((ligneCSV[index] != null) && (ligneCSV[index].trim().length() > 0)) {
            if (ligneCSV[index].trim().equals(getCleLambert1())) {
                arretPhysique.setProjectionType(cleLambert1);
                isSecondLambert = true;
            }
            else if (ligneCSV[index].trim().equals(getCleLambert2())) {
                arretPhysique.setProjectionType(cleLambert2);
                isSecondLambert = true;
            }
            else if (ligneCSV[index].trim().equals(getCleLambert3())) {
                arretPhysique.setProjectionType(cleLambert3);
                isSecondLambert = true;
            }
            else if (ligneCSV[index].trim().equals(getCleLambert4())) {
                arretPhysique.setProjectionType(cleLambert4);
                isSecondLambert = true;
            }
            else if (ligneCSV[index].trim().equals(getCleWGS84())) {
                arretPhysique.setLongLatType(LongLatTypeType.WGS84);
                isSecondWGS = true;
            }
            else
                secondProjTypeIsSetWrong = true;
            if (!secondProjTypeIsSetWrong) {
                if ((ligneCSV[index+1] == null) || (ligneCSV[index+1].trim().length() == 0) || (ligneCSV[index+2] == null) || (ligneCSV[index+2].trim().length() == 0))
                    secondCoordinatesNotSet = true;
                if (!secondCoordinatesNotSet) {
                    try {
                        if ((ligneCSV[index+1] != null) && (ligneCSV[index+1].trim().length() > 0)) {
                            String latNumber = ligneCSV[index+1].trim();
                            latNumber = latNumber.replace(',', '.');
                            if ((ligneCSV[index].equals(getCleLambert1())) || (ligneCSV[index].equals(getCleLambert2())) || (ligneCSV[index].equals(getCleLambert3())) || (ligneCSV[index].equals(getCleLambert4())))
                                arretPhysique.setX(new BigDecimal(latNumber));
                            else if (ligneCSV[index].equals(getCleWGS84()))
                                arretPhysique.setLongitude(new BigDecimal(latNumber));
                        }
                    }
                    catch(NumberFormatException e) {
                        secondXSetWrong = true;
                    }
                    try {
                        if ((ligneCSV[index+2] != null) && (ligneCSV[index+2].trim().length() > 0)) {
                            String longNumber = ligneCSV[index+2].trim();
                            longNumber = longNumber.replace(',', '.');
                            if ((ligneCSV[index].equals(getCleLambert1())) || (ligneCSV[index].equals(getCleLambert2())) || (ligneCSV[index].equals(getCleLambert3())) || (ligneCSV[index].equals(getCleLambert4())))
                                arretPhysique.setY(new BigDecimal(longNumber));
                            else if (ligneCSV[index].equals(getCleWGS84()))
                                arretPhysique.setLatitude(new BigDecimal(longNumber));
                        }
                    }
                    catch(NumberFormatException e) {
                        secondYSetWrong = true;
                    }
                }
            }
        }
        else {
            if (ligneCSV[index+1] != null)
                if (ligneCSV[index+1].trim().length() > 0)
                    secondProjTypeIsNotSetWrong = true;
            if (ligneCSV[index+2] != null)
                if (ligneCSV[index+2].trim().length() > 0)
                    secondProjTypeIsNotSetWrong = true;
        }
        index += 3;

        addArretPhysique(arretPhysique, zone);

        if (invalideType)
            throw new ServiceException(CodeIncident.INVALIDE_TYPE_ZONE, "WARN02001 : Le quatrieme champs de la section '02' doit etre '"+getCleAreaType()+"'.");
        if ((isFirstLambert && isSecondLambert) || (isFirstWGS && isSecondWGS))
            if (lineLength == 12)
                throw new ServiceException(CodeIncident.INVALIDE_CORDONEES_TYPE, "ERROR02103 : Le cinquieme et le dixieme champs de la section '02' ne doivent pas commencer tous deux par le meme mot : Lambert ou WGS.");
            else
                throw new ServiceException(CodeIncident.INVALIDE_CORDONEES_TYPE, "ERROR02103 : Le quatrieme et le neuvieme champs de la section '02' ne doivent pas commencer tous deux par le meme mot : Lambert ou WGS.");
        if (firstProjTypeIsSetWrong)
            if (lineLength == 12)
                throw new ServiceException(CodeIncident.INVALIDE_CORDONEES_TYPE, "ERROR02103 : Le cinquieme champs de la section '02' doit commencer par Lambert ou WGS.");
            else
                throw new ServiceException(CodeIncident.INVALIDE_CORDONEES_TYPE, "ERROR02103 : Le quatrieme champs de la section '02' doit commencer par Lambert ou WGS.");
        if (firstCoordinatesNotSet)
            if (lineLength == 12)
                throw new ServiceException(CodeIncident.INVALIDE_CORDONEES_TYPE, "ERROR02106 : Lorsque le cinquieme champs de la section '02' est non vide, le sixieme et le septieme champs ne doivent pas etre vides.");
            else
                throw new ServiceException(CodeIncident.INVALIDE_CORDONEES_TYPE, "ERROR02106 : Lorsque le quatrieme champs de la section '02' est non vide, le cinquieme et le sixieme champs ne doivent pas etre vides.");
        if (firstXSetWrong)
            if (lineLength == 12)
                throw new ServiceException(CodeIncident.INVALIDE_NUMBER_FORMAT, "ERROR02104 : Le sixieme champs de la section '02' est soit vide soit un nombre reel.");
            else
                throw new ServiceException(CodeIncident.INVALIDE_NUMBER_FORMAT, "ERROR02104 : Le cinquieme champs de la section '02' est soit vide soit un nombre reel.");
        if (firstYSetWrong)
            if (lineLength == 12)
                throw new ServiceException(CodeIncident.INVALIDE_NUMBER_FORMAT, "ERROR02104 : Le septieme champs de la section '02' est soit vide soit un nombre reel.");
            else
                throw new ServiceException(CodeIncident.INVALIDE_NUMBER_FORMAT, "ERROR02104 : Le sixieme champs de la section '02' est soit vide soit un nombre reel.");
        if (firstProjTypeIsNotSetWrong)
            if (lineLength == 12)
                throw new ServiceException(CodeIncident.INVALIDE_CORDONEES_TYPE, "ERROR02106 : Lorsque le cinquieme champs de la section '02' est vide, le sixieme et le septieme champs doivent etre vides.");
            else
                throw new ServiceException(CodeIncident.INVALIDE_CORDONEES_TYPE, "ERROR02106 : Lorsque le quatrieme champs de la section '02' est vide, le cinquieme et le sixieme champs doivent etre vides.");
        if (zoneNotSet)
            if (lineLength == 12)
                throw new ServiceException(CodeIncident.INVALIDE_PARENT_ARRET, "ERROR02102 : Le huitieme champs de la section '02' doit etre non vide.");
            else
                throw new ServiceException(CodeIncident.INVALIDE_PARENT_ARRET, "ERROR02102 : Le septieme champs de la section '02' doit etre non vide.");
        if (zoneSetWrong)
            if (lineLength == 12)
                throw new ServiceException(CodeIncident.INVALIDE_PARENT_ARRET, "ERROR02102 : Le huitieme champs de la section '02' doit etre egal au deuxieme champs d'une ligne de la section '01'.");
            else
                throw new ServiceException(CodeIncident.INVALIDE_PARENT_ARRET, "ERROR02102 : Le septieme champs de la section '02' doit etre egal au deuxieme champs d'une ligne de la section '01'.");

        if (secondProjTypeIsSetWrong)
            if (lineLength == 12)
                throw new ServiceException(CodeIncident.INVALIDE_CORDONEES_TYPE, "ERROR02103 : Le dixieme champs de la section '02' doit commencer par Lambert ou WGS.");
            else
                throw new ServiceException(CodeIncident.INVALIDE_CORDONEES_TYPE, "ERROR02103 : Le neuvieme champs de la section '02' doit commencer par Lambert ou WGS.");
        if (secondCoordinatesNotSet)
            if (lineLength == 12)
                throw new ServiceException(CodeIncident.INVALIDE_CORDONEES_TYPE, "ERROR02107 : Lorsque le dixieme champs de la section '02' est non vide, le onzieme et le douzieme champs ne doivent pas etre vides.");
            else
                throw new ServiceException(CodeIncident.INVALIDE_CORDONEES_TYPE, "ERROR02107 : Lorsque le neuvieme champs de la section '02' est non vide, le dixieme et le onzieme champs ne doivent pas etre vides.");
        if (secondXSetWrong)
            if (lineLength == 12)
                throw new ServiceException(CodeIncident.INVALIDE_NUMBER_FORMAT, "ERROR02105 : Le onzieme champs de la section '02' est soit vide soit un nombre reel.");
            else
                throw new ServiceException(CodeIncident.INVALIDE_NUMBER_FORMAT, "ERROR02105 : Le dixieme champs de la section '02' est soit vide soit un nombre reel.");
        if (secondYSetWrong)
            if (lineLength == 12)
                throw new ServiceException(CodeIncident.INVALIDE_NUMBER_FORMAT, "ERROR02105 : Le douzieme champs de la section '02' est soit vide soit un nombre reel.");
            else
                throw new ServiceException(CodeIncident.INVALIDE_NUMBER_FORMAT, "ERROR02105 : Le onzieme champs de la section '02' est soit vide soit un nombre reel.");
        if (secondProjTypeIsNotSetWrong)
            if (lineLength == 12)
                throw new ServiceException(CodeIncident.INVALIDE_CORDONEES_TYPE, "ERROR02107 : Lorsque le dixieme champs de la section '02' est vide, le onzieme et le douzieme champs doivent etre vides.");
            else
                throw new ServiceException(CodeIncident.INVALIDE_CORDONEES_TYPE, "ERROR02107 : Lorsque le neuvieme champs de la section '02' est vide, le dixieme et le onzieme champs doivent etre vides.");
    }
    
    public void addArretPhysique(PositionGeographique arretPhysique, PositionGeographique zone) {
        if (arretsPhysiques == null)
            arretsPhysiques = new HashMap<String, PositionGeographique>();
        PositionGeographique tmpArretPhysique = arretsPhysiques.get(arretPhysique.getRegistrationNumber());
        if (tmpArretPhysique != null)
            if (theSameArretPhysique(arretPhysique, tmpArretPhysique, zone))
                return;
            else
                throw new ServiceException(CodeIncident.DUPLICATE_NAME_ARRETPHYSIQUE, "ERROR02101 : Il ne peut y avoir deux lignes de la section '02' avec le meme deuxieme champs.");
        if (zone != null)
            addObjectIdParParentObjectId(arretPhysique.getObjectId(), zone.getObjectId());
        arretsPhysiques.put(arretPhysique.getRegistrationNumber(), arretPhysique);
        arretsPhysiquesParObjectId.put(arretPhysique.getObjectId(), arretPhysique);
    }

    private boolean theSameArretPhysique(PositionGeographique arretPhysique1, PositionGeographique arretPhysique2, PositionGeographique zone) {
        if ((arretPhysique1 == null) || (arretPhysique2 == null))
            if ((arretPhysique1 == null) && (arretPhysique2 == null))
                return true;
            else
                return false;
        if (((arretPhysique1.getRegistrationNumber() == null) || (arretPhysique2.getRegistrationNumber() == null)) &&
            ((arretPhysique1.getRegistrationNumber() != null) || (arretPhysique2.getRegistrationNumber() != null)))
            return false;
        if (arretPhysique1.getRegistrationNumber() != null)
            if (!arretPhysique1.getRegistrationNumber().equals(arretPhysique2.getRegistrationNumber()))
                return false;
        if (((arretPhysique1.getName() == null) || (arretPhysique2.getName() == null)) &&
            ((arretPhysique1.getName() != null) || (arretPhysique2.getName() != null)))
            return false;
        if (arretPhysique1.getName() != null)
            if (!arretPhysique1.getName().equals(arretPhysique2.getName()))
                return false;
        if (((arretPhysique1.getCountryCode() == null) || (arretPhysique2.getCountryCode() == null)) &&
            ((arretPhysique1.getCountryCode() != null) || (arretPhysique2.getCountryCode() != null)))
            return false;
        if (arretPhysique1.getCountryCode() != null)
            if (!arretPhysique1.getCountryCode().equals(arretPhysique2.getCountryCode()))
                return false;
        if (((zone == null) || (objectIdParParentObjectId.get(arretPhysique2.getObjectId()) == null)) &&
            ((zone != null) || (objectIdParParentObjectId.get(arretPhysique2.getObjectId()) != null)))
            return false;
        if (zone != null)
            if (!zone.getObjectId().equals(objectIdParParentObjectId.get(arretPhysique2.getObjectId())))
                return false;
        return true;
    }
    
    public void addObjectIdParParentObjectId(String objectId, String parentObjectId) {
        if (objectIdParParentObjectId == null)
            objectIdParParentObjectId = new HashMap<String, String>();
        if (objectIdParParentObjectId.get(objectId) != null)
            if (lineLength == 11)
                throw new ServiceException(CodeIncident.DUPLICATE_NAME_PARENTARRETPHYSIQUE, "ERROR02102 : Il ne peut y avoir deux lignes de la section '02' avec le meme huitieme champs.");
            else
                throw new ServiceException(CodeIncident.DUPLICATE_NAME_PARENTARRETPHYSIQUE, "ERROR02102 : Il ne peut y avoir deux lignes de la section '02' avec le meme septieme champs.");
        objectIdParParentObjectId.put(objectId, parentObjectId);
    }

    @Override
    public void completion() {
        //TODO. Case where some stops are in Lambert and others are in WGS84
        for (String registrationNumber : zones.keySet()) {
            PositionGeographique zone = zones.get(registrationNumber);
            int    count1    = 0;
            int    count2    = 0;
            double x         = (double)0.0;
            double y         = (double)0.0;
            double latitude  = (double)0.0;
            double longitude = (double)0.0;

            for (String arretPhysiqueObjectId : objectIdParParentObjectId.keySet())
                if (objectIdParParentObjectId.get(arretPhysiqueObjectId).equals(zone.getObjectId())) {
                    PositionGeographique arretPhysique = arretsPhysiquesParObjectId.get(arretPhysiqueObjectId);
                    if (arretPhysique == null)
                        throw new ServiceException(CodeIncident.DONNEE_INVALIDE, "Il doit y avoir un ARRET PHYSIQUE : "+arretPhysiqueObjectId);
                    if ((arretPhysique.getX() != null) && (arretPhysique.getY() != null)) {
                        count1++;
                        x = x + arretPhysique.getX().doubleValue();
                        y = y + arretPhysique.getY().doubleValue();
                    }
                    if ((arretPhysique.getLatitude() != null) && (arretPhysique.getLongitude() != null)) {
                        count2++;
                        latitude  = latitude  + arretPhysique.getLatitude().doubleValue();
                        longitude = longitude + arretPhysique.getLongitude().doubleValue();
                    }
                }
            if (count1 > 0) {
                x = x / (double)count1;
                y = y / (double)count1;
                zone.setX(new BigDecimal(x));
                zone.setY(new BigDecimal(y));
            }
            if (count2 > 0) {
                latitude  = latitude  / (double)count2;
                longitude = longitude / (double)count2;
                zone.setLatitude(new BigDecimal(latitude));
                zone.setLongitude(new BigDecimal(longitude));
            }
        }
        arretsNames.clear();
    }

    public String getCleAreaType() {
        return cleAreaType;
    }
    
    public void setCleAreaType(String cleAreaType) {
        this.cleAreaType = cleAreaType;
    }

    public String getCleLambert1() {
        return cleLambert1;
    }
    
    public void setCleLambert1(String cleLambert1) {
        this.cleLambert1 = cleLambert1;
    }

    public String getCleLambert2() {
        return cleLambert2;
    }
    
    public void setCleLambert2(String cleLambert2) {
        this.cleLambert2 = cleLambert2;
    }

    public String getCleLambert3() {
        return cleLambert3;
    }
    
    public void setCleLambert3(String cleLambert3) {
        this.cleLambert3 = cleLambert3;
    }

    public String getCleLambert4() {
        return cleLambert4;
    }
    
    public void setCleLambert4(String cleLambert4) {
        this.cleLambert4 = cleLambert4;
    }

    public String getCleWGS84() {
        return cleWGS84;
    }
    
    public void setCleWGS84(String cleWGS84) {
        this.cleWGS84 = cleWGS84;
    }

    public Map<String, PositionGeographique> getZones() {
        return zones;
    }
    
    @Override
    public void setZones(Map<String, PositionGeographique> zones) {
        this.zones = zones;
    }

    @Override
    public Map<String, PositionGeographique> getArretsPhysiques() {
        return arretsPhysiques;
    }
    
    @Override
    public Map<String, PositionGeographique> getArretsPhysiquesParObjectId() {
        return arretsPhysiquesParObjectId;
    }

    public void setArretsPhysiques(Map<String, PositionGeographique> arretsPhysiques) {
        this.arretsPhysiques = arretsPhysiques;
    }
    
    public PositionGeographique getArretPhysique(String name) {
        return arretsPhysiques.get(name);
    }

    public void setObjectIdParParentObjectId(Map<String, String> objectIdParParentObjectId) {
        this.objectIdParParentObjectId = objectIdParParentObjectId;
    }
    
    @Override
    public Map<String, String> getObjectIdParParentObjectId() {
        return objectIdParParentObjectId;
    }

    public String getObjectIdParParentObjectId(String objectId) {
        if (objectIdParParentObjectId == null)
            return null;
        return objectIdParParentObjectId.get(objectId);
    }
}
