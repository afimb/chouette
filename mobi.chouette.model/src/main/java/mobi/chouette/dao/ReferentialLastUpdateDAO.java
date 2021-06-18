package mobi.chouette.dao;

import java.time.LocalDateTime;


public interface ReferentialLastUpdateDAO {

    LocalDateTime getLastUpdateTimestamp();

    void setLastUpdateTimestamp(LocalDateTime lastUpdateTimestamp);


}
