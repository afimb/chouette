package mobi.chouette.exchange.gtfs.exporter.producer;


import java.util.Arrays;

import mobi.chouette.model.Timetable;
import mobi.chouette.model.type.DayTypeEnum;

import org.joda.time.LocalDate;
import org.testng.Assert;
import org.testng.annotations.Test;

public class GtfsServiceProducerTest {


    @Test
    public void test() {
        Timetable timetable = new Timetable();
        timetable.setDayTypes(Arrays.asList(DayTypeEnum.Monday,DayTypeEnum.Tuesday, DayTypeEnum.Wednesday, DayTypeEnum.Thursday,  DayTypeEnum.Saturday));
        Assert.assertFalse(GtfsServiceProducer.checkValidDay(new LocalDate(2017, 10, 29), timetable));

    }
}
