package fr.certu.chouette.service.importateur.multilignes.hastus;

import au.com.bytecode.opencsv.CSVReader;
import fr.certu.chouette.echange.ILectureEchange;
import fr.certu.chouette.echange.LectureEchange;
import fr.certu.chouette.modele.ArretItineraire;
import fr.certu.chouette.modele.Course;
import fr.certu.chouette.modele.Horaire;
import fr.certu.chouette.modele.Itineraire;
import fr.certu.chouette.modele.Ligne;
import fr.certu.chouette.modele.Mission;
import fr.certu.chouette.modele.PositionGeographique;
import fr.certu.chouette.modele.TableauMarche;
import fr.certu.chouette.service.importateur.multilignes.ILecteurPrincipal;
import fr.certu.chouette.service.importateur.multilignes.hastus.commun.CodeIncident;
import fr.certu.chouette.service.importateur.multilignes.hastus.commun.ServiceException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import org.apache.log4j.Logger;

public class LecteurPrincipal implements ILecteurPrincipal {
	
	private static final Logger             logger             = Logger.getLogger(LecteurPrincipal.class);
	private static final String             JeuCaracteres      = "UTF-8";
	private              String             repertoire;        // "."
	private              char               separateur;        // ';'
	private              ILecteurZone       lecteurZone;       // 
	private              ILecteurArret      lecteurArret;      // 
	private              ILecteurLigne      lecteurLigne;      // 
	private              ILecteurItineraire lecteurItineraire; // 
	private              ILecteurCourse     lecteurCourse;     // 
	private              ILecteurHoraire    lecteurHoraire;    // 
	private              ILecteurOrdre      lecteurOrdre;      // 
	private              String             logFileName;       // 
	private static final SimpleDateFormat   sdf                = new SimpleDateFormat("HH:mm:ss");
	
