-- A exécuter en tant qu'utilisateur chouette
-- call by : psql -U [user] -W -f set_v15data_and_geometry.sql -v SCH=[schema] [database name]


-- Localisation des stoparea sur la base des longitudes/latitudes
UPDATE :SCH.stoparea
SET geom = PointFromText('POINT(' || longitude || ' ' || latitude || ')',4326)
where longitude is not null
AND latitude is not null;


-- Maj geom à partir des coordonnées longitudes/latitudes des deux stoparea de la connexion
UPDATE :SCH.connectionlink
SET geom = LinestringFromText(
      (select 'LINESTRING(' || sa1.longitude || ' ' || sa1.latitude || ', ' || sa2.longitude || ' ' || sa2.latitude || ')'
            from :SCH.connectionlink cl, :SCH.stoparea sa1, :SCH.stoparea sa2
            where
               cl.id = connectionlink.id
               AND cl.departureid = sa1.id       -- premier stoparea
               AND cl.arrivalid = sa2.id         -- deuxieme stoparea
       ) , 4326)
FROM :SCH.stoparea sa1, :SCH.stoparea sa2
WHERE   -- condition de la mise à jour : les deux points de la connexion possedent des coordonnées
    (:SCH.connectionlink.departureid = sa1.id
	AND sa1.latitude is not null
	AND sa2.longitude is not null)
     AND
	(:SCH.connectionlink.arrivalid = sa2.id
	AND sa2.latitude is not null
	AND sa2.longitude is not null);

