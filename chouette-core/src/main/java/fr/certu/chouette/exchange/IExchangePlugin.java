package fr.certu.chouette.exchange;

public interface IExchangePlugin 
{
	/**
	 * get the API description for the specific exchange
	 * 
	 * @return
	 */
	FormatDescription getDescription();

}
