package fr.certu.chouette.struts.connectionLink;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.interceptor.validation.SkipValidation;

import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Preparable;

import fr.certu.chouette.critere.AndClause;
import fr.certu.chouette.critere.ScalarClause;
import fr.certu.chouette.critere.VectorClause;
import fr.certu.chouette.modele.Correspondance;
import fr.certu.chouette.modele.PositionGeographique;
import fr.certu.chouette.service.commun.CodeIncident;
import fr.certu.chouette.service.commun.ServiceException;
import fr.certu.chouette.service.database.ICorrespondanceManager;
import fr.certu.chouette.service.database.IPositionGeographiqueManager;
import fr.certu.chouette.service.importateur.IImportCorrespondances;
import fr.certu.chouette.struts.GeneriqueAction;
import fr.certu.chouette.struts.enumeration.ObjetEnumere;

public class ConnectionLinkAction extends GeneriqueAction implements ModelDriven<Correspondance>, Preparable
{
	private static final long serialVersionUID = 6964959559153714259L;
	private static final Log log = LogFactory.getLog(ConnectionLinkAction.class);

	private static final String INPUT_SAVE = "input_save";
  
	// Model Linked
	private Correspondance correspondanceModel = new Correspondance();  
  
	// Managers and upload class
	private IImportCorrespondances importateurCorrespondances;  
	private ICorrespondanceManager correspondanceManager;  
	private IPositionGeographiqueManager positionGeographiqueManager;
  
	// Attributes linked to fields form  
	private String useHastus;
	private Long idCorrespondance;
	private PositionGeographique criteria;
	private PositionGeographique start;
	private PositionGeographique end;
	private String actionSuivante;
	private Long idPositionGeographique;
	private String durationsFormat = "mm:ss";  
 
	private String fichierContentType;
	private File fichier;

	private List<Correspondance> connectionLinks; 
  
	// Technical attribute 
	// Added parameter to avoid ognl exception
	private String operationMode = "NONE";  

	private static Map<String, String> ops = new HashMap<String, String>();
	static
	{
		ops.put("delete", "STORE");	  
		ops.put("editCombinedActions", "STORE");//save,update,cancel
		ops.put("upload", "STORE");
		ops.put("doSearch", "STORE");
	}	

/********************************************************
   *                  MODEL + PREPARE                     *
   ********************************************************/
	public Correspondance getModel()
	{    
		return correspondanceModel;
	}

	public void prepare() throws Exception
	{
		this.correspondanceManager = this.importateurCorrespondances.getCorrespondanceManager();
		if (getIdCorrespondance() == null)
		{
			log.debug("Prepare with null id");
			correspondanceModel = new Correspondance();
			try 
			{
			  // Initialisation for list action
			  connectionLinks = correspondanceManager.lire();
			}
			catch(Exception e)
			{
				log.error("Unread ConnectionLinks : correspondanceManager.lire() failed, exception : " + e.getMessage());
			}
		}	
		else
		{
			log.debug("Prepare with id : " + getIdCorrespondance());
			try 
			{
				this.initStartEndStopAreas();
			}
			catch(Exception e)
			{
				log.error("initStartEndStopAreas failed : " + e.getMessage());
			}
		}
		log.debug("prepare ended");
	}

  /********************************************************
   *                  MAIN METHODS                    *
   ********************************************************/
  /**
   * Return connectionsLinks list 
   * @return List<Correspondance>
   */
	private List<Correspondance> getConnectionsLinks()
	{
		List<Correspondance> connectionsLinks = null;
		try 
		{
			connectionsLinks = correspondanceManager.lire();
		}
		catch(Exception e)
		{
			log.error("Unread ConnectionLinks : correspondanceManager.lire() failed, exception : " + e.getMessage());
		}
		return connectionsLinks;
	}
  
