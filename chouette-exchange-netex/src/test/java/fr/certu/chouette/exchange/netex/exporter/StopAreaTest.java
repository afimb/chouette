/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.certu.chouette.exchange.netex.exporter;

import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.StopArea;

import java.text.ParseException;
import java.util.List;
import javax.xml.xpath.XPathExpressionException;
import junit.framework.Assert;
import org.testng.annotations.Test;

/**
 * 
 * @author marc
 */
@Test(groups = { "StopArea" }, description = "Validate StopArea export in NeTEx format")
public class StopAreaTest extends ChouetteModelTest
{

   @Test(groups = { "ServiceFrame", "routingConstraintZones" }, description = "Validate presence of ITL StopPlace with expected id")
   public void verifyRoutingConstraintZoneName()
         throws XPathExpressionException, ParseException
   {
      List<StopArea> routingConstraints = line.getRoutingConstraints();

      for (StopArea routingConstraint : routingConstraints)
      {
         if (routingConstraint.getName() != null)
         {
            String xPathExpr = "boolean(//netex:ServiceFrame/netex:routingConstraintZones/"
                  + "netex:RoutingConstraintZone"
                  + "[@id = '"
                  + modelTranslator.netexId(routingConstraint)
                  + "']/"
                  + "netex:Name/text() = '"
                  + routingConstraint.getName()
                  + "')";
            assertXPathTrue(xPathExpr);
         }
      }
   }

   @Test(groups = { "ServiceFrame", "routingConstraintZones" }, description = "Validate presence of ITL StopPlace with expected description")
   public void verifyRoutingConstraintZoneDescription()
         throws XPathExpressionException, ParseException
   {
      List<StopArea> routingConstraints = line.getRoutingConstraints();

      for (StopArea routingConstraint : routingConstraints)
      {
         if (routingConstraint.getName() != null)
         {
            String xPathExpr = "boolean(//netex:ServiceFrame/netex:routingConstraintZones/"
                  + "netex:RoutingConstraintZone"
                  + "[@id = '"
                  + modelTranslator.netexId(routingConstraint)
                  + "']/"
                  + "netex:Description/text() = '"
                  + routingConstraint.getComment() + "')";
            assertXPathTrue(xPathExpr);
         }
      }
   }

   @Test(groups = { "ServiceFrame", "routingConstraintZones" }, description = "Validate presence of ITL StopPlace with expected privetCode")
   public void verifyRoutingConstraintZonePrivateCode()
         throws XPathExpressionException, ParseException
   {
      List<StopArea> routingConstraints = line.getRoutingConstraints();

      for (StopArea routingConstraint : routingConstraints)
      {
         if (routingConstraint.getName() != null)
         {
            String xPathExpr = "boolean(//netex:ServiceFrame/netex:routingConstraintZones/"
                  + "netex:RoutingConstraintZone"
                  + "[@id = '"
                  + modelTranslator.netexId(routingConstraint)
                  + "']/"
                  + "netex:PrivateCode/text() = '"
                  + routingConstraint.getRegistrationNumber() + "')";
            assertXPathTrue(xPathExpr);
         }
      }
   }

   @Test(groups = { "ServiceFrame", "routingConstraintZones" }, description = "Validate presence of ITL StopPlace with expected ZoneUse")
   public void verifyRoutingConstraintZoneZoneUse()
         throws XPathExpressionException, ParseException
   {
      List<StopArea> routingConstraints = line.getRoutingConstraints();

      for (StopArea routingConstraint : routingConstraints)
      {
         String xPathExpr = "boolean(//netex:ServiceFrame/netex:routingConstraintZones/"
               + "netex:RoutingConstraintZone"
               + "[@id = '"
               + modelTranslator.netexId(routingConstraint)
               + "']/"
               + "netex:ZoneUse/text() = 'cannotBoardAndAlightInSameZone')";
         assertXPathTrue(xPathExpr);
      }
   }

