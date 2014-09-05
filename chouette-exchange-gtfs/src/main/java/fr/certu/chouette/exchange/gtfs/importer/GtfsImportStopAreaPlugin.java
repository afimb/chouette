package fr.certu.chouette.exchange.gtfs.importer;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import lombok.Setter;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.exchange.gtfs.importer.producer.AbstractModelProducer;
import fr.certu.chouette.model.neptune.ConnectionLink;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.plugin.exchange.FormatDescription;
import fr.certu.chouette.plugin.exchange.IImportPlugin;
import fr.certu.chouette.plugin.exchange.ParameterDescription;
import fr.certu.chouette.plugin.exchange.ParameterValue;
import fr.certu.chouette.plugin.exchange.SimpleParameterValue;
import fr.certu.chouette.plugin.exchange.report.ExchangeReport;
import fr.certu.chouette.plugin.exchange.report.ExchangeReportItem;
import fr.certu.chouette.plugin.exchange.report.LimitedExchangeReportItem;
import fr.certu.chouette.plugin.exchange.tools.FileTool;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.ReportHolder;
import fr.certu.chouette.plugin.report.ReportItem;

public class GtfsImportStopAreaPlugin implements IImportPlugin<StopArea>
{
	private static final Logger logger = Logger.getLogger(GtfsImportStopAreaPlugin.class);
	private FormatDescription description;
	@Setter private String dbDirectory = "/tmp";

	private List<String>        allowedExtensions = Arrays.asList(new String[] { "zip" });
	
