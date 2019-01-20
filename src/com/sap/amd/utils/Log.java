package com.sap.amd.utils;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.sap.amd.Program;
import com.sap.amd.dispatcher.Message;
import com.sap.amd.rmi.Provider;

public class Log
{
	private static String latestSession;
	private static String latestUser;
	private static String latestProcessor;
	private static Provider provider = new Provider();

	private static String now()
	{
		return new SimpleDateFormat("[HH:mm:ss.SSS] ").format(new Date());
	}
	
	public static void write(Exception exception)
	{
		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		exception.printStackTrace(printWriter);

		System.out.println(now() + writer.toString());
	}

	public static void write(List<Message> messages)
	{
		for (Message message : messages)
		{
			write(message);
		}
	}

	public static void write(Message message)
	{
		System.out.println(now() + message.getMessage());
	}

	public static void write(Object object)
	{
		System.out.println(now() + object.toString());
	}

	public static void write(String string)
	{
		System.out.println(now() + string);
	}

	public static void newLine()
	{
		System.out.println(System.lineSeparator());
	}

	public static void writeR(Exception exception)
	{
		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		exception.printStackTrace(printWriter);

		try
		{
			provider.writeToLog(now() + writer.toString());
		}
		catch (RemoteException e)
		{
		}
	}

	public static void writeR(List<Message> messages)
	{
		for (Message message : messages)
		{
			writeR(message);
		}
	}

	public static void writeR(Message message)
	{
		try
		{
			provider.writeToLog(now() + message.getMessage());
		}
		catch (RemoteException e)
		{
		}
	}

	public static void writeR(Object object)
	{
		try
		{
			provider.writeToLog(now() + object.toString());
		}
		catch (RemoteException e)
		{
		}
	}

	public static void writeR(String string)
	{
		try
		{
			provider.writeToLog(now() + string);
		}
		catch (RemoteException e)
		{
		}
	}

	public static void newLineR()
	{
		try
		{
			provider.writeToLog(System.lineSeparator());
		}
		catch (RemoteException e)
		{
		}
	}

	public static void writeS(Exception exception)
	{
		setSimulatorStream();

		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		exception.printStackTrace(printWriter);

		System.out.println(now() + writer.toString());

		setDefaultStream();
	}

	public static void writeS(List<Message> messages)
	{
		setSimulatorStream();

		for (Message message : messages)
		{
			writeS(message);
		}

		setDefaultStream();
	}

	public static void writeS(Message message)
	{
		setSimulatorStream();

		System.out.println(now() + message.getMessage());

		setDefaultStream();
	}

	public static void writeS(Object object)
	{
		setSimulatorStream();

		System.out.println(now() + object.toString());

		setDefaultStream();
	}

	public static void writeS(String string)
	{
		setSimulatorStream();

		System.out.println(now() + string);

		setDefaultStream();
	}

	public static void newLineS()
	{
		setSimulatorStream();

		System.out.println(System.lineSeparator());

		setDefaultStream();
	}

	public static void setLogStreams(String user, String processor, String session)
	{
		latestUser = user;
		latestProcessor = processor;
		latestSession = session;

		setDefaultStream();
	}

	public static void setDefaultStream(String processor, String session)
	{
		try
		{
			if (processor != null)
			{
				latestProcessor = processor;
			}

			if (session != null)
			{
				latestSession = session;
			}

			File userFolder = new File(".\\logs\\" + latestProcessor);
			File file = new File(".\\logs\\" + latestProcessor + "\\log" + latestSession + ".txt");

			if (!userFolder.exists())
			{
				userFolder.mkdirs();
			}

			if (!file.exists())
			{
				try
				{
					file.createNewFile();
				}
				catch (IOException e)
				{
				}
			}

			System.setOut(new PrintStream(file));
		}
		catch (Exception e)
		{ /* Log streams should be already set or not null to work, thus if a null pointer exception is caught, nothing will be written; */
		}
	}

	public static void setSimulatorStream(String user, String session)
	{
		try
		{
			if (user != null)
			{
				latestUser = "robot";
			}

			if (session != null)
			{
				latestSession = session;
			}

			File simulatorFolder = new File(".\\resources\\" + latestUser + "\\simulators");
			File file = new File(".\\resources\\" + latestUser + "\\simulators\\simulators_log" + latestSession + ".txt");

			if (!simulatorFolder.exists())
			{
				simulatorFolder.mkdirs();
			}

			if (!file.exists())
			{
				try
				{
					file.createNewFile();
				}
				catch (IOException e)
				{
				}
			}

			System.setOut(new PrintStream(file));
		}
		catch (Exception e)
		{ /* Log streams should be already set or not null to work, thus if a null pointer exception is caught, nothing will be written; */
		}
	}

	public static void setSimulatorStream(String user)
	{
		setSimulatorStream(user, null);
	}

	public static void setDefaultStream(String processor)
	{
		setDefaultStream(processor, null);
	}

	private static void setSimulatorStream()
	{
		setSimulatorStream(null, null);
	}

	private static void setDefaultStream()
	{
		setDefaultStream(null, null);
	}
}
