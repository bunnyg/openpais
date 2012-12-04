IMPORT FROM "/develop/pais/sql/production/query/validation/point_imp_val.txt" OF DEL 
MODIFIED BY COLDEL, METHOD P (1, 2, 3, 4) MESSAGES 
"/develop/pais/sql/production/query/validation/point_imp_val.msg" INSERT INTO 
VALIDATION.NUCLEIPOINT (PAIS_UID, X, Y, CLASS);
