package fr.certu.chouette.service.amivif.base;

import fr.certu.chouette.service.amivif.IAccesseurAreaStop;

public class VehicleConverter {

    private VehicleAtStopConverter atcAtStopConverter = new VehicleAtStopConverter();

    private chouette.schema.VehicleJourney atc(amivif.schema.VehicleJourney amivif) {

        if (amivif == null) {
            return null;
        }

        chouette.schema.VehicleJourney chouette = new chouette.schema.VehicleJourney();

        chouette.setObjectId(amivif.getObjectId());
        if (amivif.hasObjectVersion() && amivif.getObjectVersion() >= 1) {
            chouette.setObjectVersion(amivif.getObjectVersion());
        } else {
            chouette.setObjectVersion(1);
        }
        chouette.setCreationTime(amivif.getCreationTime());
        chouette.setCreatorId(amivif.getCreatorId());
        chouette.setFacility(amivif.getFacility());
        chouette.setJourneyPatternId(amivif.getJourneyPatternId());
        chouette.setLineIdShortcut(amivif.getLineIdShortcut());
        chouette.setOperatorId(amivif.getOperatorId());
        chouette.setPublishedJourneyIdentifier(amivif.getPublishedJourneyIdentifier());
        chouette.setPublishedJourneyName(amivif.getPublishedJourneyName());
        chouette.setRouteId(amivif.getRouteId());
        chouette.setVehicleTypeIdentifier(amivif.getVehicleTypeIdentifier());

        if (amivif.hasNumber()) {
            chouette.setNumber(amivif.getNumber());
        }

        chouette.setVehicleJourneyAtStop(atcAtStopConverter.atc(amivif.getVehicleJourneyAtStop()));
        int total = chouette.getVehicleJourneyAtStopCount();
        for (int i = 0; i < total; i++) {
            chouette.getVehicleJourneyAtStop(i).setVehicleJourneyId(chouette.getObjectId());
        }

        return chouette;
    }

    public chouette.schema.VehicleJourney[] atc(amivif.schema.VehicleJourney[] amivifs) {

        if (amivifs == null) {
            return new chouette.schema.VehicleJourney[0];
        }

        int total = amivifs.length;
        chouette.schema.VehicleJourney[] chouettes = new chouette.schema.VehicleJourney[total];

        for (int i = 0; i < total; i++) {
            chouettes[i] = atc(amivifs[i]);
        }
        return chouettes;
    }

    private amivif.schema.VehicleJourney cta(chouette.schema.VehicleJourney chouette,
            IAccesseurAreaStop accesseur) {
        if (chouette == null) {
            return null;
        }

        amivif.schema.VehicleJourney amivif = new amivif.schema.VehicleJourney();
        amivif.setObjectId(chouette.getObjectId());
        if (chouette.hasObjectVersion() && chouette.getObjectVersion() >= 1) {
            amivif.setObjectVersion(chouette.getObjectVersion());
        } else {
            amivif.setObjectVersion(1);
        }
        amivif.setCreationTime(chouette.getCreationTime());
        amivif.setCreatorId(chouette.getCreatorId());

        amivif.setFacility(chouette.getFacility());
        amivif.setJourneyPatternId(chouette.getJourneyPatternId());
        amivif.setLineIdShortcut(chouette.getLineIdShortcut());
        amivif.setOperatorId(chouette.getOperatorId());
        amivif.setPublishedJourneyIdentifier(chouette.getPublishedJourneyIdentifier());
        amivif.setPublishedJourneyName(chouette.getPublishedJourneyName());
        amivif.setRouteId(chouette.getRouteId());
        amivif.setVehicleTypeIdentifier(chouette.getVehicleTypeIdentifier());

        if (chouette.hasNumber()) {
            amivif.setNumber(chouette.getNumber());
        }

        amivif.setVehicleJourneyAtStop(atcAtStopConverter.cta(chouette.getVehicleJourneyAtStop(), accesseur));

        return amivif;
    }

    public amivif.schema.VehicleJourney[] cta(chouette.schema.VehicleJourney[] chouettes,
            IAccesseurAreaStop accesseur) {

        if (chouettes == null) {
            return new amivif.schema.VehicleJourney[0];
        }

        int total = chouettes.length;
        amivif.schema.VehicleJourney[] amivifs = new amivif.schema.VehicleJourney[total];

        for (int i = 0; i < total; i++) {
            amivifs[i] = cta(chouettes[i], accesseur);
        }
        return amivifs;
    }
}
