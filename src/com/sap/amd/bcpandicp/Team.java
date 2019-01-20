package com.sap.amd.bcpandicp;

import java.io.Serializable;
import java.util.List;

public class Team implements Entity<Team>, Serializable
{
	private static final long serialVersionUID = -8931611081090352997L;
	
	private String name;
	private List<Shift> shifts;
	
	public Team(String name, List<Shift> shifts)
	{
		super();
		this.name = name;
		this.shifts = shifts;
	}

	public String getName()
	{
		return name;
	}

	public List<Shift> getShifts()
	{
		return shifts;
	}
	
	public Shift getShift(String name)
	{
		for (int i = 0; i < shifts.size(); i++)
		{
			if (shifts.get(i).getName().equalsIgnoreCase(name))
			{
				return shifts.get(i);
			}
		}
		
		return null;
	}
	
	/**
	 * Get the default shift of the team.
	 * The default shift is the first shift set in the spreadsheets. 
	 * @return shift object.
	 */
	public Shift getDefaultShift()
	{
		if (shifts != null && shifts.size() > 0)
		{
			return shifts.get(0);
		}
		else
		{
			return null;
		}
	}
	
	public boolean contains(Shift shift)
	{
		for (int i = 0; i < shifts.size(); i++)
		{
			if (shifts.get(i).isEqualTo(shift))
			{
				return true;
			}
		}
		
		return false;
	}
	
	public boolean isEqualTo(Team team)
	{
		boolean isEqual = true;
		
		isEqual = isEqual && this.getName().equals(team.getName());
		
		isEqual = isEqual && this.getShifts().size() == team.getShifts().size();
		
		if (isEqual)
		{
			for (int i = 0; i < shifts.size(); i++)
			{
				isEqual = isEqual && this.getShifts().get(i).isEqualTo(team.getShifts().get(i));
				
				if (!isEqual)
				{
					return false;
				}
			}
		}
		
		return isEqual;
	}
	
	public String toString()
	{
		return name + " (" + shifts.toString() + ")";
	}
}
