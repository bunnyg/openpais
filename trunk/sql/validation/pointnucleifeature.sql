
WITH t as(
select m.pais_uid, m.markup_id, p.class
FROM    pais.markup_polygon m, validation.nucleipoint p
WHERE   p.pais_uid = m.pais_uid AND 
DB2GSE.ST_Contains(m.polygon, DB2GSE.ST_Point(x/2.0, y/2.0, 100))=1
selectivity 0.00000002 with UR
)
select t.pais_uid, t.class, 
C.AREA, C.PERIMETER, C.ECCENTRICITY, C.CIRCULARITY, C.MAJOR_AXIS, C.MINOR_AXIS, C.EXTENT_RATIO, C.MEAN_INTENSITY,
MAX_INTENSITY,MIN_INTENSITY, STD_INTENSITY, ENTROPY, ENERGY, SKEWNESS, KURTOSIS, MEAN_GRADIENT_MAGNITUDE, STD_GRADIENT_MAGNITUDE, ENTROPY_GRADIENT_MAGNITUDE,
ENERGY_GRADIENT_MAGNITUDE, SKEWNESS_GRADIENT_MAGNITUDE, KURTOSIS_GRADIENT_MAGNITUDE, SUM_CANNY_PIXEL, MEAN_CANNY_PIXEL, 
CYTO_H_MeanIntensity,CYTO_H_MeanMedianDifferenceIntensity,
CYTO_H_MaxIntensity,CYTO_H_MinIntensity,CYTO_H_StdIntensity,CYTO_H_Entropy,CYTO_H_Energy,CYTO_H_Skewness,CYTO_H_Kurtosis,
CYTO_H_MeanGradMag,CYTO_H_StdGradMag,CYTO_H_EntropyGradMag,CYTO_H_EnergyGradMag ,CYTO_H_SkewnessGradMag,
CYTO_H_KurtosisGradMag,CYTO_H_SumCanny,CYTO_H_MeanCanny,CYTO_E_MeanIntensity,CYTO_E_MeanMedianDifferenceIntensity,
CYTO_E_MaxIntensity,CYTO_E_MinIntensity,CYTO_E_StdIntensity,CYTO_E_Entropy,CYTO_E_Energy,CYTO_E_Skewness,CYTO_E_Kurtosis,
CYTO_E_MeanGradMag,CYTO_E_StdGradMag,CYTO_E_EntropyGradMag,CYTO_E_EnergyGradMag,CYTO_E_SkewnessGradMag,CYTO_E_KurtosisGradMag,
CYTO_E_SumCanny,CYTO_E_MeanCanny,CYTO_G_MeanIntensity,CYTO_G_MeanMedianDifferenceIntensity,CYTO_G_MaxIntensity,
CYTO_G_MinIntensity,CYTO_G_StdIntensity,CYTO_G_Entropy,CYTO_G_Energy,CYTO_G_Skewness,CYTO_G_Kurtosis,CYTO_G_MeanGradMag,
CYTO_G_StdGradMag,CYTO_G_EntropyGradMag,CYTO_G_EnergyGradMag,CYTO_G_SkewnessGradMag,CYTO_G_KurtosisGradMag,
CYTO_G_SumCanny,CYTO_G_MeanCanny
from t, pais.calculation_flat c
where t.pais_uid = c.pais_uid and t.markup_id = c.markup_id;
 


