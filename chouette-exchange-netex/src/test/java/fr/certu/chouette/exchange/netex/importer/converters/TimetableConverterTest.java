package fr.certu.chouette.exchange.netex.importer.converters;

import com.ximpleware.AutoPilot;
import com.ximpleware.NavException;
import com.ximpleware.VTDGen;
import com.ximpleware.VTDNav;
import com.ximpleware.XPathEvalException;
import com.ximpleware.XPathParseException;
import fr.certu.chouette.model.neptune.Timetable;
import fr.certu.chouette.model.neptune.type.DayTypeEnum;
import java.io.File;
import java.io.FileInputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.Assert;


@ContextConfiguration(locations = {"classpath:testContext.xml"})
@SuppressWarnings("unchecked")
public class TimetableConverterTest extends AbstractTestNGSpringContextTests {

    private TimetableConverter timetableConverter;
    private AutoPilot autoPilot;
    private VTDNav nav;
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");   

    @BeforeClass
    protected void setUp() throws Exception {
        File f = FileUtils.getFile("src","test", "resources", "line2_test.xml");;
        FileInputStream fis = new FileInputStream(f);
        byte[] b = new byte[(int) f.length()];
        fis.read(b);
        
        VTDGen vg = new VTDGen();
        vg.setDoc(b);
        vg.parse(true); // set namespace awareness to true

        nav = vg.getNav();
        autoPilot = new AutoPilot(nav);
        autoPilot.declareXPathNameSpace("netex","http://www.netex.org.uk/netex");        
        
        timetableConverter = new TimetableConverter(nav);
    }
    private Timetable getByObjectId( String objectId)  throws XPathEvalException, NavException, XPathParseException, ParseException {
        List<Timetable> timetables = timetableConverter.convert();
        Timetable selectedTimetable = null;
        for( Timetable timetable : timetables) {
            if ( timetable.getObjectId().equals( objectId)) {
                selectedTimetable = timetable;
                break;
            }
        }
        
        Assert.assertNotNull( selectedTimetable, "can't find expected timetable having "+objectId+" as objectId");
        return selectedTimetable;
    }
    
    @Test(groups = {"ServiceCalendarFrame"}, description = "DayType's Name attribute reading")
    public void verifyName() throws XPathEvalException, NavException, XPathParseException, ParseException {
        Timetable selectedTimetable = getByObjectId( "T:DayType:1");
        Assert.assertEquals( selectedTimetable.getVersion(), "nom 1");
    }
    
    @Test(groups = {"ServiceCalendarFrame"}, description = "DayType's ShortName attribute reading")
    public void verifyShortName() throws XPathEvalException, NavException, XPathParseException, ParseException {
        Timetable selectedTimetable = getByObjectId( "T:DayType:1");
        Assert.assertEquals( selectedTimetable.getComment(), "short name 1");
    }
    
    @Test(groups = {"ServiceCalendarFrame"}, description = "DayType's Periods attribute reading")
    public void verifyPeriod() throws XPathEvalException, NavException, XPathParseException, ParseException {
        Timetable selectedTimetable = getByObjectId( "T:DayType:1");
        Assert.assertEquals( selectedTimetable.getPeriods().size(), 5);
        Assert.assertEquals( selectedTimetable.getPeriods().get(0).getStartDate(), dateFormat.parse("2040-03-18"));
        Assert.assertEquals( selectedTimetable.getPeriods().get(0).getEndDate(), dateFormat.parse("2040-04-22"));
    }
    
    @Test(groups = {"ServiceCalendarFrame"}, description = "DayType's CalendarDays attribute reading")
    public void verifyCalendarDay() throws XPathEvalException, NavException, XPathParseException, ParseException {
        Timetable selectedTimetable = getByObjectId( "T:DayType:1");
        Assert.assertEquals( selectedTimetable.getCalendarDays().size(), 5);
        Assert.assertEquals( selectedTimetable.getCalendarDays().get(0), dateFormat.parse("2015-03-18"));
    }
    
    @Test(groups = {"ServiceCalendarFrame"}, description = "DayType's properties attribute reading")
    public void verifyProperties() throws XPathEvalException, NavException, XPathParseException, ParseException {
        Timetable selectedTimetable = getByObjectId( "T:DayType:1");
        List<DayTypeEnum> dayTypeList = selectedTimetable.getDayTypes();
        Assert.assertEquals( dayTypeList.size(), 2);
        Assert.assertTrue( dayTypeList.contains( DayTypeEnum.MONDAY));
        Assert.assertTrue( dayTypeList.contains( DayTypeEnum.WEDNESDAY));
    }

}
