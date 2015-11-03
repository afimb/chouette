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
import mobi.chouette.exchange.hub.model.HubMission;
import mobi.chouette.exchange.hub.model.HubMissionOperation;
import mobi.chouette.exchange.hub.model.HubModeTransport;
import mobi.chouette.exchange.hub.model.HubObject;
import mobi.chouette.exchange.hub.model.HubPeriode;
import mobi.chouette.exchange.hub.model.HubRenvoi;
import mobi.chouette.exchange.hub.model.HubReseau;
import mobi.chouette.exchange.hub.model.HubSchema;
import mobi.chouette.exchange.hub.model.HubTransporteur;
import mobi.chouette.exchange.report.ActionReport;

@Log4j
public class HubExporter implements HubExporterInterface {
	public static enum EXPORTER {
		ARRET, CHEMIN, COMMUNE, CORRESPONDANCE, COURSE, COURSE_OPERATION, DIRECTION, 
		GROUPE_DE_LIGNE, HORAIRE, ITL, LIGNE, MODE_TRANSPORT, PERIODE, RENVOI, RESEAU, 
		SCHEMA, TRANSPORTEUR, MISSION, MISSION_OPERATION;
	}

	private String _path;
	private Map<String, Exporter<HubObject>> _map = new HashMap<String, Exporter<HubObject>>();

	public HubExporter(String path) {
		_path = path;
	}

	@SuppressWarnings("rawtypes")
	public void dispose(ActionReport report) {
		for (Exporter exporter : _map.values()) {
			try {
				exporter.dispose(report);
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
	public Exporter<HubArret> getArretExporter() {
		return getExporter(EXPORTER.ARRET.name(), ArretExporter.FILENAME,
				ArretExporter.class);

	}

	@SuppressWarnings("unchecked")
	@Override
	public Exporter<HubChemin> getCheminExporter() {
		return getExporter(EXPORTER.CHEMIN.name(), CheminExporter.FILENAME,
				CheminExporter.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Exporter<HubCommune> getCommuneExporter() {
		return getExporter(EXPORTER.COMMUNE.name(), CommuneExporter.FILENAME,
				CommuneExporter.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Exporter<HubCorrespondance> getCorrespondanceExporter() {
		return getExporter(EXPORTER.CORRESPONDANCE.name(), CorrespondanceExporter.FILENAME,
				CorrespondanceExporter.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Exporter<HubCourse> getCourseExporter() {
		return getExporter(EXPORTER.COURSE.name(), CourseExporter.FILENAME,
				CourseExporter.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Exporter<HubMission> getMissionExporter() {
		return getExporter(EXPORTER.MISSION.name(), MissionExporter.FILENAME,
				MissionExporter.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Exporter<HubCourseOperation> getCourseOperationExporter() {
		return getExporter(EXPORTER.COURSE_OPERATION.name(), CourseOperationExporter.FILENAME,
				CourseOperationExporter.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Exporter<HubMissionOperation> getMissionOperationExporter() {
		return getExporter(EXPORTER.MISSION_OPERATION.name(), MissionOperationExporter.FILENAME,
				MissionOperationExporter.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Exporter<HubDirection> getDirectionExporter() {
		return getExporter(EXPORTER.DIRECTION.name(), DirectionExporter.FILENAME,
				DirectionExporter.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Exporter<HubGroupeDeLigne> getGroupeDeLigneExporter() {
		return getExporter(EXPORTER.GROUPE_DE_LIGNE.name(), GroupeDeLigneExporter.FILENAME,
				GroupeDeLigneExporter.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Exporter<HubHoraire> getHoraireExporter() {
		return getExporter(EXPORTER.HORAIRE.name(), HoraireExporter.FILENAME,
				HoraireExporter.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Exporter<HubItl> getItlExporter() {
		return getExporter(EXPORTER.ITL.name(), ItlExporter.FILENAME,
				ItlExporter.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Exporter<HubLigne> getLigneExporter() {
		return getExporter(EXPORTER.LIGNE.name(), LigneExporter.FILENAME,
				LigneExporter.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Exporter<HubModeTransport> getModeTransportExporter() {
		return getExporter(EXPORTER.MODE_TRANSPORT.name(), ModeTransportExporter.FILENAME,
				ModeTransportExporter.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Exporter<HubPeriode> getPeriodeExporter() {
		return getExporter(EXPORTER.PERIODE.name(), PeriodeExporter.FILENAME,
				PeriodeExporter.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Exporter<HubRenvoi> getRenvoiExporter() {
		return getExporter(EXPORTER.RENVOI.name(), RenvoiExporter.FILENAME,
				RenvoiExporter.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Exporter<HubReseau> getReseauExporter() {
		return getExporter(EXPORTER.RESEAU.name(), ReseauExporter.FILENAME,
				ReseauExporter.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Exporter<HubSchema> getSchemaExporter() {
		return getExporter(EXPORTER.SCHEMA.name(), SchemaExporter.FILENAME,
				SchemaExporter.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Exporter<HubTransporteur> getTransporteurExporter() {
		return getExporter(EXPORTER.TRANSPORTEUR.name(), TransporteurExporter.FILENAME,
				TransporteurExporter.class);
	}


}
