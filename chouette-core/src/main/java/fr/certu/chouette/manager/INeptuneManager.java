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
import fr.certu.chouette.core.CoreException;
import fr.certu.chouette.dao.IDaoTemplate;
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
import fr.certu.chouette.plugin.validation.ValidationParameters;
import fr.certu.chouette.plugin.validation.ValidationStepDescription;

/**
 * the generic interface for every NeptuneBean manager
 */
public interface INeptuneManager <T extends NeptuneIdentifiedObject>
{
	// management
	/**
	 * add DAO management to manager
	 * 
	 * @param dao an IDaoTemplate implementation
	 */
	void setDao(IDaoTemplate<T> dao);

	/**
	 * add DAO management to manager
	 * 
	 * @param dao an IDaoTemplate implementation
	 */
	void setJdbcDao(IDaoTemplate<T> dao);

	// Create
	/**
	 * Instantiate a new bean but don't save or attach it in storage
	 * 
	 * @param user user account for security check 
	 * @return a new empty bean
	 * @throws ChouetteException invalid user access 
	 */
	T getNewInstance(User user) throws ChouetteException;

	/**
	 * set an object id for a bean without one
	 * 
	 * @param user user account for security check 
	 * @param bean the bean to update
	 * @param prefix prefix for object id (null for default value)
	 * 
	 * @throws ChouetteException
	 */
	void setObjectId(User user,T bean,String prefix) throws ChouetteException;
	
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
	T addNew(User user,T bean)  throws ChouetteException;

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
	boolean exists(User user, T  bean)  throws ChouetteException;


	// Read
	/**
	 * find a bean on unique criteria<br/>
	 * if more than one beans are eligible for the criteria, an Exception is thrown
	 * 
	 * @param user user account for security check 
	 * @param filter parameters for search
	 * @return the bean found or null if not found
	 * @throws ChouetteException invalid user access or not unicity criteria
	 */
	T get(User user, Filter filter) throws ChouetteException;

	/**
	 * find a list of beans on a criteria (may be empty)
	 * 
	 * @param user user account for security check 
	 * @param filter parameters for search
	 * @return the beans found 
	 * @throws ChouetteException invalid user access or storage access problem
	 */
	List<T> getAll(User user, Filter filter)  throws ChouetteException;

	/**
	 * return all Neptune Identified object 
	 * @param user
	 * @return
	 * @throws ChouetteException
	 */
	List<T> getAll(User user) throws ChouetteException;
	
	/**
	 * return object by his objectId
	 * 
	 * @param objectId
	 * @return
	 * @throws ChouetteException
	 */
	T getByObjectId(String objectId) throws ChouetteException;
	
	/**
	 * return object by his id
	 * 
	 * @param id
	 * @return
	 * @throws ChouetteException
	 */
	T getById(Long id) throws ChouetteException;
	
    /**
     * count object which respond to clause (may be null)
     * @param user
     * @param clause clause (may be null)
     * @return
     * @throws ChouetteException
     */
    long count(User user, Filter clause) throws ChouetteException;


	// Update 
	/**
	 * update an existing bean <br/>
	 * the bean may contain dependencies with new beans, in this case, the new beans will be added<br/>
	 * in the other case, change in the dependency beans will be also saved 
	 * 
	 * @param user user account for security check 
	 * @param bean the bean to update
	 * @throws ChouetteException invalid user access or constraints conflits or storage access problem 
	 */
	void update(User user,T bean)  throws ChouetteException;

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
	boolean isRemovable(User user,T bean)  throws ChouetteException;

	/**
	 * delete a bean<BR/>
	 * delete a bean and its children<br/>
	 * delete also relational dependencies
	 * 
	 * @param user user account for security check 
	 * @param bean the bean to delete
	 * @throws ChouetteException invalid user access or constraints conflicts or storage access problem 
	 */
	void remove(User user,T bean,boolean propagate)  throws ChouetteException;

	/**
	 * remove beans on criteria<br/>
	 * delete all beans found on criteria and their childrens<br/>
	 * delete also relationals dependencies
	 * 
	 * @param user user account for security check 
	 * @param filter the criteria for deletion
	 * @throws ChouetteException invalid user access or constraints conflits or storage access problem 
	 */
	int removeAll(User user,Filter filter)  throws ChouetteException;

	/**
	 * remove a list of beans 
	 * @param user
	 * @param objects
	 * @param propagate
	 * @throws ChouetteException
	 */
	void removeAll(User user,List<T> objects, boolean propagate)  throws ChouetteException;

    /**
     * purge incomplete beans 
     * 
     * @param user
     * @return
     * @throws ChouetteException
     */
    int purge(User user) throws ChouetteException;

	// importation

	/**
	 * get usage information for all existing import format
	 * 
	 * @param user user account for security check 
	 * @return a list of import format description
	 * @throws ChouetteException invalid user access
	 */
	List<FormatDescription> getImportFormats(User user) throws ChouetteException;

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
	List<T> doImport(User user,String formatDescriptor,List<ParameterValue> parameters,ReportHolder report) throws ChouetteException;

