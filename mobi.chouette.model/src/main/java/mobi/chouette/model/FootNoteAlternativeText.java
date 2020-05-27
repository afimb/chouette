package mobi.chouette.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@ToString(callSuper=true)
@Entity
@Table(name = "footnote_alternative_texts")
@NoArgsConstructor
@Cacheable
public class FootNoteAlternativeText  extends NeptuneIdentifiedObject {


    @Getter
    @Setter
    @GenericGenerator(name = "footnote_alternative_texts_id_seq", strategy = "mobi.chouette.persistence.hibernate.ChouetteIdentifierGenerator", parameters = {
            @Parameter(name = "sequence_name", value = "footnote_alternative_texts_id_seq"),
            @Parameter(name = "increment_size", value = "10") })
    @GeneratedValue(generator = "footnote_alternative_texts_id_seq")
    @Id
    @Column(name = "id", nullable = false)
    protected Long id;

    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "footnote_id")
    private Footnote footnote;

    @Getter
    @Setter
    @Column(name = "text")
    private String text;

    @Getter
    @Setter
    @Column(name = "language")
    private  String language;


}
