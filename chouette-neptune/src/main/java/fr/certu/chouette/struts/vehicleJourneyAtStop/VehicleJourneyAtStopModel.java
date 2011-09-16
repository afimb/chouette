package fr.certu.chouette.struts.vehicleJourneyAtStop;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import lombok.Getter;
import lombok.Setter;
import fr.certu.chouette.model.neptune.JourneyPattern;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.model.neptune.VehicleJourney;
import fr.certu.chouette.model.neptune.VehicleJourneyAtStop;

public class VehicleJourneyAtStopModel
{
  @Getter @Setter private Map<Long, JourneyPattern> missionParIdMission;
  @Getter @Setter private Map<Long, List<VehicleJourneyAtStop>> horairesParIdCourse;
  @Getter @Setter private Map<Long, Integer> positionArretParIdArret;
  @Getter @Setter private Map<Long, StopArea> arretPhysiqueParIdArret;
  @Getter @Setter private Map<Long, SortedSet<Integer>> tableauxMarcheParIdCourse;
  @Getter @Setter private Map<Long, String> tableauxMarche;
  @Getter @Setter private List<Time> heuresCourses;
  @Getter @Setter private List<Integer> idsHorairesInvalides;
  @Getter @Setter private List<VehicleJourneyAtStop> horairesCourses;
  @Getter @Setter private List<StopPoint> arretsItineraire;
  @Getter @Setter private Time tempsDecalage;
  @Getter @Setter private Integer nbreCourseDecalage;
  @Getter @Setter private Long idCourseADecaler;
  @Getter @Setter private List<VehicleJourney> courses = new ArrayList<VehicleJourney>();
  @Getter @Setter private Map<Long, VehicleJourney> coursesParIdCourse;
  @Getter @Setter private List<VehicleJourney> coursesPage;
  @Getter @Setter private Route route;

}