	/**
	 * save in storage the imported beans<br/>
	 * new beans will be added<br/>
	 * beans of main type will replace existing beans<br/>
	 * existing beans in relation dependencies will be updated
	 * 
	 * @param user user account for security check 
	 * @param beans a collection of beans to save
	 * @param propagate indicate if sub objects should be saved automatically 
	 * @throws ChouetteException invalid user access or constraints conflits or storage access problem 
	 */
	//void saveAll(User user, List<T> beans,boolean propagate) throws ChouetteException;

	
	/**
	 * 
	 * @param object to be saved
	 * @param propagate indicate if sub objects should be saved automatically
	 * @throws CoreException
	 */
	void save(User user,T object,boolean propagate) throws ChouetteException;
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
	void saveOrUpdateAll(User user, List<T> beans) throws ChouetteException;

	// exportation
	/**
	 * get usage information for all existing export format
	 * 
	 * @param user user account for security check 
	 * @return a list of export format description
	 * @throws ChouetteException invalid user access
	 */
	List<FormatDescription> getExportFormats(User user) throws ChouetteException;

	/**
	 * export preloaded beans in specified format<br/>
	 * 
	 * 
	 * @param user user account for security check 
	 * @param beans beans to export
	 * @param formatDescriptor name of the format found in ExportFormatDescription structure
	 * @param parameters export parameter according to specified format
	 * @param report export reporting container
	 * @throws ChouetteException invalid user access or export failure
	 */
	void doExport(User user,List<T> beans, String formatDescriptor, List<ParameterValue> parameters,ReportHolder report) throws ChouetteException;

	/**
	 * export beans from database in specified format<br/>
	 * 
	 * 
	 * @param user user account for security check 
	 * @param beanIds ids of beans to export
	 * @param formatDescriptor name of the format found in ExportFormatDescription structure
	 * @param parameters export parameter according to specified format
	 * @param report export reporting container
	 * @throws ChouetteException invalid user access or export failure
	 */
	void doExportFromDatabase(User user,List<Long> beanIds, String formatDescriptor, List<ParameterValue> parameters,ReportHolder report) throws ChouetteException;

	/**
	 * get usage information for all existing export format for deleted objects
	 * 
	 * @param user user account for security check 
	 * @return a list of export format description
	 * @throws ChouetteException invalid user access
	 */
	List<FormatDescription> getDeleteExportFormats(User user) throws ChouetteException;

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
	void doExportDeleted(User user,List<T> beans, String formatDescriptor, List<ParameterValue> parameters,ReportHolder report) throws ChouetteException;

	// validation
	
	/**
	 * check if any validate plug'in is available
	 * 
	 * @return true if a plug'in is available
	 */
	boolean canValidate();
	
	/**
	 * validate a list of beans according to model rules
	 * 
	 * @param user user account for security check 
	 * @param beans beans to be validate
	 * @param parameters validation parameters
	 * @return a diagnostic step by step 
	 * @throws ChouetteException invalid user access or validation failure
	 */
	Report validate(User user,List<T> beans,ValidationParameters parameters, Boolean ... propagate) throws ChouetteException;

	/**
	 * get the steps description for validation
	 * 
	 * @param user user account for security check 
	 * @return a step description 
	 * @throws ChouetteException invalid user access
	 */
	List<ValidationStepDescription> getValidationSteps(User user) throws ChouetteException;

	/**
	 * validate a list of beans according to one step of the model rules
	 * 
	 * @param user user account for security check 
	 * @param beans beans to be validate 
	 * @param stepDescriptor the step to execute
	 * @param parameters validation parameters
	 * @return the step diagnostic 
	 * @throws ChouetteException invalid user access or export failure
	 */
	ReportItem validateStep(User user, List<T> beans, String stepDescriptor,ValidationParameters parameters) throws ChouetteException;


	/**
	 * import plug'in injection
	 * 
	 * @param plugin
	 */
	void addImportPlugin(IImportPlugin<T> plugin);

	/**
	 * export plug'in injection
	 * 
	 * @param plugin
	 */
	void addExportPlugin(IExportPlugin<T> plugin);
	/**
	 * export plug'in injection
	 * 
	 * @param plugin
	 */
	void addExportDeletionPlugin(IExportPlugin<T> plugin);
	/**
	 * validation plug'in injection
	 * 
	 * @param plugin
	 */
	void addValidationPlugin(IValidationPlugin<T> plugin);
	
	/**
	 * Object completion <br />
	 * Complete this object by setting all shortcutIds and objects required (in export process for example)
	 * @param user
	 * @param bean
	 * @throws ChouetteException 
	 */
	void completeObject(User user, T bean) throws ChouetteException;
	
	/**
	 * save in storage the imported beans<br/>
	 * new beans will be added<br/>
	 * beans of main type will replace existing beans<br/>
	 * existing beans in relation dependencies will be updated
	 * 
	 * @param user user account for security check 
	 * @param beans a collection of beans to save
	 * @param propagate indicates if sub objects should be saved automatically 
	 * @param fast indicates if you want to save quickly
	 * @throws ChouetteException invalid user access or constraints conflits or storage access problem 
	 */
	void saveAll(User user, List<T> beans,boolean propagate, boolean fast) throws ChouetteException;
}
