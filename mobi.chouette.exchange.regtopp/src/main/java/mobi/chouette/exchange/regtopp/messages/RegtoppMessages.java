package mobi.chouette.exchange.regtopp.messages;

import lombok.extern.log4j.Log4j;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

@Log4j
public class RegtoppMessages {

    private static ResourceBundle bundle = ResourceBundle.getBundle("mobi.chouette.exchange.regtopp.customMessages");

    public static String getMessage(String key) {
        try {
            return bundle.getString(key);
        }
        catch (MissingResourceException ex) {
            log.warn("Could not get message for key '" + key + "'. Could this be due to a typo or a missing key.");
            return null;
        }
    }

}
