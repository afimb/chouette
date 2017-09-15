package mobi.chouette.exchange.importer.updater;

import java.util.Collection;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import mobi.chouette.common.CollectionUtil;
import mobi.chouette.common.Context;
import mobi.chouette.common.Pair;
import mobi.chouette.dao.FootnoteDAO;
import mobi.chouette.dao.StopPointDAO;
import mobi.chouette.model.Footnote;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.VehicleJourneyAtStop;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

@Stateless(name = VehicleJourneyAtStopUpdater.BEAN_NAME)
public class VehicleJourneyAtStopUpdater implements
		Updater<VehicleJourneyAtStop> {

	public static final String BEAN_NAME = "VehicleJourneyAtStopUpdater";

	@EJB 
	private StopPointDAO stopPointDAO;

	@EJB
	private FootnoteDAO footnoteDAO;

	@EJB(beanName = FootnoteUpdater.BEAN_NAME)
	private Updater<Footnote> footnoteUpdater;

	@Override
	public void update(Context context, VehicleJourneyAtStop oldValue,
			VehicleJourneyAtStop newValue) throws Exception { 
		
		Referential cache = (Referential) context.get(CACHE);

		
		// The list of fields to sunchronize with LineRegisterCommand.write(StringWriter buffer, VehicleJourney vehicleJourney, StopPoint stopPoint,
		//    VehicleJourneyAtStop vehicleJourneyAtStop)

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
	
		if (newValue.getArrivalTime() != null
				&& !newValue.getArrivalTime().equals(oldValue.getArrivalTime())) {
			oldValue.setArrivalTime(newValue.getArrivalTime());
		}
		if (newValue.getDepartureTime() != null
				&& !newValue.getDepartureTime().equals(
						oldValue.getDepartureTime())) {
			oldValue.setDepartureTime(newValue.getDepartureTime());
		}
		
		if (newValue.getArrivalDayOffset() != oldValue.getArrivalDayOffset()) {
			oldValue.setArrivalDayOffset(newValue.getArrivalDayOffset());
		}
		if (newValue.getDepartureDayOffset() != oldValue.getDepartureDayOffset()) {
			oldValue.setDepartureDayOffset(newValue.getDepartureDayOffset());
		}
		
		// if (newValue.getElapseDuration() != null
		// 		&& !newValue.getElapseDuration().equals(
		// 				oldValue.getElapseDuration())) {
		// 	oldValue.setElapseDuration(newValue.getElapseDuration());
		// }
		// if (newValue.getHeadwayFrequency() != null
		// 		&& !newValue.getHeadwayFrequency().equals(
		// 				oldValue.getHeadwayFrequency())) {
		// 	oldValue.setHeadwayFrequency(newValue.getHeadwayFrequency());
		// }

		// StopPoint
		if (oldValue.getStopPoint() == null
				|| !oldValue.getStopPoint().equals(newValue.getStopPoint())) {
			StopPoint stopPoint = stopPointDAO.findByObjectId(newValue
					.getStopPoint().getObjectId());
			if (stopPoint != null) {
				oldValue.setStopPoint(stopPoint);
			}
		}
		updateFootnotes(context,oldValue,newValue,cache);
//		monitor.stop();
	}
	
	private void updateFootnotes(Context context, VehicleJourneyAtStop oldValue, VehicleJourneyAtStop newValue, Referential cache) throws Exception {
		Collection<Footnote> addedFootnote = CollectionUtil.substract(newValue.getFootnotes(),
				oldValue.getFootnotes(), NeptuneIdentifiedObjectComparator.INSTANCE);
		List<Footnote> footnotes = null;
		for (Footnote item : addedFootnote) {
			Footnote footnote = cache.getFootnotes().get(item.getObjectId());
			if (footnote == null) {
				if (footnotes == null) {
					footnotes = footnoteDAO.findByObjectId(UpdaterUtils.getObjectIds(addedFootnote));
					for (Footnote object : footnotes) {
						cache.getFootnotes().put(object.getObjectId(), object);
					}
				}
				footnote = cache.getFootnotes().get(item.getObjectId());
			}
			if (footnote == null) {
				footnote = ObjectFactory.getFootnote(cache, item.getObjectId());
			}
			oldValue.getFootnotes().add(footnote);
		}

		Collection<Pair<Footnote, Footnote>> modifiedFootnote = CollectionUtil.intersection(
				oldValue.getFootnotes(), newValue.getFootnotes(),
				NeptuneIdentifiedObjectComparator.INSTANCE);
		for (Pair<Footnote, Footnote> pair : modifiedFootnote) {
			footnoteUpdater.update(context, pair.getLeft(), pair.getRight());
		}

		Collection<Footnote> removedFootnote = CollectionUtil.substract(oldValue.getFootnotes(),
				newValue.getFootnotes(), NeptuneIdentifiedObjectComparator.INSTANCE);
		for (Footnote Footnote : removedFootnote) {
			oldValue.getFootnotes().remove(Footnote);
		}

	}

}