	public List<ILectureEchange> getLecturesEchange() {
		FileWriter fw = null;
		List<ILectureEchange> lecturesEchange = new ArrayList<ILectureEchange>();
		try {
			fw = new FileWriter(logFileName, true);
			fw.write("######################################## I N T E G R A T I O N ###############################################\n");
			Map<String, Ligne> ligneParRegistration = lecteurLigne.getLigneParRegistration();
			Map<String, PositionGeographique> zones = lecteurZone.getZonesParObjectId();
			Map<String, PositionGeographique> arretsPhysiquesParObjectId = lecteurArret.getArretsPhysiquesParObjectId();
			Map<Itineraire, Map<String, ArretItineraire>> arretsItineraireParItineraire = lecteurHoraire.getArretsItineraireParItineraire();
			Map<Itineraire, Ligne> ligneParItineraire = lecteurItineraire.getLigneParItineraire();
			Set<Itineraire> itinerairesSet = ligneParItineraire.keySet();
			Map<Course, Ligne> ligneParCourse = lecteurCourse.getLigneParCourse();
			Set<Course> coursesSet = ligneParCourse.keySet();
			Map<String, Mission> missionParNom = lecteurHoraire.getMissionParNom();
			
			for (PositionGeographique pg : arretsPhysiquesParObjectId.values()) {
				String tmpName = pg.getComment();
				pg.setComment(pg.getName());
				pg.setName(tmpName);
			}
			
			for (Itineraire itineraire : arretsItineraireParItineraire.keySet()) {
				Map<String, ArretItineraire> arretsIt = arretsItineraireParItineraire.get(itineraire);
				if (arretsIt == null)
					continue;
				int size = arretsIt.size() - 1;
				for (String key : arretsIt.keySet()) {
					if (arretsIt.get(key).getPosition() == size) {
						String arretPhName = key;
						if (arretPhName.endsWith("$"))
							arretPhName = arretPhName.substring(0, arretPhName.length()-1);
						PositionGeographique posGeo = lecteurArret.getArretsPhysiques().get(arretPhName);
						if (posGeo != null) {
							String fatherObjectId = lecteurArret.getObjectIdParParentObjectId().get(posGeo.getObjectId());
							if (fatherObjectId != null) {
								PositionGeographique fatherZone = lecteurZone.getZonesParObjectId().get(fatherObjectId);
								itineraire.setPublishedName(fatherZone.getName());
							}
						}
					}
				}
			}
			Map<String, List<String>> listeOrdonneeArretsParItineraireName = lecteurOrdre.getListeOrdonneeArretsParItineraireName();
			for (String itineraireName : listeOrdonneeArretsParItineraireName.keySet()) {
				boolean isFound = false;
				for (Itineraire itineraire : arretsItineraireParItineraire.keySet())
					if (itineraireName.equals(itineraire.getNumber())) {
						isFound = true;
						break;
					}
				if (!isFound) {
					logger.error("Il n'existe aucun itineraire ... "+itineraireName); // ERROR
					fw.write("Il n'existe aucun itineraire ... "+itineraireName+"\n");
				}
			}
			for (String registration : ligneParRegistration.keySet()) {
				logger.debug("INTEGRATION LIGNE : "+registration);
				Ligne ligne = ligneParRegistration.get(registration);
				LectureEchange lectureEchange = new LectureEchange();
				lectureEchange.setReseau(lecteurLigne.getReseau());
				if (lectureEchange.getReseau() == null) {
					logger.error("La ligne "+registration+" n'a pas de reseau.");
					fw.write("La ligne "+registration+" n'a pas de reseau.\n");
					continue;
				}
				lectureEchange.setTransporteur(lecteurLigne.getTransporteur());
				if (lectureEchange.getTransporteur() == null) {
					logger.error("La ligne "+registration+" n'a pas de transporteur.");
					fw.write("La ligne "+registration+" n'a pas de transporteur.\n");
					continue;
				}
				lectureEchange.setLigne(ligne);
				List<Itineraire> itineraires = new ArrayList<Itineraire>();
				List<Mission> missions = new ArrayList<Mission>();
				Set<Mission> missionsSet = new HashSet<Mission>();
				Set<Itineraire> itineraireSet = new HashSet<Itineraire>();
				List<ArretItineraire> arretsItineraires = new ArrayList<ArretItineraire>();
				List<PositionGeographique> arretsPhysiques = new ArrayList<PositionGeographique>();
				Map<String, String> itineraireParArret = new HashMap<String, String>();
				List<String> objectIdZonesGeneriques = new ArrayList<String>();
				List<PositionGeographique> zonesCommerciales = new ArrayList<PositionGeographique>();
				logger.debug("\tLES ITINERAIRES");
				for (Itineraire itineraire : itinerairesSet)
					if ((ligneParItineraire.get(itineraire) == ligne) && (itineraireSet.add(itineraire))) {
						String itineraireNumber = itineraire.getNumber();
						if (itineraireNumber != null) {
							Mission mission = missionParNom.get(itineraireNumber);
							if ((mission != null) && (missionsSet.add(mission)))
								missions.add(mission);
							else {
								logger.warn("L'itineraire "+itineraire.getName()+" n'a pas de mission.");
								fw.write("L'itineraire "+itineraire.getName()+" n'a pas de mission.\n");
							}
						}
						Map<String, ArretItineraire> arretsItineraire = arretsItineraireParItineraire.get(itineraire);
						boolean itPossedeHoraires = false;
						if (arretsItineraire != null) {
							Set<String> arretsItineraireNames = arretsItineraire.keySet();
							for (String name : arretsItineraireNames) {
								if (arretsItineraire.get(name) != null) {
									arretsItineraires.add(arretsItineraire.get(name));
									itineraireParArret.put(arretsItineraire.get(name).getObjectId(), itineraire.getObjectId());
									itPossedeHoraires = true;
									if (arretsItineraire.get(name).getContainedIn() != null)
										if (arretsPhysiquesParObjectId.get(arretsItineraire.get(name).getContainedIn()) != null) {
											if (!arretsPhysiques.contains(arretsPhysiquesParObjectId.get(arretsItineraire.get(name).getContainedIn())))
												arretsPhysiques.add(arretsPhysiquesParObjectId.get(arretsItineraire.get(name).getContainedIn()));
											if (!objectIdZonesGeneriques.contains(arretsItineraire.get(name).getContainedIn()))
												objectIdZonesGeneriques.add(arretsItineraire.get(name).getContainedIn());
										}
								}
							}
						}
						if (itPossedeHoraires)
							itineraires.add(itineraire);
						else {
							logger.warn("L'itineraire "+itineraire.getName()+" n'a pas d'horaires.");
							fw.write("L'itineraire "+itineraire.getName()+" n'a pas d'horaires.\n");
						}
					}
				List<Course> courses = new ArrayList<Course>();
				logger.debug("\tLES COURSES");
				for (Course course : coursesSet)
					if (ligneParCourse.get(course) == ligne)
						if ((course.getRouteId() == null) || (course.getJourneyPatternId() == null))
							continue;
						else
							courses.add(course);
				logger.debug("\tLES HORAIRES");
				if (lecteurHoraire.getListHorairesParRegistrationLigne(registration) == null) {
					logger.error("La ligne "+registration+" n'a pas d'horaire.");
					fw.write("La ligne "+registration+" n'a pas d'horaire.\n");
					continue;
				}
				List<Horaire> horaires = lecteurHoraire.getListHorairesParRegistrationLigne(registration);
				
				// FUSION DES ITINERAIRES (MISSIONS, ARRETSITINERAIRES ...
				logger.debug("Fusion des itineraires.");
				List<Itineraire> itinerairesDest = new ArrayList<Itineraire>();
				for (Itineraire itToAdd : itineraires) {
					Collection<ArretItineraire> arretsDeItToAdd = arretsItineraireParItineraire.get(itToAdd).values();
					Itineraire tmp = null;
					for (Itineraire it : itinerairesDest) {
						Collection<ArretItineraire> arretsDeIt = arretsItineraireParItineraire.get(it).values();
						if (arretsDeItToAdd.size() != arretsDeIt.size())
							continue;
						boolean equals = true;
						for (ArretItineraire arretDeItToAdd : arretsDeItToAdd) {
							for (ArretItineraire arretDeIt : arretsDeIt)
								if (arretDeItToAdd.getPosition() == arretDeIt.getPosition()) {
									if (!arretDeItToAdd.getContainedIn().equals(arretDeIt.getContainedIn()))
										equals = false;
									break;
								}
							if (!equals)
								break;
						}
						if (equals) {
							tmp = it;
							break;
						}
					}
					if (tmp == null)
						itinerairesDest.add(itToAdd);
					else { // fusionner itToAdd to tmp
						String st1 = tmp.getName();
						String st2 = itToAdd.getName();
						st1 = st1 + st2.substring(st2.lastIndexOf("-"));
						tmp.setName(st1);
						if (!tmp.getPublishedName().equals(itToAdd.getPublishedName()))
							tmp.setPublishedName(tmp.getPublishedName()+"--"+itToAdd.getPublishedName());
						String nb1 = tmp.getNumber();
						String nb2 = itToAdd.getNumber();
						nb1 = nb1 + nb2.substring(nb2.lastIndexOf("-"));
						tmp.setNumber(nb2);
						// Les courses de itToAdd passe sur tmp et sa mission
						String missionObjectId = null;
						for (Course cr : courses)
							if (cr.getRouteId().equals(tmp.getObjectId())) {
								missionObjectId = cr.getJourneyPatternId();
								break;
							}
						String missionToRemoveObjId = null;
						for (Course cr : courses)
							if (cr.getRouteId().equals(itToAdd.getObjectId())) {
								missionToRemoveObjId = cr.getJourneyPatternId();
								break;
							}
						Mission missionToRemove = null;
						for (Mission mission : missions)
							if (mission.getObjectId().equals(missionToRemoveObjId)) {
								missionToRemove = mission;
								break;
							}
						missions.remove(missionToRemove);
						for (Course cr : courses)
							if (cr.getRouteId().equals(itToAdd.getObjectId())) {
								cr.setRouteId(tmp.getObjectId());
								cr.setJourneyPatternId(missionObjectId);
							}
						// arretsItineraires de itToAdd seront detruits
						arretsItineraires.removeAll(arretsDeItToAdd);
						// Les Horaires sur itToAdd passent des arrets de itToAdd aux arrets de tmp
						for (Horaire hr : horaires)
							for (ArretItineraire arretItineraire : arretsDeItToAdd) {
								boolean isSet = false;
								if (hr.getStopPointId().equals(arretItineraire.getObjectId()))
									for (ArretItineraire arretItineraire2 : arretsItineraireParItineraire.get(tmp).values())
										if (arretItineraire.getPosition() == arretItineraire2.getPosition()) {
											hr.setStopPointId(arretItineraire2.getObjectId());
											isSet = true;
											break;
										}
								if (isSet)
									break;
							}
					}
				}
				
				// fusion des courses
				logger.debug("Fusion des courses. Nombre de courses : "+courses.size());
				List<TableauMarche> tableauxMarche = lecteurCourse.getTableauxMarches(ligne);
				if (tableauxMarche == null)
					logger.warn("PAS DE TMs POUR LA LIGNE "+ligne.getNumber());
				else
					logger.debug("NUMBER OF TMs : "+tableauxMarche.size()+", LIGNE : "+ligne.getNumber());
				List<Course> coursesDest = new ArrayList<Course>(/*courses*/);
				
				Map<String, Set<Course>> equalCourses = new HashMap<String, Set<Course>>();
				for (Course cr : courses) {
					String signature = cr.getRouteId();//+"#"+cr.getJourneyPatternId();
					Date date = null;
					for (Horaire hr : horaires)
						if (hr.getVehicleJourneyId().equals(cr.getObjectId()))
							if ((date == null) || (date.after(hr.getDepartureTime())))
								date = hr.getDepartureTime();
					if (date != null)
						signature = signature + "#" + sdf.format(date);
					if (equalCourses.get(signature) == null)
						equalCourses.put(signature, new HashSet<Course>());
					equalCourses.get(signature).add(cr);
				}
				Set<Horaire> hrToRm = new HashSet<Horaire>();
				for (Set<Course> crs : equalCourses.values()) {
					if (crs.size() == 1)
						coursesDest.add(crs.iterator().next());
					else {
						Map<String, Set<Course>> tmpEqualCourses = new HashMap<String, Set<Course>>();
						for (Course cr : crs) {
							String signature = cr.getRouteId();//+"#"+cr.getJourneyPatternId();
							SortedSet<String> hrs = new TreeSet<String>();
							for (Horaire hr : horaires)
								if (hr.getVehicleJourneyId().equals(cr.getObjectId()))
									hrs.add(hr.getStopPointId()+"#"+sdf.format(hr.getDepartureTime()));
							Iterator<String> ite = hrs.iterator();
							while (ite.hasNext())
								signature = signature+"#"+ite.next();
							if (tmpEqualCourses.get(signature) == null)
								tmpEqualCourses.put(signature, new HashSet<Course>());
							tmpEqualCourses.get(signature).add(cr);
						}
						for (Set<Course> tmpCrs : tmpEqualCourses.values()) {
							Course[] crsTb = tmpCrs.toArray(new Course[tmpCrs.size()]);
							Course cr = crsTb[0];
							coursesDest.add(cr);
							for (int i = 1; i < crsTb.length; i++) {
								if (crsTb[i] == cr)
									continue;
								cr.setComment(cr.getComment()+"--"+crsTb[i].getComment());
								cr.setPublishedJourneyName(cr.getPublishedJourneyName()+"--"+crsTb[i].getPublishedJourneyName());
								for (Horaire hr : horaires)
									if (hrToRm.contains(hr))
										continue;
									else
										if (hr.getVehicleJourneyId().equals(crsTb[i].getObjectId()))
											hrToRm.add(hr);
								for (TableauMarche tableauMarche : tableauxMarche)
									for (int j = 0; j < tableauMarche.getVehicleJourneyIdCount(); j++)
										if (tableauMarche.getVehicleJourneyId(j).equals(crsTb[i].getObjectId())) {
											tableauMarche.getTimetable().removeVehicleJourneyId(crsTb[i].getObjectId());
											tableauMarche.getTimetable().removeVehicleJourneyId(cr.getObjectId());
											tableauMarche.getTimetable().addVehicleJourneyId(cr.getObjectId());
										}
							}
						}
					}
				}
				horaires.removeAll(hrToRm);
				
				// Mise a jours des noms des itineraires et des arrets physiques
				for (Itineraire it : itinerairesDest) {
					String tmpName = it.getPublishedName();
					it.setPublishedName(it.getName());
					it.setName(tmpName);
				}
				
				lectureEchange.setItineraires(itinerairesDest);
				if ((lectureEchange.getItineraires() == null) || (lectureEchange.getItineraires().size() == 0)) {
					logger.error("La ligne "+registration+" n'a pas d'itineraire.");
					fw.write("La ligne "+registration+" n'a pas d'itineraire.\n");
					continue;
				}
				lectureEchange.setMissions(missions);
				if ((lectureEchange.getMissions() == null) || (lectureEchange.getMissions().size() == 0)) {
					logger.error("La ligne "+registration+" n'a pas de mission.");
					fw.write("La ligne "+registration+" n'a pas de mission.\n");
					continue;
				}
				lectureEchange.setArrets(arretsItineraires);
				if ((lectureEchange.getArrets() == null) || (lectureEchange.getArrets().size() == 0)) {
					logger.error("La ligne "+registration+" n'a pas d'arret.");
					fw.write("La ligne "+registration+" n'a pas d'arret.\n");
					continue;
				}
				lectureEchange.setArretsPhysiques(arretsPhysiques);
				if ((lectureEchange.getArretsPhysiques() == null) || (lectureEchange.getArretsPhysiques().size() == 0)) {
					logger.error("La ligne "+registration+" n'a pas d'arrets physiques.");
					fw.write("La ligne "+registration+" n'a pas d'arrets physiques.\n");
					continue;
				}
				lectureEchange.setCourses(coursesDest);
				if ((lectureEchange.getCourses() == null) || (lectureEchange.getCourses().size() == 0)) {
					logger.error("La ligne "+registration+" n'a pas de course.");
					fw.write("La ligne "+registration+" n'a pas de course.\n");
					continue;
				}
				lectureEchange.setTableauxMarche(lecteurCourse.getTableauxMarches(ligne)); // TOUS LES TMs
				lectureEchange.setItineraireParArret(itineraireParArret);
				Map<String, String> objectIdParParentObjectId = lecteurArret.getObjectIdParParentObjectId();
				List<String> objectIdZonesGeneriquesTmp = new ArrayList<String>(objectIdZonesGeneriques);
				Map<String, String> _objectIdParParentObjectId = new HashMap<String, String>();
				logger.debug("\tLES ZONES");
				for (String objectId : objectIdZonesGeneriques)
					if (objectIdParParentObjectId.get(objectId) != null) {
						_objectIdParParentObjectId.put(objectId, objectIdParParentObjectId.get(objectId));
						if (!objectIdZonesGeneriquesTmp.contains(objectIdParParentObjectId.get(objectId)))
							objectIdZonesGeneriquesTmp.add(objectIdParParentObjectId.get(objectId));
						if (!zonesCommerciales.contains(zones.get(objectIdParParentObjectId.get(objectId))))
							zonesCommerciales.add(zones.get(objectIdParParentObjectId.get(objectId)));
					}
				lectureEchange.setZoneParenteParObjectId(_objectIdParParentObjectId);
				lectureEchange.setObjectIdZonesGeneriques(objectIdZonesGeneriquesTmp);
				lectureEchange.setZonesCommerciales(zonesCommerciales);
				lectureEchange.setHoraires(horaires);
				lecturesEchange.add(lectureEchange);
			}
			fw.write("##############################################################################################################\n");
		}
		catch(IOException e) {
		}
		finally {
			try {
				fw.flush();
				fw.close();
			}
			catch(IOException e) {
			}
		}
		return lecturesEchange;
	}

