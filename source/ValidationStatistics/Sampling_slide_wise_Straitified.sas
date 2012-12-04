*========================================================================================================
* This Program Shows How to Conduct Stratified Sampling	by Slide & Calculate Sample Mean/SD
* -------------------------------------------------------------------------------------------------------
* Input files:
*            (1) One dataset containing all features per slide. File name follows format: "Slide+number"
*            (2) Dataset "true_value" which contains the total number of subregions
*========================================================================================================;
options nodate nonumber;
libname cci "C:\"; * Path of local folder in which datasets are stored;

/*----------------------------------------
Stratified Random Sampling
----------------------------------------*/
ods select none;
ods listing close;

proc printto new log="C:\log.log";
run;

proc datasets lib=cci;
	delete Stratified_Sample_Slide_All;
run;

%macro Stra_slide(slide=1, startss=5, endss=10, stepss=1, nsim=1000);

proc datasets lib=cci;
	delete Stratified_Sample_Slide_&slide;
run;

data para;
	set cci.true_value;
	call symput('total', trim(left(put(total_subregion, 4.))));
	where slideno = &slide;
run; 

data slide&slide;
	set cci.slide&slide;
run;


PROC FREQ DATA = Slide&slide;
	TABLES stratumlabel/OUT=NEWFREQ NOPRINT;
RUN;

%do n=&startss %to &endss %by &stepss;
DATA NEWFREQ2;
	SET NEWFREQ;
	SAMPNUM=(PERCENT * &n)/100;
	_NSIZE_= ROUND(SAMPNUM,1);
	SAMPNUM=ROUND(SAMPNUM,.01);
	IF _NSIZE_=0 THEN DELETE;
	OUTPUT NEWFREQ2;
RUN;

DATA NEWFREQ3;
	SET NEWFREQ2;
	KEEP stratumlabel _NSIZE_;
RUN;

PROC SORT DATA = NEWFREQ3;
	BY stratumlabel;
RUN;

PROC SORT DATA = slide&slide;
	BY stratumlabel;
RUN;

/*
proc print data=NEWFREQ2; run;
proc print data=NEWFREQ3; run;
*/


PROC SURVEYSELECT data=slide&slide method=SRS out=sample SAMPSIZE=NEWFREQ3 seed=123456 REPS=&nsim;*SAMPSIZE=&n SAMPRATE=.5;
	strata stratumlabel;	
run;


proc sort data=sample;
	by replicate stratumlabel;	
run;

data cci.all_samp_slide_&slide._Stra_n_&n;
	set sample;
run;
/*
PROC PRINT DATA=sample;
run;

proc contents data=sample; run;
*/

proc sql;
	create table strat_total as
	select b.*, a.count as _TOTAL_
	from NEWFREQ2 as a join sample as b on a.stratumlabel=b.stratumlabel;
quit;

PROC PRINT DATA=strat_total;
run;

proc surveymeans data=sample total=strat_total;
	/*
	ratio;
	*/
	class stratumlabel;
	var ratio distance;
	strata stratumlabel;
	weight SamplingWeight;
	by replicate;
	ods output statistics=stat;
run;

/*
proc print data=stat;
run;
*/

data cci.all_samp_mean_slide_&slide._Stra_n_&n;
	set stat;
run;

* Calculate mean/SD from samples;
proc sql;
	create table summary_sampling as
	select  &slide as slideno, * 
	from (	select 	mean(Mean) as sim_mean_ratio, std(Mean) as sim_sd_ratio, stderr(Mean) as sim_se_ratio
			from stat
			where varname="ratio") as a,
		 (	select mean(Mean) as sim_mean_dis, std(Mean) as sim_sd_dis, stderr(Mean) as sim_se_dis
			from stat
			where varname="distance") as b 
	;
quit;

* Calculate BIAS and SD;
proc sql;
	create table bias as
	select 	trim(left(b.pais_uid)) as Slide_Name, b.total_SUBREGION as Subregion_Size, &n as Sample_Size, 
			round(a.sim_mean_ratio, .001) as Mean_Ratio, 
			round((a.sim_mean_ratio-b.slide_avg_ratio), .001) as Bias_Mean_Ratio,  
			round(a.sim_sd_ratio, .001) as SE_Mean_Ratio,
			round((a.sim_mean_ratio - 1.96*a.sim_sd_ratio), .001) as CI_Lower_Mean_Ratio,
		  	round((a.sim_mean_ratio + 1.96*a.sim_sd_ratio), .001) as CI_Upper_Mean_Ratio,
			round(a.sim_mean_dis, .001) as Mean_Distance, 
			round((a.sim_mean_dis-b.slide_avg_dis), .001) as Bias_Mean_Distance, 
			round(a.sim_sd_dis, .001) as SE_Mean_Distance,
			round((a.sim_mean_dis - 1.96*a.sim_sd_dis), .001) as CI_Lower_Mean_Distance,
		  	round((a.sim_mean_dis + 1.96*a.sim_sd_dis), .001) as CI_Upper_Mean_Distance
	from summary_sampling as a join cci.true_value2 as b on a.slideno=b.slideno
	;
