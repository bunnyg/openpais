--Example patient 0001:
--TCGA-02-0001-01Z-00-DX1_20x_20x_NS-MORPH_1, 203172
--TCGA-02-0001-01Z-00-DX2_20x_20x_NS-MORPH_1, 511628  
--TCGA-02-0001-01Z-00-DX3_20x_20x_NS-MORPH_1, 574582

-- Default: pais_uid='CGA-02-0001-01Z-00-DX2_20x_20x_NS-MORPH_1' patientid ='001'

-- Retrieve feature vector for nuclei segmented from algorithm 'NS-MORPH'
-- with first parameter set for each patient:


-----------------------------------------------------------------------
echo 'meanfeaturevectorbypatient';
insert into bchmk.querytime SELECT 'meanfeaturevectorbypatient', current timestamp, null  FROM sysibm.sysdummy1;

SELECT p.patientid, 
       AVG(AREA),AVG(PERIMETER),AVG(ECCENTRICITY),AVG(CIRCULARITY),AVG(MAJOR_AXIS),AVG(MINOR_AXIS), 
       AVG(EXTENT_RATIO), AVG(MEAN_INTENSITY), AVG(MAX_INTENSITY),AVG(MIN_INTENSITY),AVG(STD_INTENSITY),
       AVG(ENTROPY),AVG(ENERGY),AVG(SKEWNESS),AVG(KURTOSIS),AVG(MEAN_GRADIENT_MAGNITUDE),
       AVG(STD_GRADIENT_MAGNITUDE),AVG(ENTROPY_GRADIENT_MAGNITUDE),AVG(ENERGY_GRADIENT_MAGNITUDE),
       AVG(SKEWNESS_GRADIENT_MAGNITUDE),AVG(KURTOSIS_GRADIENT_MAGNITUDE),AVG(SUM_CANNY_PIXEL),
       AVG(MEAN_CANNY_PIXEL)
FROM  pais.calculation_flat f, pais.collection c, pais.patient p
WHERE f.pais_uid=c.pais_uid AND c.methodname ='NS-MORPH' AND c.sequencenumber ='1' and p.pais_uid = c.pais_uid
GROUP BY p.patientid;
update bchmk.querytime set endtime = current timestamp where queryname = 'meanfeaturevectorbypatient';

-----------------------------------------------------------------------

echo 'meanfeaturevectorbyslide';
insert into bchmk.querytime SELECT 'meanfeaturevectorbyslide', current timestamp, null  FROM sysibm.sysdummy1;

SELECT c.pais_uid, 
       AVG(AREA),AVG(PERIMETER),AVG(ECCENTRICITY),AVG(CIRCULARITY),AVG(MAJOR_AXIS),AVG(MINOR_AXIS), 
       AVG(EXTENT_RATIO), AVG(MEAN_INTENSITY), AVG(MAX_INTENSITY),AVG(MIN_INTENSITY),AVG(STD_INTENSITY),
       AVG(ENTROPY),AVG(ENERGY),AVG(SKEWNESS),AVG(KURTOSIS),AVG(MEAN_GRADIENT_MAGNITUDE),
       AVG(STD_GRADIENT_MAGNITUDE),AVG(ENTROPY_GRADIENT_MAGNITUDE),AVG(ENERGY_GRADIENT_MAGNITUDE),
       AVG(SKEWNESS_GRADIENT_MAGNITUDE),AVG(KURTOSIS_GRADIENT_MAGNITUDE),AVG(SUM_CANNY_PIXEL),
       AVG(MEAN_CANNY_PIXEL)
FROM  pais.calculation_flat f, pais.collection c
WHERE f.pais_uid=c.pais_uid AND c.methodname ='NS-MORPH' AND c.sequencenumber ='1'
GROUP BY c.pais_uid;
update bchmk.querytime set endtime = current timestamp where queryname = 'meanfeaturevectorbyslide';

-----------------------------------------------------------------------
echo 'meanfeaturevectorsinglepatient';
insert into bchmk.querytime SELECT 'meanfeaturevectorsinglepatient', current timestamp, null  FROM sysibm.sysdummy1;

