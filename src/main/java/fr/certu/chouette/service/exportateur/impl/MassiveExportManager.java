package fr.certu.chouette.service.exportateur.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;

import chouette.schema.ChouettePTNetworkTypeType;
import chouette.schema.Timetable;
import fr.certu.chouette.modele.Ligne;
import fr.certu.chouette.modele.TableauMarche;
import fr.certu.chouette.service.database.IExportManager;
import fr.certu.chouette.service.database.IReseauManager;
import fr.certu.chouette.service.exportateur.IMassiveExportManager;
import fr.certu.chouette.service.validation.commun.TypeInvalidite;
import fr.certu.chouette.service.validation.commun.ValidationException;
import fr.certu.chouette.service.validation.util.MainSchemaProducer;
import fr.certu.chouette.service.xml.ILecteurFichierXML;

public class MassiveExportManager implements IMassiveExportManager {

	private IReseauManager networkManager;
	private IExportManager exportManager;
	private ILecteurFichierXML xmlFileReader;
    private String notificationEmailAddress;
    private String notificationSmtpServer;

	
	private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-dd-MM--HH-mm");
	private static final String NOTIFICATION_EMAIL_FROM = "noreply@chouette.mobi";
	public static final String EXPORT_DIR = "exports/";
	static{
		new File(EXPORT_DIR).mkdirs();
	}


	private static final ResourceBundle messages = ResourceBundle.getBundle(MassiveExportManager.class.getName());
	
	private static final Logger logger = Logger
			.getLogger(MassiveExportManager.class);
	
	@Override
	public void exportNetwork(long networkId, Date startDate, Date endDate,
			boolean excludeConnectionLinks) {
		File zipFile = new File(EXPORT_DIR+"Network_" + networkId + ".zip");

		List<Long> lineIds = getNetworkLineIds(networkId);

		try {
			exportLines(zipFile, lineIds, startDate, endDate,
					excludeConnectionLinks);
		} catch (IOException e) {
			logger.error(e);
		}
	}

	@Override
	public void exportNetworkInBackground(long networkId, Date startDate,
			Date endDate, boolean excludeConnectionLinks) {
		String zipFileName = "Network_" + networkId + "_"+ getFormattedDate() +".zip";
		List<Long> lineIds = getNetworkLineIds(networkId);
		Thread t = new Thread(new RunnableExport(zipFileName, lineIds,
				startDate, endDate, excludeConnectionLinks));
		t.start();
	}

	private String getFormattedDate() {
		return dateFormat.format(Calendar.getInstance().getTime());
	}

	public void exportLines(File zipFile, List<Long> lineIds, Date startDate,
			Date endDate, boolean excludeConnectionLinks) throws IOException {
		ZipOutputStream zipOutputStream = new ZipOutputStream(
				new FileOutputStream(zipFile));
		zipOutputStream.setLevel(ZipOutputStream.DEFLATED);

		MassiveExportReport report = new MassiveExportReport(startDate,
				endDate, excludeConnectionLinks);

		for (Long lineId : lineIds) {
			// time counter
			long timeCounter = System.currentTimeMillis();

			exportLine(zipOutputStream, lineId, startDate, endDate,
					excludeConnectionLinks, report);

			// timeCounter
			logger
					.info("time spent to export line "
							+ lineId
							+ " : "
							+ ((System.currentTimeMillis() - timeCounter) / 1000)
							+ "s");
		}
		writeInZipStream(zipOutputStream, report.toString(), "report.txt");
		zipOutputStream.close();
	}

	private void exportLine(ZipOutputStream zipOutputStream, long lineId,
			Date startDate, Date endDate, boolean excludeConnectionLinks,
			MassiveExportReport report) throws IOException {
		boolean isValid = true;
		ChouettePTNetworkTypeType line = exportManager
				.getExportParIdLigne(lineId);

		if (excludeConnectionLinks) {
			line.removeAllConnectionLink();
		}

		List<Timetable> outOfPeriodTimetables = new ArrayList<Timetable>();
		for (Timetable timetable : line.getTimetable()) {
			TableauMarche tm = new TableauMarche();
			tm.setTimetable(timetable);
			if (!tm.isTimetableInPeriod(startDate, endDate)) {
				outOfPeriodTimetables.add(timetable);
			}
		}

		for (Timetable timetable : outOfPeriodTimetables) {
			line.removeTimetable(timetable);
		}

		try {
			MainSchemaProducer mainSchemaProducer = new MainSchemaProducer();
			mainSchemaProducer.getASG(line);
			report.addLine(line);
		} catch (ValidationException e) {
			isValid = false;
			List<TypeInvalidite> categories = e.getCategories();
			if (categories != null) {
				for (TypeInvalidite category : categories) {
					Set<String> messages = e.getTridentIds(category);
					for (String message : messages) {
						logger.info(message);
						report.addError(line.getChouetteLineDescription()
								.getLine().getName()
								+ " " + message);
					}
				}
			}
		}
		writeLineInZipStream(zipOutputStream, line,
				getLineFileName(lineId, isValid));

	}

