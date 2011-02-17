package fr.certu.chouette.struts.neptuneValidation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.opensymphony.xwork2.Preparable;
import com.vividsolutions.jts.geom.Coordinate;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.manager.INeptuneManager;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.plugin.exchange.FormatDescription;
import fr.certu.chouette.plugin.exchange.ParameterValue;
import fr.certu.chouette.plugin.exchange.SimpleParameterValue;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.Report.STATE;
import fr.certu.chouette.plugin.report.ReportHolder;
import fr.certu.chouette.plugin.report.ReportItem;
import fr.certu.chouette.plugin.validation.ValidationParameters;
import fr.certu.chouette.struts.GeneriqueAction;

/**
 * 
 * @author mamadou keira
 *
 */
@SuppressWarnings("serial")
public class NeptuneValidationAction extends GeneriqueAction implements Preparable,ServletResponseAware, ServletRequestAware{

	private static final Log LOGGER = LogFactory.getLog(NeptuneValidationAction.class);

	@Getter @Setter private File file;
	@Getter @Setter private String fileFileName;
	@Getter @Setter private INeptuneManager<Line> lineManager;
	@Getter @Setter private boolean validate;
	@Getter @Setter private List<FormatDescription> formats;
	@Getter @Setter private boolean imported;
	@Getter @Setter private Report report;
	@Getter @Setter private Report reportValidation;
	@Getter @Setter private List<Line> lines;
	@Getter @Setter private int cookieExpires;
	@Getter @Setter private ValidationParameters validationParam ;
	@Getter @Setter private ValidationParameters validationParamDefault ;
	@Setter private String polygonCoordinatesAsString;

	// For access to the raw servlet request / response, eg for cookies
	@Getter @Setter protected HttpServletResponse servletResponse;
	@Getter @Setter protected HttpServletRequest servletRequest;

	@Override
	public void prepare() throws Exception {
		validationParam = new ValidationParameters();
	}


	public String execute(){
		if(session.get("imported") != null)
			session.remove("imported");
		// Load from cookie if any
		loadFromCookie(validationParam);
		return SUCCESS;
	}

