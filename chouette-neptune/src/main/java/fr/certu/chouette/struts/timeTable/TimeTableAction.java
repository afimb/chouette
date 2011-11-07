package fr.certu.chouette.struts.timeTable;

import java.lang.reflect.InvocationTargetException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.interceptor.validation.SkipValidation;

import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Preparable;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.filter.Filter;
import fr.certu.chouette.manager.INeptuneManager;
import fr.certu.chouette.model.neptune.PTNetwork;
import fr.certu.chouette.model.neptune.Period;
import fr.certu.chouette.model.neptune.Timetable;
import fr.certu.chouette.model.neptune.type.DayTypeEnum;
import fr.certu.chouette.model.user.User;
import fr.certu.chouette.struts.GeneriqueAction;
import fr.certu.chouette.struts.converter.JourTypeTMConverter;

@SuppressWarnings({ "unchecked", "serial" })
public class TimeTableAction extends GeneriqueAction implements Preparable, ModelDriven<Timetable>
{

   private static final Log           log                = LogFactory.getLog(TimeTableAction.class);
   @Getter
   @Setter
   private INeptuneManager<Timetable> timetableManager;
   @Getter
   @Setter
   private INeptuneManager<PTNetwork> networkManager;
   private Long                       idTableauMarche;
   private Date                       jour;
   private Date                       debut;
   private Date                       fin;
   private Integer                    idxDate;
   private Integer                    idxPeriod;
   private List<DayTypeEnum>          joursTypes;
   private List<PTNetwork>            reseaux;
   private String                     commentaire        = null;
   private Long                       idReseau           = null;
   private Date                       dateDebutPeriode   = null;
   private Date                       dateFinPeriode     = null;
   private Timetable                  tableauMarcheModel = new Timetable();
   private String                     mappedRequest;
   private User                       user;

   public Long getIdTableauMarche()
   {
      return idTableauMarche;
   }

   public void setIdTableauMarche(Long idTableauMarche)
   {
      this.idTableauMarche = idTableauMarche;
   }

   /********************************************************
    * MODEL + PREPARE *
    ********************************************************/
   public Timetable getModel()
   {
      return tableauMarcheModel;
   }

   public void prepare() throws Exception
   {
      log.debug("Prepare with id : " + getIdTableauMarche());
      if (getIdTableauMarche() == null)
      {
         tableauMarcheModel = new Timetable();
      }
      else
      {
         tableauMarcheModel = timetableManager.get(null, Filter.getNewEqualsFilter("id", idTableauMarche));
      }

      // Chargement des réseaux
      reseaux = networkManager.getAll(null);

      // Création de la liste des types de jours
      joursTypes = JourTypeTMConverter.getProperties(tableauMarcheModel);
   }

   /********************************************************
    * CRUD
    * 
    * @throws ChouetteException
    * @throws InvocationTargetException
    * @throws IllegalAccessException
    *            *
    ********************************************************/

