package mobi.chouette.exchange.hub.model;

import lombok.Getter;
import lombok.Setter;

public class HubTransporteur extends HubObject implements Comparable<HubTransporteur>{
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
	public int compareTo(HubTransporteur arg0) 
	{
		return code.compareTo(arg0.getCode());
	}

	@Override
	public void clear() {
		code = null;
		nom = null;
		identifiant=null;
	}


}
