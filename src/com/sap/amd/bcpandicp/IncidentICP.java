package com.sap.amd.bcpandicp;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.sap.amd.utils.Log;
import com.sap.amd.utils.types.TPriority;

public class IncidentICP implements Serializable {

	private static final long serialVersionUID = 7496577409023333088L; // ????

	private Region region;

	private String objectID;
	private String mainCategory;
	private String serviceTeam;
	private String employeeResponsible;
	private String status;
	private String category01;
	private String category02;
	private String messageNumber;
	private TPriority priority;
	private String customer;
	private String country;
	private String customerContact;
	private Date creationDate;
	private Date changedDate;
	private String reportMain;
	private String description;
	private Date nextUpdateTime;
	private String sentFrom;
	private String component;
	private String messageStatus;
	private String messageLevel;
	private TPriority messagePriority;
	private String messageProcessor;
	private Date messageChangedTime;
	private String acrfInfo;
	private String reason;

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

	public void setDescription(String description) {
		this.description = description;
	}

	public String getComponent() {
		return component;
	}

	public String getCountry() {
		return country;
	}

	public String getDescription() {
		return description;
	}

	public TPriority getPriority() {
		return priority;
	}

	/**
	 * Set the region of the incident.
	 * 
	 * @param region
	 *            object region to set.
	 */
	public void setRegion(Region region) {
		this.region = region;
	}

	/**
	 * Set the region of the incident for a given set of regions, based on the
	 * country.
	 * 
	 * @param regions
	 *            set of regions.
	 * @return true if the region was set, false otherwise.
	 */
	public boolean setRegion(List<Region> regions) {
		for (int i = 0; i < regions.size(); i++) {
			if (regions.get(i).contains(country)) {
				this.region = regions.get(i);
				return true;
			}
		}

		return false;
	}

	public Region getRegion() {
		return region;
	}

	public void setComponent(String component) {
		this.component = component;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public void setPriority(TPriority priority) {
		this.priority = priority;
	}

	public static int getContractPriority(String contract) {
		String[] contracts = contract.replace(" ", "").split("/");
		int priority = 0;

		for (int i = 0; i < contracts.length; i++) {
			if (contracts[i].equals("SLA")) {
				priority += 20;
			} else if (contracts[i].equals("MA") || contracts[i].equals("AE")) {
				priority += 10;
			} else if (contracts[i].equals("SEC")) {
				priority += 4;
			} else if (contracts[i].equals("ES")) {
				priority += 3;
			} else if (contracts[i].equals("PSLE")) {
				priority += 2;
			} else if (contracts[i].equals("STD")) {
				priority += 1;
			}
		}

		return priority;
	}

	private Date parseDateWithoutSeconds(String date) throws ParseException {
		try {
			if (date.length() > 3) {
				SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");
				return formatter.parse(date);
			}
		} catch (ParseException e) {
			return null;
		}
		return null;
	}

	private Date parseDateWithSeconds(String date) throws ParseException {
		try {
			if (!(date.trim().equals("0000-00-00 00:00:00"))) {
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				return formatter.parse(date);
			}
		} catch (ParseException e) {
			return null;
		}
		return null;
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

	public String getObjectID() {
		return objectID;
	}

	public void setObjectID(String objectID) {
		this.objectID = objectID;
	}

	public String getMainCategory() {
		return mainCategory;
	}

	public void setMainCategory(String mainCategory) {
		this.mainCategory = mainCategory;
	}

	public String getServiceTeam() {
		return serviceTeam;
	}

	public void setServiceTeam(String serviceTeam) {
		this.serviceTeam = serviceTeam;
	}

	public String getEmployeeResponsible() {
		return employeeResponsible;
	}

	public void setEmployeeResponsible(String employeeResponsible) {
		this.employeeResponsible = employeeResponsible;
	}

	public String getCategory01() {
		return category01;
	}

	public void setCategory01(String category01) {
		this.category01 = category01;
	}

	public String getCategory02() {
		return category02;
	}

	public void setCategory02(String category02) {
		this.category02 = category02;
	}

	public String getMessageNumber() {
		return messageNumber;
	}

	public void setMessageNumber(String messageNumber) {
		this.messageNumber = messageNumber;
	}

	public String getCustomerContact() {
		return customerContact;
	}

	public void setCustomerContact(String customerContact) {
		this.customerContact = customerContact;
	}

	public String getCreationDate() throws ParseException {
		return formatDateUTC(creationDate);
	}

	public void setCreationDate(String date) throws ParseException {
		this.creationDate = parseDateWithSeconds(date);
	}

	public String getChangedDate() throws ParseException {
		return formatDateUTC(changedDate);
	}

	public void setChangedDate(String date) throws ParseException {
		this.changedDate = parseDateWithSeconds(date);
	}

	public String getReportMain() {
		return reportMain;
	}

	public void setReportMain(String reportMain) {
		this.reportMain = reportMain;
	}

	public String getNextUpdateTime() throws ParseException {
		return formatDateUTC(nextUpdateTime);
	}

	public void setNextUpdateTime(String date) throws ParseException {
		this.nextUpdateTime = parseDateWithSeconds(date);
	}

	public String getSentFrom() {
		return sentFrom;
	}

	public void setSentFrom(String sentFrom) {
		this.sentFrom = sentFrom;
	}

	public String getMessageStatus() {
		return messageStatus;
	}

	public void setMessageStatus(String messageStatus) {
		this.messageStatus = messageStatus;
	}

	public String getMessageLevel() {
		return messageLevel;
	}

	public void setMessageLevel(String messageLevel) {
		this.messageLevel = messageLevel;
	}

	public TPriority getMessagePriority() {
		return messagePriority;
	}

	public void setMessagePriority(TPriority messagePriority) {
		this.messagePriority = messagePriority;
	}

	public String getMessageProcessor() {
		return messageProcessor;
	}

	public void setMessageProcessor(String messageProcessor) {
		this.messageProcessor = messageProcessor;
	}

	public String getMessageChangedTime() throws ParseException {
		return formatDateUTC(messageChangedTime);
	}

	public void setMessageChangedTime(String date) throws ParseException {
		this.messageChangedTime = parseDateWithoutSeconds(date);
	}

	public String getAcrfInfo() {
		return acrfInfo;
	}

	public void setAcrfInfo(String acrfInfo) {
		this.acrfInfo = acrfInfo;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
