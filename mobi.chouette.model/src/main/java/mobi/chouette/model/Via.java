package mobi.chouette.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

/**
 * Chouette Via : stops passed (via) by a vehicle on the way to final destination
 *
 * @since 3.4.2
 */

@Entity
@Table(name = "vias")
@NoArgsConstructor
public class Via extends NeptuneObject {

    private static final long serialVersionUID = 5387017150733888047L;

    @Getter
    @Setter
    @Id
    @Column(name = "id", nullable = false)
    protected Long id;

    /**
     * parent destination display reference
     *
     * @return The actual value
     */
    @Getter
    @ManyToOne(cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "parent_destination_display_id")
    private DestinationDisplay parentDestinationDisplay;

    /**
     * set parent destination display
     *
     * @param parentDestinationDisplay New value
     */
    public void setParentDestinationDisplay(DestinationDisplay parentDestinationDisplay) {
        if (this.parentDestinationDisplay != null) {
            this.parentDestinationDisplay.getVias().remove(this);
        }
        this.parentDestinationDisplay = parentDestinationDisplay;
        if (parentDestinationDisplay != null) {
            parentDestinationDisplay.getVias().add(this);
        }
    }

    /**
     * destination display reference <br/>
     *
     * @return The actual value
     */

    @Getter
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "destination_display_id")
    private DestinationDisplay destinationDisplay;

    /**
     * set the destination display for this via <br/>

     * @param destinationDisplay new value
     */
    public void setDestinationDisplay(DestinationDisplay destinationDisplay) {
        this.destinationDisplay = destinationDisplay;
    }

}
