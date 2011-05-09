package fr.certu.chouette.service.amivif.base;

public class TransportNetworkConverter {

    private RegistrationConverter registrationConverter = new RegistrationConverter();
    private SourceTypeTypeConverter sourceTypeTypeConverter = new SourceTypeTypeConverter();

    public chouette.schema.PTNetwork atc(amivif.schema.TransportNetwork amivifTransportNetwork) {
        if (amivifTransportNetwork == null) {
            return null;
        }
        chouette.schema.PTNetwork chouettePTNetwork = new chouette.schema.PTNetwork();
        chouettePTNetwork.setComment(amivifTransportNetwork.getComment());
        chouettePTNetwork.setCreationTime(amivifTransportNetwork.getCreationTime());
        chouettePTNetwork.setCreatorId(amivifTransportNetwork.getCreatorId());
        chouettePTNetwork.setDescription(amivifTransportNetwork.getDescription());
        chouettePTNetwork.setLineId(amivifTransportNetwork.getLineId());
        chouettePTNetwork.setName(amivifTransportNetwork.getName());
        chouettePTNetwork.setObjectId(amivifTransportNetwork.getObjectId());
        if (amivifTransportNetwork.hasObjectVersion() && amivifTransportNetwork.getObjectVersion() >= 1) {
            chouettePTNetwork.setObjectVersion(amivifTransportNetwork.getObjectVersion());
        } else {
            chouettePTNetwork.setObjectVersion(1);
        }
        chouettePTNetwork.setRegistration(registrationConverter.atc(amivifTransportNetwork.getRegistration()));
        chouettePTNetwork.setSourceIdentifier(amivifTransportNetwork.getSourceIdentifier());
        chouettePTNetwork.setSourceName(amivifTransportNetwork.getSourceName());
        chouettePTNetwork.setSourceType(sourceTypeTypeConverter.atc(amivifTransportNetwork.getSourceType()));
        chouettePTNetwork.setVersionDate(amivifTransportNetwork.getVersionDate());
        return chouettePTNetwork;
    }

    public chouette.schema.PTNetwork[] atc(amivif.schema.TransportNetwork[] amivifTransportNetworks) {
        if (amivifTransportNetworks == null) {
            return new chouette.schema.PTNetwork[0];
        }
        int totalPTNetworks = amivifTransportNetworks.length;
        chouette.schema.PTNetwork[] chouettePTNetworks = new chouette.schema.PTNetwork[totalPTNetworks];
        for (int j = 0; j < totalPTNetworks; j++) {
            chouettePTNetworks[j] = atc(amivifTransportNetworks[j]);
        }
        return chouettePTNetworks;
    }

    public amivif.schema.TransportNetwork cta(chouette.schema.PTNetwork chouettePTNetwork) {
        if (chouettePTNetwork == null) {
            return null;
        }
        amivif.schema.TransportNetwork amivifTransportNetwork = new amivif.schema.TransportNetwork();
        amivifTransportNetwork.setComment(chouettePTNetwork.getComment());
        amivifTransportNetwork.setCreationTime(chouettePTNetwork.getCreationTime());
        amivifTransportNetwork.setCreatorId(chouettePTNetwork.getCreatorId());
        amivifTransportNetwork.setDescription(chouettePTNetwork.getDescription());
        amivifTransportNetwork.setLineId(chouettePTNetwork.getLineId());
        amivifTransportNetwork.setName(chouettePTNetwork.getName());
        amivifTransportNetwork.setObjectId(chouettePTNetwork.getObjectId());
        if (chouettePTNetwork.hasObjectVersion() && chouettePTNetwork.getObjectVersion() >= 1) {
            amivifTransportNetwork.setObjectVersion(chouettePTNetwork.getObjectVersion());
        } else {
            amivifTransportNetwork.setObjectVersion(1);
        }
        amivifTransportNetwork.setRegistration(registrationConverter.cta(chouettePTNetwork.getRegistration()));
        amivifTransportNetwork.setSourceIdentifier(chouettePTNetwork.getSourceIdentifier());
        amivifTransportNetwork.setSourceName(chouettePTNetwork.getSourceName());
        amivifTransportNetwork.setSourceType(sourceTypeTypeConverter.cta(chouettePTNetwork.getSourceType()));
        amivifTransportNetwork.setVersionDate(chouettePTNetwork.getVersionDate());
        return amivifTransportNetwork;
    }

    public amivif.schema.TransportNetwork[] cta(chouette.schema.PTNetwork[] chouettePTNetworks) {
        if (chouettePTNetworks == null) {
            return new amivif.schema.TransportNetwork[0];
        }
        int totalTransportNetworks = chouettePTNetworks.length;
        amivif.schema.TransportNetwork[] amivifTransportNetworks = new amivif.schema.TransportNetwork[totalTransportNetworks];
        for (int j = 0; j < totalTransportNetworks; j++) {
            amivifTransportNetworks[j] = cta(chouettePTNetworks[j]);
        }
        return amivifTransportNetworks;
    }
}
