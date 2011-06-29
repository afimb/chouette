package fr.certu.chouette.struts.lineFilesComparisonService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.interceptor.ServletRequestAware;

import fr.certu.chouette.echange.comparator.ChouetteObjectState;
import fr.certu.chouette.echange.comparator.ComparisonReport;
import fr.certu.chouette.echange.comparator.IExchangeableLineComparator;
import fr.certu.chouette.service.commun.ServiceException;
import fr.certu.chouette.service.identification.IIdentificationManager;
import fr.certu.chouette.struts.GeneriqueAction;

public class LineFilesComparisonServiceAction extends GeneriqueAction implements ServletRequestAware {

	private static final long serialVersionUID = 2257063668664142870L;
	private static final Log logger = LogFactory.getLog(LineFilesComparisonServiceAction.class);

	private IIdentificationManager identificationManager;
	private HttpServletRequest request;

	/**
	 *  Available exchange formats to launch
	 */

	private Map<String, IExchangeableLineComparator> availableExchangeFormats ;

	/**
	 * @category Members linked to INPUT HTML Fields
	 **/
	private String title = null;
	private File sourceFile = null;
	private File targetFile = null;
	private String sourceFileContentType = null;
	private String targetFileContentType = null;
	private List<StringPair> exchangeFormats = null;
	private String exchangeFormat = null;
	private boolean enableVerboseMode = false;

	private List<ChouetteObjectState> objectStates = null;
	private ComparisonReport comparisonReport = null;

	/** ***** Constructor ***** **/
	public LineFilesComparisonServiceAction()
	{
		super();

	}

	private void initExchangeFormats()
	{
		exchangeFormats = new ArrayList<StringPair>();

		Set<String> formatKeys  = availableExchangeFormats.keySet();
		for (String formatKey : formatKeys) 
		{
			// TODO put a label in a localized property file
			exchangeFormats.add(new StringPair(formatKey, formatKey)); 
		}     
	}
	/**
	 * @param availableExchangeFormats the availableExchangeFormats to set
	 */
	public void setAvailableExchangeFormats(
			Map<String, IExchangeableLineComparator> availableExchangeFormats)
	{
		this.availableExchangeFormats = availableExchangeFormats;
		initExchangeFormats();
	}

	public String index()
	{
		//TODO no clear done, to investigate
		//clearErrorsAndMessages();
		setTitle(getText("index.title"));
		return SUCCESS;
	}

	/** ***** Compare Main Method ***** **/
	public String compare()
	{		

		// TODO all messages are keys, have to be stored with human readable message
		// as ihm parameter
		if (sourceFile == null || sourceFile.length() == 0)
		{
			addFieldError("sourceFile", getText("error.field.file"));
			return INPUT;
		}
		if (targetFile == null || targetFile.length() == 0)
		{
			addFieldError("targetFile", getText("error.field.file"));
			return INPUT;
		}

		if (exchangeFormat == null || exchangeFormat.length() == 0)
		{
			addFieldError("exchangeFormat",  getText("error.field.exchangeFormat"));
			return INPUT;
		}

		// Log info, print selected files names,
		// files necessary exist at this step, so nothing is done in never-visited catch
		try 
		{
			logger.info("Source file selected : " + sourceFile.getCanonicalPath());
			logger.info("Target file selected : " + targetFile.getCanonicalPath());
		}
		catch (Exception e)
		{

		}

		IExchangeableLineComparator comparator = availableExchangeFormats.get(exchangeFormat);
		if (comparator != null)
		{
			try
			{
				// Launch comparison 
				doComparison(comparator);
			}

			catch(Exception e) 
			{           
				return doExceptionTreatments(e);
			}

		}

		else
		{  
			addFieldError("exchangeFormat", "comparator.error.field.exchangeFormat");           
			return INPUT;
		}

		return "comparison-success";
	}

