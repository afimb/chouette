package fr.certu.chouette.export.metadata.writer;

import java.util.Calendar;

import fr.certu.chouette.export.metadata.model.Metadata;

public interface Formater
{
String format(Metadata.Period period);
String format(Metadata.Box box);
String format(Metadata.Resource resource);
String formatDate(Calendar date);
}
