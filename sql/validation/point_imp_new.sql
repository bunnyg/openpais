IMPORT FROM "/develop/pais/sql/production/query/validation/point_imp_new.txt" OF DEL 
MODIFIED BY COLDEL, METHOD P (1, 2, 3, 4) MESSAGES 
"/develop/pais/sql/production/query/validation/point_imp_new.msg" INSERT INTO 
VALIDATION.NUCLEIPOINT (PAIS_UID, X, Y, CLASS);
