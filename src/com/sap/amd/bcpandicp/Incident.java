package com.sap.amd.bcpandicp;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.sap.amd.utils.DateTimeUtils;
import com.sap.amd.utils.types.TPriority;

public class Incident implements Serializable
{
	private static final long serialVersionUID = 7496577409023333060L;

	private String ID;
	private String number;
	private int workPriority;
	private TPriority priority;
	private String description;
	private boolean isIRTFulfilled;
	private Date IRT;
	private String component;
	private String contract;
	private String country;
	private Date MPT;
	private boolean isEscalated;
	private boolean isRampUp;
	private int year;
	private Date lastUpdatedBySAP;
	private String processorInQueue;
	private Date timeOfLastReaction;
    private String customer;
    private String status;
    private String transactionType;
    private String cimSR;
    private String devHelpRequest;
    private boolean incidentUpdated;
    private String mptTrafficLight;
    private boolean customerCallback;
    private String customerID;
    private String numberOfCallsFromCustomer;
    private String processorID;
    private String processingOrg;
    private String serviceTeam;
    private Date creationDate;
    
    public String getProcessingOrg() {
		return processingOrg;
	}

	public void setProcessingOrg(String processingOrg) {
		this.processingOrg = processingOrg;
	}

	public String getServiceTeam() {
		return serviceTeam;
	}

	public void setServiceTeam(String serviceTeam) {
		this.serviceTeam = serviceTeam;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(String date)  throws ParseException{
		creationDate = parseDateWithSeconds(date);
	}
   
    public Date getTimeOfLastReaction() {
		return timeOfLastReaction;
	}

	public void setTimeOfLastReaction(String date) throws ParseException
	{
		timeOfLastReaction = parseDateWithSeconds(date);
	}

	public String getCustomer() {
		return customer;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public String getCimSR() {
		return cimSR;
	}

	public void setCimSR(String cimSR) {
		this.cimSR = cimSR;
	}

	public String getDevHelpRequest() {
		return devHelpRequest;
	}

	public void setDevHelpRequest(String devHelpRequest) {
		this.devHelpRequest = devHelpRequest;
	}

	public boolean isIncidentUpdated() {
		return incidentUpdated;
	}

	public void setIncidentUpdated(boolean incidentUpdated) {
		this.incidentUpdated = incidentUpdated;
	}

	public String getMptTrafficLight() {
		return mptTrafficLight;
	}

	public void setMptTrafficLight(String mptTrafficLight) {
		this.mptTrafficLight = mptTrafficLight;
	}

	public String getCustomerID() {
		return customerID;
	}

	public void setCustomerID(String customerID) {
		this.customerID = customerID;
	}

	public String getNumberOfCallsFromCustomer() {
		return numberOfCallsFromCustomer;
	}

	public void setNumberOfCallsFromCustomer(String numberOfCallsFromCustomer) {
		this.numberOfCallsFromCustomer = numberOfCallsFromCustomer;
	}

	public String getProcessorID() {
		return processorID;
	}

	public void setProcessorID(String processorID) {
		this.processorID = processorID;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getComponent()
	{
		return component;
	}

	public String getContract()
	{
		return contract;
	}

	public String getCountry()
	{
		return country;
	}

	public String getDescription()
	{
		return description;
	}

	public String getID()
	{
		return ID;
	}

	public Date getIRT()
	{
		return IRT;
	}

	public Date getLastUpdatedBySAP()
	{
		return lastUpdatedBySAP;
	}

	public int getMinutes()
	{
		return DateTimeUtils.getMinutesBetween(lastUpdatedBySAP, new Date());
	}
	
	public Date getMPT()
	{
		return MPT;
	}

	public String getNumber()
	{
		return number;
	}

	public TPriority getPriority()
	{
		return priority;
	}

	public int getWorkPriority()
	{
		return workPriority;
	}
	


	public int getYear()
	{
		return year;
	}

	
	public boolean isEscalated()
	{
		return isEscalated;
	}

	public boolean isIRTFulfilled()
	{
		return isIRTFulfilled;
	}
	


	public boolean isRampUp()
	{
		return isRampUp;
	}

	public void setComponent(String component)
	{
		this.component = component;
	}

	public void setContract(String contract)
	{
		this.contract = contract;
	}


	public void setCountry(String country)
	{
		this.country = country;
	}

	public void setEscalated(boolean isEscalated)
	{
		this.isEscalated = isEscalated;
	}

	public void setID(String id)
	{
		this.ID = id;
	}

	private Date parseDateWithoutSeconds(String date) throws ParseException
	{
		 try 
		    {  
				if(date.length() > 3){
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
					return formatter.parse(date);
				}
			}
		 catch (ParseException e) 
		    {
		        return null;
		    }
		return null;
	}
	
	private Date parseDateWithSeconds(String date) throws ParseException
	{
		 try 
		    {  
				if(date.length() > 3){
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					return formatter.parse(date);
				}
			}
		 catch (ParseException e) 
		    {
		        return null;
		    }
		return null;
	}
	
	public void setIRT(String date) throws ParseException
	{
		IRT = parseDateWithoutSeconds(date);
	}

	public void setIRTFulfilled(boolean isIRTFulfilled)
	{
		this.isIRTFulfilled = isIRTFulfilled;
	}
	

	public void setLastUpdatedBySAP(String date) throws ParseException
	{
		lastUpdatedBySAP = parseDateWithSeconds(date);
	}

	public void setMPT(String date) throws ParseException
	{
		this.MPT = parseDateWithoutSeconds(date);
	}

	public void setNumber(String number)
	{
		this.number = number;
	}

	public void setPriority(TPriority priority)
	{
		this.priority = priority;
	}

	public void setRampUp(boolean isRampUp)
	{
		this.isRampUp = isRampUp;
	}

	public void setWorkPriority(int workPriority)
	{
		this.workPriority = workPriority;
	}

	public void setYear(int year)
	{
		this.year = year;
	}

	public void setProcessor(String processor)
	{
		this.processorInQueue = processor;
	}

	public String getProcessor()
	{
		return processorInQueue;
	}

	public boolean isCustomerCallback() {
		return customerCallback;
	}

	public void setCustomerCallback(boolean customerCallback) {
		this.customerCallback = customerCallback;
	}
}