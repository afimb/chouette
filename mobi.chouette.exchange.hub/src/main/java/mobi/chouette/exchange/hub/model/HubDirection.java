package mobi.chouette.exchange.hub.model;

import lombok.Getter;
import lombok.Setter;

public class HubDirection extends HubObject implements Comparable<HubDirection>{

	/**
	 * rang = 1
	 * Direction
	 * type = texte
	 * TailleMax = 128
	 */
	@Getter @Setter
	private String direction;
	
	/**
	 * rang = 2
	 * code ligne
	 * type = texte
	 * TailleMax = 14
	 */
	@Getter @Setter
	private String codeLigne;

	/**
	 * rang = 3
	 * sens
	 * type = entier
	 * TailleMax = 1
	 */
	@Getter @Setter
	private int sens = SENS_ALLER;
	
	/**
	 * rang = 4
	 * code chemin
	 * type = texte
	 * TailleMax = 30
	 * optionnel
	 */
	@Getter @Setter
	private String codeChemin;

	@Override
	public int compareTo(HubDirection arg0) {
		int result = codeLigne.compareTo(arg0.codeLigne);
		if (result == 0) result = sens - arg0.sens;
		if (result == 0) result = codeChemin.compareTo(arg0.codeChemin);
		return result;
	}

	@Override
	public void clear() {
		direction=null;
		codeLigne=null;
		sens = SENS_ALLER;
		codeChemin = null;
		
	}
	
}