  /**
   * Init connection link departure (start) and arrival (end) 
   */
  private void initStartEndStopAreas()
  {	  
	  correspondanceModel = correspondanceManager.lire(getIdCorrespondance());
	  // This case could occurs if user 2nd step creation is bypass, as
	  // start end constraints aren't checked
	  if (correspondanceModel.getIdDepart() != null)
	  {
		  this.start = positionGeographiqueManager.lire(correspondanceModel.getIdDepart());
	  }
	  if (correspondanceModel.getIdArrivee() != null)
	  {
		  this.end = positionGeographiqueManager.lire(correspondanceModel.getIdArrivee());
	  }
  }
  
  
  /**
   * Connection Links Import
   * @return String result REDIRECTLIST
   */
  @SkipValidation
  public String upload()
  {
	  log.debug("importConnectionLinks");
	  
	  // Validate File path
	  String canonicalPath = null;
	  try 
	  {
		  if (null == fichier)
		  {
			  throw new IOException("null file");
		  }
		  canonicalPath = fichier.getCanonicalPath();
	  }
	  catch (Exception e) 
	  {
		  log.debug("unvalid path file");
		  addFieldError("fichier", "unvalid.path.file");
		  if (null != e.getMessage())
		  {
			  log.debug("unvalid path file, " + e.getMessage());
		  }
		
		  if (null == this.connectionLinks)
		  {
			  addActionError("Unread ConnectionLinks : correspondanceManager.lire() failed");
		  }
		  return REDIRECTLIST;
	  }
	  
	  // Connection links importation
	  try 
	  {
		  List<String> messages = importateurCorrespondances.lire(canonicalPath);
		  if (messages != null)
		  {
			  // same error on several connectionlinks, retreive duplicates			  
			  Map<String, String> duplicates = new HashMap<String, String>();
			  if (messages.size() > 0)
			  {				  
				  for (String errMsg : messages)
				  {
						if (! duplicates.containsKey(errMsg))
						{
							duplicates.put(errMsg, null);
							log.debug(errMsg);
							addActionError(errMsg);
						}
				  }				  
			  }
			  else
			  {
				  String errMsg = "Unread ConnectionLinks : importateurCorrespondances.lire(canonicalPath) failed without messages";
				  log.debug(errMsg);
				  addActionError(errMsg);
			  }
		  }
		  else
		  {
			  addActionMessage(getText("import.csv.format.ok"));
		  }		  		  
		}
		catch (ServiceException e)
		{
			String errMsg = "";
			if (CodeIncident.ERR_CSV_NON_TROUVE.equals(e.getCode())) 
			{
				errMsg = getText("import.csv.fichier.introuvable");
				addFieldError("fichier", errMsg);
			}
			else
			{			
				errMsg = getText("import.csv.format.ko");
				addActionError(errMsg);
			}			
			errMsg += e.getMessage();
			log.debug(errMsg);
		}
		
		this.connectionLinks = getConnectionsLinks();
		if (null == connectionLinks)
		{
			addActionError("Unread ConnectionLinks : correspondanceManager.lire() failed");
		}
		return REDIRECTLIST;
  }
  /********************************************************
   *                           CRUD                       *
   ********************************************************/
  
  @SkipValidation
  public String list()
  {	  
	  this.connectionLinks = getConnectionsLinks();
	  if (null == connectionLinks)
	  {
		  addActionError("Unread ConnectionLinks : correspondanceManager.lire() failed");
		  return INPUT;
	  }
	  return LIST;
  }
  
  @SkipValidation
  public String add()
  {
    return EDIT;
  }
    
  public String save()
  {	  	  
	  try
	  {		 
		  correspondanceManager.creer(getModel());		  		 
		  addActionMessage(getText ("connectionlink.create.ok"));
	  }
	  catch (Exception exception)
	  {
		  addActionError(getText("connectionlink.create.ko"));
		  log.error("ConnectionLink creation failed with message : " + exception.getMessage());
		  return INPUT;
	  }
	  this.setIdCorrespondance(correspondanceModel.getId());
	  return REDIRECTEDIT;
  }

  //@Action(value="edit", interceptorRefs={@InterceptorRef(value="store",params={"operationMode", "RETRIEVE"})})
  @SkipValidation
  public String edit()
  {
	  return EDIT;
  }

  
  // TODO : why doesn't work
  //@Action(value="update", interceptorRefs={@InterceptorRef(value="store",params={"operationMode", "STORE"})})
  public String update()
  {
	  log.debug("Update connectionLink with id : " + getModel().getId());
	  try
	  {
		  correspondanceManager.modifier(getModel());
		  String msg = getText("connectionlink.update.ok");
		  log.debug(msg);
		  addActionMessage(msg);
		  return REDIRECTLIST;
	  }
	  catch (Exception e)
	  {
		  String errMsg = getText("connectionlink.update.ko");
		  errMsg += e.getMessage();
		  log.debug(errMsg);
		  addActionError(errMsg);
    	
		  return REDIRECTEDIT;
	  }    
  }

  public String delete()
  {
	log.debug("Delete connectionLink with id : " + getModel().getId());
	correspondanceManager.supprimer(getModel().getId());
    addActionMessage(getText("connectionlink.delete.ok"));    
    
    return REDIRECTLIST;
  }

  @SkipValidation
  public String cancel()
  {
	addActionMessage(getText("connectionlink.cancel.ok"));    
    return REDIRECTLIST;
  }

  @Override
  @SkipValidation
  public String input() throws Exception
  {
    return INPUT;
  }

  @SkipValidation
  public String search()
  {
    return SEARCH;
  }

