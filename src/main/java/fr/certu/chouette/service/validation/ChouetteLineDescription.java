package fr.certu.chouette.service.validation;

import java.util.ArrayList;
import java.util.List;

public class ChouetteLineDescription {
	
	private ChouettePTNetwork chouettePTNetwork;
	private Line line;
	private List<ChouetteRoute> chouetteRoutes = new ArrayList<ChouetteRoute>();
	private List<StopPoint> stopPoints = new ArrayList<StopPoint>();
	private List<ITL> iTLs = new ArrayList<ITL>();
	private List<PtLink> ptLinks = new ArrayList<PtLink>();
	private List<JourneyPattern> journeyPatterns = new ArrayList<JourneyPattern>();
	private List<VehicleJourney> vehicleJourneys = new ArrayList<VehicleJourney>();
	
	public void setChouettePTNetwork(ChouettePTNetwork chouettePTNetwork) {
		this.chouettePTNetwork = chouettePTNetwork;
	}
	
	public ChouettePTNetwork getChouettePTNetwork() {
		return chouettePTNetwork;
	}
	
	public void setLine(Line line) {
		this.line = line;
	}
	
	public Line getLine() {
		return line;
	}
	
	public void addChouetteRoute(ChouetteRoute chouetteRoute) throws IndexOutOfBoundsException {
		chouetteRoutes.add(chouetteRoute);
	}
	
	public void addChouetteRoute(int index, ChouetteRoute chouetteRoute) throws IndexOutOfBoundsException {
		chouetteRoutes.add(index, chouetteRoute);
	}
	
	public void removeChouetteRoute(int index) throws IndexOutOfBoundsException {
		chouetteRoutes.remove(index);
	}
	
	public void removeChouetteRoute(ChouetteRoute chouetteRoute) {
		chouetteRoutes.remove(chouetteRoute);
	}
	
	public void clearChouetteRoutes() {
		chouetteRoutes.clear();
	}
	
	public void setChouetteRoutes(List<ChouetteRoute> chouetteRoutes) {
		this.chouetteRoutes = chouetteRoutes;
	}
	
	public List<ChouetteRoute> getChouetteRoutes() {
		return chouetteRoutes;
	}
	
	public ChouetteRoute[] getChouetteRouteAsTable() {
		int size = chouetteRoutes.size();
		ChouetteRoute[] mArray = new ChouetteRoute[size];
		for (int index = 0; index < size; index++)
			mArray[index] = (ChouetteRoute)chouetteRoutes.get(index);
		return mArray;
	}
	
	public ChouetteRoute getChouetteRoute(int index) throws IndexOutOfBoundsException {
		if ((index < 0) || (index > chouetteRoutes.size()))
            throw new IndexOutOfBoundsException();
		return (ChouetteRoute) chouetteRoutes.get(index);
	}
	
	public int getChouetteRouteCount() {
        return chouetteRoutes.size();
    }
	
	public void setChouetteRoutes(ArrayList<ChouetteRoute> chouetteRoutes) {
        this.chouetteRoutes = chouetteRoutes;
    }
	
    public void setChouetteRoutes(ChouetteRoute[] arrayOfChouetteRoutes) {
    	chouetteRoutes.clear();
        for (int i = 0; i < arrayOfChouetteRoutes.length; i++)
            chouetteRoutes.add(arrayOfChouetteRoutes[i]);
    }
    
    public void setChouetteRoute(int index, ChouetteRoute chouetteRoute) throws IndexOutOfBoundsException {
    	if ((index < 0) || (index > chouetteRoutes.size()))
    		throw new IndexOutOfBoundsException();
    	chouetteRoutes.set(index, chouetteRoute);
    }
	
	public void addStopPoint(StopPoint stopPoint) throws IndexOutOfBoundsException {
		stopPoints.add(stopPoint);
	}
	
	public void addStopPoint(int index, StopPoint stopPoint) throws IndexOutOfBoundsException {
		stopPoints.add(index, stopPoint);
	}
	
	public void removeStopPoint(int index) throws IndexOutOfBoundsException {
		stopPoints.remove(index);
	}
	
	public void removeStopPoint(StopPoint stopPoint) {
		stopPoints.remove(stopPoint);
	}
	
