package com.sap.amd.rmi;

import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.gln.arf.entities.Dictionary;
import com.gln.arf.entities.Property;
import com.gln.arf.io.Decoder;
import com.sap.amd.bcpandicp.Incident;
import com.sap.amd.bcpandicp.IncidentICP;
import com.sap.amd.bcpandicp.Processor;
import com.sap.amd.bcpandicp.Region;
import com.sap.amd.dispatcher.Button;
import com.sap.amd.dispatcher.ConfigIDs;
import com.sap.amd.dispatcher.Context;
import com.sap.amd.dispatcher.CrashEmail;
import com.sap.amd.dispatcher.IncidentsDataBase;
import com.sap.amd.dispatcher.Link;
import com.sap.amd.importers.IncidentImporter;
import com.sap.amd.parsers.ConfigParser;
import com.sap.amd.parsers.IncidentParser;
import com.sap.amd.parsers.SearchParser;
import com.sap.amd.parsers.SpreadsheetParser;
import com.sap.amd.utils.Log;
import com.sap.amd.utils.State;
import com.sap.amd.utils.exceptions.SessionUnlockerException;
import com.sap.amd.utils.types.TDispatchState;
import com.sap.amd.utils.types.TImportState;
import com.sap.amd.utils.types.TState;
import com.sap.amd.utils.types.TStream;

public class Provider implements Wrapper {
	private IncidentImporter incidentImporter;
	private String user = "robot";
	private String processor = System.getProperty("user.name");
	private String session = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
	private boolean locked;
	private boolean busy;
	private ConfigIDs resultIDsBCP;
	private ConfigIDs resultIDsICP;
	private List<Incident> incidentsResultBCP;
	private List<IncidentICP> incidentsResultICP;
	private boolean isBCP;
	private WebDriver driver;
	private File profileFolder;
	
	public Provider(String user) {
		this.user = user;
	}
	
	public Provider(){}

	
	@Override
	public void clearIncidentImporter() throws RemoteException {
		this.incidentImporter = null;
	}

	@Override
	public State<TImportState> getIncidentImportProgress() throws RemoteException {
		try {
			return incidentImporter.getState();
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public void getIncidentImportResultBCPICP(boolean isBCP) throws RemoteException {
		try
		{
			if(isBCP){
				incidentsResultBCP = incidentImporter.getResult();
				insertInDatabase(isBCP);
			}else
			{
				incidentsResultICP = incidentImporter.getResultICP();
				insertInDatabase(isBCP);
			}
		}
		catch (Exception e) {
		
		}
	}
	
	@Override
	public String post(String url, String json) throws IOException {
		try {
			// Proxy infos
			int proxyPort = 8080;
			String proxyHost = "proxy";

			// Configurated proxy
			Proxy proxyTest = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
			
			String username = getUsernameProperties();
			String password = getPasswordProperties();
			
			// Creating the client with the proxy in it
			OkHttpClient.Builder builder = new OkHttpClient.Builder().addInterceptor(new BasicAuthInterceptor(username, password)).proxy(proxyTest);
			OkHttpClient client = builder.build();

			// setting media type to post it
			MediaType JSON = MediaType.parse("application/json; charset=utf-8");

			// creating the request with the json in it(in the body) and placing
			// the url
			RequestBody body = RequestBody.create(JSON, json);
			Request request = new Request.Builder().url(url).post(body).build();
			Log.write("URL: " + url);
			Log.write("Sending JSON: " + json);
			// Response response = client.newCall(request).execute();
			Response response = client.newCall(request).execute();
			Log.write("Resposta: " + response.body().string());
			return response.body().string();
		} catch (Exception e) {
			return "";
		}
	}
	
	@Override
	public String update(String url, String json) throws IOException {
		try {
			// Proxy infos
			int proxyPort = 8080;
			String proxyHost = "proxy";

			// Configurated proxy
			Proxy proxyTest = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));

			String username = getUsernameProperties();
			String password = getPasswordProperties();
			
			// Creating the client with the proxy in it
			OkHttpClient.Builder builder = new OkHttpClient.Builder().addInterceptor(new BasicAuthInterceptor(username, password)).proxy(proxyTest);
			OkHttpClient client = builder.build();
			
			// setting media type to post it
			MediaType JSON = MediaType.parse("application/json; charset=utf-8");
						
			RequestBody body = RequestBody.create(JSON, json);
			
			Request request = new Request.Builder().url(url).put(body).build();
			Log.write("URL: " + url);
			Log.write("Sending Update for an job");
			// Response response = client.newCall(request).execute();
			Response response = client.newCall(request).execute();
			
			return response.body().string();
		} catch (Exception e) {
			return "";
		}
	}
	