   @SkipValidation
   public String list() throws ChouetteException, IllegalAccessException, InvocationTargetException
   {
      if ("".equals(commentaire))
      {
         commentaire = null;
      }
      if ("".equals(dateDebutPeriode))
      {
         dateDebutPeriode = null;
      }
      if ("".equals(dateFinPeriode))
      {
         dateFinPeriode = null;
      }

      List<Timetable> timetables = new ArrayList<Timetable>();
      Filter filter = Filter.getNewIgnoreCaseLikeFilter(Timetable.COMMENT, commentaire);

      List<Timetable> timetablesAll = timetableManager.getAll(user, filter);

      if (dateDebutPeriode == null && dateFinPeriode == null)
      {
         timetables.addAll(timetablesAll);
      }
      else
      {
         if (dateDebutPeriode != null | dateFinPeriode != null)
         {
            for (Timetable timetable : timetablesAll)
            {
               boolean isValid = false;

               if (timetable.getPeriods() != null)
               {
                  for (Period period : timetable.getPeriods())
                  {
                     if (period == null)
                        continue;

                     if ((dateFinPeriode == null || period.getStartDate().equals(dateFinPeriode) || period
                           .getStartDate().before(dateFinPeriode))
                           &&

                           (dateDebutPeriode == null || period.getEndDate().equals(dateDebutPeriode) || period
                                 .getEndDate().after(dateDebutPeriode)))
                     {
                        isValid = true;
                        break;
                     }

                  }
               }
               if (!isValid && timetable.getCalendarDays() != null)
               {
                  for (Date date : timetable.getCalendarDays())
                  {
                     if (date == null)
                        continue;
                     if ((dateFinPeriode == null || date.equals(dateFinPeriode) || date.before(dateFinPeriode)) &&

                     (dateDebutPeriode == null || date.equals(dateDebutPeriode) || date.after(dateDebutPeriode)))
                     {
                        isValid = true;
                        break;
                     }
                  }
               }

               if (isValid)
               {
                  timetables.add(timetable);
               }

            }
         }

      }

      this.request.put("tableauxMarche", timetables);
      log.debug("List of tableauMarche");
      return LIST;
   }

   @SkipValidation
   public String add()
   {
      setMappedRequest(SAVE);
      return EDIT;
   }

   public String save() throws ChouetteException
   {
      timetableManager.addNew(null, tableauMarcheModel);

      setMappedRequest(UPDATE);
      String[] args = new String[1];
      args[0] = tableauMarcheModel.getObjectId();
      addActionMessage(getText("tableauMarche.create.ok", args));
      // Update timetable id to update timetable
      setIdTableauMarche(tableauMarcheModel.getId());
      log.debug("Create tableauMarche with id : " + tableauMarcheModel.getId());
      return REDIRECTEDIT;
   }

   @SkipValidation
   public String edit()
   {
      setMappedRequest(UPDATE);
      return EDIT;
   }

   public String update() throws ChouetteException
   {
      JourTypeTMConverter.setDayTypes(tableauMarcheModel, joursTypes);

      timetableManager.update(null, tableauMarcheModel);
      setMappedRequest(UPDATE);
      String[] args = new String[1];
      args[0] = tableauMarcheModel.getObjectId();
      addActionMessage(getText("tableauMarche.update.ok", args));
      log.debug("Update tableauMarche with id : " + tableauMarcheModel.getId());
      return REDIRECTEDIT;
   }

   @SuppressWarnings("rawtypes")
   public String delete() throws ChouetteException
   {
      timetableManager.remove(null, tableauMarcheModel, false);

      ArrayList args = new ArrayList();
      args.add(tableauMarcheModel.getObjectId());
      addActionMessage(getText("tableauMarche.delete.ok", args));
      log.debug("Delete tableauMarche with id : " + tableauMarcheModel.getId());

      return REDIRECTLIST;
   }

   @SkipValidation
   public String cancel()
   {
      addActionMessage(getText("tableauMarche.cancel.ok"));
      return REDIRECTLIST;
   }

   @Override
   @SkipValidation
   public String input() throws Exception
   {
      return INPUT;
   }

   public String addPeriode() throws ChouetteException
   {
      if (debut != null && fin != null)
      {
         Period p = new Period();

         p.setStartDate(debut);
         p.setEndDate(fin);
         tableauMarcheModel.addPeriod(p);
         debut = null;
         fin = null;

         if (tableauMarcheModel.getId() == null)
         {
            timetableManager.addNew(null, tableauMarcheModel);
            addActionMessage(getText("tableauMarche.addperiod.ok"));
         }
         else
         {
            timetableManager.update(null, tableauMarcheModel);
            addActionMessage(getText("tableauMarche.addperiod.ok"));
         }
      }
      return REDIRECTEDIT;
   }

