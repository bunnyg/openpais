package edu.emory.cci.pais.dataloader.db2helper;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.BatchUpdateException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.zip.ZipInputStream;

/**
 * @author Fusheng Wang, Center for Comprehensive Informatics, Emory University
 * @version 1.0
 */


public class PAISDBHelper {
	private static String DRIVER_NAME = "com.ibm.db2.jcc.DB2Driver";
	private int CentroidCalCulationFlag = 0;




	private String markup_polygon_table = "PAIS.MARKUP_POLYGON";


	private String getZipPreparedSql ="select * from PAIS.STAGINGDOC where sequence_number = ? ";
	private long queryCounter = 0;
	private long batchCounter = 0;
	private static final int COMMIT_BATCH_SIZE = 3000; //500;
	private static final int RECORD_COUNT = 1000; //100000;
	private HashMap <String, PreparedStatement> pstmtMap = null;
	private String calculationFlatSQL = null;

	private Connection db2Connection = null;
	private Statement db2Statement = null;
	private PreparedStatement completeDocPstmt = null;


	/**
	 * Use PAISDBHelper(DBConfig dbConfig) instead.
	 */
	public PAISDBHelper(){
		initialize();
	}


	/**
	 * Use PAISDBHelper(DBConfig dbConfig) instead.
	 */
	public PAISDBHelper(boolean autoCommit){
		initialize();
		setAutoCommit(autoCommit);
	}


	/**
	 * Use PAISDBHelper(DBConfig dbConfig) instead.
	 */
	public PAISDBHelper(String host, String port, String username, String passwd, String database){
		setLogin(host, port, username, passwd, database);
		initialize();
		setAutoCommit(true);

	}

	public PAISDBHelper(DBConfig dbConfig) {
		Properties props = dbConfig.getProperties();
		setLogin(props.getProperty("host"), props.getProperty("port"), props.getProperty("username"), props.getProperty("passwd"), props.getProperty("database"));
		initialize();
		setAutoCommit(new Boolean(props.getProperty("autocommit")).booleanValue());
	}

