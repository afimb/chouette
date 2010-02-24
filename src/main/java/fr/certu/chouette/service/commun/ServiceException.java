package fr.certu.chouette.service.commun;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public  class ServiceException extends RuntimeException 
{

	private CodeIncident code;
	private CodeDetailIncident detail;
	private Object[] args;

	public ServiceException(CodeIncident code, CodeDetailIncident detail, Object...args ) 
	{
		super("");
		this.code = code;
		this.detail = detail;
		this.args = args;
	}
	public ServiceException(CodeIncident code, CodeDetailIncident detail, Throwable exception,Object...args) 
	{
		super("",exception);
		this.code = code;
		this.detail = detail;
		this.args = args;
	}
	/*
   public ServiceException(CodeIncident code, Object...args ) 
   {
      this(code,CodeDetailIncident.DEFAULT,args);
   }
	 */   
	public ServiceException(CodeIncident code, Throwable exception, Object...args) 
	{
		this(code,CodeDetailIncident.DEFAULT,exception,args);
	}


	public ServiceException(CodeIncident code, Throwable exception) 
	{
		this(code,CodeDetailIncident.DEFAULT,exception,new Object[0]);
	}

	public ServiceException(CodeIncident code) 
	{
		this(code,CodeDetailIncident.DEFAULT,new Object[0]);
	}



	public CodeIncident getCode() {
		return code;
	}
	public CodeDetailIncident getDetailCode() {
		return detail;
	}

	/* (non-Javadoc)
	 * @see java.lang.Throwable#getMessage()
	 */
	@Override
	public String getMessage()
	{
		try
		{
			ResourceBundle bundle = ResourceBundle.getBundle("ServiceException",Locale.getDefault());
			String format = "";
			try
			{
				format = bundle.getString("msg."+code.name()+"."+detail.name());
			}
			catch (MissingResourceException e)
			{
				format = bundle.getString("msg.noMsg");
				args = new Object[]{code.name(),detail.name()};
			}

			String message = MessageFormat.format(format,args);
			if (this.getCause() != null)
			{
				format = bundle.getString("msg.cause");
				message += "\n"+MessageFormat.format(format,this.getCause().getLocalizedMessage());
			}
			return message;
		}
		catch (Exception ex)
		{
			return this.getClass().getName() + ":"+ code.name()+" "+detail.name();
		}
	}
	/* (non-Javadoc)
	 * @see java.lang.Throwable#getLocalizedMessage()
	 */
	@Override
	public String getLocalizedMessage()
	{
		return getMessage();
	}

}
