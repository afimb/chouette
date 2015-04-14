package mobi.chouette.exchange.hub.model;

import lombok.Getter;
import lombok.Setter;

public class HubArret extends HubObject implements Comparable<HubArret> {
	/**
	 * rang = 1 Code type = texte TailleMax = 6
	 */
	@Getter
	@Setter
	private String code;

	/**
	 * rang = 2 Nom usuel type = texte TailleMax = 75
	 */
	@Getter
	@Setter
	private String nom;

	/**
	 * rang = 3 Description type = texte TailleMax = 255 (AG) 60 (AP) Optionnel
	 */
	@Getter
	@Setter
	private String description = "";

	/**
	 * rang = 4 Type type = texte composé de O et de N TailleMax = 18
	 * 
	 * "ONNNNNNNNNNNNNNNNN" si area_type="CommercialStopPoint" ou
	 * "NNNNNNNNNNNNNNNNNN" si area_type="BoradingPosition" ou "Quay". Si en
	 * plus mobility_restricted_suitability=true remplacer le 14iem N par O
	 * ("ONNNNNNNNNNNNONNNN" ou "NNNNNNNNNNNNNONNNN")
	 */
	@Getter
	@Setter
	private String type;

	/**
	 * rang = 5 nom réduit de l'arrêt parent type = texte TailleMax = 6 ou 12 ?
	 */
	@Getter
	@Setter
	private String nomReduit = "";

	/**
	 * rang = 6 Coordonnée X en Lambert2 étendu (-1 si non fixé) type = flottant
	 * TailleMax = 8
	 */
	@Getter
	@Setter
	private Integer x;

	/**
	 * rang = 7 Coordonnée Y en Lambert2 étendu (-1 si non fixé) type = flottant
	 * TailleMax = 8
	 */
	@Getter
	@Setter
	private Integer y;

	/**
	 * rang = 8 Commune type = texte TailleMax = 80
	 */
	@Getter
	@Setter
	private String commune;

	/**
	 * rang = 9 Code INSEE type = entier TailleMax = 5
	 */
	@Getter
	@Setter
	private Integer codeInsee;

	/**
	 * rang = 10 Commentaire type = texte TailleMax = 255 optionnel
	 */
	@Getter
	@Setter
	private String commentaire;

	/**
	 * rang = 11 identifiant de l'arrêt physique type = texte TailleMax = 8
	 */
	@Getter
	@Setter
	private Integer identifiant;

	@Override
	public int compareTo(HubArret arg0) {
		int result = -type.substring(0, 1).compareTo(arg0.type.substring(0, 1));
		if (result == 0)
			result = nomReduit.compareTo(arg0.nomReduit);
		return result;
	}

	@Override
	public void clear() {
		code = null;
		nom = null;
		description = null;
		type = null;
		nomReduit = null;
		x = null;
		y = null;
		commune = null;
		codeInsee = null;
		commentaire = null;
		identifiant = null;
	}

}
