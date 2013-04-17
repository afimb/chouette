/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.certu.chouette.exchange.netex.exporter;

import fr.certu.chouette.exchange.netex.ModelTranslator;
import fr.certu.chouette.model.neptune.GroupOfLine;
import java.text.ParseException;

import javax.xml.xpath.XPathExpressionException;
import org.testng.annotations.Test;

@Test(groups = {"GroupOfLines"}, description = "Validate GroupOfLines export in NeTEx format")
public class GroupOfLinesTest extends ChouetteModelTest {
    
    @Test(groups = { "ServiceFrame", "groupOfLines"}, description = "Validate presence of GroupOfLines element with expected id")
    public void verifyGroupOfLinesId() throws XPathExpressionException, ParseException {
        assertXPathCount( "count(//netex:ServiceFrame/netex:groupsOfLines/netex:GroupOfLines)", 
                             line.getGroupOfLines().size());
        for( GroupOfLine groupOfLine : line.getGroupOfLines()) {
            assertXPathTrue( "boolean(//netex:ServiceFrame/netex:groupsOfLines/netex:GroupOfLines[@id = '"+
                    modelTranslator.netexId(groupOfLine)+"'])");
        }
    }
    
    @Test(groups = {"ServiceFrame", "groupOfLines"}, description = "Validate presence of GroupOfLines element with expected name")
    public void verifyGroupOfLinesName() throws XPathExpressionException, ParseException {
        for( GroupOfLine groupOfLine : line.getGroupOfLines()) {
            assertXPathTrue( "boolean(//netex:GroupOfLines[@id = '"+
                    modelTranslator.netexId(groupOfLine) +
                    "']/netex:Name/text()='"+groupOfLine.getName()+"')");
        }
    }
    
    @Test(groups = {"ServiceFrame", "groupOfLines"}, description = "Validate presence of GroupOfLines element with expected comment")
    public void verifyGroupOfLinesComment() throws XPathExpressionException, ParseException {
        for( GroupOfLine groupOfLine : line.getGroupOfLines()) {
            assertXPathTrue( "boolean(//netex:GroupOfLines[@id = '"+
                    modelTranslator.netexId(groupOfLine) +
                    "']/netex:Description/text()='"+groupOfLine.getComment()+"')");
        }
    }
    
}
