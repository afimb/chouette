package mobi.chouette.exchange.hub.model.exporter;

import mobi.chouette.exchange.hub.model.HubArret;
import mobi.chouette.exchange.hub.model.HubChemin;
import mobi.chouette.exchange.hub.model.HubCommune;
import mobi.chouette.exchange.hub.model.HubCorrespondance;
import mobi.chouette.exchange.hub.model.HubCourse;
import mobi.chouette.exchange.hub.model.HubCourseOperation;
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
	Exporter<HubArret> getArretExporter() throws Exception;

	Exporter<HubChemin> getCheminExporter() throws Exception;

	Exporter<HubCommune> getCommuneExporter() throws Exception;

	Exporter<HubCorrespondance> getCorrespondanceExporter() throws Exception;

	Exporter<HubCourse> getCourseExporter() throws Exception;

	Exporter<HubCourseOperation> getCourseOperationExporter() throws Exception;

	Exporter<HubDirection> getDirectionExporter() throws Exception;

	Exporter<HubGroupeDeLigne> getGroupeDeLigneExporter() throws Exception;

	Exporter<HubHoraire> getHoraireExporter() throws Exception;

	Exporter<HubItl> getItlExporter() throws Exception;

	Exporter<HubLigne> getLigneExporter() throws Exception;

	Exporter<HubModeTransport> getModeTransportExporter() throws Exception;

	Exporter<HubPeriode> getPeriodeExporter() throws Exception;

	Exporter<HubRenvoi> getRenvoiExporter() throws Exception;

	Exporter<HubReseau> getReseauExporter() throws Exception;

	Exporter<HubSchema> getSchemaExporter() throws Exception;

	Exporter<HubTransporteur> getTransporteurExporter() throws Exception;
}
