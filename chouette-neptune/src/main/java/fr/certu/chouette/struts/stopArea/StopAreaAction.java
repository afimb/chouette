package fr.certu.chouette.struts.stopArea;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.interceptor.validation.SkipValidation;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Preparable;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.filter.Filter;
import fr.certu.chouette.manager.INeptuneManager;
import fr.certu.chouette.model.neptune.AreaCentroid;
import fr.certu.chouette.model.neptune.Company;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.PTNetwork;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.model.neptune.type.Address;
import fr.certu.chouette.model.neptune.type.ChouetteAreaEnum;
import fr.certu.chouette.model.neptune.type.ProjectedPoint;
import fr.certu.chouette.struts.GeneriqueAction;
import fr.certu.chouette.struts.enumeration.EnumerationApplication;

@SuppressWarnings("serial")
public class StopAreaAction extends GeneriqueAction implements ModelDriven<StopArea>, Preparable
{

   private static final Log           logger                  = LogFactory.getLog(StopAreaAction.class);
   @Getter
   @Setter
   private INeptuneManager<PTNetwork> networkManager;
   @Getter
   @Setter
   private INeptuneManager<Company> companyManager;
   @Setter
   @Getter
   private INeptuneManager<StopArea>  stopAreaManager;
   @Setter
   @Getter
   private INeptuneManager<Line>      lineManager;

   @Getter
   private StopArea                   model                = new StopArea();
   @Setter
   private String                     mappedRequest;
   @Getter
   @Setter
   private Long                       idPositionGeographique;
   // Gestion des zones
   @Getter
   @Setter
   private StopArea                   searchCriteria;
   @Getter
   @Setter
   private Line                       lineCriteria;
   @Getter
   @Setter
   private List<StopArea>             children;
   @Getter
   @Setter
   private List<Line>                 lines;
   @Getter
   @Setter
   private StopArea                   father;
   @Getter
   @Setter
   private Long                       idChild;
   @Getter
   @Setter
   private Long                       idFather;
   @Getter
   @Setter
   private String                     authorizedType;
   @Getter
   private List<Route>                itineraires          = new ArrayList<Route>();
   private Map<Long, Line>            ligneParIdItineraire;
   private Map<Long, PTNetwork>       reseauParIdLigne;
   private Map<Long, Boolean>         presenceItineraireParIdPhysique;
   // Paramètre permettant la gestion des redirections pour les méthodes
   // modifier ArretPhysique, redirection vers :
   // - liste des horaires de passage
   // - ou liste des arrêts physiques
   @Getter
   @Setter
   private String                     actionSuivante;
   // Numéro de la page actuelle pour la navigation parmi les différentes
   // courses
   // private Integer page;
   // Chaine de caractere implémenté pour complété les retours des actions fait
   // par struts
   @Getter
   @Setter
   private String                     nomArret             = null;
   @Getter
   @Setter
   private String                     codeInsee            = null;
   @Getter
   @Setter
   private Long                       idReseau             = null;
   @Getter
   @Setter
   private List<PTNetwork>            reseaux;
   private Map<Long, PTNetwork>       reseauParId;
   @Getter
   @Setter
   private Long                       idItineraire;
   @Getter
   @Setter
   private Long                       idLigne;
   // Type de position Geographique
   private static final String        ARRETPHYSIQUE        = "/boardingPosition";
   private static final String        ZONE                 = "/stopPlace";
   private static final String        ITL                  = "/routingConstraint";
   @Getter
   @Setter
   private Long                       idArretDestination   = null;
   @Getter
   @Setter
   private String                     nomArretDestination  = null;
   @Getter
   @Setter
   private Long                       idArretSource        = null;
   private String                     boardingPositionName = "";
   @Getter
   @Setter
   private Long                       idLine;
   // private Integer START_INDEX_AJAX_LIST = 0;
   // private Integer END_INDEX_AJAX_LIST = 10;
   private static String              actionMsg            = null;
   private static String              actionErr            = null;

   /********************************************************
    * MODEL + PREPARE *
    ********************************************************/

