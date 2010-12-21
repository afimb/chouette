package fr.certu.chouette.service.database;

import java.util.List;

import fr.certu.chouette.critere.IClause;
import fr.certu.chouette.modele.InterdictionTraficLocal;

public interface IITLManager {

	List<InterdictionTraficLocal> getITLLigne(Long idLigne);

	void creer(InterdictionTraficLocal itl);

	void modifier(InterdictionTraficLocal itl);

	void supprimer(Long idITL);

	InterdictionTraficLocal lire(Long idITL);

	List<InterdictionTraficLocal> lire();
	
	List<InterdictionTraficLocal> select(IClause clause);
}