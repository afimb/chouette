package fr.certu.chouette.struts;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.interceptor.ServletRequestAware;

import org.springframework.context.ApplicationContext;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import fr.certu.chouette.manager.SingletonManager;
//TODO : see exception politic with zak
import fr.certu.chouette.service.commun.CodeIncident;
import fr.certu.chouette.service.commun.ServiceException;
import fr.certu.chouette.service.identification.IIdentificationManager;

import fr.certu.chouette.echange.comparator.ChouetteObjectState;
import fr.certu.chouette.echange.comparator.ComparisonReport;
import fr.certu.chouette.echange.comparator.IExchangeableLineComparator;

@SuppressWarnings("serial")
public class LineFileComparisonServiceAction extends GeneriqueAction implements ServletRequestAware {

	//todo move that in the generiqueActio
	private static final Log logger = LogFactory.getLog(LineFileComparisonServiceAction.class);

	//TODO test Logger.getLog(...)
	private IIdentificationManager identificationManager;
	private HttpServletRequest request;

	/**
	 *  Available exchange formats, code label map
	 *  TODO dynamic list (linked to installed extension)
	 *  or set at least as parameters or ...
	 */
	private final static HashMap<String, String> AVAILABLE_EXCHANGE_FORMATS = new HashMap<String, String>();
	static
	{
		AVAILABLE_EXCHANGE_FORMATS.put("Chouette", "Chouette");
		AVAILABLE_EXCHANGE_FORMATS.put("Amivif", "Amivif");
	}

	private final static HashMap<String, String> EXCHANGE_FORMAT_BEAN_COMPARATOR_MAP = new HashMap<String, String>();
	public static HashMap<String, String> getEXCHANGE_FORMAT_BEAN_COMPARATOR_MAP() {
		return EXCHANGE_FORMAT_BEAN_COMPARATOR_MAP;
	}


	static
	{
		//EXCHANGE_FORMAT_BEAN_COMPARATOR_MAP.put("Chouette", "ExchangeableLineComparatorChouette");
		EXCHANGE_FORMAT_BEAN_COMPARATOR_MAP.put("Amivif", "ExchangeableAmivifLineComparator");
	}

	// Defaut comparison format and verbose mode
	// TODO : set as application parameters
	private static String DEFAULT_FORMAT = "Chouette";
	private static boolean ENABLE_VERBOSE_MODE = false;


	/**
	 * @category Members linked to INPUT HTML Fields
	 **/
	private String title = null;
	private File sourceFile = null;
	private File targetFile = null;
	private  String sourceFileContentType = null;
	private  String targetFileContentType = null;
	private List<StringPair> exchangeFormats = null;
	private String exchangeFormat = null;
	private boolean enableVerboseMode = false;

	private List<ChouetteObjectState> objectStates = null;
	private ComparisonReport comparisonReport = null;

	/** ***** Constructor ***** **/
	public LineFileComparisonServiceAction()
	{
		super();
		logger.debug("DEBUG TEST OK");

		// Initialization of available formats view list
		initExchangeFormats();
		//TODO set all that correctly with properties !
		exchangeFormat = DEFAULT_FORMAT;
		this.enableVerboseMode = ENABLE_VERBOSE_MODE;
	}

	private void initExchangeFormats()
	{
		exchangeFormats = new ArrayList<StringPair>();

		Set<String> formatKeys  = getAvailableFormat().keySet();
		for (String formatKey : formatKeys)
		{
			exchangeFormats.add(new StringPair(formatKey, getAvailableFormat().get(formatKey)));
		}
	}

	public String index()
	{
		//TODO no clear done, to investigate
		clearErrorsAndMessages();
		logger.debug("comparator::indexAction");
		setTitle(getText("comparator.action.title.index"));
		return SUCCESS;
	}

	/** ***** Compare Main Method ***** **/
	public String compare()
	{
		logger.debug("compare::mark 1");
		//TODO remove when chouette comparator "profile" will be complete !
		setExchangeFormat("Amivif");

		//TODO a field to switch mode
		//setEnableVerboseMode(true);

		//TODO no clear done, to investigate
		clearErrorsAndMessages();

		// TODO all messages are keys, have to be stored with human readable message
		// as ihm parameter
		if (sourceFile == null || sourceFile.length() == 0)
		{
			addFieldError("sourceFile", getText("comparator.error.field.file"));
			return INPUT;
		}
		if (targetFile == null || targetFile.length() == 0)
		{
			addFieldError("targetFile", getText("comparator.error.field.file"));
			return INPUT;
		}
		if (exchangeFormat == null || exchangeFormat.length() == 0)
		{
			addFieldError("exchangeFormat",  getText("comparator.error.field.exchangeFormat"));
			return INPUT;
		}

		logger.debug("compare::mark 2");
		ApplicationContext applicationContext = SingletonManager.getApplicationContext();
		HashMap<String, String> availableFormats = getAvailableFormat();
		boolean foundFormat = false;
		Iterator<Entry<String, String>> iter = availableFormats.entrySet().iterator();

		logger.debug("compare::mark 3");
		while (iter.hasNext())
		{
			Entry<String, String> format = iter.next();
			//La clé de la HashMap
			if (format.getKey().equals(exchangeFormat))
			{
				logger.debug("compare::mark 4");
				foundFormat = true;
				String beanKey = "Exchangeable" + format.getValue() + "LineComparator";
				logger.debug("compare::beanKey : " + beanKey);
				boolean completeIhmMessage = false;

				IExchangeableLineComparator comparator = null;
				try
				{
					comparator = (IExchangeableLineComparator)applicationContext.getBean(beanKey);
				}
				catch(Exception e)
				{
					logger.debug("compare::mark 5");
					String errorMessageCompletion = "Invalid Spring Bean Referenced by : " + beanKey;
					ServiceException se = new ServiceException(CodeIncident.COMPARATOR_UNVAILABLE_RESOURCE, errorMessageCompletion);
					return doExceptionTreatments(se, completeIhmMessage);
				}

				try
				{
					// Launch comparison
					logger.debug("compare::mark 6");
					doComparison(comparator);
				}
				catch(ServiceException e)
				{
					logger.debug("compare::mark 7");
					if (e.getCode().equals(CodeIncident.COMPARATOR_DUPLICATED_KEY))
					{
						completeIhmMessage = true;
					}
					return doExceptionTreatments(e, completeIhmMessage);

				}
				catch(Exception e)
				{
					logger.debug("compare::mark 8");
					return doExceptionTreatments(e, completeIhmMessage);
				}
			}//end if
		}//end while

		if(! foundFormat)
		{
			logger.debug("compare::mark 9");
			addFieldError("exchangeFormat", "comparator.error.field.exchangeFormat");
			return INPUT;
		}
		logger.debug("compare::mark success");
		//Build action/view title
		ArrayList<Object> completionMessage = new ArrayList<Object>();
		completionMessage.add(exchangeFormat);
		setTitle(getText("comparator.action.title.reporting", completionMessage));
		return SUCCESS;
	}

