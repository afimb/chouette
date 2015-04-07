package mobi.chouette.exchange.hub.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class HubSchema extends HubObject implements Comparable<HubSchema>{
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
	 * sens
	 * type = entier
	 * TailleMax = 1
	 */
	@Getter @Setter
	private int sens = SENS_ALLER;
	
	/**
	 * rang = 3
	 * Identifiant du schéma
	 * type = entier
	 * TailleMax = 8
	 */
	@Getter @Setter
	private Integer identifiant;
	
	/**
	 * rang = 4+
	 * arrêts ordonnés selon le parcours
	 */
	@Getter @Setter
	private List<ArretSchema> arrets = new ArrayList<>();
	

	public class ArretSchema
	{
		/**
		 * rang = 4 (modulo 2)
		 * code Arrêt n
		 * type = texte
		 * TailleMax = 6
		 */
		@Getter @Setter
		private String code;
		
		/**
		 * rang = 5 (modulo 2)
		 * identifiant Arrêt n
		 * type = entier
		 * TailleMax = 8
		 */
		@Getter @Setter
		private Long identifiant;
	}


	@Override
	public int compareTo(HubSchema arg0) {
		int result = codeLigne.compareTo(arg0.codeLigne);
		if (result == 0) result = sens - arg0.sens;
		// TODO à vérifier
		return result;
	}


	@Override
	public void clear() {
		codeLigne = null;
		sens = SENS_ALLER;
		identifiant = null;
		arrets.clear();
	
	}

}
