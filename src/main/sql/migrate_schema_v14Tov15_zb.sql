-- A exécuter en tant qu'utilisateur chouette
-- call by : psql -U [user] -W -f migrate_schema_v14Tov15.sql -v SCH=[schema] -v SCH_QUOTE="'[schema]'" [database name]

--   TABLE Stoparea

-- Création de la colonne géométrique
-- ALTER TABLE stoparea DROP COLUMN geom;
SELECT AddGeometryColumn(:SCH_QUOTE, 'stoparea','geom',4326,'POINT',2);  -- EPSG:4326 common coordinate reference system that refers to WGS84 as (latitude, longitude) pair coordinates in degrees with Greenwich as the central meridian
COMMENT ON COLUMN :SCH.stoparea.geom IS 'Stop area location, EPSG:4326 refers to WGS84 as (latitude, longitude) pair coordinates in degrees with Greenwich as the central meridian';
CREATE INDEX stopareageom_idx ON chouette.stoparea
   USING GIST ( geom GIST_GEOMETRY_OPS );


-- Table connectionlink
-- ALTER TABLE connectionlink DROP COLUMN geom;
SELECT AddGeometryColumn(:SCH_QUOTE, 'connectionlink','geom',4326,'LINESTRING',2);  -- EPSG:4326 common coordinate reference system that refers to WGS84 as (latitude, longitude) pair coordinates in degrees with Greenwich as the central meridian
COMMENT ON COLUMN :SCH.connectionlink.geom IS 'Connect the two Stop Area that make the connection';
CREATE INDEX connectionlinkgeom_idx ON connectionlink
   USING GIST ( geom GIST_GEOMETRY_OPS );