SELECT  
       AVG(AREA),AVG(PERIMETER),AVG(ECCENTRICITY),AVG(CIRCULARITY),AVG(MAJOR_AXIS),AVG(MINOR_AXIS), 
       AVG(EXTENT_RATIO), AVG(MEAN_INTENSITY), AVG(MAX_INTENSITY),AVG(MIN_INTENSITY),AVG(STD_INTENSITY),
       AVG(ENTROPY),AVG(ENERGY),AVG(SKEWNESS),AVG(KURTOSIS),AVG(MEAN_GRADIENT_MAGNITUDE),
       AVG(STD_GRADIENT_MAGNITUDE),AVG(ENTROPY_GRADIENT_MAGNITUDE),AVG(ENERGY_GRADIENT_MAGNITUDE),
       AVG(SKEWNESS_GRADIENT_MAGNITUDE),AVG(KURTOSIS_GRADIENT_MAGNITUDE),AVG(SUM_CANNY_PIXEL),
       AVG(MEAN_CANNY_PIXEL)
FROM  pais.calculation_flat f, pais.collection c, pais.patient p
WHERE f.pais_uid=c.pais_uid AND c.methodname ='NS-MORPH' AND c.sequencenumber ='1' and p.pais_uid = c.pais_uid and p.patientid ='0001';

update bchmk.querytime set endtime = current timestamp where queryname = 'meanfeaturevectorsinglepatient';

-----------------------------------------------------------------------
echo 'meanfeaturevectorsingleslide';
insert into bchmk.querytime SELECT 'meanfeaturevectorsingleslide', current timestamp, null  FROM sysibm.sysdummy1;

SELECT  
       AVG(AREA),AVG(PERIMETER),AVG(ECCENTRICITY),AVG(CIRCULARITY),AVG(MAJOR_AXIS),AVG(MINOR_AXIS), 
       AVG(EXTENT_RATIO), AVG(MEAN_INTENSITY), AVG(MAX_INTENSITY),AVG(MIN_INTENSITY),AVG(STD_INTENSITY),
       AVG(ENTROPY),AVG(ENERGY),AVG(SKEWNESS),AVG(KURTOSIS),AVG(MEAN_GRADIENT_MAGNITUDE),
       AVG(STD_GRADIENT_MAGNITUDE),AVG(ENTROPY_GRADIENT_MAGNITUDE),AVG(ENERGY_GRADIENT_MAGNITUDE),
       AVG(SKEWNESS_GRADIENT_MAGNITUDE),AVG(KURTOSIS_GRADIENT_MAGNITUDE),AVG(SUM_CANNY_PIXEL),
       AVG(MEAN_CANNY_PIXEL)
FROM  pais.calculation_flat f, pais.collection c
WHERE f.pais_uid=c.pais_uid AND c.methodname ='NS-MORPH' AND c.sequencenumber ='1' and c.pais_uid ='TCGA-02-0001-01Z-00-DX2_20x_20x_NS-MORPH_1';

update bchmk.querytime set endtime = current timestamp where queryname = 'meanfeaturevectorsingleslide';

-----------------------------------------------------------------------

echo 'meanfeaturevectorbygenetypeperslide';
insert into bchmk.querytime SELECT 'meanfeaturevectorbygenetypeperslide', current timestamp, null  FROM sysibm.sysdummy1;
-- Find the average features for each gene subtype for each slide:
SELECT c.pais_uid, pc.subtype, AVG(area), AVG(perimeter), AVG(sum_canny_pixel) 
FROM   pais.calculation_flat c, TCGA.PATIENT_CHARACTERISTIC pc, pais.patient p 
WHERE  p.patientid = pc.patient_id AND p.pais_uid = c.pais_uid 
GROUP BY c.pais_uid, pc.subtype;
update bchmk.querytime set endtime = current timestamp where queryname = 'meanfeaturevectorbygenetypeperslide';


-----------------------------------------------------------------------

