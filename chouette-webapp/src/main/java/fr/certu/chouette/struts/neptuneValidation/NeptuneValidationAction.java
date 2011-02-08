package fr.certu.chouette.struts.neptuneValidation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.interceptor.CookiesAware;

import com.opensymphony.xwork2.Preparable;
import com.vividsolutions.jts.geom.Coordinate;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.manager.INeptuneManager;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.plugin.exchange.FormatDescription;
import fr.certu.chouette.plugin.exchange.ParameterValue;
import fr.certu.chouette.plugin.exchange.SimpleParameterValue;
import fr.certu.chouette.plugin.report.Report;
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
public class NeptuneValidationAction extends GeneriqueAction implements Preparable, CookiesAware{

	private static final Log LOGGER = LogFactory.getLog(NeptuneValidationAction.class);

	@Getter @Setter private File file;
	@Getter @Setter private String fileFileName;
	@Getter @Setter private INeptuneManager<Line> lineManager;
	@Getter @Setter private boolean validate;
	@Getter @Setter private List<Report> reports;
	@Getter @Setter private List<FormatDescription> formats;
	@Getter @Setter private boolean imported;
	@Getter @Setter private Report report;
	@Getter @Setter private Report reportValidation;
	@Getter @Setter private List<Line> lines;
	@Getter @Setter private Map<String, String> cookiesMap;

	//Validation parameters
	@Getter @Setter private float test3_1_MinimalDistance;
	@Getter @Setter private float test3_2_MinimalDistance;
	@Getter @Setter private String test3_2_Polygon;
	@Getter @Setter private float test3_7_MinimalDistance;
	@Getter @Setter private float test3_7_MaximalDistance;
	@Getter @Setter private float test3_8a_MinimalSpeed;
	@Getter @Setter private float test3_8a_MaximalSpeed;
	@Getter @Setter private float test3_8b_MinimalSpeed;
	@Getter @Setter private float test3_8b_MaximalSpeed;
	@Getter @Setter private float test3_8c_MinimalSpeed;
	@Getter @Setter private float test3_8c_MaximalSpeed;
	@Getter @Setter private float test3_8d_MinimalSpeed;
	@Getter @Setter private float test3_8d_MaximalSpeed;
	@Getter @Setter private float test3_9_MinimalSpeed;
	@Getter @Setter private float test3_9_MaximalSpeed;
	@Getter @Setter private float test3_10_MinimalDistance;
	@Getter @Setter private long test3_16c_MinimalTime;
	@Getter @Setter private long test3_16c_MaximalTime;


	@Override
	public void prepare() throws Exception {
		test3_2_Polygon=test3_2_Polygon.replace(" ", "\t");
	}

	public String execute(){

		session.remove("imported");
		return SUCCESS;
	}

	/**
	 * Neptune import
	 * @return
	 * @throws ChouetteException
	 * @throws IOException 
	 */
	@SuppressWarnings("unchecked")
	public String importNeptune() throws ChouetteException, IOException {
		//fileName = "The file name...";

		String result = null;
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
		ValidationParameters validationParameters =new ValidationParameters();

		//Making of Validation parameters

		validationParameters.setTest3_10_MinimalDistance(test3_10_MinimalDistance);
		validationParameters.setTest3_16c_MaximalTime(test3_16c_MaximalTime);
		validationParameters.setTest3_16c_MinimalTime(test3_16c_MinimalTime);
		validationParameters.setTest3_1_MinimalDistance(test3_1_MinimalDistance);
		validationParameters.setTest3_2_MinimalDistance(test3_2_MinimalDistance);
		validationParameters.setTest3_2_Polygon(getPolygonCoordinatesFromString(test3_2_Polygon));
		validationParameters.setTest3_7_MaximalDistance(test3_7_MaximalDistance);
		validationParameters.setTest3_7_MinimalDistance(test3_7_MinimalDistance);
		validationParameters.setTest3_8a_MaximalSpeed(test3_8a_MaximalSpeed);
		validationParameters.setTest3_8a_MinimalSpeed(test3_8a_MinimalSpeed);
		validationParameters.setTest3_8b_MaximalSpeed(test3_8b_MaximalSpeed);
		validationParameters.setTest3_8b_MinimalSpeed(test3_8b_MinimalSpeed);
		validationParameters.setTest3_8c_MaximalSpeed(test3_8c_MaximalSpeed);
		validationParameters.setTest3_8c_MinimalSpeed(test3_8c_MinimalSpeed);
		validationParameters.setTest3_8d_MaximalSpeed(test3_8d_MaximalSpeed);
		validationParameters.setTest3_8d_MinimalSpeed(test3_8d_MinimalSpeed);
		validationParameters.setTest3_9_MaximalSpeed(test3_9_MaximalSpeed);
		validationParameters.setTest3_9_MinimalSpeed(test3_9_MinimalSpeed);

		for(Line line : lines){
			//LOGGER.info("Line : "+line.toString("\t",89));

			reportValidation = lineManager.validate(null,line,validationParameters);

			if(reportValidation != null){
				LOGGER.info("Report "+reportValidation.toString());

				reports.add(reportValidation);
			}

		}

		return SUCCESS;
	}
	private void getReportItemDetails(List<ReportItem> reportItems){
		for(ReportItem reportItem : reportItems){
			LOGGER.info("ReportItem Origin "+reportItem.getOriginKey());
			LOGGER.info("ReportItem Message "+reportItem.getLocalizedMessage(getLocale())+" STATUS "+reportItem.getStatus().name());
			if(reportItem.getMessageArgs() != null){
				for(String arg : reportItem.getMessageArgs())
					LOGGER.info("ReportItem message arg "+arg);
			}

			if(reportItem.getItems() != null && reportItem.getItems().size()>0)
				getReportItemDetails(reportItem.getItems());
		}

	}

	private List<Coordinate> getPolygonCoordinatesFromString(String text){
		List<Coordinate> coordinates = new ArrayList<Coordinate>();

		String coordinateAsStringTab[] = text.split("\t");
		for(String coordinateAsString : coordinateAsStringTab){
			double x = Double.valueOf(coordinateAsString.split(",")[0]);
			double y = Double.valueOf(coordinateAsString.split(",")[1]);
			Coordinate coordinate = new Coordinate(x, y);
			coordinates.add(coordinate);
		}

		return coordinates;
	}
}
