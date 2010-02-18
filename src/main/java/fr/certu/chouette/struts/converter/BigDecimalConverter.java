package fr.certu.chouette.struts.converter;

import java.math.BigDecimal;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.util.StrutsTypeConverter;

import com.opensymphony.xwork2.conversion.TypeConversionException;

public final class BigDecimalConverter extends StrutsTypeConverter
{
	private static final Log log = LogFactory.getLog(BigDecimalConverter.class);
	
	@Override
	public Object convertFromString(Map arg0, String[] value, Class arg2) {
		if (value.length != 1)
		{
			throw new TypeConversionException();
		}
		String valeur = value[0];
		
		if (valeur == null || valeur.trim().length()==0) {
            return null;
        }
		
		BigDecimal bigDecimal = null;
		try
		{
			// prise en compte des saisies avec virgule
			valeur = valeur.replace( ',', '.');
			
			bigDecimal = new BigDecimal( valeur);
			if ( bigDecimal==null)
				throw new TypeConversionException();
		}
		catch( NumberFormatException e)
		{
			log.error( "Echec de la lecture de "+valeur+", msg"+e.getMessage());
			throw new TypeConversionException();
		}
		return bigDecimal;
	}

	@Override
	public String convertToString(Map arg0, Object arg1) {
		if (!arg1.getClass().equals(BigDecimal.class))
		{
			throw new TypeConversionException();
		}
		return arg1.toString();
	}
}