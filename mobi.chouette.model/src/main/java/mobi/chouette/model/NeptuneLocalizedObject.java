/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */
package mobi.chouette.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import mobi.chouette.model.type.LongLatTypeEnum;
import mobi.chouette.model.util.Coordinate;
import mobi.chouette.model.util.CoordinateUtil;

import org.apache.commons.lang.StringUtils;

/**
 * Abstract object used for all Localized Chouette Objects
 * <p/>
 */
@SuppressWarnings("serial")
@MappedSuperclass
@ToString(callSuper=true)
public abstract class NeptuneLocalizedObject extends NeptuneIdentifiedObject {

	/**
	 * longitude in degrees
	 * 
	 * @param longitude
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@Column(name = "longitude", precision = 19, scale = 16)
	private BigDecimal longitude;

	/**
	 * latitude in degrees
	 * 
	 * @param latitude
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@Column(name = "latitude", precision = 19, scale = 16)
	private BigDecimal latitude;

	/**
	 * coordinate system for longitude and latitude <br/>
	 * only WGS84 is valid
	 * 
	 * @param longLatType
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	@Column(name = "long_lat_type")
	private LongLatTypeEnum longLatType;

	/**
	 * x coordinate on secondary projection <br/>
	 * not mapped in database, used only for exchange purpose
	 * 
	 * @param x
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@Transient
	private BigDecimal x;

	/**
	 * y coordinate on secondary projection <br/>
	 * not mapped in database, used only for exchange purpose
	 * 
	 * @param y
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@Transient
	private BigDecimal y;

	/**
	 * secondary projection system <br/>
	 * only epsg:xxx values are valids<br/>
	 * changing this value will not change x and y attributes unless using the
	 * geographic service
	 * 
	 * @param projectionType
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@Transient
	private String projectionType;

	/**
	 * country code : administrative city code
	 * 
	 * @return The actual value
	 */
	@Getter
	@Column(name = "country_code")
	private String countryCode;

	/**
	 * set country code <br/>
	 * truncated to 255 characters if too long
	 * 
	 * @param value
	 *            New value
	 */
	public void setCountryCode(String value) {
		countryCode = StringUtils.abbreviate(value, 255);
	}

	/**
	 * zip code : postal city code
	 * 
	 * @return The actual value
	 */
	@Getter
	@Column(name = "zip_code")
	private String zipCode;

	/**
	 * set zip code <br/>
	 * truncated to 255 characters if too long
	 * 
	 * @param value
	 *            New value
	 */
	public void setZipCode(String value) {
		zipCode = StringUtils.abbreviate(value, 255);
	}

	/**
	 * city name
	 * 
	 * @return The actual value
	 */
	@Getter
	@Column(name = "city_name")
	private String cityName;

	/**
	 * set city name <br/>
	 * truncated to 255 characters if too long
	 * 
	 * @param value
	 *            New value
	 */
	public void setCityName(String value) {
		cityName = StringUtils.abbreviate(value, 255);
	}

	/**
	 * street name
	 * 
	 * @return The actual value
	 */
	@Getter
	@Column(name = "street_name")
	private String streetName;

	/**
	 * set street name <br/>
	 * truncated to 255 characters if too long
	 * 
	 * @param value
	 *            New value
	 */
	public void setStreetName(String value) {
		streetName = StringUtils.abbreviate(value, 255);
	}

	/**
	 * check if longitude, latitude and longLatType are not null
	 * 
	 * @return true when all 3 attributes are present
	 */
	public boolean hasCoordinates() {
		return longitude != null && latitude != null && longLatType != null;
	}

	/**
	 * check if country code or street name are not null nor empty
	 * 
	 * @return true when one attribute is present
	 */
	public boolean hasAddress() {
		return notEmptyString(countryCode) || notEmptyString(streetName);
	}

	/**
	 * check if x, x and projectionType are not null
	 * 
	 * @return true when all 3 attributes are present
	 */
	public boolean hasProjection() {
		return x != null && x != null && notEmptyString(projectionType);
	}

	/**
	 * check if a string contains text
	 * 
	 * @param data
	 *            string to check
	 * @return true if data contains text, false otherwise
	 */
	private boolean notEmptyString(String data) {
		return data != null && !data.isEmpty();
	}

	/**
	 * project latitude and longitude on x and y if not already set<br/>
	 * clears projection if no projection is given
	 * 
	 * @param projectionType
	 *            type of projection (EPSG:xxx)
	 */
	public void toProjection(String projectionType) {
		if (!hasCoordinates())
			return;

		String projection = null;
		if (projectionType == null || projectionType.isEmpty()) {
			x = null;
			y = null;
			this.projectionType = null;
			return;
		}
		if (hasProjection())
			return;
		projection = projectionType.toUpperCase();

		Coordinate p = new Coordinate(latitude, longitude);
		Coordinate coordinate = CoordinateUtil.transform(Coordinate.WGS84,
				projection, p);
		if (coordinate != null) {
			x = coordinate.x;
			y = coordinate.y;
			this.projectionType = projection;
		}
	}

	/**
	 * project x and y on latitude and longitude if not already set
	 */
	public void toLatLong() {
		if (!hasProjection())
			return;
		if (hasCoordinates())
			return;

		String projection = null;
		if (projectionType == null || projectionType.isEmpty()) {
			projection = Coordinate.LAMBERT;
		} else {
			projection = projectionType.toUpperCase();
			if (projection.equals("WGS84")) {
				projection = Coordinate.WGS84;
			}
		}
		Coordinate p = new Coordinate(x, y);
		Coordinate coordinate = CoordinateUtil.transform(projection,
				Coordinate.WGS84, p);
		if (coordinate != null) {
			latitude = coordinate.x;
			longitude = coordinate.y;
		}
	}

}
