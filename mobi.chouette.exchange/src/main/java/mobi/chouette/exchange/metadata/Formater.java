package mobi.chouette.exchange.metadata;


import org.joda.time.ReadablePartial;

public interface Formater
{
String format(Metadata.Period period);
String format(Metadata.Box box);
String format(Metadata.Resource resource);
String formatDate(ReadablePartial date);
}
