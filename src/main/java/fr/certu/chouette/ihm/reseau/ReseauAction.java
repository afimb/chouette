package fr.certu.chouette.ihm.reseau;

import fr.certu.chouette.ihm.GeneriqueAction;
import fr.certu.chouette.ihm.struts.ModelInjectable;
import fr.certu.chouette.ihm.struts.PreparableModel;
import fr.certu.chouette.modele.Reseau;
import fr.certu.chouette.service.database.IReseauManager;
import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Preparable;

public class ReseauAction extends GeneriqueAction implements ModelInjectable, ModelDriven, Preparable {
	
	private        Object         model;
	private static IReseauManager reseauManager;
	
	public Object getModel() {
		return model;
	}
	
	public void setModel(Object model) {
		this.model = model;
	}
	
	public void prepare() throws Exception {
		if (model instanceof PreparableModel)
			((PreparableModel)model).prepare(reseauManager);
	}
	
	public String list() {
		ListReseauModel model = (ListReseauModel)this.model;
		model.setReseaux(reseauManager.lire());
		return SUCCESS;
	}
	
	public String edit() {
		return INPUT;
	}
	
	public String createAndEdit() throws Exception {
		Reseau reseau = ((CrudReseauModel)model).getReseau();
		if (reseau == null)
			return INPUT;
		if (reseau.getId() == null) {
			reseauManager.creer(reseau);
			addActionMessage(getText("reseau.create.ok"));
			return "createAndEdit";
		}
		else
			return INPUT;
	}
	
	public String update() {
		Reseau reseau = ((CrudReseauModel)model).getReseau();
		if (reseau == null)
			return INPUT;
		if (reseau.getId() == null) {
			reseauManager.creer(reseau);
			addActionMessage(getText("reseau.create.ok"));
		}
		else {
			reseauManager.modifier(reseau);
			addActionMessage(getText("reseau.update.ok"));
		}		
		return INPUT;
	}

	public String delete() {
		Reseau reseau = ((CrudReseauModel)model).getReseau();
		reseauManager.supprimer(reseau.getId());
		addActionMessage(getText("reseau.delete.ok"));
		return SUCCESS;
	}
	
	public void setReseauManager(IReseauManager reseauManager) {
		this.reseauManager = reseauManager;
	}
	
	public String cancel() {
		addActionMessage(getText("reseau.cancel.ok"));
		return SUCCESS;
	}
	
	@Override
	public String input() throws Exception {
		return INPUT;
	}
}