	/**
	 * Loading from cookie if any
	 */
	private void loadFromCookie(ValidationParameters validationParam){
		try {
			BeanUtils.copyProperties(validationParam, validationParamDefault);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

		if(servletRequest != null && servletRequest.getCookies() != null && servletRequest.getCookies().length>0)
			for(Cookie c : servletRequest.getCookies()) {
				String cookieName = c.getName();
				String cookieValue = c.getValue();
			
				if (cookieName.equals("test3_1_MinimalDistance"))
					validationParam.setTest3_1_MinimalDistance(Float.valueOf(cookieValue));

				if(cookieName.equals("test3_2_Polygon"))
					validationParam.setTest3_2_PolygonPoints(cookieValue);

				if(cookieName.equals("test3_2_MinimalDistance"))
					validationParam.setTest3_2_MinimalDistance(Float.valueOf(cookieValue));

				if(cookieName.equals("test3_10_MinimalDistance"))
					validationParam.setTest3_10_MinimalDistance(Float.valueOf(cookieValue));

				if(cookieName.equals("test3_16c_MaximalTime"))
					validationParam.setTest3_16c_MaximalTime(Long.valueOf(cookieValue));

				if(cookieName.equals("test3_16c_MinimalTime"))
					validationParam.setTest3_16c_MinimalTime(Long.valueOf(cookieValue));

				if(cookieName.equals("test3_7_MaximalDistance"))
					validationParam.setTest3_7_MaximalDistance(Float.valueOf(cookieValue));

				if(cookieName.equals("test3_7_MinimalDistance"))
					validationParam.setTest3_7_MinimalDistance(Float.valueOf(cookieValue));

				if(cookieName.equals("test3_8a_MaximalSpeed"))
					validationParam.setTest3_8a_MaximalSpeed(Float.valueOf(cookieValue));

				if(cookieName.equals("test3_8a_MinimalSpeed"))
					validationParam.setTest3_8a_MinimalSpeed(Float.valueOf(cookieValue));

				if(cookieName.equals("test3_8b_MaximalSpeed"))
					validationParam.setTest3_8b_MaximalSpeed(Float.valueOf(cookieValue));

				if(cookieName.equals("test3_8b_MinimalSpeed"))
					validationParam.setTest3_8b_MinimalSpeed(Float.valueOf(cookieValue));

				if(cookieName.equals("test3_8c_MaximalSpeed"))
					validationParam.setTest3_8c_MaximalSpeed(Float.valueOf(cookieValue));

				if(cookieName.equals("test3_8c_MinimalSpeed"))
					validationParam.setTest3_8c_MaximalSpeed(Float.valueOf(cookieValue));

				if(cookieName.equals("test3_8d_MaximalSpeed"))
					validationParam.setTest3_8d_MaximalSpeed(Float.valueOf(cookieValue));

				if(cookieName.equals("test3_8d_MinimalSpeed"))
					validationParam.setTest3_8d_MinimalSpeed(Float.valueOf(cookieValue));

				if(cookieName.equals("test3_9_MaximalSpeed"))
					validationParam.setTest3_9_MaximalSpeed(Float.valueOf(cookieValue));

				if(cookieName.equals("test3_9_MinimalSpeed"))
					validationParam.setTest3_9_MinimalSpeed(Float.valueOf(cookieValue));
			}
	}
	/**
	 * Neptune import
	 * @return
	 * @throws ChouetteException
	 * @throws IOException 
	 */
	@SuppressWarnings("unchecked")
	public String importNeptune() throws ChouetteException, IOException {
		String result = null;
		if(session.get("lines") != null)
			session.remove("lines");
		LOGGER.info("Import neptune XML file(s) ");

		if(file != null && file.length()>0){
			formats = lineManager.getImportFormats(null);
			for(FormatDescription formatDescription : formats)
				LOGGER.info(formatDescription.toString());

			LOGGER.info("File extension "+FilenameUtils.getExtension(fileFileName));
			if(FilenameUtils.getExtension(fileFileName).equals("xml"))
				//An XML file
				imported = importXmlFile(file);
			else
				//A zip file
				imported =importZip(file);

			if(imported){
				if(lines != null){
					setImported(true);
					session.put("imported", true);
					LOGGER.info("File successfully imported ");
					result =SUCCESS;	
				}
				else{
					addActionError(getText("error.import.file.failure"));
					result = INPUT;
				}
			}

		}else{
			addActionError(getText("error.import.file.require"));
			result = INPUT;
		}
		session.put("lines", lines);
		session.put("fileFileName", fileFileName);
		loadFromCookie(validationParam);
		return result;
	}

	private boolean importXmlFile(File file) throws ChouetteException{

		boolean result = false;
		List<ParameterValue> parameters = new ArrayList<ParameterValue>();
		SimpleParameterValue simpleParameterValue = new SimpleParameterValue("xmlFile");

		simpleParameterValue.setFilepathValue(file.getPath());
		parameters.add(simpleParameterValue);
		SimpleParameterValue simpleParameterValue2 = new SimpleParameterValue("validateXML");
		simpleParameterValue2.setBooleanValue(validate);
		parameters.add(simpleParameterValue2);	
		ReportHolder reportHolder = new ReportHolder();

		lines = lineManager.doImport(null,formats.get(0).getName(),parameters, reportHolder);
		if(lines != null){
			report = reportHolder.getReport();
			LOGGER.info("Report STATUS "+report.getStatus().name());
			getReportItemDetails(report.getItems());
			result = true;
		}

		return result;
	}

	/**
	 * import a zip file
	 * @param file
	 * @return
	 * @throws ChouetteException
	 */
	private boolean importZip(File file) throws ChouetteException{
		try{
			boolean result = true;
			ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(file));
			ZipEntry zipEntry = zipInputStream.getNextEntry();
			while (zipEntry != null) {
				LOGGER.info("The zip file size : "+zipEntry.getSize());
				byte[] bytes = new byte[4096];
				int len = zipInputStream.read(bytes);
				File tmp = new File(zipEntry.getName());
				FileOutputStream fos = new FileOutputStream(tmp);
				while (len > 0) {
					fos.write(bytes, 0, len);
					len = zipInputStream.read(bytes);
				}
				if(!importXmlFile(tmp)){
					result = false;
					break;
				}
				zipEntry = zipInputStream.getNextEntry();
			}
			return result;
		}
		catch (Exception e){
			addActionError(getExceptionMessage(e));
			return false;
		}
	}
	/**
	 * Neptune validation 
	 * @return
	 * @throws ChouetteException
	 */
	@SuppressWarnings("unchecked")
	public String validation() throws ChouetteException{
		lines = (List<Line>)session.get("lines");
		validationParam.setTest3_2_PolygonPoints(polygonCoordinatesAsString);
		reportValidation = lineManager.validate(null,lines,validationParam);

		if(reportValidation != null){
			LOGGER.info("Report "+reportValidation.getLocalizedMessage(getLocale()));
			getReportItemDetails(reportValidation.getItems());
		}		
		boolean isDefault = false;
		if(session.get("isDefault") != null)
			isDefault = (Boolean)session.get("isDefault");
		if(!isDefault){
			// Save to cookie
			saveCookie("test3_1_MinimalDistance", validationParam.getTest3_1_MinimalDistance());
			saveCookie("test3_2_Polygon", polygonCoordinatesAsString);
			saveCookie("test3_10_MinimalDistance",validationParam.getTest3_10_MinimalDistance());

			saveCookie("test3_16c_MaximalTime", validationParam.getTest3_16c_MaximalTime());
			saveCookie("test3_16c_MinimalTime", validationParam.getTest3_16c_MinimalTime());

			saveCookie("test3_2_MinimalDistance", validationParam.getTest3_2_MinimalDistance());

			saveCookie("test3_7_MaximalDistance", validationParam.getTest3_7_MaximalDistance());
			saveCookie("test3_7_MinimalDistance", validationParam.getTest3_7_MinimalDistance());

			saveCookie("test3_8a_MaximalSpeed", validationParam.getTest3_8a_MaximalSpeed());
			saveCookie("test3_8a_MinimalSpeed", validationParam.getTest3_8a_MinimalSpeed());

			saveCookie("test3_8b_MaximalSpeed", validationParam.getTest3_8b_MaximalSpeed());
			saveCookie("test3_8b_MinimalSpeed", validationParam.getTest3_8b_MinimalSpeed());

			saveCookie("test3_8c_MaximalSpeed", validationParam.getTest3_8c_MaximalSpeed());
			saveCookie("test3_8c_MinimalSpeed", validationParam.getTest3_8c_MinimalSpeed());

			saveCookie("test3_8d_MaximalSpeed", validationParam.getTest3_8d_MaximalSpeed());
			saveCookie("test3_8d_MinimalSpeed", validationParam.getTest3_8d_MinimalSpeed());

			saveCookie("test3_9_MaximalSpeed", validationParam.getTest3_9_MaximalSpeed());
			saveCookie("test3_9_MinimalSpeed", validationParam.getTest3_9_MinimalSpeed());
		}
		

		//Adding validation parameters values in a session scope
		session.put("validationParam", validationParam);
		session.put("polygonCoordinatesAsString", polygonCoordinatesAsString);

		return LIST;
	}