	public String getUsernameProperties () throws FileNotFoundException, IOException
	{
		Properties login = new Properties();
		try (FileReader in = new FileReader("C:\\Users\\I853300\\Desktop\\user.properties")) {
		    login.load(in);
		}
		String username = login.getProperty("username");
		
		return username;
	}
	
	public String getPasswordProperties() throws FileNotFoundException, IOException {
		Properties login = new Properties();
		try (FileReader in = new FileReader("C:\\Users\\I853300\\Desktop\\user.properties")) {
		    login.load(in);
		}
		
		String password = login.getProperty("password");
		
		return password;
	}
	
	public class BasicAuthInterceptor implements Interceptor {

	    private String credentials;

	    public BasicAuthInterceptor(String user, String password) {
	        this.credentials = Credentials.basic(user, password);
	    }

	    @Override
	    public Response intercept(Chain chain) throws IOException {
	        Request request = chain.request();
	        Request authenticatedRequest = request.newBuilder()
	                    .header("Authorization", credentials).build();
	        return chain.proceed(authenticatedRequest);
	    }

	}
	
	@Override
	public String getNextJob(String url) throws IOException {
		try {
			
			// Proxy infos
			int proxyPort = 8080;
			String proxyHost = "proxy";

			// Configurated proxy
			Proxy proxyTest = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
			
			String username = getUsernameProperties();
			String password = getPasswordProperties();
			
			// Creating the client with the proxy in it
			OkHttpClient.Builder builder = new OkHttpClient.Builder().addInterceptor(new BasicAuthInterceptor(username, password)).proxy(proxyTest);
			OkHttpClient client = builder.build();
					

			Request request = new Request.Builder().url(url).get().build();
			Log.write("URL: " + url);
			Log.write("Sending GET for the next job");
			// Response response = client.newCall(request).execute();
			Response response = client.newCall(request).execute();
			
			return response.body().string();
		} catch (Exception e) {
			return "";
		}
	}

	private String formatDateUTC(Date date) throws ParseException {
		if (!(date == null)) {
			try {
				SimpleDateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
				return utcFormat.format(date);
			} catch (Exception e) {
				Log.write("Error formatting Date for UTC or it is null: " + date);
				return null;
			}
		}
		return null;
	}