	public void clearStopPoints() {
		stopPoints.clear();
	}
	
	public void setStopPoints(List<StopPoint> stopPoints) {
		this.stopPoints = stopPoints;
	}
	
	public List<StopPoint> getStopPoints() {
		return stopPoints;
	}
	
	public StopPoint[] getStopPointAsTable() {
		int size = stopPoints.size();
		StopPoint[] mArray = new StopPoint[size];
		for (int index = 0; index < size; index++)
			mArray[index] = (StopPoint)stopPoints.get(index);
		return mArray;
	}
	
	public StopPoint getStopPoint(int index) throws IndexOutOfBoundsException {
		if ((index < 0) || (index > stopPoints.size()))
            throw new IndexOutOfBoundsException();
		return (StopPoint) stopPoints.get(index);
	}
	
	public int getStopPointCount() {
        return stopPoints.size();
    }
	
	public void setStopPoints(ArrayList<StopPoint> stopPoints) {
        this.stopPoints = stopPoints;
    }
	
    public void setStopPoints(StopPoint[] arrayOfStopPoints) {
    	stopPoints.clear();
        for (int i = 0; i < arrayOfStopPoints.length; i++)
            stopPoints.add(arrayOfStopPoints[i]);
    }
    
    public void setStopPoint(int index, StopPoint stopPoint) throws IndexOutOfBoundsException {
    	if ((index < 0) || (index > stopPoints.size()))
    		throw new IndexOutOfBoundsException();
    	stopPoints.set(index, stopPoint);
    }

	public void addITL(ITL iTL) throws IndexOutOfBoundsException {
		iTLs.add(iTL);
	}
	
	public void addITL(int index, ITL iTL) throws IndexOutOfBoundsException {
		iTLs.add(index, iTL);
	}
	
	public void removeITL(int index) throws IndexOutOfBoundsException {
		iTLs.remove(index);
	}
	
	public void removeITL(ITL iTL) {
		iTLs.remove(iTL);
	}
	
	public void clearITLs() {
		iTLs.clear();
	}
	
	public void setITLs(List<ITL> iTLs) {
		this.iTLs = iTLs;
	}
	
	public List<ITL> getITLS() {
		return iTLs;
	}
	
	public ITL[] getITLAsTable() {
		int size = iTLs.size();
		ITL[] mArray = new ITL[size];
		for (int index = 0; index < size; index++)
			mArray[index] = (ITL)iTLs.get(index);
		return mArray;
	}
	
	public ITL getITL(int index) throws IndexOutOfBoundsException {
		if ((index < 0) || (index > iTLs.size()))
            throw new IndexOutOfBoundsException();
		return (ITL) iTLs.get(index);
	}
	
	public int getITLCount() {
        return iTLs.size();
    }
	
	public void setITLs(ArrayList<ITL> iTLs) {
        this.iTLs = iTLs;
    }
	
    public void setITLs(ITL[] arrayOfITLs) {
    	iTLs.clear();
        for (int i = 0; i < arrayOfITLs.length; i++)
            iTLs.add(arrayOfITLs[i]);
    }
    
    public void setITL(int index, ITL iTL) throws IndexOutOfBoundsException {
    	if ((index < 0) || (index > iTLs.size()))
    		throw new IndexOutOfBoundsException();
    	iTLs.set(index, iTL);
    }
	
	public void addPtLink(PtLink ptLink) throws IndexOutOfBoundsException {
		ptLinks.add(ptLink);
	}
	
	public void addPtLink(int index, PtLink ptLink) throws IndexOutOfBoundsException {
		ptLinks.add(index, ptLink);
	}
	
	public void removePtLink(int index) throws IndexOutOfBoundsException {
		ptLinks.remove(index);
	}
	
	public void removePtLink(PtLink ptLink) {
		ptLinks.remove(ptLink);
	}
	
	public void clearPtLinks() {
		ptLinks.clear();
	}
	
	public void setPtLinks(List<PtLink> ptLinks) {
		this.ptLinks = ptLinks;
	}
	
	public List<PtLink> getPtLinks() {
		return ptLinks;
	}
	
