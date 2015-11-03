package mobi.chouette.exchange.hub.model.exporter;

import mobi.chouette.exchange.hub.model.HubArret;
import mobi.chouette.exchange.hub.model.HubChemin;
import mobi.chouette.exchange.hub.model.HubCommune;
import mobi.chouette.exchange.hub.model.HubCorrespondance;
import mobi.chouette.exchange.hub.model.HubCourse;
import mobi.chouette.exchange.hub.model.HubMission;
import mobi.chouette.exchange.hub.model.HubCourseOperation;
import mobi.chouette.exchange.hub.model.HubMissionOperation;
import mobi.chouette.exchange.hub.model.HubDirection;
import mobi.chouette.exchange.hub.model.HubGroupeDeLigne;
import mobi.chouette.exchange.hub.model.HubHoraire;
import mobi.chouette.exchange.hub.model.HubItl;
import mobi.chouette.exchange.hub.model.HubLigne;
import mobi.chouette.exchange.hub.model.HubModeTransport;
import mobi.chouette.exchange.hub.model.HubPeriode;
import mobi.chouette.exchange.hub.model.HubRenvoi;
import mobi.chouette.exchange.hub.model.HubReseau;
import mobi.chouette.exchange.hub.model.HubSchema;
import mobi.chouette.exchange.hub.model.HubTransporteur;


public interface HubExporterInterface {
	Exporter<HubArret> getArretExporter();

	Exporter<HubChemin> getCheminExporter();

	Exporter<HubCommune> getCommuneExporter();

	Exporter<HubCorrespondance> getCorrespondanceExporter();

	Exporter<HubCourse> getCourseExporter();

	Exporter<HubMission> getMissionExporter();

	Exporter<HubCourseOperation> getCourseOperationExporter();

	Exporter<HubMissionOperation> getMissionOperationExporter();

	Exporter<HubDirection> getDirectionExporter();

	Exporter<HubGroupeDeLigne> getGroupeDeLigneExporter();

	Exporter<HubHoraire> getHoraireExporter();

	Exporter<HubItl> getItlExporter();

	Exporter<HubLigne> getLigneExporter();

	Exporter<HubModeTransport> getModeTransportExporter();

	Exporter<HubPeriode> getPeriodeExporter();

	Exporter<HubRenvoi> getRenvoiExporter();

	Exporter<HubReseau> getReseauExporter();

	Exporter<HubSchema> getSchemaExporter();

	Exporter<HubTransporteur> getTransporteurExporter();
}
