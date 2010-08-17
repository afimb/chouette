package fr.certu.chouette.service.importateur.multilignes.hastus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import org.apache.log4j.Logger;
import fr.certu.chouette.service.importateur.IReducteur;

public class Reducteur implements IReducteur {
    
    private static final Logger logger    = Logger.getLogger(Reducteur.class);
    private              String repertoire;
    
    public String reduire(String nom, boolean estCanonique) {
	if (estCanonique)
	    return reduireCheminFichier(nom);
	return reduireCheminFichier(getCheminfichier(nom));
    }
    
    private String getCheminfichier(String nom) {
	return repertoire + File.separator + nom;
    }
    
    public String reduireCheminFichier(String nom) {
	String nouveauNom = nom+".out";
	try {
	    int oldLineNumber01 = 0;
	    int newLineNumber01 = 0;
	    int oldLineNumber02 = 0;
	    int newLineNumber02 = 0;
	    int oldLineNumber03 = 0;
	    int newLineNumber03 = 0;
	    int oldLineNumber04 = 0;
	    int newLineNumber04 = 0;
	    int oldLineNumber05 = 0;
	    int newLineNumber05 = 0;
	    int oldLineNumber06 = 0;
	    int newLineNumber06 = 0;
	    int oldLineNumber07 = 0;
	    int newLineNumber07 = 0;
	    LineNumberReader lnr = new LineNumberReader(new InputStreamReader(new FileInputStream(nom), "ISO-8859-1"));
	    OutputStreamWriter fw = new OutputStreamWriter(new FileOutputStream(nouveauNom), "ISO-8859-1");
	    Set<String> lines = new HashSet<String>();
	    String line = lnr.readLine().trim();
	    SortedMap<String, SortedMap<String, List<String>>> sortedHoraires = new TreeMap<String, SortedMap<String, List<String>>>();
	    Map<String, String> arretsOrdonnees = new HashMap<String, String>();
	    while (line != null) {
		line = line.trim();
		if (line.startsWith("01"))
		    oldLineNumber01++;
		else if (line.startsWith("02"))
		    oldLineNumber02++;
		else if (line.startsWith("03"))
		    oldLineNumber03++;
		else if (line.startsWith("04"))
		    oldLineNumber04++;
		else if (line.startsWith("05"))
		    oldLineNumber05++;
		else if (line.startsWith("06"))
		    oldLineNumber06++;
		else if (line.startsWith("07"))
		    oldLineNumber07++;
		if (line.startsWith("06")) {
		    String[] strs = line.split(";");
		    if (strs[2].charAt(0) < 'H') {
			if (lines.add(line)) {
			    String key1 = strs[1]+";"+strs[2];
			    if (sortedHoraires.get(key1) == null)
				sortedHoraires.put(key1, new TreeMap<String, List<String>>());
			    String key2 = strs[4];
			    if (key2.startsWith("00:"))
				key2 = "24" + key2.substring(2);
			    else if (key2.startsWith("01:"))
				key2 = "25" + key2.substring(2);
			    String value = strs[3];
			    if (sortedHoraires.get(key1).get(key2) == null)
				sortedHoraires.get(key1).put(key2, new ArrayList<String>());
			    sortedHoraires.get(key1).get(key2).add(value);
			    newLineNumber06++;
			}
		    }
		}
		else if (lines.add(line)) {
		    if (line.startsWith("01")) {
			fw.write(line+"\n");
			newLineNumber01++;
		    }
		    else if (line.startsWith("02")) {
			fw.write(line+"\n");
			newLineNumber02++;
		    }
		    else if (line.startsWith("03")) {
			fw.write(line+"\n");
			newLineNumber03++;
		    }
		    else if (line.startsWith("04")) {
			fw.write(line+"\n");
			newLineNumber04++;
		    }
		    else if (line.startsWith("05")) {
			fw.write(line+"\n");
			newLineNumber05++;
		    }
		    else if (line.startsWith("07")) {
			String[] strs = line.split(";");
			String key = strs[1]+";"+strs[2];
			String value = strs[3];
			arretsOrdonnees.put(key, value);
			newLineNumber07++;
		    }
		}
		line = lnr.readLine();
	    }
	    lnr.close();
	    for (String key : sortedHoraires.keySet())
		for (String key2 : sortedHoraires.get(key).keySet()) {
		    if (sortedHoraires.get(key).get(key2).size() == 1) {
			String value = sortedHoraires.get(key).get(key2).get(0);
			String key3 = key2;
			if (key3.startsWith("24:"))
			    key3 = "00" + key3.substring(2);
			else if (key3.startsWith("25:"))
			    key3 = "01" + key3.substring(2);
			fw.write("06;"+key+";"+value+";"+key3+"\n");
		    }
		    else {
			String[] strs = key.split(";");
			int number = sortedHoraires.get(key).get(key2).size();
			int[] ordres = new int[number];
			String[] values = new String[number];
			for (int i = 0; i < number; i++) {
			    values[i] = sortedHoraires.get(key).get(key2).get(i);
			    if (arretsOrdonnees.get(strs[1]+";"+values[i]) == null) {
				logger.warn(strs[1]+";"+values[i]+" : NON ORDONNEES.");
				continue;
			    }
			    ordres[i] = Integer.parseInt(arretsOrdonnees.get(strs[1]+";"+values[i]));
			}
			sort(values, ordres);
			String key3 = key2;
			if (key3.startsWith("24:"))
			    key3 = "00" + key3.substring(2);
			else if (key3.startsWith("25:"))
			    key3 = "01" + key3.substring(2);
			for (int i = 0; i < number; i++)
			    fw.write("06;"+key+";"+values[i]+";"+key3+"\n");
		    }
		}
	    fw.flush();

	    lines = new HashSet<String>();
	    lnr = new LineNumberReader(new FileReader(nom));
	    line = lnr.readLine().trim();
	    sortedHoraires = new TreeMap<String, SortedMap<String, List<String>>>();
	    while (line != null) {
		line = line.trim();
		if (line.startsWith("06")) {
		    String[] strs = line.split(";");
		    if (strs[2].charAt(0) >= 'H') {
			if (lines.add(line)) {
			    String key1 = /*strs[0]+";"+*/strs[1]+";"+strs[2];
			    if (sortedHoraires.get(key1) == null)
				sortedHoraires.put(key1, new TreeMap<String, List<String>>());
			    String key2 = strs[4];
			    if (key2.startsWith("00:"))
				key2 = "24" + key2.substring(2);
			    else if (key2.startsWith("01:"))
				key2 = "25" + key2.substring(2);
			    String value = strs[3];
			    if (sortedHoraires.get(key1).get(key2) == null)
				sortedHoraires.get(key1).put(key2, new ArrayList<String>());
			    sortedHoraires.get(key1).get(key2).add(value);
			    newLineNumber06++;
			}
		    }
		}
		line = lnr.readLine();
	    }
	    lnr.close();
	    for (String key : sortedHoraires.keySet())
		for (String key2 : sortedHoraires.get(key).keySet()) {
		    if (sortedHoraires.get(key).get(key2).size() == 1) {
			String value = sortedHoraires.get(key).get(key2).get(0);
			String key3 = key2;
			if (key3.startsWith("24:"))
			    key3 = "00" + key3.substring(2);
			if (key3.startsWith("25:"))
			    key3 = "01" + key3.substring(2);
			fw.write("06;"+key+";"+value+";"+key3+"\n");
		    }
		    else {
			String[] strs = key.split(";");
			int number = sortedHoraires.get(key).get(key2).size();
			int[] ordres = new int[number];
			String[] values = new String[number];
			for (int i = 0; i < number; i++) {
			    values[i] = sortedHoraires.get(key).get(key2).get(i);
			    if (arretsOrdonnees.get(strs[1]+";"+values[i]) == null) {
				logger.warn(strs[1]+";"+values[i]+" : NON ORDONNEES.");
				continue;
			    }
			    ordres[i] = Integer.parseInt(arretsOrdonnees.get(strs[1]+";"+values[i]));
			}
			sort(values, ordres);
			String key3 = key2;
			if (key3.startsWith("24:"))
			    key3 = "00" + key3.substring(2);
			else if (key3.startsWith("25:"))
			    key3 = "01" + key3.substring(2);
			for (int i = 0; i < number; i++)
			    fw.write("06;"+key+";"+values[i]+";"+key3+"\n");
		    }
		}
	    fw.flush();
	    fw.close();
	    sortedHoraires.clear();
	    lines.clear();
	    
	    if (oldLineNumber01 != newLineNumber01)
		System.out.println("Ancien nombre de lieux = "+oldLineNumber01+", nouveau nombre = "+newLineNumber01);
	    if (oldLineNumber02 != newLineNumber02)
		System.out.println("Ancien nombre d'arrets = "+oldLineNumber02+", nouveau nombre = "+newLineNumber02);
	    if (oldLineNumber03 != newLineNumber03)
		System.out.println("Ancien nombre de lignes = "+oldLineNumber03+", nouveau nombre = "+newLineNumber03);
	    if (oldLineNumber04 != newLineNumber04)
		System.out.println("Ancien nombre d'itineraires = "+oldLineNumber04+", nouveau nombre = "+newLineNumber04);
	    if (oldLineNumber05 != newLineNumber05)
		System.out.println("Ancien nombre de courses = "+oldLineNumber05+", nouveau nombre = "+newLineNumber05);
	    if (oldLineNumber06 != newLineNumber06)
		System.out.println("Ancien nombre d'horaires = "+oldLineNumber06+", nouveau nombre = "+newLineNumber06);
	    if (oldLineNumber07 != newLineNumber07)
		System.out.println("Ancien nombre d'arrêts ordonnées = "+oldLineNumber07+", nouveau nombre = "+newLineNumber07);
	}
	catch(Exception e) {
	    e.printStackTrace();
	}
	return nouveauNom;
    }
    
    private void sort(String[] values, int[] ordres) throws Exception {
	int size = values.length;
	if (ordres.length != size)
	    throw new Exception("Situation impossible.");
	for (int i = 1; i < size; i++)
	    for (int j = 0; j < i; j++)
		if (ordres[i] < ordres[j]) {
		    // inserer ordres[i] à la j ème place
		    int    tmp   = ordres[i];
		    String tmpSt = values[i];
		    for (int k = j+1; k <= i; k++) {
			values[k] = values[k-1];
			ordres[k] = ordres[k-1];
		    }
		    ordres[j] = tmp;
		    values[j] = tmpSt;
		}
    }
    
    public String getRepertoire() {
	return repertoire;
    }
    
    public void setRepertoire(String repertoire) {
	this.repertoire = repertoire;
    }
}
