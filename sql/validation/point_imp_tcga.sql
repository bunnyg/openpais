IMPORT FROM "/develop/pais/sql/production/query/validation/point_imp_tcga.txt" OF DEL 
MODIFIED BY COLDEL, METHOD P (1, 2, 3, 4) MESSAGES 
"/tmp/point_imp_tcga.msg" INSERT INTO 
VALIDATION.NUCLEIPOINT (PAIS_UID, X, Y, CLASS);
