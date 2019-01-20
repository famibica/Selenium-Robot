package com.sap.amd.utils.exceptions;

public class SessionUnlockerException extends Exception
{
	private static final long serialVersionUID = -3872558468622409368L;

	private String name;
	
	public SessionUnlockerException(String name)
	{
		this.name = name;
	}
	
	public String toString()
	{
		return "Session Unlocker was unable to be started for " + name + ".";
	}
}
