package fr.certu.chouette.service.database;

import java.util.Collection;
import java.util.List;

import fr.certu.chouette.critere.IClause;
import fr.certu.chouette.critere.Ordre;
import fr.certu.chouette.modele.Ligne;
import fr.certu.chouette.modele.Reseau;

public interface IReseauManager {

	List<Ligne> getLignesReseau( final Long idReseau);

	void modifier(Reseau reseau);

	void creer(Reseau reseau);

	Reseau lire(final Long idReseau);

	List<Reseau> lire();
	
	List<Reseau> getReseaux( final Collection<Long> idReseaux);

	void supprimer(final Long idReseau);
	
	List<Reseau> select(IClause clause);
	List<Reseau> select(IClause clause, Collection <Ordre> ordres);

}