   public void prepare() throws Exception
   {
      logger.debug("Prepare with id : " + getIdPositionGeographique());
      if (getIdPositionGeographique() == null)
      {
         model = new StopArea();
      }
      else
      {
         model = stopAreaManager.get(null, Filter.getNewEqualsFilter("id", getIdPositionGeographique()));

      }

      // Chargement des réseaux
      reseaux = networkManager.getAll(null);
      reseauParId = new Hashtable<Long, PTNetwork>();
      for (PTNetwork reseau : reseaux)
      {
         reseauParId.put(reseau.getId(), reseau);
      }

      if (idPositionGeographique == null)
      {
         return;
      }

      // Création des zones filles et parentes
      children = model.getContainedStopAreas();

      father = null;
      if (model.getParents() != null)
      {
         for (StopArea parent : model.getParents())
         {
            if (!parent.getAreaType().equals(ChouetteAreaEnum.ITL))
            {
               father = parent;
               break;
            }
         }
      }

      // lignes si ITL
      lines = model.getRoutingConstraintLines();

      // Création de la liste des itinéraires
      // itineraires =
      // positionGeographiqueManager.getItinerairesArretPhysique(idPositionGeographique);
      // Getting stopPoints by stopArea
      List<StopPoint> stopPoints = model.getContainedStopPoints();
      for (StopPoint stopPoint : stopPoints)
      {
         Route route = stopPoint.getRoute();
         if (route != null && !itineraires.contains(route))
            itineraires.add(route);
      }

      ligneParIdItineraire = new Hashtable<Long, Line>();
      // Création de la liste des identifiants de lignes (pas de doublons)
      // Collection<Long> idsLignes = new HashSet<Long>();
      List<Line> lignes = new ArrayList<Line>();
      for (Route itineraire : itineraires)
      {
         Line line = itineraire.getLine();
         if (line != null)
         {
            lignes.add(line);
         }
      }

      // Création d'une map liant id Ligne -> Objet Ligne
      Map<Long, Line> ligneParId = new Hashtable<Long, Line>();
      for (Line ligne : lignes)
      {
         ligneParId.put(ligne.getId(), ligne);
      }
      // Création d'une hashtable liant id Itineraire -> Objet Ligne
      for (Route itineraire : itineraires)
      {
         Line line = itineraire.getLine();
         if (line != null)
         {
            Line ligne = ligneParId.get(line.getId());
            ligneParIdItineraire.put(itineraire.getId(), ligne);
         }
      }

      reseauParIdLigne = new Hashtable<Long, PTNetwork>();
      // Création d'une hashtable liant id Ligne -> Objet Reseau
      for (Line ligne : lignes)
      {
         PTNetwork network = ligne.getPtNetwork();
         if (network != null)
         {
            PTNetwork reseau = reseauParId.get(network.getId());
            reseauParIdLigne.put(ligne.getId(), reseau);
         }
      }
   }

