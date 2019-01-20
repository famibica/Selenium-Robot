package com.sap.amd.dispatcher;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.swing.JOptionPane;

import com.sap.amd.bcpandicp.Incident;
import com.sap.amd.utils.Log;

public class IncidentsDataBase {
	
	private String user;
	private File file;
	private String[] splitedIncidents;
	private ArrayList <IncidentsAux> incidentsOnDataBase = new ArrayList<IncidentsAux>();
	private List<Incident> incidentsADispatched = new ArrayList<Incident>();
	
	public IncidentsDataBase(String user) {
		super();
		this.file = new File(".\\resources\\" + user + "\\IncidentsDB.txt"); 
	}
	
	class IncidentsAux {
		private String incidentNumber;
		private int incidentYear;
		private String incidentDataRecord;
		
		public IncidentsAux(String incidentNumber, int incidentYear,
				String incidentDataRecord) {
			super();
			this.incidentNumber = incidentNumber;
			this.incidentYear = incidentYear;
			this.incidentDataRecord = incidentDataRecord;
		}
		
		public IncidentsAux(String incidentNumber, String incidentDataRecord) {
			super();
			this.incidentNumber = incidentNumber;
			this.incidentDataRecord = incidentDataRecord;
		}
		
		public String getIncidentNumber() {
			return incidentNumber;
		}
		public void setIncidentNumber(String incidentNumber) {
			this.incidentNumber = incidentNumber;
		}
		public int getIncidentYear() {
			return incidentYear;
		}
		public void setIncidentYear(int incidentYear) {
			this.incidentYear = incidentYear;
		}
		public String getIncidentDataRecord() {
			return incidentDataRecord;
		}
		public void setIncidentDataRecord(String incidentDataRecord) {
			this.incidentDataRecord = incidentDataRecord;
		}
	}

	//Verify if the file exists
	public boolean fileExist(){
		Log.write("Acessing the Incidents Data Base File...");
		Log.write(new java.io.File("").getAbsolutePath());
		if (file.exists()) {
				Log.write("The file already exist... Returning true");
				return true;
	    }    
		else {
				Log.write("The file not found, process to create the file to "+ user +" started...");
		    	FileWriter arquivo;  
				     try {  
				        arquivo = new FileWriter(file);   
				        arquivo.close();  
				        Log.write("File Created on " + file.getName());
						JOptionPane.showMessageDialog(null, "Incidents Data Base Not Found, Creating new File");
				     } catch (IOException e) {  
				            e.printStackTrace();  
				            Log.write("A following error ocurred on process to creation the file " + e);
				     }
		}
		return false;	
	}
	
	//Deleting the old incidents on DataBase.txt
	public void CheckingAndDeletingOldIncidents(){
		Log.write("Deleting the old incidents on Data Base process started ...");
		String dateNow = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
		ArrayList <IncidentsAux> result = new ArrayList<IncidentsAux>();
		readingIncidents();
			for(int i = 0; i < incidentsOnDataBase.size(); i++){
				String dateIncident =  incidentsOnDataBase.get(i).getIncidentDataRecord();
				int mathResult =Integer.parseInt(dateNow) - Integer.parseInt(dateIncident) / 1000000 ;
					if(mathResult >= 30000){
					}
					else {
						result.add(incidentsOnDataBase.get(i));
					}
			}
		writingOldIncidents(result);	
		Log.write("Process completed.");
	}
	   
	//Rewriting incidents on DataBase.txt after the deleting and checking the old Incidents
	public void writingOldIncidents (ArrayList <IncidentsAux> result){ 
		Log.write("Rewriting the incidents on dataBase process started...");
			for (int i = 0; i < result.size(); i++){
				DataBaseCom db = new DataBaseCom(result.get(i).getIncidentNumber(), result.get(i).getIncidentYear(), result.get(i).getIncidentDataRecord());
					try {   
						if(i==0){
					        BufferedWriter out = new BufferedWriter(new FileWriter(file.getPath()));  
					        out.write(db.transformToString());  
					        out.close();  
						}
						else {
						    BufferedWriter out = new BufferedWriter(new FileWriter(file.getPath(), true));  
					        out.write(db.transformToString());  
					        out.close();  
						}
						
				    } catch (IOException ex) {  
				        ex.printStackTrace();  
				        Log.write("A following error ocurred on process to write the incident " + ex);
				    }  
			}
	}
	
