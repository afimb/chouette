/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.certu.chouette.exchange.netex.exporter;

import java.text.ParseException;

import javax.xml.xpath.XPathExpressionException;
import org.apache.commons.lang.StringEscapeUtils;
import org.testng.annotations.Test;

@Test(groups = {"Line"}, description = "Validate Line export in NeTEx format")
public class LineTest extends ChouetteModelTest {
    
    private String lineId() {
        return modelTranslator.netexId(line);
    }
    
    @Test(groups = { "ServiceFrame", "lines"}, description = "Validate presence of Line element with expected id")
    public void verifyLineId() throws XPathExpressionException, ParseException {
        assertXPathCount( "count(//netex:ServiceFrame/netex:lines/netex:Line)", 
                             1);
        assertXPathTrue( "boolean(//netex:ServiceFrame/netex:lines/netex:Line[@id = '"+
                lineId()+"'])");
    }
    
    @Test(groups = {"ServiceFrame", "lines"}, description = "Validate presence of Line element with expected name")
    public void verifyLineName() throws XPathExpressionException, ParseException {
        assertXPathTrue( "boolean(//netex:Line[@id = '"+
                        lineId()+
                        "']/netex:Name/text()='"+line.getName()+"')");
    }
    
    @Test(groups = {"ServiceFrame", "lines"}, description = "Validate presence of Line element with expected comment")
    public void verifyLineComment() throws XPathExpressionException, ParseException {
        assertXPathTrue( "boolean(//netex:Line[@id = '"+
                lineId()+
                "']/netex:Description/text()='"+line.getComment()+"')");
    }
    
    @Test(groups = {"ServiceFrame", "lines"}, description = "Validate presence of Line element with expected registrationNumber")
    public void verifyLineRegistrationNumber() throws XPathExpressionException, ParseException {
        assertXPathTrue( "boolean(//netex:Line[@id = '"+
                lineId()+
                "']/netex:PrivateCode/text()='"+line.getRegistrationNumber()+"')");
    }
    
    @Test(groups = {"ServiceFrame", "lines"}, description = "Validate presence of Line element with expected number")
    public void verifyLineNumber() throws XPathExpressionException, ParseException {
        assertXPathTrue( "boolean(//netex:Line[@id = '"+
                lineId()+
                "']/netex:PublicCode/text()='"+line.getNumber()+"')");
    }
    
    @Test(groups = {"ServiceFrame", "lines"}, description = "Validate presence of Line element with expected publishedName")
    public void verifyLinePublishedName() throws XPathExpressionException, ParseException {
        assertXPathTrue( "boolean(//netex:Line[@id = '"+
                lineId()+
                "']/netex:ShortName/text()='"+
                StringEscapeUtils.escapeXml(line.getPublishedName())+"')");
    }
    
}
