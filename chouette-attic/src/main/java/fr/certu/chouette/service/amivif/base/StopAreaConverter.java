package fr.certu.chouette.service.amivif.base;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import amivif.schema.AMIVIF_StopArea_Extension;
import amivif.schema.ProjectedPoint;
import chouette.schema.AreaCentroid;
import chouette.schema.StopAreaExtension;
import chouette.schema.types.ChouetteAreaType;
import chouette.schema.types.LongLatTypeType;
import fr.certu.chouette.service.amivif.IAccesseurAreaStop;
import fr.certu.chouette.service.identification.ObjectIdLecteur;

public class StopAreaConverter {

    ObjectIdLecteur objectIdLecteur = new ObjectIdLecteur();

    private chouette.schema.StopArea atc(amivif.schema.StopArea amivif) {

        if (amivif == null) {
            return null;
        }

        chouette.schema.StopArea chouette = new chouette.schema.StopArea();

        chouette.setObjectId(amivif.getObjectId());
        if (amivif.hasObjectVersion() && amivif.getObjectVersion() >= 1) {
            chouette.setObjectVersion(amivif.getObjectVersion());
        } else {
            chouette.setObjectVersion(1);
        }
        chouette.setCreationTime(amivif.getCreationTime());
        chouette.setCreatorId(amivif.getCreatorId());

        chouette.setComment(amivif.getComment());
        chouette.setName(amivif.getName());

        int totalZonesContenues = amivif.getContainsCount();
        for (int i = 0; i < totalZonesContenues; i++) {
            chouette.addContains(amivif.getContains(i));
        }

        AMIVIF_StopArea_Extension amivifExt = amivif.getAMIVIF_StopArea_Extension();
        if (amivifExt != null) {
            StopAreaExtension extension = new StopAreaExtension();
            chouette.setStopAreaExtension(extension);

            extension.setFareCode((int) amivifExt.getUpFarZone());
            extension.setAreaType(ChouetteAreaType.STOPPLACE);
            if (amivifExt.getProjectedPoint() != null) {
                chouette.setCentroidOfArea(ObjectIdLecteur.getNouveauId(amivif.getObjectId(), "Place"));
            }
        }

        return chouette;
    }

    private chouette.schema.AreaCentroid atcPlace(amivif.schema.StopArea amivif) {

        if (amivif == null) {
            return null;
        }

        AMIVIF_StopArea_Extension amivifExt = amivif.getAMIVIF_StopArea_Extension();
        if (amivifExt == null || amivifExt.getProjectedPoint() == null) {
            return null;
        }

        chouette.schema.AreaCentroid chouette = new chouette.schema.AreaCentroid();
        chouette.schema.ProjectedPoint chouetteProjectedPoint = new chouette.schema.ProjectedPoint();
        chouetteProjectedPoint.setX(amivifExt.getProjectedPoint().getX());
        chouetteProjectedPoint.setY(amivifExt.getProjectedPoint().getY());

        chouette.setObjectId(ObjectIdLecteur.getNouveauId(amivif.getObjectId(), "Place"));
        if (amivif.hasObjectVersion() && amivif.getObjectVersion() >= 1) {
            chouette.setObjectVersion(amivif.getObjectVersion());
        } else {
            chouette.setObjectVersion(1);
        }
        chouette.setCreationTime(amivif.getCreationTime());
        chouette.setCreatorId(amivif.getCreatorId());
        chouette.setProjectedPoint(chouetteProjectedPoint);
        chouette.setLatitude(new BigDecimal(0));
        chouette.setLongitude(new BigDecimal(0));
        chouette.setLongLatType(LongLatTypeType.WGS84);
        chouette.setName(amivif.getName());

        chouette.setContainedIn(amivif.getObjectId());

        return chouette;
    }

    public chouette.schema.StopArea[] atc(amivif.schema.StopArea[] amivifs) {
        if (amivifs == null) {
            return new chouette.schema.StopArea[0];
        }

        int total = amivifs.length;
        chouette.schema.StopArea[] chouettes = new chouette.schema.StopArea[total];

        for (int i = 0; i < total; i++) {
            chouettes[i] = atc(amivifs[i]);
        }
        return chouettes;
    }

    public chouette.schema.AreaCentroid[] atcPlace(amivif.schema.StopArea[] amivifs) {
        if (amivifs == null) {
            return new chouette.schema.AreaCentroid[0];
        }

        int total = amivifs.length;
        List<chouette.schema.AreaCentroid> chouettes = new ArrayList<chouette.schema.AreaCentroid>();

        for (int i = 0; i < total; i++) {
            chouette.schema.AreaCentroid areaCentroid = atcPlace(amivifs[i]);

            if (areaCentroid != null) {
                chouettes.add(areaCentroid);
            }
        }
        return chouettes.toArray(new chouette.schema.AreaCentroid[]{});
    }

    private amivif.schema.StopArea cta(chouette.schema.StopArea chouette,
            IAccesseurAreaStop accesseur) {
        amivif.schema.StopArea amivif = new amivif.schema.StopArea();

        amivif.setObjectId(chouette.getObjectId());
        if (chouette.hasObjectVersion()) {
            amivif.setObjectVersion(chouette.getObjectVersion());
        } else {
            amivif.setObjectVersion(1);
        }
        amivif.setCreationTime(chouette.getCreationTime());
        amivif.setCreatorId(chouette.getCreatorId());

        amivif.setName(chouette.getName());
        amivif.setComment(chouette.getComment());

        int totalContenues = chouette.getContainsCount();
        for (int i = 0; i < totalContenues; i++) {
            amivif.addContains(chouette.getContains(i));
        }

        AMIVIF_StopArea_Extension amivifExt = new AMIVIF_StopArea_Extension();
        if (chouette.getStopAreaExtension() != null) {
            amivifExt.setDownFarZone(0);
            amivifExt.setUpFarZone(chouette.getStopAreaExtension().getFareCode());
        }
        if (chouette.getCentroidOfArea() != null) {
            AreaCentroid centroid = accesseur.getCentroidById(chouette.getCentroidOfArea());
            ProjectedPoint projectedPoint = new ProjectedPoint();
            projectedPoint.setX(centroid.getProjectedPoint().getX());
            projectedPoint.setY(centroid.getProjectedPoint().getY());
            amivifExt.setProjectedPoint(projectedPoint);
        }
        amivif.setAMIVIF_StopArea_Extension(amivifExt);

        return amivif;
    }

    public amivif.schema.StopArea[] cta(chouette.schema.StopArea[] chouettes,
            IAccesseurAreaStop accesseur) {
        if (chouettes == null) {
            return null;
        }

        int total = chouettes.length;

        Collection<amivif.schema.StopArea> amivifs = new HashSet<amivif.schema.StopArea>();
        for (int i = 0; i < total; i++) {
            ChouetteAreaType areaType = chouettes[ i].getStopAreaExtension().getAreaType();
            if (ChouetteAreaType.STOPPLACE.equals(areaType)
                    || ChouetteAreaType.COMMERCIALSTOPPOINT.equals(areaType)) {
                amivifs.add(cta(chouettes[ i], accesseur));
            }
        }

        return amivifs.toArray(new amivif.schema.StopArea[0]);
    }
}
