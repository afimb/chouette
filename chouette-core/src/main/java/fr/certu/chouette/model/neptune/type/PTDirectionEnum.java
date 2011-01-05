package fr.certu.chouette.model.neptune.type;

public enum PTDirectionEnum implements java.io.Serializable
{

	//------------------/
	//- Enum Constants -/
	//------------------/

	/**
	 * Constant NORTH
	 */
	NORTH("North"),
	/**
	 * Constant NORTHEAST
	 */
	NORTHEAST("NorthEast"),
	/**
	 * Constant EAST
	 */
	EAST("East"),
	/**
	 * Constant SOUTHEAST
	 */
	SOUTHEAST("SouthEast"),
	/**
	 * Constant SOUTH
	 */
	SOUTH("South"),
	/**
	 * Constant SOUTHWEST
	 */
	SOUTHWEST("SouthWest"),
	/**
	 * Constant WEST
	 */
	WEST("West"),
	/**
	 * Constant NORTHWEST
	 */
	NORTHWEST("NorthWest"),
	/**
	 * Constant CLOCKWISE
	 */
	CLOCKWISE("ClockWise"),
	/**
	 * Constant COUNTERCLOCKWISE
	 */
	COUNTERCLOCKWISE("CounterClockWise"),
	/**
	 * Constant A
	 */
	A("A"),
	/**
	 * Constant R
	 */
	R("R");

	//--------------------------/
	//- Class/Member Variables -/
	//--------------------------/

	/**
	 * Field value.
	 */
	private final java.lang.String value;


	//----------------/
	//- Constructors -/
	//----------------/

	private PTDirectionEnum(final java.lang.String value) 
	{
		this.value = value;
	}


	//-----------/
	//- Methods -/
	//-----------/

	/**
	 * Method fromValue.
	 * 
	 * @param value
	 * @return the constant for this value
	 */
	public static PTDirectionEnum fromValue(final java.lang.String value) 
	{
		for (PTDirectionEnum c: PTDirectionEnum.values()) 
		{
			if (c.value.equals(value)) 
			{
				return c;
			}
		}
		throw new IllegalArgumentException(value);
	}


	/**
	 * Method toString.
	 * 
	 * @return the value of this constant
	 */
	public java.lang.String toString()
	{
		return this.value;
	}

	/**
	 * Method value.
	 * 
	 * @return the value of this constant
	 */
	public java.lang.String value() 
	{
		return this.value;
	}



}
