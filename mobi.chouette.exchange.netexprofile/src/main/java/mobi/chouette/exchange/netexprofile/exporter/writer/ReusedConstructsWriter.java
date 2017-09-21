package mobi.chouette.exchange.netexprofile.exporter.writer;

import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducer.netexFactory;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.NOTICE_ASSIGNMENTS;

import java.util.Collection;

import javax.xml.bind.Marshaller;
import javax.xml.stream.XMLStreamWriter;

import org.rutebanken.netex.model.NoticeAssignment;

public class ReusedConstructsWriter {

	public static void writeNoticeAssignmentsElement(XMLStreamWriter writer, Collection<NoticeAssignment> noticeAssignments, Marshaller marshaller) {
		try {
			if (!noticeAssignments.isEmpty()) {
				writer.writeStartElement(NOTICE_ASSIGNMENTS);
				for (NoticeAssignment noticeAssignment : noticeAssignments) {
					marshaller.marshal(netexFactory.createNoticeAssignment(noticeAssignment), writer);
				}
				writer.writeEndElement();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
