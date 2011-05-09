package fr.certu.chouette.service.amivif.base;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import amivif.schema.StopPoint;
import chouette.schema.AreaCentroid;
import chouette.schema.StopArea;
import fr.certu.chouette.service.amivif.IAccesseurAreaStop;
import fr.certu.chouette.service.identification.ObjectIdLecteur;

public class StopPointConverter {

    private static final Logger logger = Logger.getLogger(StopPointConverter.class);
    private AddressConverter addressConverter = new AddressConverter();
    private LongLatTypeConverter longLatTypeConverter = new LongLatTypeConverter();
    private ProjectedPointConverter projectedPointConverter = new ProjectedPointConverter();
    private StopAreaExtensionConverter stopAreaExtensionConverter = new StopAreaExtensionConverter();

    private chouette.schema.StopPoint atc(amivif.schema.StopPoint amivifStopPoint) {
        if (amivifStopPoint == null) {
            return null;
        }
        chouette.schema.StopPoint chouetteStopPoint = new chouette.schema.StopPoint();
        chouetteStopPoint.setAddress(addressConverter.atc(amivifStopPoint.getAddress()));
        chouetteStopPoint.setComment(amivifStopPoint.getComment());
        // PERTE D'INFOS : EST-CE RAISONABLE ?
        //if (amivifStopPoint.getContainedIn() != null && amivifStopPoint.getContainedIn().length > 0)
        //	chouetteStopPoint.setContainedIn(amivifStopPoint.getContainedIn(0));
        //chouetteStopPoint.setContainedIn(objectIdLecteur.getNouveauId(chouetteStopPoint.getObjectId(), "StopArea"));

        // Avant la transformation de débouclage
        // le StopPoint AMIVIF (qui est vu a priori comme un arret physique) est traduit  
        // un StopPoint CHOUETTE et un StopArea CHOUETTE reliés entre eux
        // La transformation de débouclage transformera les StopPoint (et leurs objectId) 
        // mais laissera le StopArea inchangé
        chouetteStopPoint.setContainedIn(amivifStopPoint.getObjectId()); // WHY ?

        chouetteStopPoint.setCreationTime(amivifStopPoint.getCreationTime());
        chouetteStopPoint.setCreatorId(amivifStopPoint.getCreatorId());
        chouetteStopPoint.setLatitude(amivifStopPoint.getLatitude());
        chouetteStopPoint.setLineIdShortcut(amivifStopPoint.getLineIdShortcut());
        chouetteStopPoint.setLongitude(amivifStopPoint.getLongitude());
        chouetteStopPoint.setLongLatType(longLatTypeConverter.atc(amivifStopPoint.getLongLatType()));
        chouetteStopPoint.setName(amivifStopPoint.getName());
        chouetteStopPoint.setObjectId(amivifStopPoint.getObjectId());
        if (amivifStopPoint.hasObjectVersion() && amivifStopPoint.getObjectVersion() >= 1) {
            chouetteStopPoint.setObjectVersion(amivifStopPoint.getObjectVersion());
        } else {
            chouetteStopPoint.setObjectVersion(1);
        }
        chouetteStopPoint.setProjectedPoint(projectedPointConverter.atc(amivifStopPoint.getProjectedPoint()));
        chouetteStopPoint.setPtNetworkIdShortcut(amivifStopPoint.getPtNetworkIdShortcut());
        return chouetteStopPoint;
    }

