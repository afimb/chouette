-- TimetableVehicleJourney

ALTER TABLE timetablevehiclejourney DROP CONSTRAINT IF EXISTS timetablevehiclejourney_pkey;

ALTER TABLE :SCH.timetablevehiclejourney DROP COLUMN IF EXISTS id;

ALTER TABLE :SCH.timetablevehiclejourney
  ADD CONSTRAINT timetablevehiclejourney_pkey PRIMARY KEY(timetableid, vehiclejourneyid);
  
-- VehicleJourneyAtStop

ALTER TABLE :SCH.vehiclejourneyatstop DROP CONSTRAINT IF EXISTS vehiclejourneyatstop_pkey;

ALTER TABLE :SCH.vehiclejourneyatstop DROP COLUMN IF EXISTS id;

ALTER TABLE :SCH.vehiclejourneyatstop
  ADD CONSTRAINT vehiclejourneyatstop_pkey PRIMARY KEY(vehiclejourneyid, stoppointid);


