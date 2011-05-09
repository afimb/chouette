package fr.certu.chouette.service.amivif.base;

import fr.certu.chouette.service.amivif.IAccesseurAreaStop;

public class JourneyPatternConverter {

    private chouette.schema.JourneyPattern atc(amivif.schema.JourneyPattern amivif) {

        if (amivif == null) {
            return null;
        }

        chouette.schema.JourneyPattern chouette = new chouette.schema.JourneyPattern();

        chouette.setObjectId(amivif.getObjectId());
        if (amivif.hasObjectVersion() && amivif.getObjectVersion() >= 1) {
            chouette.setObjectVersion(amivif.getObjectVersion());
        } else {
            chouette.setObjectVersion(1);
        }
        chouette.setCreationTime(amivif.getCreationTime());
        chouette.setCreatorId(amivif.getCreatorId());
        chouette.setRouteId(amivif.getRouteId());
        chouette.setName(amivif.getName());
        chouette.setPublishedName(amivif.getPublishedName());
        chouette.setComment(amivif.getComment());

        int total = amivif.getStopPointListCount();

        for (int i = 0; i < total; i++) {
            chouette.addStopPointList(amivif.getStopPointList(i));
        }

        return chouette;
    }

    public chouette.schema.JourneyPattern[] atc(amivif.schema.JourneyPattern[] amivifs) {

        if (amivifs == null) {
            return new chouette.schema.JourneyPattern[0];
        }

        int total = amivifs.length;
        chouette.schema.JourneyPattern[] chouettes = new chouette.schema.JourneyPattern[total];

        for (int i = 0; i < total; i++) {
            chouettes[i] = atc(amivifs[i]);
        }
        return chouettes;
    }

    private amivif.schema.JourneyPattern cta(chouette.schema.JourneyPattern chouette,
            IAccesseurAreaStop accesseur) {
        amivif.schema.JourneyPattern amivif = new amivif.schema.JourneyPattern();

        amivif.setObjectId(chouette.getObjectId());
        if (chouette.hasObjectVersion() && chouette.getObjectVersion() >= 1) {
            amivif.setObjectVersion(chouette.getObjectVersion());
        } else {
            amivif.setObjectVersion(1);
        }
        amivif.setCreationTime(chouette.getCreationTime());
        amivif.setCreatorId(chouette.getCreatorId());

        amivif.setRouteId(chouette.getRouteId());
        amivif.setName(chouette.getName());
        amivif.setPublishedName(chouette.getPublishedName());
        amivif.setComment(chouette.getComment());


        int total = chouette.getStopPointListCount();
        for (int i = 0; i < total; i++) {
            amivif.addStopPointList(accesseur.getStopAreaOfStop(chouette.getStopPointList(i)).getObjectId());
        }
        return amivif;
    }

    public amivif.schema.JourneyPattern[] cta(chouette.schema.JourneyPattern[] chouettes,
            IAccesseurAreaStop accesseur) {

        if (chouettes == null) {
            return new amivif.schema.JourneyPattern[0];
        }

        int total = chouettes.length;
        amivif.schema.JourneyPattern[] amivifs = new amivif.schema.JourneyPattern[total];

        for (int i = 0; i < total; i++) {
            amivifs[i] = cta(chouettes[i], accesseur);
        }
        return amivifs;
    }
}
