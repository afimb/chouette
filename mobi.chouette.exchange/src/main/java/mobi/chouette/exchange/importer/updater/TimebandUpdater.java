package mobi.chouette.exchange.importer.updater;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import mobi.chouette.common.Context;
import mobi.chouette.dao.TimebandDAO;
import mobi.chouette.model.Timeband;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

@Stateless(name = TimebandUpdater.BEAN_NAME)
public class TimebandUpdater implements Updater<Timeband> {

	public static final String BEAN_NAME = "TimebandUpdater";

	@EJB 
	private TimebandDAO timebandDAO;

	@Override
	public void update(Context context, Timeband oldValue, Timeband newValue) throws Exception {
		if (newValue.isSaved()) {
			return;
		}
		newValue.setSaved(true);

		if (newValue.getObjectId() != null
				&& !newValue.getObjectId().equals(oldValue.getObjectId())) {
			oldValue.setObjectId(newValue.getObjectId());
		}
		if (newValue.getObjectVersion() != null
				&& !newValue.getObjectVersion().equals(
						oldValue.getObjectVersion())) {
			oldValue.setObjectVersion(newValue.getObjectVersion());
		}
		if (newValue.getCreationTime() != null
				&& !newValue.getCreationTime().equals(
						oldValue.getCreationTime())) {
			oldValue.setCreationTime(newValue.getCreationTime());
		}
		if (newValue.getCreatorId() != null
				&& !newValue.getCreatorId().equals(oldValue.getCreatorId())) {
			oldValue.setCreatorId(newValue.getCreatorId());
		}
		if (newValue.getName() != null
				&& !newValue.getName().equals(oldValue.getName())) {
			oldValue.setName(newValue.getName());
		}
		if (newValue.getStartTime() != null
				&& !newValue.getStartTime().equals(oldValue.getStartTime())) {
			oldValue.setStartTime(newValue.getStartTime());
		}
		if (newValue.getEndTime() != null
				&& !newValue.getEndTime().equals(oldValue.getEndTime())) {
			oldValue.setEndTime(newValue.getEndTime());
		}
		if (oldValue.getName() == null || oldValue.getName().isEmpty()) {
			DateTimeFormatter timeFormatter = DateTimeFormat.forPattern("HH:MM");
			oldValue.setName(timeFormatter.print(oldValue.getStartTime())+" - "+timeFormatter.print(oldValue.getEndTime()));
		}
		if (timebandDAO.findByObjectId(oldValue.getObjectId()) == null)
			timebandDAO.create(oldValue);
		else
			timebandDAO.update(oldValue);
	}
}
