-- select 'call validation.gensubregionmarkup(''' || pais_uid  || ''');'  FROM (
-- select distinct pais_uid from validation.subregion_density );

connect to paisext;

call validation.gensubregionmarkup('astroII.1_40x_20x_NS-MORPH_1');                                    
call validation.gensubregionmarkup('astroII.2_40x_20x_NS-MORPH_1');                                    
call validation.gensubregionmarkup('gbm0.1_40x_20x_NS-MORPH_1');                                 
call validation.gensubregionmarkup('gbm0.2_40x_20x_NS-MORPH_1');   
call validation.gensubregionmarkup('gbm1.1_40x_20x_NS-MORPH_1');                                       
call validation.gensubregionmarkup('gbm1.2_40x_20x_NS-MORPH_1');   
call validation.gensubregionmarkup('gbm2.1_40x_20x_NS-MORPH_1');   
call validation.gensubregionmarkup('gbm2.2_40x_20x_NS-MORPH_1');                                       
call validation.gensubregionmarkup('normal.2_40x_20x_NS-MORPH_1');                                     
call validation.gensubregionmarkup('normal.3_40x_20x_NS-MORPH_1');                                     
call validation.gensubregionmarkup('oligoastroII.1_40x_20x_NS-MORPH_1');                               
call validation.gensubregionmarkup('oligoastroII.2_40x_20x_NS-MORPH_1');                               
call validation.gensubregionmarkup('oligoastroIII.1_40x_20x_NS-MORPH_1');                              
call validation.gensubregionmarkup('oligoastroIII.2_40x_20x_NS-MORPH_1');                              
call validation.gensubregionmarkup('oligoII.1_40x_20x_NS-MORPH_1');                                    
call validation.gensubregionmarkup('oligoII.2_40x_20x_NS-MORPH_1');                                    
call validation.gensubregionmarkup('oligoIII.1_40x_20x_NS-MORPH_1');                                   
call validation.gensubregionmarkup('oligoIII.2_40x_20x_NS-MORPH_1');     
