package com.sap.amd.utils.html;

public class PlainText
{
	public static String get(String text)
	{
		String[] lines = text.split("\n");
		
		String result = "";
		
		for (int i = 0; i < lines.length; i++)
		{
			result += "<p class=PlainText><span>" + lines[i] + "</span></p>";
		}
		
		return result;
	}
}
