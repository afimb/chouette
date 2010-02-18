package fr.certu.chouette.service.validation.amivif;

import java.util.List;

public class Registration {
	
	private String					registrationNumber;		// 1
	private List<String>			transportNetworkIds;	// 0..w
	private List<TransportNetwork>	transportNetworks;		// 0..w
	private List<String>			lineIds;				// 0..w
	private List<Line>				lines;					// 0..w
	private String					companyId;				// 0..1
	private Company					company;				// 0..1
	
	public void setRegistrationNumber(String registrationNumber) {
		this.registrationNumber = registrationNumber;
	}
	
	public String getRegistrationNumber() {
		return registrationNumber;
	}
	
	public void setLines(List<Line> lines) {
		this.lines = lines;
	}
	
	public void setTransportNetworks(List<TransportNetwork> transportNetworks) {
		this.transportNetworks = transportNetworks;
	}
	
	public List<TransportNetwork> getTransportNetworks() {
		return transportNetworks;
	}
	
	public void addTransportNetwork(TransportNetwork transportNetwork) {
		transportNetworks.add(transportNetwork);
	}
	
	public void removeTransportNetwork(TransportNetwork transportNetwork) {
		transportNetworks.remove(transportNetwork);
	}
	
	public void removeTransportNetwork(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getTransportNetworksCount()))
			throw new IndexOutOfBoundsException();
		transportNetworks.remove(i);
	}
	
	public TransportNetwork getTransportNetwork(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getTransportNetworksCount()))
			throw new IndexOutOfBoundsException();
		return (TransportNetwork)transportNetworks.get(i);
	}
	
	public int getTransportNetworksCount() {
		if (transportNetworks == null)
			return 0;
		return transportNetworks.size();
	}
	
	public void setTransportNetworkIds(List<String> transportNetworkIds) {
		this.transportNetworkIds = transportNetworkIds;
	}
	
	public List<String> getTransportNetworkIds() {
		return transportNetworkIds;
	}
	
	public void addTransportNetworkId(String transportNetworkId) {
		transportNetworkIds.add(transportNetworkId);
	}
	
	public void removeTransportNetworkId(String transportNetworkId) {
		transportNetworkIds.remove(transportNetworkId);
	}
	
	public void removeTransportNetworkId(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getTransportNetworkIdsCount()))
			throw new IndexOutOfBoundsException();
		transportNetworkIds.remove(i);
	}
	
	public String getTransportNetworkId(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getTransportNetworkIdsCount()))
			throw new IndexOutOfBoundsException();
		return (String)transportNetworkIds.get(i);
	}
	
	public int getTransportNetworkIdsCount() {
		if (transportNetworkIds == null)
			return 0;
		return transportNetworkIds.size();
	}
	
	public List<Line> getLines() {
		return lines;
	}
	
	public void addLine(Line line) {
		lines.add(line);
	}
	
	public void removeLine(Line line) {
		lines.remove(line);
	}
	
	public void removeLine(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getLinesCount()))
			throw new IndexOutOfBoundsException();
		lines.remove(i);
	}
	
	public Line getLine(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getLinesCount()))
			throw new IndexOutOfBoundsException();
		return (Line)lines.get(i);
	}
	
	public int getLinesCount() {
		if (lines == null)
			return 0;
		return lines.size();
	}
	
	public void setLineIds(List<String> lineIds) {
		this.lineIds = lineIds;
	}
	
	public List<String> getLineIds() {
		return lineIds;
	}
	
	public void addLineId(String lineId) {
		lineIds.add(lineId);
	}
	
	public void removeLineId(String lineId) {
		lineIds.remove(lineId);
	}
	
	public void removeLineId(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getLineIdsCount()))
			throw new IndexOutOfBoundsException();
		lineIds.remove(i);
	}
	
	public String getLineId(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getLineIdsCount()))
			throw new IndexOutOfBoundsException();
		return (String)lineIds.get(i);
	}
	
	public int getLineIdsCount() {
		if (lineIds == null)
			return 0;
		return lineIds.size();
	}
	
	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}
	
	public String getCompanyId() {
		return companyId;
	}
	
	public void setCompany(Company company) {
		this.company = company;
	}
	
	public Company getCompany() {
		return company;
	}
}
