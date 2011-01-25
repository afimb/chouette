package fr.certu.chouette.manager;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.ArrayList;
import java.util.List;

import org.springframework.test.context.ContextConfiguration;
import org.testng.Assert;
import org.testng.annotations.Test;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;
import fr.certu.chouette.plugin.exchange.FormatDescription;
import fr.certu.chouette.plugin.exchange.IImportPlugin;
import fr.certu.chouette.plugin.exchange.ParameterDescription;
import fr.certu.chouette.plugin.exchange.ParameterValue;
import fr.certu.chouette.plugin.exchange.SimpleParameterValue;
import fr.certu.chouette.plugin.report.ReportHolder;
@ContextConfiguration(locations={"classpath:ChouetteContext.xml"})
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
		importDescription = new FormatDescription() ;
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
		assert list.contains(importDescription);
		verify(importMock);
	}
			
	@Test (groups = {"importPlugins"}, description = "manager should return imported beans" )// , dependsOnMethods="verifyGetImportFormat")
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
		Assert.assertEquals(retBeans, beans);
	}


}
