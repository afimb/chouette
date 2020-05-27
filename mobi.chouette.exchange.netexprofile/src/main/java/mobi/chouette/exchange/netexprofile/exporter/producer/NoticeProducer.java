package mobi.chouette.exchange.netexprofile.exporter.producer;

import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.NOTICE_ASSIGNMENT;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Set;

import mobi.chouette.model.FootNoteAlternativeText;
import org.rutebanken.netex.model.AlternativeText;
import org.rutebanken.netex.model.AlternativeTextRefStructure;
import org.rutebanken.netex.model.AlternativeTexts_RelStructure;
import org.rutebanken.netex.model.MultilingualString;
import org.rutebanken.netex.model.Notice;
import org.rutebanken.netex.model.NoticeAssignment;
import org.rutebanken.netex.model.NoticeRefStructure;
import org.rutebanken.netex.model.VersionOfObjectRefStructure;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.netexprofile.ConversionUtil;
import mobi.chouette.exchange.netexprofile.exporter.ExportableNetexData;
import mobi.chouette.model.Footnote;
import mobi.chouette.model.NeptuneIdentifiedObject;

public class NoticeProducer extends NetexProducer {

	public static void addNoticeAndNoticeAssignments(Context context, ExportableNetexData exportableNetexData, Set<NoticeAssignment> destination,
													 Collection<Footnote> footnotes, NeptuneIdentifiedObject noticedObject) {

		for (Footnote footnote : footnotes) {
			Notice notice = netexFactory.createNotice();
			NetexProducerUtils.populateId(footnote, notice);

			if (!exportableNetexData.getSharedNotices().containsKey(notice.getId())) {
				notice.setText(ConversionUtil.getMultiLingualString(footnote.getLabel()));
				notice.setPublicCode(footnote.getCode());

				exportableNetexData.getSharedNotices().put(notice.getId(), notice);
			}
			NoticeRefStructure noticeRefStruct = netexFactory.createNoticeRefStructure();
			NetexProducerUtils.populateReference(notice, noticeRefStruct, false);

			VersionOfObjectRefStructure noticedObjectRef = netexFactory.createVersionOfObjectRefStructure();
			NetexProducerUtils.populateReference(noticedObject, noticedObjectRef, true);

			if (!footnote.getAlternativeTexts().isEmpty()) {
				AlternativeTexts_RelStructure alternativeTextsRelStructure = netexFactory.createAlternativeTexts_RelStructure();
				for (FootNoteAlternativeText footNoteAlternativeText : footnote.getAlternativeTexts()) {
					AlternativeText alternativeText = netexFactory.createAlternativeText();
					NetexProducerUtils.populateId(footNoteAlternativeText, alternativeText);
					MultilingualString multilingualString = netexFactory.createMultilingualString();
					multilingualString.setLang(footNoteAlternativeText.getLanguage());
					multilingualString.setValue(footNoteAlternativeText.getText());
					alternativeText.withText(multilingualString);
					alternativeTextsRelStructure.getAlternativeText().add(alternativeText);
				}
				notice.withAlternativeTexts(alternativeTextsRelStructure);
			}

			String noticeAssignmentId = NetexProducerUtils.createUniqueId(context, NOTICE_ASSIGNMENT);
			NoticeAssignment noticeAssignment = netexFactory.createNoticeAssignment().withVersion("1").withId(noticeAssignmentId)
					.withOrder(BigInteger.valueOf(destination.size() + 1)).withNoticeRef(noticeRefStruct).withNoticedObjectRef(noticedObjectRef);

			destination.add(noticeAssignment);
		}

	}

}