	@Override
	public boolean insertInDatabase(boolean isBCP) throws RemoteException {
		try {

			if (isBCP) {
				Log.write("----------------Total Incidents BCP----------------");
				Log.write("----------------" + incidentsResultBCP.size() + "----------------");
				Log.write("----------------Sending First Incident BCP----------------");
				Log.write("----------------" + formatDateUTC(new Date()) + "----------------");
				String json = "";
				
				JSONArray arrayJSON = new JSONArray();
				
				for (int i = 0; i < incidentsResultBCP.size(); i++) {
					
					// getting all data for post
					String incidentID = incidentsResultBCP.get(i).getID();
					String number = incidentsResultBCP.get(i).getNumber();
					String workPriority = "" + incidentsResultBCP.get(i).getWorkPriority();
					String priority = "" + incidentsResultBCP.get(i).getPriority();
					String description = incidentsResultBCP.get(i).getDescription().replace("\\s+", " ").trim();
					String IRT = formatDateUTC(incidentsResultBCP.get(i).getIRT());
					String component = incidentsResultBCP.get(i).getComponent().replace("\\s+", " ").trim();
					String contract = incidentsResultBCP.get(i).getContract().replace("\\s+", " ").trim();
					String country = incidentsResultBCP.get(i).getCountry().replace("\\s+", " ").trim();
					String MPT = formatDateUTC(incidentsResultBCP.get(i).getMPT());
					String year = "" + incidentsResultBCP.get(i).getYear();
					String lastUpdatedBySAP = formatDateUTC(incidentsResultBCP.get(i).getLastUpdatedBySAP());
					String processor = incidentsResultBCP.get(i).getProcessor().replace("\\s+", " ").trim();
					String timeOfLastReaction = formatDateUTC(incidentsResultBCP.get(i).getTimeOfLastReaction());
					String customer = incidentsResultBCP.get(i).getCustomer().replace("\\s+", " ").trim();
					String status = incidentsResultBCP.get(i).getStatus();
					String transactionType = incidentsResultBCP.get(i).getTransactionType().replace("\\s+", " ").trim();
					String cimSR = incidentsResultBCP.get(i).getCimSR().replace("\\s+", " ").trim();
					String devHelpRequest = incidentsResultBCP.get(i).getDevHelpRequest().replace("\\s+", " ").trim();
					String customerID = incidentsResultBCP.get(i).getCustomerID().replace("\\s+", " ").trim();
					String numberOfCallsFromCustomer = incidentsResultBCP.get(i).getNumberOfCallsFromCustomer();
					String processorID = incidentsResultBCP.get(i).getProcessorID().replace("\\s+", " ").trim();
					String mptTrafficLight = incidentsResultBCP.get(i).getMptTrafficLight();
					String processingOrg = incidentsResultBCP.get(i).getProcessingOrg().replace("\\s+", " ").trim();
					String serviceTeam = incidentsResultBCP.get(i).getServiceTeam().replace("\\s+", " ").trim();
					String creationDate = formatDateUTC(incidentsResultBCP.get(i).getCreationDate());

					String IRTTrafficLight;
					if (incidentsResultBCP.get(i).isIRTFulfilled() == true) {
						IRTTrafficLight = "X";
					} else {
						IRTTrafficLight = "";
					}
					
					String escalation;
					if (incidentsResultBCP.get(i).isEscalated() == true) {
						escalation = "X";
					} else {
						escalation = "";
					}
					
					String rampUp;
					if (incidentsResultBCP.get(i).isRampUp() == true) {
						rampUp = "X";
					} else {
						rampUp = "";
					}
					
					String incidentUpdated;
					if (incidentsResultBCP.get(i).isIncidentUpdated() == true) {
						incidentUpdated = "X";
					} else {
						incidentUpdated = "";
					}
							
					String customerCallback;
					if (incidentsResultBCP.get(i).isCustomerCallback() == true) {
						customerCallback = "X";
					} else {
						customerCallback = "";
					}
					
					JSONObject jsonObject = new JSONObject();
					
					jsonObject.put("incidentID", incidentID);
					jsonObject.put("incidentNumber", number);
					jsonObject.put("wp", workPriority);
					jsonObject.put("priority", priority);
					jsonObject.put("description", description);
					jsonObject.put("irtTrafficLight", IRTTrafficLight);
					jsonObject.put("component", component);
					jsonObject.put("contractType", contract);
					jsonObject.put("country", country);
					jsonObject.put("escalation", escalation);
					jsonObject.put("incidentYear", year);
					jsonObject.put("processor", processor);
					jsonObject.put("customer", customer);
					jsonObject.put("status", status);
					jsonObject.put("transactionType", transactionType);
					jsonObject.put("cimServiceRequest", cimSR);
					jsonObject.put("devHelpRequest", devHelpRequest);
					jsonObject.put("incidentUpdated", incidentUpdated);
					jsonObject.put("mptTrafficLight", mptTrafficLight);
					jsonObject.put("customerCallback", customerCallback);
					jsonObject.put("incidentIDCustomer", customerID);
					jsonObject.put("numberCallsFromCustomer", numberOfCallsFromCustomer);
					jsonObject.put("processorID", processorID);
					jsonObject.put("rampUp", rampUp);
					jsonObject.put("processingOrg", processingOrg);
					jsonObject.put("serviceTeam", serviceTeam);
					jsonObject.put("creationDate", creationDate);
					jsonObject.put("irtPlannedEndDate", IRT);
					jsonObject.put("mptPlannedEndDate", MPT);
					jsonObject.put("lastUpdateBySAPAt", lastUpdatedBySAP);
					jsonObject.put("timeOfLastReaction", timeOfLastReaction);
					
					arrayJSON.put(jsonObject);
					//json = jsonObject.toString();
					//System.out.println(json);
					
				}
				
				json = arrayJSON.toString();	
				
				String response = post(
						"https://p2monitoringspringi853300trial.hanatrial.ondemand.com/P2MonitoringSpring/rest/incidentBCP/",
						json);
				
				//TODO create a verification method				
				System.out.println(response);
				
				Log.write("----------------Sending Last Incident BCP----------------");
				Log.write("----------------" + formatDateUTC(new Date()) + "----------------");
			} else
			{// if it's service request
				
				JSONArray arrayJSON = new JSONArray();

				Log.write("----------------Total Incidents ICP----------------");
				Log.write("----------------" + incidentsResultICP.size() + "----------------");
				Log.write("----------------Sending First Incident ICP----------------");
				Log.write("----------------" + formatDateUTC(new Date()) + "----------------");
				String json = "";

				for (int i = 0; i < incidentsResultICP.size(); i++) {
										
					// getting all data for post
					String creationDate = incidentsResultICP.get(i).getCreationDate();
					String changedDate = incidentsResultICP.get(i).getChangedDate();
					String nextUpdateTime = incidentsResultICP.get(i).getNextUpdateTime();
					String messageChangedTime = incidentsResultICP.get(i).getMessageChangedTime();
					String objectID = incidentsResultICP.get(i).getObjectID().replaceAll("[\\s\\t]", " ").trim();
					String mainCategory = incidentsResultICP.get(i).getMainCategory().replaceAll("[\\s\\t]", " ").trim();
					String serviceTeam = ""	+ incidentsResultICP.get(i).getServiceTeam().replaceAll("[\\s\\t]", " ").trim();
					String employeeResponsible = ""	+ incidentsResultICP.get(i).getEmployeeResponsible().replaceAll("[\\s\\t]", " ").trim();
					String status = incidentsResultICP.get(i).getStatus().replaceAll("[\\s\\t]", " ").trim();
					String category01 = "" + incidentsResultICP.get(i).getCategory01().replaceAll("[\\s\\t]", " ").trim();
					String category02 = incidentsResultICP.get(i).getCategory02().replaceAll("[\\s\\t]", " ").trim();
					String messageNumber = incidentsResultICP.get(i).getMessageNumber().replaceAll("[\\s\\t]", " ").trim();
					String priority = "" + incidentsResultICP.get(i).getPriority();
					String customer = "" + incidentsResultICP.get(i).getCustomer().replaceAll("[\\s\\t]", " ").trim().replace("\\", "\\\\");
					String country = "" + incidentsResultICP.get(i).getCountry().replaceAll("[\\s\\t]", " ").trim();
					String customerContact = ""	+ incidentsResultICP.get(i).getCustomerContact().replaceAll("[\\s\\t]", " ").trim();
					String reportMain = incidentsResultICP.get(i).getReportMain().replaceAll("[\\s\\t]", " ").trim();
					String description = incidentsResultICP.get(i).getDescription().replaceAll("[\\s\\t]", " ").trim().replace("\\", "\\\\");
					String sentFrom = incidentsResultICP.get(i).getSentFrom().replaceAll("[\\s\\t]", " ").trim();
					String component = incidentsResultICP.get(i).getComponent().replaceAll("[\\s\\t]", " ").trim();
					String messageStatus = incidentsResultICP.get(i).getMessageStatus().replaceAll("[\\s\\t]", " ").trim();
					String messageLevel = incidentsResultICP.get(i).getMessageLevel().replaceAll("[\\s\\t]", " ");
					String messagePriority = "" + incidentsResultICP.get(i).getMessagePriority();
					String messageProcessor = incidentsResultICP.get(i).getMessageProcessor().replaceAll("[\\s\\t]", " ").trim();
					String acrfInfo = incidentsResultICP.get(i).getAcrfInfo().replaceAll("[\\s\\t]", " ").trim();
					String reason = incidentsResultICP.get(i).getReason().replaceAll("[\\s\\t]", " ").trim();
	
					JSONObject jsonObject = new JSONObject();
					
					jsonObject.put("objectID", objectID);
					jsonObject.put("mainCategory", mainCategory);
					jsonObject.put("serviceTeam", serviceTeam);
					jsonObject.put("employeeResponsible", employeeResponsible);
					jsonObject.put("status", status);
					jsonObject.put("category01", category01);
					jsonObject.put("category02", category02);
					jsonObject.put("messageNumber", messageNumber);
					jsonObject.put("priority", priority);
					jsonObject.put("customer", customer);
					jsonObject.put("country", country);
					jsonObject.put("customerContact", customerContact);
					jsonObject.put("reportMain", reportMain);
					jsonObject.put("description", description);
					jsonObject.put("sentFrom", sentFrom);
					jsonObject.put("component", component);
					jsonObject.put("messageStatus", messageStatus);
					jsonObject.put("messageLevel", messageLevel);
					jsonObject.put("messageProcessor", messageProcessor);
					jsonObject.put("acrfInfo", acrfInfo);
					jsonObject.put("reason", reason);
					jsonObject.put("messagePriority", messagePriority);
					jsonObject.put("serviceTeam", serviceTeam);
					
					if (creationDate == null) {
						jsonObject.put("creationDate", JSONObject.NULL);
					} else {
						jsonObject.put("creationDate", creationDate);
					}

					if (changedDate == null) {
						jsonObject.put("changedDate", JSONObject.NULL);
					} else {
						jsonObject.put("changedDate", changedDate);
					}

					if (messageChangedTime == null) {
						jsonObject.put("messageChangedTime", JSONObject.NULL);
					} else {
						jsonObject.put("messageChangedTime", messageChangedTime);
					}

					if (nextUpdateTime == null) {
						jsonObject.put("nextUpdateTime", JSONObject.NULL);
					} else {
						jsonObject.put("nextUpdateTime", nextUpdateTime);
					}
					
					arrayJSON.put(jsonObject);
					
				}
				
				json = arrayJSON.toString();
				
				String response = post(
						"https://p2monitoringspringi853300trial.hanatrial.ondemand.com/P2MonitoringSpring/rest/incidentICP/", json);
				
				//TODO create a verification method
				if (response == json) {
					//Log.write("Json and Response Equal");
				}
				System.out.println(response);

				Log.write("----------------Sending Last Incident ICP----------------");
				Log.write("----------------" + formatDateUTC(new Date()) + "----------------");
			}
			return true;
		} catch (

		Exception e) {
			return false;
		}
	}

