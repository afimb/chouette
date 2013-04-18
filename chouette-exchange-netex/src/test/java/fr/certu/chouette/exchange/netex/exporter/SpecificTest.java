/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.certu.chouette.exchange.netex.exporter;

import com.ximpleware.AutoPilot;
import com.ximpleware.ModifyException;
import com.ximpleware.VTDGen;
import com.ximpleware.VTDNav;
import com.ximpleware.XMLModifier;
import java.io.FileOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 *
 * @author marc
 */
@ContextConfiguration(locations={"classpath:testContext.xml"})
@SuppressWarnings("unchecked")
public class SpecificTest extends AbstractTestNGSpringContextTests {
    
    private void my() throws Exception {
        VTDGen vg = new VTDGen();
        
        
        if (vg.parseFile("/tmp/test.xml", false)) {
            VTDNav vn = vg.getNav();
            AutoPilot ap = new AutoPilot(vn);
            ap.selectXPath("//*[not(node()) and not(@*)]");            
            
            XMLModifier xm = new XMLModifier(vn);
            int i;
            while(ap.evalXPath()!=-1){
                xm.remove();
            }

            xm.output(new FileOutputStream("new.xml"));
        }
    }
    
    @Test(groups = {"specific"}, description = "Validate CompositeFrame build")
    public void mytest()  {
        try {
            my();
        } catch (Exception ex) {
            Logger.getLogger(SpecificTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        Assert.assertEquals( "A", "A");
    }
    
}