	/* By Tunan Wu
	 * read db configurations from a text file
     * The file contains lines:
     * jdbc.host=xxxx
     * jdbc.database=xxxx
     * jdbc.port=xx
     * jdbc.username=
     * jdbc.password=
     * */
    public PAISDBHelper(String connectionFileName) {
            Properties properties = new Properties();
            try {
                    properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(connectionFileName));
            } catch (IOException e1) {
              System.out.println("Load stream Error");
              e1.printStackTrace();
            }
            String host = properties.getProperty("jdbc.host");
            String database = properties.getProperty("jdbc.database");
            String port = properties.getProperty("jdbc.port");
            String username = properties.getProperty("jdbc.username");
            String password = properties.getProperty("jdbc.password");
            setLogin(host, port, username, password, database);
            initialize();
            setAutoCommit(true);
    }

	private void initialize() {
		setDBConnection();
	}


	public void setAutoCommit(boolean auto){
		try {
			db2Connection.setAutoCommit(auto);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}


	public void setLogin(String host, String port, String username, String passwd, String database ){
		XMLDB_URI_KEY = "jdbc:db2://" + host + ":" + port;
		USERNAME = username;
		PASSWORD = passwd;
		db2Collection = database;
	}

	//Initialize database connection. This connection is reused.
	private void setDBConnection() {
		String jdbcUrl = XMLDB_URI_KEY + "/" + db2Collection;
		try {
			Class.forName(DRIVER_NAME).newInstance();
			Properties connectProperties = new Properties();
			connectProperties.put("user", USERNAME);
			connectProperties.put("password", PASSWORD);
			System.out.println("JDBC URL: " + jdbcUrl);
			db2Connection = DriverManager.getConnection(jdbcUrl, connectProperties);
			if (db2Connection != null) db2Statement = db2Connection.createStatement();
			System.out.println("DB successfully connected.");
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public Connection getDBConnection(){
		return db2Connection;
	}


	public Statement getStatement(){
		return db2Statement;

	}

	public void closeStatement(){
		if (db2Statement != null)
			try {
				db2Statement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
	}


	public void close(){
		try {
			if (db2Statement != null ) db2Statement.close();
			if (completeDocPstmt != null ) completeDocPstmt.close();
			if (pstmtMap != null){
				PreparedStatement pstmt = null;
				Set <String> keys = pstmtMap.keySet();
				Iterator<String> it = keys.iterator();
				while (it.hasNext()) {
					String key =  it.next();
					pstmt = pstmtMap.get(key);
					if (pstmt != null) pstmt.close();
				}
			}
			if (db2Connection.isClosed() == false ){
				db2Connection.commit();
				db2Connection.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void commit(){
		if(db2Connection != null)
			try {
				db2Connection.commit();
			} catch (SQLException e) {
				e.printStackTrace();
			}
	}


	public void setCentroidCalulationFlag(){
		this.CentroidCalCulationFlag = 1;
	}

	public int getCentroidCalulationFlag(){
		return this.CentroidCalCulationFlag;
	}

	public void initLoadingPreparedStatements(String loadingConfigFile){
		if (pstmtMap != null) return;
		QueryGenerator queryGenerator;
		try {
			queryGenerator = QueryGenerator.getQueryGenerator();
		} catch(IllegalStateException e) {
			queryGenerator = QueryGenerator.getQueryGenerator(loadingConfigFile);
		}

		if (CentroidCalCulationFlag == 1)
			queryGenerator.setCentFlag();

		String markupSQL = queryGenerator.generatePreparedMarkupSQL();
		String regionSQL = queryGenerator.generatePreparedRegionSQL();
		String ordinalObsSQL = queryGenerator.generatePareparedObservationSQL("Ordinal");
		String nominalObsSQL = queryGenerator.generatePareparedObservationSQL("Nominal");
		String annotationMarkupSQL = queryGenerator.generatePreparedAnnotationMarkupSQL();
		pstmtMap = new HashMap <String, PreparedStatement>();
		PreparedStatement markupPstmt = getPareparedStatement(markupSQL);
		PreparedStatement regionPstmt = getPareparedStatement(regionSQL);
		PreparedStatement ordinalObservationPstmt = getPareparedStatement(ordinalObsSQL);
		PreparedStatement nominalObservationPstmt = getPareparedStatement(nominalObsSQL);
		PreparedStatement annotationMarkupPstmt =   getPareparedStatement(annotationMarkupSQL);
		pstmtMap.put("markupPstmt", markupPstmt);
		pstmtMap.put("regionPstmt", regionPstmt);
		pstmtMap.put("ordinalObservationPstmt", ordinalObservationPstmt);
		pstmtMap.put("nominalObservationPstmt", nominalObservationPstmt);
		pstmtMap.put("annotationMarkupPstmt", annotationMarkupPstmt);
	}

	/* Annotation statement is dynamically generated for calculations as the number of features can vary.
	 * If the PreparedStatement String is already one of the pstmtMap key, no need to create a new PreparedStatement.
	 * Otherwise, current PreparedStatement for calculations is removed and recreated.  */
	public void setLoadingPreparedStatement(String pstmtName, String pstmtSQL){
		if( pstmtSQL.equals(calculationFlatSQL) ) return;
		PreparedStatement pstmt = null;
		PreparedStatement newPstmt = createPreparedStatement(pstmtSQL);
		if(pstmtMap.containsKey(pstmtName)){
			pstmtMap.remove(pstmtName);
			pstmt = pstmtMap.get(pstmtName);
			if(pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		pstmtMap.put(pstmtName, newPstmt);
		calculationFlatSQL = pstmtSQL;
	}


	public boolean finalizeLoadingStatements(){
		if (pstmtMap != null){
			PreparedStatement pstmt = null;
			Set <String> keys = pstmtMap.keySet();
			Iterator<String> it = keys.iterator();
			while (it.hasNext()) {
				String key =  it.next();
				pstmt = pstmtMap.get(key);
				if (pstmt != null)
					try {
						pstmt.executeBatch();
					} catch(BatchUpdateException bue) {
						System.err.println("\n*** BatchUpdateException while loading '"+key+"':\n");
						int [] affectedCount = bue.getUpdateCounts();
						for (int i = 0; i < affectedCount.length; i++) {
							System.err.print(affectedCount[i] + "  ");
						}
						System.err.println();
						System.err.println("Message:     " + bue.getMessage());
						System.err.println("SQLState:    " + bue.getSQLState());
						System.err.println("NativeError: " + bue.getErrorCode());
						System.err.println();
						SQLException sqe = bue.getNextException();
						while (sqe != null) {
							System.err.println("Message:     " + sqe.getMessage());
							System.err.println("SQLState:    " + sqe.getSQLState());
							System.err.println("NativeError: " + sqe.getErrorCode());
							System.err.println();
							sqe = sqe.getNextException();
						}
						// TODO: it seems pstmt != pstmtMap.get("markupPstmt") is always true.
						// See how to check for boundary in wrong format.
						/*
						if( pstmt != pstmtMap.get("markupPstmt") )//Markup boundary may be in wrong format, ignore it...
							throw new RuntimeException(new Exception("Markup boundary might be in wrong format."));
						*/
						throw new RuntimeException(bue);
					}
					catch (SQLException sqe){
						sqe.printStackTrace();
						throw new RuntimeException(sqe);
					}
			}
			try {
				getDBConnection().commit();
			} catch (SQLException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		return true;
	}

	public PreparedStatement getLoadingPreparedStatement(String pstmtName){
		return pstmtMap.get(pstmtName);
	}


	public boolean disableLogging(String tabName){
		//alter table activate not logged initially;
		String sql = "alter table " + tabName + " activate not logged initially";
		try {
			getStatement().executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean executeBatch(PreparedStatement pstmt){
		//disableLogging("PAIS.MARKUP_POLYGON");
		batchCounter ++;
		if (batchCounter > COMMIT_BATCH_SIZE){
			try {
				pstmt.executeBatch();
				if(db2Connection.getAutoCommit() == false)
					db2Connection.commit();
				//System.out.println(COMMIT_BATCH_SIZE + " inserts committed.");
				batchCounter = 0;
				return true;
			} catch(BatchUpdateException bue) {
				System.err.println("\n*** BatchUpdateException:\n");
				int [] affectedCount = bue.getUpdateCounts();
				for (int i = 0; i < affectedCount.length; i++) {
					System.err.print(affectedCount[i] + "  ");
				}
				System.err.println();
				System.err.println("Message:     " + bue.getMessage());
				System.err.println("SQLState:    " + bue.getSQLState());
				System.err.println("NativeError: " + bue.getErrorCode());
				System.err.println();
				SQLException sqe = bue.getNextException();
				while (sqe != null) {
					System.err.println("Message:     " + sqe.getMessage());
					System.err.println("SQLState:    " + sqe.getSQLState());
					System.err.println("NativeError: " + sqe.getErrorCode());
					System.err.println();
					sqe = sqe.getNextException();
				}
				if( pstmt != pstmtMap.get("markupPstmt") )//Markup boundary may be in wrong format, ignore it...
					return false;
			}
			catch (SQLException sqe){
				sqe.printStackTrace();
				return false;
			}
		}
		return true;
	}

	public boolean finalExecuteBatch(PreparedStatement pstmt){
		try {
			if(pstmt != null){
				pstmt.executeBatch();
				db2Connection.commit();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	//To be updated if BatchUpdateException happens.
	public boolean batchUpdate(String sqlStr, boolean singleSql){
		String sqls[] = null;
		String sql = null;
		try {
			if (singleSql == true){
				//stmt.addBatch(sqlStr);
				db2Statement.executeUpdate(sqlStr);
				queryCounter ++;
			}
			else{
				sqls = sqlStr.split(";");
				for(int i = 0; i < sqls.length; i++){
					sql = sqls[i];
					queryCounter ++;
					db2Statement.executeUpdate(sql);
					//stmt.addBatch(sql);
				}
			}
			//int[] result = stmt.executeBatch();
			if (queryCounter >= COMMIT_BATCH_SIZE){
				queryCounter = 0;
				//System.out.println("Counter: " + queryCounter);
				//int[] result = stmt.executeBatch();
				db2Connection.commit();
				//stmt.close();
				//deleteStatement();
				//db2Connection.setAutoCommit(false);
				//System.out.println(COMMIT_BATCH_SIZE + " inserts committed.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			//return false;
			System.out.println("Failed SQL: " + sql);
		}
		return true;
	}


	public boolean batchUpdate(String[] sqls, boolean immediateCommit){
		Statement stmt = getStatement();
		queryCounter ++;
		String sql = null;
		try {
			for(int i = 0; i < sqls.length; i++){
				sql = sqls[i];
				stmt.addBatch(sql);
			}
			stmt.executeBatch();
			if (immediateCommit == true)
				db2Connection.commit();
			else if ( (queryCounter % COMMIT_BATCH_SIZE) == 0)
				db2Connection.commit();
		} catch (Exception e) {
			e.printStackTrace();
			//return false;
			System.out.println("Failed SQL: " + sql);
		}
		return true;
	}

	public boolean batchUpdate1(String sqlStr){
		Connection con = db2Connection;
		try {
			con.setAutoCommit(false);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String sqls[] = sqlStr.split(";");
		Statement stmt = null;
		try {
			stmt = con.createStatement();
			for(String sql: sqls)
			stmt.addBatch(sql);
			stmt.executeBatch();
			//con.commit();
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean executeUpdate(String sql){
		Connection con = db2Connection;
		try {
			PreparedStatement pstmt = con.prepareStatement(sql);
			pstmt.executeUpdate();
			pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/** Return result set from a query
	 * @throws Exception */
	public ResultSet getSqlQueryResult(String sqlquery) throws Exception {
		Statement statement = null;
		ResultSet rs = null;
		try {
			statement = getDBConnection().createStatement();
			rs = statement.executeQuery(sqlquery);
			//statement.close();
		} catch (SQLException e){
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
					System.out.println("Closing statement error.");
				}
			e.printStackTrace();
			System.out.println("Getting query result set error.");
			return null;
		}
			return rs;
	}

	/** Return unzipped file based on a ZipInputStream from the BLOB column.  */
    public String  getXmlDocFile(ZipInputStream zipIn) {
            File tempFile;
            String prefix = UUID.randomUUID().toString();
			try {
				tempFile = File.createTempFile(prefix, ".xml");
            OutputStream outStream = new FileOutputStream(tempFile);
            byte[] buffer = new byte[2000000];
            int nrBytesRead;

            //Get next zip entry and start reading data
            if (zipIn.getNextEntry() != null) {
                while ((nrBytesRead = zipIn.read(buffer)) > 0) {
                    outStream.write(buffer, 0, nrBytesRead);
                }
            }
            //Finish off by closing the streams
            outStream.close();
            zipIn.close();
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
            return tempFile.getAbsolutePath();
    }

    public PreparedStatement getPareparedStatement(String sql){
    	PreparedStatement pstmt = null;
    	try {
			pstmt = getDBConnection().prepareStatement(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return pstmt;
    }

    public boolean deleteFile(String filename){
    	File file = new File(filename);
    	return file.delete();
    }


    public PreparedStatement getCompleteJobPstmt(String completeDocSQL){
    	if ( completeDocPstmt != null) return completeDocPstmt;
    	else{
    		completeDocPstmt = getPareparedStatement(completeDocSQL);
    		return completeDocPstmt;
    	}
    }


    //SELECT NEXTVAL FOR STAGINGDOC_SEQ FROM PAIS.STAGINGDOC
    //CREATE SEQUENCE STAGINGDOC_SEQ START WITH 1 INCREMENT BY 1 NO MAXVALUE NO CYCLE NO CACHE;
    public int generateNextSeqNum(String tableName, String seqName){
    	String nextSeqNumSql = "SELECT NEXTVAL FOR " + seqName + " FROM " + tableName;
    	try {
			ResultSet rs = getSqlQueryResult(nextSeqNumSql);
			while (rs.next() ){
				int nextSeqNum = rs.getInt(1);
				return nextSeqNum;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
		return -1;
    }


    public PreparedStatement createPreparedStatement(String psql){
    	try {
			return getDBConnection().prepareStatement(psql);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
    }



	public void testTempTableInsert(PAISDBHelper db, int recordCount, int BATCH_SIZE) {
		Statement stmt = db.getStatement();
		String psql = "INSERT INTO "+markup_polygon_table+"(NAME, MARKUP_UID, GEOMETRICSHAPE_ID, MARKUP_ID, ANNOTATION_ID, POLYGON, PROVENANCE_ID, TILENAME, PAIS_UID) VALUES(null, '10514095644100004', 10514095644300004, 10514095644100004, 10514095644200004, DB2GSE.ST_Polygon(PAIS.coords2wktpoly(?) , 100), null, 'oligoIII.2.ndpi-0000073728-0000024576', ?)";
		String queryStart = "INSERT INTO "+markup_polygon_table+"(NAME, MARKUP_UID, GEOMETRICSHAPE_ID, MARKUP_ID, ANNOTATION_ID, POLYGON, PROVENANCE_ID, TILENAME, PAIS_UID) SELECT * FROM TABLE(VALUES ";
		String queryEnd = ") AS t(NAME, MARKUP_UID, GEOMETRICSHAPE_ID, MARKUP_ID, ANNOTATION_ID, POLYGON, PROVENANCE_ID, TILENAME, PAIS_UID)";
		String points = "73729,24770 73730,24770 73731,24770 73732,24770 73733,24770 73733,24769 73734,24769 73735,24769 73735,24768 73736,24768 73737,24768 73737,24767 73738,24767 73739,24767 73740,24767 73740,24768 73741,24768 73742,24768 73742,24769 73743,24769 73743,24770 73743,24771 73742,24771 73742,24772 73741,24772 73741,24773 73740,24773 73739,24773 73739,24774 73738,24774 73738,24775 73737,24775 73737,24776 73736,24776 73736,24777 73736,24778 73736,24779 73735,24779 73735,24780 73734,24780 73734,24781 73733,24781 73733,24782 73733,24783 73732,24783 73732,24784 73732,24785 73731,24785 73731,24786 73730,24786 73730,24787 73729,24787 73729,24786 73729,24785 73729,24784 73729,24783 73729,24782 73729,24781 73729,24780 73729,24779 73729,24778 73729,24777 73729,24776 73729,24775 73729,24774 73729,24773 73729,24772 73729,24771 73729,24770";
		StringBuffer tempRecsBuf = new StringBuffer();
		for (int i=1; i < recordCount + 1; i++){
			String uid = "'" + i + "'";
			String rec = "(null, '10514095644100004', 10514095644300004, 10514095644100004, 10514095644200004, DB2GSE.ST_Polygon(PAIS.coords2wktpoly('" + points + "') , 100), null, 'oligoIII.2.ndpi-0000073728-0000024576'," + uid + ")";
			tempRecsBuf.append(rec);

			try {
				if ( (i % BATCH_SIZE) == 0){
					String query = queryStart + tempRecsBuf.toString() + queryEnd;
					//System.out.println("Query: " + query);
					stmt.executeUpdate(query);
					tempRecsBuf = new StringBuffer();
					db.getDBConnection().commit();
				}
				else tempRecsBuf.append(",");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		try {
			db.getDBConnection().commit();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		//db.close();
	}


	public void testNonPreparedInsert(PAISDBHelper db, int recordSize, int batchSize) {
		Statement stmt = db.getStatement();
		for (int i=1; i <  recordSize + 1; i++){
			String uid = "'" + i + "'";
			String query = "INSERT INTO "+markup_polygon_table+"(NAME, MARKUP_UID, GEOMETRICSHAPE_ID, MARKUP_ID, ANNOTATION_ID, POLYGON, PROVENANCE_ID, TILENAME, PAIS_UID) VALUES(null, '10514095644100004', 10514095644300004, 10514095644100004, 10514095644200004, DB2GSE.ST_Polygon(PAIS.coords2wktpoly('73729,24770 73730,24770 73731,24770 73732,24770 73733,24770 73733,24769 73734,24769 73735,24769 73735,24768 73736,24768 73737,24768 73737,24767 73738,24767 73739,24767 73740,24767 73740,24768 73741,24768 73742,24768 73742,24769 73743,24769 73743,24770 73743,24771 73742,24771 73742,24772 73741,24772 73741,24773 73740,24773 73739,24773 73739,24774 73738,24774 73738,24775 73737,24775 73737,24776 73736,24776 73736,24777 73736,24778 73736,24779 73735,24779 73735,24780 73734,24780 73734,24781 73733,24781 73733,24782 73733,24783 73732,24783 73732,24784 73732,24785 73731,24785 73731,24786 73730,24786 73730,24787 73729,24787 73729,24786 73729,24785 73729,24784 73729,24783 73729,24782 73729,24781 73729,24780 73729,24779 73729,24778 73729,24777 73729,24776 73729,24775 73729,24774 73729,24773 73729,24772 73729,24771 73729,24770') , 100), null, 'oligoIII.2.ndpi-0000073728-0000024576'," + uid + ")";
			try {
				stmt.addBatch(query);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			try {
				if ( (i % batchSize) == 0){
					stmt.executeBatch();
					db.getDBConnection().commit();
					//System.out.println("Current idx:" + i);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		try {
			db.getDBConnection().commit();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	public void testPreparedStmtWithBatch(PAISDBHelper db, int batchSize) {
		String psql = "INSERT INTO "+markup_polygon_table+"(NAME, MARKUP_UID, GEOMETRICSHAPE_ID, MARKUP_ID, ANNOTATION_ID, POLYGON, PROVENANCE_ID, TILENAME, PAIS_UID) VALUES(null, '10514095644100004', 10514095644300004, 10514095644100004, 10514095644200004, DB2GSE.ST_Polygon(PAIS.coords2wktpoly(?) , 100), null, 'oligoIII.2.ndpi-0000073728-0000024576', ?)";
		PreparedStatement pstmt = db.getPareparedStatement(psql);
		//Statement pstmt = db.getPareparedStatement(query);
		for (int i=1; i < 100001; i++){
			String points = "73729,24770 73730,24770 73731,24770 73732,24770 73733,24770 73733,24769 73734,24769 73735,24769 73735,24768 73736,24768 73737,24768 73737,24767 73738,24767 73739,24767 73740,24767 73740,24768 73741,24768 73742,24768 73742,24769 73743,24769 73743,24770 73743,24771 73742,24771 73742,24772 73741,24772 73741,24773 73740,24773 73739,24773 73739,24774 73738,24774 73738,24775 73737,24775 73737,24776 73736,24776 73736,24777 73736,24778 73736,24779 73735,24779 73735,24780 73734,24780 73734,24781 73733,24781 73733,24782 73733,24783 73732,24783 73732,24784 73732,24785 73731,24785 73731,24786 73730,24786 73730,24787 73729,24787 73729,24786 73729,24785 73729,24784 73729,24783 73729,24782 73729,24781 73729,24780 73729,24779 73729,24778 73729,24777 73729,24776 73729,24775 73729,24774 73729,24773 73729,24772 73729,24771 73729,24770";
			try {
				pstmt.setString(1, points);
				pstmt.setString(2, i + "");
				pstmt.addBatch();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			try {
				//String query = "INSERT INTO "+markup_polygon_table+"(NAME, MARKUP_UID, GEOMETRICSHAPE_ID, MARKUP_ID, ANNOTATION_ID, POLYGON, PROVENANCE_ID, TILENAME, PAIS_UID) VALUES(null, '10514095644100004', 10514095644300004, 10514095644100004, 10514095644200004, DB2GSE.ST_Polygon(PAIS.coords2wktpoly('73729,24770 73730,24770 73731,24770 73732,24770 73733,24770 73733,24769 73734,24769 73735,24769 73735,24768 73736,24768 73737,24768 73737,24767 73738,24767 73739,24767 73740,24767 73740,24768 73741,24768 73742,24768 73742,24769 73743,24769 73743,24770 73743,24771 73742,24771 73742,24772 73741,24772 73741,24773 73740,24773 73739,24773 73739,24774 73738,24774 73738,24775 73737,24775 73737,24776 73736,24776 73736,24777 73736,24778 73736,24779 73735,24779 73735,24780 73734,24780 73734,24781 73733,24781 73733,24782 73733,24783 73732,24783 73732,24784 73732,24785 73731,24785 73731,24786 73730,24786 73730,24787 73729,24787 73729,24786 73729,24785 73729,24784 73729,24783 73729,24782 73729,24781 73729,24780 73729,24779 73729,24778 73729,24777 73729,24776 73729,24775 73729,24774 73729,24773 73729,24772 73729,24771 73729,24770') , 100), null, 'oligoIII.2.ndpi-0000073728-0000024576'," + uid + ")";
				//String query = "INSERT INTO "+markup_polygon_table+"(NAME, MARKUP_UID, GEOMETRICSHAPE_ID, MARKUP_ID, ANNOTATION_ID, POINTS, PROVENANCE_ID, TILENAME, PAIS_UID) VALUES(null, '10514095644100004', 10514095644300004, 10514095644100004, 10514095644200004, '73729,24770 73730,24770 73731,24770 73732,24770 73733,24770 73733,24769 73734,24769 73735,24769 73735,24768 73736,24768 73737,24768 73737,24767 73738,24767 73739,24767 73740,24767 73740,24768 73741,24768 73742,24768 73742,24769 73743,24769 73743,24770 73743,24771 73742,24771 73742,24772 73741,24772 73741,24773 73740,24773 73739,24773 73739,24774 73738,24774 73738,24775 73737,24775 73737,24776 73736,24776 73736,24777 73736,24778 73736,24779 73735,24779 73735,24780 73734,24780 73734,24781 73733,24781 73733,24782 73733,24783 73732,24783 73732,24784 73732,24785 73731,24785 73731,24786 73730,24786 73730,24787 73729,24787 73729,24786 73729,24785 73729,24784 73729,24783 73729,24782 73729,24781 73729,24780 73729,24779 73729,24778 73729,24777 73729,24776 73729,24775 73729,24774 73729,24773 73729,24772 73729,24771 73729,24770', null, 'oligoIII.2.ndpi-0000073728-0000024576'," + uid + ")";
				//stmt.executeUpdate(query);
				//stmt.addBatch(query);
				if ( (i % batchSize) == 0){
					pstmt.executeBatch();
					//db.getDBConnection().commit();
					//System.out.println("Current idx:" + i);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			pstmt.executeBatch();
			//db.getDBConnection().commit();
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		//db.close();
	}


	public void testPreparedStmtNoBatch(PAISDBHelper db, int batchSize) {
		String psql = "INSERT INTO "+markup_polygon_table+"(NAME, MARKUP_UID, GEOMETRICSHAPE_ID, MARKUP_ID, ANNOTATION_ID, POLYGON, PROVENANCE_ID, TILENAME, PAIS_UID) VALUES(null, '10514095644100004', 10514095644300004, 10514095644100004, 10514095644200004, DB2GSE.ST_Polygon(PAIS.coords2wktpoly(?) , 100), null, 'oligoIII.2.ndpi-0000073728-0000024576', ?)";
		PreparedStatement pstmt = db.getPareparedStatement(psql);
		for (int i=1; i < RECORD_COUNT + 1; i++){
			String points = "73729,24770 73730,24770 73731,24770 73732,24770 73733,24770 73733,24769 73734,24769 73735,24769 73735,24768 73736,24768 73737,24768 73737,24767 73738,24767 73739,24767 73740,24767 73740,24768 73741,24768 73742,24768 73742,24769 73743,24769 73743,24770 73743,24771 73742,24771 73742,24772 73741,24772 73741,24773 73740,24773 73739,24773 73739,24774 73738,24774 73738,24775 73737,24775 73737,24776 73736,24776 73736,24777 73736,24778 73736,24779 73735,24779 73735,24780 73734,24780 73734,24781 73733,24781 73733,24782 73733,24783 73732,24783 73732,24784 73732,24785 73731,24785 73731,24786 73730,24786 73730,24787 73729,24787 73729,24786 73729,24785 73729,24784 73729,24783 73729,24782 73729,24781 73729,24780 73729,24779 73729,24778 73729,24777 73729,24776 73729,24775 73729,24774 73729,24773 73729,24772 73729,24771 73729,24770";
			try {
				pstmt.setString(1, points);
				pstmt.setString(2, i + "");
				pstmt.executeUpdate();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}

/*			try {
				if ( (i % batchSize) == 0){
					db.getDBConnection().commit();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}*/
		}
		try {
			db.getDBConnection().commit();
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/* get the first BLOB and save it as a zip file. Test only. */
	public boolean getZip(String filePath, int seqNo){
		PreparedStatement pstmt = null;
		Connection con = null;
		try {
			con = db2Connection;
			pstmt = con.prepareStatement(getZipPreparedSql);
			pstmt.setInt(1, seqNo);
			ResultSet rs = pstmt.executeQuery();
			rs.next();

			Blob blob = rs.getBlob(3);
			byte[] array = blob.getBytes(1, (int) blob.length());
			FileOutputStream out = new FileOutputStream(new File(filePath));
			out.write(array);

			pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
			try {
				if (pstmt != null) pstmt.close();
			} catch (SQLException e1) {
			}
			return false;
		}
		return true;
	}

	public static void main(String[] args) {
		long starttime = System.currentTimeMillis() ;
		PAISDBHelper db = new PAISDBHelper(false);
		//db.testTempTableInsert(db, 200, 200);
		//db.testNonPreparedInsert(db, 200, 200);
		//db. testPreparedStmtWithBatch(db, 50);
		//db.testPreparedStmtNoBatch(db, 2000);

		String filePath7838 = "/tmp/7830.zip";
		String filePath11254 = "/tmp/11254.zip";

		db.getZip(filePath7838, 7838);
		db.getZip(filePath11254, 11254);

		long endtime = System.currentTimeMillis();
		System.out.println(" DB init time = " + (endtime - starttime)/1000.0 + " seconds." );
	}
}
