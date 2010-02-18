package fr.certu.chouette.service.importateur.multilignes.altibus;

import altibus.schema.Intervalle;
import altibus.schema.Intervalles;
import altibus.schema.types.EType;
import java.util.ArrayList;
import java.util.List;

public class TableauMarche {
	
	private String        name;
	private String        ref;
	private List<Periode> periodes; 
	
	public void setName(String name) {
		this.name = name;
		this.periodes = new ArrayList<Periode>();
	}
	
	public String getName() {
		return name;
	}
	
	public void setRef(String ref) {
		this.ref = ref;
	}
	
	public String getRef() {
		return ref;
	}
	
	public void setPeriodes(List<Periode> periodes) {
		this.periodes = periodes;
	}
	
	public List<Periode> getPeriodes() {
		return periodes;
	}
	
	public List<Periode> getIntervalles(Intervalles[] intervalles) throws TableauMarcheException {
		if (intervalles == null)
			return null;
		for (int i = 0; i < intervalles.length; i++) {
			Intervalle[] ints = intervalles[i].getIntervalle();
			for (int j = 0; j < ints.length; j++) {
				Intervalle intervalle = ints[j]; 
				if (intervalle.getRefCalendrier() != ref)
					continue;
				Periode periode = new Periode();
				periode.setDateDebut(intervalle.getDebut().toDate());
				periode.setDateFin(intervalle.getFin().toDate());
				periode.setRef(intervalle.getRefIntervalle());
				boolean inclusive = true;
				switch(intervalle.getType()) {
					case E:
						inclusive = false;
						break;
					case I:
						inclusive = true;
						break;
					default:
						throw new TableauMarcheException("ERREUR POUR TABLEAU_MARCHE TYPE : "+intervalle.getType());
				}
				if (inclusive) {
					periode.setDimanche(intervalle.getDimanche());
					periode.setLundi(intervalle.getLundi());
					periode.setMardi(intervalle.getMardi());
					periode.setMercredi(intervalle.getMercredi());
					periode.setJeudi(intervalle.getJeudi());
					periode.setVendredi(intervalle.getVendredi());
					periode.setSamedi(intervalle.getSamedi());
				}
				else {
					periode.setDimanche(!intervalle.getDimanche());
					periode.setLundi(!intervalle.getLundi());
					periode.setMardi(!intervalle.getMardi());
					periode.setMercredi(!intervalle.getMercredi());
					periode.setJeudi(!intervalle.getJeudi());
					periode.setVendredi(!intervalle.getVendredi());
					periode.setSamedi(!intervalle.getSamedi());
				}
				periodes.add(periode);
			}
		}
		return periodes;
	}
}
