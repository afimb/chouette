/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */
package fr.certu.chouette.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.dao.IDaoTemplate;
import fr.certu.chouette.exchange.ExchangeException;
import fr.certu.chouette.exchange.FormatDescription;
import fr.certu.chouette.exchange.IExportPlugin;
import fr.certu.chouette.exchange.IImportPlugin;
import fr.certu.chouette.exchange.ParameterValue;
import fr.certu.chouette.exchange.PluginContainer;
import fr.certu.chouette.filter.DetailLevelEnum;
import fr.certu.chouette.filter.Filter;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;
import fr.certu.chouette.model.user.User;
import fr.certu.chouette.validation.ValidationStepDescription;
import fr.certu.chouette.validation.ValidationStepResult;

/**
 * 
 */
public abstract class AbstractNeptuneManager<T extends NeptuneIdentifiedObject> implements INeptuneManager<T>
{
	// data storage access
	@Getter @Setter private IDaoTemplate<T> dao; 

	// exchange and validation plugin
	@Getter private PluginContainer<T> pluginContainer;

	private Map<String,IImportPlugin<T>> importPluginMap = new HashMap<String, IImportPlugin<T>>();
	private Map<String,IExportPlugin<T>> exportPluginMap = new HashMap<String, IExportPlugin<T>>();

	// specific setter
	public void setPluginContainer(PluginContainer<T> container)
	{
		pluginContainer = container;
		if (pluginContainer != null)
		{
			for (IImportPlugin<T> plugin : pluginContainer.getImportPlugins()) 
			{
				importPluginMap.put(plugin.getDescription().getName(),plugin);
			}
			for (IExportPlugin<T> plugin : pluginContainer.getExportPlugins()) 
			{
				exportPluginMap.put(plugin.getDescription().getName(),plugin);
			}
		}

	}


	/* (non-Javadoc)
	 * @see fr.certu.chouette.manager.INeptuneManager#addNew(fr.certu.chouette.model.user.User, fr.certu.chouette.model.neptune.NeptuneObject)
	 */
	@Override
	public T addNew(User user, T bean) throws ChouetteException 
	{
		// TODO : check user access
		getDao().save(bean);
		return bean;
	}

