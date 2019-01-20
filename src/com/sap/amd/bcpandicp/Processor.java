package com.sap.amd.bcpandicp;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class Processor implements Comparable<Processor>, Serializable
{
	private static final long serialVersionUID = -126781292260050926L;
	
	private boolean isAvailable;
	private String INumber;
	private String name;
	private String email;
	private String language;
	private Team team;
	private Shift shift;
	private int threshold;
	private List<Incident> incidentsInQueue;
	private String[] components;
	private String[] componentsQS;
	public int getVh() {
		return vh;
	}

	public void setVh(int vh) {
		this.vh = vh;
	}

	public int getUrgent() {
		return urgent;
	}

	public void setUrgent(int urgent) {
		this.urgent = urgent;
	}

	public int getNonSla() {
		return nonSla;
	}

	public void setNonSla(int nonSla) {
		this.nonSla = nonSla;
	}

	public int getSla() {
		return sla;
	}

	public void setSla(int sla) {
		this.sla = sla;
	}

	private int vh;
	private int urgent;
	private int nonSla;
	private int sla;
	private String session;
	
	
	public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
	}

	public int compareTo(Processor arg0)
	{
		String NameA = this.getName().toUpperCase();
		String NameB = arg0.getName().toUpperCase();

		return NameA.compareTo(NameB);
	}
	
	public Processor()
	{
		incidentsInQueue = new LinkedList<Incident>();
	}
	
	public Team getTeam()
	{
		return team;
	}

	public void setTeam(Team team)
	{
		this.team = team;
	}

	public String getLanguage()
	{
		return language;
	}
	
	public void setLanguage(String newLanguage)
	{
		this.language = newLanguage;
	}
	
	public Shift getShift()
	{
		return shift;
	}
	
	

	public boolean setShift(Shift shift)
	{
		if (team != null)
		{
			if (team.contains(shift))
			{
				this.shift = shift;
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			return false;
		}
	}
	
	public String[] getComponents()
	{
		return components;
	}

	
	public String[] getComponentsQS()
	{
		return componentsQS;
	}
	
	public String getEmail()
	{
		return email;
	}
	public String getINumber()
	{
		return INumber;
	}
	public String getName()
	{
		return name;
	}
	
	public boolean doesProcessIncidentRegion(Region region)
	{
		for (int i = 0; i < getRegions().size(); i++)
		{
			if (getRegions().get(i).isEqualTo(region))
			{
				return true;
			}
		}
		
		return false;
	}
	
	public List<Region> getRegions()
	{
		if (team != null && shift != null)
		{
			return shift.getRegions();
		}
		else
		{
			return new LinkedList<Region>();
		}
	}

	public int getThreshold()
	{
		return threshold;
	}

	public boolean isAvailable()
	{
		return isAvailable;
	}

	public void setAvailable(boolean isAvailable)
	{
		this.isAvailable = isAvailable;
	}

	public void setComponents(String components)
	{
		this.components = components.replaceAll(" ", "").split(",");
	}
	
	public void setComponentsQS(String componentsQS)
	{
		this.componentsQS = componentsQS.replaceAll(" ", "").split(",");
	}

	public void setComponents(String[] components)
	{
		this.components = components;
	}

	public void setComponentsQS(String[] componentsQS)
	{
		this.componentsQS = componentsQS;
	}
	
	public void setEmail(String email)
	{
		this.email = email;
	}

	public void setINumber(String iNumber)
	{
		this.INumber = iNumber;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public void setThreshold(int threshold)
	{
		this.threshold = threshold;
	}
	
	public boolean doesProcessIncidentComponent(Incident incident)
	{
		boolean result = false;
		
		for (String component : this.getComponents())
		{
			if (component.endsWith("*")) {
				if (incident.getComponent().contains(component.substring(0, component.indexOf("*")))) {
					result = true;
					break;
				}
			}
			else {
				if (incident.getComponent().equalsIgnoreCase(component)) {
					result = true;
					break;
				}
			}
		}

		return result;
	}
	
	public boolean doesProcessIncidentComponentQS(Incident incident)
	{
		boolean result = false;
		
		for (String component : this.getComponentsQS())
		{
			if (component.endsWith("*")) {
				if (incident.getComponent().contains(component.substring(0, component.indexOf("*")))) {
					result = true;
					break;
				}
			}
			else {
				if (incident.getComponent().equalsIgnoreCase(component)) {
					result = true;
					break;
				}
			}
		}

		return result;
	}


	public String regionsToString()
	{
		String regions = "";
		
		for (int i = 0; i < getRegions().size(); i++)
		{
			regions += getRegions().get(i).getName() + (i < getRegions().size() - 1 ? ", " : "");
		}
		
		return regions;
	}
	
	public List<Incident> getIncidentsInQueue()
	{
		return incidentsInQueue;
	}
	
	private boolean contains(Incident incident)
	{
		for (int i = 0; i < incidentsInQueue.size(); i++)
		{
			if (incident.getID().equals(incidentsInQueue.get(i).getID()))
			{
				return true;
			}
		}
		
		return false;
	}

	public void addIncidentInQueue(Incident incidentInQueue)
	{
		if (!contains(incidentInQueue))
		{
			this.incidentsInQueue.add(incidentInQueue);
		}
	}

	public String toString()
	{
		return name + "/" + regionsToString() + (isAvailable() ? " (Available)" : " (Unavailable)");
	}
}
