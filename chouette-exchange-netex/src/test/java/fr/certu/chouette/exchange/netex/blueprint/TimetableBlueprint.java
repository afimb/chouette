package fr.certu.chouette.exchange.netex.blueprint;

import com.tobedevoured.modelcitizen.annotation.Blueprint;
import com.tobedevoured.modelcitizen.annotation.Default;
import com.tobedevoured.modelcitizen.annotation.Mapped;
import com.tobedevoured.modelcitizen.annotation.MappedList;
import com.tobedevoured.modelcitizen.field.FieldCallback;
import fr.certu.chouette.model.neptune.Period;
import fr.certu.chouette.model.neptune.Timetable;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Blueprint(Timetable.class)
public class TimetableBlueprint {
    
    @Default
    FieldCallback objectId = new FieldCallback() {
        @Override
        public String get( Object model) {
            return "RATP_PIVI:TimeTable:" + UUID.randomUUID();
        }
        
    };     
    
    @MappedList(target = Period.class, size = 0, ignoreEmpty = false)
    List<Period> periods;
    
    @Mapped
    List<Date> calendarDays;
    
    @Default
    Integer intDayTypes;

}
