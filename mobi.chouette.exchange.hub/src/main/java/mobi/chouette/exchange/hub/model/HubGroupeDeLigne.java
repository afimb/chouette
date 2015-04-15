package mobi.chouette.exchange.hub.model;

import lombok.Getter;
import lombok.Setter;

public class HubGroupeDeLigne extends HubObject implements Comparable<HubGroupeDeLigne>{
	
	/**
	 * rang = 1
	 * code
	 * type = texte
	 * TailleMax = 6
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
	public int compareTo(HubGroupeDeLigne arg0) {
		return code.compareTo(arg0.code);
	}
	
	@Override
	public void clear() {
		code = null;
		nom = null;
		identifiant=null;
	}


}
