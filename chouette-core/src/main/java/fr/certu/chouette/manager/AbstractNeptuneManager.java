/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */
package fr.certu.chouette.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.core.CoreException;
import fr.certu.chouette.core.CoreExceptionCode;
import fr.certu.chouette.core.CoreRuntimeException;
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
import fr.certu.chouette.plugin.validation.ValidationClassReportItem;
import fr.certu.chouette.plugin.validation.ValidationParameters;
import fr.certu.chouette.plugin.validation.ValidationReport;
import fr.certu.chouette.plugin.validation.ValidationStepDescription;

/**
 * @author michel
 *
 * @param <T>
 */
public abstract class AbstractNeptuneManager<T extends NeptuneIdentifiedObject> implements INeptuneManager<T>
{
	private static final Logger localLogger = Logger.getLogger(AbstractNeptuneManager.class);
	// data storage access by hibernate
	@Getter @Setter private IDaoTemplate<T> dao; 

	// data storage access by jdbc
	@Getter @Setter private IDaoTemplate<T> jdbcDao;

	@Getter @Setter private String objectIdDefaultPrefix ;

	private static Random random = new Random(System.currentTimeMillis());

	private Map<String,IImportPlugin<T>> importPluginMap = new HashMap<String, IImportPlugin<T>>();
	private Map<String,IExportPlugin<T>> exportPluginMap = new HashMap<String, IExportPlugin<T>>();
	private Map<String,IExportPlugin<T>> exportDeletionPluginMap = new HashMap<String, IExportPlugin<T>>();
	private List<IValidationPlugin<T>> validationPluginList = new ArrayList<IValidationPlugin<T>>();

	private static Map<Class<?>,INeptuneManager<?>> managers = new HashMap<Class<?>, INeptuneManager<?>>();

	protected abstract Logger getLogger();
	/**
	 * 
	 */
	private Class<?> neptuneType ;

	private @Getter String objectIdKey;


	public AbstractNeptuneManager(Class<?> neptuneType,String objectIdKey) 
	{
		managers.put(neptuneType, this);
		this.neptuneType = neptuneType;
		this.objectIdKey = objectIdKey;
	}



	@SuppressWarnings("unchecked")
	public T getNewInstance(User user) throws ChouetteException 
	{
		try 
		{
			return (T) neptuneType.getConstructor(new Class[0]).newInstance(new Object[0]);
		} 
		catch (Exception e) 
		{
			throw new CoreRuntimeException(CoreExceptionCode.FATAL, e);
		} 
	}





	/* (non-Javadoc)
	 * @see fr.certu.chouette.manager.INeptuneManager#setObjectId(fr.certu.chouette.model.user.User, fr.certu.chouette.model.neptune.NeptuneIdentifiedObject, java.lang.String)
	 */
	@Override
	public void setObjectId(User user, T bean, String prefix) throws ChouetteException 
	{
		if (bean.getId() == null) return;
		String objectId = (prefix == null?objectIdDefaultPrefix:prefix)+":"+objectIdKey+":"+bean.getId().toString();
		bean.setObjectId(objectId);
	}

	/**
	 * @param neptuneType
	 * @return
	 */
	public static INeptuneManager<? extends NeptuneIdentifiedObject> getManager(Class<? extends NeptuneIdentifiedObject> neptuneType)
	{
		return managers.get(neptuneType);
	}

	/* (non-Javadoc)
	 * @see fr.certu.chouette.manager.INeptuneManager#addImportPlugin(fr.certu.chouette.plugin.exchange.IImportPlugin)
	 */
	@Override
	public void addImportPlugin(IImportPlugin<T> plugin)
	{
		importPluginMap.put(plugin.getDescription().getName(),plugin);
	}

