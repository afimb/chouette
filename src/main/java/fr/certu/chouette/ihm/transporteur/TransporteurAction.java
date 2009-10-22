package fr.certu.chouette.ihm.transporteur;

import fr.certu.chouette.ihm.GeneriqueAction;
import fr.certu.chouette.ihm.struts.ModelInjectable;
import fr.certu.chouette.ihm.struts.PreparableModel;
import fr.certu.chouette.modele.Transporteur;
import fr.certu.chouette.service.database.ITransporteurManager;
import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Preparable;

public class TransporteurAction extends GeneriqueAction implements ModelInjectable, ModelDriven, Preparable {
	
	private        Object         model;
	private static ITransporteurManager transporteurManager;
	
	public Object getModel() {
		return model;
	}
	
	public void setModel(Object model) {
		this.model = model;
	}
	
	public void prepare() throws Exception {
		if (model instanceof PreparableModel)
			((PreparableModel)model).prepare(transporteurManager);
	}
	
	public String list() {
		ListTransporteurModel model = (ListTransporteurModel)this.model;
		model.setTransporteurs(transporteurManager.lire());
		return SUCCESS;
	}
	
	public String edit() {
		return INPUT;
	}
	
	public String createAndEdit() throws Exception {
		Transporteur transporteur = ((CrudTransporteurModel)model).getTransporteur();
		if (transporteur == null)
			return INPUT;
		if (transporteur.getId() == null) {
			transporteurManager.creer(transporteur);
			addActionMessage(getText("transporteur.create.ok"));
			return "createAndEdit";
		}
		else
			return INPUT;
	}
	
	public String update() {
		Transporteur transporteur = ((CrudTransporteurModel)model).getTransporteur();
		if (transporteur == null)
			return INPUT;
		if (transporteur.getId() == null) {
			transporteurManager.creer(transporteur);
			addActionMessage(getText("transporteur.create.ok"));
		}
		else {
			transporteurManager.modifier(transporteur);
			addActionMessage(getText("transporteur.update.ok"));
		}		
		return INPUT;
	}

	public String delete() {
		Transporteur transporteur = ((CrudTransporteurModel)model).getTransporteur();
		transporteurManager.supprimer(transporteur.getId());
		addActionMessage(getText("transporteur.delete.ok"));
		return SUCCESS;
	}
	
	public void setTransporteurManager(ITransporteurManager transporteurManager) {
		this.transporteurManager = transporteurManager;
	}
	
	public String cancel() {
		addActionMessage(getText("transporteur.cancel.ok"));
		return SUCCESS;
	}
	
	@Override
	public String input() throws Exception {
		return INPUT;
	}
}
