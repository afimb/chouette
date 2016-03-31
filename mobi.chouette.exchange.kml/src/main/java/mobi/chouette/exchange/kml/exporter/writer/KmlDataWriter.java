package mobi.chouette.exchange.kml.exporter.writer;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;

import mobi.chouette.exchange.kml.exporter.KmlData;
import mobi.chouette.exchange.kml.exporter.KmlData.KmlItem;
import mobi.chouette.exchange.kml.exporter.KmlData.KmlPoint;

public class KmlDataWriter extends AbstractWriter {

	public static void write(Writer writer, KmlData data) throws IOException, DatatypeConfigurationException {
		writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		writer.write("<kml  xmlns=\"http://www.opengis.net/kml/2.2\">\n");
		writer.write("  <Document>\n");
		if (nonEmpty(data.getName()))
			writer.write("    <name>" + toXml(data.getName()) + "</name>\n");
		if (!data.getExtraData().isEmpty()) {
			writer.write("        <ExtendedData>\n");
			for (Object extra : data.getExtraData().keyList()) {
				writer.write("          <Data name=\"" + extra + "\">\n");
				writer.write("            <value>" + toXml(data.getExtraData().get(extra)) + "</value>\n");
				writer.write("          </Data>\n");
			}
			writer.write("        </ExtendedData>\n");
		}
		for (KmlItem item : data.getItems().values()) {
			writer.write("        <Placemark id=\"" + item.getId() + "\" >\n");
			for (Object attribute : item.getAttributes().keyList()) {
				writer.write("            <" + attribute + ">" + toXml(item.getAttributes().get(attribute)) + "</"
						+ attribute + ">\n");
			}
			if (!item.getExtraData().isEmpty()) {
				writer.write("        <ExtendedData>\n");
				for (Object extra : item.getExtraData().keyList()) {
					writer.write("          <Data name=\"" + extra + "\">\n");
					writer.write("            <value>" + toXml(item.getExtraData().get(extra)) + "</value>\n");
					writer.write("          </Data>\n");
				}
				writer.write("        </ExtendedData>\n");
			}
			if (item.getPoint() != null) {
				writer.write("        <Point>\n");
				writer.write("          <coordinates>" + item.getPoint().longitude + "," + item.getPoint().latitude
						+ "</coordinates>\n");
				writer.write("        </Point>\n");
			}
			if (item.getLineString() != null) {
				writer.write("        <LineString>\n");
				writer.write("          <coordinates>\n");
				for (KmlPoint point : item.getLineString()) {
					writer.write("            " + point.longitude + "," + point.latitude + "\n");
				}
				writer.write("         </coordinates>\n");
				writer.write("        </LineString>\n");
			}
			if (item.getMultiLineString() != null) {
				writer.write("        <MultiGeometry>\n");
				for (List<KmlPoint> line : item.getMultiLineString()) {
					writer.write("          <LineString>\n");
					writer.write("            <coordinates>\n");
					for (KmlPoint point : line) {
						writer.write("              " + point.longitude + "," + point.latitude + "\n");
					}
					writer.write("           </coordinates>\n");
					writer.write("          </LineString>\n");
				}
				writer.write("        </MultiGeometry>\n");

			}
			writer.write("        </Placemark>\n");
		}
		writer.write("    </Document>\n");
		writer.write("  </kml>\n");
	}

}
