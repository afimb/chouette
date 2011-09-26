package fr.certu.chouette.struts.neptuneValidation;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.opensymphony.xwork2.Preparable;

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
@SuppressWarnings("unchecked")
public class NeptuneValidationAction extends GeneriqueAction implements Preparable,ServletResponseAware, ServletRequestAware{

	private static final long serialVersionUID = 8449003243520401472L;

	@Getter @Setter private File file;
	@Getter @Setter private String fileFileName;
	@Getter @Setter private INeptuneManager<Line> lineManager;
	@Getter @Setter private boolean validate = true;
	@Getter @Setter private List<FormatDescription> formats;
	@Getter @Setter private boolean imported;
	@Getter @Setter private Report report;
	@Getter @Setter private Report reportValidation;
	@Getter @Setter private List<Line> lines;
	@Getter @Setter private int cookieExpires;
	@Getter @Setter private ValidationParameters validationParam ;
	@Getter @Setter private ValidationParameters validationParamDefault ;

	// For access to the raw servlet request / response, eg for cookies
	@Getter @Setter protected HttpServletResponse servletResponse;
	@Getter @Setter protected HttpServletRequest servletRequest;

	@Getter @Setter private boolean save = false;


	@Override
	public void prepare() throws Exception {
		validationParam = new ValidationParameters();
	}


