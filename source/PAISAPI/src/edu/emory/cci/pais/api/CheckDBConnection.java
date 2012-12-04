package edu.emory.cci.pais.api;

import java.io.File;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import edu.emory.cci.pais.api.WebAPI;
import edu.emory.cci.pais.dataloader.db2helper.PAISDBHelper;


public class CheckDBConnection implements ServletContextListener {
	
	Timer paisapiTimer;

	class DBTask extends TimerTask{
		@Override
		public void run() {
			try {
				if ( WebAPI.db.getDBConnection().isClosed() )
					WebAPI.db =  new PAISDBHelper("connection.properties");
			} catch (SQLException e) {
				System.out.println("Counldn't establish database connection at this time.");
				e.printStackTrace();
			}
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		paisapiTimer.cancel();
		WebAPI.db.close();
		
		File baseDir = new File(System.getProperty("java.io.tmpdir"));
		for (File tmpDir: baseDir.listFiles()) {
			if (tmpDir.isDirectory() && tmpDir.getPath().contains("paisAPI-")) {
				for(File childDir: tmpDir.listFiles()) {
					if (childDir.isDirectory()) {
						for(File child: childDir.listFiles())
							child.delete();
						childDir.delete();
					}
				}
				tmpDir.delete();
			}
		}
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		 paisapiTimer = new Timer();
		 paisapiTimer.schedule(new DBTask(),System.currentTimeMillis()+1000, 1000*60*60*24);
	}
}