   public String deletePeriod() throws ChouetteException
   {
      if (this.idxPeriod != null)
      {
         Period p = tableauMarcheModel.getPeriods().get(idxPeriod.intValue() - 1);
         tableauMarcheModel.removePeriod(p);
         idxPeriod = null;
      }
      if (tableauMarcheModel.getId() == null)
      {
         timetableManager.addNew(null, tableauMarcheModel);
         addActionMessage(getText("tableauMarche.deleteperiod.ok"));
      }
      else
      {
         timetableManager.update(null, tableauMarcheModel);
         addActionMessage(getText("tableauMarche.deleteperiod.ok"));
      }

      return REDIRECTEDIT;
   }

   public String addDate() throws ChouetteException
   {
      if (jour != null)
      {
         tableauMarcheModel.addCalendarDay(jour);
         jour = null;
      }

      if (tableauMarcheModel.getId() == null)
      {
         timetableManager.addNew(null, tableauMarcheModel);
         addActionMessage(getText("tableauMarche.addcalendarday.ok"));
      }
      else
      {
         timetableManager.update(null, tableauMarcheModel);
         addActionMessage(getText("tableauMarche.addcalendarday.ok"));
      }

      return REDIRECTEDIT;
   }

   public String deleteDate() throws ChouetteException
   {
      if (this.idxDate != null)
      {
         Date d = tableauMarcheModel.getCalendarDays().get(idxDate.intValue() - 1);
         tableauMarcheModel.removeCalendarDay((java.sql.Date) d);
         idxDate = null;
      }
      if (tableauMarcheModel.getId() == null)
      {
         timetableManager.addNew(null, tableauMarcheModel);
         addActionMessage(getText("tableauMarche.deletecalendarday.ok"));
      }
      else
      {
         timetableManager.update(null, tableauMarcheModel);
         addActionMessage(getText("tableauMarche.deletecalendarday.ok"));
      }

      return REDIRECTEDIT;
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
    * FILTER *
    ********************************************************/
   public Long getIdReseau()
   {
      return idReseau;
   }

   public void setIdReseau(Long idReseau)
   {
      this.idReseau = idReseau;
   }

   public String getCommentaire()
   {
      return commentaire;
   }

   public void setCommentaire(String commentaire)
   {
      this.commentaire = commentaire;
   }

   public Date getDateDebutPeriode()
   {
      return dateDebutPeriode;
   }

   public void setDateDebutPeriode(Date dateDebutPeriode)
   {
      this.dateDebutPeriode = dateDebutPeriode;
   }

   public Date getDateFinPeriode()
   {
      return dateFinPeriode;
   }

   public void setDateFinPeriode(Date dateFinPeriode)
   {
      this.dateFinPeriode = dateFinPeriode;
   }

   /********************************************************
    * METHODS *
    ********************************************************/
   public Date getJour()
   {
      return jour;
   }

   public void setJour(Date jour)
   {
      this.jour = jour;
   }

   public Date getDebut()
   {
      return debut;
   }

   public void setDebut(Date debut)
   {
      this.debut = debut;
   }

   public Date getFin()
   {
      return fin;
   }

   public void setFin(Date fin)
   {
      this.fin = fin;
   }

   public Integer getIdxDate()
   {
      return idxDate;
   }

   public void setIdxDate(Integer idxDate)
   {
      this.idxDate = idxDate;
   }

   public Integer getIdxPeriod()
   {
      return idxPeriod;
   }

   public void setIdxPeriod(Integer idxPeriod)
   {
      this.idxPeriod = idxPeriod;
   }

   public List<DayTypeEnum> getJoursTypes()
   {
      return JourTypeTMConverter.getProperties(tableauMarcheModel);
   }

   public void setJoursTypes(List<DayTypeEnum> joursTypes)
   {
      this.joursTypes = joursTypes;
   }

   public List<PTNetwork> getReseaux()
   {
      return reseaux;
   }

   public void setReseaux(List<PTNetwork> reseaux)
   {
      this.reseaux = reseaux;
   }
}
