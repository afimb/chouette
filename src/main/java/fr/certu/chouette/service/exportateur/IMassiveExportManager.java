package fr.certu.chouette.service.exportateur;

import java.util.Date;

public interface IMassiveExportManager 
{
	public void exportNetwork(long networkId, Date startDate, Date endDate, boolean excludeConnectionLinks);

	void exportNetworkInBackground(long networkId, Date startDate, Date endDate, boolean excludeConnectionLinks);
	
	public String getNotificationEmailAddress();

  boolean isPending();
	
}