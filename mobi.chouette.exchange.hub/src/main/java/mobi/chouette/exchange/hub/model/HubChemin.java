package mobi.chouette.exchange.hub.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class HubChemin extends HubObject implements Comparable<HubChemin>{
	
	public static final String TYPE_COM = "COM";

	
	/**
	 * rang = 1
	 * Ligne de rattachement du chemin
	 * type = texte
	 * TailleMax = 14
	 */
	@Getter @Setter
	private String codeLigne;
	
	/**
	 * rang = 2
	 * Code du chemin
	 * type = texte
	 * TailleMax = 30
	 */
	@Getter @Setter
	private String codeChemin;
	
	/**
	 * rang = 3
	 * Identifiant du chemin
	 * type = entier
	 * TailleMax = 8
	 */
	@Getter @Setter
	private Integer identifiant;
	
	/**
	 * rang = 4
	 * Nom
	 * type = texte
	 * TailleMax = 75
	 */
	@Getter @Setter
	private String nom;
	
	/**
	 * rang = 5
	 * Sens ( 1 = Aller, 2 = Retour )
	 * type = entier
	 * TailleMax = 1
	 */
	@Getter @Setter
	private int sens = SENS_ALLER;
	
	/**
	 * rang = 6
	 * Type : HLP ou COM
	 * type = texte
	 * TailleMax = 5
	 */
	@Getter @Setter
	private String type = TYPE_COM;
	
	/**
	 * rang = 7
	 * code couleur
	 * type = entier
	 * TailleMax = 5
	 * vide à l'export
	 */
	@Getter @Setter
	private Integer codeRepresentation ;
	
	/**
	 * rang = 8+
	 * arrets
	 */
	@Getter @Setter
	private List<ArretChemin> arrets = new ArrayList<>();
	

	public class ArretChemin 
	{
		/**
		 * rang = 8 (modulo 4)
		 * code Arrêt n
		 * type = texte
		 * TailleMax = 6
		 */
		@Getter @Setter
		private String code;
		
		/**
		 * rang = 9 (modulo 4)
		 * identifiant Arrêt n
		 * type = entier
		 * TailleMax = 8
		 */
		@Getter @Setter
		private Integer identifiant;
		
		/**
		 * rang = 10 (modulo 4)
		 * distance avec l'arrêt suivant
		 * type = entier
		 * TailleMax = 8
		 * optionnel vide à l'export
		 */
		@Getter @Setter
		private Integer distance;
		
		/**
		 * rang = 11 (modulo 4)
		 * type Arrêt n
		 * type = texte
		 * TailleMax = 18
		 * optionnel vide à l'export
		 */
		@Getter @Setter
		private String type;
		
		
	}


	@Override
	public int compareTo(HubChemin arg0) {
		int result = codeLigne.compareTo(arg0.codeLigne);
		if (result == 0) result = codeChemin.compareTo(arg0.codeChemin);
		if (result == 0) result = sens - arg0.sens;
		return result;
	}


	@Override
	public void clear() {
		codeLigne = null;
		codeChemin=null;
		identifiant=null;
		nom=null;
		sens=SENS_ALLER;
		type=TYPE_COM;
		codeRepresentation=null;
		arrets.clear();
	}


}
