package fr.certu.chouette.manager;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.ArrayList;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.core.CoreException;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;
import fr.certu.chouette.plugin.exchange.FormatDescription;
import fr.certu.chouette.plugin.exchange.IImportPlugin;
import fr.certu.chouette.plugin.exchange.ParameterDescription;
import fr.certu.chouette.plugin.exchange.ParameterValue;
import fr.certu.chouette.plugin.exchange.SimpleParameterValue;
import fr.certu.chouette.plugin.report.ReportHolder;
public abstract class AbstractImportManagerTests<T extends NeptuneIdentifiedObject> extends AbstractManagerTests<T>
{

	protected FormatDescription importDescription;

	public IImportPlugin<T> importMock;

	@SuppressWarnings("unchecked")
	public void initManager(String beanName, String managerName, T bean)
	{
		manager = (AbstractNeptuneManager<T>) applicationContext.getBean(managerName);
		this.beanName = beanName;
		this.bean = bean;
		importMock = createMock(IImportPlugin.class );
		importDescription = new FormatDescription(this.getClass().getName()) ;
		importDescription.setName("TestImportPlugin");
		List<ParameterDescription> params = new ArrayList<ParameterDescription>();
		importDescription.setParameterDescriptions(params);		
	}


	
	@Test (groups = {"importPlugins"}, description = "manager should return import format list ")
	public void verifyGetImportFormat() throws ChouetteException
	{
		expect(importMock.getDescription()).andReturn(importDescription).anyTimes();
		replay(importMock);
		manager.addImportPlugin(importMock);
		List<FormatDescription> list = manager.getImportFormats(null);
		Assert.assertTrue(list.contains(importDescription),"list should contain expected format");
		verify(importMock);
	}
			
	@Test (groups = {"importPlugins"}, description = "manager should return imported beans" )
	public void verifyDoImport() throws ChouetteException
	{
		List<ParameterValue> values = new ArrayList<ParameterValue>();
		SimpleParameterValue val = new SimpleParameterValue("first");
		val.setStringValue("String value");
		values.add(val);
		ReportHolder report = new ReportHolder();
		List<T> beans = new ArrayList<T>();
		beans.add(bean);
		expect(importMock.getDescription()).andStubReturn(importDescription);
		expect(importMock.doImport(values, report)).andReturn(beans);
		replay(importMock);
		manager.addImportPlugin(importMock);
		List<T> retBeans = manager.doImport(null, importDescription.getName(), values, report);
		Assert.assertEquals(retBeans, beans,"imported beans should match expected beans");
		verify(importMock);
	}

	@Test (groups = {"importPlugins"}, description = "manager should report unknown format" , expectedExceptions={CoreException.class})
	public void verifyDoImportUnknownFormat() throws ChouetteException
	{
		List<ParameterValue> values = new ArrayList<ParameterValue>();
		SimpleParameterValue val = new SimpleParameterValue("first");
		val.setStringValue("String value");
		values.add(val);
		ReportHolder report = new ReportHolder();
		List<T> beans = new ArrayList<T>();
		beans.add(bean);
		manager.doImport(null, "wrongformat", values, report);
		Assert.fail("expected exception not raised");
	}


}
