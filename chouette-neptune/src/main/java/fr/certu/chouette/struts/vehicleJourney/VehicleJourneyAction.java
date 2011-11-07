package fr.certu.chouette.struts.vehicleJourney;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.validation.SkipValidation;

import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Preparable;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.filter.Filter;
import fr.certu.chouette.manager.INeptuneManager;
import fr.certu.chouette.model.neptune.JourneyPattern;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.model.neptune.Timetable;
import fr.certu.chouette.model.neptune.VehicleJourney;
import fr.certu.chouette.model.user.User;
import fr.certu.chouette.struts.GeneriqueAction;

public class VehicleJourneyAction extends GeneriqueAction implements ModelDriven<VehicleJourney>, Preparable
{

   private static final long               serialVersionUID = -6995413388411467285L;

   private static final Logger             log              = Logger.getLogger(VehicleJourneyAction.class);

   private static final Set<String>        PARTICULARITES_VALIDES;
   static
   {
      PARTICULARITES_VALIDES = new HashSet<String>();
      PARTICULARITES_VALIDES.add("TAD");
   }

   private static String                   actionMsg        = null;
   private static String                   errorMsg         = null;

   // Managers
   @Getter
   @Setter
   private INeptuneManager<VehicleJourney> vehicleJourneyManager;
   @Getter
   @Setter
   private INeptuneManager<Route>          routeManager;
   @Getter
   @Setter
   private INeptuneManager<JourneyPattern>          journeyPatternManager;
   @Getter
   @Setter
   private INeptuneManager<Timetable>      timetableManager;
   @Getter
   @Setter
   private INeptuneManager<Line>           lineManager;
   // Identifiants
   @Getter
   @Setter
   private Long                            idCourse;
   @Getter
   @Setter
   private Long                            idLigne;
   @Getter
   @Setter
   private Long                            idItineraire;
   @Getter
   @Setter
   private Long                            idTableauMarche;
   @Getter
   private List<String>                    particularites   = new ArrayList<String>();
   @Getter
   @Setter
   private Time                            seuilHeureDepartCourse;
   @Getter
   @Setter
   private Long                            page;
   // Liste des courses et course sélectionnée
   // @Setter private String saisieTableauMarche;
   @Setter
   private Long                            saisieTableauMarcheKey;
   private List<Timetable>                 tableauxMarcheAssocieCourse;
   private List<Timetable>                 tableauxMarchePasAssocieCourse;
   @Getter
   private VehicleJourney                  model            = new VehicleJourney();
   private String                          mappedRequest;

   private User                            user             = null;

   /********************************************************
    * MODEL + PREPARE *
    ********************************************************/

   @SuppressWarnings("unchecked")
   @Override
   public void prepare() throws Exception
   {
      log.debug("Prepare with id : " + getIdCourse());
      if (getIdCourse() == null)
      {
         model = new VehicleJourney();
      }
      else
      {
         model = vehicleJourneyManager.getById(getIdCourse());
         String vehicleTypeIdentifier = model.getVehicleTypeIdentifier();
         if (vehicleTypeIdentifier != null)
         {
            particularites = Arrays.asList(vehicleTypeIdentifier.split(","));
         }
      }

      if (idCourse != null)
      {
         // Création d'une map idTableauMarche -> TableauMarche
         List<Timetable> tableauxMarches = timetableManager.getAll(user);
         // Récupération des tableaux de marche associés à la course
         tableauxMarcheAssocieCourse = model.getTimetables();
         // Récupération de la liste des ids de tous les tableaux de marche
         Map<Long, Timetable> tableauxMarcheParId = new HashMap<Long, Timetable>();
         for (Timetable tableauMarche : tableauxMarches)
         {
            tableauxMarcheParId.put(tableauMarche.getId(), tableauMarche);
         }
         // Elimination dans la liste des tableaux de marche ceux déjà associés
         // à la course
         for (Timetable tableauMarche : tableauxMarcheAssocieCourse)
         {
            if (tableauxMarcheParId.containsKey(tableauMarche.getId()))
            {
               tableauxMarcheParId.remove(tableauMarche.getId());
            }
         }
         // Récupération des tableaux de marche non associés à la course
         tableauxMarchePasAssocieCourse = new ArrayList<Timetable>();
         tableauxMarchePasAssocieCourse.addAll(tableauxMarcheParId.values());
         // Place en requête la liste des tableaux de marche pas associé à la
         // course
         request.put("jsonTableauMarches", getJsonTableauMarches());
      }
   }

