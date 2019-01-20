package com.sap.amd.rmi;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import com.sap.amd.bcpandicp.Incident;
import com.sap.amd.bcpandicp.IncidentICP;
import com.sap.amd.bcpandicp.Processor;
import com.sap.amd.bcpandicp.Region;
import com.sap.amd.dispatcher.ConfigIDs;
import com.sap.amd.utils.State;
import com.sap.amd.utils.exceptions.SessionUnlockerException;
import com.sap.amd.utils.types.TDispatchState;
import com.sap.amd.utils.types.TImportState;
import com.sap.amd.utils.types.TState;

public interface Wrapper extends Remote
{
	State<TImportState> getSearchImportProgress() throws RemoteException;
	List<String> getSearchImportResult() throws RemoteException;
	
	void clearSearchImporter() throws RemoteException;
	
	State<TImportState> getIncidentImportProgress() throws RemoteException;
	void clearIncidentImporter() throws RemoteException;
	
	List<Processor> parseProcessors() throws RemoteException;
	List<Region> parseRegions() throws RemoteException;
	
	List<State<TDispatchState>> getDispatchProgress() throws RemoteException;
	void clearDispatcher() throws RemoteException;
	
	//void sendEmail(String sender, DispatchResult dispatchResult, boolean useHtml) throws RemoteException;
	void sendCrashEmail(Exception exception, String message) throws RemoteException;
	
	State<TState> saveSpreadsheet(List<Processor> processors) throws RemoteException;
	boolean isDefaultSpreadsheetAvailable() throws RemoteException;
	
	void writeToLog(String text) throws RemoteException;
	
	String getProcessor() throws RemoteException;
	String getSession() throws RemoteException;
	String getUser() throws RemoteException;
	
	void setSession(String processor, String session) throws RemoteException;
	
	State<TState> authenticate(String user, String password) throws RemoteException;
	boolean isLocked() throws RemoteException;
	void lock() throws RemoteException;
	void unlock() throws RemoteException;
	boolean isBusy() throws RemoteException;
	void reportTaskConclusion() throws RemoteException;
	void startSessionUnlocker(Wrapper provider) throws RemoteException, SessionUnlockerException;
	
	List<String> getAvailableSimulators(List<Incident> incidents, List<Processor> processors, Processor firstProcessor) throws RemoteException;
	void clearSimulatorLoader() throws RemoteException;
	void killClient() throws RemoteException;
	void sendBugEmail(Exception exception, String message)throws RemoteException;
	ArrayList<String> announcementsStringResult() throws RemoteException;
	boolean writingAnnouncement(String text, int type, boolean isGlobal)
			throws RemoteException;
		String post(String url, String json) throws IOException;
	ConfigIDs parseIdsBCP() throws RemoteException;
	ConfigIDs parseIdsICP() throws RemoteException;
	void importSearches(boolean isBCP) throws RemoteException;
	void saveSearchesToFile(List<String> searches, boolean isBCP) throws RemoteException;
	List<String> getSearchesFromFile(boolean isBCP) throws RemoteException;
	boolean insertInDatabase(boolean isBCP) throws RemoteException;
	String getNextJob(String url) throws IOException;
	void importIncidents(String search, boolean isBCP) throws RemoteException;
	void getIncidentImportResultBCPICP(boolean isBCP) throws RemoteException;
	String update(String url, String json) throws IOException;
	void loginServer();
}
