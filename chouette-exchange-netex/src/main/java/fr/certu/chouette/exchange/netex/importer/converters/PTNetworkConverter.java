package fr.certu.chouette.exchange.netex.importer.converters;

import com.ximpleware.AutoPilot;
import com.ximpleware.EOFException;
import com.ximpleware.EncodingException;
import com.ximpleware.EntityException;
import com.ximpleware.ParseException;
import com.ximpleware.VTDGen;
import com.ximpleware.VTDNav;
import com.ximpleware.XPathEvalException;
import com.ximpleware.XPathParseException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class PTNetworkConverter {
    
    VTDGen vg = new VTDGen();
    AutoPilot ap = new AutoPilot();

    public PTNetworkConverter(String fileName) throws FileNotFoundException, IOException, EncodingException, EOFException, EntityException, ParseException, XPathParseException, XPathEvalException {                
        // open a file and read the content into a byte array
        File f = new File(fileName);
        FileInputStream fis = new FileInputStream(f);
        byte[] b = new byte[(int) f.length()];
        fis.read(b);
        
        vg.setDoc(b);
        vg.parse(true); // set namespace awareness to true

        VTDNav vn = vg.getNav();
        AutoPilot ap = new AutoPilot(vn);
        ap.declareXPathNameSpace("netex","http://www.netex.org.uk/netex");
        ap.selectXPath("//ns1:*");
        int result = -1;
        int count = 0;
//        while((result = ap.evalXPath())!=-1){
//            System.out.print(""+result+" "); 
//            System.out.print("Element name ==> "+vn.toString(result));
//            int t = vn.getText(); // get the index of the text (char data or CDATA)
//            if (t!=-1)
//                System.out.println(" Text ==> "+vn.toNormalizedString(t));
//            System.out.println("\n ============================== ");
//            count++;
//        }
        System.out.println("Total # of element "+count);
        
        //define defaults xpath
        ap.selectXPath("/respPTLineStructTimetable/VehicleJourney");
        AutoPilot ap2 = new AutoPilot();
        ap2.selectXPath("*/stopPointId");
        
    }
            
     

    
    
    
}
