package mobi.chouette.exchange.metadata;

import java.util.Calendar;

public interface Formater
{
String format(Metadata.Period period);
String format(Metadata.Box box);
String format(Metadata.Resource resource);
String formatDate(Calendar date);
}