   @Test(groups = { "ServiceFrame", "routingConstraintZones" }, description = "Validate presence of ITL StopPlace with expected LineRefs")
   public void verifyRoutingConstraintZoneLineRef()
         throws XPathExpressionException, ParseException
   {
      List<StopArea> routingConstraints = line.getRoutingConstraints();

      for (StopArea routingConstraint : routingConstraints)
      {
         for (Line line : routingConstraint.getRoutingConstraintLines())
         {
            String xPathExpr = "boolean(//netex:ServiceFrame/netex:routingConstraintZones/"
                  + "netex:RoutingConstraintZone"
                  + "[@id = '"
                  + modelTranslator.netexId(routingConstraint)
                  + "']/"
                  + "netex:lines/"
                  + "netex:LineRef[@ref = '"
                  + modelTranslator.netexId(line) + "'])";
            assertXPathTrue(xPathExpr);
         }
      }
   }

   @Test(groups = { "ServiceFrame", "routingConstraintZones" }, description = "Validate presence of ITL StopPlace with expected name")
   public void verifyRoutingConstraintZoneLines()
         throws XPathExpressionException, ParseException
   {

      List<StopArea> routingConstraints = line.getRoutingConstraints();
      Assert.assertTrue(routingConstraints.size() > 0);

      for (StopArea routingConstraint : routingConstraints)
      {
         String xPathExpr = "boolean(//netex:ServiceFrame/netex:routingConstraintZones/"
               + "netex:RoutingConstraintZone"
               + "[@id = '"
               + modelTranslator.netexId(routingConstraint) + "'])";
         assertXPathTrue(xPathExpr);
      }
   }

   @Test(groups = { "SiteFrame", "stopPlaces" }, description = "Validate presence of StopPlace with expected id")
   public void verifyStopPlaceId() throws XPathExpressionException,
         ParseException
   {

      List<StopArea> topoPoints = line.getStopPlaces();

      for (StopArea topoPoint : topoPoints)
      {
         String xPathExpr = "boolean(//netex:SiteFrame/netex:stopPlaces/"
               + "netex:StopPlace" + "[@id = '"
               + modelTranslator.netexId(topoPoint) + "'])";
         assertXPathTrue(xPathExpr);
      }
   }

   @Test(groups = { "SiteFrame", "stopPlaces" }, description = "Validate presence of parent StopPlace with expected name")
   public void verifyStopPlaceParentName() throws XPathExpressionException,
         ParseException
   {

      List<StopArea> topoPoints = line.getStopPlaces();

      for (StopArea topoPoint : topoPoints)
      {
         String xPathExpr = "boolean(//netex:SiteFrame/netex:stopPlaces/"
               + "netex:StopPlace[not(netex:quays/netex:Quay)]/"
               + "netex:Name/" + "text() = '" + topoPoint.getName() + "')";
         assertXPathTrue(xPathExpr);
      }
   }

   @Test(groups = { "SiteFrame", "stopPlaces" }, description = "Validate presence of expected amount of StopPlace")
   public void verifyStopPlaceCount() throws XPathExpressionException,
         ParseException
   {
      String xPathExpr = "count(//netex:SiteFrame/netex:stopPlaces/"
            + "netex:StopPlace)";
      assertXPathCount(xPathExpr, line.getStopPlaces().size()
            + line.getCommercialStopPoints().size());
   }

   @Test(groups = { "SiteFrame", "stopPlaces" }, description = "Validate presence of StopPlace with expected id")
   public void verifyId() throws XPathExpressionException, ParseException
   {
      List<StopArea> quays = line.getQuays();
      quays.addAll(line.getBoardingPositions());

      for (StopArea quay : quays)
      {
         String xPathExpr = "boolean(//netex:SiteFrame/netex:stopPlaces/"
               + "netex:StopPlace" + "[@id = '"
               + modelTranslator.netexId(quay.getParent()) + "'])";
         assertXPathTrue(xPathExpr);
      }
   }

   @Test(groups = { "SiteFrame", "stopPlaces" }, description = "Validate presence of ParentZoneRef with expected ref")
   public void verifyParentZoneRef() throws XPathExpressionException,
         ParseException
   {
      List<StopArea> commercials = line.getCommercialStopPoints();

      for (StopArea commercial : commercials)
      {
         if (commercial.getParent() != null)
         {
            String xPathExpr = "boolean(//netex:SiteFrame/netex:stopPlaces/"
                  + "netex:StopPlace/netex:ParentZoneRef" + "[@ref = '"
                  + modelTranslator.netexId(commercial.getParent()) + "'])";
            assertXPathTrue(xPathExpr);
         }
      }
   }

