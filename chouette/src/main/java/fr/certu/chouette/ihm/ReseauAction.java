package fr.certu.chouette.ihm;

import fr.certu.chouette.modele.Reseau;
import fr.certu.chouette.service.database.IReseauManager;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.interceptor.validation.SkipValidation;
import com.opensymphony.xwork2.Preparable;

public class ReseauAction extends GeneriqueAction implements Preparable {
	
	private static final Log            log           = LogFactory.getLog(ReseauAction.class);
	private static       IReseauManager reseauManager;
	private              List<Reseau>   reseaux;
	private              Reseau         reseau;
	private              Long           idReseau;
	private              String         CREATEANDEDIT = "createAndEdit";
	
	public String cancel() {
		addActionMessage(getText("reseau.cancel.ok"));
		return SUCCESS;
	}

	public String delete() {
		reseauManager.supprimer(idReseau);
		addActionMessage(getText("reseau.delete.ok"));
		return SUCCESS;
	}	
	
	public String edit() {
		return INPUT;
	}

	public Reseau getReseau() {
		return reseau;
	}

	public List<Reseau> getReseaux() {
		return reseaux;
	}

	@Override
	public String input() throws Exception {
		return INPUT;
	}
	
	@SkipValidation
	public String list() {
		reseaux = reseauManager.lire();
		return SUCCESS;
	}
	
	public void prepare() throws Exception {
		if (idReseau != null)
			reseau = reseauManager.lire(idReseau);
	}

	public void setIdReseau(Long idReseau) {
		this.idReseau = idReseau;
	}

	public void setManager(IReseauManager manager) {
		this.reseauManager = manager;
	}

	public void setReseau(Reseau reseau) {
		this.reseau = reseau;
	}

	public String update() throws Exception {
		if (reseau == null)
			return INPUT;
		//Remplissage de l'id automatiquement par hibernate et donc obligation de le mettre a null par un try catch
		if ( reseau.getId()==null) {
				reseauManager.creer(reseau);
				addActionMessage(getText("reseau.create.ok"));
		}
		else {
				reseauManager.modifier(reseau);
				addActionMessage(getText("reseau.update.ok"));
		}		
		return INPUT;
	}
	
	public String createAndEdit() throws Exception {
		if (reseau == null)
			return INPUT;
		//Remplissage de l'id automatiquement par hibernate et donc obligation de le mettre a null par un try catch
		if (reseau.getId() == null) {
				reseauManager.creer(reseau);
				addActionMessage(getText("reseau.create.ok"));
				return CREATEANDEDIT;
		}
		else
			return INPUT;
	}
}
