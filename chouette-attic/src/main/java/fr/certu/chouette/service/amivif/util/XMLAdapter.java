package fr.certu.chouette.service.amivif.util;

public class XMLAdapter {

    private static final String AMIVIF_ADRESSE = "<address xsi:type=\"PostalAddressType\"";
    private static final String CHOUETTE_ADRESSE = "<address";

    public static String atcSimplify(String contenu) {
        return contenu.replaceAll(AMIVIF_ADRESSE, CHOUETTE_ADRESSE);
    }

    public static String ctaSimplify(String contenu) {
        return contenu.replaceAll(AMIVIF_ADRESSE, CHOUETTE_ADRESSE);
    }
}
