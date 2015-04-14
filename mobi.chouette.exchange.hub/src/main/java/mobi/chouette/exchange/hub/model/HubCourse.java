package mobi.chouette.exchange.hub.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class HubCourse extends HubObject implements Comparable<HubCourse>{
	
	/**
	 * rang = 1
	 * numéro
	 * type = entier
	 * TailleMax = 10
	 */
	@Getter @Setter
	private Integer numero;
	
	/**
	 * rang = 2
	 * service voiture
	 * type = entier
	 * TailleMax = 4
	 * optionnel  vide à l'export
	 */
	@Getter @Setter
	private Integer serviceVoiture;
	
	/**
	 * rang = 3
	 * type matériel
	 * type = entier
	 * TailleMax = 6
	 * optionnel  vide à l'export
	 */
	@Getter @Setter
	private Integer typeMateriel;
	
	/**
	 * rang = 4
	 * Code arrêt (départ puis arrivée)
	 * type = texte
	 * TailleMax = 6
	 * optionnel
	 */
	@Getter @Setter
	private String codeArret;
	
	/**
	 * rang = 5
	 * Heure de passage à l'arrêt en seconde
	 * type = entier
	 * TailleMax = 5
	 * optionnel
	 */
	@Getter @Setter
	private Integer heure;
	
	/**
	 * rang = 6
	 * code ligne
	 * type = texte
	 * TailleMax = 14
	 */
	@Getter @Setter
	private String codeLigne;
	
	/**
	 * rang = 7
	 * code chemin
	 * type = texte
	 * TailleMax = 30
	 */
	@Getter @Setter
	private String codeChemin;
	
	/**
	 * rang = 8
	 * type d'arrêt (D ou A)
	 * type = texte
	 * TailleMax = 1
	 */
	@Getter @Setter
	private String type;
	
	/**
	 * rang = 9
	 * sens de la course : 1 = aller, 2 = retour
	 * type = entier
	 * TailleMax = 1
	 */
	@Getter @Setter
	private int sens = SENS_ALLER;
	
	/**
	 * rang = 10
	 * validité
	 * type = entier
	 * TailleMax = 4
	 */
	@Getter @Setter
	private Integer validite;
	
	/**
	 * rang = 11
	 * graphique
	 * type = texte
	 * TailleMax = 30
	 * optionnel
	 */
	@Getter @Setter
	private String graphique;
	
	/**
	 * rang = 12
	 * identifiant arrêt
	 * type = entier
	 * TailleMax = 10
	 */
	@Getter @Setter
	private Integer identifiantArret;
	
	/**
	 * rang = 13
	 * identifiant renvois (séparateur |)
	 * type = entier
	 * TailleMax = 8 par identifiant
	 */
	@Getter @Setter
	private List<Integer> identifiantsRenvoi = new ArrayList<>();

	/**
	 * rang = 14
	 * liste des codes période (séparateur |)
	 * type = texte
	 * TailleMax = 6 par code
	 */
	@Getter @Setter
	private List<String> codesPeriode = new ArrayList<>();
	
	/**
	 * rang = 15
	 * catégorie (commerciale)
	 * type = entier
	 * TailleMax = 1
	 * optionnelle
	 */
	@Getter @Setter
	private Integer categorie = Integer.valueOf(1);
	
	/**
	 * rang = 16
	 * identifiant
	 * type = entier
	 * TailleMax = 8
	 */
	@Getter @Setter
	private Integer identifiant;

	@Override
	public int compareTo(HubCourse arg0) {
		int result = numero - arg0.numero;
		if (result == 0) result = sens - arg0.sens;
		if (result == 0) result = -type.compareTo(arg0.type);
		return result;
	}

	@Override
	public void clear() {
		numero = null;
		serviceVoiture = null;
		typeMateriel = null;
		codeArret=null;
		heure = null;
		codeLigne = null;
		codeChemin = null;
		type = null;
		sens=SENS_ALLER;
		validite = null;
		graphique=null;
		identifiantArret = null;
		identifiantsRenvoi.clear();
		codesPeriode.clear();
		categorie = Integer.valueOf(1);
		identifiant = null;
	}
	
}
