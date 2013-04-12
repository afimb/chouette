package fr.certu.chouette.exchange.netex.importer.converters;

import com.vividsolutions.jts.util.Assert;
import com.ximpleware.AutoPilot;
import com.ximpleware.NavException;
import com.ximpleware.VTDGen;
import com.ximpleware.VTDNav;
import com.ximpleware.XPathEvalException;
import com.ximpleware.XPathParseException;
import fr.certu.chouette.model.neptune.Timetable;
import java.io.File;
import java.io.FileInputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


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

    @Test(groups = {"NeptuneConverter"}, description = "Must return time tables")
    public void verifyTimetableConverter() throws XPathEvalException, NavException, XPathParseException, ParseException {
        List<Timetable> timetables = timetableConverter.convert();
        
        Timetable firstTimetable = timetables.get(0);
        
        Assert.equals( dateFormat.parse("2015-03-18"), firstTimetable.getCalendarDays().get(0) );
        Assert.equals( dateFormat.parse("2040-03-18"), firstTimetable.getPeriods().get(0).getStartDate() );
        Assert.equals( dateFormat.parse("2040-04-22"), firstTimetable.getPeriods().get(0).getEndDate() );

    }

}