   /********************************************************
    * CRUD *
    ********************************************************/
   @SuppressWarnings("unchecked")
   @SkipValidation
   public String list()
   {
      log.debug("List of vehicleJourney");
      Filter filter = Filter.getNewEqualsFilter(VehicleJourney.ROUTE + "." + Route.ID, idItineraire);
      try
      {
         this.request.put("courses", vehicleJourneyManager.getAll(user, filter));
      }
      catch (ChouetteException e)
      {
         errorMsg = e.getLocalizedMessage();
      }
      if (actionMsg != null)
      {
         addActionMessage(actionMsg);
         actionMsg = null;
      }
      if (errorMsg != null)
      {
         addActionError(errorMsg);
         errorMsg = null;
      }
      return LIST;
   }

   @SkipValidation
   public String add()
   {
      setMappedRequest(SAVE);
      return EDIT;
   }

   public String save()
   {

      try
      {
         // ré-affecter l'identifiant de l'itinéraire sur la course
         if (model.getRoute() == null)
         {
            Route route;
            route = routeManager.getById(idItineraire);
            model.setRoute(route);
            if (route.getJourneyPatterns().isEmpty())
            {
               JourneyPattern journeyPattern = new JourneyPattern();
               journeyPattern.setObjectId(model.getRoute().getObjectId().split(":")[0]);
               journeyPattern.setRoute(route);
               for (StopPoint stop : route.getStopPoints())       
               {
                  journeyPattern.addStopPoint(stop);
                  journeyPatternManager.save(user, journeyPattern, false);
               }
               model.setJourneyPattern(journeyPattern);
            }
            else
            {
               model.setJourneyPattern(route.getJourneyPatterns().get(0));
            }
         }

         // remplissage du champ vehicleTypeIdentifier avec les particularites
         if (particularites.size() > 0)
            model.setVehicleTypeIdentifier(StringUtils.join(particularites, ','));
         else
            model.setVehicleTypeIdentifier(null);

         // base objectId on routeObjectId
         model.setObjectId(model.getRoute().getObjectId().split(":")[0]);

         vehicleJourneyManager.save(user, model, false);

         // addActionMessage(getText("course.create.ok"));
         actionMsg = getText("course.create.ok");
         setMappedRequest(SAVE);
         log.debug("Create vehicleJourney with id : " + getModel().getId());
      }
      catch (ChouetteException e)
      {
         errorMsg = e.getLocalizedMessage();
      }
      return REDIRECTLIST;
   }

   @SkipValidation
   public String edit()
   {
      setMappedRequest(UPDATE);
      return EDIT;
   }

   public String update()
   {

      try
      {
         // ré-affecter l'identifiant de l'itinéraire sur la course
         if (model.getRoute() == null)
         {
            Route route;
            route = routeManager.getById(idItineraire);
            model.setRoute(route);
         }

         // remplissage du champ vehicleTypeIdentifier avec les particularites
         if (particularites.size() > 0)
         {
            model.setVehicleTypeIdentifier(StringUtils.join(particularites, ','));
         }
         else
         {
            model.setVehicleTypeIdentifier(null);
         }

         vehicleJourneyManager.update(user, model);
         setMappedRequest(UPDATE);
         actionMsg = getText("course.update.ok");
         log.debug("Update vehicleJourney with id : " + model.getId());
      }
      catch (ChouetteException e)
      {
         errorMsg = e.getLocalizedMessage();
      }
      return REDIRECTLIST;
   }

   public String delete()
   {
      try
      {
         vehicleJourneyManager.remove(user, model, false);
         actionMsg = getText("course.delete.ok");
         log.debug("Delete vehicleJourney with id : " + getModel().getId());
      }
      catch (ChouetteException e)
      {
         errorMsg = e.getLocalizedMessage();
      }
      return REDIRECTLIST;
   }

   @SkipValidation
   public String cancel()
   {
      addActionMessage(getText("course.cancel.ok"));
      return REDIRECTLIST;
   }

