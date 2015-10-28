package mobi.chouette.exchange.hub.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class HubMission extends HubObject implements Comparable<HubMission>{

	/**
	 * rang = 1
	 * numéro
	 * type = entier
	 * TailleMax = 4
	 */
	@Getter @Setter
	private Integer numero;
	
	/**
	 * rang = 2
	 * Code arrêt (départ)
	 * type = texte
	 * TailleMax = 6
	 * optionnel
	 */
	@Getter @Setter
	private String codeArretDepart;
	
	/**
	 * rang = 3
	 * Heure de passage à l'arrêt de départ en seconde
	 * type = entier
	 * TailleMax = 5
	 * optionnel
	 */
	@Getter @Setter
	private Integer heureDepart;
	
	/**
	 * rang = 4
	 * Code arrêt (arrivée)
	 * type = texte
	 * TailleMax = 6
	 * optionnel
	 */
	@Getter @Setter
	private String codeArretArrivee;
	
	/**
	 * rang = 5
	 * Heure de passage à l'arrêt d'arrivée en seconde
	 * type = entier
	 * TailleMax = 5
	 * optionnel
	 */
	@Getter @Setter
	private Integer heureArrivee;

	/**
	 * rang = 6
	 * catégorie (commerciale)
	 * type = entier
	 * TailleMax = 1
	 * optionnelle
	 */
	@Getter @Setter
	private Integer categorie = Integer.valueOf(1);
	
	/**
	 * rang = 7
	 * service voiture
	 * type = entier
	 * TailleMax = 6
	 * optionnel  vide à l'export
	 */
	@Getter @Setter
	private Integer serviceVoiture;
	
	/**
	 * rang = 8
	 * service agent
	 * type = entier
	 * TailleMax = 6
	 * optionnel  vide à l'export
	 */
	@Getter @Setter
	private Integer serviceAgent;
	
	/**
	 * rang = 9
	 * validité
	 * type = entier
	 */
	@Getter @Setter
	private Integer validite;

	/**
	 * rang = 10
	 * forfaitisé
	 * type = String
	 * TailleMax = 1
	 */
	@Getter @Setter
	private String forfaitise = "N";
	
	/**
	 * rang = 11
	 * Temps payé brut de la mission en seconde
	 * type = entier
	 * TailleMax = 5
	 * optionnel
	 */
	@Getter @Setter
	private Integer tempsPaye;

	/**
	 * rang = 12
	 * code ligne
	 * type = texte
	 * TailleMax = 14
	 */
	@Getter @Setter
	private String codeLigne;
	
	/**
	 * rang = 13
	 * code chemin
	 * type = texte
	 * TailleMax = 40
	 */
	@Getter @Setter
	private String codeChemin;
	
	/**
	 * rang = 14
	 * nom du chemin (affiché dans les missions des GBM)
	 * type = texte
	 * TailleMax = 30
	 */
	@Getter @Setter
	private String nom;
	
	/**
	 * rang = 15
	 * Distance parcourue par la mission en mètre
	 * type = entier
	 */
	@Getter @Setter
	private Integer distance;
	
	/**
	 * rang = 16
	 * Type de matériel à utiliser pour couvrir cette mission
	 * type = texte
	 * TailleMax = 6
	 */
	@Getter @Setter
	private String typeMateriel;
	
	/**
	 * rang = 17
	 * nature du tour d'appartenance de la mission
	 * type = texte
	 * TailleMax = 6
	 */
	@Getter @Setter
	private String nature;
	
	/**
	 * rang = 18
	 * Commentaire libre sur la mission
	 * type = texte
	 * TailleMax = 200
	 */
	@Getter @Setter
	private String commentaire;
	
	/**
	 * rang = 19
	 * Type de matériel utilisé pour couvrir cette mission
	 * type = texte
	 * TailleMax = 6
	 */
	@Getter @Setter
	private String typeMaterielUtilise;
	
	/**
	 * rang = 20
	 * graphique
	 * type = texte
	 * TailleMax = 31
	 * optionnel
	 */
	@Getter @Setter
	private String graphique;

	
	/**
	 * rang = 21
	 * identifiant de l'arrêt physique de départ
	 * type = entier
	 */
	@Getter @Setter
	private Integer identifiantArretDepart;
	
	/**
	 * rang = 22
	 * identifiant de l'arrêt physique d'arrivée
	 * type = entier
	 */
	@Getter @Setter
	private Integer identifiantArretArrivee;
	
	/**
	 * rang = 23
	 * liste des codes période (séparateur |)
	 * type = texte
	 * TailleMax = 6 par code
	 */
	@Getter @Setter
	private List<String> codesPeriode = new ArrayList<>();
	
	/**
	 * rang = 24
	 * identifiant unique de la mission
	 * type = entier
	 * TailleMax = 8
	 */
	@Getter @Setter
	private Integer identifiant;

	@Override
	public int compareTo(HubMission arg0) {
		return numero - arg0.numero;
	}
	
	@Override
	public void clear() {
		numero = null;
		codeArretDepart=null;
		heureDepart = null;
		codeArretArrivee=null;
		heureArrivee = null;
		categorie = Integer.valueOf(1);
		serviceVoiture = null;
		serviceAgent = null;
		validite = null;
		forfaitise = "N";
		tempsPaye=null;
		codeLigne = null;
		codeChemin = null;
		nom = null;
		distance = null;
		typeMateriel = null;
		nature = null;
		commentaire = null;
		typeMaterielUtilise = null;
		graphique = null;
		identifiantArretDepart = null;
		identifiantArretArrivee = null;
		codesPeriode.clear();
		identifiant = null;
	}
}