	/**
	 * Saving to cookie
	 * @param name
	 * @param value
	 */
	private void saveCookie(String name, Object value){
		Cookie cookie = new Cookie(name, String.valueOf(value));
		int month = 60*60*24*30;
		cookie.setMaxAge(month*cookieExpires);
		servletResponse.addCookie(cookie);
	}

	/**
	 * Restore the validation parameters default values
	 * @return
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	@SuppressWarnings("unchecked")
	public String defaultValue() throws IllegalAccessException, InvocationTargetException{
		BeanUtils.copyProperties(validationParam, validationParamDefault);
		addActionMessage(getText("neptune.field.restore.default.value.success"));
		session.put("isDefault", true);
		return SUCCESS;
	}

	private void getReportItemDetails(List<ReportItem> reportItems){
		for(ReportItem reportItem : reportItems){
			LOGGER.info("\tReportItem Message "+reportItem.getLocalizedMessage(getLocale())+" STATUS "+reportItem.getStatus().name());
			if(reportItem.getMessageArgs() != null){
				for(String arg : reportItem.getMessageArgs())
					LOGGER.info("\tReportItem message arg "+arg);
			}

			if(reportItem.getItems() != null && reportItem.getItems().size()>0)
				getReportItemDetails(reportItem.getItems());
		}
	}
	/**
	 * Get the polygon coordinates from a string
	 * @param text
	 * @return
	 */
	public String getPolygonCoordinatesAsString(){
		List<Coordinate> coordinates = validationParam.getTest3_2_Polygon();
		String coodinatesAsString = "";
		for(Coordinate coordinate : coordinates){
			coodinatesAsString =coodinatesAsString.concat(coordinate.x+","+coordinate.y+"\t");
		}
		return coodinatesAsString;
	}
	/**
	 * 
	 * @param report
	 * @return
	 */
	public Map<STATE, Integer> getCountMap(){
		Map<STATE, Integer> countMap = new TreeMap<Report.STATE, Integer>();
		int nbUNCHECK = 0;
		int nbOK = 0;
		int nbWARN = 0;
		int nbERROR = 0;
		int nbFATAL = 0;
		for (ReportItem item1  : reportValidation.getItems()) // Categories
		{
			for (ReportItem item2 : item1.getItems()) // fiche
			{
				for (ReportItem item3 : item2.getItems()) //test
				{
					STATE status = item3.getStatus();
					switch (status)
					{
					case UNCHECK : 
						nbUNCHECK++;						
						break;
					case OK : 
						nbOK++;						
						break;
					case WARNING : 
						nbWARN++; 						
						break;
					case ERROR : 
						nbERROR++;	
						break;
					case FATAL : 
						nbFATAL++;		
						break;
					}
				}
			}
		}
		countMap.put(STATE.OK, nbOK);
		countMap.put(STATE.WARNING, nbWARN);
		countMap.put(STATE.ERROR, nbERROR);
		countMap.put(STATE.FATAL, nbFATAL);
		countMap.put(STATE.UNCHECK, nbUNCHECK);
		return countMap;
	}
}
