package fr.certu.chouette.model.neptune.type;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;
/**
 * 
 * @author mamadou keira
 *
 */
public class FacilityLocation implements Serializable {
	private static final long serialVersionUID = -7881467215151257373L;
	 /**
     * Field longitude.
     */
	@Getter @Setter private BigDecimal longitude;
    /**
     * Field latitude.
     */
	@Getter @Setter private BigDecimal latitude;
    /**
     * Field longLatType.
     */
	@Getter @Setter private LongLatTypeEnum longLatType;
    /**
     * Field address.
     */
	@Getter @Setter private Address address;
    /**
     * Field projectedPoint.
     */
	@Getter @Setter private ProjectedPoint projectedPoint;
    /**
     * Field containedIn.
     */
	@Getter @Setter private String containedIn;
}
