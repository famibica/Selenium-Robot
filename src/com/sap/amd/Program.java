package com.sap.amd;

import java.io.File;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONObject;

import com.sap.amd.rmi.Provider;
import com.sap.amd.utils.Log;

public class Program implements Serializable {
	private static final long serialVersionUID = -5242518199973489441L;
	private static Provider provider = new Provider();

	public static void main(String[] args) throws RemoteException {
		boolean isBCP = false;

		while (true) {			
			String jobDateTime = "";
			String systemName = "";
			String searchName = "";
			String region = "";
			String personalId = "";
			String lapseInMinutes = "";
			String status = "";
			String jsonData = "";
			
			try {
				try {

					jsonData = provider.getNextJob("https://p2monitoringspringi853300trial.hanatrial.ondemand.com/P2MonitoringSpring/rest/Job/NextJob/");
					JSONObject Jobject = new JSONObject(jsonData);
					jobDateTime = "" + Jobject.get("jobDateTime");
					systemName = "" + Jobject.get("system");
					searchName = "" + Jobject.get("search");
					region = "" + Jobject.get("region");
					personalId = "" + Jobject.get("personalId");
					lapseInMinutes = "" + Jobject.get("lapseInMinutes");
					status = "" + Jobject.get("status");

				} catch (Exception e) {
					Log.write("Was not possible to find a next Job");
				}	
				

				boolean canContinue = false;

				if (!systemName.equals("")) {
					// Setting System
					if (systemName.equals("BCP")) {
						if (!searchName.equals("")) {
							isBCP = true;
							canContinue = true;
							provider.parseIdsBCP();
						}
					} else {
						isBCP = false;
						canContinue = true;
						provider.parseIdsICP();
					}

					if (canContinue) {
						try {
							provider.importIncidents(searchName, isBCP);
							
							try {
								provider.getIncidentImportResultBCPICP(isBCP);
								String json = jsonData;
								provider.update("https://p2monitoringspringi853300trial.hanatrial.ondemand.com/P2MonitoringSpring/rest/Job/UpdateJob/", json);
							} catch (Exception e) {
								Log.write("Problem found when fetching Import results or sending Json");
							}
							
						} catch (Exception e) {
							Log.write("Problem found when importing Incidents");
						}
					}
				}
			} catch (Exception e) {
				Log.write(e);
			}

			try {
				Thread.sleep(10000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
}