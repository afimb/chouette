package mobi.chouette.exchange.netexprofile.exporter.producer;

import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.NOTICE_ASSIGNMENT;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Set;

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

		int i = 0;
		for (Footnote footnote : footnotes) {
			i++;
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

			String noticeAssignmentId = NetexProducerUtils.createUniqueId(context, NOTICE_ASSIGNMENT);
			NoticeAssignment noticeAssignment = netexFactory.createNoticeAssignment().withVersion("1").withId(noticeAssignmentId)
					.withOrder(BigInteger.valueOf(i + 1)).withNoticeRef(noticeRefStruct).withNoticedObjectRef(noticedObjectRef);

			destination.add(noticeAssignment);
		}

	}

}