  @SkipValidation
  public String doSearch()
  {
	  Collection<String> areas = new HashSet<String>();
	  if (criteria.getAreaType() != null)
	  {
		  log.debug("*** EZ selected criteria areatype : " + criteria.getAreaType());
		  areas.add(criteria.getAreaType().toString());
	  }
	  else
	  {
		  try 
		  {
			  //List<ObjetEnumere> areaEnumerations = fr.certu.chouette.struts.enumeration.EnumerationApplication.getArretPhysiqueAreaTypeEnum();
			  //areaEnumerations.addAll(fr.certu.chouette.struts.enumeration.EnumerationApplication.getZoneAreaTypeEnum());
			  List<ObjetEnumere> areaEnumerations = getStopAreaEnum("");
			  for (ObjetEnumere enumeration : areaEnumerations)
			  {
				  areas.add(enumeration.getEnumeratedTypeAccess().toString());
			  }
		  }
		  catch (Exception e) 
		  {
			  String msg = "search action unvailable, and by relation start end added unvailable too";
			  log.debug(msg);
			  addActionError(msg);
			  
			  if (e.getMessage() != null)
			  {
				  msg = "Exception code and message : " + e.getMessage();
				  log.debug(msg);
			  }
			  if (e.getCause() != null)
			  {
				  msg = "Exception cause : " + e.getCause();
				  log.debug(msg);  
			  }
			  return "error";
		  }
	  }
	  if ("".equals(criteria.getName()))
	  {
		  criteria.setName(null);
	  }
	  if ("".equals(criteria.getCountryCode()))
	  {
		  criteria.setCountryCode(null);
	  }
	  List<PositionGeographique> positionGeographiquesResultat = positionGeographiqueManager.select(new AndClause().add(ScalarClause.newIlikeClause("name", criteria.getName())).
            add(ScalarClause.newIlikeClause("countryCode", criteria.getCountryCode())).
            add(VectorClause.newInClause("areaType", areas)));

	  request.put("positionGeographiquesResultat", positionGeographiquesResultat);
	  return SEARCH;
  }

  @SkipValidation
  public String cancelSearch()
  {
    return REDIRECTEDIT;
  }


  @SkipValidation
  public String addStart()
  {
    if (idPositionGeographique != null && idCorrespondance != null)
    {
      correspondanceModel = correspondanceManager.lire(idCorrespondance);
      correspondanceModel.setIdDepart(idPositionGeographique);
      correspondanceManager.modifier(correspondanceModel);
    }
    return REDIRECTEDIT;
  }

  @SkipValidation
  public String addEnd()
  {
    if (idPositionGeographique != null && idCorrespondance != null)
    {
      correspondanceModel = correspondanceManager.lire(idCorrespondance);
      correspondanceModel.setIdArrivee(idPositionGeographique);
      correspondanceManager.modifier(correspondanceModel);
    }
    return REDIRECTEDIT;
  }

  /********************************************************
   *                        INIT                          *
   ********************************************************/
  public void setUseHastus(String useHastus)
  {
    this.useHastus = useHastus;
  }

  public String getUseHastus()
  {
    return useHastus;
  }

  /********************************************************
   *                        MANAGER ET IMPORATEUR                     *
   ********************************************************/
  public void setPositionGeographiqueManager(IPositionGeographiqueManager positionGeographiqueManager)
  {
    this.positionGeographiqueManager = positionGeographiqueManager;
  }

  public void setCorrespondanceManager(ICorrespondanceManager correspondanceManager)
  {
    this.correspondanceManager = correspondanceManager;
  }

  public IImportCorrespondances getImportateurCorrespondances() 
  {
	return importateurCorrespondances;
  }

  public void setImportateurCorrespondances(
		IImportCorrespondances importateurCorrespondances) 
  {
	this.importateurCorrespondances = importateurCorrespondances;
  }

  /********************************************************
   *                   FILTER                             *
   ********************************************************/
  public PositionGeographique getCriteria()
  {
    return criteria;
  }

  public void setCriteria(PositionGeographique criteria)
  {
    this.criteria = criteria;
  }

  /********************************************************
   *                   TIME FORMAT                        *
   ********************************************************/
  public void setStrutsOccasionalTravellerDuration(String s)
  {
    SimpleDateFormat sdfHoraire = new SimpleDateFormat(durationsFormat);
    if (s != null && s.length() > 0)
    {
      try
      {
        Date d = sdfHoraire.parse(s);
        correspondanceModel.setOccasionalTravellerDuration(d);
      }
      catch (Exception ex)
      {
        addActionError(ex.getLocalizedMessage());
      }
    }
    else
    {
      correspondanceModel.setOccasionalTravellerDuration(null);
    }
  }

  public String getStrutsOccasionalTravellerDuration()
  {
    if (correspondanceModel != null && correspondanceModel.getOccasionalTravellerDuration() != null)
    {
      Date d = correspondanceModel.getOccasionalTravellerDuration();
      SimpleDateFormat sdfHoraire = new SimpleDateFormat(durationsFormat);
      return sdfHoraire.format(d);
    }
    else
    {
      return null;
    }
  }