echo 'areacovarianceall';
insert into bchmk.querytime SELECT 'areacovarianceall', current timestamp, null  FROM sysibm.sysdummy1;
SELECT pais_uid,
      COVARIANCE(AREA, PERIMETER) AS COV_AREA_PERIMETER,
      COVARIANCE(AREA, ECCENTRICITY) AS COV_AREA_ECCENTRICITY,
      COVARIANCE(AREA, CIRCULARITY) AS COV_AREA_CIRCULARITY,
      COVARIANCE(AREA, MAJOR_AXIS) AS COV_AREA_MAJOR_AXIS,
      COVARIANCE(AREA, MINOR_AXIS) AS COV_AREA_MINOR_AXIS, 
      COVARIANCE(AREA, EXTENT_RATIO) AS COV_AREA_EXTENT_RATIO, 
      COVARIANCE(AREA, MEAN_INTENSITY) AS COV_AREA_MEAN_INTENSITY, 
      COVARIANCE(AREA, MAX_INTENSITY) AS COV_AREA_MAX_INTENSITY,
      COVARIANCE(AREA, MIN_INTENSITY) AS COV_AREA_MIN_INTENSITY,
      COVARIANCE(AREA, STD_INTENSITY) AS COV_AREA_STD_INTENSITY, 
      COVARIANCE(AREA, ENTROPY) AS COV_AREA_ENTROPY,
      COVARIANCE(AREA, ENERGY) AS COV_AREA_ENERGY,
      COVARIANCE(AREA, SKEWNESS) AS COV_AREA_SKEWNESS,
      COVARIANCE(AREA, KURTOSIS) AS COV_AREA_KURTOSIS,
      COVARIANCE(AREA, MEAN_GRADIENT_MAGNITUDE) AS COV_AREA_MEAN_GRADIENT_MAGNITUDE,
      COVARIANCE(AREA, STD_GRADIENT_MAGNITUDE) AS COV_AREA_STD_GRADIENT_MAGNITUDE, 
      COVARIANCE(AREA, ENTROPY_GRADIENT_MAGNITUDE) AS COV_AREA_ENTROPY_GRADIENT_MAGNITUDE, 
      COVARIANCE(AREA, ENERGY_GRADIENT_MAGNITUDE) AS COV_AREA_ENERGY_GRADIENT_MAGNITUDE,
      COVARIANCE(AREA, SKEWNESS_GRADIENT_MAGNITUDE) AS COV_AREA_SKEWNESS_GRADIENT_MAGNITUDE,
      COVARIANCE(AREA, KURTOSIS_GRADIENT_MAGNITUDE) AS COV_AREA_KURTOSIS_GRADIENT_MAGNITUDE,
      COVARIANCE(AREA, SUM_CANNY_PIXEL) AS COV_AREA_SUM_CANNY_PIXEL,
      COVARIANCE(AREA, MEAN_CANNY_PIXEL) AS COV_AREA_MEAN_CANNY_PIXEL
FROM  pais.calculation_flat  GROUP BY pais_uid;
update bchmk.querytime set endtime = current timestamp where queryname = 'areacovarianceall';

-----------------------------------------------------------------------

echo 'areacovariancesinglepatient';
insert into bchmk.querytime SELECT 'areacovariancesinglepatient', current timestamp, null  FROM sysibm.sysdummy1;
SELECT 
      COVARIANCE(AREA, PERIMETER) AS COV_AREA_PERIMETER,
      COVARIANCE(AREA, ECCENTRICITY) AS COV_AREA_ECCENTRICITY,
      COVARIANCE(AREA, CIRCULARITY) AS COV_AREA_CIRCULARITY,
      COVARIANCE(AREA, MAJOR_AXIS) AS COV_AREA_MAJOR_AXIS,
      COVARIANCE(AREA, MINOR_AXIS) AS COV_AREA_MINOR_AXIS, 
      COVARIANCE(AREA, EXTENT_RATIO) AS COV_AREA_EXTENT_RATIO, 
      COVARIANCE(AREA, MEAN_INTENSITY) AS COV_AREA_MEAN_INTENSITY, 
      COVARIANCE(AREA, MAX_INTENSITY) AS COV_AREA_MAX_INTENSITY,
      COVARIANCE(AREA, MIN_INTENSITY) AS COV_AREA_MIN_INTENSITY,
      COVARIANCE(AREA, STD_INTENSITY) AS COV_AREA_STD_INTENSITY, 
      COVARIANCE(AREA, ENTROPY) AS COV_AREA_ENTROPY,
      COVARIANCE(AREA, ENERGY) AS COV_AREA_ENERGY,
      COVARIANCE(AREA, SKEWNESS) AS COV_AREA_SKEWNESS,
      COVARIANCE(AREA, KURTOSIS) AS COV_AREA_KURTOSIS,
      COVARIANCE(AREA, MEAN_GRADIENT_MAGNITUDE) AS COV_AREA_MEAN_GRADIENT_MAGNITUDE,
      COVARIANCE(AREA, STD_GRADIENT_MAGNITUDE) AS COV_AREA_STD_GRADIENT_MAGNITUDE, 
      COVARIANCE(AREA, ENTROPY_GRADIENT_MAGNITUDE) AS COV_AREA_ENTROPY_GRADIENT_MAGNITUDE, 
      COVARIANCE(AREA, ENERGY_GRADIENT_MAGNITUDE) AS COV_AREA_ENERGY_GRADIENT_MAGNITUDE,
      COVARIANCE(AREA, SKEWNESS_GRADIENT_MAGNITUDE) AS COV_AREA_SKEWNESS_GRADIENT_MAGNITUDE,
      COVARIANCE(AREA, KURTOSIS_GRADIENT_MAGNITUDE) AS COV_AREA_KURTOSIS_GRADIENT_MAGNITUDE,
      COVARIANCE(AREA, SUM_CANNY_PIXEL) AS COV_AREA_SUM_CANNY_PIXEL,
      COVARIANCE(AREA, MEAN_CANNY_PIXEL) AS COV_AREA_MEAN_CANNY_PIXEL
