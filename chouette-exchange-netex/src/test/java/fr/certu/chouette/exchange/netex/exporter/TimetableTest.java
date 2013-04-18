/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.certu.chouette.exchange.netex.exporter;

import fr.certu.chouette.exchange.netex.ModelTranslator;
import fr.certu.chouette.model.neptune.Period;
import fr.certu.chouette.model.neptune.Timetable;

import fr.certu.chouette.model.neptune.type.DayTypeEnum;
import java.text.ParseException;
import java.util.List;
import java.util.Date;
import javax.xml.xpath.XPathExpressionException;
import org.testng.annotations.Test;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 *
 * @author marc
 */
@Test(groups = {"Timetable"}, description = "Validate Timetable export in NeTEx format")
public class TimetableTest extends ChouetteModelTest {
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    ModelTranslator enumTranslator = new ModelTranslator();
    
    @Test(groups = {"ServiceCalendarFrame"}, description = "Validate presence of ServiceCalendarFrame with expected id")
    public void verifyServiceCalendarFrameId() throws XPathExpressionException, ParseException {
        
        List<Timetable> timetables = line.getTimetables();
        
        int i = 0;
        for( Timetable timetable : timetables) {
            String xPathExpr = "boolean(//netex:ServiceCalendarFrame"+
                                "[@id = '"+
                                timetable.objectIdPrefix()+
                                ":ServiceCalendarFrame:SFC"+
                                i+
                                "'])";
            assertXPathTrue( xPathExpr);
            i++;
        }
    }
    
    @Test(groups = {"ServiceCalendarFrame"}, description = "Validate presence of ServiceCalendar with expected id")
    public void verifyServiceCalendarId() throws XPathExpressionException, ParseException {
        
        List<Timetable> timetables = line.getTimetables();
        
        int i = 0;
        for( Timetable timetable : timetables) {
            String xPathExpr = "boolean(//netex:ServiceCalendarFrame"+
                                "/netex:ServiceCalendar"+
                                "[@id = '"+
                                timetable.objectIdPrefix()+
                                ":ServiceCalendar:SFC"+
                                i+
                                "'])";
            assertXPathTrue( xPathExpr);
            i++;
        }
    }
    
    @Test(groups = {"ServiceCalendarFrame", "dayTypes"}, description = "Validate presence of DayType with expected id")
    public void verifyDayTypeId() throws XPathExpressionException, ParseException {
        
        List<Timetable> timetables = line.getTimetables();
        
        for( Timetable timetable : timetables) {
            String xPathExpr = "boolean(//netex:ServiceCalendarFrame"+
                                "/netex:dayTypes"+
                                "/netex:DayType"+
                                "[@id = '"+
                                modelTranslator.netexId( timetable)+
                                "'])";
            assertXPathTrue( xPathExpr);
        }
    }
    
    @Test(groups = {"ServiceCalendarFrame", "dayTypes"}, description = "Validate presence of DayType with expected Name")
    public void verifyDayTypeName() throws XPathExpressionException, ParseException {
        
        List<Timetable> timetables = line.getTimetables();
        
        for( Timetable timetable : timetables) {
            String xPathExpr = "boolean(//netex:ServiceCalendarFrame"+
                                "/netex:dayTypes"+
                                "/netex:DayType"+
                                "[@id = '"+
                                modelTranslator.netexId( timetable)+
                                "']/netex:Name/text()='"+
                                timetable.getVersion()+"')";
            assertXPathTrue( xPathExpr);
        }
    }
    
