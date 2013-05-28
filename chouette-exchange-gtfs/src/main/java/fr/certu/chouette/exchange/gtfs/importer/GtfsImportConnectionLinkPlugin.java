package fr.certu.chouette.exchange.gtfs.importer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import lombok.Setter;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.exchange.gtfs.importer.producer.AbstractModelProducer;
import fr.certu.chouette.exchange.gtfs.importer.producer.ConnectionLinkProducer;
import fr.certu.chouette.exchange.gtfs.model.GtfsTransfer;
import fr.certu.chouette.manager.INeptuneManager;
import fr.certu.chouette.model.neptune.ConnectionLink;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.plugin.exchange.FormatDescription;
import fr.certu.chouette.plugin.exchange.IImportPlugin;
import fr.certu.chouette.plugin.exchange.ParameterDescription;
import fr.certu.chouette.plugin.exchange.ParameterValue;
import fr.certu.chouette.plugin.exchange.SimpleParameterValue;
import fr.certu.chouette.plugin.exchange.report.ExchangeReport;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.ReportHolder;

public class GtfsImportConnectionLinkPlugin implements IImportPlugin<ConnectionLink>
{
	private static final Logger logger = Logger.getLogger(GtfsImportConnectionLinkPlugin.class);
	private FormatDescription description;

	private List<String>        allowedExtensions = Arrays.asList(new String[] { "zip" });
	
	/**
	 * Connection producer from GtfsTransfer
	 */
	@Setter private ConnectionLinkProducer connectionLinkProducer ;
	
	@Setter private INeptuneManager<StopArea> stopAreaManager;


	public GtfsImportConnectionLinkPlugin()
	{
		description = new FormatDescription(this.getClass().getName());
		description.setName("GTFS");
		List<ParameterDescription> params = new ArrayList<ParameterDescription>();
		{
			ParameterDescription param = new ParameterDescription("inputFile", ParameterDescription.TYPE.FILEPATH, false, true);
			param.setAllowedExtensions(Arrays.asList(new String[]{"zip"}));
			params.add(param);
		}
		{
			ParameterDescription param = new ParameterDescription("fileFormat", ParameterDescription.TYPE.STRING, false,
					"file extension");
			param.setAllowedExtensions(Arrays.asList(new String[] { "zip" }));
			params.add(param);
		}
		{
			ParameterDescription param = new ParameterDescription("objectIdPrefix", ParameterDescription.TYPE.STRING, false, true);
			params.add(param);
		}
		{
			ParameterDescription param = new ParameterDescription("optimizeMemory", ParameterDescription.TYPE.BOOLEAN,false , "false");
			params.add(param);
		}



		description.setParameterDescriptions(params);
	}

	@Override
	public FormatDescription getDescription() 
	{
		return description;
	}

