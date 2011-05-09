package fr.certu.chouette.service.amivif.base;

public class ConnectionLinkConverter {

    public chouette.schema.ConnectionLink atc(amivif.schema.ConnectionLink amivifConnectionLink) {
        if (amivifConnectionLink == null) {
            return null;
        }
        chouette.schema.ConnectionLink chouetteConnectionLink = new chouette.schema.ConnectionLink();
        chouetteConnectionLink.setObjectId(amivifConnectionLink.getObjectId());
        if (amivifConnectionLink.hasObjectVersion() && amivifConnectionLink.getObjectVersion() >= 1) {
            chouetteConnectionLink.setObjectVersion(amivifConnectionLink.getObjectVersion());
        } else {
            chouetteConnectionLink.setObjectVersion(1);
        }
        chouetteConnectionLink.setCreationTime(amivifConnectionLink.getCreationTime());
        chouetteConnectionLink.setCreatorId(amivifConnectionLink.getCreatorId());
        chouetteConnectionLink.setName(amivifConnectionLink.getName());
        chouetteConnectionLink.setComment(amivifConnectionLink.getComment());
        chouetteConnectionLink.setStartOfLink(amivifConnectionLink.getStartOfLink());
        chouetteConnectionLink.setEndOfLink(amivifConnectionLink.getEndOfLink());
        chouetteConnectionLink.setLinkDistance(amivifConnectionLink.getLinkDistance());
        if (amivifConnectionLink.getLinkType() != null) {
            chouetteConnectionLink.setLinkType(chouette.schema.types.ConnectionLinkTypeType.fromValue(amivifConnectionLink.getLinkType().toString()));
        }
        chouetteConnectionLink.setDefaultDuration(amivifConnectionLink.getDefaultDuration());
        chouetteConnectionLink.setFrequentTravellerDuration(amivifConnectionLink.getFrequentTravellerDuration());
        chouetteConnectionLink.setMobilityRestrictedTravellerDuration(amivifConnectionLink.getMobilityRestrictedTravellerDuration());
        chouetteConnectionLink.setOccasionalTravellerDuration(amivifConnectionLink.getOccasionalTravellerDuration());
        if (amivifConnectionLink.hasLiftAvailability()) {
            chouetteConnectionLink.setLiftAvailability(amivifConnectionLink.getLiftAvailability());
        }
        if (amivifConnectionLink.hasStairsAvailability()) {
            chouetteConnectionLink.setStairsAvailability(amivifConnectionLink.getStairsAvailability());
        }
        if (amivifConnectionLink.hasMobilityRestrictedSuitability()) {
            chouetteConnectionLink.setMobilityRestrictedSuitability(amivifConnectionLink.getMobilityRestrictedSuitability());
        }
        return chouetteConnectionLink;
    }

    public chouette.schema.ConnectionLink[] atc(amivif.schema.ConnectionLink[] amivifConnectionLinks) {
        if (amivifConnectionLinks == null) {
            return new chouette.schema.ConnectionLink[0];
        }
        int total = amivifConnectionLinks.length;
        chouette.schema.ConnectionLink[] chouetteConnectionLinks = new chouette.schema.ConnectionLink[total];
        for (int i = 0; i < total; i++) {
            chouetteConnectionLinks[i] = atc(amivifConnectionLinks[i]);
        }
        return chouetteConnectionLinks;
    }

    public amivif.schema.ConnectionLink cta(chouette.schema.ConnectionLink chouetteConnectionLink) {
        if (chouetteConnectionLink == null) {
            return null;
        }
        amivif.schema.ConnectionLink amivifConnectionLink = new amivif.schema.ConnectionLink();
        amivifConnectionLink.setObjectId(chouetteConnectionLink.getObjectId());
        if (chouetteConnectionLink.hasObjectVersion() && chouetteConnectionLink.getObjectVersion() >= 1) {
            amivifConnectionLink.setObjectVersion(chouetteConnectionLink.getObjectVersion());
        } else {
            amivifConnectionLink.setObjectVersion(1);
        }
        amivifConnectionLink.setCreationTime(chouetteConnectionLink.getCreationTime());
        amivifConnectionLink.setCreatorId(chouetteConnectionLink.getCreatorId());
        amivifConnectionLink.setName(chouetteConnectionLink.getName());
        amivifConnectionLink.setComment(chouetteConnectionLink.getComment());
        amivifConnectionLink.setStartOfLink(chouetteConnectionLink.getStartOfLink());
        amivifConnectionLink.setEndOfLink(chouetteConnectionLink.getEndOfLink());
        amivifConnectionLink.setLinkDistance(chouetteConnectionLink.getLinkDistance());
        if (chouetteConnectionLink.getLinkType() != null) {
            amivifConnectionLink.setLinkType(amivif.schema.types.ConnectionLinkTypeType.fromValue(chouetteConnectionLink.getLinkType().toString()));
        }
        amivifConnectionLink.setDefaultDuration(chouetteConnectionLink.getDefaultDuration());
        amivifConnectionLink.setFrequentTravellerDuration(chouetteConnectionLink.getFrequentTravellerDuration());
        amivifConnectionLink.setMobilityRestrictedTravellerDuration(chouetteConnectionLink.getMobilityRestrictedTravellerDuration());
        amivifConnectionLink.setOccasionalTravellerDuration(chouetteConnectionLink.getOccasionalTravellerDuration());
        if (chouetteConnectionLink.hasLiftAvailability()) {
            amivifConnectionLink.setLiftAvailability(chouetteConnectionLink.getLiftAvailability());
        }
        if (chouetteConnectionLink.hasStairsAvailability()) {
            amivifConnectionLink.setStairsAvailability(chouetteConnectionLink.getStairsAvailability());
        }
        if (chouetteConnectionLink.hasMobilityRestrictedSuitability()) {
            amivifConnectionLink.setMobilityRestrictedSuitability(chouetteConnectionLink.getMobilityRestrictedSuitability());
        }
        //amivif.schema.AMIVIF_ConnectionLink_Extension amivifConnectionLinkExtension = new amivif.schema.AMIVIF_ConnectionLink_Extension();
        //amivifConnectionLink.setAMIVIF_ConnectionLink_Extension(AMIVIF_ConnectionLink_Extension)
        //amivifConnectionLink.setExpiryTime(expiryTime)
        //amivifConnectionLink.setReferencingMethod(referencingMethod);
        //amivifConnectionLink.setTridentObjectTypeChoice(tridentObjectTypeChoice)
        return amivifConnectionLink;
    }

    public amivif.schema.ConnectionLink[] cta(chouette.schema.ConnectionLink[] chouetteConnectionLinks) {
        if (chouetteConnectionLinks == null) {
            return new amivif.schema.ConnectionLink[0];
        }
        int total = chouetteConnectionLinks.length;
        amivif.schema.ConnectionLink[] amivifConnectionLinks = new amivif.schema.ConnectionLink[total];
        for (int i = 0; i < total; i++) {
            amivifConnectionLinks[i] = cta(chouetteConnectionLinks[i]);
        }
        return amivifConnectionLinks;
    }
}
