package com.sap.amd.parsers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

import com.sap.amd.bcpandicp.Incident;
import com.sap.amd.bcpandicp.IncidentICP;
import com.sap.amd.bcpandicp.Region;
import com.sap.amd.utils.EnumUtils;
import com.sap.amd.utils.Log;
import com.sap.amd.utils.types.TPriority;
import com.sap.amd.utils.types.TStream;

public class IncidentParser implements Serializable
{
	private static final long serialVersionUID = -969880603506111519L;
	
	private File file;
	
	public IncidentParser(File file)
	{
		this.file = file;
	}
	
	public List<Incident> parseBCP(TStream type)
	{
		Log.write("Parsing " + (type == TStream.CSV ? "comma-separated" : "spreadsheets" ) + " file (" + file.getName() + ")...");
		
		List<Incident> result = new LinkedList<Incident>();

		if (type == TStream.Spreadsheet)
		{
			try
			{
				FileInputStream stream = new FileInputStream(file);
				Workbook workbook = Workbook.getWorkbook(stream);
				Sheet sheet = workbook.getSheet(0);

				int rows = sheet.getRows();

				int ignored = 0;
				int parsed = 0;
				
				for (int i = 1; i < rows; i++)
				{
					try
					{						
						Cell id = sheet.getCell(0, i);
						Cell number = sheet.getCell(1, i);
						Cell workPriority = sheet.getCell(2, i);
						Cell priority = sheet.getCell(3, i);
						Cell description = sheet.getCell(4, i);
						Cell IRTFulfilled = sheet.getCell(5, i);
						Cell IRT = sheet.getCell(6, i);
						Cell component = sheet.getCell(7, i);
						Cell contract = sheet.getCell(8, i);
						Cell country = sheet.getCell(9, i);
						Cell MPT = sheet.getCell(10, i);
						Cell escalation = sheet.getCell(11, i);
						Cell rampUp = sheet.getCell(12, i);
						Cell year = sheet.getCell(13, i);
						Cell lastUpdatedBySAP = sheet.getCell(14, i);
						Cell processor = sheet.getCell(15, i);
						Cell timeOfLastReaction = sheet.getCell(16, i);
						Cell customer = sheet.getCell(17, i);
						Cell status = sheet.getCell(18, i);
						Cell transactionType = sheet.getCell(19, i);
						Cell cimSR = sheet.getCell(20, i);
						Cell devHelpRequest = sheet.getCell(21, i);
						Cell incidentUpdated = sheet.getCell(22, i);
						Cell mptTrafficLight = sheet.getCell(23, i);
						Cell customerCallback = sheet.getCell(24, i);
						Cell customerID = sheet.getCell(25, i);
						Cell numberOfCallsFromCustomer = sheet.getCell(26, i);
						Cell processorID = sheet.getCell(27, i);
						Cell processingOrg = sheet.getCell(28, i);
						Cell serviceTeam = sheet.getCell(29, i);
						Cell creationDate = sheet.getCell(30, i);
						
						Incident incident = new Incident();		

						incident.setID(id.getContents().replaceAll(" ", "").replaceAll("\\", "\\\\"));
						incident.setNumber(number.getContents().replaceAll(" ", ""));
						incident.setWorkPriority(Integer.parseInt(workPriority.getContents().replaceAll(" ", "")));
						incident.setDescription(description.getContents().replace("\\", "\\\\"));
						incident.setPriority((TPriority)(EnumUtils.getEnum(priority.getContents(), TPriority.values())));
						incident.setIRTFulfilled((IRTFulfilled.getContents().trim().equals("OK")) ? true : false);
						incident.setIRT(IRT.getContents());
						incident.setComponent(component.getContents());
						incident.setContract(contract.getContents().replaceAll(" ", "").replace("\\", "\\\\"));
						incident.setEscalated((escalation.getContents().trim().equals("X")) ? true : false);
						incident.setRampUp((rampUp.getContents().trim().equals("X")) ? true : false);
						incident.setYear(Integer.parseInt(year.getContents()));
						incident.setMPT(MPT.getContents());
						incident.setCountry(country.getContents());
						incident.setProcessor(processor.getContents());
						incident.setTimeOfLastReaction(timeOfLastReaction.getContents());
						incident.setCustomer(customer.getContents());
						incident.setStatus(status.getContents());
						incident.setTransactionType(transactionType.getContents());
						incident.setCimSR(cimSR.getContents());
						incident.setDevHelpRequest(devHelpRequest.getContents());
						incident.setIncidentUpdated((incidentUpdated.getContents().trim().equals("X")) ? true : false);
						incident.setMptTrafficLight(mptTrafficLight.getContents());
						incident.setCustomerCallback((customerCallback.getContents().trim().equals("X")) ? true : false);
						incident.setCustomerID(customerID.getContents());
						incident.setNumberOfCallsFromCustomer(numberOfCallsFromCustomer.getContents());
						incident.setProcessorID(processorID.getContents());	
						incident.setLastUpdatedBySAP(lastUpdatedBySAP.getContents());
						incident.setCreationDate(creationDate.getContents());
						incident.setProcessingOrg(processingOrg.getContents());
						incident.setServiceTeam(serviceTeam.getContents());
												
						result.add(incident);
						parsed++;
					}
					catch (Exception e)
					{
						ignored++;
						
						Log.write(e);
					}
				}
				
				workbook.close();
				stream.close();
				
				Log.write(parsed + " out of " + (rows-1) + " incident(s) parsed, " + ignored + " incident(s) ignored");
			}
			catch (Exception e)
			{
				Log.write(e);
			}
		}
		else
		{
			try
			{
				FileInputStream stream = new FileInputStream(file);
				InputStreamReader inputStreamReader = new InputStreamReader(stream, "UTF-16");
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
	
				List<String[]> table = new LinkedList<String[]>();
	
				String line = bufferedReader.readLine();
	
				while (!(line == null))
				{
					String[] row = line.split("\"\t\"");
					
					for (int i = 0; i < row.length; i++)
					{
						row[i] = row[i].replaceAll("\"", "");
					}
					
					table.add(row);
					line = bufferedReader.readLine();
				}
				
				int ignored = 0;
				int parsed = 0;
	
				for (int i = 1; i < table.size(); i++)
				{
					try
					{
						String id = table.get(i)[0];
						String number = table.get(i)[1];
						String workPriority = table.get(i)[2];
						String priority = table.get(i)[3];
						String description = table.get(i)[4];
						String IRTFulfilled = table.get(i)[5];
						String IRT = table.get(i)[6];
						String component = table.get(i)[7];
						String contract = table.get(i)[8];
						String country = table.get(i)[9];
						String MPT = table.get(i)[10];
						String escalation = table.get(i)[11];
						String rampUp = table.get(i)[12];
						String year = table.get(i)[13];
						String lastUpdatedBySAP = table.get(i)[14];
						String processor = table.get(i)[15];
						String timeOfLastReaction = table.get(i)[16];
						String customer = table.get(i)[17];
						String status = table.get(i)[18];
						String transactionType = table.get(i)[19];
						String cimSR = table.get(i)[20];
						String devHelpRequest = table.get(i)[21];
						String incidentUpdated = table.get(i)[22];
						String mptTrafficLight = table.get(i)[23];
						String customerCallback = table.get(i)[24];
						String customerID = table.get(i)[25];
						String numberOfCallsFromCustomer = table.get(i)[26];
						String processorID = table.get(i)[27];
						String processingOrg = table.get(i)[28];
						String serviceTeam = table.get(i)[29];
						String creationDate = table.get(i)[30];
												
						Incident incident = new Incident();
	
						incident.setID(id.replaceAll(" ", ""));
						incident.setNumber(number.replaceAll(" ", ""));
						incident.setWorkPriority(Integer.parseInt(workPriority.replaceAll(" ", "")));
						incident.setPriority((TPriority)(EnumUtils.getEnum(priority, TPriority.values())));
						incident.setDescription(description);
						incident.setIRTFulfilled((IRTFulfilled.trim().equals("OK")) ? true : false);
						incident.setIRT(IRT);
						incident.setComponent(component);
						incident.setContract(contract.replaceAll(" ", ""));
						incident.setCountry(country);
						incident.setMPT(MPT);
						incident.setEscalated((escalation.trim().equals("X")) ? true : false);
						incident.setRampUp((rampUp.trim().equals("X")) ? true : false);
						incident.setYear(Integer.parseInt(year));
						incident.setLastUpdatedBySAP(lastUpdatedBySAP);
						incident.setProcessor(processor);
						incident.setTimeOfLastReaction(timeOfLastReaction);
						incident.setCustomer(customer);
						incident.setStatus(status);
						incident.setTransactionType(transactionType);
						incident.setCimSR(cimSR);
						incident.setDevHelpRequest(devHelpRequest);
						incident.setIncidentUpdated((incidentUpdated.trim().equals("X")) ? true : false);
						incident.setMptTrafficLight(mptTrafficLight);
						incident.setCustomerCallback((customerCallback.trim().equals("X")) ? true : false);
						incident.setCustomerID(customerID);
						incident.setNumberOfCallsFromCustomer(numberOfCallsFromCustomer);
						incident.setProcessorID(processorID);						
						incident.setLastUpdatedBySAP(lastUpdatedBySAP);
						incident.setProcessingOrg(processingOrg);
						incident.setServiceTeam(serviceTeam);
						incident.setCreationDate(creationDate);
						
						result.add(incident);
						parsed++;
					}
					catch (Exception e)
					{
						ignored++;
						
						Log.write(e);
					}
				}
				
				bufferedReader.close();
				
				Log.write(parsed + " out of " + (table.size()-1) + " incident(s) parsed, " + ignored + " incident(s) ignored");
			}
			catch (Exception e)
			{
				Log.write(e);
			}
		}
		
		return result;
	}
	