    private chouette.schema.AreaCentroid atcPlace(amivif.schema.StopPoint amivifStopPoint) {
        if (amivifStopPoint == null) {
            return null;
        }
        chouette.schema.AreaCentroid chouetteAreaCentroid = new chouette.schema.AreaCentroid();

        chouetteAreaCentroid.setAddress(addressConverter.atc(amivifStopPoint.getAddress()));
        chouetteAreaCentroid.setComment(amivifStopPoint.getComment());
        // PERTE D'INFOS : EST-CE RAISONABLE ?
        //if (amivifStopPoint.getContainedIn() != null && amivifStopPoint.getContainedIn().length > 0)
        //	chouetteAreaCentroid.setContainedIn(amivifStopPoint.getContainedIn(0));
        //chouetteAreaCentroid.setContainedIn(objectIdLecteur.getNouveauId(chouetteAreaCentroid.getObjectId(), "StopArea"));		
        chouetteAreaCentroid.setContainedIn(null); // WHY ?
        chouetteAreaCentroid.setCreationTime(amivifStopPoint.getCreationTime());
        chouetteAreaCentroid.setCreatorId(amivifStopPoint.getCreatorId());
        chouetteAreaCentroid.setLatitude(amivifStopPoint.getLatitude());
        chouetteAreaCentroid.setLongitude(amivifStopPoint.getLongitude());
        chouetteAreaCentroid.setLongLatType(longLatTypeConverter.atc(amivifStopPoint.getLongLatType()));
        chouetteAreaCentroid.setName(amivifStopPoint.getName());
        chouetteAreaCentroid.setObjectId(ObjectIdLecteur.getNouveauId(amivifStopPoint.getObjectId(), "Place"));
        chouetteAreaCentroid.setContainedIn(amivifStopPoint.getObjectId());
        if (amivifStopPoint.hasObjectVersion() && amivifStopPoint.getObjectVersion() >= 1) {
            chouetteAreaCentroid.setObjectVersion(amivifStopPoint.getObjectVersion());
        } else {
            chouetteAreaCentroid.setObjectVersion(1);
        }
        chouetteAreaCentroid.setProjectedPoint(projectedPointConverter.atc(amivifStopPoint.getProjectedPoint()));
        return chouetteAreaCentroid;
    }

    private chouette.schema.StopArea atcArea(amivif.schema.StopPoint amivifStopPoint) {
        if (amivifStopPoint == null) {
            return null;
        }
        chouette.schema.StopArea chouetteStopArea = new chouette.schema.StopArea();

        //chouetteStopArea.addBoundaryPoint(vBoundaryPoint);
        //chouetteStopArea.addContains(vContains);
        //chouetteStopArea.setCentroidOfArea(centroidOfArea);
        chouetteStopArea.setObjectId(amivifStopPoint.getObjectId());
//		chouetteStopArea.addContains( objectIdLecteur.getNouveauId(chouetteStopArea.getObjectId(), "StopPoint"));
        chouetteStopArea.setCentroidOfArea(ObjectIdLecteur.getNouveauId(chouetteStopArea.getObjectId(), "Place"));
        chouetteStopArea.setComment(amivifStopPoint.getComment());
        chouetteStopArea.setCreationTime(amivifStopPoint.getCreationTime());
        chouetteStopArea.setCreatorId(amivifStopPoint.getCreatorId());
        chouetteStopArea.setName(amivifStopPoint.getName());
        //chouetteStopArea.setObjectId(objectIdLecteur.getNouveauId(amivifStopPoint.getObjectId(), "StopArea"));
        if (amivifStopPoint.hasObjectVersion() && amivifStopPoint.getObjectVersion() >= 1) {
            chouetteStopArea.setObjectVersion(amivifStopPoint.getObjectVersion());
        } else {
            chouetteStopArea.setObjectVersion(1);
        }
        chouetteStopArea.setStopAreaExtension(stopAreaExtensionConverter.atc(amivifStopPoint.getAMIVIF_StopPoint_Extension()));
        return chouetteStopArea;
    }

    public chouette.schema.StopPoint[] atc(amivif.schema.StopPoint[] amivifStopPoints) {
        if (amivifStopPoints == null) {
            return new chouette.schema.StopPoint[0];
        }
        int totalStopPoints = amivifStopPoints.length;
        chouette.schema.StopPoint[] chouetteStopPoints = new chouette.schema.StopPoint[totalStopPoints];
        for (int i = 0; i < totalStopPoints; i++) {
            chouetteStopPoints[i] = atc(amivifStopPoints[i]);
        }
        return chouetteStopPoints;
    }