	public String execute(){
		session.clear();
		// Load from cookie if any
		loadFromCookie(validationParam);
		return SUCCESS;
	}
	private void copyProperties(ValidationParameters param1,ValidationParameters param2){
		try {
			BeanUtils.copyProperties(param1, param2);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Loading from cookie if any
	 */
	private void loadFromCookie(ValidationParameters validationParam){ 
		copyProperties(validationParam, validationParamDefault);
		if(servletRequest != null && servletRequest.getCookies() != null && servletRequest.getCookies().length>0)
			try {
				for(Cookie c : servletRequest.getCookies()) {
					String cookieName = c.getName();
					String cookieValue = c.getValue();

					if (cookieName.equals("test3_1_MinimalDistance"))
						validationParam.setTest3_1_MinimalDistance(Float.valueOf(cookieValue));
					if(cookieName.equals("test3_2_Polygon")){
						validationParam.setTest3_2_PolygonPoints(cookieValue);
					}
					if(cookieName.equals("test3_2_MinimalDistance"))
						validationParam.setTest3_2_MinimalDistance(Float.valueOf(cookieValue));
					if(cookieName.equals("test3_10_MinimalDistance"))
						validationParam.setTest3_10_MinimalDistance(Float.valueOf(cookieValue));
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
						validationParam.setTest3_8c_MinimalSpeed(Float.valueOf(cookieValue));

					if(cookieName.equals("test3_8d_MaximalSpeed"))
						validationParam.setTest3_8d_MaximalSpeed(Float.valueOf(cookieValue));

					if(cookieName.equals("test3_8d_MinimalSpeed"))
						validationParam.setTest3_8d_MinimalSpeed(Float.valueOf(cookieValue));

					if(cookieName.equals("test3_9_MaximalSpeed"))
						validationParam.setTest3_9_MaximalSpeed(Float.valueOf(cookieValue));
					if(cookieName.equals("test3_9_MinimalSpeed"))
						validationParam.setTest3_9_MinimalSpeed(Float.valueOf(cookieValue));
					if(cookieName.equals("test3_15_MinimalTime"))
						validationParam.setTest3_15_MinimalTime(Long.valueOf(cookieValue));
					if(cookieName.equals("test3_16_1_MaximalTime"))
						validationParam.setTest3_16_1_MaximalTime(Long.valueOf(cookieValue));
					if(cookieName.equals("test3_16_3a_MaximalTime"))
						validationParam.setTest3_16_3a_MaximalTime(Long.valueOf(cookieValue));
					if(cookieName.equals("test3_16_3b_MaximalTime"))
						validationParam.setTest3_16_3b_MaximalTime(Long.valueOf(cookieValue));

					if(cookieName.equals("test3_21a_MaximalSpeed"))
						validationParam.setTest3_21a_MaximalSpeed(Float.valueOf(cookieValue));
					if(cookieName.equals("test3_21a_MinimalSpeed"))
						validationParam.setTest3_21a_MinimalSpeed(Float.valueOf(cookieValue));
					if(cookieName.equals("test3_21b_MaximalSpeed"))
						validationParam.setTest3_21b_MaximalSpeed(Float.valueOf(cookieValue));
					if(cookieName.equals("test3_21b_MinimalSpeed"))
						validationParam.setTest3_21b_MinimalSpeed(Float.valueOf(cookieValue));
					if(cookieName.equals("test3_21c_MaximalSpeed"))
						validationParam.setTest3_21c_MaximalSpeed(Float.valueOf(cookieValue));
					if(cookieName.equals("test3_21c_MinimalSpeed"))
						validationParam.setTest3_21c_MaximalSpeed(Float.valueOf(cookieValue));
					if(cookieName.equals("test3_21d_MaximalSpeed"))
						validationParam.setTest3_21d_MaximalSpeed(Float.valueOf(cookieValue));
					if(cookieName.equals("test3_21d_MinimalSpeed"))
						validationParam.setTest3_21d_MinimalSpeed(Float.valueOf(cookieValue));

					if(cookieName.equals("projection_reference"))
						validationParam.setProjection_reference(cookieValue);
				}
			} catch (IllegalArgumentException e) {
				copyProperties(validationParam, validationParamDefault);
			}

	}

    private String importNeptune() throws ChouetteException, IOException {
        String result = ERROR;
        session.clear();
        if (file == null) {
            addActionError(getText("error.import.file.require"));
        } else if (file.length() == 0) {
            addActionError(getText("error.import.file.empty"));
        } else {
            formats = lineManager.getImportFormats(null);
            imported = importXmlFile(file);
            if (imported) {
                session.put("lines", lines);
                if (lines != null && !lines.isEmpty()) {
                    setImported(true);
                    result = SUCCESS;
                } else {
                    //no line to import
                    /*
                    boolean afficheMessage = true;
                    List<ReportItem> sheets = report.getItems();
                    if (sheets != null) {
                        sheets :
                        for (Report sheet : sheets) {
                            //addActionError("Sheet " + sheet.getOriginKey() + " : " + sheet.getLocalizedMessage()+ " : "+sheet.getStatus());
                            if (sheet.getStatus().equals(Report.STATE.ERROR) || sheet.getStatus().equals(Report.STATE.FATAL)) {
                                afficheMessage = false;
                                break sheets;
                            }
                            List<ReportItem> items = sheet.getItems();
                            if (items != null) {
                                for (Report item : items) {
                                    //addActionError("Item " + item.getOriginKey() + " : " + item.getLocalizedMessage()+ " : "+item.getStatus());
                                    if (item.getStatus().equals(Report.STATE.ERROR) || item.getStatus().equals(Report.STATE.FATAL)) {
                                        afficheMessage = false;
                                        break sheets;
                                    }
                                    List<ReportItem> sub_items = item.getItems();
                                    if (sub_items != null) {
                                        for (Report sub_item : sub_items) {
                                            //addActionError("Sub Item " + sub_item.getOriginKey() + " : " + sub_item.getLocalizedMessage()+ " : "+sub_item.getStatus());
                                            if (sub_item.getStatus().equals(Report.STATE.ERROR) || sub_item.getStatus().equals(Report.STATE.FATAL)) {
                                                afficheMessage = false;
                                                break sheets;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (afficheMessage)
                        addActionError(getText("error.import.file.failure"));
                     */
                    result = SUCCESS;
                }
            } else {
                // bad file format
                addActionError(getText("error.import.file.type"));
                result = ERROR;
            }
        }
        session.put("imported", imported);
        return result;
    }

    private boolean importXmlFile(File file) throws ChouetteException {
        if (!FilenameUtils.getExtension(fileFileName).toLowerCase().equals("xml")
                && !FilenameUtils.getExtension(fileFileName).toLowerCase().equals("zip")) {
            return false;
        }
        List<ParameterValue> parameters = new ArrayList<ParameterValue>();
        SimpleParameterValue simpleParameterValue = new SimpleParameterValue("inputFile");
        simpleParameterValue.setFilepathValue(file.getPath());
        parameters.add(simpleParameterValue);
        
        SimpleParameterValue simpleParameterValue2 = new SimpleParameterValue("validate");
        simpleParameterValue2.setBooleanValue(validate);
        parameters.add(simpleParameterValue2);

        SimpleParameterValue simpleParameterValue3 = new SimpleParameterValue("fileFormat");
        simpleParameterValue3.setStringValue(FilenameUtils.getExtension(fileFileName));
        parameters.add(simpleParameterValue3);
        
        ReportHolder reportHolder = new ReportHolder();

        lines = lineManager.doImport(null, "NEPTUNE", parameters, reportHolder);
        report = reportHolder.getReport();
        session.put("fileFileName", fileFileName);
        session.put("report", report);
        return true;
    }

    public String validation() throws ChouetteException, IOException {
        String res = null;
        if (file != null) {
            res = importNeptune();
        } else if (session.get("lines") != null) {
            lines = (List<Line>) session.get("lines");
            res = SUCCESS;
        } else {
            addActionError(getText("error.import.file.require"));
            return ERROR;
        }

        if (validate && session.get("lines") != null) {
            if (res.equals(SUCCESS)) {
                reportValidation = lineManager.validate(null, lines, validationParam);
                boolean isDefault = false;
                if (session.get("isDefault") != null) {
                    isDefault = (Boolean) session.get("isDefault");
                }
                if (!isDefault) {
                    // Save to cookie
                    saveCookie("test3_1_MinimalDistance", validationParam.getTest3_1_MinimalDistance());
                    saveCookie("test3_2_Polygon", validationParam.getTest3_2_PolygonPoints());
                    saveCookie("test3_10_MinimalDistance", validationParam.getTest3_10_MinimalDistance());

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
                    saveCookie("test3_15_MinimalTime", validationParam.getTest3_15_MinimalTime());
                    saveCookie("test3_16_1_MaximalTime", validationParam.getTest3_16_1_MaximalTime());
                    saveCookie("test3_16_3a_MaximalTime", validationParam.getTest3_16_3a_MaximalTime());
                    saveCookie("test3_16_3b_MaximalTime", validationParam.getTest3_16_3b_MaximalTime());

                    saveCookie("test3_21a_MaximalSpeed", validationParam.getTest3_21a_MaximalSpeed());
                    saveCookie("test3_21a_MinimalSpeed", validationParam.getTest3_21a_MinimalSpeed());

                    saveCookie("test3_21b_MaximalSpeed", validationParam.getTest3_21b_MaximalSpeed());
                    saveCookie("test3_21b_MinimalSpeed", validationParam.getTest3_21b_MinimalSpeed());

                    saveCookie("test3_21c_MaximalSpeed", validationParam.getTest3_21c_MaximalSpeed());
                    saveCookie("test3_21c_MinimalSpeed", validationParam.getTest3_21c_MinimalSpeed());

                    saveCookie("test3_21d_MaximalSpeed", validationParam.getTest3_21d_MaximalSpeed());
                    saveCookie("test3_21d_MinimalSpeed", validationParam.getTest3_21d_MinimalSpeed());

                    saveCookie("projection_reference", validationParam.getProjection_reference());
                }
            }
        }

        if (save) {
            lineManager.saveAll(null, lines, true, true);
            addActionMessage("successfully saved");
            return INPUT;
        }

        if (res.equals(INPUT) || res.equals(SUCCESS)) {
            res = LIST;
        } else {
            return res;
        }
        return res;
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
	public String defaultValue() throws IllegalAccessException, InvocationTargetException{
		BeanUtils.copyProperties(validationParam, validationParamDefault);
		addActionMessage(getText("neptune.field.restore.default.value.success"));
		session.put("isDefault", true);
		return SUCCESS;
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
		if(reportValidation != null){
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
		}

		//Import report
		if(report != null){
			for (ReportItem item1  : report.getItems()) {// Categories
				if(item1.getItems() != null){
					for (ReportItem item2 : item1.getItems()) {// fiche
						if(item2.getItems() != null){
							STATE status = item2.getStatus();
							switch (status){
							case UNCHECK : nbUNCHECK++; break;
							case OK :nbOK++; break;
							case WARNING :nbWARN++; break;
							case ERROR : nbERROR++; break;
							case FATAL : nbFATAL++; break;
							}	
						}		
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
