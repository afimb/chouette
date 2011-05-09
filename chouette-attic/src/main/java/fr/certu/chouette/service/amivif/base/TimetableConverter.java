package fr.certu.chouette.service.amivif.base;

import chouette.schema.Period;

public class TimetableConverter {

    private chouette.schema.Timetable atc(amivif.schema.Timetable amivif) {

        if (amivif == null) {
            return null;
        }

        chouette.schema.Timetable chouette = new chouette.schema.Timetable();

        chouette.setObjectId(amivif.getObjectId());
        if (amivif.hasObjectVersion() && amivif.getObjectVersion() >= 1) {
            chouette.setObjectVersion(amivif.getObjectVersion());
        } else {
            chouette.setObjectVersion(1);
        }
        chouette.setCreationTime(amivif.getCreationTime());
        chouette.setCreatorId(amivif.getCreatorId());
        chouette.setComment(amivif.getComment());

        int totalVehicle = amivif.getVehicleJourneyIdCount();
        for (int i = 0; i < totalVehicle; i++) {
            chouette.addVehicleJourneyId(amivif.getVehicleJourneyId(i));
        }

        int totalJours = amivif.getCalendarDayCount();
        for (int i = 0; i < totalJours; i++) {
            chouette.addCalendarDay(amivif.getCalendarDay(i));
        }

        int totalPeriodes = amivif.getPeriodCount();
        for (int i = 0; i < totalPeriodes; i++) {
            Period periode = new Period();
            periode.setEndOfPeriod(amivif.getPeriod(i).getEndOfPeriod());
            periode.setStartOfPeriod(amivif.getPeriod(i).getStartOfPeriod());
            chouette.addPeriod(periode);
        }

        return chouette;
    }

    public chouette.schema.Timetable[] atc(amivif.schema.Timetable[] amivifs) {

        if (amivifs == null) {
            return new chouette.schema.Timetable[0];
        }

        int total = amivifs.length;
        chouette.schema.Timetable[] chouettes = new chouette.schema.Timetable[total];

        for (int i = 0; i < total; i++) {
            chouettes[i] = atc(amivifs[i]);
        }
        return chouettes;
    }

    private amivif.schema.Timetable cta(chouette.schema.Timetable chouette) {
        amivif.schema.Timetable amivif = new amivif.schema.Timetable();

        amivif.setObjectId(chouette.getObjectId());
        if (chouette.hasObjectVersion() && chouette.getObjectVersion() >= 1) {
            amivif.setObjectVersion(chouette.getObjectVersion());
        } else {
            amivif.setObjectVersion(1);
        }
        amivif.setCreationTime(chouette.getCreationTime());
        amivif.setCreatorId(chouette.getCreatorId());

        amivif.setComment(chouette.getComment());
        int totalDays = chouette.getCalendarDayCount();
        for (int i = 0; i < totalDays; i++) {
            amivif.addCalendarDay(chouette.getCalendarDay(i));
        }
        int totalPeriods = chouette.getPeriodCount();
        for (int i = 0; i < totalPeriods; i++) {
            amivif.schema.Period amivifPeriod = new amivif.schema.Period();
            amivifPeriod.setStartOfPeriod(chouette.getPeriod(i).getStartOfPeriod());
            amivifPeriod.setEndOfPeriod(chouette.getPeriod(i).getEndOfPeriod());
            amivif.addPeriod(amivifPeriod);
        }
        int totalTypes = chouette.getDayTypeCount();
        for (int i = 0; i < totalTypes; i++) {
//			DayTypeType dayType = DayTypeType.
        }

        int totalVJ = chouette.getVehicleJourneyIdCount();
        for (int i = 0; i < totalVJ; i++) {
            amivif.addVehicleJourneyId(chouette.getVehicleJourneyId(i));
        }

        return amivif;
    }

    public amivif.schema.Timetable[] cta(chouette.schema.Timetable[] chouettes) {
        if (chouettes == null) {
            return null;
        }

        int total = chouettes.length;
        amivif.schema.Timetable[] amivifs = new amivif.schema.Timetable[total];
        for (int i = 0; i < total; i++) {
            amivifs[ i] = cta(chouettes[ i]);
        }
        return amivifs;
    }
}
