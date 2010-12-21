package fr.certu.chouette.service.database;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import fr.certu.chouette.modele.Horaire;
import fr.certu.chouette.service.database.impl.modele.EtatMajHoraire;

public interface IHoraireManager 
{
	Horaire lire(Long idHoraire);
	List<Horaire> lire();
	
	/**
	 * Precondition: les structures de maj d'horaire
	 * ne regroupent que des horaires de course d'un même itinéraire
	 * 
	 * @param majHoraires : structure de maj d'horaire
	 */
	void modifier( Collection<EtatMajHoraire> majHoraires);
	
	
	List<Integer> filtreHorairesInvalides( List<Date> horairesModifie, int totalArrets);
}
