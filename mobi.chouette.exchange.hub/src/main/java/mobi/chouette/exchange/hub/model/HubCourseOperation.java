package mobi.chouette.exchange.hub.model;

import lombok.Getter;
import lombok.Setter;

public class HubCourseOperation extends HubObject implements Comparable<HubCourseOperation>{
	
	/**
	 * rang = 1
	 * numéro
	 * type = entier
	 * TailleMax = 10
	 */
	@Getter @Setter
	private Integer numeroCourse;
	
	/**
	 * rang = 2
	 * code opération
	 * type = texte
	 * TailleMax = 8
	 * optionnel
	 */
	@Getter @Setter
	private String codeOperation;
	
	/**
	 * rang = 3
	 * code lot
	 * type = texte
	 * TailleMax = 8
	 * optionnel vide
	 */
	@Getter @Setter
	private String codeLot;
	
	/**
	 * rang = 4
	 * code activité
	 * type = texte
	 * TailleMax = 8
	 * optionnel vide
	 */
	@Getter @Setter
	private String codeActivite;
	
	/**
	 * rang = 5
	 * Mode de transport
	 * type = texte
	 * TailleMax = 75
	 * optionnel
	 */
	@Getter @Setter
	private MODE_TRANSPORT modeTransport;
	
	/**
	 * rang = 6
	 * champ libre 1
	 * type = texte
	 * TailleMax = nd
	 * optionnel vide
	 */
	@Getter @Setter
	private String libre1;
	
	/**
	 * rang = 7
	 * champ libre 2
	 * type = texte
	 * TailleMax = nd
	 * optionnel vide
	 */
	@Getter @Setter
	private String libre2;
	
	/**
	 * rang = 8
	 * champ libre 3
	 * type = texte
	 * TailleMax = nd
	 * optionnel vide
	 */
	@Getter @Setter
	private String libre3;

	@Override
	public int compareTo(HubCourseOperation arg0) {
		return numeroCourse - arg0.numeroCourse;
	}

	@Override
	public void clear() {
		numeroCourse = null;
		codeOperation = null;
		codeLot = null;
		codeActivite=null;
		modeTransport=null;
		libre1=null;
		libre2=null;
		libre3= null;
	}
	
	


}