	//Writing the new incidents on DataBase.txt
	public void writing (String incidentNumber, int year, String dataRecord){ 
		DataBaseCom db = new DataBaseCom(incidentNumber, year, dataRecord);
		Log.write("Wrtiting on incidents Data Base ...");
			try {   
		        BufferedWriter out = new BufferedWriter(new FileWriter(file.getPath(), true));  
		        out.write(db.transformToString());  
		        out.close();  
		        Log.write("Incident " + incidentNumber + " was writed.");
		          
		    } catch (IOException ex) {  
		        ex.printStackTrace();  
		        Log.write("A following error ocurred on process to write the incident " + ex);
		    }  
	}
	
	//Updating the DataBase.txt with new incidents
	public void updateListDB(List<Incident> incidents){ 
		Log.write("Update the database.txt process started....");
		if (incidents != null ){
			int Listsize = incidents.size();
				for(int i = 0; i < Listsize; i++){
					String incidentNumber = incidents.get(i).getNumber();
					int year = incidents.get(i).getYear();
					String dataRecord = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
					writing(incidentNumber, year, dataRecord);
				} 
			}
		Log.write("database.txt updated....");
		}
	
	//SearchNumber
	public boolean SearchIncident(String numberAndYear) {
		Log.write("Searching Incident number "+ numberAndYear + " process started...");
		for (int i = 0; i < incidentsOnDataBase.size(); i ++ ){
			if (incidentsOnDataBase.get(i).getIncidentNumber().equalsIgnoreCase(numberAndYear)){
				Log.write( "Comparing " + incidentsOnDataBase.get(i).getIncidentNumber() + " WITH " + numberAndYear);
				Log.write("Incident Number "+numberAndYear+ " found, returning true..." );
				return true;
			}
			else {
				Log.write( "Comparing " + incidentsOnDataBase.get(i).getIncidentNumber() + " WITH " + numberAndYear);
			}
		}
		Log.write("Incident Number "+numberAndYear+ " not found, returning false..." );
		return false;
	}
	
	public String transformIncidentNumber(String number, int year){
		String result = number + "." + year;
		return result;
	}
	
	//Creating a List for QueueScreening
	public List<Incident> ListForQueueScreening(List<Incident> incidents) { 
		Log.write("Creating the list for Queue Screening process started....");
			if(fileExist()==true){
				if(file.length()==0){
					return incidents;
				}
				readingIncidents();
					if (getSplitedIncidents() != null) {
						Log.write("Incidents List Size : " + incidents.size());
							for(int i = 0; i < incidents.size(); i++){
								if (SearchIncident(transformIncidentNumber(incidents.get(i).getNumber(), incidents.get(i).getYear())) == true){
									Log.write("Removing this incident :" + incidents.get(i).getNumber() + " at the list" );
								}
								else {
									Log.write("Add the incident " + incidents.get(i).getNumber() + " to the list" );
									incidentsADispatched.add(incidents.get(i));
								}
							}
						}
					else {
						return incidents;
					}
				Log.write("Finished the Incidents List");
				return incidentsADispatched;
			}
			else {
				return incidents;
			}
	}
	
	//GETTING INCIDENTS FROM DATA BASE
	public String [] readingIncidents(){
		Log.write("Getting the incidents on database.txt");
    	BufferedReader in = null;
    	try {
    		in = new BufferedReader(new FileReader(file));
    	 	String read = null;
    	 		while ((read = in.readLine()) != null) {
    	 			splitedIncidents = read.split(";");
	    		    }
    	} catch (IOException e) {
    		Log.write("There was a problem on Incident Data Base: " + e);
    		e.printStackTrace();
    		} 
    	for(int i = 0; i < splitedIncidents.length; i++){
			Log.write("Incidents: " + splitedIncidents[i]);
    	}
    	generateSplitedFactors();
       return splitedIncidents;        
   	}

	//GENERATE THE OTHER SPLITS VARIABLES
	public void  generateSplitedFactors(){
		String [] temporary;
		Log.write("Making the Split list with " + splitedIncidents.length +" incidents...");
			for(int i = 0; i < splitedIncidents.length; i++){
				temporary = splitedIncidents[i].split(":");
					Log.write(temporary[0]);
					Log.write(temporary[1]);
				IncidentsAux aux = new IncidentsAux(temporary[0], temporary[1]);
				incidentsOnDataBase.add(aux);
			}
		Log.write("Split complete...");
	}
	
	//GET INCIDENTS DATA BASE
	public String[] getSplitedIncidents() {
		return splitedIncidents;
	}
}