	private String doExceptionTreatments(Exception e, boolean completeIhmMessage)
	{
		logger.debug("compare::mark 10");
		// Log message completion
		List<Object> completionMessage = new ArrayList<Object>();
		completionMessage.add(e.getMessage());

		// Possible ihm message completion
		List<Object> ihmCompletionMessage = null;

		String logKey = null;
		String ihmKey = null;
		if (e instanceof ServiceException)
		{
			logKey = ((ServiceException)e).getCode().name();
			e = null; // isn't transmit to logger
		}
		else
		{
			logKey = ihmKey = "COMPARATOR_UNKNOWN_EXCEPTION";
		}
		logKey += "_LOG";
		ihmKey += "_IHM";

		// add origine message to ihm message if required
		if (completeIhmMessage)
		{
			ihmCompletionMessage = completionMessage;
		}

		logger.error(getText(logKey, completionMessage), e);
		addActionError(getText(ihmKey, ihmCompletionMessage));
		return ERROR;
	}

	private void doComparison(IExchangeableLineComparator comparator) throws Exception
	{

		logger.debug("compare::mark 11");
		objectStates = comparator.getObjectStateList();
		ComparisonReport comparisonReport= new ComparisonReport(objectStates);
		boolean comparisonResult = comparator.doComparison(sourceFile, targetFile);
		if (comparisonResult)
		{
			logger.debug("compare::mark 12");
			addActionMessage(getText("comparator.result.success"));
		}
		else
		{
			logger.debug("compare::mark 13");
			if (! enableVerboseMode)
			{
				request.setAttribute("comparisonReport", comparisonReport.getErrorItems());
			}
			addActionMessage(getText("comparator.result.failure"));
		}

		//set report into request

		if (enableVerboseMode)
		{
			request.setAttribute("comparisonReport", comparisonReport.getAllItems());
		}
		logger.debug("compare::mark 14");
		return;
	}

	public String downloadReport()
	{
		logger.debug("Comparator::downReportAction");
		clearErrorsAndMessages();
		addActionMessage(getText("error.notYetImplemented"));
		return SUCCESS;
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

	public File getSourceFile() {
		return sourceFile;
	}

	public void setSourceFile(File sourceFile) {
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

	public void setSourceFileContentType(String sourceFileContentType) {
		this.sourceFileContentType = sourceFileContentType;
	}


	public String getSourceFileContentType() {
		return sourceFileContentType;
	}

	public void setTargetFileContentType(String targetFileContentType) {
		this.targetFileContentType = targetFileContentType;
	}


	public String getTargetFileContentType() {
		return targetFileContentType;
	}


	public void setEnableVerboseMode(boolean enableVerboseMode) {
		this.enableVerboseMode = enableVerboseMode;
	}

	public boolean isEnableVerboseMode() {
		return enableVerboseMode;
	}
	/** inner StringPair class for exchange formats list**/
	//TODO Move into a toolkit
	public class StringPair {
		private String key;
		private String value;

		public StringPair(String key, String value) {
			setKey(key);
			setValue(value);
		}

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
	}

	/** Static basic methods on private static fields **/
	public static HashMap<String, String> getAvailableFormat()
	{
		return AVAILABLE_EXCHANGE_FORMATS;
	}


	public static String getDefaultFormat()
	{
		return DEFAULT_FORMAT;
	}


	public static boolean SetDefaultFormat(String defaultFormat)
	{
		if (getAvailableFormat().containsKey(defaultFormat))
		{
			DEFAULT_FORMAT = defaultFormat;
			return true;
		}
		return false;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public void setObjectStates(List<ChouetteObjectState> objectStates) {
		this.objectStates = objectStates;
	}

	public List<ChouetteObjectState> getObjectStates() {
		return objectStates;
	}

	public void setComparisonReport(ComparisonReport comparisonReport) {
		this.comparisonReport = comparisonReport;
	}

	public ComparisonReport getComparisonReport() {
		return comparisonReport;
	}
}