   /********************************************************
    * CRUD
    * 
    * @throws ChouetteException
    *            *
    ********************************************************/
   @SuppressWarnings("unchecked")
   @SkipValidation
   public String list() throws ChouetteException
   {
      // Récupération du namespace pour basculer sur des arrèts physiques ou
      // zones
      String namespace = ActionContext.getContext().getActionInvocation().getProxy().getNamespace();
      logger.debug("namespace :  " + namespace);
      List<ChouetteAreaEnum> areaTypes = new ArrayList<ChouetteAreaEnum>();

      if (ARRETPHYSIQUE.equals(namespace))
      {
         areaTypes.add(ChouetteAreaEnum.QUAY);
         areaTypes.add(ChouetteAreaEnum.BOARDINGPOSITION);
      }
      else if (ZONE.equals(namespace))
      {
         areaTypes.add(ChouetteAreaEnum.STOPPLACE);
         areaTypes.add(ChouetteAreaEnum.COMMERCIALSTOPPOINT);
      }
      else
      // ITL
      {
         areaTypes.add(ChouetteAreaEnum.ITL);
      }

      if (nomArret != null)
      {
         if ("".equals(nomArret.trim()))
         {
            nomArret = null;
         }
      }
      if (codeInsee != null)
      {
         if ("".equals(codeInsee.trim()))
         {
            codeInsee = null;
         }
      }

      Filter filter = Filter.getNewInFilter(StopArea.AREA_TYPE, areaTypes);

      if (nomArret != null && codeInsee != null)
      {
         filter = Filter.getNewAndFilter(
               Filter.getNewIgnoreCaseLikeFilter(StopArea.NAME, nomArret),
               Filter.getNewEqualsFilter(StopArea.AREACENTROID + "." + AreaCentroid.ADDRESS + "."
                     + Address.COUNTRY_CODE, codeInsee), filter);
      }
      else if (nomArret != null)
      {
         filter = Filter.getNewAndFilter(Filter.getNewIgnoreCaseLikeFilter(StopArea.NAME, nomArret), filter);
      }
      else if (codeInsee != null)
      {
         filter = Filter.getNewAndFilter(
               Filter.getNewEqualsFilter(StopArea.AREACENTROID + "." + AreaCentroid.ADDRESS + "."
                     + Address.COUNTRY_CODE, codeInsee), filter);
      }

      List<StopArea> positionGeographiques = stopAreaManager.getAll(null, filter);
      if (idReseau != null)
      {
         List<StopArea> filteredStopAreas = new ArrayList<StopArea>();
         Set<Route> validRoute = new HashSet<Route>();
         Set<Route> unvalidRoute = new HashSet<Route>();
         if (ARRETPHYSIQUE.equals(namespace))
         {
            for (StopArea stopArea : positionGeographiques)
            {
               if (stopArea.getContainedStopPoints() != null)
               {
                  boolean valid = checkPhysicalStopForNetworkFilter(validRoute, unvalidRoute, stopArea);
                  if (valid)
                     filteredStopAreas.add(stopArea);
               }
            }
            positionGeographiques = filteredStopAreas;
         }
         else
         // ZONE or ITL
         {
            for (StopArea stopArea : positionGeographiques)
            {
               ChouetteAreaEnum type = stopArea.getAreaType();
               if (type.equals(ChouetteAreaEnum.COMMERCIALSTOPPOINT))
               {
                  boolean valid = checkCommercialStopForNetworkFilter(validRoute, unvalidRoute, stopArea);
                  if (valid)
                     filteredStopAreas.add(stopArea);
               }
               else
               {
                  boolean valid = checkStopPlaceForNetworkFilter(validRoute, unvalidRoute, stopArea);
                  if (valid)
                     filteredStopAreas.add(stopArea);
               }
            }
         }
      }
      request.put("positionGeographiques", positionGeographiques);
      logger.debug("List of stopArea");
      if (actionMsg != null)
      {
         addActionMessage(actionMsg);
         actionMsg = null;
      }
      if (actionErr != null)
      {
         addActionError(actionErr);
         actionErr = null;
      }
      return LIST;
   }

   private boolean checkStopPlaceForNetworkFilter(Set<Route> validRoute, Set<Route> unvalidRoute, StopArea stopArea)
   {
      boolean valid = false;
      if (stopArea.getContainedStopAreas() == null)
         return valid;
      for (StopArea child : stopArea.getContainedStopAreas())
      {
         if (child.getAreaType().equals(ChouetteAreaEnum.COMMERCIALSTOPPOINT))
         {
            if (checkCommercialStopForNetworkFilter(validRoute, unvalidRoute, child))
            {
               valid = true;
               break;
            }
         }
         else
         {
            if (checkStopPlaceForNetworkFilter(validRoute, unvalidRoute, child))
            {
               valid = true;
               break;
            }
         }

      }
      return valid;
   }

   /**
    * @param validRoute
    * @param unvalidRoute
    * @param stopArea
    * @return
    */
   private boolean checkCommercialStopForNetworkFilter(Set<Route> validRoute, Set<Route> unvalidRoute, StopArea stopArea)
   {
      boolean valid = false;
      if (stopArea.getContainedStopAreas() == null)
         return valid;
      for (StopArea child : stopArea.getContainedStopAreas())
      {
         if (checkPhysicalStopForNetworkFilter(validRoute, unvalidRoute, child))
         {
            valid = true;
            break;
         }
      }
      return valid;
   }

