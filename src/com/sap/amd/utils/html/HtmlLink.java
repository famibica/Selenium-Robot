package com.sap.amd.utils.html;

public class HtmlLink
{
	public static String get(String text, String url)
	{
		return "<a href=\"" + url + "\">" + text +"</a>";
	}
}
