package fr.certu.chouette.exchange.netex.blueprint;

import com.tobedevoured.modelcitizen.annotation.Blueprint;
import com.tobedevoured.modelcitizen.annotation.Default;
import com.tobedevoured.modelcitizen.annotation.MappedList;
import fr.certu.chouette.model.neptune.Period;
import fr.certu.chouette.model.neptune.Timetable;
import java.util.Date;
import java.util.List;

@Blueprint(Timetable.class)
public class TimetableBlueprint {
    
    @Default
    String objectId = "RATP_PIVI:TimeTable:317452";
        
    @Default
    String publishedJourneyName = "1001101070001";   
    
    @MappedList(target = Period.class, size = 2, ignoreEmpty = false)
    List<Period> periods;
    
    @MappedList(target = Date.class, size = 2, ignoreEmpty = false)
    List<Date> calendarDays;

}
