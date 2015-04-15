package mobi.chouette.exchange.hub.model;

import lombok.Getter;
import lombok.Setter;

public class HubCorrespondance extends HubObject implements Comparable<HubCorrespondance>{
	/**
	 * rang = 1
	 * code arrêt 1
	 * type = texte
	 * TailleMax = 6
	 */
	@Getter @Setter
	private String codeArret1;

	/**
	 * rang = 2
	 * identifiant arrêt 1
	 * type = entier
	 * TailleMax = 8
	 */
	@Getter @Setter
	private Integer identifiantArret1;

	/**
	 * rang = 3
	 * code arrêt 2
	 * type = texte
	 * TailleMax = 6
	 */
	@Getter @Setter
	private String codeArret2;

	/**
	 * rang = 4
	 * identifiant arrêt 2
	 * type = entier
	 * TailleMax = 8
	 */
	@Getter @Setter
	private Integer identifiantArret2;
	
	/**
	 * rang = 5
	 * distance en mètre
	 * type = entier
	 * TailleMax = 4
	 */
	@Getter @Setter
	private Integer distance;
	
	/**
	 * rang = 6
	 * temps de parcours en secondes
	 * type = entier
	 * TailleMax = 4
	 */
	@Getter @Setter
	private Integer tempsParcours;
	
	/**
	 * rang = 7
	 * identifiant correspondance
	 * type = entier
	 * TailleMax = 8
	 */
	@Getter @Setter
	private Integer identifiant;

	@Override
	public int compareTo(HubCorrespondance arg0) {
		return (int) (identifiant - arg0.identifiant);
	}

	@Override
	public void clear() {
		codeArret1 = null;
		identifiantArret1 = null;
		codeArret2 = null;
		identifiantArret2 = null;
		distance = null;
		tempsParcours = null;
		identifiant = null;
	}

}