	/* (non-Javadoc)
	 * @see fr.certu.chouette.manager.INeptuneManager#addExportPlugin(fr.certu.chouette.plugin.exchange.IExportPlugin)
	 */
	@Override
	public void addExportPlugin(IExportPlugin<T> plugin)
	{
		exportPluginMap.put(plugin.getDescription().getName(),plugin);
	}
	/* (non-Javadoc)
	 * @see fr.certu.chouette.manager.INeptuneManager#addExportDeletionPlugin(fr.certu.chouette.plugin.exchange.IExportPlugin)
	 */
	@Override
	public void addExportDeletionPlugin(IExportPlugin<T> plugin)
	{
		exportDeletionPluginMap.put(plugin.getDescription().getName(),plugin);
	}

	/* (non-Javadoc)
	 * @see fr.certu.chouette.manager.INeptuneManager#addValidationPlugin(fr.certu.chouette.plugin.validation.IValidationPlugin)
	 */
	@Override
	public void addValidationPlugin(IValidationPlugin<T> plugin)
	{
		validationPluginList.add(plugin);
	}

	/* (non-Javadoc)
	 * @see fr.certu.chouette.manager.INeptuneManager#addNew(fr.certu.chouette.model.user.User, fr.certu.chouette.model.neptune.NeptuneObject)
	 */
	@Transactional
	@Override
	public T addNew(User user, T bean) throws ChouetteException 
	{
		save(user,bean,false);
		return bean;
	}

