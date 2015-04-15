package mobi.chouette.exchange.hub.model;

import lombok.Getter;
import lombok.Setter;

public class HubLigne extends HubObject implements Comparable<HubLigne>{
	/**
	 * rang = 1
	 * code
	 * type = texte
	 * TailleMax = 14
	 */
	@Getter @Setter
	private String code;
	
	/**
	 * rang = 2
	 * code commercial
	 * type = texte
	 * TailleMax = 6
	 */
	@Getter @Setter
	private String codeCommercial;
	
	/**
	 * rang = 3
	 * nom
	 * type = texte
	 * TailleMax = 75
	 * optionnel
	 */
	@Getter @Setter
	private String nom;
	
	/**
	 * rang = 4
	 * code de représentation
	 * type = entier
	 * TailleMax = 8
	 * optionnel
	 * forcé à vide
	 */
	@Getter @Setter
	private Integer codeRepresentation;
	
	/**
	 * rang = 5
	 * code sous-traitant
	 * type = texte
	 * TailleMax = 10
	 * optionnel
	 * forcé à vide
	 */
	@Getter @Setter
	private String codeSousTraitant;
	
	/**
	 * rang = 6
	 * code transporteur
	 * type = texte
	 * TailleMax = 3
	 */
	@Getter @Setter
	private String codeTransporteur;
	
	/**
	 * rang = 7
	 * code réseau
	 * type = texte
	 * TailleMax = 3
	 */
	@Getter @Setter
	private String codeReseau;
	
	/**
	 * rang = 8
	 * code grouep de ligne
	 * type = texte
	 * TailleMax = 6
	 */
	@Getter @Setter
	private String codeGroupeDeLigne;
	
	/**
	 * rang = 9
	 * identifiant
	 * type = entier
	 * TailleMax = 8
	 */
	@Getter @Setter
	private Integer identifiant;

	@Override
	public int compareTo(HubLigne arg0) {
		return code.compareTo(arg0.code);
	}

	@Override
	public void clear() {
		code=null;
		codeCommercial=null;
		nom=null;
		codeRepresentation=null;
		codeSousTraitant=null;
		codeTransporteur=null;
		codeReseau=null;
		codeGroupeDeLigne=null;
		identifiant=null;
		
	}
	
}
