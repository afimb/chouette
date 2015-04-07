package mobi.chouette.exchange.hub.model.exporter;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import lombok.extern.log4j.Log4j;
import mobi.chouette.exchange.hub.model.HubArret;
import mobi.chouette.exchange.hub.model.HubChemin;
import mobi.chouette.exchange.hub.model.HubCommune;
import mobi.chouette.exchange.hub.model.HubCorrespondance;
import mobi.chouette.exchange.hub.model.HubCourse;
import mobi.chouette.exchange.hub.model.HubCourseOperation;
import mobi.chouette.exchange.hub.model.HubDirection;
import mobi.chouette.exchange.hub.model.HubException;
import mobi.chouette.exchange.hub.model.HubException.ERROR;
import mobi.chouette.exchange.hub.model.HubGroupeDeLigne;
import mobi.chouette.exchange.hub.model.HubHoraire;
import mobi.chouette.exchange.hub.model.HubItl;
import mobi.chouette.exchange.hub.model.HubLigne;
import mobi.chouette.exchange.hub.model.HubModeTransport;
import mobi.chouette.exchange.hub.model.HubObject;
import mobi.chouette.exchange.hub.model.HubPeriode;
import mobi.chouette.exchange.hub.model.HubRenvoi;
import mobi.chouette.exchange.hub.model.HubReseau;
import mobi.chouette.exchange.hub.model.HubSchema;
import mobi.chouette.exchange.hub.model.HubTransporteur;

@Log4j
public class HubExporter implements HubExporterInterface {
	public static enum EXPORTER {
		ARRET, CHEMIN, COMMUNE, CORRESPONDANCE, COURSE, COURSE_OPERATION, DIRECTION, 
		GROUPE_DE_LIGNE, HORAIRE, ITL, LIGNE, MODE_TRANSPORT, PERIODE, RENVOI, RESEAU, 
		SCHEMA, TRANSPORTEUR;
	}

	private String _path;
	private Map<String, Exporter<HubObject>> _map = new HashMap<String, Exporter<HubObject>>();

	public HubExporter(String path) {
		_path = path;
	}

	@SuppressWarnings("rawtypes")
	public void dispose() {
		for (Exporter exporter : _map.values()) {
			try {
				exporter.dispose();
			} catch (IOException e) {
				log.error(e);
			}
		}
		_map.clear();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Exporter getExporter(String name, String path, Class clazz) {
		Exporter result = _map.get(name);

		if (result == null) {
			try {
				result = ExporterFactory.build(Paths.get(_path, path)
						.toString(), clazz.getName());
				_map.put(name, result);
			} catch (ClassNotFoundException | IOException e) {
				Context context = new Context();
				context.put(Context.PATH, _path);
				context.put(Context.ERROR, ERROR.SYSTEM);
				throw new HubException(context, e);
			}

		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Exporter<HubArret> getArretExporter() throws Exception {
		return getExporter(EXPORTER.ARRET.name(), ArretExporter.FILENAME,
				ArretExporter.class);

	}

	@SuppressWarnings("unchecked")
	@Override
	public Exporter<HubChemin> getCheminExporter() throws Exception {
		return getExporter(EXPORTER.CHEMIN.name(), CheminExporter.FILENAME,
				CheminExporter.class);
	}

	@Override
	public Exporter<HubCommune> getCommuneExporter() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Exporter<HubCorrespondance> getCorrespondanceExporter() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Exporter<HubCourse> getCourseExporter() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Exporter<HubCourseOperation> getCourseOperationExporter() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Exporter<HubDirection> getDirectionExporter() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Exporter<HubGroupeDeLigne> getGroupeDeLigneExporter() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Exporter<HubHoraire> getHoraireExporter() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Exporter<HubItl> getItlExporter() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Exporter<HubLigne> getLigneExporter() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Exporter<HubModeTransport> getModeTransportExporter() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Exporter<HubPeriode> getPeriodeExporter() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Exporter<HubRenvoi> getRenvoiExporter() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Exporter<HubReseau> getReseauExporter() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Exporter<HubSchema> getSchemaExporter() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Exporter<HubTransporteur> getTransporteurExporter() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}


}
