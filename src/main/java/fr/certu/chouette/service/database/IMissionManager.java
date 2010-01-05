package fr.certu.chouette.service.database;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.certu.chouette.modele.Mission;

public interface IMissionManager {

	Mission lire(Long idMission);

	List<Mission> lire();

	void modifier(Mission mission);

	void creer(Mission mission);

	Map<String, Long> getMissionParSupport(Long idItineraire);

	void majMissions(Set<Long> courses, Set<Long> missions, Map<String, Long> missionParSupport);

	void fusionnerMissions(Long idItineraire);
	
	List<Mission> getMissions(Collection<Long> idMissions);
}