	public GtfsImportStopAreaPlugin()
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
			ParameterDescription param = new ParameterDescription("incremental", ParameterDescription.TYPE.STRING,false , false);
			params.add(param);
		}
		{
			ParameterDescription param = new ParameterDescription("maxDistanceForCommercial", ParameterDescription.TYPE.INTEGER,false , "10");
			params.add(param);
		}
		{
			ParameterDescription param = new ParameterDescription("ignoreLastWord", ParameterDescription.TYPE.BOOLEAN, false, "false");
			params.add(param);
		}
		{
			ParameterDescription param = new ParameterDescription("ignoreEndChars", ParameterDescription.TYPE.INTEGER, false, "0");
			params.add(param);
		}
		{
			ParameterDescription param = new ParameterDescription("maxDistanceForConnectionLink", ParameterDescription.TYPE.INTEGER,false , "50");
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
	public List<StopArea> doImport(List<ParameterValue> parameters,ReportHolder importReport,ReportHolder validationReport) throws ChouetteException 
	{
		String filePath = null;
		String objectIdPrefix = null;
		double maxDistanceForCommercialStop = 10;
		double maxDistanceForConnectionLink = 50;
		boolean ignoreLastWord = false;;
		int ignoreEndCharacters = 0;
		boolean optimizeMemory = false;
		String incrementalPrefix = "";
		String extension = "file extension";
		boolean stopFound = false;
		GtfsData data = null;
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
				else if (svalue.getName().equalsIgnoreCase("incremental")) 
				{
					incrementalPrefix = svalue.getStringValue();
				} 
				else if (svalue.getName().equalsIgnoreCase("maxDistanceForCommercial")) 
				{
					maxDistanceForCommercialStop = (double) svalue.getIntegerValue().doubleValue();
				} 
				else if (svalue.getName().equalsIgnoreCase("ignoreLastWord")) 
				{
					ignoreLastWord = svalue.getBooleanValue().booleanValue();
				}
				else if (svalue.getName().equalsIgnoreCase("optimizeMemory")) 
				{
					optimizeMemory = svalue.getBooleanValue().booleanValue();
				}
				else if (svalue.getName().equalsIgnoreCase("ignoreEndChars")) 
				{
					ignoreEndCharacters = svalue.getIntegerValue().intValue();
				}
				else if (svalue.getName().equalsIgnoreCase("maxDistanceForConnectionLink")) 
				{
					maxDistanceForConnectionLink = (double) svalue.getIntegerValue().doubleValue();
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
		report.updateStatus(Report.STATE.OK);
		importReport.setReport(report);

		ZipFile zip = null;
		try 
		{
			Charset encoding = FileTool.getZipCharset(filePath);
			if (encoding == null)
			{
				ReportItem item = new ExchangeReportItem(ExchangeReportItem.KEY.FILE_ERROR,Report.STATE.ERROR,filePath,"unknown encoding");
				report.addItem(item);
				report.updateStatus(Report.STATE.ERROR);
				logger.error("zip import failed (unknown encoding)");
				return null;
			}
			zip = new ZipFile(filePath,encoding);
		} 
		catch (IOException e) 
		{
			ReportItem item = new ExchangeReportItem(ExchangeReportItem.KEY.FILE_ERROR,Report.STATE.ERROR,filePath,e.getLocalizedMessage());
			report.addItem(item);
			report.updateStatus(Report.STATE.ERROR);
			logger.error("zip import failed (cannot open zip)" + e.getLocalizedMessage());
			return null;
		}
		try
		{
		    data = new GtfsData(objectIdPrefix,dbDirectory,optimizeMemory);
			data.loadNetwork(objectIdPrefix);
			boolean ok = true;
			for (Enumeration<? extends ZipEntry> entries = zip.entries(); entries.hasMoreElements() && ok;) 
			{
				ZipEntry entry = entries.nextElement();

				// ignore directory
				if (entry.isDirectory()) continue;

				String entryName = entry.getName();

				logger.info("analyzing "+entryName) ;
				if (entryName.endsWith("stops.txt"))
				{
					stopFound=true;
					try 
					{
						ReportItem item = new LimitedExchangeReportItem(ExchangeReportItem.KEY.ZIP_ENTRY,Report.STATE.OK,entryName);
						data.loadStops(zip.getInputStream(entry),item);
						report.addItem(item);
					} 
					catch (Exception e) 
					{
						ReportItem item = new ExchangeReportItem(ExchangeReportItem.KEY.ZIP_ERROR,Report.STATE.ERROR,"stops.txt",filePath,e.getLocalizedMessage());
						report.addItem(item);
						report.updateStatus(Report.STATE.ERROR);
						logger.error("zip import failed (cannot read stops.txt)" + e.getLocalizedMessage(),e);
						ok = false;
					}
				}
				else if (entryName.endsWith("transfers.txt"))
				{
					try 
					{
						ReportItem item = new LimitedExchangeReportItem(ExchangeReportItem.KEY.ZIP_ENTRY,Report.STATE.OK,entryName);
						data.loadTransfers(zip.getInputStream(entry),item);
						report.addItem(item);
					} 
					catch (Exception e) 
					{
						ReportItem item = new ExchangeReportItem(ExchangeReportItem.KEY.ZIP_ERROR,Report.STATE.ERROR,"transfers.txt",filePath,e.getLocalizedMessage());
						report.addItem(item);
						report.updateStatus(Report.STATE.ERROR);
						logger.error("zip import failed (cannot read transfers.txt)" + e.getLocalizedMessage(),e);
						ok = false;
					}
				}
				else
				{
					ReportItem item = new ExchangeReportItem(ExchangeReportItem.KEY.FILE_IGNORED,Report.STATE.OK,entryName);
					report.addItem(item);
					logger.info("entry "+entryName+" unused");
				}
			}
			if (!stopFound)
			{
				ReportItem item = new ExchangeReportItem(ExchangeReportItem.KEY.ZIP_MISSING_ENTRY,Report.STATE.ERROR,"stops.txt",filePath);
				report.addItem(item);
				report.updateStatus(Report.STATE.ERROR);
				logger.error("zip import failed (missing entry stops.txt)");
				ok = false;	
			}
			if (ok)
			{
				NeptuneConverter converter = new NeptuneConverter();
				AbstractModelProducer.setPrefix(objectIdPrefix);
				AbstractModelProducer.setIncrementalPrefix(incrementalPrefix);

				// stopareas
				List<StopArea> commercials = new ArrayList<StopArea>();
				List<StopArea> areas = new ArrayList<StopArea>();
				Map<String, StopArea> mapStopAreasByStopId = new HashMap<String, StopArea>();
				converter.convertStopAreas(data,report, areas,commercials,mapStopAreasByStopId,maxDistanceForCommercialStop, ignoreLastWord, ignoreEndCharacters);
				
				// ConnectionLinks
				List<ConnectionLink> links = new ArrayList<ConnectionLink>();
				converter.convertConnectionLink(data, report, links, commercials,
						mapStopAreasByStopId, maxDistanceForConnectionLink);

				// report objects count
				{
					ExchangeReportItem countItem = new ExchangeReportItem(ExchangeReportItem.KEY.LINE_COUNT,Report.STATE.OK,0);
					report.addItem(countItem);
					countItem = new ExchangeReportItem(ExchangeReportItem.KEY.ROUTE_COUNT,Report.STATE.OK,0);
					report.addItem(countItem);
					countItem = new ExchangeReportItem(ExchangeReportItem.KEY.JOURNEY_PATTERN_COUNT,Report.STATE.OK,0);
					report.addItem(countItem);
					countItem = new ExchangeReportItem(ExchangeReportItem.KEY.VEHICLE_JOURNEY_COUNT,Report.STATE.OK,0);
					report.addItem(countItem);
					countItem = new ExchangeReportItem(ExchangeReportItem.KEY.STOP_AREA_COUNT,Report.STATE.OK,areas.size());
					report.addItem(countItem);
					countItem = new ExchangeReportItem(ExchangeReportItem.KEY.CONNECTION_LINK_COUNT,Report.STATE.OK,links.size());
					report.addItem(countItem);
					countItem = new ExchangeReportItem(ExchangeReportItem.KEY.TIME_TABLE_COUNT,Report.STATE.OK,0);
					report.addItem(countItem);
				}
                return areas;
			}
			else
			{
				return new ArrayList<StopArea>();
			}
		}
		finally
		{
			if (data != null)
			{
				data.close();
			}
			try 
			{
				zip.close();
			} 
			catch (IOException e) 
			{
				logger.warn("cannot close zip file");
			}

		}

	}


}