	/* (non-Javadoc)
	 * @see fr.certu.chouette.manager.INeptuneManager#exists(fr.certu.chouette.model.user.User, fr.certu.chouette.model.neptune.NeptuneObject)
	 */
	@Transactional
	@Override
	public boolean exists(User user, T bean) throws ChouetteException
	{
		if (getDao() == null) throw new CoreException(CoreExceptionCode.NO_DAO_AVAILABLE,"unavailable resource");
		// TODO : check user access
		if (bean.getId() != null)
		{
			return getDao().exists(bean.getId());
		}
		if (bean.getObjectId() != null && !bean.getObjectId().isEmpty())
		{
			return getDao().exists(bean.getObjectId());
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see fr.certu.chouette.manager.INeptuneManager#get(fr.certu.chouette.model.user.User, fr.certu.chouette.filter.Filter, fr.certu.chouette.filter.DetailLevelEnum)
	 */
	@Transactional
	@Override
	public T get(User user, Filter filter) throws ChouetteException
	{
		if (getDao() == null) throw new CoreException(CoreExceptionCode.NO_DAO_AVAILABLE,"unavailable resource");
		// TODO : check user access
		if (filter.getType().equals(Filter.Type.EQUALS))
		{
			T bean = null;
			if (filter.getAttribute().equals("id"))
			{
				bean = getDao().get((Long) filter.getFirstValue());
			}
			else if (filter.getAttribute().equals("objectId"))
			{
				bean = getDao().getByObjectId((String) filter.getFirstValue());
			}
			else
			{
				throw new CoreException(CoreExceptionCode.NO_DAO_AVAILABLE,"invalid filter");
			}
			//			bean.expand(level);
			return bean;
		}

		return null;
	}

	/* (non-Javadoc)
	 * @see fr.certu.chouette.manager.INeptuneManager#getAll(fr.certu.chouette.model.user.User, fr.certu.chouette.filter.Filter, fr.certu.chouette.filter.DetailLevelEnum)
	 */
	@Transactional
	@Override
	public List<T> getAll(User user, Filter filter) throws ChouetteException
	{
		if (getDao() == null) throw new CoreException(CoreExceptionCode.NO_DAO_AVAILABLE,"unavailable resource");
		// TODO : check user access
		List<T> beans =  getDao().select(filter);

		return beans;
	}


	@Transactional
	@Override
	public List<T> getAll(User user) throws ChouetteException {
		// TODO check User access
		if (getDao() == null) throw new CoreException(CoreExceptionCode.NO_DAO_AVAILABLE,"unavailable resource");
		return getDao().getAll();
	}
	
	
	/* (non-Javadoc)
	 * @see fr.certu.chouette.manager.INeptuneManager#count(fr.certu.chouette.model.user.User, fr.certu.chouette.filter.Filter)
	 */
	@Override
	public long count(User user, Filter clause) throws ChouetteException 
	{
		if (getDao() == null) throw new CoreException(CoreExceptionCode.NO_DAO_AVAILABLE,"unavailable resource");
        return getDao().count(clause);
	}



	/* (non-Javadoc)
	 * @see fr.certu.chouette.manager.INeptuneManager#purge(fr.certu.chouette.model.user.User, boolean)
	 */
	@Override
	public int purge(User user) throws ChouetteException 
	{
		if (getJdbcDao() == null) throw new CoreException(CoreExceptionCode.NO_DAO_AVAILABLE,"unavailable resource");
        return getJdbcDao().purge();
	}
	
	/* (non-Javadoc)
	 * @see fr.certu.chouette.manager.NeptuneBeanManager#update(fr.certu.chouette.model.user.User, fr.certu.chouette.model.neptune.NeptuneBean)
	 */
	@Transactional
	@Override
	public void update(User user, T bean) throws ChouetteException
	{
		if (getDao() == null) throw new CoreException(CoreExceptionCode.NO_DAO_AVAILABLE,"unavailable resource");

		getDao().update(bean);
	}

	/* (non-Javadoc)
	 * @see fr.certu.chouette.manager.NeptuneBeanManager#isRemovable(fr.certu.chouette.model.user.User, fr.certu.chouette.model.neptune.NeptuneBean)
	 */
	@Transactional
	@Override
	public boolean isRemovable(User user, T bean) throws ChouetteException
	{
		if (getDao() == null) throw new CoreException(CoreExceptionCode.NO_DAO_AVAILABLE,"unavailable resource");
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see fr.certu.chouette.manager.NeptuneBeanManager#remove(fr.certu.chouette.model.user.User, fr.certu.chouette.model.neptune.NeptuneBean)
	 */
	@Transactional
	@Override
	public void remove(User user, T bean,boolean propagate) throws ChouetteException
	{
		if (getDao() == null) throw new CoreException(CoreExceptionCode.NO_DAO_AVAILABLE,"unavailable resource");
		getLogger().debug("removing object :"+bean.getObjectId());
		getDao().remove(bean.getId());
	}

	/* (non-Javadoc)
	 * @see fr.certu.chouette.manager.NeptuneBeanManager#removeAll(fr.certu.chouette.model.user.User, fr.certu.chouette.manager.Filter)
	 */
	@Transactional
	@Override
	public void removeAll(User user, List<T> objects,boolean propagate) throws ChouetteException
	{
		if (getDao() == null) throw new CoreException(CoreExceptionCode.NO_DAO_AVAILABLE,"unavailable resource");
		/*
		for (T t : objects) {
			remove(user, t, propagate);
		}
		 */
		getDao().removeAll(objects);
	}

	@Transactional
	@Override
	public int removeAll(User user,Filter filter) throws ChouetteException
	{
		if (getDao() == null) throw new CoreException(CoreExceptionCode.NO_DAO_AVAILABLE,"unavailable resource");
		return 0;
	}	

	/* (non-Javadoc)
	 * @see fr.certu.chouette.manager.INeptuneManager#getImportFormats(fr.certu.chouette.model.user.User)
	 */
	@Override
	public List<FormatDescription> getImportFormats(User user)
	throws ChouetteException 
	{
		List<FormatDescription> formats = new ArrayList<FormatDescription>();

		for (IImportPlugin<T> plugin : importPluginMap.values()) 
		{
			formats.add(plugin.getDescription());
		}

		return formats;
	}

	@Override
	public List<T> doImport(User user, String formatDescriptor,List<ParameterValue> parameters,ReportHolder report) throws ChouetteException 
	{
		IImportPlugin<T> plugin = importPluginMap.get(formatDescriptor);
		if (plugin == null) throw new CoreException(CoreExceptionCode.NO_PLUGIN_AVAILABLE,"unknown format :"+formatDescriptor);

		return plugin.doImport(parameters,report);


	}

	@Transactional
	@Override
	public void save(User user, T object ,boolean propagate) throws ChouetteException
	{
		if (getDao() == null) throw new CoreException(CoreExceptionCode.NO_DAO_AVAILABLE,"unavailable resource");

		String prefix = null;
		if (object.getId() == null)
		{
			if (object.getObjectId() != null && !object.getObjectId().isEmpty())
			{
				if (!object.getObjectId().contains(":"))
				{
					prefix = object.getObjectId();
					object.setObjectId(null);
				}
			}
			if (object.getObjectId() == null || object.getObjectId().isEmpty()) 
				object.setObjectId("::pending_Id::"+random.nextLong()); // mandatory in database
			getDao().save(object);
		}
		if (object.getObjectId() == null || object.getObjectId().isEmpty() || object.getObjectId().startsWith("::pending_Id::")) 
			setObjectId(user, object, prefix);
		getLogger().debug("saving object :"+object.getObjectId());
		if (object.getCreationTime() == null) object.setCreationTime(new Date());
		if (object.getObjectVersion() <= 0) object.setObjectVersion(1);
		getDao().update(object);
	}

	/* (non-Javadoc)
	 * @see fr.certu.chouette.manager.INeptuneManager#saveAll(fr.certu.chouette.model.user.User, java.util.List)
	 */
	//	@Override
	//	public final void saveAll(User user, List<T> beans,boolean propagate) throws ChouetteException 
	//	{
	//
	////		if(getJdbcDao() == null)
	////			throw new CoreException(CoreExceptionCode.NO_JDBC_DAO_AVAILABLE, "unavailable resource");
	////
	////		getJdbcDao().saveOrUpdateAll(beans);
	//	}

	@Transactional
	@Override
	public void saveAll(User user, List<T> beans,boolean propagate, boolean fast) throws ChouetteException 
	{
		Date creationTime = new Date();
		for (T object : beans) 
		{
			if (object.getCreationTime() == null) object.setCreationTime(creationTime);
		}
		if(fast)
		{
			if(getJdbcDao() == null)
				throw new CoreException(CoreExceptionCode.NO_JDBC_DAO_AVAILABLE, "unavailable resource");
         if (getDao() == null) throw new CoreException(CoreExceptionCode.NO_DAO_AVAILABLE,"unavailable resource");
			// maybe resave fast allready connected object
         getDao().detach(beans);
         // save
			getJdbcDao().saveOrUpdateAll(beans);	
		}
		else
		{
			if (getDao() == null) throw new CoreException(CoreExceptionCode.NO_DAO_AVAILABLE,"unavailable resource");
			getLogger().debug("saving "+beans.size()+" "+neptuneType.getSimpleName());

			getDao().saveOrUpdateAll(beans);

		}
	}
	/* (non-Javadoc)
	 * @see fr.certu.chouette.manager.NeptuneBeanManager#saveOrUpdateAll(fr.certu.chouette.model.user.User, java.util.List)
	 */
	@Transactional
	@Override
	public void saveOrUpdateAll(User user, List<T> beans) throws ChouetteException
	{
		if (getDao() == null) throw new CoreException(CoreExceptionCode.NO_DAO_AVAILABLE,"unavailable resource");
		getDao().saveOrUpdateAll(beans);
	}


	/* (non-Javadoc)
	 * @see fr.certu.chouette.manager.INeptuneManager#getExportFormats(fr.certu.chouette.model.user.User)
	 */
	@Override
	public List<FormatDescription> getExportFormats(User user)
	throws ChouetteException 
	{
		List<FormatDescription> formats = new ArrayList<FormatDescription>();

		for (IExportPlugin<T> plugin : exportPluginMap.values()) 
		{
			formats.add(plugin.getDescription());
		}
		return formats;
	}

	/* (non-Javadoc)
	 * @see fr.certu.chouette.manager.INeptuneManager#doExport(fr.certu.chouette.model.user.User, java.util.List, java.lang.String, java.util.List, fr.certu.chouette.plugin.report.ReportHolder)
	 */
	@Override
	public void doExport(User user, List<T> beans, String formatDescriptor,
			List<ParameterValue> parameters,ReportHolder report) 
	throws ChouetteException 
	{
		IExportPlugin<T> plugin = exportPluginMap.get(formatDescriptor);
		if (plugin == null) throw new CoreException(CoreExceptionCode.NO_PLUGIN_AVAILABLE,"unknown format :"+formatDescriptor);

		plugin.doExport(beans,parameters,report);

	}


	/* (non-Javadoc)
	 * @see fr.certu.chouette.manager.INeptuneManager#doExportFromDatabase(fr.certu.chouette.model.user.User, java.util.List, java.lang.String, java.util.List, fr.certu.chouette.plugin.report.ReportHolder)
	 */
	@Override
	public void doExportFromDatabase(User user, List<Long> beanIds,
			String formatDescriptor, List<ParameterValue> parameters,
			ReportHolder report) 
	throws ChouetteException 
	{
		if (getDao() == null) throw new CoreException(CoreExceptionCode.NO_DAO_AVAILABLE,"unavailable resource");
		IExportPlugin<T> plugin = exportPluginMap.get(formatDescriptor);
		if (plugin == null) throw new CoreException(CoreExceptionCode.NO_PLUGIN_AVAILABLE,"unknown format :"+formatDescriptor);
		Filter clause = Filter.getNewInFilter("id", beanIds);
		List<T> beans = getDao().select(clause);
		if (!beans.isEmpty())
		{
			plugin.doExport(beans,parameters,report);
		}

	}

	/* (non-Javadoc)
	 * @see fr.certu.chouette.manager.INeptuneManager#getDeleteExportFormats(fr.certu.chouette.model.user.User)
	 */
	@Override
	public List<FormatDescription> getDeleteExportFormats(User user)
	throws ChouetteException 
	{
		List<FormatDescription> formats = new ArrayList<FormatDescription>();

		for (IExportPlugin<T> plugin : exportDeletionPluginMap.values()) 
		{
			formats.add(plugin.getDescription());
		}
		return formats;
	}

	/* (non-Javadoc)
	 * @see fr.certu.chouette.manager.INeptuneManager#doExportDeleted(fr.certu.chouette.model.user.User, java.util.List, java.lang.String, java.util.List, fr.certu.chouette.plugin.report.ReportHolder)
	 */
	@Override
	public void doExportDeleted(User user, List<T> beans,
			String formatDescriptor, List<ParameterValue> parameters,ReportHolder report)
	throws ChouetteException 
	{
		IExportPlugin<T> plugin = exportDeletionPluginMap.get(formatDescriptor);
		if (plugin == null) throw new CoreException(CoreExceptionCode.NO_PLUGIN_AVAILABLE,"unknown format :"+formatDescriptor);

		plugin.doExport(beans,parameters,report);

	}

	/* (non-Javadoc)
	 * @see fr.certu.chouette.manager.INeptuneManager#canValidate()
	 */
	@Override
	public boolean canValidate()
	{
		return validationPluginList.size() > 0;

	}


	/* (non-Javadoc)
	 * @see fr.certu.chouette.manager.INeptuneManager#validate(fr.certu.chouette.model.user.User, java.util.List, fr.certu.chouette.plugin.validation.ValidationParameters)
	 */
	@Override
	public Report validate(User user, List<T> beans, ValidationParameters parameters, Boolean... propagate) throws ChouetteException 
	{
		if (validationPluginList.size() == 0) throw new CoreException(CoreExceptionCode.NO_VALIDATION_PLUGIN_AVAILABLE,"");

		boolean hasToPropagate = true;
		if (propagate.length > 0) 
		{
			hasToPropagate=propagate[0].booleanValue();
		}

		Report r = new ValidationReport();
		ValidationClassReportItem[] validationClasses = new ValidationClassReportItem[ValidationClassReportItem.CLASS.values().length]; // see how manage max enum
		for (int i = 0; i < validationClasses.length; i++)
		{
			validationClasses[i] = new ValidationClassReportItem(ValidationClassReportItem.CLASS.values()[i]);
		}

		for (IValidationPlugin<T> plugin : validationPluginList)
		{
			List<ValidationClassReportItem> stepItems = plugin.doValidate(beans,parameters);

			for (ValidationClassReportItem item : stepItems) 
			{
				int rank=item.getValidationClass().ordinal();
				validationClasses[rank].addAll(item.getItems());
			}

		}

		if (hasToPropagate)
		{
			Report propagationReport = propagateValidation(user, beans, parameters,hasToPropagate);
			if (propagationReport != null && propagationReport.getItems() != null)
			{
				for (ReportItem item : propagationReport.getItems())
				{
					ValidationClassReportItem classItem = (ValidationClassReportItem) item;
					validationClasses[classItem.getValidationClass().ordinal()].addAll(classItem.getItems());
					validationClasses[classItem.getValidationClass().ordinal()].updateStatus(classItem.getStatus());
				}
			}
		}

		for (ValidationClassReportItem item : validationClasses) 
		{
			if (item.getItems() != null && !item.getItems().isEmpty())
			{
				item.sortItems();
				r.addItem(item);
			}
		}
		return r;
	}

	protected Report propagateValidation(User user, List<T> beans, ValidationParameters parameters,boolean propagate) throws ChouetteException 
	{
		return null;
	}


	/* (non-Javadoc)
	 * @see fr.certu.chouette.manager.INeptuneManager#getValidationSteps(fr.certu.chouette.model.user.User)
	 */
	@Override
	public List<ValidationStepDescription> getValidationSteps(User user)
	throws ChouetteException 
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see fr.certu.chouette.manager.INeptuneManager#validateStep(fr.certu.chouette.model.user.User, java.util.List, java.lang.String, fr.certu.chouette.plugin.validation.ValidationParameters)
	 */
	@Override
	public ReportItem validateStep(User user, List<T> beans, String stepDescriptor, ValidationParameters parameters)
	throws ChouetteException 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void completeObject(User user, T bean) throws ChouetteException 
	{
	}

	@Transactional
	@Override
	public T getByObjectId(String objectId) throws CoreException
	{
		if (getDao() == null) throw new CoreException(CoreExceptionCode.NO_DAO_AVAILABLE,"unavailable resource");

		T bean = getDao().getByObjectId(objectId);	
		//		bean.expand(DetailLevelEnum.ATTRIBUTE);
		return bean;
	}

	@Override
	public T getById(Long id) throws CoreException
	{
		if (getDao() == null) throw new CoreException(CoreExceptionCode.NO_DAO_AVAILABLE,"unavailable resource");

		T bean = getDao().get(id);	
		//		bean.expand(DetailLevelEnum.ATTRIBUTE);
		return bean;
	}

	/**
	 * merge source collection in target one : <br/>
	 * add source objects if not already in target
	 * @param <U> type of source and target entries
	 * @param target collection to fill
	 * @param source objects to add
	 */
	protected static <U> void mergeCollection(Collection<U> target, Collection<U> source )
	{
		if (source == null || source.isEmpty() ) return;
		for (U object : source) 
		{
			if (!target.contains(object)) 
			{
				target.add(object);
			}
		}
	}

	/**
	 * add source in target collection if not null, not already in database and not already in target 
	 * @param <U> type of source and collection entries
	 * @param target collection
	 * @param source object to add
	 */
	protected static <U extends NeptuneIdentifiedObject> void addIfMissingInCollection(Collection<U> target, U source )
	{
		if (source == null ) return;
		if (source.getId() != null ) 
		{
			localLogger.debug(source.getObjectId()+" not added , already saved as "+source.getId());
			return;
		}
		if (!target.contains(source)) target.add(source);

	}

	/**
	 * build a list of ids from object collection
	 * 
	 * @param <U> type of collection entries
	 * @param beans  collection
	 * @return 
	 */
	protected static <U extends NeptuneIdentifiedObject> List<Long> getIds(Collection<U> beans)
	{
		List<Long> ids = new ArrayList<Long>();
		for (U bean : beans) 
		{
			if (bean.getId() != null)
			{
				if (!ids.contains(bean.getId()))
				{
					ids.add(bean.getId());
				}
			}
		}
		return ids;
	}

}
