package mobi.chouette.exchange.hub.model;

import lombok.Getter;
import lombok.Setter;

public class HubMissionOperation extends HubObject implements Comparable<HubMissionOperation>{
	
	/**
	 * rang = 1
	 * numéro
	 * type = entier
	 * TailleMax = 10
	 */
	@Getter @Setter
	private Integer numeroMission;
    
	/**
	 * rang = 2
	 * code_operation
	 * type = text, optionnel
	 * TailleMax = 8
	 */
	@Getter @Setter
	private String codeOperation;
    
	/**
	 * rang = 3
	 * code_lot
	 * type = text, optionnel
	 * TailleMax = 8
	 */
	@Getter @Setter
	private String codeLot;
    
	/**
	 * rang = 4
	 * code_activite
	 * type = text, optionnel
	 * TailleMax = 6
	 */
	@Getter @Setter
	private String codeActivite;
    
	/**
	 * rang = 5
	 * champ_libre_1
	 * type = text, optionnel
	 */
	@Getter @Setter
	private String champLibre1;
    
	/**
	 * rang = 6
	 * champ_libre_2
	 * type = text, optionnel
	 */
	@Getter @Setter
	private String champLibre2;
    
	/**
	 * rang = 7
	 * champ_libre_3
	 * type = text, optionnel
	 */
	@Getter @Setter
	private String champLibre3;
	
	@Override
	public int compareTo(HubMissionOperation arg0) {
		return numeroMission - arg0.numeroMission;
	}

	@Override
	public void clear() {
		numeroMission = null;
		codeOperation = null;
		codeLot = null;
		codeActivite = null;
		champLibre1 = null;
		champLibre2 = null;
		champLibre3 = null;
	}
}
