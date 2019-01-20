package com.sap.amd.dispatcher;

import com.sap.amd.utils.Log;


public class DataBaseCom {
	
	private String IncidentNumber;
	private int year;
	private String dataRecord;
	

	public DataBaseCom(String incidentNumber, int year, String dataRecord) {
		super();
		IncidentNumber = incidentNumber;
		this.year = year;
		this.dataRecord = dataRecord;
	}
	
	public String transformToString(){
		String wr = IncidentNumber+"."+ year+":"+dataRecord+";";
		Log.write(IncidentNumber+"."+ year+":"+dataRecord+";");
		return wr;
	}
	
	

}
