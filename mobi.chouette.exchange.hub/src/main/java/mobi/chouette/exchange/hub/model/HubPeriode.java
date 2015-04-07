package mobi.chouette.exchange.hub.model;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class HubPeriode extends HubObject implements Comparable<HubPeriode>{
	/**
	 * rang = 1
	 * code
	 * type = texte
	 * TailleMax = 6
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
	 * date début
	 * type = date format dd/MM/yyyy
	 * TailleMax = 10
	 */
	@Getter @Setter
	private Date dateDebut;
	
	/**
	 * rang = 4
	 * date de fin
	 * type = date
	 * TailleMax = 10
	 */
	@Getter @Setter
	private Date dateFin;
	
	/**
	 * rang = 5
	 * calendrier
	 * type = entier
	 * TailleMax = 1 par jour
	 */
	@Getter @Setter
	private List<Boolean> calendrier = new ArrayList<>();
	
	/**
	 * rang = 6
	 * identifiant
	 * type = entier
	 * TailleMax = 8
	 */
	@Getter @Setter
	private Integer identifiant;

	@Override
	public int compareTo(HubPeriode arg0) {
		return code.compareTo(arg0.code);
	}

	@Override
	public void clear() {
		code=null;
		nom=null;
		dateDebut=null;
		dateFin=null;
		calendrier.clear();
		identifiant=null;
	}
	
	

}
