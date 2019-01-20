package com.sap.amd.utils;

import java.util.List;

public class StringUtils
{
	public static String join(String delimiter, List<String> strings)
	{
		String result = "";
		
		for (int i = 0; i < strings.size(); i++)
		{
			result += strings.get(i) + (i < strings.size() - 1 ? delimiter : "");
		}
		
		return result;
	}
	
	public static String join(String delimiter, String[] strings)
	{
		String result = "";
		
		for (int i = 0; i < strings.length; i++)
		{
			result += strings[i] + (i < strings.length - 1 ? delimiter : "");
		}
		
		return result;
	}
	
	public static boolean isEmpty(String text)
	{
		if (text == null)
		{
			return true;
		}
		else
		{
			String result = text.replaceAll("\u0020", "").replaceAll("\u00A0", "").replaceAll("\u1680", "")
								.replaceAll("\u2002", "").replaceAll("\u2003", "").replaceAll("\u2004", "")
								.replaceAll("\u2005", "").replaceAll("\u2006", "").replaceAll("\u2007", "")
								.replaceAll("\u2008", "").replaceAll("\u2009", "").replaceAll("\u200A", "")
					            .replaceAll("\u202F", "").replaceAll("\u205F", "").replaceAll("\u3000", "");
			return result.isEmpty();
		}
	}
}
