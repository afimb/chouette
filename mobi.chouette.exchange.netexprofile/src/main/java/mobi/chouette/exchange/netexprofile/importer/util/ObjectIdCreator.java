package mobi.chouette.exchange.netexprofile.importer.util;

import mobi.chouette.exchange.netexprofile.importer.NetexprofileImportParameters;
import mobi.chouette.model.util.ObjectIdTypes;

public class ObjectIdCreator {

    public static String composeGenericObjectId(String prefix, String type, String id) {
        return prefix + ":" + type + ":" + id.trim();
    }

    public static String extractOriginalId(String objectId) {
        return objectId.split(":")[2];
    }

    // TODO find out what kind of prefix to use
    public static String createCompanyId(NetexprofileImportParameters configuration, String operatorCode) {
        //return ObjectIdCreator.composeGenericObjectId(configuration.getObjectIdPrefix(), ObjectIdTypes.COMPANY_KEY, operatorCode);
        return ObjectIdCreator.composeGenericObjectId("PREFIX", ObjectIdTypes.COMPANY_KEY, operatorCode);
    }

    // TODO find out what kind of prefix to use
    public static String createNetworkId(NetexprofileImportParameters configuration, String networkId) {
        //return ObjectIdCreator.composeGenericObjectId(configuration.getObjectIdPrefix(), ObjectIdTypes.COMPANY_KEY, operatorCode);
        return ObjectIdCreator.composeGenericObjectId("PREFIX", ObjectIdTypes.PTNETWORK_KEY, networkId);
    }

}
