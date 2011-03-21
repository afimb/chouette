package fr.certu.chouette.service.importateur.multilignes.genericcsv;

import java.util.List;
import java.util.Map;
import java.util.Set;
import fr.certu.chouette.modele.Ligne;
import fr.certu.chouette.modele.PositionGeographique;

public interface ILecteurZone extends ILecteurSpecifique {
    
    public void init();
    public void lire(Ligne ligne, String[] ligneCSV);
    public Map<String, PositionGeographique> getZones();
    public Map<PositionGeographique, Set<PositionGeographique>> getArretsPhysiquesParZoneParente();
    public List<PositionGeographique> getArretsPhysiques();
    public List<PositionGeographique> getArretsPhysiques(Ligne ligne);
    public List<PositionGeographique> getZones(Ligne ligne);
    public Map<String, String> getZoneParenteParObjectId(Ligne ligne);
}