   /**
    * @param validRoute
    * @param unvalidRoute
    * @param stopArea
    * @return
    */
   private boolean checkPhysicalStopForNetworkFilter(Set<Route> validRoute, Set<Route> unvalidRoute, StopArea stopArea)
   {
      boolean valid = false;
      if (stopArea.getContainedStopPoints() == null)
         return false;
      for (StopPoint point : stopArea.getContainedStopPoints())
      {
         Route route = point.getRoute();
         if (validRoute.contains(route))
         {
            valid = true;
            break;
         }
         if (unvalidRoute.contains(route))
         {
            break;
         }
         if (route.getLine().getPtNetwork().getId().equals(idReseau))
         {
            valid = true;
            validRoute.add(route);
            break;
         }
         unvalidRoute.add(route);
      }
      return valid;
   }

   @SkipValidation
   public String add()
   {
      setMappedRequest(SAVE);
      return EDIT;
   }

   public String save() throws ChouetteException
   {
      stopAreaManager.addNew(null, model);
      if (getTypePositionGeographique().equals(ARRETPHYSIQUE))
      {
         actionMsg = getText("arretPhysique.create.ok");
         logger.debug("Create boardingPosition with id : " + model.getId());
      }
      else if (getTypePositionGeographique().equals(ZONE))
      {
         actionMsg = getText("zone.create.ok");
         logger.debug("Create stopPlace with id : " + model.getId());
      }
      else
      // ITL
      {
         actionMsg = getText("routingConstraint.create.ok");
         logger.debug("Create routingConstraint with id : " + model.getId());
      }

      setIdPositionGeographique(model.getId());
      setMappedRequest(UPDATE);
      return REDIRECTEDIT;
   }

   @SkipValidation
   public String edit()
   {
      // Get namespace to know if it's a stop place or a boarding position
      String namespace = ActionContext.getContext().getActionInvocation().getProxy().getNamespace();

      if (ZONE.equals(namespace))
      {
         for (StopArea positionGeographique : children)
         {
            AreaCentroid areaCentroid = positionGeographique.getAreaCentroid();
            if (areaCentroid != null)
            {
               ProjectedPoint projectedPoint = areaCentroid.getProjectedPoint();
               if (!((areaCentroid.getLongitude() != null && areaCentroid.getLatitude() != null) || (projectedPoint != null)))
               {
                  actionMsg = getText("stopplace.children.nocoordinates");
                  break;
               }
            }

         }
      }

      setMappedRequest(UPDATE);
      if (actionMsg != null)
      {
         addActionMessage(actionMsg);
         actionMsg = null;
      }
      if (actionErr != null)
      {
         addActionError(actionErr);
         actionErr = null;
      }
      return EDIT;
   }

   public String update() throws ChouetteException
   {
      stopAreaManager.update(null, model);
      if (getTypePositionGeographique().equals(ARRETPHYSIQUE))
      {
         actionMsg = getText("arretPhysique.update.ok");
         logger.debug("Update boardingPosition with id : " + model.getId());
      }
      else if (getTypePositionGeographique().equals(ZONE))
      {
         actionMsg = getText("zone.update.ok");
         logger.debug("Update stopPlace with id : " + model.getId());
      }
      else
      // ITL
      {
         actionMsg = getText("routingConstraint.update.ok");
         logger.debug("Update routingConstraint with id : " + model.getId());
      }

      setMappedRequest(UPDATE);
      return REDIRECTEDIT;
   }

   public String delete() throws ChouetteException
   {
      try
      {
         stopAreaManager.remove(null, model, false);
      }
      catch (ChouetteException e)
      {
         actionErr = getText("arretPhysique.used");
         logger.error("actionErr",e);
         return REDIRECTLIST;
      }
      if (getTypePositionGeographique().equals(ARRETPHYSIQUE))
      {
         actionMsg = getText("arretPhysique.delete.ok");
         logger.debug("Delete boardingPosition with id : " + model.getId());
      }
      else if (getTypePositionGeographique().equals(ZONE))
      {
         actionMsg = getText("zone.delete.ok");
         logger.debug("Delete stopPlace with id : " + model.getId());
      }
      else
      // ITL
      {
         actionMsg = getText("routingConstraint.delete.ok");
         logger.debug("Delete routingConstraint with id : " + model.getId());
      }

      return REDIRECTLIST;
   }