  public void setStrutsMobilityRestrictedTravellerDuration(String s)
  {
    SimpleDateFormat sdfHoraire = new SimpleDateFormat(durationsFormat);
    if (s != null && s.length() > 0)
    {
      try
      {
        Date d = sdfHoraire.parse(s);
        correspondanceModel.setMobilityRestrictedTravellerDuration(d);
      }
      catch (Exception ex)
      {
        addActionError(ex.getLocalizedMessage());
      }
    }
    else
    {
      correspondanceModel.setMobilityRestrictedTravellerDuration(null);
    }
  }

  public String getStrutsMobilityRestrictedTravellerDuration()
  {
    if (correspondanceModel != null && correspondanceModel.getMobilityRestrictedTravellerDuration() != null)
    {
      Date d = correspondanceModel.getMobilityRestrictedTravellerDuration();
      SimpleDateFormat sdfHoraire = new SimpleDateFormat(durationsFormat);
      return sdfHoraire.format(d);
    }
    else
    {
      return null;
    }
  }

  public void setStrutsFrequentTravellerDuration(String s)
  {
    SimpleDateFormat sdfHoraire = new SimpleDateFormat(durationsFormat);
    if (s != null && s.length() > 0)
    {
      try
      {
        Date d = sdfHoraire.parse(s);
        correspondanceModel.setFrequentTravellerDuration(d);
      }
      catch (Exception ex)
      {
        addActionError(ex.getLocalizedMessage());
      }
    }
    else
    {
      correspondanceModel.setFrequentTravellerDuration(null);
    }
  }

  public String getStrutsFrequentTravellerDuration()
  {
    if (correspondanceModel != null && correspondanceModel.getFrequentTravellerDuration() != null)
    {
      Date d = correspondanceModel.getFrequentTravellerDuration();
      SimpleDateFormat sdfHoraire = new SimpleDateFormat(durationsFormat);
      return sdfHoraire.format(d);
    }
    else
    {
      return null;
    }
  }

  public void setStrutsDefaultDuration(String s)
  {
    SimpleDateFormat sdfHoraire = new SimpleDateFormat(durationsFormat);
    if (s != null && s.length() > 0)
    {
      try
      {
        Date d = sdfHoraire.parse(s);
        correspondanceModel.setDefaultDuration(d);
      }
      catch (Exception ex)
      {
        addActionError(ex.getLocalizedMessage());
      }
    }
    else
    {
      correspondanceModel.setDefaultDuration(null);
    }
  }

  public String getStrutsDefaultDuration()
  {
    if (correspondanceModel != null && correspondanceModel.getDefaultDuration() != null)
    {
      Date d = correspondanceModel.getDefaultDuration();
      SimpleDateFormat sdfHoraire = new SimpleDateFormat(durationsFormat);
      return sdfHoraire.format(d);
    }
    else
    {
      return null;
    }
  }

  public PositionGeographique getStart()
  {
    return start;
  }

  public void setStart(PositionGeographique start)
  {
    this.start = start;
  }

  public PositionGeographique getEnd()
  {
    return end;
  }

  public void setEnd(PositionGeographique end)
  {
    this.end = end;
  }

  public String getActionSuivante()
  {
    return actionSuivante;
  }

  public void setActionSuivante(String actionSuivante)
  {
    this.actionSuivante = actionSuivante;
  }
  
  public String getFichierContentType() 
  {
	return fichierContentType;
  }

  public void setFichierContentType(String fichierContentType) 
  {
	this.fichierContentType = fichierContentType;
  }

  public File getFichier() 
  {
	return fichier;
  }

  public void setFichier(File fichier) 
  {
	this.fichier = fichier;
  }

  public Long getIdCorrespondance()
  {
    return idCorrespondance;
  }

  public void setIdCorrespondance(Long idCorrespondance)
  {
    this.idCorrespondance = idCorrespondance;
  }

  public Long getIdPositionGeographique()
  {
    return idPositionGeographique;
  }

  public void setIdPositionGeographique(Long idPositionGeographique)
  {
    this.idPositionGeographique = idPositionGeographique;
  }
  
  public Map<String,String>getOps() 
  {
	  return ops;
  }
  
  public void setOps(Map<String,String> ops) 
  {
	  this.ops = ops;
  }

  public String getOperationMode() 
  {
	return operationMode;
  }

  public void setOperationMode(String operationMode) 
  {
	  this.operationMode = operationMode;
  }
  
  public void setConnectionLinks(List<Correspondance> connectionLinks) 
  {
	this.connectionLinks = connectionLinks;
  }

  public List<Correspondance> getConnectionLinks() 
  {
	  return connectionLinks;
  }
}