package com.sap.amd.bcpandicp;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class Shift implements Entity<Shift>, Serializable
{
	private static final long serialVersionUID = 5241327261313817517L;
	
	private String name;
	private List<Region> regions;
	
	public Shift(String name, List<Region> regions)
	{
		super();
		this.name = name;
		this.regions = regions;
	}

	public String getName()
	{
		return name;
	}

	public List<Region> getRegions()
	{
		if (regions == null)
		{
			return new LinkedList<Region>();
		}
		else
		{
			return regions;
		}
	}
	
	public Region getRegion(String region)
	{
		for (int i = 0; i < regions.size(); i++)
		{
			if (regions.get(i).getName().equals(region))
			{
				return regions.get(i);
			}
		}
		
		return null;
	}
	
	public boolean contains(Region region)
	{
		for (int i = 0; i < regions.size(); i++)
		{
			if (regions.get(i).isEqualTo(region))
			{
				return true;
			}
		}
		
		return false;
	}
	
	public boolean isEqualTo(Shift shift)
	{
		boolean isEqual = true;
		
		isEqual = isEqual && this.getName().equals(shift.getName());
		isEqual = isEqual && this.getRegions().size() == shift.getRegions().size();
		
		if (isEqual)
		{
			for (int i = 0; i < this.getRegions().size(); i++)
			{
				isEqual = isEqual && this.getRegions().get(i).isEqualTo(shift.getRegions().get(i));
				
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
		return name + " (" + regions.toString() + ")";
	}
}