    public chouette.schema.StopArea[] atcArea(amivif.schema.StopPoint[] amivifStopPoints) {
        if (amivifStopPoints == null) {
            return new chouette.schema.StopArea[0];
        }
        int totalStopAreas = amivifStopPoints.length;
        chouette.schema.StopArea[] chouetteStopAreas = new chouette.schema.StopArea[totalStopAreas];
        for (int i = 0; i < totalStopAreas; i++) {
            chouetteStopAreas[i] = atcArea(amivifStopPoints[i]);
        }
        return chouetteStopAreas;
    }

    public chouette.schema.AreaCentroid[] atcPlace(amivif.schema.StopPoint[] amivifStopPoints) {
        if (amivifStopPoints == null) {
            return new chouette.schema.AreaCentroid[0];
        }
        int totalAreaCentroids = amivifStopPoints.length;
        chouette.schema.AreaCentroid[] chouetteAreaCentroids = new chouette.schema.AreaCentroid[totalAreaCentroids];
        for (int i = 0; i < totalAreaCentroids; i++) {
            chouetteAreaCentroids[i] = atcPlace(amivifStopPoints[i]);
        }
        return chouetteAreaCentroids;
    }

    private amivif.schema.StopPoint cta(chouette.schema.StopPoint chouette, IAccesseurAreaStop accesseur) {
        if (chouette == null) {
            return null;
        }

        StopPoint amivif = new StopPoint();
        StopArea chouetteArea = accesseur.getStopAreaOfStop(chouette.getObjectId());
        AreaCentroid chouetteCentroid = accesseur.getAreaCentroidOfStop(chouette.getObjectId());

        amivif.setObjectId(chouetteArea.getObjectId());
        if (chouetteArea.hasObjectVersion() && chouetteArea.getObjectVersion() >= 1) {
            amivif.setObjectVersion(chouetteArea.getObjectVersion());
        } else {
            amivif.setObjectVersion(1);
        }
        amivif.setCreationTime(chouetteArea.getCreationTime());
        amivif.setCreatorId(chouetteArea.getCreatorId());

        amivif.setName(chouetteArea.getName());
        amivif.setLongitude(chouetteCentroid.getLongitude());
        amivif.setLatitude(chouetteCentroid.getLatitude());
        amivif.setLongLatType(longLatTypeConverter.cta(chouetteCentroid.getLongLatType()));
        amivif.setProjectedPoint(projectedPointConverter.cta(chouetteCentroid.getProjectedPoint()));
        amivif.setAddress(addressConverter.cta(chouetteCentroid.getAddress()));
        amivif.setAMIVIF_StopPoint_Extension(stopAreaExtensionConverter.cta(chouetteArea.getStopAreaExtension()));
        amivif.setComment(chouetteArea.getComment());

        return amivif;
    }

    public amivif.schema.StopPoint[] cta(chouette.schema.StopPoint[] chouettes, IAccesseurAreaStop accesseur) {
        if (chouettes == null) {
            return new amivif.schema.StopPoint[0];
        }

        Set<String> objectIdDisincts = new HashSet<String>();
        Set<amivif.schema.StopPoint> amivifs = new HashSet<amivif.schema.StopPoint>();

        int total = chouettes.length;
        for (int i = 0; i < total; i++) {
            StopArea stopArea = accesseur.getStopAreaOfStop(chouettes[ i].getObjectId());
            if (objectIdDisincts.add(stopArea.getObjectId())) {
                amivifs.add(cta(chouettes[ i], accesseur));
            }
        }
        return (amivif.schema.StopPoint[]) amivifs.toArray(new amivif.schema.StopPoint[0]);
    }
}
