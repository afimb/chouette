package mobi.chouette;


import org.postgis.DriverWrapper;
import org.postgresql.PGConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;

/**
 * This class extends the PostGIS JDBC driver to fix a compatibility issue between Chouette and recent versions of the
 * POSTGIS/PostgreSQL JDBC driver.
 * Chouette uses PostGIS JDBC (https://github.com/postgis/postgis-java/tree/master/jdbc) for transparently mapping
 * PostGIS geometries into Java objects.
 * The mapping is performed by the PostGIS JDBC driver wrapper
 * (@see {@link DriverWrapper.TypesAdder80#addGeometries(Connection)})
 * However the wrapper assumes that the PostGIS types are defined either in the public schema or the current schema.
 * Since Chouette2 requires that the PostGIS extension is installed in a separate schema named "shared_extensions"
 * (see https://github.com/influitive/apartment/issues/22 , https://github.com/influitive/apartment/issues/534
 * and https://github.com/influitive/apartment)
 * the JDBC type mapping fails leading to the following exception when Hibernate Spatial tries to retrieve geometries:
 * java.lang.IllegalArgumentException: Can't convert object of type org.postgresql.util.PGobject
 * at org.hibernate.spatial.dialect.postgis.PGGeometryValueExtractor.toJTS(PGGeometryValueExtractor.java:113)
 * <p>
 * This extended wrapper allows for resolving PostGIS types defined in the "shared_extensions" schema.
 */
public class ChouettePostGisDriverWrapper extends DriverWrapper {

    public ChouettePostGisDriverWrapper() throws SQLException {
        typesAdder = new ChouetteTypesAdder();
    }

    protected static class ChouetteTypesAdder extends TypesAdder80 {

        @Override
        public void addGeometries(Connection conn) throws SQLException {
            super.addGeometries(conn);
            PGConnection pgconn = (PGConnection) conn;
            pgconn.addDataType("shared_extensions.geometry", org.postgis.PGgeometry.class);
            pgconn.addDataType("\"shared_extensions\".\"geometry\"", org.postgis.PGgeometry.class);
        }
    }

    static {
        try {
            java.sql.DriverManager.registerDriver(new ChouettePostGisDriverWrapper());
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error registering Chouette PostGIS Wrapper Driver", e);
        }
    }
}