   @Test(groups = { "SiteFrame", "stopPlaces" }, description = "Validate presence of Quay with expected id")
   public void verifyQuayId() throws XPathExpressionException, ParseException
   {
      List<StopArea> quays = line.getQuays();
      quays.addAll(line.getBoardingPositions());

      for (StopArea quay : quays)
      {
         String xPathExpr = "boolean(//netex:SiteFrame/netex:stopPlaces/"
               + "netex:StopPlace/netex:quays/netex:Quay" + "[@id = '"
               + modelTranslator.netexId(quay) + "'])";
         assertXPathTrue(xPathExpr);
      }
   }

   @Test(groups = { "SiteFrame", "stopPlaces" }, description = "Validate presence of Quay with expected comment")
   public void verifyQuayComment() throws XPathExpressionException,
         ParseException
   {
      List<StopArea> quays = line.getQuays();
      quays.addAll(line.getBoardingPositions());

      for (StopArea quay : quays)
      {
         String xPathExpr = "boolean(//netex:SiteFrame/netex:stopPlaces/"
               + "netex:StopPlace/netex:quays/netex:Quay" + "[@id = '"
               + modelTranslator.netexId(quay)
               + "']/netex:Description/text() = '" + quay.getComment() + "')";
         assertXPathTrue(xPathExpr);
      }
   }

   @Test(groups = { "SiteFrame", "stopPlaces" }, description = "Validate presence of Quay with expected name")
   public void verifyQuayName() throws XPathExpressionException, ParseException
   {
      List<StopArea> quays = line.getQuays();
      quays.addAll(line.getBoardingPositions());

      for (StopArea quay : quays)
      {
         String xPathExpr = "boolean(//netex:SiteFrame/netex:stopPlaces/"
               + "netex:StopPlace/netex:quays/netex:Quay" + "[@id = '"
               + modelTranslator.netexId(quay) + "']/netex:Name/text() = '"
               + quay.getName() + "')";
         assertXPathTrue(xPathExpr);
      }
   }

   @Test(groups = { "SiteFrame", "stopPlaces" }, description = "Validate presence of Quay with expected nearestTopicName")
   public void verifyQuayNearestTopicName() throws XPathExpressionException,
         ParseException
   {
      List<StopArea> quays = line.getQuays();
      quays.addAll(line.getBoardingPositions());

      for (StopArea quay : quays)
      {
         String xPathExpr = "boolean(//netex:SiteFrame/netex:stopPlaces/"
               + "netex:StopPlace/netex:quays/netex:Quay" + "[@id = '"
               + modelTranslator.netexId(quay) + "']/netex:Landmark/text() = '"
               + quay.getNearestTopicName() + "')";
         assertXPathTrue(xPathExpr);
      }
   }

   @Test(groups = { "SiteFrame", "stopPlaces" }, description = "Validate presence of Quay with expected streetName")
   public void verifyQuayStreetName() throws XPathExpressionException,
         ParseException
   {
      List<StopArea> quays = line.getQuays();
      quays.addAll(line.getBoardingPositions());

      for (StopArea quay : quays)
      {
         String xPathExpr = "boolean(//netex:SiteFrame/netex:stopPlaces/"
               + "netex:StopPlace/netex:quays/netex:Quay" + "[@id = '"
               + modelTranslator.netexId(quay)
               + "']//netex:AddressLine1/text() = '" + quay.getStreetName()
               + "')";
         assertXPathTrue(xPathExpr);
      }
   }

   @Test(groups = { "SiteFrame", "stopPlaces" }, description = "Validate presence of Quay with expected countryCode")
   public void verifyQuayPostCode() throws XPathExpressionException,
         ParseException
   {
      List<StopArea> quays = line.getQuays();
      quays.addAll(line.getBoardingPositions());

      for (StopArea quay : quays)
      {
         String xPathExpr = "boolean(//netex:SiteFrame/netex:stopPlaces/"
               + "netex:StopPlace/netex:quays/netex:Quay" + "[@id = '"
               + modelTranslator.netexId(quay)
               + "']//netex:PostCode/text() = '" + quay.getCountryCode() + "')";
         assertXPathTrue(xPathExpr);
      }
   }

