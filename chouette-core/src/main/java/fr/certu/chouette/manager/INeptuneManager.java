/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */
package fr.certu.chouette.manager;

import java.util.List;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.filter.DetailLevelEnum;
import fr.certu.chouette.filter.Filter;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;
import fr.certu.chouette.model.user.User;
import fr.certu.chouette.plugin.exchange.FormatDescription;
import fr.certu.chouette.plugin.exchange.IExportPlugin;
import fr.certu.chouette.plugin.exchange.IImportPlugin;
import fr.certu.chouette.plugin.exchange.ParameterValue;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.ReportHolder;
import fr.certu.chouette.plugin.report.ReportItem;
import fr.certu.chouette.plugin.validation.IValidationPlugin;
import fr.certu.chouette.plugin.validation.ValidationStepDescription;

/**
 * the generic interface for every NeptuneBean manager
 */
public interface INeptuneManager <T extends NeptuneIdentifiedObject>
{

   
   // Create
   /**
    * Instantiate a new bean but don't save or attach it in storage
    * 
    * @param user user account for security check 
    * @return a new empty bean
    * @throws ChouetteException invalid user access 
    */
   public T getNewInstance(User user) throws ChouetteException;

   /**
    * insert a new bean in storage<br/>
    * the bean may include dependency beans, either existing or not<br/>
    * when the dependency bean exists, it may be updated (if needed)<br/>
    * in the other case, it will be added
    * 
    * @param user user account for security check 
    * @param bean the bean to insert and its dependencies
    * @return the bean inserted (may be updated with technical informations)
    * @throws ChouetteException invalid user access or potentially newly beans exist or break integrity constraints
    * 
    */
   public T addNew(User user,T bean)  throws ChouetteException;

   /**
    * check if a bean with attributes involved in unique constraints exists <br/>
    * only non empty attributes will be checked <br/>
    * if multiple unique constraints matched, true will be returned, 
    * even if different objects match these constraints
    * 
    * @param user user account for security check 
    * @param bean the bean to be checked
    * @return true (exists) or false (not exists) 
    * @throws ChouetteException invalid user access 
    */
   public boolean exists(User user, T  bean)  throws ChouetteException;


   // Read
   /**
    * find a bean on unique criteria<br/>
    * if more than one beans are eligible for the criteria, an Exception is thrown
    * 
    * @param user user account for security check 
    * @param filter parameters for search
    * @param level returned detail level
    * @return the bean found or null if not found
    * @throws ChouetteException invalid user access or not unicity criteria
    */
   public  T get(User user, Filter filter, DetailLevelEnum level) throws ChouetteException;

   /**
    * find a list of beans on a criteria (may be empty)
    * 
    * @param user user account for security check 
    * @param filter parameters for search
    * @param level returned detail level
    * @return the beans found 
    * @throws ChouetteException invalid user access or storage access problem
    */
   public  List<T> getAll(User user, Filter filter, DetailLevelEnum level)  throws ChouetteException;


   // Update 
   /**
    * update an existing bean <br/>
    * the bean may contain dependencies with new beans, in this case, the new beans will be added<br/>
    * in the other case, change in the dependency beans will be ignored
    * 
    * @param user user account for security check 
    * @param bean the bean to update
    * @throws ChouetteException invalid user access or constraints conflits or storage access problem 
    */
   public void update(User user,T bean)  throws ChouetteException;

   /**
    * update an existing bean <br/>
    * the bean may contain dependencies with new beans, in this case, the new beans will be added in cascade<br/>
    * in the other case, change in the dependency beans will be checked with level detail
    * 
    * @param user user account for security check 
    * @param bean the bean to update
    * @param level detail level to follow for updated dependencies
    * @throws ChouetteException invalid user access or constraints conflits or storage access problem 
    */
   public void update(User user,T bean,DetailLevelEnum level )  throws ChouetteException;


   // Delete
   /**
    * check if an object is eligible for deletion<br/>
    * an object can be deleted if its deletion don't break model constraints<br/>
    * strong dependencies won't be checked as they are deleted in cascade
    * 
    * @param user user account for security check 
    * @param bean the bean to delete
    * @return true if the bean is removable , false if not
    * @throws ChouetteException invalid user access or storage access problem 
    */
   public boolean isRemovable(User user,T bean)  throws ChouetteException;

   /**
    * delete a bean<BR/>
    * delete a bean and its children<br/>
    * delete also relational dependencies
    * 
    * @param user user account for security check 
    * @param bean the bean to delete
    * @throws ChouetteException invalid user access or constraints conflicts or storage access problem 
    */
   public void remove(User user,T bean)  throws ChouetteException;

   /**
    * remove beans on criteria<br/>
    * delete all beans found on criteria and their childrens<br/>
    * delete also relationals dependencies
    * 
    * @param user user account for security check 
    * @param filter the criteria for deletion
    * @throws ChouetteException invalid user access or constraints conflits or storage access problem 
    */
   public int removeAll(User user,Filter filter)  throws ChouetteException;

