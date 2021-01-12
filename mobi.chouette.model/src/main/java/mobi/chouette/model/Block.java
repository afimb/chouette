package mobi.chouette.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.joda.time.LocalDate;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Entity
@Table(name = "blocks")
@NoArgsConstructor
@ToString(callSuper = true, exclude = {"vehicleJourneys"})
public class Block extends NeptuneIdentifiedObject {

    @Getter
    @Setter
    @GenericGenerator(name = "blocks_id_seq", strategy = "mobi.chouette.persistence.hibernate.ChouetteIdentifierGenerator", parameters = {
            @Parameter(name = "sequence_name", value = "blocks_id_seq"),
            @Parameter(name = "increment_size", value = "100")})
    @GeneratedValue(generator = "blocks_id_seq")
    @Id
    @Column(name = "id", nullable = false)
    protected Long id;

    /**
     * Identification of block, not intended for the public.
     */
    @Getter
    @Column(name = "private_code")
    private String privateCode;

    /**
     * set private code <br/>
     * truncated to 255 characters if too long
     *
     * @param value New value
     */
    public void setPrivateCode(String value) {
        privateCode = StringUtils.abbreviate(value, 255);

    }

    /**
     * timetables
     */
    @Getter
    @Setter
    @ManyToMany(cascade = {CascadeType.PERSIST}, fetch = FetchType.LAZY)
    @JoinTable(name = "time_tables_blocks", joinColumns = {@JoinColumn(name = "block_id", nullable = false, updatable = false)}, inverseJoinColumns = {@JoinColumn(name = "time_table_id", nullable = false, updatable = false)})
    private List<Timetable> timetables = new ArrayList<>(0);

    /**
     * Vehicle Journeys.
     */
    @Getter
    @Setter
    @ManyToMany(cascade = {CascadeType.PERSIST}, fetch = FetchType.EAGER)
    @OrderColumn(name = "position")
    @JoinTable(name = "blocks_vehicle_journeys", joinColumns = {@JoinColumn(name = "block_id")}, inverseJoinColumns = {@JoinColumn(name = "vehicle_journey_id")})
    private List<VehicleJourney> vehicleJourneys = new ArrayList<>();

    public void addVehicleJourney(VehicleJourney vehicleJourney) {
        if (vehicleJourney != null) {
            vehicleJourney.getBlocks().add(this);
            vehicleJourneys.add(vehicleJourney);
        }
    }

    public boolean hasActiveTimetablesOnPeriod(LocalDate startDate, LocalDate endDate) {
        return getTimetables().stream().anyMatch(t -> t.isActiveOnPeriod(startDate, endDate));
    }

    public boolean filter(Date startDate, Date endDate) {
        return hasActiveTimetablesOnPeriod(new LocalDate(startDate), new LocalDate(endDate));
    }
}