	private String doExceptionTreatments(Exception e)
	{
		// Log message completion
		List<Object> completionMessage = new ArrayList<Object>();
		completionMessage.add( getExceptionMessage(e));

		String logKey = null;
		String ihmKey = null;
		if (e instanceof ServiceException)
		{
			logKey = ihmKey = ((ServiceException)e).getCode().name();
			e = null; // won't transmit it to logger
		}
		else
		{
			logKey = ihmKey = "COMPARATOR_UNKNOWN_EXCEPTION";
		} 
		logKey += "_LOG";
		ihmKey += "_IHM";

		logger.error(getText(logKey, completionMessage), e);
		addActionError(getText(ihmKey, completionMessage));
		return ERROR;
	}

	private void doComparison(IExchangeableLineComparator comparator) throws Exception
	{
		objectStates = comparator.getObjectStateList();
		ComparisonReport comparisonReport= new ComparisonReport(objectStates);
		boolean comparisonResult = comparator.doComparison(sourceFile, targetFile);
		if (comparisonResult)
		{
			addActionMessage(getText("comparison.result.success"));
		}
		else
		{
			if (! enableVerboseMode)
			{
				request.setAttribute("comparisonReport", comparisonReport.getErrorItems());
			}
			addActionMessage(getText("comparison.result.failure"));
		}

		//set report into request

		if (enableVerboseMode)
		{
			request.setAttribute("comparisonReport", comparisonReport.getAllItems());
		}
		return;
	}

	public String downloadReport()
	{		
		clearErrorsAndMessages();
		addActionMessage(getText("error.notYetImplemented"));
		return "report-download-success";
	}

	/**
	 * @category Basic objects methods as getters and setters
	 */
	public void setIdentificationManager(IIdentificationManager identificationManager)
	{
		this.identificationManager = identificationManager;
	}

	public IIdentificationManager getIdentificationManager()
	{
		return identificationManager;
	}

	public File getSourceFile() 
	{
		return sourceFile;
	}

	public void setSourceFile(File sourceFile) 
	{
		this.sourceFile = sourceFile;
	}

	public void setTargetFile(File target)
	{
		this.targetFile = target;
	}

	public File getTargetFile()
	{
		return targetFile;
	}

	public void setServletRequest(HttpServletRequest request)
	{
		this.request = request;
	}

	public List<StringPair> getExchangeFormats()
	{
		return exchangeFormats;
	}

	public void setExchangeFormats(List<StringPair> exchangeFormat)
	{
		this.exchangeFormats = exchangeFormat;
	}

	public String getExchangeFormat() {
		return exchangeFormat;
	}

	public void setExchangeFormat(String exchangeFormat)
	{
		this.exchangeFormat = exchangeFormat;
	}

	public void setSourceFileContentType(String sourceFileContentType) 
	{
		this.sourceFileContentType = sourceFileContentType;
	}


	public String getSourceFileContentType() 
	{
		return sourceFileContentType;
	}

	public void setTargetFileContentType(String targetFileContentType) 
	{
		this.targetFileContentType = targetFileContentType;
	}


	public String getTargetFileContentType() 
	{
		return targetFileContentType;
	}


	public void setEnableVerboseMode(boolean enableVerboseMode) 
	{
		this.enableVerboseMode = enableVerboseMode;
	}

	public boolean isEnableVerboseMode() 
	{
		return enableVerboseMode;
	}
	/** inner StringPair class for exchange formats list**/
	//TODO Move into a toolkit
	public class StringPair 
	{
		private String key;
		private String value;

		public StringPair(String key, String value) 
		{
			setKey(key);
			setValue(value);
		}

		public String getKey() 
		{
			return key;
		}

		public void setKey(String key) 
		{
			this.key = key;
		}

		public String getValue() 
		{
			return value;
		}
		public void setValue(String value) 
		{
			this.value = value;
		}
	}


	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getTitle() 
	{
		return title;
	}

	public void setObjectStates(List<ChouetteObjectState> objectStates) 
	{
		this.objectStates = objectStates;
	}

	public List<ChouetteObjectState> getObjectStates() 
	{
		return objectStates;
	}

	public void setComparisonReport(ComparisonReport comparisonReport)
	{
		this.comparisonReport = comparisonReport;
	}

	public ComparisonReport getComparisonReport()
	{
		return comparisonReport;
	}
}
