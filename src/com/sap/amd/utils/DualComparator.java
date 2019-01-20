package com.sap.amd.utils;

import java.util.Comparator;

public abstract class DualComparator<T> implements Comparator<T>
{
	private int comparation;
	
	public DualComparator()
	{
		comparation = 0;
	}
	
	@Override
	public int compare(T o1, T o2)
	{
		if (comparation == 0)
		{
			return compareDescending(o1, o2);
		}
		else
		{
			return compareAscending(o1, o2);
		}
	}
	
	public abstract int compareDescending(T o1, T o2);
	public abstract int compareAscending(T o1, T o2);
	
	public DualComparator<T> setDescending()
	{
		comparation = 0;
		return this;
	}
	
	public DualComparator<T> setAscending()
	{
		comparation = 1;
		return this;
	}
}
