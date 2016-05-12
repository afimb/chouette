package mobi.chouette.exchange.regtopp.messages;

import org.testng.annotations.Test;

import java.nio.charset.Charset;
import java.util.Locale;

import static org.testng.Assert.assertEquals;

public class RegtoppMessagesTest {

    @Test
    public void testResourceBundleEncoding() throws Exception {
        Locale.setDefault(new Locale("nb", "NO"));
        String message = new String(RegtoppMessages.getMessage("label.validation.oneOf").getBytes(), Charset.forName("iso-8859-1"));
        assertEquals(message, "En av fÃ¯Â¿Â½lgende felter mÃ¯Â¿Â½ vÃ¯Â¿Â½re satt", "Please verify that the encoding in customMessages_no.properties is iso-8859-1.");
        assertEquals(message.getBytes(), "En av fÃ¯Â¿Â½lgende felter mÃ¯Â¿Â½ vÃ¯Â¿Â½re satt".getBytes(), "Please verify that the encoding in customMessages_no.properties is iso-8859-1.");
    }

}