	private void writeLineInZipStream(ZipOutputStream zipOutputStream,
			ChouettePTNetworkTypeType readLine, String lineFileName)
			throws IOException, FileNotFoundException {
		File lineFile = File.createTempFile(lineFileName, null);
		xmlFileReader.ecrire(readLine, lineFile);
		zipOutputStream.putNextEntry(new ZipEntry(lineFileName));
		byte[] bytes = new byte[(int) lineFile.length()];
		FileInputStream fis = new FileInputStream(lineFile);
		fis.read(bytes);
		zipOutputStream.write(bytes);
		zipOutputStream.flush();
		lineFile.delete();
	}

	private void writeInZipStream(ZipOutputStream zipOutputStream,
			String content, String fileName)
			throws IOException, FileNotFoundException {
		File file = File.createTempFile(fileName, null);
		
		BufferedWriter bw = new BufferedWriter(new FileWriter(file));
		bw.write(content);
		bw.close();
		
		zipOutputStream.putNextEntry(new ZipEntry(fileName));
		byte[] bytes = new byte[(int) file.length()];
		FileInputStream fis = new FileInputStream(file);
		fis.read(bytes);
		zipOutputStream.write(bytes);
		zipOutputStream.flush();
		file.delete();
	}

	
	private List<Long> getNetworkLineIds(long networkId) {
		List<Ligne> lines = networkManager.getLignesReseau(networkId);
		List<Long> lineIds = new ArrayList<Long>();
		for (Ligne l : lines) {
			lineIds.add(l.getId());
		}
		return lineIds;
	}

	public void setNetworkManager(IReseauManager networkManager) {
		this.networkManager = networkManager;
	}

	public void setExportManager(IExportManager exportManager) {
		this.exportManager = exportManager;
	}

	public void setXmlFileReader(ILecteurFichierXML xmlFileReader) {
		this.xmlFileReader = xmlFileReader;
	}
	
	public String getNotificationEmailAddress() {
		return notificationEmailAddress;
	}

	public void setNotificationEmailAddress(String notificationEmailAddress) {
		this.notificationEmailAddress = notificationEmailAddress;
	}

	public void setNotificationSmtpServer(String notificationSmtpServer) {
		this.notificationSmtpServer = notificationSmtpServer;
	}

	private String getLineFileName(long lineId, boolean isValid) {
		StringBuilder sb = new StringBuilder();
		if (!isValid) {
			sb.append("INVALIDE_");
		}
		sb.append(lineId).append(".xml");
		return sb.toString();
	}

	private class RunnableExport implements Runnable {

		private final File zipFile;
		private final List<Long> lineIds;
		private final Date startDate;
		private final Date endDate;
		private final boolean excludeConnectionLinks;

		public RunnableExport(String zipFileName, List<Long> lineIds,
				Date startDate, Date endDate, boolean excludeConnectionLinks) {
			this.zipFile = new File(EXPORT_DIR+zipFileName);
			this.lineIds = lineIds;
			this.startDate = startDate;
			this.endDate = endDate;
			this.excludeConnectionLinks = excludeConnectionLinks;
		}

		@Override
		public void run() {
			exportLines();
		}

		public void exportLines() {
			ZipOutputStream zipOutputStream = null;
			MassiveExportReport report = new MassiveExportReport(startDate,
					endDate, excludeConnectionLinks);
			
			try {
				zipOutputStream = new ZipOutputStream(new FileOutputStream(
						zipFile));
				zipOutputStream.setLevel(ZipOutputStream.DEFLATED);

				for (Long lineId : lineIds) {
					// time counter
					long timeCounter = System.currentTimeMillis();

					exportLine(zipOutputStream, lineId, startDate, endDate,
							excludeConnectionLinks, report);

					// timeCounter
					logger.info("time spent to export line "
									+ lineId
									+ " : "
									+ ((System.currentTimeMillis() - timeCounter) / 1000)
									+ "s");
				}
				sendSuccessMail();
			} catch (Exception e) {
				logger.error(e.getStackTrace());
				sendFailureMail();
			} finally {
				try {
					writeInZipStream(zipOutputStream, report.toString(), "report.txt");
					zipOutputStream.close();
					logger.info("massive export in background ended !");
				} catch (IOException e) {
					logger.error(e.getStackTrace());
				}
			}
		}

		public void sendSuccessMail() {
			String subject = messages.getString("notification.email.subject.success");
			String content = messages.getString("notification.email.content.success").replace("#{filename}", zipFile.getName());
			try {
				sendMail(subject, content);
			} catch (Exception e) {
				logger.error(e.getStackTrace());
			}
		}

		public void sendFailureMail() {
			String subject = messages.getString("notification.email.subject.failure");
			String content = messages.getString("notification.email.content.failure").replace("#{filename}", zipFile.getName());
			try {
				sendMail(subject, content);
			} catch (Exception e) {
				logger.error(e.getStackTrace());
			}
		}
		
		private void sendMail(String subject, String content) throws Exception{
			// Setup mail server
	         Properties props = System.getProperties();
	         props.put("mail.smtp.host", notificationSmtpServer);

	         // Get a mail session
	         Session session = Session.getDefaultInstance(props, null);

	         // Define a new mail message
	         Message message = new MimeMessage(session);
	         message.setFrom(new InternetAddress(NOTIFICATION_EMAIL_FROM));
	         message.addRecipient(Message.RecipientType.TO, new InternetAddress(notificationEmailAddress));
	         message.setSubject(subject);

	         message.setText(content);


	         // Send the message
	         Transport.send(message);
		}

	}

}
