package fr.certu.chouette.struts.validator;

import java.math.BigDecimal;

import org.apache.log4j.Logger;

import com.opensymphony.xwork2.validator.ValidationException;
import com.opensymphony.xwork2.validator.validators.FieldValidatorSupport;

public class BigDecimalValidator extends  FieldValidatorSupport 
{
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger
			.getLogger(BigDecimalValidator.class);
	
	private int precision;
	private int scale;
	
	public void validate(Object object) throws ValidationException 
	{
		String fieldName = getFieldName();

		BigDecimal bigDecimal = ( BigDecimal)getFieldValue(fieldName, object);
		BigDecimal max = new BigDecimal( Math.pow( 10d, precision-scale));
		
		if ( bigDecimal!=null 
		&& (precision<bigDecimal.precision() || scale<bigDecimal.scale()
				|| bigDecimal.compareTo( max)>0))
			addFieldError(fieldName, object);
	}

	public void setPrecision(int precision) {
		this.precision = precision;
	}

	public void setScale(int scale) {
		this.scale = scale;
	}

	public int getPrecision() {
		return precision;
	}

	public int getScale() {
		return scale;
	}
}
