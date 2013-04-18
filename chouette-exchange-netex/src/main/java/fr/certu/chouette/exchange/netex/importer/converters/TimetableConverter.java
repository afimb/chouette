package fr.certu.chouette.exchange.netex.importer.converters;

import com.ximpleware.AutoPilot;
import com.ximpleware.NavException;
import com.ximpleware.VTDNav;
import com.ximpleware.XPathEvalException;
import com.ximpleware.XPathParseException;
import fr.certu.chouette.model.neptune.Period;
import fr.certu.chouette.model.neptune.Timetable;
import fr.certu.chouette.model.neptune.type.DayTypeEnum;
import java.text.ParseException;
import java.util.ArrayList;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.datatype.DatatypeConfigurationException;
import lombok.Getter;
import org.apache.log4j.Logger;

public class TimetableConverter extends GenericConverter 
{    
    private static final Logger       logger = Logger.getLogger(TimetableConverter.class);
    private List<Timetable> timetables = new ArrayList<Timetable>();        
    private AutoPilot autoPilot;
    private VTDNav nav;        
    
    @Getter
    private Map<String, Timetable> timetablesByObjectId = new HashMap<String, Timetable>();
    
    public TimetableConverter(VTDNav vTDNav) throws XPathParseException, XPathEvalException, NavException, DatatypeConfigurationException
    {
        nav = vTDNav;
        
        autoPilot = new AutoPilot(nav);
        autoPilot.declareXPathNameSpace("netex","http://www.netex.org.uk/netex");        
    }
    
    public List<Timetable> convert() throws XPathEvalException, NavException, XPathParseException, ParseException
    {
        timetables.clear();
        timetablesByObjectId.clear();
        
        int result = -1;
        autoPilot.selectXPath("//netex:ServiceCalendarFrame");        
        
        while( (result = autoPilot.evalXPath()) != -1 )
        {                     
            Timetable timetable = new Timetable();
            
            // Mandatory                        
            timetable.setObjectId( (String)parseMandatoryAttribute(nav, "DayType", "id"));
            
            // Optionnal            
            Object objectVersion =  parseOptionnalAttribute(nav, "DayType", "version", "Integer");
            timetable.setObjectVersion( objectVersion != null ? (Integer)objectVersion : 0 );
            timetable.setVersion( (String)parseOptionnalSubElement(nav, "DayType", "Name") );
            timetable.setComment( (String)parseOptionnalSubElement(nav, "DayType", "ShortName") );
            
            // Day Types                        
            timetable.setDayTypes( toDayTypeEnumList(parseOptionnalElements(nav, "DaysOfWeek", "DayTypeEnum")) );
            
            // Calendar Day
            timetable.setCalendarDays( toShortDateList( parseMandatoryElements(nav, "CalendarDate", "ShortDate")) );                                    
            
            // Periods
            nav.push();
            AutoPilot pilot = new AutoPilot(nav);
            pilot.selectElement("OperatingPeriod");
        
            while( pilot.iterate() ) // iterate will iterate thru all elements
            {                                   
                Period period = new Period();
                period.setStartDate( (Date)parseMandatoryElement(nav, "FromDate", "DateTime") );
                period.setEndDate( (Date)parseMandatoryElement(nav, "ToDate", "DateTime") );
                timetable.addPeriod(period);
            }           
            nav.pop();
            
            timetablesByObjectId.put(timetable.getObjectId(), timetable);
            timetables.add(timetable);
        } 
        
        return timetables;
    }
    
}
