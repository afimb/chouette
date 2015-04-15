package mobi.chouette.exchange.hub.model;

import lombok.Getter;
import lombok.Setter;

public class HubReseau extends HubObject implements Comparable<HubReseau>{
	/**
	 * rang = 1
	 * code
	 * type = texte
	 * TailleMax = 3
	 */
	@Getter @Setter
	private String code;
	
	/**
	 * rang = 2
	 * nom
	 * type = texte
	 * TailleMax = 75
	 */
	@Getter @Setter
	private String nom;
	
	/**
	 * rang = 3
	 * identifiant
	 * type = entier
	 * TailleMax = 8
	 */
	@Getter @Setter
	private Integer identifiant;

	@Override
	public int compareTo(HubReseau arg0) {
		return code.compareTo(arg0.code);
	}

	@Override
	public void clear() {
		code = null;
		nom = null;
		identifiant=null;
	}

}
