package mobi.chouette.exchange.hub.model;

import lombok.Getter;
import lombok.Setter;

public class HubItl extends HubObject implements Comparable<HubItl>{
	/**
	 * rang = 1
	 * code ligne
	 * type = texte
	 * TailleMax = 14
	 */
	@Getter @Setter
	private String codeLigne;
	
	/**
	 * rang = 2
	 * sens
	 * type = entier
	 * TailleMax = 1
	 */
	@Getter @Setter
	private int sens;
	
	/**
	 * rang = 3
	 * code arrêt
	 * type = texte
	 * TailleMax = 6
	 */
	@Getter @Setter
	private String codeArret;
	
	/**
	 * rang = 4
	 * identifiant arrêt
	 * type = entier
	 * TailleMax = 8
	 */
	@Getter @Setter
	private Integer identifiantArret;
	
	/**
	 * rang = 5
	 * ordre
	 * type = entier
	 * TailleMax = 2
	 */
	@Getter @Setter
	private Integer ordre;
	
	/**
	 * rang = 6
	 * type d'itl (1= interdiction de montée, 2 = interdiction de descente
	 * type = entier
	 * TailleMax = 1
	 */
	@Getter @Setter
	private Integer type;
	
	/**
	 * rang = 7
	 * identifiant
	 * type = entier
	 * TailleMax = 8
	 */
	@Getter @Setter
	private Integer identifiant;

	@Override
	public int compareTo(HubItl arg0) {
		int result = codeLigne.compareTo(arg0.codeLigne);
		if (result == 0) result = sens - arg0.sens;
		if (result == 0) result = ordre - arg0.ordre;
		return result;
	}

	@Override
	public void clear() {
		codeLigne=null;
		sens=SENS_ALLER;
		codeArret=null;
		identifiantArret = null;
		ordre=null;
		type=null;
		identifiant=null;
	}

}