   @Override
   @SkipValidation
   public String input() throws Exception
   {
      return REDIRECTLIST;
   }

   /********************************************************
    * METHOD ACTION *
    ********************************************************/
   // this prepares command for button on initial screen write
   public void setMappedRequest(String actionMethod)
   {
      this.mappedRequest = actionMethod;
   }

   // when invalid, the request parameter will restore command action
   public void setActionMethod(String method)
   {
      this.mappedRequest = method;
   }

   public String getActionMethod()
   {
      return mappedRequest;
   }

   /********************************************************
    * JSON *
    ********************************************************/
   public String getJsonTableauMarches()
   {
      StringBuffer resultat = new StringBuffer("{");
      Timetable dernier = null;
      List<Timetable> tms = tableauxMarchePasAssocieCourse;
      if (tms.size() > 0)
      {
         dernier = tms.remove(tms.size() - 1);
      }
      for (Timetable tm : tms)
      {
         resultat.append("\"");
         resultat.append(tm.getComment());
         resultat.append("(");
         resultat.append(tm.getObjectId());
         resultat.append(")\": ");
         resultat.append(tm.getId());
         resultat.append(",");
      }
      if (dernier != null)
      {
         resultat.append("\"");
         resultat.append(dernier.getComment());
         resultat.append("(");
         resultat.append(dernier.getObjectId());
         resultat.append(")\": ");
         resultat.append(dernier.getId());
      }
      resultat.append("}");
      // bien penser remettre élément dans la liste pour qu'elle demeure
      // inchangée
      tableauxMarchePasAssocieCourse.add(dernier);
      return resultat.toString();
   }

   public Line getLigne()
   {
      try
      {
         return lineManager.getById(idLigne);
      }
      catch (ChouetteException e)
      {
         addActionError(e.getLocalizedMessage());
         return null;
      }
   }

   public Route getItineraire()
   {
      try
      {
         return routeManager.getById(idItineraire);
      }
      catch (ChouetteException e)
      {
         addActionError(e.getLocalizedMessage());
         return null;
      }
   }

   public List<Timetable> getTableauxMarche()
   {
      return tableauxMarcheAssocieCourse;
   }

   public List<Timetable> getTableauxMarchePasAssocieCourse()
   {
      return tableauxMarchePasAssocieCourse;
   }

   @SkipValidation
   public String creerAssociationTableauMarche()
   {
      if (saisieTableauMarcheKey != null)
      {
         for (Timetable tableauMarche : tableauxMarchePasAssocieCourse)
         {
            if (saisieTableauMarcheKey.equals(tableauMarche.getId()))
            {
               model.addTimetable(tableauMarche);
               break;
            }
         }
         try
         {
            vehicleJourneyManager.update(user, model);
            addActionMessage(getText("course.associationTableauMarche.ok"));

         }
         catch (ChouetteException e)
         {
            addActionError(getText("course.associationTableauMarche.ko"));
         }
      }
      return REDIRECTEDIT;
   }

   @SkipValidation
   public String supprimerAssociationTableauMarche()
   {
      if (idTableauMarche != null)
      {
         for (Timetable tableauMarche : tableauxMarcheAssocieCourse)
         {
            if (idTableauMarche.equals(tableauMarche.getId()))
            {
               model.removeTimetable(tableauMarche);
               break;
            }
         }
         try
         {
            vehicleJourneyManager.update(user, model);
            addActionMessage(getText("course.supprimerAssociationTableauMarche.ok"));

         }
         catch (ChouetteException e)
         {
            addActionError(e.getLocalizedMessage());
         }
      }

      return REDIRECTEDIT;
   }

   public String getModeTransportLigne()
   {
      Line ligne;
      try
      {
         ligne = lineManager.getById(idLigne);
         return ligne.getTransportModeName().toString();
      }
      catch (ChouetteException e)
      {
         addActionError(e.getLocalizedMessage());
         return null;
      }

   }

   public void setParticularites(List<String> particularites)
   {
      // hack : remove empty strings from list
      while (particularites.contains(""))
         particularites.remove("");
      this.particularites = particularites;
   }

   public static Set<String> getParticularitesValides()
   {
      return PARTICULARITES_VALIDES;
   }

}