	@Override
	public List<ConnectionLink> doImport(List<ParameterValue> parameters, ReportHolder reportContainer) throws ChouetteException 
	{
		String filePath = null;
		String objectIdPrefix = null;
		boolean optimizeMemory = false;
		String extension = "file extension";
		for (ParameterValue value : parameters) 
		{
			if (value instanceof SimpleParameterValue) 
			{
				SimpleParameterValue svalue = (SimpleParameterValue)value;
				if (svalue.getName().equalsIgnoreCase("inputFile")) 
				{
					filePath = svalue.getFilepathValue();
				} 
				else if (svalue.getName().equals("fileFormat"))
				{
					extension = svalue.getStringValue().toLowerCase();
				}
				else if (svalue.getName().equalsIgnoreCase("objectIdPrefix")) 
				{
					objectIdPrefix = svalue.getStringValue().toLowerCase();
				} 
				else if (svalue.getName().equalsIgnoreCase("optimizeMemory")) 
				{
					optimizeMemory = svalue.getBooleanValue().booleanValue();
				}
				else 
				{
					throw new IllegalArgumentException("unexpected argument " + svalue.getName());
				}
			}
			else 
			{
				throw new IllegalArgumentException("unexpected argument " + value.getName());
			}
		}
		if (filePath == null) 
		{
			logger.error("missing argument zipFile");
			throw new IllegalArgumentException("zipFile required");
		}

		if (objectIdPrefix == null) 
		{
			logger.error("missing argument objectIdPrefix");
			throw new IllegalArgumentException("objectIdPrefix required");
		}

		if (extension.equals("file extension"))
		{
			extension = FilenameUtils.getExtension(filePath).toLowerCase();
		}
		if (!allowedExtensions.contains(extension))
		{
			logger.error("invalid argument inputFile " + filePath + ", allowed format : "
					+ Arrays.toString(allowedExtensions.toArray()));
			throw new IllegalArgumentException("invalid file type : " + extension);
		}

		Report report = new ExchangeReport(ExchangeReport.KEY.IMPORT, description.getName());
		report.setStatus(Report.STATE.OK);
		reportContainer.setReport(report);
		
		ZipFile zip = null;
		try 
		{
			zip = new ZipFile(filePath);
		} 
		catch (IOException e) 
		{
			//            ReportItem item = new GtfsReportItem(GtfsReportItem.KEY.FILE_ERROR,Report.STATE.ERROR,filePath,e.getLocalizedMessage());
			//            report.addItem(item);
			//            report.setStatus(Report.STATE.FATAL);
			logger.error("zip import failed (cannot open zip)" + e.getLocalizedMessage());
			return null;
		}
		try
		{
			GtfsData data = new GtfsData(objectIdPrefix,optimizeMemory);
			data.loadNetwork(objectIdPrefix);
			boolean ok = true;
			for (Enumeration<? extends ZipEntry> entries = zip.entries(); entries.hasMoreElements() && ok;) 
			{
				ZipEntry entry = entries.nextElement();

				// ignore directory
				if (entry.isDirectory()) continue;

				String entryName = entry.getName();

				logger.info("analyzing "+entryName) ;
				if (entryName.endsWith("transfers.txt"))
				{
					try 
					{
						data.loadTransfers(zip.getInputStream(entry));
					} 
					catch (Exception e) 
					{
						logger.error("zip import failed (cannot read transfers.txt)" + e.getLocalizedMessage(),e);
						ok = false;
					}
				}
				else
				{
					logger.info("entry "+entryName+" unused");
				}
			}
			if (ok)
			{
				System.gc();
				List<StopArea> areas = stopAreaManager.getAll(null);
				
				if (areas.size() == 0)
				{
                	logger.warn("no area for connection link ");
					return new ArrayList<ConnectionLink>();
					
				}
				Map<String, StopArea> areaByKey = NeptuneIdentifiedObject.mapOnObjectIds(areas);
				
				List<ConnectionLink> links = new ArrayList<ConnectionLink>();
				List<ConnectionLink> excludedLinks = new ArrayList<ConnectionLink>();
				AbstractModelProducer.setPrefix(objectIdPrefix);

				for (GtfsTransfer transfer : data.getTransfers().getAll())
				{
					ConnectionLink link = connectionLinkProducer.produce(transfer, null);
					
					link.setStartOfLinkId(objectIdPrefix+":"+StopArea.STOPAREA_KEY+":"+link.getStartOfLinkId());
					link.setEndOfLinkId(objectIdPrefix+":"+StopArea.STOPAREA_KEY+":"+link.getEndOfLinkId());

					if ("FORBIDDEN".equals(link.getName()))
					{
						excludedLinks.add(link);
					}
					else
					{
						link.setStartOfLink(areaByKey.get(link.getStartOfLinkId()));
						link.setEndOfLink(areaByKey.get(link.getEndOfLinkId()));
                        if (link.getStartOfLink() == null )
                        {
                        	// report missing link
                        	logger.warn("missing start of link "+link.getStartOfLinkId());
                        	continue;
                        }
                        if (link.getEndOfLink() == null )
                        {
                        	// report missing link
                        	logger.warn("missing end of link "+link.getEndOfLinkId());
                        	continue;
                        }
						link.setName("from "+link.getStartOfLink().getName()+" to "+link.getEndOfLink().getName());
						links.add(link);
					}
				}
                return links;
			}
			else
			{
				return new ArrayList<ConnectionLink>();
			}
		}
		finally
		{
			try {
				zip.close();
			} catch (IOException e) {
				logger.warn("cannot close zip file");
			}

		}

	}


}
