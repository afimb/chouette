package mobi.chouette.exchange.hub.model;

import lombok.Getter;
import lombok.Setter;

public class HubHoraire extends HubObject implements Comparable<HubHoraire>{
	
	/**
	 * rang = 1
	 * code arrêt
	 * type = texte
	 * TailleMax = 6
	 */
	@Getter @Setter
	private String codeArret;
	
	/**
	 * rang = 2
	 * heure en seconde
	 * type = entier
	 * TailleMax = 5
	 */
	@Getter @Setter
	private Integer heure;
	
	/**
	 * rang = 3
	 * type (départ/arrivée)
	 * type = entier
	 * TailleMax = 1
	 */
	@Getter @Setter
	private String type;
	/**
	 * rang = 4
	 * numéro de la course
	 * type = entier
	 * TailleMax = 5
	 */
	@Getter @Setter
	private Integer numeroCourse;
	
	/**
	 * rang = 5
	 * numéro de la mission
	 * type = entier
	 * TailleMax = 5
	 */
	@Getter @Setter
	private Integer numeroMission;
	
	/**
	 * rang = 6
	 * identifiant arrêt
	 * type = entier
	 * TailleMax = 8
	 */
	@Getter @Setter
	private Integer identifiantArret;

	/**
	 * rang = 7
	 * identifiant horaire
	 * type = entier
	 * TailleMax = 8
	 */
	@Getter @Setter
	private Integer identifiant;

	@Override
	public int compareTo(HubHoraire arg0) 
	{
		int result = numeroCourse - arg0.numeroCourse;
		if (result == 0) result = heure - arg0.heure;
		return 0;
	}

	@Override
	public void clear() {
		codeArret=null;
		heure = null;
		type = null;
		numeroCourse = null;
		numeroMission = null;
		identifiantArret = null;
		identifiant = null;
	}


}
