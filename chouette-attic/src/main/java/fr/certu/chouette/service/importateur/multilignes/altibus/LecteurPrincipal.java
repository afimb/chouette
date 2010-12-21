package fr.certu.chouette.service.importateur.multilignes.altibus;

import altibus.schema.Altibus;
import fr.certu.chouette.echange.ILectureEchange;
import fr.certu.chouette.service.identification.IIdentificationManager;
import fr.certu.chouette.service.importateur.multilignes.ILecteurPrincipal;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;

public class LecteurPrincipal implements ILecteurPrincipal {
	
	private static final Logger                             logger                = Logger.getLogger(LecteurPrincipal.class);
	private              String                             logFileName; 
	private              IIdentificationManager             identificationManager;
	private              List<String>                       names;
	private              String                             urlPrefix;
	private              Map<altibus.schema.Reseau, Reseau> reseaux;
	
	public LecteurPrincipal() {
		super();
		reseaux = new HashMap<altibus.schema.Reseau, Reseau>();
	}
	
	public List<ILectureEchange> getLecturesEchange() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void lire(String nom) {
		lireCheminFichier(getCheminFichier(nom));
	}
	
	private String getCheminFichier(String nom) {
		return nom;		
	}
	
	public void lireCheminFichier(String chemin) {
		String msgs = "";
        for (int i = 0; i < names.size(); i++) {
        	InputStream is = null;
        	try {
        		String name = names.get(i);
            	URL url = new URL(urlPrefix+name);
                is = url.openStream();
                Reader reader = new BufferedReader(new InputStreamReader(is));                
                parse(reader);
                is.close();
            }
        	catch(Throwable e) {
        		String msg = "ERROR DANS LA LECTURE DU : "+names.get(i)+" : "+e.getMessage();
        		logger.error(msg);
        		msgs += msg +"<br>";
        		if (is != null) {
        			try {
        				is.close();
        			}
        			catch(IOException ioe) {
        				is = null;
        			}
        		}
        	}
        }
        if (msgs.length() > 0)
        	throw new RuntimeException(msgs);
	}
	
	private void parse(Reader reader) throws MarshalException, ValidationException, TableauMarcheException {
		try {
			Unmarshaller               anUnmarshaller = new Unmarshaller(Altibus.class);
			anUnmarshaller.setValidation(false);
			Altibus                    altibus        = (Altibus)anUnmarshaller.unmarshal(reader);
			Reseau                     reseau         = getReseau(altibus);
			Transporteur               transporteur   = reseau.getTransporteur(altibus.getExploitant().getNomExploitant());
			Map<String, Arret>         arrets         = reseau.getArrets(altibus.getStations());
			transporteur.setArrets(arrets);
			Map<String, TableauMarche> tableauxMarche = reseau.getTableauxMarche(altibus);
			transporteur.setTableauxMarche(tableauxMarche);
			Map<String, Ligne>         lignes         = transporteur.getLignes(altibus);
			//TODO. xxx
		}
		catch(MarshalException e) {
			logger.error("MarshalException : "+e.getMessage());
			throw e;
		}
		catch (ValidationException e) {
			logger.error("ValidationException : "+e.getMessage());
			throw e;
		}
	}
	
	private Reseau getReseau(Altibus altibus) {
		altibus.schema.Reseau res = altibus.getReseau();
		Reseau reseau = reseaux.get(res);
		if (reseau == null) {
			reseau = new Reseau();
			reseau.setName(res.getNomReseau());
			reseaux.put(altibus.getReseau(), reseau);
		}
		return reseau;
	}

	public String getLogFileName() {
		return logFileName;
	}
	
	public void setLogFileName(String logFileName) {
		this.logFileName = logFileName;
	}
	
	public IIdentificationManager getIdentificationManager() {
		return identificationManager;
	}
	
	public void setIdentificationManager(IIdentificationManager identificationManager) {
		this.identificationManager = identificationManager;
	}
	
	public void setUrlPrefix(String urlPrefix) {
		this.urlPrefix = urlPrefix;
	}
	
	public String getUrlPrefix() {
		return urlPrefix;
	}
	
	public void setNames(List<String> names) {
		this.names = names;
	}
	
	public List<String> getNames() {
		return names;
	}
}
