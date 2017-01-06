package mobi.chouette.exchange.hub.model;

import lombok.Getter;
import lombok.Setter;

public class HubCheminOperation extends HubObject implements Comparable<HubCheminOperation>{
	
	public static final String TYPE_COM = "COM";

		
	/**
	 * rang = 1
	 * Code du chemin
	 * type = texte
	 * TailleMax = 30
	 */
	@Getter @Setter
	private String codeChemin;
		
	/**
	 * rang = 2
	 * Type : HLP ou COM
	 * type = texte
	 * TailleMax = 5
	 */
	@Getter @Setter
	private String type = TYPE_COM;
	
	/**
	 * rang = 3
	 * 1
	 * type = entier
	 * TailleMax = 1
	 * valeur forcée à 1
	 */
	@Getter // @Setter
	private Integer un = Integer.valueOf(1) ;
	
	/**
	 * rang = 4
	 * Code girouette
	 * type = texte
	 * TailleMax = 30
	 */
	@Getter @Setter
	private String codeGirouette;
	



	@Override
	public int compareTo(HubCheminOperation arg0) {
		int result = codeChemin.compareTo(arg0.codeChemin);
		if (result == 0) result = codeGirouette.compareTo(arg0.codeGirouette);
		return result;
	}


	@Override
	public void clear() {
		codeChemin=null;
		type=TYPE_COM;
		// un = Integer.valueOf(1) ;
		codeGirouette=null;
	}


}
