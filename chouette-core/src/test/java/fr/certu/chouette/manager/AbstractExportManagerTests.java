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
import fr.certu.chouette.plugin.exchange.IExportPlugin;
import fr.certu.chouette.plugin.exchange.ParameterDescription;
import fr.certu.chouette.plugin.exchange.ParameterValue;
import fr.certu.chouette.plugin.exchange.SimpleParameterValue;
import fr.certu.chouette.plugin.report.ReportHolder;
public abstract class AbstractExportManagerTests<T extends NeptuneIdentifiedObject> extends AbstractManagerTests<T>
{

	protected FormatDescription exportDescription;
	protected FormatDescription exportDeletionDescription;

	public IExportPlugin<T> exportMock;

	@SuppressWarnings("unchecked")
	public void initManager(String beanName, String managerName, T bean)
	{
		manager = (AbstractNeptuneManager<T>) applicationContext.getBean(managerName);
		this.beanName = beanName;
		this.bean = bean;
		exportMock = createMock(IExportPlugin.class );
		exportDescription = new FormatDescription(this.getClass().getName()) ;
		exportDescription.setName("TestExportPlugin");
		List<ParameterDescription> params = new ArrayList<ParameterDescription>();
		exportDescription.setParameterDescriptions(params);		
		exportDeletionDescription = new FormatDescription(this.getClass().getName()) ;
		exportDeletionDescription.setName("TestExportDeletionPlugin");
		exportDeletionDescription.setParameterDescriptions(params);		
	}


	
	@Test (groups = {"exportPlugins"}, description = "manager should return export format list ")
	public void verifyGetExportFormat() throws ChouetteException
	{
		expect(exportMock.getDescription()).andReturn(exportDescription).anyTimes();
		replay(exportMock);
		manager.addExportPlugin(exportMock);
		List<FormatDescription> list = manager.getExportFormats(null);
		Assert.assertTrue(list.contains(exportDescription),"list should contain expected format");
		verify(exportMock);
	}
			
	@Test (groups = {"exportPlugins"}, description = "manager should pass exported beans" )
	public void verifyDoExport() throws ChouetteException
	{
		List<ParameterValue> values = new ArrayList<ParameterValue>();
		SimpleParameterValue val = new SimpleParameterValue("first");
		val.setStringValue("String value");
		values.add(val);
		ReportHolder report = new ReportHolder();
		List<T> beans = new ArrayList<T>();
		beans.add(bean);
		expect(exportMock.getDescription()).andStubReturn(exportDescription);
		exportMock.doExport(beans, values, report);
		replay(exportMock);
		manager.addExportPlugin(exportMock);
		manager.doExport(null, beans, exportDescription.getName(), values, report);
		verify(exportMock);
	}

	@Test (groups = {"exportPlugins"}, description = "manager should report unknown format" , expectedExceptions={CoreException.class})
	public void verifyDoExportUnknownFormat() throws ChouetteException
	{
		List<ParameterValue> values = new ArrayList<ParameterValue>();
		SimpleParameterValue val = new SimpleParameterValue("first");
		val.setStringValue("String value");
		values.add(val);
		ReportHolder report = new ReportHolder();
		List<T> beans = new ArrayList<T>();
		beans.add(bean);
		manager.doExport(null, beans, "wrongformat", values, report);
		Assert.fail("expected exception not raised");
	}


}