	@Override
	public String getProcessor() throws RemoteException {
		return processor;
	}

	@Override
	public List<String> getSearchesFromFile(boolean isBCP) throws RemoteException {
		return new SearchParser().parseSearches(isBCP);
	}


	@Override
	public String getSession() throws RemoteException {
		return session;
	}

	@Override
	public String getUser() throws RemoteException {
		return user;
	}

	@Override
	public void loginServer(){
		
			profileFolder = new File(System.getenv().get("APPDATA") + "\\Mozilla\\Firefox\\Profiles");
			profileFolder = getDefaultProfileFolder(profileFolder.listFiles());
			
			if (profileFolder != null) {
				FirefoxProfile profile = new FirefoxProfile(profileFolder);
				profile.setPreference("security.default_personal_cert", "Select Automatically");

				try {
					driver = new FirefoxDriver(profile);
				} catch (Exception e) {
					Log.write("Firefox could not be opened. Either you do not have it installed, or it is out of date.");
					return;
				}
				driver.get("https://p2monitoringspringi853300trial.hanatrial.ondemand.com/P2MonitoringSpring/rest/");

				try {
					Thread.sleep(15000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				driver.quit();
			}
	}
	
	private File getDefaultProfileFolder(File[] folders) {
		for (File file : folders) {
			if (file.getName().endsWith(".default")) {
				return file;
			}
		}

		return null;
	}
	
	@Override
	public void importIncidents(String search, boolean isBCP) throws RemoteException {
		this.busy = true;

		// Uses the team folder in order to download and load the Work Monitor
		// Spreadsheet
		String teamFolder = ".\\resources\\CIM\\temp";
		if (isBCP == true) {
			incidentImporter = new IncidentImporter(search, teamFolder, 60, resultIDsBCP, isBCP);
		} else {
			incidentImporter = new IncidentImporter(search, teamFolder, 60, resultIDsICP, isBCP);
		}

		incidentImporter.importIncidents();

		// this.busy = false;
	}

	@Override
	public boolean isDefaultSpreadsheetAvailable() throws RemoteException {
		return new File(".\\resources\\" + user + "\\Processors.xls").exists();
	}

	@SuppressWarnings("unchecked")
	@Override
	public State<TState> authenticate(String user, String password) throws RemoteException {
		File file = new File(".\\Stubs.arf");

		if (file.exists()) {
			Dictionary stubs = new Decoder(file).decode();

			Property<?> stubUser = stubs.get(user);

			if (stubUser != null) {
				String stubPassword = ((Property<String>) stubUser).getValue();

				if (password.equals(stubPassword)) {
					this.user = user;

					return new State<TState>(TState.Success);
				} else {
					return new State<TState>(TState.Error, new String[] { "Wrong Password", "Password is incorrect." });
				}
			} else {
				return new State<TState>(TState.Error,
						new String[] { "Wrong User", "User is not registered in the server." });
			}
		} else {
			return new State<TState>(TState.Error,
					new String[] { "Database Not Found", "User database was not found." });
		}
	}

	@Override
	public List<Processor> parseProcessors() throws RemoteException {
		parseIdsBCP();
		parseIdsICP();
		SpreadsheetParser parser = new SpreadsheetParser(new File(".\\resources\\" + user + "\\Processors.xls"));
		return parser.parseProcessors();
	}

	@Override
	public List<Region> parseRegions() throws RemoteException {
		SpreadsheetParser parser = new SpreadsheetParser(new File(".\\resources\\" + user + "\\Processors.xls"));
		return parser.parseRegions();
	}

	@Override
	public void saveSearchesToFile(List<String> searches, boolean isBCP) throws RemoteException {
		new SearchParser().saveSearches(searches, isBCP);
	}

	@Override
	public State<TState> saveSpreadsheet(List<Processor> processors) throws RemoteException {
		File file = new File(".\\resources\\" + user + "\\Processors.xls");

		if (file.exists()) {
			SpreadsheetParser handler = new SpreadsheetParser(file);
			return handler.saveSpreadsheet(processors, true);
		}

		return new State<TState>(TState.Error,
				new String[] { "File Not Found", "File could not be located in the server." });
	}

	@Override
	public void sendCrashEmail(Exception exception, String message) throws RemoteException {
		SpreadsheetParser parser = new SpreadsheetParser(new File(".\\resources\\" + user + "\\Processors.xls"));
		new CrashEmail(exception, message, parser.getInterestedParties(), processor, session).send();
	}

	@Override
	public void sendBugEmail(Exception exception, String message) throws RemoteException {
		SpreadsheetParser parser = new SpreadsheetParser(new File(".\\resources\\" + user + "\\Processors.xls"));
		new CrashEmail(exception, message, parser.getInterestedParties(), processor, session).sendBugEmail();
	}

	@Override
	public void setSession(String processor, String session) throws RemoteException {
		this.processor = processor;
		this.session = session;

		Log.setLogStreams(user, processor, session);
	}

	@Override
	public ConfigIDs parseIdsBCP() throws RemoteException {

		ConfigIDs aux = new ConfigIDs();

		aux.setSharedSearchesIDArea("C18_W61_V62_V63");
		aux.setSavedSearchesIDArea("C25_W85_V86");
		aux.setButtonExportExcel("C29_W99_V102_V111_EXPORT_TO_EXCEL");
		aux.setButtonEdit("C12_W37_V40_EDIT");
		aux.setButtonSave("C12_W37_V40_SAVE");
		aux.setCurrenteProcessorText("C12_W37_V40_V50_btpartnerset_emp_resp_name");
		aux.setServiceTeamText("C12_W37_V40_V50_btpartnerset_service_unit_name");

		ConfigParser parser = new ConfigParser();
		ConfigIDs result = parser.parseIds().get(0);

		if (result == null) {
			Log.write("Your are using the default IDs on Firefox (ConfigIDs.xls returned null):");
			Log.write("Shared Search C18_W61_V62_V63");
			Log.write("Saved Search C25_W85_V86");
			Log.write("Export Excel C29_W99_V102_V111_EXPORT_TO_EXCEL");
			Log.write("Edit C12_W37_V40_EDIT");
			Log.write("Save C12_W37_V40_SAVE");
			Log.write("Processor Text C12_W37_V40_V50_btpartnerset_emp_resp_name");
			Log.write("Service team Text C12_W37_V40_V50_btpartnerset_service_unit_name");
			return aux;
		} else {
			if (result.getButtonEdit() == null || result.getButtonEdit().length() < 5) {
				result.setButtonEdit(aux.getButtonEdit());
			}

			if (result.getButtonExportExcel() == null || result.getButtonExportExcel().length() < 5) {
				result.setButtonExportExcel(aux.getButtonExportExcel());
			}

			if (result.getButtonSave() == null || result.getButtonSave().length() < 5) {
				result.setButtonSave(aux.getButtonSave());
			}

			if (result.getCurrenteProcessorText() == null || result.getCurrenteProcessorText().length() < 5) {
				result.setCurrenteProcessorText(aux.getCurrenteProcessorText());
			}

			if (result.getServiceTeamText() == null || result.getServiceTeamText().length() < 5) {
				result.setServiceTeamText(aux.getServiceTeamText());
			}
			if (result.getSavedSearchesIDArea() == null || result.getSavedSearchesIDArea().length() < 5) {
				result.setSavedSearchesIDArea(aux.getSavedSearchesIDArea());
			}
			if (result.getSharedSearchesIDArea() == null || result.getSharedSearchesIDArea().length() < 5) {
				result.setSharedSearchesIDArea(aux.getSharedSearchesIDArea());
			}

			Log.write("Your are using the following IDs on Firefox");
			Log.write("Shared Search " + result.getSharedSearchesIDArea());
			Log.write("Saved Search " + result.getSavedSearchesIDArea());
			Log.write("Export Excel " + result.getButtonExportExcel());
			Log.write("Edit " + result.getButtonEdit());
			Log.write("Save " + result.getButtonSave());
			Log.write("Processor Text " + result.getCurrenteProcessorText());
			Log.write("Service team " + result.getServiceTeamText());

			resultIDsBCP = result;
			return result;
		}
	}

	@Override
	public ConfigIDs parseIdsICP() throws RemoteException {

		ConfigIDs aux = new ConfigIDs();

		aux.setSharedSearchesIDArea("");
		aux.setSavedSearchesIDArea("");
		aux.setButtonExportExcel("");
		aux.setButtonEdit("");
		aux.setButtonSave("");
		aux.setCurrenteProcessorText("");
		aux.setServiceTeamText("");

		ConfigParser parser = new ConfigParser();
		ConfigIDs result = parser.parseIds().get(1);

		if (result == null) {
			Log.write("Your are using the default IDs to ICP on Firefox (ConfigIDs.xls returned null):");
			Log.write("Shared Search ");
			Log.write("Saved Search ");
			Log.write("Export Excel ");
			Log.write("Edit ");
			Log.write("Save ");
			Log.write("Processor Text ");
			Log.write("Service team Text ");
			return aux;
		} else {
			if (result.getButtonEdit() == null || result.getButtonEdit().length() < 5) {
				result.setButtonEdit(aux.getButtonEdit());
			}

			if (result.getButtonExportExcel() == null || result.getButtonExportExcel().length() < 5) {
				result.setButtonExportExcel(aux.getButtonExportExcel());
			}

			if (result.getButtonSave() == null || result.getButtonSave().length() < 5) {
				result.setButtonSave(aux.getButtonSave());
			}

			if (result.getCurrenteProcessorText() == null || result.getCurrenteProcessorText().length() < 5) {
				result.setCurrenteProcessorText(aux.getCurrenteProcessorText());
			}

			if (result.getServiceTeamText() == null || result.getServiceTeamText().length() < 5) {
				result.setServiceTeamText(aux.getServiceTeamText());
			}
			if (result.getSavedSearchesIDArea() == null || result.getSavedSearchesIDArea().length() < 5) {
				result.setSavedSearchesIDArea(aux.getSavedSearchesIDArea());
			}
			if (result.getSharedSearchesIDArea() == null || result.getSharedSearchesIDArea().length() < 5) {
				result.setSharedSearchesIDArea(aux.getSharedSearchesIDArea());
			}

			Log.write("Your are using the following IDs on Firefox");
			Log.write("Shared Search " + result.getSharedSearchesIDArea());
			Log.write("Saved Search " + result.getSavedSearchesIDArea());
			Log.write("Export Excel " + result.getButtonExportExcel());
			Log.write("Edit " + result.getButtonEdit());
			Log.write("Save " + result.getButtonSave());
			Log.write("Processor Text " + result.getCurrenteProcessorText());
			Log.write("Service team " + result.getServiceTeamText());

			resultIDsICP = result;
			return result;
		}
	}

	@Override
	public void writeToLog(String text) throws RemoteException // Used to write
																// remotely to
																// log
	{
		System.out.println(text);
	}

	@Override
	public boolean isLocked() throws RemoteException {
		return locked;
	}

	@Override
	public void lock() throws RemoteException {
		locked = true;
	}

	@Override
	public void unlock() throws RemoteException {
		locked = false;
	}

	@Override
	public boolean isBusy() throws RemoteException {
		return busy;
	}

	@Override
	public void reportTaskConclusion() throws RemoteException {
		busy = false;
	}

	@Override
	public void startSessionUnlocker(Wrapper provider) throws RemoteException, SessionUnlockerException {
		new Thread(new SessionUnlocker(provider, 10)).start();
	}

	@Override
	public void killClient() throws RemoteException {
		this.processor = "\\AMDSM";
	}

	@Override
	public State<TImportState> getSearchImportProgress() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getSearchImportResult() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void clearSearchImporter() throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<State<TDispatchState>> getDispatchProgress() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void clearDispatcher() throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<String> getAvailableSimulators(List<Incident> incidents, List<Processor> processors,
			Processor firstProcessor) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void clearSimulatorLoader() throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ArrayList<String> announcementsStringResult() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean writingAnnouncement(String text, int type, boolean isGlobal) throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void importSearches(boolean isBCP) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

}