   @SkipValidation
   public String cancel()
   {
      if (getTypePositionGeographique().equals(ARRETPHYSIQUE))
      {
         actionMsg = getText("arretPhysique.cancel.ok");
      }
      else if (getTypePositionGeographique().equals(ZONE))
      {
         actionMsg = getText("zone.cancel.ok");
      }
      else
      // ITL
      {
         actionMsg = getText("routingConstraint.cancel.ok");
      }
      return REDIRECTLIST;
   }

   @Override
   @SkipValidation
   public String input() throws Exception
   {
      return INPUT;
   }

   @SkipValidation
   public String fusionnerArrets()
   {
      if (idArretSource == null || idArretDestination == null)
      {
         actionErr = getText("arretPhysique.merge.ko");
      }
      else
      {
         actionMsg = getText("arretPhysique.merge.ok");
         // TODO
         // positionGeographiqueManager.fusionnerPositionsGeographiques(idArretSource,
         // idArretDestination);
      }
      return REDIRECTLIST;
   }

   @SkipValidation
   public String search()
   {
      if (idPositionGeographique != null)
      {
         if ("addChild".equals(getActionSuivante()))
         {
            switch (model.getAreaType())
            {
            case ITL:
               authorizedType = EnumerationApplication.AUTHORIZEDTYPESET_ALL;
               break;
            case STOPPLACE:
               authorizedType = EnumerationApplication.AUTHORIZEDTYPESET_ALL;
               break;

            case COMMERCIALSTOPPOINT:
               authorizedType = EnumerationApplication.AUTHORIZEDTYPESET_QB;
               break;

            case BOARDINGPOSITION:
            case QUAY:
               break;
            }
         }
         else if ("addFather".equals(getActionSuivante()))
         {
            switch (model.getAreaType())
            {
            case STOPPLACE:
               authorizedType = EnumerationApplication.AUTHORIZEDTYPESET_S;
               break;

            case COMMERCIALSTOPPOINT:
               authorizedType = EnumerationApplication.AUTHORIZEDTYPESET_S;
               break;

            case BOARDINGPOSITION:
            case QUAY:
               authorizedType = EnumerationApplication.AUTHORIZEDTYPESET_CS;
               break;
            }
         }
         else if ("addLine".equals(getActionSuivante()))
         {

            return SEARCH_LINE;
         }
      }
      return SEARCH;
   }