quit;

proc append base=cci.Stratified_Sample_Slide_&slide data=bias; quit;
%end;
%mend;

%Stra_slide(slide=1, startss=40, endss=340, stepss=10, nsim=1000);
proc append base=cci.Stratified_Sample_Slide_All data=cci.Stratified_Sample_Slide_1; quit;

%Stra_slide(slide=2, startss=40, endss=500, stepss=10, nsim=1000);
proc append base=cci.Stratified_Sample_Slide_All data=cci.Stratified_Sample_Slide_2; quit;

%Stra_slide(slide=3, startss=40, endss=500, stepss=10, nsim=1000);
proc append base=cci.Stratified_Sample_Slide_All data=cci.Stratified_Sample_Slide_3; quit;

%Stra_slide(slide=4, startss=40, endss=500, stepss=10, nsim=1000);
proc append base=cci.Stratified_Sample_Slide_All data=cci.Stratified_Sample_Slide_4; quit;

%Stra_slide(slide=5, startss=40, endss=500, stepss=10, nsim=1000);
proc append base=cci.Stratified_Sample_Slide_All data=cci.Stratified_Sample_Slide_5; quit;

%Stra_slide(slide=6, startss=40, endss=500, stepss=10, nsim=1000);
proc append base=cci.Stratified_Sample_Slide_All data=cci.Stratified_Sample_Slide_6; quit;

%Stra_slide(slide=7, startss=40, endss=500, stepss=10, nsim=1000);
proc append base=cci.Stratified_Sample_Slide_All data=cci.Stratified_Sample_Slide_7; quit;

%Stra_slide(slide=8, startss=40, endss=500, stepss=10, nsim=1000);
proc append base=cci.Stratified_Sample_Slide_All data=cci.Stratified_Sample_Slide_8; quit;

%Stra_slide(slide=9, startss=40, endss=500, stepss=10, nsim=1000);
proc append base=cci.Stratified_Sample_Slide_All data=cci.Stratified_Sample_Slide_9; quit;

%Stra_slide(slide=10, startss=40, endss=500, stepss=10, nsim=1000);
proc append base=cci.Stratified_Sample_Slide_All data=cci.Stratified_Sample_Slide_10; quit;

%Stra_slide(slide=11, startss=40, endss=500, stepss=10, nsim=1000);
proc append base=cci.Stratified_Sample_Slide_All data=cci.Stratified_Sample_Slide_11; quit;

%Stra_slide(slide=12, startss=40, endss=500, stepss=10, nsim=1000);
proc append base=cci.Stratified_Sample_Slide_All data=cci.Stratified_Sample_Slide_12; quit;

%Stra_slide(slide=13, startss=40, endss=500, stepss=10, nsim=1000);
proc append base=cci.Stratified_Sample_Slide_All data=cci.Stratified_Sample_Slide_13; quit;

%Stra_slide(slide=14, startss=40, endss=500, stepss=10, nsim=1000);
proc append base=cci.Stratified_Sample_Slide_All data=cci.Stratified_Sample_Slide_14; quit;

%Stra_slide(slide=15, startss=40, endss=500, stepss=10, nsim=1000);
proc append base=cci.Stratified_Sample_Slide_All data=cci.Stratified_Sample_Slide_15; quit;

%Stra_slide(slide=16, startss=40, endss=500, stepss=10, nsim=1000);
proc append base=cci.Stratified_Sample_Slide_All data=cci.Stratified_Sample_Slide_16; quit;

%Stra_slide(slide=17, startss=40, endss=500, stepss=10, nsim=1000);
proc append base=cci.Stratified_Sample_Slide_All data=cci.Stratified_Sample_Slide_17; quit;

%Stra_slide(slide=18, startss=40, endss=500, stepss=10, nsim=1000);
proc append base=cci.Stratified_Sample_Slide_All data=cci.Stratified_Sample_Slide_18; quit;

ods select all;
ods listing;
ods rtf file="C:\Stratified_Sample_Slide_All.doc";
title;
proc print data=cci.Stratified_Sample_Slide_All; run;
ods rtf close;
proc printto; run;
