/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.certu.chouette.service.importateur.multilignes.hastus.impl;

import fr.certu.chouette.service.identification.IIdentificationManager;

/**
 *
 * @author zbouziane
 */
class Lecteur {
    
    //private int                    counter;
    private String                 cleCode;               //
    private String                 special;               // "SPECIAL"
    private String                 space;                 // "SPACE"
    private IIdentificationManager identificationManager; //
    private String                 hastusCode;            // "HastusTUR"

    /*
    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public void incrementCounter() {
        counter++;
    }*/
    
    public String getCleCode() {
	return cleCode;
    }

    public void setCleCode(String cleCode) {
	this.cleCode = cleCode;
    }

    public void reinit() {
        //this.counter = 0;
    }

    public boolean isTitreReconnu(String[] ligneCSV) {
	if ((ligneCSV == null) || (ligneCSV.length == 0))
	    return false;
        if (ligneCSV[0] == null)
            return false;
	return ligneCSV[0].trim().equals(getCleCode());
    }
    
    protected String toTrident(String str) {
        if ((str == null) || (str.length() == 0))
            return "";
        String result = "";
        for (int i = 0; i < str.length(); i++)
            if (('a' <= str.charAt(i)) && (str.charAt(i) <= 'z') ||
                ('A' <= str.charAt(i)) && (str.charAt(i) <= 'Z') ||
                ('0' <= str.charAt(i)) && (str.charAt(i) <= '9'))
                result += str.charAt(i);
            else if ((str.charAt(i) == ' ') || (str.charAt(i) == '\t'))
                result += space;
            else
                result += special;
        return result;
    }
    
    public String getSpecial() {
        return special;
    }

    public void setSpecial(String special) {
        this.special = special;
    }

    public String getSpace() {
        return space;
    }

    public void setSpace(String space) {
        this.space = space;
    }

    public IIdentificationManager getIdentificationManager() {
	return identificationManager;
    }

    public void setIdentificationManager(IIdentificationManager identificationManager) {
	this.identificationManager = identificationManager;
    }

    public String getHastusCode() {
	return hastusCode;
    }

    public void setHastusCode(String hastusCode) {
	this.hastusCode = hastusCode;
    }
}