   // importation
   
   /**
    * get usage information for all existing import format
    * 
    * @param user user account for security check 
    * @return a list of import format description
    * @throws ChouetteException invalid user access
    */
   public List<FormatDescription> getImportFormats(User user) throws ChouetteException;
   
   /**
    * import beans from specified format<br/>
    * 
    * 
    * @param user user account for security check 
    * @param formatDescriptor name of the format found in ImportFormatDescription structure
    * @param parameters import parameter according to specified format
    * @param report import reporting container
    * @return a collection of imported beans (with all imported dependencies)
    * @throws ChouetteException invalid user access or import failure
    */
   public List<T> doImport(User user,String formatDescriptor,List<ParameterValue> parameters,ReportHolder report) throws ChouetteException;
   
   /**
    * save in storage the imported beans<br/>
    * new beans will be added<br/>
    * beans of main type will replace existing beans<br/>
    * existing beans in relation dependencies will be updated
    * 
    * @param user user account for security check 
    * @param beans a collection of beans to save
    * @throws ChouetteException invalid user access or constraints conflits or storage access problem 
    */
   public void saveAll(User user, List<T> beans) throws ChouetteException;
   
   /**
    * save in storage the imported beans<br/>
    * new beans will be added<br/>
    * all existing beans will be updated<br/>
    * new dependencies will be added but when multiple dependency is allowed, old dependencies wont be deleted
    * 
    * @param user user account for security check 
    * @param beans a collection of beans to save
    * @throws ChouetteException invalid user access or constraints conflicts or storage access problem 
    */
   public void saveOrUpdateAll(User user, List<T> beans) throws ChouetteException;
   
   // exportation
   /**
    * get usage information for all existing export format
    * 
    * @param user user account for security check 
    * @return a list of export format description
    * @throws ChouetteException invalid user access
    */
   public List<FormatDescription> getExportFormats(User user) throws ChouetteException;
   
   /**
    * export beans in specified format<br/>
    * 
    * 
    * @param user user account for security check 
    * @param beans beans to export
    * @param formatDescriptor name of the format found in ExportFormatDescription structure
    * @param parameters export parameter according to specified format
    * @param report export reporting container
    * @throws ChouetteException invalid user access or export failure
    */
   public void doExport(User user,List<T> beans, String formatDescriptor, List<ParameterValue> parameters,ReportHolder report) throws ChouetteException;
   
   /**
    * get usage information for all existing export format for deleted objects
    * 
    * @param user user account for security check 
    * @return a list of export format description
    * @throws ChouetteException invalid user access
    */
   public List<FormatDescription> getDeleteExportFormats(User user) throws ChouetteException;
   
   /**
    * export deletion informations for beans in specified format<br/>
    * 
    * 
    * @param user user account for security check 
    * @param beans beans to export
    * @param formatDescriptor name of the format found in ExportFormatDescription structure
    * @param parameters export parameter according to specified format
    * @param report export reporting container
    * @throws ChouetteException invalid user access or export failure
    */
   public void doExportDeleted(User user,List<T> beans, String formatDescriptor, List<ParameterValue> parameters,ReportHolder report) throws ChouetteException;
   
   // validation
   /**
    * validate a bean according to model rules
    * 
    * @param user user account for security check 
    * @param bean bean to be validate
    * @return a diagnostic step by step 
    * @throws ChouetteException invalid user access or validation failure
    */
   public Report validate(User user,T bean) throws ChouetteException;
   
   /**
    * get the steps description for validation
    * 
    * @param user user account for security check 
    * @return a step description 
    * @throws ChouetteException invalid user access
    */
   public List<ValidationStepDescription> getValidationSteps(User user) throws ChouetteException;
   
   /**
    * validate a bean according to one step of the model rules
    * 
    * @param user user account for security check 
    * @param bean bean to be validate 
    * @param stepDescriptor the step to execute
    * @return the step diagnostic 
    * @throws ChouetteException invalid user access or export failure
    */
   public ReportItem validateStep(User user, T bean, String stepDescriptor) throws ChouetteException;
   
   
	/**
	 * import plug'in injection
	 * 
	 * @param plugin
	 */
	public void addImportPlugin(IImportPlugin<T> plugin);

	/**
	 * export plug'in injection
	 * 
	 * @param plugin
	 */
	public void addExportPlugin(IExportPlugin<T> plugin);
	/**
	 * export plug'in injection
	 * 
	 * @param plugin
	 */
	public void addExportDeletionPlugin(IExportPlugin<T> plugin);
	/**
	 * validation plug'in injection
	 * 
	 * @param plugin
	 */
	public void addValidationPlugin(IValidationPlugin<T> plugin);

}
