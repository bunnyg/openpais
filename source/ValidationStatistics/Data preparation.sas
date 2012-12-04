
*========================================================================================================
* This Program Prepares Datasets for Sampling
* -------------------------------------------------------------------------------------------------------
* Input files:
*            (1) One dataset containing all features for all slides. File name: "subregion_texture_metrics_all"
* Output files:
*            (1) "slide1"..."sliden": one dataset per slide
*            (2) "true_value": dataset containing slide-wide mean/SD/total # of subregions
*========================================================================================================;
options nodate nonumber;
libname cci "C:\"; * Path of local folder in which datasets are stored;

options nodate nonumber; 

*** Split data by slide so one dataset per slide ***;
%macro slides();
%do i=1 %to 18;
data cci.slide&i;
	set cci.subregion_texture_metrics_all;
	where slideno=&i;
run;
%end;
%mend;
%slides();

*** True value = population mean, population stddev ***;
proc means data=cci.subregion_texture_metrics_all;
	var  RATIO DISTANCE;
	class pais_uid;
	*ods output summary=true_value;
	output out=cci.true_value mean=slide_avg_ratio slide_avg_dis n=total_subregion std=slide_sd_ratio slide_sd_dis;
run;

data cci.true_value;
	set cci.true_value;
	slideno = _N_-1;
	if pais_uid=" " then delete;
	drop  _TYPE_    _FREQ_;
run;
