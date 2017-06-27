package mobi.chouette.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Parameter;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Chouette DestinationDisplay : direction and destination info displayed for each stop at a vehicle
 *
 * @since 3.4.2
 */

@Entity
@Table(name = "destination_displays")
@NoArgsConstructor
public class DestinationDisplay extends NeptuneObject {

    private static final long serialVersionUID = 6790138295242844540L;

    @Getter
    @Setter
    @GenericGenerator(name = "destination_displays_id_seq", strategy = "mobi.chouette.persistence.hibernate.ChouetteIdentifierGenerator", parameters = {
            @Parameter(name = "sequence_name", value = "destination_displays_id_seq"),
            @Parameter(name = "increment_size", value = "10")})
    @GeneratedValue(generator = "destination_displays_id_seq")
    @Id
    @Column(name = "id", nullable = false)
    protected Long id;

    /**
     * name
     *
     * @return The actual value
     */
    @Getter
    @Column(name = "name")
    private String name;

    /**
     * set name <br/>
     * truncated to 255 characters if too long
     *
     * @param value New value
     */
    public void setName(String value) {
        name = StringUtils.abbreviate(value, 255);
    }

    /**
     * side text
     *
     * @return The actual value
     */
    @Getter
    @Column(name = "side_text")
    private String sideText;

    /**
     * set side text <br/>
     * truncated to 255 characters if too long
     *
     * @param value New value
     */
    public void setSideText(String value) {
        sideText = StringUtils.abbreviate(value, 255);
    }

    /**
     * front text
     *
     * @return The actual value
     */
    @Getter
    @Column(name = "front_text")
    private String frontText;

    /**
     * set front text <br/>
     * truncated to 255 characters if too long
     *
     * @param value New value
     */
    public void setFrontText(String value) {
        frontText = StringUtils.abbreviate(value, 255);
    }

    /**
     * public code
     *
     * @return The actual value
     */
    @Getter
    @Column(name = "public_code")
    private String publicCode;

    /**
     * set public code <br/>
     * truncated to 255 characters if too long
     *
     * @param value New value
     */
    public void setPublicCode(String value) {
        publicCode = StringUtils.abbreviate(value, 255);
    }

    /**
     * creation time
     *
     * @param createdAt
     * new creation time
     * @return The actual creation time
     */
    @Getter
    @Setter
    @Column(name = "created_at")
    private Date createdAt = GregorianCalendar.getInstance().getTime();

    /**
     * last update time
     *
     * @param updatedAt
     * new last update time
     * @return The actual last update time
     */
    @Getter
    @Setter
    @Column(name = "updated_at")
    private Date updatedAt = new Date(createdAt.getTime());

    /**
     * vias
     *
     * @param vias
     * New value
     * @return The actual value
     */
    @Getter
    @Setter
    @OneToMany(mappedBy = "destinationDisplay")
    private List<Via> vias = new ArrayList<Via>(0);

}
