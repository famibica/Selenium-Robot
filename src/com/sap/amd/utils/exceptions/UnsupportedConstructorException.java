package com.sap.amd.utils.exceptions;

import java.lang.reflect.Constructor;

import org.apache.commons.lang3.StringUtils;

public class UnsupportedConstructorException extends Exception
{
	private static final long serialVersionUID = 1557619072485570786L;

	private Constructor<?> constructor;
	
	public UnsupportedConstructorException(Constructor<?> constructor)
	{
		this.constructor = constructor;
	}
	
	public String toString()
	{
		return "Unsupported constructor " + constructor.getName() + "(" + getParameters() + ") for simulator class " + constructor.getDeclaringClass().getName();
	}
	
	private String getParameters()
	{
		String[] parameters = new String[constructor.getParameterTypes().length];
		
		for (int i = 0; i < constructor.getParameterTypes().length; i++)
		{
			parameters[i] = constructor.getParameterTypes().getClass().getName();
		}
		
		return StringUtils.join(parameters);
	}
}