    @Test(groups = {"ServiceCalendarFrame", "dayTypes"}, description = "Validate presence of DayType with expected ShortName")
    public void verifyDayTypeShortName() throws XPathExpressionException, ParseException {
        
        List<Timetable> timetables = line.getTimetables();
        
        for( Timetable timetable : timetables) {
            String xPathExpr = "boolean(//netex:ServiceCalendarFrame"+
                                "/netex:dayTypes"+
                                "/netex:DayType"+
                                "[@id = '"+
                                modelTranslator.netexId( timetable)+
                                "']/netex:ShortName/text()='"+
                                timetable.getComment()+"')";
            assertXPathTrue( xPathExpr);
        }
    }
    @Test(groups = {"ServiceCalendarFrame", "dayTypes"}, description = "Validate presence of DayType with expected properties")
    public void verifyDayTypeProperties() throws XPathExpressionException, ParseException {
        
        List<Timetable> timetables = line.getTimetables();
        
        for( Timetable timetable : timetables) {
            for ( DayTypeEnum dtenum : timetable.getDayTypes()) {
                if( enumTranslator.toDayTypeNetex(dtenum) != null )
                {
                    String xPathExpr = "boolean(//netex:ServiceCalendarFrame"+
                            "/netex:dayTypes"+
                            "/netex:DayType"+
                            "[@id = '"+
                            modelTranslator.netexId( timetable)+
                            "']/netex:properties/netex:PropertyOfDay/"+
                            "netex:DaysOfWeek/"+
                            "text()='"+
                            dtenum.value()+"')";
                    assertXPathTrue( xPathExpr);
                }
            }
        }
    }
    @Test(groups = {"ServiceCalendarFrame", "operatingPeriods"}, description = "Validate presence of OperatingPeriod with expected Id")
    public void verifyOperatingPeriodId() throws XPathExpressionException, ParseException {
        
        List<Timetable> timetables = line.getTimetables();
        
        for( Timetable timetable : timetables) {
            for ( Period period : timetable.getPeriods()) {
                String xPathExpr = "boolean(//netex:ServiceCalendarFrame"+
                                    "/netex:operatingPeriods"+
                                    "/netex:OperatingPeriod"+
                                    "[@id = '"+
                                        timetable.objectIdPrefix()+
                                        ":OperatingPeriod:"+
                                        timetable.objectIdSuffix()+
                                        "S"+dateFormat.format( period.getStartDate())+
                                        "E"+dateFormat.format( period.getEndDate())+
                                    "']"+
                                    ")";
                assertXPathTrue( xPathExpr);
            }
        }
    }
    @Test(groups = {"ServiceCalendarFrame", "operatingPeriods"}, description = "Validate presence of OperatingPeriod with expected FromDate")
    public void verifyOperatingPeriodFromDate() throws XPathExpressionException, ParseException {
        
        List<Timetable> timetables = line.getTimetables();
        
        for( Timetable timetable : timetables) {
            for ( Period period : timetable.getPeriods()) {
                String xPathExpr = "boolean(//netex:ServiceCalendarFrame"+
                                    "/netex:operatingPeriods"+
                                    "/netex:OperatingPeriod/netex:FromDate/"+
                                    "text() = '"+
                                    dateTimeFormat.format( period.getStartDate())+
                                    "')";
                assertXPathTrue( xPathExpr);
            }
        }
    }
    @Test(groups = {"ServiceCalendarFrame", "operatingPeriods"}, description = "Validate presence of OperatingPeriod with expected ToDate")
    public void verifyOperatingPeriodToDate() throws XPathExpressionException, ParseException {
        
        List<Timetable> timetables = line.getTimetables();
        
        for( Timetable timetable : timetables) {
            for ( Period period : timetable.getPeriods()) {
                String xPathExpr = "boolean(//netex:ServiceCalendarFrame"+
                                    "/netex:operatingPeriods"+
                                    "/netex:OperatingPeriod/netex:ToDate/"+
                                    "text() = '"+
                                    dateTimeFormat.format( period.getEndDate())+
                                    "')";
                assertXPathTrue( xPathExpr);
            }
        }
    }
    @Test(groups = {"ServiceCalendarFrame", "operatingDays"}, description = "Validate presence of OperatingDay with expected Id")
    public void verifyOperatingDayId() throws XPathExpressionException, ParseException {
        
        List<Timetable> timetables = line.getTimetables();
        
        for( Timetable timetable : timetables) {
            for ( Date day : timetable.getCalendarDays()) {
                String xPathExpr = "boolean(//netex:ServiceCalendarFrame"+
                                    "/netex:operatingDays"+
                                    "/netex:OperatingDay"+
                                    "[@id = '"+
                                        timetable.objectIdPrefix()+
                                        ":OperatingDay:"+
                                        timetable.objectIdSuffix()+
                                        "D"+dateFormat.format( day)+
                                    "']"+
                                    ")";
                assertXPathTrue( xPathExpr);
            }
        }
    }
    @Test(groups = {"ServiceCalendarFrame", "operatingDays"}, description = "Validate presence of OperatingDay with expected calendarDate")
    public void verifyOperatingDayDay() throws XPathExpressionException, ParseException {
        
        List<Timetable> timetables = line.getTimetables();
        
        for( Timetable timetable : timetables) {
            for ( Date day : timetable.getCalendarDays()) {
                String xPathExpr = "boolean(//netex:ServiceCalendarFrame"+
                                    "/netex:operatingDays"+
                                    "/netex:OperatingDay/netex:CalendarDate/"+
                                    "text() = '"+
                                    dateFormat.format( day)+
                                    "')";
                assertXPathTrue( xPathExpr);
            }
        }
    }
    @Test(groups = {"ServiceCalendarFrame", "dayTypeAssignments"}, description = "Validate count of DayTypeAssignments with expected DayTypeRef")
    public void verifyDayTypeAssignment() throws XPathExpressionException, ParseException {
        
        List<Timetable> timetables = line.getTimetables();
        
        for( Timetable timetable : timetables) {
            String xPathExpr = "count(//netex:ServiceCalendarFrame"+
                                "/netex:dayTypeAssignments"+
                                "/netex:DayTypeAssignment/netex:DayTypeRef"+
                                "[@ref = '"+
                                modelTranslator.netexId( timetable)+
                                "'])";
            assertXPathCount( xPathExpr, 
                    timetable.getCalendarDays().size()+
                    timetable.getPeriods().size());
        }
    }
    @Test(groups = {"ServiceCalendarFrame", "dayTypeAssignments"}, description = "Validate count of DayTypeAssignments with expected OperatingPeriodRef")
    public void verifyDayTypeAssignmentOperatingPeriodRef() throws XPathExpressionException, ParseException {
        
        List<Timetable> timetables = line.getTimetables();
        
        for( Timetable timetable : timetables) {
            for ( Period period : timetable.getPeriods()) {
                String xPathExpr = "boolean(//netex:ServiceCalendarFrame"+
                                    "/netex:dayTypeAssignments"+
                                    "/netex:DayTypeAssignment/netex:OperatingPeriodRef"+
                                    "[@ref = '"+
                                            timetable.objectIdPrefix()+
                                            ":OperatingPeriod:"+
                                            timetable.objectIdSuffix()+
                                            "S"+dateFormat.format( period.getStartDate())+
                                            "E"+dateFormat.format( period.getEndDate())+
                                    "'])";
                assertXPathTrue( xPathExpr);
            }
        }
    }
    @Test(groups = {"ServiceCalendarFrame", "dayTypeAssignments"}, description = "Validate count of DayTypeAssignments with expected OperatingDayRef")
    public void verifyDayTypeAssignmentOperatingDayRef() throws XPathExpressionException, ParseException {
        
        List<Timetable> timetables = line.getTimetables();
        
        for( Timetable timetable : timetables) {
            for ( Date day : timetable.getCalendarDays()) {
                String xPathExpr = "boolean(//netex:ServiceCalendarFrame"+
                                    "/netex:dayTypeAssignments"+
                                    "/netex:DayTypeAssignment/netex:OperatingDayRef"+
                                    "[@ref = '"+
                                        timetable.objectIdPrefix()+
                                        ":OperatingDay:"+
                                        timetable.objectIdSuffix()+
                                        "D"+dateFormat.format( day)+
                                    "'])";
                assertXPathTrue( xPathExpr);
            }
        }
    }
}
