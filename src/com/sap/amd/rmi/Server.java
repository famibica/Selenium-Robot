package com.sap.amd.rmi;

import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import com.sap.amd.utils.exceptions.SessionUnlockerException;

public class Server
{
	public static void bind() throws RemoteException, AlreadyBoundException, SessionUnlockerException
	{
		bind(null);
	}
	
	public static void bind(String name) throws RemoteException, AlreadyBoundException, SessionUnlockerException
	{
	    Provider provider = new Provider(name);
	    Wrapper stub = (Wrapper) UnicastRemoteObject.exportObject(provider, 0);

	    Registry registry = LocateRegistry.getRegistry();
	    
	    registry.bind("AMD" + (name == null ? "" : ":" + name), stub);
	    
	    startSessionUnlocker(name);
	}
	
	public static void unbind(String name) throws AccessException, RemoteException, NotBoundException
	{
		Registry registry = LocateRegistry.getRegistry();
		registry.unbind("AMD" + (name == null ? "" : ":" + name));
	}
	
	public static void rebind(String name) throws RemoteException, SessionUnlockerException, NotBoundException
	{
		Provider provider = new Provider(name);
	    Wrapper stub = (Wrapper)UnicastRemoteObject.exportObject(provider, 0);

		Registry registry = LocateRegistry.getRegistry();
	    registry.rebind("AMD" + (name == null ? "" : ":" + name), stub);
	    
	    startSessionUnlocker(name);
	}
	
	private static void startSessionUnlocker(String name) throws SessionUnlockerException
	{
		try
		{
			Registry registry = LocateRegistry.getRegistry();
			Wrapper provider = (Wrapper) registry.lookup(name == null ? "AMD" : "AMD:" + name);
			provider.startSessionUnlocker(provider);
		}
		catch (Exception e)
		{
			throw new SessionUnlockerException(name);
		}
	}
	
	public static boolean isBound(String name) throws RemoteException
	{
		Registry registry = LocateRegistry.getRegistry();
		String[] list = registry.list();
		
		for (int i = 0; i < list.length; i++)
		{
			if (list[i].equals((name == null ? "AMD" : "AMD:" + name)))
			{
				return true;
			}
		}
		
		return false;
	}
}