   @Test(groups = { "SiteFrame", "stopPlaces" }, description = "Validate presence of Quay with expected registrationNumber")
   public void verifyQuayRegistrationNumber() throws XPathExpressionException,
         ParseException
   {
      List<StopArea> quays = line.getQuays();
      quays.addAll(line.getBoardingPositions());

      for (StopArea quay : quays)
      {
         String xPathExpr = "boolean(//netex:SiteFrame/netex:stopPlaces/"
               + "netex:StopPlace/netex:quays/netex:Quay" + "[@id = '"
               + modelTranslator.netexId(quay)
               + "']/netex:PrivateCode/text() = '"
               + quay.getRegistrationNumber() + "')";
         assertXPathTrue(xPathExpr);
      }
   }

   @Test(groups = { "SiteFrame", "stopPlaces" }, description = "Validate presence of Location with expected id")
   public void verifyLocation() throws XPathExpressionException, ParseException
   {
      List<StopArea> quays = line.getQuays();
      quays.addAll(line.getBoardingPositions());

      for (StopArea quay : quays)
      {
         String xPathExpr = "boolean(//netex:SiteFrame/netex:stopPlaces/"
               + "netex:StopPlace/netex:quays/netex:Quay" + "[@id = '"
               + modelTranslator.netexId(quay)
               + "']/netex:Centroid/netex:Location" + "[@id = '"
               + quay.objectIdPrefix() + ":Location:" + quay.objectIdSuffix()
               + "'])";
         assertXPathTrue(xPathExpr);
      }
   }

   @Test(groups = { "SiteFrame", "stopPlaces" }, description = "Validate presence of Quay with expected latitude")
   public void verifyQuayLatitude() throws XPathExpressionException,
         ParseException
   {
      List<StopArea> quays = line.getQuays();
      quays.addAll(line.getBoardingPositions());

      for (StopArea quay : quays)
      {
         String xPathExpr = "boolean(//netex:SiteFrame/netex:stopPlaces/"
               + "netex:StopPlace/netex:quays/netex:Quay" + "[@id = '"
               + modelTranslator.netexId(quay)
               + "']/netex:Centroid/netex:Location/" + "netex:Latitude/"
               + "text() = '" + quay.getLatitude() + "')";
         assertXPathTrue(xPathExpr);
      }
   }

   @Test(groups = { "SiteFrame", "stopPlaces" }, description = "Validate presence of Quay with expected longitude")
   public void verifyQuayLongitude() throws XPathExpressionException,
         ParseException
   {
      List<StopArea> quays = line.getQuays();
      quays.addAll(line.getBoardingPositions());

      for (StopArea quay : quays)
      {
         String xPathExpr = "boolean(//netex:SiteFrame/netex:stopPlaces/"
               + "netex:StopPlace/netex:quays/netex:Quay" + "[@id = '"
               + modelTranslator.netexId(quay)
               + "']/netex:Centroid/netex:Location/" + "netex:Longitude/"
               + "text() = '" + quay.getLongitude() + "')";
         assertXPathTrue(xPathExpr);
      }
   }

   @Test(groups = { "SiteFrame", "stopPlaces" }, description = "Validate presence of Quay with expected longlattype")
   public void verifyQuayProjectionType() throws XPathExpressionException,
         ParseException
   {
      List<StopArea> quays = line.getQuays();
      quays.addAll(line.getBoardingPositions());

      for (StopArea quay : quays)
      {
         String xPathExpr = "boolean(//netex:SiteFrame/netex:stopPlaces/"
               + "netex:StopPlace/netex:quays/netex:Quay" + "[@id = '"
               + modelTranslator.netexId(quay)
               + "']/netex:Centroid/netex:Location/" + "gml:pos"
               + "[@srsName = '" + quay.getProjectionType() + "'])";
         assertXPathTrue(xPathExpr);
      }
   }
}