	/* (non-Javadoc)
	 * @see fr.certu.chouette.manager.INeptuneManager#exists(fr.certu.chouette.model.user.User, fr.certu.chouette.model.neptune.NeptuneObject)
	 */
	@Override
	public boolean exists(User user, T bean) throws ChouetteException
	{
		// TODO : check user access
		if (bean.getId() != null)
		{
			return getDao().exists(bean.getId());
		}
		if (bean.getObjectId() != null)
		{
			return getDao().exists(bean.getObjectId());
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see fr.certu.chouette.manager.INeptuneManager#get(fr.certu.chouette.model.user.User, fr.certu.chouette.filter.Filter, fr.certu.chouette.filter.DetailLevelEnum)
	 */
	@Override
	public T get(User user, Filter filter, DetailLevelEnum level) throws ChouetteException
	{
		// TODO : check user access
		if (filter.getType().equals(Filter.Type.EQUALS))
		{
			T bean = null;
			if (filter.getAttribute().equals("id"))
			{
				bean = getDao().get((Long) filter.getFirstValue());
			}
			if (filter.getAttribute().equals("objectId"))
			{
				bean = getDao().getByObjectId((String) filter.getFirstValue());
			}
			bean.expand(level);
			return bean;
		}

		return null;
	}

	/* (non-Javadoc)
	 * @see fr.certu.chouette.manager.INeptuneManager#getAll(fr.certu.chouette.model.user.User, fr.certu.chouette.filter.Filter, fr.certu.chouette.filter.DetailLevelEnum)
	 */
	@Override
	public List<T> getAll(User user, Filter filter, DetailLevelEnum level) throws ChouetteException
	{
		// TODO : check user access
		List<T> beans =  getDao().select(filter);
		for (T bean : beans)
		{
			bean.expand(level);
		}

		return beans;
	}

	/* (non-Javadoc)
	 * @see fr.certu.chouette.manager.INeptuneManager#getImportFormats(fr.certu.chouette.model.user.User)
	 */
	@Override
	public List<FormatDescription> getImportFormats(User user)
	throws ChouetteException 
	{
		List<FormatDescription> formats = new ArrayList<FormatDescription>();
		if (pluginContainer != null)
		{
			for (IImportPlugin<T> plugin : pluginContainer.getImportPlugins()) 
			{
				formats.add(plugin.getDescription());
			}
		}
		return formats;
	}

	/* (non-Javadoc)
	 * @see fr.certu.chouette.manager.INeptuneManager#doImport(fr.certu.chouette.model.user.User, java.lang.String, java.util.List)
	 */
	@Override
	public List<T> doImport(User user, String formatDescriptor,List<ParameterValue> parameters) throws ChouetteException 
	{
		IImportPlugin<T> plugin = importPluginMap.get(formatDescriptor);
		if (plugin == null) throw new ChouetteException("unknown format :"+formatDescriptor);
		try 
		{
			return plugin.doImport(parameters);
		}
		catch (ExchangeException e) 
		{
			throw new ChouetteException("import failed", e);
		}

	}

	/* (non-Javadoc)
	 * @see fr.certu.chouette.manager.INeptuneManager#saveAll(fr.certu.chouette.model.user.User, java.util.List)
	 */
	@Override
	public void saveAll(User user, List<T> beans) throws ChouetteException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see fr.certu.chouette.manager.NeptuneBeanManager#saveOrUpdateAll(fr.certu.chouette.model.user.User, java.util.List)
	 */
	@Override
	public void saveOrUpdateAll(User user, List<T> beans) throws ChouetteException
	{
		// TODO Auto-generated method stub

	}


	/* (non-Javadoc)
	 * @see fr.certu.chouette.manager.INeptuneManager#getExportFormats(fr.certu.chouette.model.user.User)
	 */
	@Override
	public List<FormatDescription> getExportFormats(User user)
	throws ChouetteException 
	{
		return new ArrayList<FormatDescription>();
	}

	/* (non-Javadoc)
	 * @see fr.certu.chouette.manager.INeptuneManager#doExport(fr.certu.chouette.model.user.User, java.util.List, java.lang.String, java.util.List)
	 */
	@Override
	public void doExport(User user, List<T> beans, String formatDescriptor,
			List<ParameterValue> parameters) throws ChouetteException 
			{
		// TODO Auto-generated method stub

			}

	/* (non-Javadoc)
	 * @see fr.certu.chouette.manager.INeptuneManager#getDeleteExportFormats(fr.certu.chouette.model.user.User)
	 */
	@Override
	public List<FormatDescription> getDeleteExportFormats(User user)
	throws ChouetteException 
	{
		return new ArrayList<FormatDescription>();
	}

	/* (non-Javadoc)
	 * @see fr.certu.chouette.manager.INeptuneManager#doExportDeleted(fr.certu.chouette.model.user.User, java.util.List, java.lang.String, java.util.List)
	 */
	@Override
	public void doExportDeleted(User user, List<T> beans,
			String formatDescriptor, List<ParameterValue> parameters)
	throws ChouetteException 
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see fr.certu.chouette.manager.INeptuneManager#validate(fr.certu.chouette.model.user.User, fr.certu.chouette.model.neptune.NeptuneObject)
	 */
	@Override
	public List<ValidationStepResult> validate(User user, T bean)
	throws ChouetteException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see fr.certu.chouette.manager.INeptuneManager#getValidationSteps(fr.certu.chouette.model.user.User)
	 */
	@Override
	public List<ValidationStepDescription> getValidationSteps(User user)
	throws ChouetteException 
	{
		return new ArrayList<ValidationStepDescription>();
	}

	/* (non-Javadoc)
	 * @see fr.certu.chouette.manager.INeptuneManager#validateStep(fr.certu.chouette.model.user.User, fr.certu.chouette.model.neptune.NeptuneObject, java.lang.String)
	 */
	@Override
	public ValidationStepResult validateStep(User user, T bean,
			String stepDescriptor) throws ChouetteException 
			{
		// TODO Auto-generated method stub
		return null;
			}

}
