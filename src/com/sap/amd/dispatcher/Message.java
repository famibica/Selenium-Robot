package com.sap.amd.dispatcher;

import com.sap.amd.utils.types.TMessage;

public class Message extends Throwable
{
	private static final long serialVersionUID = -8275839705866356811L;

	private TMessage type;
	private String text;

	public Message(TMessage type, String text)
	{
		this.type = type;
		this.text = text;
	}
	
	@Override
	public String getMessage()
	{
		return getType().name() + ": " + getText();
	}
	
	@Override
	public String toString()
	{
		return getMessage();
	}

	public TMessage getType()
	{
		return type;
	}

	public String getText()
	{
		return text;
	}
}
