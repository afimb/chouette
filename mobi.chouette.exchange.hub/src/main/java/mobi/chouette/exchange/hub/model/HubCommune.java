package mobi.chouette.exchange.hub.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode (callSuper=false)
public class HubCommune extends HubObject implements Comparable<HubCommune>{
	/**
	 * rang = 1
	 * code insee
	 * type = entier
	 * TailleMax = 5
	 */
	@Getter @Setter
	private int codeInsee;

	/**
	 * rang = 2
	 * nom
	 * type = texte
	 * TailleMax = 80
	 */
	@Getter @Setter
	private String nom;

	@Override
	public int compareTo(HubCommune arg0) {
		int result = codeInsee - arg0.codeInsee;
		if (result == 0) result = nom.compareTo(arg0.nom);
		return result;
	}

	@Override
	public void clear() {
		codeInsee=0;
		nom=null;
	}

}
