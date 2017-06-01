package mobi.chouette.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import java.util.Date;
import java.util.GregorianCalendar;

@Entity
@Table(name = "codespaces")
@NoArgsConstructor
public class Codespace extends NeptuneObject {

    private static final long serialVersionUID = 4540292075146449057L;

    @Getter
    @Setter
    @GenericGenerator(name = "codespaces_id_seq", strategy = "mobi.chouette.persistence.hibernate.ChouetteIdentifierGenerator", parameters = {
            @Parameter(name = "sequence_name", value = "codespaces_id_seq"),
            @Parameter(name = "increment_size", value = "10")})
    @GeneratedValue(generator = "codespaces_id_seq")
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Getter
    @Column(name = "xmlns", nullable = false, unique = true, length = 3)
    private String xmlns;

    public void setXmlns(String value) {
        this.xmlns = value;
    }

    @Getter
    @Column(name = "xmlns_url", nullable = false, unique = true)
    private String xmlnsUrl;

    public void setXmlnsUrl(String value) {
        this.xmlnsUrl = value;
    }

    @Getter
    @Setter
    @Column(name = "created_at")
    private Date createdAt = GregorianCalendar.getInstance().getTime();

    @Getter
    @Setter
    @Column(name = "updated_at")
    private Date updatedAt = new Date(createdAt.getTime());

}