	public List<IncidentICP> parseICP(TStream type) {
		Log.write("Parsing " + (type == TStream.CSV ? "comma-separated" : "spreadsheets") + " file (" + file.getName()
				+ ")...");

		List<IncidentICP> result = new LinkedList<IncidentICP>();

		if (type == TStream.Spreadsheet) {
			try {
				FileInputStream stream = new FileInputStream(file);
				Workbook workbook = Workbook.getWorkbook(stream);
				Sheet sheet = workbook.getSheet(0);

				int rows = sheet.getRows();

				int ignored = 0;
				int parsed = 0;

				for (int i = 1; i < rows; i++) {
					try {
						Cell objectID = sheet.getCell(0, i);
						Cell mainCategory = sheet.getCell(1, i);
						Cell serviceTeam = sheet.getCell(2, i);
						Cell employeeResponsible = sheet.getCell(3, i);
						Cell status = sheet.getCell(4, i);
						Cell category01 = sheet.getCell(5, i);
						Cell category02 = sheet.getCell(6, i);
						Cell messageNumber = sheet.getCell(7, i);
						Cell priority = sheet.getCell(8, i);
						Cell customer = sheet.getCell(9, i);
						Cell country = sheet.getCell(10, i);
						Cell customerContact = sheet.getCell(11, i);
						Cell creationDate = sheet.getCell(12, i);
						Cell changedDate = sheet.getCell(13, i);
						Cell reportMain = sheet.getCell(14, i);
						Cell description = sheet.getCell(15, i);
						Cell nextUpdateTime = sheet.getCell(16, i);
						Cell sentFrom = sheet.getCell(17, i);
						Cell component = sheet.getCell(18, i);
						Cell messageStatus = sheet.getCell(19, i);
						Cell messageLevel = sheet.getCell(20, i);
						Cell messagePriority = sheet.getCell(21, i);
						Cell messageProcessor = sheet.getCell(22, i);
						Cell messageChangedTime = sheet.getCell(23, i);
						Cell acrfInfo = sheet.getCell(24, i);
						Cell reason = sheet.getCell(25, i);

						IncidentICP incident = new IncidentICP();

						incident.setObjectID(objectID.getContents().replaceAll(" ", ""));
						incident.setMainCategory(mainCategory.getContents());
						incident.setServiceTeam(serviceTeam.getContents());
						incident.setEmployeeResponsible(employeeResponsible.getContents());
						incident.setStatus(status.getContents());
						incident.setCategory01(category01.getContents());
						incident.setCategory02(category02.getContents());
						incident.setMessageNumber(messageNumber.getContents());
						incident.setPriority((TPriority)(EnumUtils.getEnum(priority.getContents(), TPriority.values())));
						incident.setCustomer(customer.getContents().replace("\\", "\\\\"));
						incident.setCountry(country.getContents());
						incident.setCustomerContact(customerContact.getContents());
						incident.setCreationDate(creationDate.getContents().trim());
						incident.setChangedDate(changedDate.getContents().trim());
						incident.setReportMain(reportMain.getContents());
						incident.setDescription(description.getContents().replace("\\", "\\\\"));
						incident.setNextUpdateTime(nextUpdateTime.getContents().trim());
						incident.setSentFrom(sentFrom.getContents());
						incident.setComponent(component.getContents());
						incident.setMessageStatus(messageStatus.getContents());
						incident.setMessageLevel(messageLevel.getContents());
						incident.setMessagePriority((TPriority)(EnumUtils.getEnum(messagePriority.getContents(), TPriority.values())));
						incident.setMessageProcessor(messageProcessor.getContents());
						incident.setMessageChangedTime(messageChangedTime.getContents().trim());
						incident.setAcrfInfo(acrfInfo.getContents());
						incident.setReason(reason.getContents());
										
						result.add(incident);
						parsed++;
					} catch (Exception e) {
						ignored++;

						Log.write(e);
					}
				}

				workbook.close();
				stream.close();
				
				Log.write(
						parsed + " out of " + (rows - 1) + " incident(s) parsed, " + ignored + " incident(s) ignored");
			} catch (Exception e) {
				Log.write(e);
			}
		} else {
			try {
				FileInputStream stream = new FileInputStream(file);
				InputStreamReader inputStreamReader = new InputStreamReader(stream, "UTF-16");
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

				List<String[]> table = new LinkedList<String[]>();

				String line = bufferedReader.readLine();

				while (!(line == null)) {
					String[] row = line.split("\"\t\"");

					for (int i = 0; i < row.length; i++) {
						row[i] = row[i].replaceAll("\"", "");
					}

					table.add(row);
					line = bufferedReader.readLine();
				}

				int ignored = 0;
				int parsed = 0;

				for (int i = 1; i < table.size(); i++) {
					try {
						String objectID = table.get(i)[0];
						String mainCategory = table.get(i)[1];
						String serviceTeam = table.get(i)[2];
						String employeeResponsible = table.get(i)[3];
						String status = table.get(i)[4];
						String category01 = table.get(i)[5];
						String category02 = table.get(i)[6];
						String messageNumber = table.get(i)[7];
						String priority = table.get(i)[8];
						String customer = table.get(i)[9];
						String country = table.get(i)[10];
						String customerContact = table.get(i)[11];
						String creationDate = table.get(i)[12];
						String changedDate = table.get(i)[13];
						String reportMain = table.get(i)[14];
						String description = table.get(i)[15];
						String nextUpdateTime = table.get(i)[16];
						String sentFrom = table.get(i)[17];
						String component = table.get(i)[18];
						String messageStatus = table.get(i)[19];
						String messageLevel = table.get(i)[20];
						String messagePriority = table.get(i)[21];
						String messageProcessor = table.get(i)[22];
						String messageChangedTime = table.get(i)[23];
						String acrfInfo = table.get(i)[24];
						String reason = table.get(i)[25];

						IncidentICP incident = new IncidentICP();

						incident.setObjectID(objectID.replaceAll(" ", ""));
						incident.setMainCategory(mainCategory);
						incident.setServiceTeam(serviceTeam);
						incident.setEmployeeResponsible(employeeResponsible);
						incident.setStatus(status);
						incident.setCategory01(category01);
						incident.setCategory02(category02);
						incident.setMessageNumber(messageNumber);
						incident.setPriority((TPriority)(EnumUtils.getEnum(priority, TPriority.values())));
						incident.setCustomer(customer);
						incident.setCountry(country);
						incident.setCustomerContact(customerContact);
						incident.setCreationDate(creationDate.trim());
						incident.setChangedDate(changedDate.trim());
						incident.setReportMain(reportMain);
						incident.setDescription(description.replace("\\", "\\\\"));
						incident.setNextUpdateTime(nextUpdateTime.trim());
						incident.setSentFrom(sentFrom);
						incident.setComponent(component);
						incident.setMessageStatus(messageStatus);
						incident.setMessageLevel(messageLevel);
						incident.setMessagePriority((TPriority)(EnumUtils.getEnum(messagePriority, TPriority.values())));
						incident.setMessageProcessor(messageProcessor);
						incident.setMessageChangedTime(messageChangedTime.trim());
						incident.setAcrfInfo(acrfInfo);
						incident.setReason(reason);
												
						result.add(incident);
						parsed++;
					} catch (Exception e) {
						ignored++;

						Log.write(e);
					}
				}

				bufferedReader.close();

				Log.write(parsed + " out of " + (table.size() - 1) + " incident(s) parsed, " + ignored
						+ " incident(s) ignored");
			} catch (Exception e) {
				Log.write(e);
			}
		}

		return result;
	}
}
