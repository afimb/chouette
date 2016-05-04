package mobi.chouette.exchange.regtopp.messages;

import java.util.MissingResourceException;
import java.util.ResourceBundle;


public class RegtoppMessages {

    private static ResourceBundle bundle = ResourceBundle.getBundle("mobi.chouette.exchange.regtopp.customMessages");


    public static String getLabel(String key) {
        try {
            return bundle.getString("label." + key);
        }
        catch (MissingResourceException ex) {
            return null;
        }
    }

}