	public void lire(String nom) {
		lireCheminFichier(getCheminfichier(nom));
	}
	
	public void lireCheminFichier(String chemin) {
		logger.debug("LECTURE DE DONNEES CSV : "+chemin);
		CSVReader lecteur = null;
		initialisation();
		int ligneNumber = 0;
		FileWriter fw = null;
		try {
			fw = new FileWriter(logFileName, true);
			fw.write("##############################################################################################################\n");
			fw.write("# LECTURE DES DONNEES HASTUS \""+chemin+"\" #\n");
			fw.write("##############################################################################################################\n");
			Set<String> aSet = new HashSet<String>();
			InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(chemin), JeuCaracteres);
			lecteur = new CSVReader(inputStreamReader, separateur);
			int counter = 0;
			String[] ligneCSV = lecteur.readNext();
			while (ligneCSV != null) {
				try {
					ligneNumber++;
					if (lecteurZone.isTitreReconnu(ligneCSV)) {
						if (counter != 0)
							throw new ServiceException(CodeIncident.INVALIDE_FILE_FORMAT, "Les \"Zones\" doivent etre definies au debut du fichier.");
						lecteurZone.lire(ligneCSV);
					}
					else if (lecteurArret.isTitreReconnu(ligneCSV)) {
						if (counter == 0) {
							lecteurArret.setCounter(lecteurZone.getCounter());
							lecteurArret.setZones(lecteurZone.getZones());
							counter++;
						}
						if (counter != 1)
							throw new ServiceException(CodeIncident.INVALIDE_FILE_FORMAT, "Les \"Arrets Physiques\" doivent etre definis en deuxieme juste apres les \"Zones\".");
						lecteurArret.lire(ligneCSV);
					}
					else if (lecteurLigne.isTitreReconnu(ligneCSV)) {
						if (counter == 1) {
							lecteurArret.completion();
							lecteurLigne.setCounter(lecteurArret.getCounter());
							counter++;
						}
						if (counter != 2)
							throw new ServiceException(CodeIncident.INVALIDE_FILE_FORMAT, "Les \"Lignes\" doivent etre definis en troisieme juste apres les \"Arrets Physiques\".");
						lecteurLigne.lire(ligneCSV);
					}
					else if (lecteurItineraire.isTitreReconnu(ligneCSV)) {
						if (counter == 2) {
							lecteurItineraire.setCounter(lecteurLigne.getCounter());
							lecteurItineraire.setLigneParRegistration(lecteurLigne.getLigneParRegistration());
							lecteurItineraire.setZones(lecteurZone.getZones());
							//lecteurItineraire.setObjectIdParParentObjectId(lecteurArret.getObjectIdParParentObjectId());
							//lecteurItineraire.setZonesParObjectId(lecteurZone.getZonesParObjectId());
							counter++;
						}
						if (counter != 3)
							throw new ServiceException(CodeIncident.INVALIDE_FILE_FORMAT, "Les \"Itineraires\" doivent etre definis en quatrieme juste apres les \"Lignes\".");
						lecteurItineraire.lire(ligneCSV);
					}
					else if (lecteurCourse.isTitreReconnu(ligneCSV)) {
						if (counter == 3) {
							lecteurItineraire.completion();
							lecteurCourse.setCounter(lecteurItineraire.getCounter());
							lecteurCourse.setLigneParRegistration(lecteurLigne.getLigneParRegistration());
							counter++;
						}
						if (counter != 4)
							throw new ServiceException(CodeIncident.INVALIDE_FILE_FORMAT, "Les \"Courses\" doivent etre definis en cinqieme juste apres les \"Itineraires\".");
						lecteurCourse.lire(ligneCSV);
					}
					else if (lecteurHoraire.isTitreReconnu(ligneCSV)) {
						if (counter == 4) {
							lecteurHoraire.setCounter(lecteurCourse.getCounter());
							lecteurHoraire.setCourseParNom(lecteurCourse.getCourseParNom());
							lecteurHoraire.setItineraireParNom(lecteurItineraire.getItineraireParNom());
							lecteurHoraire.setArretsPhysiquesParNom(lecteurArret.getArretsPhysiques());
							lecteurHoraire.setObjectIdParParentObjectId(lecteurArret.getObjectIdParParentObjectId());
							counter++;
						}
						if (counter != 5)
							throw new ServiceException(CodeIncident.INVALIDE_FILE_FORMAT, "Les \"Horaires\" doivent etre definis en sixieme juste apres les \"Courses\".");
						lecteurHoraire.lire(ligneCSV);
					}
					else if (lecteurOrdre.isTitreReconnu(ligneCSV)) {
						if (counter == 5) {
							lecteurHoraire.completion();
							lecteurOrdre.setCounter(lecteurHoraire.getCounter());
							counter++;
						}
						if (counter != 6)
							throw new ServiceException(CodeIncident.INVALIDE_FILE_FORMAT, "Les \"Ordres\" doivent etre definis en dernier juste apres les \"Horaires\".");
						lecteurOrdre.lire(ligneCSV);
					}
					else if (!isEmptyLigne(ligneCSV))
						throw new ServiceException(CodeIncident.INVALIDE_LIGNE_FORMAT, "La ligne \""+ligneCSV+"\" est invalide.");
				}
				catch(ServiceException e) {
					if (aSet.add(e.getCode()+" : "+e.getMessage())) {
						logger.error("LIGNE NUMERO "+ligneNumber+" : "+e.getCode()+" : "+e.getMessage());
						fw.write("LIGNE NUMERO "+ligneNumber+" : "+e.getCode()+" : "+e.getMessage()+"\n");
					}
				}
				ligneCSV = lecteur.readNext();
			}
			lecteur.close();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new ServiceException(CodeIncident.FILE_NOT_FOUND, "Le fichier \""+chemin+"\" est introuvable.");
		}
		catch (ServiceException e) {
			throw new ServiceException(e.getCode(), "LIGNE NUMERO "+ligneNumber+" : "+e.getMessage());
		}
		catch(Exception e) {
			throw new ServiceException(CodeIncident.DONNEE_INVALIDE, "Echec initialisation", e);
		}
		finally {
			try {
				fw.flush();
				fw.close();
			}
			catch(Exception e) {
			}
			if (lecteur != null) {
				try {
					logger.error("Numero de ligne actuel :"+ligneNumber);
					lecteur.close();
				}
				catch (Exception e) {
					logger.error("Echec cloture du fichier "+chemin+", "+e.getMessage(), e);
				}
			}
		}
		logger.debug("FIN DE LECTURE DE DONNEES CSV : "+chemin);
	}
	
	private boolean isEmptyLigne(String[] ligneCSV) {
		if (ligneCSV == null)
			return true;
		if (ligneCSV.length == 0)
			return true;
		for (int i = 0; i < ligneCSV.length; i++)
			if (ligneCSV[i] != null)
				if (ligneCSV[i].trim().length() != 0)
					return false;
		return true;
	}

	private String getCheminfichier(String nom) {
		return repertoire + File.separator + nom;
	}
	
	private void initialisation() {
		lecteurZone.reinit();
		lecteurArret.reinit();
		lecteurLigne.reinit();
		lecteurItineraire.reinit();
		lecteurCourse.reinit();
		lecteurHoraire.reinit();
		lecteurOrdre.reinit();
	}
	
	public String getRepertoire() {
		return repertoire;
	}
	
	public void setRepertoire(String repertoire) {
		this.repertoire = repertoire;
	}
	
	public char getSeparateur() {
		return separateur;
	}
	
	public void setSeparateur(char separateur) {
		this.separateur = separateur;
	}
	
	public ILecteurZone getLecteurZone() {
		return lecteurZone;
	}
	
	public void setLecteurZone(ILecteurZone lecteurZone) {
		this.lecteurZone = lecteurZone;
	}
	
	public ILecteurArret getLecteurArret() {
		return lecteurArret;
	}
	
	public void setLecteurArret(ILecteurArret lecteurArret) {
		this.lecteurArret = lecteurArret;
	}
	
	public ILecteurLigne getLecteurLigne() {
		return lecteurLigne;
	}
	
	public void setLecteurLigne(ILecteurLigne lecteurLigne) {
		this.lecteurLigne = lecteurLigne;
	}
	
	public ILecteurItineraire getLecteurItineraire() {
		return lecteurItineraire;
	}
	
	public void setLecteurItineraire(ILecteurItineraire lecteurItineraire) {
		this.lecteurItineraire = lecteurItineraire;
	}
	
	public ILecteurCourse getLecteurCourse() {
		return lecteurCourse;
	}
	
	public void setLecteurCourse(ILecteurCourse lecteurCourse) {
		this.lecteurCourse = lecteurCourse;
	}
	
	public ILecteurHoraire getLecteurHoraire() {
		return lecteurHoraire;
	}
	
	public void setLecteurHoraire(ILecteurHoraire lecteurHoraire) {
		this.lecteurHoraire = lecteurHoraire;
	}
	
	public ILecteurOrdre getLecteurOrdre() {
		return lecteurOrdre;
	}
	
	public void setLogFileName(String logFileName) {
		this.logFileName = logFileName;
	}

	public void setLecteurOrdre(ILecteurOrdre lecteurOrdre) {
		this.lecteurOrdre = lecteurOrdre;
	}
}
