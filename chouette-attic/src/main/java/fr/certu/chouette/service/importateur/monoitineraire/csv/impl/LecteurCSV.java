package fr.certu.chouette.service.importateur.monoitineraire.csv.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;

public class LecteurCSV {

	private final char separateur = ';';
	private final String encodage = "ISO-8859-1";
	private final char texteSeparateur = '"';
	
	
	
	@SuppressWarnings("unchecked")
	//public List<String[]> lire(File fichier) throws IOException {
	public List<String[]> lire(File fichier) throws Exception {
		CSVReader cr = new CSVReader(new InputStreamReader(new FileInputStream(fichier),encodage),separateur,texteSeparateur);
		List<String[]> donnees = cr.readAll();
		cr.close();
		return donnees;
	}
}
