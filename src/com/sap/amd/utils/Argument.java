package com.sap.amd.utils;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class Argument implements Serializable
{
	private static final long serialVersionUID = 7911856043622823304L;
	
	private String owner;
	private List<String> commands;
	
	public Argument(String owner, List<String> values)
	{
		this.owner = owner;
		this.commands = values;
	}
	
	public Argument(String owner, String value)
	{
		this.owner = owner;
		this.commands = new LinkedList<String>();
		commands.add(value);
	}

	public void add(String arg)
	{
		if (arg != null)
		{
			commands.add(arg);
		}
	}

	public List<String> getCommands()
	{
		return commands;
	}
	
	public String getOwner()
	{
		return owner;
	}
}
