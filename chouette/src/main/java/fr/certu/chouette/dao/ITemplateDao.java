package fr.certu.chouette.dao;

import java.util.Collection;
import java.util.List;

import fr.certu.chouette.critere.IClause;
import fr.certu.chouette.critere.Ordre;
import fr.certu.chouette.modele.BaseObjet;

public interface ITemplateDao <T extends BaseObjet> {
	
	   abstract List<T> getAll();
	   abstract T get(Long id);
	   abstract void save(T object);
	   abstract void remove(Long id);
	   abstract void update(BaseObjet object);
	   abstract T getByObjectId( final String objectId);
	   List<T> select(final IClause clause);
	   List<T> select(final IClause clause, final Collection<Ordre> ordre);
}
