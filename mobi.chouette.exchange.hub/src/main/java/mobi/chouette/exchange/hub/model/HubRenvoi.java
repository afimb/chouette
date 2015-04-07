package mobi.chouette.exchange.hub.model;

import lombok.Getter;
import lombok.Setter;

public class HubRenvoi extends HubObject implements Comparable<HubRenvoi>{
	/**
	 * rang = 1
	 * code
	 * type = texte
	 * TailleMax = 5
	 */
	@Getter @Setter
	private String code;
	
	/**
	 * rang = 2
	 * nom
	 * type = texte
	 * TailleMax = 255
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
	public int compareTo(HubRenvoi arg0) {
		return identifiant - arg0.identifiant;
	}
	
	@Override
	public void clear() {
		code = null;
		nom = null;
		identifiant=null;
	}


}
