/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.certu.chouette.service.importateur.monoligne.csv;

/**
 *
 * @author Zakaria BOUZIANE
 */
class Lecteur {
    
    protected String trimInside(String str) {
        if (str == null)
            return null;
        String result = str.trim();
        result = result.replace(' ', '_');
        result = result.replace('\t', '_');
        return result;
    }
}
