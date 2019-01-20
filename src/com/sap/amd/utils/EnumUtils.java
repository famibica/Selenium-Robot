package com.sap.amd.utils;

public class EnumUtils
{
	public static Enum<?> getEnum(String name, Enum<?>[] constants)
	{
		for (int i = 0; i < constants.length; i++)
		{
			if (name.replaceAll(" ", "_").equalsIgnoreCase(constants[i].name()))
			{
				return constants[i];
			}
		}
		
		return null;
	}
}