	public PtLink[] getPtLinkAsTable() {
		int size = ptLinks.size();
		PtLink[] mArray = new PtLink[size];
		for (int index = 0; index < size; index++)
			mArray[index] = (PtLink)ptLinks.get(index);
		return mArray;
	}
	
	public PtLink getPtLink(int index) throws IndexOutOfBoundsException {
		if ((index < 0) || (index > ptLinks.size()))
            throw new IndexOutOfBoundsException();
		return (PtLink) ptLinks.get(index);
	}
	
	public int getPtLinkCount() {
        return ptLinks.size();
    }
	
	public void setPtLinks(ArrayList<PtLink> ptLinks) {
        this.ptLinks = ptLinks;
    }
	
    public void setPtLinks(PtLink[] arrayOfPtLinks) {
    	ptLinks.clear();
        for (int i = 0; i < arrayOfPtLinks.length; i++)
            ptLinks.add(arrayOfPtLinks[i]);
    }
    
    public void setPtLink(int index, PtLink ptLink) throws IndexOutOfBoundsException {
    	if ((index < 0) || (index > ptLinks.size()))
    		throw new IndexOutOfBoundsException();
    	ptLinks.set(index, ptLink);
    }

	public void addJourneyPattern(JourneyPattern journeyPattern) throws IndexOutOfBoundsException {
		journeyPatterns.add(journeyPattern);
	}
	
	public void addJourneyPattern(int index, JourneyPattern journeyPattern) throws IndexOutOfBoundsException {
		journeyPatterns.add(index, journeyPattern);
	}
	
	public void removeJourneyPattern(int index) throws IndexOutOfBoundsException {
		journeyPatterns.remove(index);
	}
	
	public void removeJourneyPattern(JourneyPattern journeyPattern) {
		journeyPatterns.remove(journeyPattern);
	}
	
	public void clearJourneyPatterns() {
		journeyPatterns.clear();
	}
	
	public void setJourneyPatterns(List<JourneyPattern> journeyPatterns) {
		this.journeyPatterns = journeyPatterns;
	}
	
	public List<JourneyPattern> getJourneyPatterns() {
		return journeyPatterns;
	}
	
	public JourneyPattern[] getJourneyPatternAsTable() {
		int size = journeyPatterns.size();
		JourneyPattern[] mArray = new JourneyPattern[size];
		for (int index = 0; index < size; index++)
			mArray[index] = (JourneyPattern)journeyPatterns.get(index);
		return mArray;
	}
	
	public JourneyPattern getJourneyPattern(int index) throws IndexOutOfBoundsException {
		if ((index < 0) || (index > journeyPatterns.size()))
            throw new IndexOutOfBoundsException();
		return (JourneyPattern) journeyPatterns.get(index);
	}
	
	public int getJourneyPatternCount() {
        return journeyPatterns.size();
    }
	
	public void setJourneyPatterns(ArrayList<JourneyPattern> journeyPatterns) {
        this.journeyPatterns = journeyPatterns;
    }
	
    public void setJourneyPatterns(JourneyPattern[] arrayOfJourneyPatterns) {
    	journeyPatterns.clear();
        for (int i = 0; i < arrayOfJourneyPatterns.length; i++)
            journeyPatterns.add(arrayOfJourneyPatterns[i]);
    }
    
    public void setJourneyPattern(int index, JourneyPattern journeyPattern) throws IndexOutOfBoundsException {
    	if ((index < 0) || (index > journeyPatterns.size()))
    		throw new IndexOutOfBoundsException();
    	journeyPatterns.set(index, journeyPattern);
    }

	public void addVehicleJourney(VehicleJourney vehicleJourney) throws IndexOutOfBoundsException {
		vehicleJourneys.add(vehicleJourney);
	}
	
	public void addVehicleJourney(int index, VehicleJourney vehicleJourney) throws IndexOutOfBoundsException {
		vehicleJourneys.add(index, vehicleJourney);
	}
	
	public void removeVehicleJourney(int index) throws IndexOutOfBoundsException {
		vehicleJourneys.remove(index);
	}
	
	public void removeVehicleJourney(VehicleJourney vehicleJourney) {
		vehicleJourneys.remove(vehicleJourney);
	}
	
