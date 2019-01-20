package com.sap.amd.utils;

import java.util.LinkedList;
import java.util.List;

public class ArrayUtils
{
	public static <E> List<E> toList(E[] array)
	{
		List<E> result = new LinkedList<E>();
		
		for (E element : array)
		{
			result.add(element);
		}
		
		return result;
	}
}