FROM  pais.calculation_flat f, pais.patient p
WHERE f.pais_uid = p.pais_uid  AND p.patientid ='0001';
update bchmk.querytime set endtime = current timestamp where queryname = 'areacovariancesinglepatient';


-----------------------------------------------------------------------

echo 'areacovariancesingleslide';
insert into bchmk.querytime SELECT 'areacovariancesingleslide', current timestamp, null  FROM sysibm.sysdummy1;
SELECT 
      COVARIANCE(AREA, PERIMETER) AS COV_AREA_PERIMETER,
      COVARIANCE(AREA, ECCENTRICITY) AS COV_AREA_ECCENTRICITY,
      COVARIANCE(AREA, CIRCULARITY) AS COV_AREA_CIRCULARITY,
      COVARIANCE(AREA, MAJOR_AXIS) AS COV_AREA_MAJOR_AXIS,
      COVARIANCE(AREA, MINOR_AXIS) AS COV_AREA_MINOR_AXIS, 
      COVARIANCE(AREA, EXTENT_RATIO) AS COV_AREA_EXTENT_RATIO, 
      COVARIANCE(AREA, MEAN_INTENSITY) AS COV_AREA_MEAN_INTENSITY, 
      COVARIANCE(AREA, MAX_INTENSITY) AS COV_AREA_MAX_INTENSITY,
      COVARIANCE(AREA, MIN_INTENSITY) AS COV_AREA_MIN_INTENSITY,
      COVARIANCE(AREA, STD_INTENSITY) AS COV_AREA_STD_INTENSITY, 
      COVARIANCE(AREA, ENTROPY) AS COV_AREA_ENTROPY,
      COVARIANCE(AREA, ENERGY) AS COV_AREA_ENERGY,
      COVARIANCE(AREA, SKEWNESS) AS COV_AREA_SKEWNESS,
      COVARIANCE(AREA, KURTOSIS) AS COV_AREA_KURTOSIS,
      COVARIANCE(AREA, MEAN_GRADIENT_MAGNITUDE) AS COV_AREA_MEAN_GRADIENT_MAGNITUDE,
      COVARIANCE(AREA, STD_GRADIENT_MAGNITUDE) AS COV_AREA_STD_GRADIENT_MAGNITUDE, 
      COVARIANCE(AREA, ENTROPY_GRADIENT_MAGNITUDE) AS COV_AREA_ENTROPY_GRADIENT_MAGNITUDE, 
      COVARIANCE(AREA, ENERGY_GRADIENT_MAGNITUDE) AS COV_AREA_ENERGY_GRADIENT_MAGNITUDE,
      COVARIANCE(AREA, SKEWNESS_GRADIENT_MAGNITUDE) AS COV_AREA_SKEWNESS_GRADIENT_MAGNITUDE,
      COVARIANCE(AREA, KURTOSIS_GRADIENT_MAGNITUDE) AS COV_AREA_KURTOSIS_GRADIENT_MAGNITUDE,
      COVARIANCE(AREA, SUM_CANNY_PIXEL) AS COV_AREA_SUM_CANNY_PIXEL,
      COVARIANCE(AREA, MEAN_CANNY_PIXEL) AS COV_AREA_MEAN_CANNY_PIXEL
FROM  pais.calculation_flat f
WHERE f.pais_uid = 'TCGA-02-0001-01Z-00-DX2_20x_20x_NS-MORPH_1';
update bchmk.querytime set endtime = current timestamp where queryname = 'areacovariancesingleslide';


--select patientid, count(*) from pais.collection c, pais.patient p where  c.pais_uid = p.pais_uid  group by patientid;