   @SuppressWarnings("unchecked")
   @SkipValidation
   public String searchResults() throws ChouetteException
   {
      List<StopArea> positionGeographiquesResultat = new ArrayList<StopArea>();

      // Clause areaType
      Collection<String> areas = new ArrayList<String>();
      if (searchCriteria.getAreaType() != null)
      {
         areas.add(searchCriteria.getAreaType().toString());
      }
      else
      {
         if (EnumerationApplication.AUTHORIZEDTYPESET_C.equals(authorizedType))
         {
            areas.add(ChouetteAreaEnum.COMMERCIALSTOPPOINT.toString());
         }
         else if (EnumerationApplication.AUTHORIZEDTYPESET_CS.equals(authorizedType))
         {
            areas.add(ChouetteAreaEnum.COMMERCIALSTOPPOINT.toString());
            areas.add(ChouetteAreaEnum.STOPPLACE.toString());
         }
         else if (EnumerationApplication.AUTHORIZEDTYPESET_QB.equals(authorizedType))
         {
            areas.add(ChouetteAreaEnum.QUAY.toString());
            areas.add(ChouetteAreaEnum.BOARDINGPOSITION.toString());
         }
         else if (EnumerationApplication.AUTHORIZEDTYPESET_S.equals(authorizedType))
         {
            areas.add(ChouetteAreaEnum.STOPPLACE.toString());
         }
         else if (EnumerationApplication.AUTHORIZEDTYPESET_ALL.equals(authorizedType))
         {
            areas.add(ChouetteAreaEnum.COMMERCIALSTOPPOINT.toString());
            areas.add(ChouetteAreaEnum.STOPPLACE.toString());
            areas.add(ChouetteAreaEnum.BOARDINGPOSITION.toString());
            areas.add(ChouetteAreaEnum.QUAY.toString());
         }
      }

      AreaCentroid areaCentroid = searchCriteria.getAreaCentroid();

      Filter filter1 = null;
      if (searchCriteria.getName() != null && !searchCriteria.getName().isEmpty())
      {
         filter1 = Filter.getNewIgnoreCaseLikeFilter(StopArea.NAME, searchCriteria.getName());
      }
      Filter filter2 = null;
      if (areas.size() > 0)
      {
         filter2 = Filter.getNewInFilter(StopArea.AREA_TYPE, areas);
      }
      Filter filter3 = null;
      if (areaCentroid != null)
      {
         Address address = areaCentroid.getAddress();

         if (address != null)
         {
            filter3 = Filter.getNewIgnoreCaseLikeFilter(StopArea.AREACENTROID + "." + AreaCentroid.ADDRESS + "."
                  + Address.COUNTRY_CODE, address.getCountryCode());
         }
      }
      Filter filter = Filter.getNewAndFilter(filter1, filter2, filter3);
      positionGeographiquesResultat = stopAreaManager.getAll(null, filter);
      request.put("positionGeographiquesResultat", positionGeographiquesResultat);
      return SEARCH;
   }
   @SuppressWarnings("unchecked")
   @SkipValidation
   public String searchLineResults() throws ChouetteException
   {
      List<Line> linesResultat = new ArrayList<Line>();


      Filter filter1 = null;
      if (lineCriteria.getName() != null && !lineCriteria.getName().isEmpty())
      {
         filter1 = Filter.getNewIgnoreCaseLikeFilter(Line.NAME, lineCriteria.getName());
      }
      Filter filter2 = null;
      if (lineCriteria.getNumber() != null && !lineCriteria.getNumber().isEmpty())
      {
         filter2 = Filter.getNewIgnoreCaseLikeFilter(Line.NUMBER, lineCriteria.getNumber());
      }
      Filter filter3 = null;
      if (lineCriteria.getPtNetwork() != null && lineCriteria.getPtNetwork().getId() != null)
      {
         filter3 = Filter.getNewEqualsFilter(Line.PTNETWORK+"."+PTNetwork.ID, lineCriteria.getPtNetwork().getId());
      }
      Filter filter4 = null;
      if (lineCriteria.getCompany() != null && lineCriteria.getCompany().getId() != null)
      {
         filter4 = Filter.getNewEqualsFilter(Line.COMPANY+"."+Company.ID, lineCriteria.getCompany().getId());
      }
      
      Filter filter = Filter.getNewAndFilter(filter1,filter2,filter3,filter4);
      linesResultat = lineManager.getAll(null, filter);

      request.put("linesResultat", linesResultat);
      return SEARCH_LINE;
   }

   @SkipValidation
   public String removeChildFromParent() throws ChouetteException
   {
      if (idChild != null)
      {
         StopArea child = stopAreaManager.getById(idChild);
         StopArea parent = stopAreaManager.getById(idPositionGeographique);
         if (child != null)
         {
            parent.removeContainedStopArea(child);
            child.removeParent(parent);
            stopAreaManager.update(null, parent);
         }
      }
      return REDIRECTEDIT;
   }

   @SkipValidation
   public String addChild() throws ChouetteException
   {
      if (idChild != null && idPositionGeographique != null)
      {
         StopArea child = stopAreaManager.getById(idChild);
         StopArea parent = stopAreaManager.getById(idPositionGeographique);
         if (child != null)
         {
            parent.addContainedStopArea(child);
            stopAreaManager.update(null, parent);
         }

      }

      return REDIRECTEDIT;
   }

   @SkipValidation
   public String addLine() throws ChouetteException
   {
      if (idLine != null && idPositionGeographique != null)
      {
         Line line = lineManager.getById(idLine);
         StopArea parent = stopAreaManager.getById(idPositionGeographique);
         if (line != null)
         {
            parent.addRoutingConstraintLine(line);
            stopAreaManager.update(null, parent);
         }

      }

      return REDIRECTEDIT;
   }

   @SkipValidation
   public String addFather() throws ChouetteException
   {
      if (idFather != null && idPositionGeographique != null)
      {
         StopArea stopArea = stopAreaManager.getById(idPositionGeographique);
         StopArea father = stopAreaManager.getById(idFather);
         if (stopArea != null)
         {
            stopArea.addParent(father);
            stopAreaManager.update(null, stopArea);
         }
      }

      return REDIRECTEDIT;
   }

