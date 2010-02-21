package fr.certu.chouette.service.exportateur.monoitineraire.csv.impl;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import au.com.bytecode.opencsv.CSVWriter;

public class EcrivainCSV {
	
	private final char separateur = ';';
	private final String encodage = "ISO-8859-1";
	private final char texteSeparateur = '"';
	
	
	
	public void ecrire(List<String[]> donnees, File fichier) throws IOException{
		CSVWriter cw = new CSVWriter(new PrintWriter(fichier,encodage),separateur,texteSeparateur);
		cw.writeAll(donnees);
		cw.close();
	}
}