	public void clearVehicleJourneys() {
		vehicleJourneys.clear();
	}
	
	public void setVehicleJourneys(List<VehicleJourney> vehicleJourneys) {
		this.vehicleJourneys = vehicleJourneys;
	}
	
	public List<VehicleJourney> getVehicleJourneys() {
		return vehicleJourneys;
	}
	
	public VehicleJourney[] getVehicleJourneyAsTable() {
		int size = vehicleJourneys.size();
		VehicleJourney[] mArray = new VehicleJourney[size];
		for (int index = 0; index < size; index++)
			mArray[index] = (VehicleJourney)vehicleJourneys.get(index);
		return mArray;
	}
	
	public VehicleJourney getVehicleJourney(int index) throws IndexOutOfBoundsException {
		if ((index < 0) || (index > vehicleJourneys.size()))
            throw new IndexOutOfBoundsException();
		return (VehicleJourney) vehicleJourneys.get(index);
	}
	
	public int getVehicleJourneyCount() {
        return vehicleJourneys.size();
    }
	
	public void setVehicleJourneys(ArrayList<VehicleJourney> vehicleJourneys) {
        this.vehicleJourneys = vehicleJourneys;
    }
	
    public void setVehicleJourneys(VehicleJourney[] arrayOfVehicleJourneys) {
    	vehicleJourneys.clear();
        for (int i = 0; i < arrayOfVehicleJourneys.length; i++)
            vehicleJourneys.add(arrayOfVehicleJourneys[i]);
    }
    
    public void setVehicleJourney(int index, VehicleJourney vehicleJourney) throws IndexOutOfBoundsException {
    	if ((index < 0) || (index > vehicleJourneys.size()))
    		throw new IndexOutOfBoundsException();
    	vehicleJourneys.set(index, vehicleJourney);
    }

	public String toString() {
		StringBuffer stb = new StringBuffer();
		stb.append("<ChouetteLineDescription>\n");
		stb.append(line.toString());
		for (int i = 0; i < chouetteRoutes.size(); i++)
			stb.append(chouetteRoutes.get(i).toString());
		for (int i = 0; i < stopPoints.size(); i++)
			stb.append(stopPoints.get(i).toString());
		for (int i = 0; i < iTLs.size(); i++)
			stb.append(iTLs.get(i).toString());
		for (int i = 0; i < ptLinks.size(); i++)
			stb.append(ptLinks.get(i).toString());
		for (int i = 0; i < journeyPatterns.size(); i++)
			stb.append(journeyPatterns.get(i).toString());
		for (int i = 0; i < vehicleJourneys.size(); i++)
			stb.append(vehicleJourneys.get(i).toString());		
		stb.append("</ChouetteLineDescription>\n");
		return stb.toString();
	}

	public String toString(int indent, int indentSize) {
		StringBuffer stb = new StringBuffer();
		for (int i = 0; i < indent; i++)
			for (int j = 0; j < indentSize; j++)
				stb.append(" ");
		stb.append("<ChouetteLineDescription>\n");
		stb.append(line.toString(indent+1, indentSize));
		for (int i = 0; i < chouetteRoutes.size(); i++)
			stb.append(chouetteRoutes.get(i).toString(indent+1, indentSize));
		for (int i = 0; i < stopPoints.size(); i++)
			stb.append(stopPoints.get(i).toString(indent+1, indentSize));
		for (int i = 0; i < iTLs.size(); i++)
			stb.append(iTLs.get(i).toString(indent+1, indentSize));
		for (int i = 0; i < ptLinks.size(); i++)
			stb.append(ptLinks.get(i).toString(indent+1, indentSize));
		for (int i = 0; i < journeyPatterns.size(); i++)
			stb.append(journeyPatterns.get(i).toString(indent+1, indentSize));
		for (int i = 0; i < vehicleJourneys.size(); i++)
			stb.append(vehicleJourneys.get(i).toString(indent+1, indentSize));
		for (int i = 0; i < indent; i++)
			for (int j = 0; j < indentSize; j++)
				stb.append(" ");
		stb.append("</ChouetteLineDescription>\n");
		return stb.toString();
	}
}