   /********************************************************
    * METHOD ACTION *
    ********************************************************/

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
    * TYPE *
    ********************************************************/
   public String getTypePositionGeographique()
   {

      if (model.getId() != null)
      {
         if (model.getAreaType() == ChouetteAreaEnum.QUAY || model.getAreaType() == ChouetteAreaEnum.BOARDINGPOSITION)
         {
            return ARRETPHYSIQUE;
         }
         else if (model.getAreaType() == ChouetteAreaEnum.COMMERCIALSTOPPOINT
               || model.getAreaType() == ChouetteAreaEnum.STOPPLACE)
         {
            return ZONE;
         }
         else
         {
            return ITL;
         }
      }
      else
      {
         // Récupération du namespace pour basculer sur des arrèts physiques ou
         // zones
         return ActionContext.getContext().getActionInvocation().getProxy().getNamespace();
      }
   }

   /********************************************************
    * Manage line association
    * 
    * @return
    * @throws ChouetteException
    *            *
    ********************************************************/
   @SkipValidation
   public String removeLineFromRoutingConstraint() throws ChouetteException
   {
      if (idLine != null)
      {
         StopArea routingConstraint = stopAreaManager.getById(idPositionGeographique);
         List<Line> lines = routingConstraint.getRoutingConstraintLines();
         for (Line line : lines)
         {
            if (line.getId().equals(idLine))
            {
               routingConstraint.removeRoutingConstraintLine(line);
               break;
            }
         }
         stopAreaManager.update(null, routingConstraint);

      }
      return REDIRECTEDIT;
   }

   /********************************************************
    * OTHER METHODS *
    ********************************************************/
   // public void setPage(int page)
   // {
   // this.page = page;
   // }

   public Long getIdZone()
   {
      return idPositionGeographique;
   }

   public String getLiaisonItineraire(Long idPhysique)
   {
      return presenceItineraireParIdPhysique.get(idPhysique).booleanValue() ? "hidden" : "visible";
   }

   public Line getLigne(Long idItineraire)
   {
      return ligneParIdItineraire.get(idItineraire);
   }

   public PTNetwork getReseau(Long idLigne)
   {
      return reseauParIdLigne.get(idLigne);
   }

   /********************************************************
    * AJAX AUTOCOMPLETE
    * 
    * @throws ChouetteException
    *            *
    ********************************************************/
   @SuppressWarnings("unchecked")
   @SkipValidation
   public String ajaxBoardingPositions() throws ChouetteException
   {
      // List<PositionGeographique> boardingPositions =
      // positionGeographiqueManager.lireArretsPhysiques();
      List<StopArea> boardingPositions = stopAreaManager.getAll(null, StopArea.physicalStopsFilter);

      // Filter boarding position with the name in request
      int count = 0;
      List<StopArea> boardingPositionsAfterFilter = new ArrayList<StopArea>();
      for (StopArea boardingPosition : boardingPositions)
      {
         String countryName = "", streetName = "";
         AreaCentroid areaCentroid = boardingPosition.getAreaCentroid();
         if (areaCentroid != null)
         {
            Address address = areaCentroid.getAddress();
            if (address != null)
            {
               countryName = address.getCountryCode();
               streetName = address.getStreetName();
            }
         }
         String name = boardingPosition.getName() + " " + countryName + " " + streetName + " "
               + boardingPosition.getObjectId();
         if (name.contains(boardingPositionName))
         {
            boardingPositionsAfterFilter.add(boardingPosition);
            count++;
            if (count >= 10)
               break;
         }
      }
      request.put("boardingPositions", boardingPositionsAfterFilter);
      return AUTOCOMPLETE;
   }

   public String getBoardingPositionName()
   {
      return boardingPositionName;
   }

   public void setBoardingPositionName(String boardingPositionName)
   {
      this.boardingPositionName = boardingPositionName;
   }
   
   public List<PTNetwork> getNetworks() throws ChouetteException
   {
      return networkManager.getAll(null);
   }
   public List<Company> getCompanies() throws ChouetteException
   {
      return companyManager.getAll(null);
   }
}
