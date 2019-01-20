package com.sap.amd.utils;

import java.io.Serializable;

public class State<E extends Enum<?>> implements Serializable
{
	private static final long serialVersionUID = -3203503066621312908L;
	
	private E type;
	private String[] args;
	private int code;
	
	public State(int code)
	{
		this.code = code;
	}
	
	public State(int code, String[] args)
	{
		this.code = code;
		this.args = args;
	}
	
	public State(int code, E type)
	{
		this.type = type;
		this.code = code;
	}
	
	public State(int code, E type, String[] args)
	{
		this.type = type;
		this.args = args;
		this.code = code;
	}
	
	public State(String[] args)
	{
		this.args = args;
	}
	
	public State(E type)
	{
		this.type = type;
	}
	
	public State(E type, String[] args)
	{
		this.type = type;
		this.args = args;
	}
	
	public String[] getArgs()
	{
		return args;
	}

	public int getCode()
	{
		return code;
	}

	public E getType()
	{
		return type;
	}

	public String toString()
	{
		String result = "(" + code + ")";
		
		if (type != null)
		{
			result += " " + type.name();
		}
		
		if (args != null && args.length > 0)
		{
			result += " = " + StringUtils.join(", ", args);
		}
		
		return result;
	}

	public void setType(E type)
	{
		this.type = type;
	}

	public void setArgs(String[] args)
	{
		this.args = args;
	}

	public void setCode(int code)
	{
		this.code = code;
	}
	
	public boolean equals(State<E> state)
	{
		if (state != null)
		{
			boolean result = true;
			
			result = result && this.code == state.code;
			
			if (this.type != null && state.type != null)
			{
				result = result && this.type == state.type;
			}
			else
			{
				result = result && (this.type == null && state.type == null);
			}
			
			if (this.args != null && state.args != null)
			{
				if (this.args.length != state.args.length)
				{
					return false;
				}
				else
				{
					for (int i = 0; i < this.args.length; i++)
					{
						result = result && this.args[i].equals(state.args[i]);
					}
				}
			}
			else
			{
				result = result && (this.args == null && state.args == null);
			}
			
			return result;
		}
		else
		{
			return false;
		}
	}
}
