package mobi.chouette.exchange.hub.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class HubModeTransport extends HubObject implements Comparable<HubModeTransport>{
	

	/**
	 * rang = 1
	 * code
	 * type = texte
	 * TailleMax = 50
	 */
	@Getter @Setter
	private MODE_TRANSPORT code;
	
	/**
	 * rang = 2
	 * commentaire
	 * type = texte
	 * TailleMax = 255
	 * optionnel vide
	 */
	@Getter @Setter
	private String commentaire;
	
	/**
	 * rang = 3
	 * codes lignes triés séparés par |
	 * type = texte
	 * TailleMax = 14 par ligne
	 */
	@Getter @Setter
	private List<String> codesLigne = new ArrayList<>();

	@Override
	public int compareTo(HubModeTransport arg0) {
		return code.compareTo(arg0.code);
	}

	@Override
	public void clear() {
		code=null;
		commentaire=null;
		codesLigne.clear();
	}
 

}
