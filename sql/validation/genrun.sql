select 'call validation.gensubregionmarkup(''' || pais_uid  || ''');' || 
'select count(*) from validation.subregion_markup where pais_uid = ''' || pais_uid || ''';'
FROM (
select distinct pais_uid from validation.subregion_density );

