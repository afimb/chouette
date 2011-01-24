package fr.certu.chouette.service.validation;

import java.util.ArrayList;
import java.util.List;

public class ChouetteArea {
    
    private ChouettePTNetwork 	chouettePTNetwork;
    private List<AreaCentroid> 	areaCentroids 		= new ArrayList<AreaCentroid>();
    private List<StopArea> 	stopAreas 		= new ArrayList<StopArea>();

    public void setChouettePTNetwork(ChouettePTNetwork chouettePTNetwork) {
        this.chouettePTNetwork = chouettePTNetwork;
    }
    
    public ChouettePTNetwork getChouettePTNetwork() {
        return chouettePTNetwork;
    }

    public void addAreaCentroid(AreaCentroid areaCentroid) throws IndexOutOfBoundsException {
        areaCentroids.add(areaCentroid);
    }
    
    public void addAreaCentroid(int index, AreaCentroid areaCentroid) throws IndexOutOfBoundsException {
        areaCentroids.add(index, areaCentroid);
    }

    public void removeAreaCentroid(int index) throws IndexOutOfBoundsException {
        areaCentroids.remove(index);
    }
    
    public void removeAreaCentroid(AreaCentroid areaCentroid) {
        areaCentroids.remove(areaCentroid);
    }

    public void clearAreaCentroids() {
        areaCentroids.clear();
    }
    
    public void setAreaCentroids(List<AreaCentroid> areaCentroids) {
        this.areaCentroids = areaCentroids;
    }

    public List<AreaCentroid> getAreaCentroids() {
        return areaCentroids;
    }
    
    public AreaCentroid[] getAreaCentroidAsTable() {
        int size = areaCentroids.size();
        AreaCentroid[] mArray = new AreaCentroid[size];
        for (int index = 0; index < size; index++)
            mArray[index] = (AreaCentroid)areaCentroids.get(index);
        return mArray;
    }

    public AreaCentroid getAreaCentroid(int index) throws IndexOutOfBoundsException {
        if ((index < 0) || (index > areaCentroids.size()))
            throw new IndexOutOfBoundsException();
        return (AreaCentroid) areaCentroids.get(index);
    }
    
    public int getAreaCentroidCount() {
        return areaCentroids.size();
    }

    public void setAreaCentroids(ArrayList<AreaCentroid> areaCentroids) {
        this.areaCentroids = areaCentroids;
    }
	
    public void setAreaCentroids(AreaCentroid[] arrayOfAreaCentroids) {
    	areaCentroids.clear();
        for (int i = 0; i < arrayOfAreaCentroids.length; i++)
            areaCentroids.add(arrayOfAreaCentroids[i]);
    }
    
    public void setAreaCentroid(int index, AreaCentroid areaCentroid) throws IndexOutOfBoundsException {
    	if ((index < 0) || (index > areaCentroids.size()))
    		throw new IndexOutOfBoundsException();
    	areaCentroids.set(index, areaCentroid);
    }

    public void addStopArea(StopArea stopArea) throws IndexOutOfBoundsException {
        stopAreas.add(stopArea);
    }
    
    public void addStopArea(int index, StopArea stopArea) throws IndexOutOfBoundsException {
        stopAreas.add(index, stopArea);
    }

    public void removeStopArea(int index) throws IndexOutOfBoundsException {
        stopAreas.remove(index);
    }
    
    public void removeStopArea(StopArea stopArea) {
        stopAreas.remove(stopArea);
    }

    public void clearStopAreas() {
        stopAreas.clear();
    }
    
    public void setStopAreas(List<StopArea> stopAreas) {
        this.stopAreas = stopAreas;
    }

    public List<StopArea> getStopAreas() {
        return stopAreas;
    }
    
    public StopArea[] getStopAreaAsTable() {
        int size = stopAreas.size();
        StopArea[] mArray = new StopArea[size];
        for (int index = 0; index < size; index++)
            mArray[index] = (StopArea)stopAreas.get(index);
        return mArray;
    }

    public StopArea getStopArea(int index) throws IndexOutOfBoundsException {
        if ((index < 0) || (index > stopAreas.size()))
            throw new IndexOutOfBoundsException();
        return (StopArea) stopAreas.get(index);
    }
    
    public int getStopAreaCount() {
        return stopAreas.size();
    }

    public void setStopAreas(ArrayList<StopArea> stopAreas) {
        this.stopAreas = stopAreas;
    }
	
    public void setStopAreas(StopArea[] arrayOfStopAreas) {
    	stopAreas.clear();
        for (int i = 0; i < arrayOfStopAreas.length; i++)
            stopAreas.add(arrayOfStopAreas[i]);
    }
    
    public void setStopArea(int index, StopArea stopArea) throws IndexOutOfBoundsException {
    	if ((index < 0) || (index > stopAreas.size()))
    		throw new IndexOutOfBoundsException();
    	stopAreas.set(index, stopArea);
    }
    
    public String toString() {
        StringBuffer stb = new StringBuffer();
        stb.append("<ChouetteArea>\n");
        for (int i = 0; i < stopAreas.size(); i++)
            stb.append(((StopArea)stopAreas.get(i)).toString());
        for (int i = 0; i < areaCentroids.size(); i++)
            stb.append(((AreaCentroid)areaCentroids.get(i)).toString());
        stb.append("</ChouetteArea>\n");
        return stb.toString();
    }
    
    public String toString(int indent, int indentSize) {
        StringBuffer stb = new StringBuffer();
        for (int i = 0; i < indent; i++)
            for (int j = 0; j < indentSize; j++)
                stb.append(" ");
        stb.append("<ChouetteArea>\n");
        for (int i = 0; i < stopAreas.size(); i++)
            stb.append(((StopArea)stopAreas.get(i)).toString(indent+1, indentSize));
        for (int i = 0; i < areaCentroids.size(); i++)
            stb.append(((AreaCentroid)areaCentroids.get(i)).toString(indent+1, indentSize));
        for (int i = 0; i < indent; i++)
            for (int j = 0; j < indentSize; j++)
                stb.append(" ");
        stb.append("</ChouetteArea>\n");
        return stb.toString();
    }
}