-- The following patient 0451 has four slides, with 1486,959 nuclei in the slides.
-- select count(*) from pais.markup_polygon m, pais.patient p,  pais.collection c where c.pais_uid = p.pais_uid and m.pais_uid = c.pais_uid and p.patientid ='0451';



-----------------------------------------------------------------------

echo 'areacovariancesingleslidetumorregion';
insert into bchmk.querytime SELECT 'areacovariancesingleslidetumorregion', current timestamp, null  FROM sysibm.sysdummy1;
SELECT 
      COVARIANCE(AREA, PERIMETER) AS COV_AREA_PERIMETER,
      COVARIANCE(AREA, ECCENTRICITY) AS COV_AREA_ECCENTRICITY,
      COVARIANCE(AREA, CIRCULARITY) AS COV_AREA_CIRCULARITY,
      COVARIANCE(AREA, MAJOR_AXIS) AS COV_AREA_MAJOR_AXIS,
      COVARIANCE(AREA, MINOR_AXIS) AS COV_AREA_MINOR_AXIS, 
      COVARIANCE(AREA, EXTENT_RATIO) AS COV_AREA_EXTENT_RATIO, 
      COVARIANCE(AREA, MEAN_INTENSITY) AS COV_AREA_MEAN_INTENSITY, 
      COVARIANCE(AREA, MAX_INTENSITY) AS COV_AREA_MAX_INTENSITY,
      COVARIANCE(AREA, MIN_INTENSITY) AS COV_AREA_MIN_INTENSITY,
      COVARIANCE(AREA, STD_INTENSITY) AS COV_AREA_STD_INTENSITY, 
      COVARIANCE(AREA, ENTROPY) AS COV_AREA_ENTROPY,
      COVARIANCE(AREA, ENERGY) AS COV_AREA_ENERGY,
      COVARIANCE(AREA, SKEWNESS) AS COV_AREA_SKEWNESS,
      COVARIANCE(AREA, KURTOSIS) AS COV_AREA_KURTOSIS,
      COVARIANCE(AREA, MEAN_GRADIENT_MAGNITUDE) AS COV_AREA_MEAN_GRADIENT_MAGNITUDE,
      COVARIANCE(AREA, STD_GRADIENT_MAGNITUDE) AS COV_AREA_STD_GRADIENT_MAGNITUDE, 
      COVARIANCE(AREA, ENTROPY_GRADIENT_MAGNITUDE) AS COV_AREA_ENTROPY_GRADIENT_MAGNITUDE, 
      COVARIANCE(AREA, ENERGY_GRADIENT_MAGNITUDE) AS COV_AREA_ENERGY_GRADIENT_MAGNITUDE,
      COVARIANCE(AREA, SKEWNESS_GRADIENT_MAGNITUDE) AS COV_AREA_SKEWNESS_GRADIENT_MAGNITUDE,
      COVARIANCE(AREA, KURTOSIS_GRADIENT_MAGNITUDE) AS COV_AREA_KURTOSIS_GRADIENT_MAGNITUDE,
      COVARIANCE(AREA, SUM_CANNY_PIXEL) AS COV_AREA_SUM_CANNY_PIXEL,
      COVARIANCE(AREA, MEAN_CANNY_PIXEL) AS COV_AREA_MEAN_CANNY_PIXEL
FROM pais.calculation_flat f,
( SELECT m.markup_id, m.tilename
  FROM  pais.markup_polygon m, pais.markup_polygon_human h,  pais.observation_quantification_nominal o
  WHERE h.pais_uid = 'TCGA-02-0001-01Z-00-DX2_20x_20x_TR-HUMAN_1' AND 
      m.pais_uid = 'TCGA-02-0001-01Z-00-DX2_20x_20x_NS-MORPH_1' AND 
      o.pais_uid = 'TCGA-02-0001-01Z-00-DX2_20x_20x_TR-HUMAN_1' AND       
      o.observation_name='Normal and Tumor Region Classification' AND 	
      o.quantification_value = 'Tumor' AND 
      DB2GSE.ST_CONTAINS(h.polygon, m.polygon) = 1
)AS M
WHERE f.tilename = M.tilename and f.markup_id = m.markup_id and f.pais_uid = 'TCGA-02-0001-01Z-00-DX2_20x_20x_NS-MORPH_1';

update bchmk.querytime set endtime = current timestamp where queryname = 'areacovariancesingleslidetumorregion';




